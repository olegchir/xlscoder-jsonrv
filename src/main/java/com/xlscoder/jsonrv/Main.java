package com.xlscoder.jsonrv;

import org.apache.poi.ss.usermodel.Cell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.xlscoder.jsonrv.Helpers.*;

public class Main {
    public static final Logger logger = LoggerFactory.getLogger(Main.class);
    public static POpts popts;
    public static PSchemas schemas;

    public static final int NUMBER_OF_CLIENTS = 100;
    public static final boolean LIMIT_CLIENTS = false;

    public static void main(String[] args) throws IOException {
        popts = new POpts(args);
        schemas = new PSchemas(popts.getSchemaFile());

        // Вычитываем информацию из JSON-файла в класс PEntries (это на самом деле хэшмап)
        PEntries data = readJSON(popts);

        // Валидировать имеет смысл только тех товарищей, кто ходил более чем на 1 конфу
        // Поэтому пройдемся по списку и выбросим всех ненужных людей
        List<PEntry> fdata = onlyFrequentClients(data);

        // В тестовых целях не имеет смысла перебирать вообще все элементы
        // Можно ограничить количество анализируемых данных первыми N клиентами
        if (LIMIT_CLIENTS) {
            fdata = fdata.stream().limit(NUMBER_OF_CLIENTS).collect(Collectors.toList());
        }

        // Превратим обычные данные в "обогащённые"
        // Этой дополнительной информации ещё нет, но смысл в том,
        // что более богатая инфа требует новые поля - значит нужно создавать новый класс.
        List<REntry> rdata = withAdditionalData(fdata);

        // Открывать файл на каждый элемент - слишком долго.
        // Поэтому надо перегруппировать элементы так, чтобы дальше
        // в цикле идти по файлам, а не по элементам.
        Map<String, List<REntry>> fileToEntry = mapFileToEntry(rdata);

        // Распечатаем шаблон для тех, кто хочет сделать схему в CSV файле
        printSchema(fileToEntry);

        // Найдем все файлы, в которых мы хотим что-то искать
        // Это рекурсивное сканирование директории, делать его каждый раз заново сложно
        Collection<File> files = findExcelFiles(popts);

        // Здесь мы будем хранить важные данные, которые потом пойдут в отчёт.
        // В основном, это всё будут ошибки, конечно.
        List<IdInFile> notFounds = new ArrayList<>();
        List<InconsistentId> inconsistentIds = new ArrayList<>();
        AtomicInteger failedMatches = new AtomicInteger(0);

        // Здесь происходит вся реальная работа
        processRecords(fileToEntry, files, notFounds, inconsistentIds, failedMatches);

        // Распечатываем данные для отчета
        printFinalBanner(data.size(), rdata.size(), popts.getOutputJSONFile(), failedMatches.get());
        printNotFounds(notFounds);
        printInconsistentIds(inconsistentIds);

        // Сохраняем JSON
        saveJSON(rdata, popts);

        System.out.println("end");
    }

    private static void processRecords(Map<String, List<REntry>> fileToEntry, Collection<File> fileCache, List<IdInFile> notFounds, List<InconsistentId> inconsistentIds, AtomicInteger failedMatches) {
        // Последовательно открываем все Excel-файлы
        // (функция отдает уже полностью готовый Excel-лист, по которому можно бегать в цикле)
        // и соответствуюище им "обогащённые дополнительной информацией записи"
        // Обогащать будем прямо внутри этой функции
        forEachFile(fileToEntry, fileCache, (file, sheet, list) -> {

            // Для каждой обогащаемой записи
            for (REntry rEntry: list) {

                //Вычисляем имена файлов
                String originalFileName = originalFileName(file.getName());
                String fname = file.getName();

                // Определяем, согласно структуре, на какой позиции в исходных данных
                // находится обрабатываемый ID, соответствующий имени файла
                int position = rEntry.files.indexOf(originalFileName);
                RId richId = null;
                try {
                    richId = rEntry.ids.get(position);
                } catch (IndexOutOfBoundsException e) {
                    System.out.println(String.format("Inconsistent ids: file name is on position %d, but mapped ids contains only %d elements (file: %s, hash=\"%s\")", position, rEntry.ids.size(), fname, rEntry.key));
                    inconsistentIds.add(new InconsistentId(rEntry.key, fname, position, rEntry.ids.size()));
                    continue;
                }

                // Получаем схему файла (набор занчимых полей)
                // из CSV, переданного в командной строке
                PSchema schema = findSchemaForFile(file, schemas);
                if (null == schema) {
                    continue;
                }

                // Размечаем память под список
                if (richId.data.size() < schema.fields.size()) {
                    richId.data = new ArrayList<>(schema.fields.size());
                }

                // Ищем в Excel-странице ячейку, у которой значение равно
                // интересующему нас сейчас ID
                Cell idCell = findIdCell(schema, sheet, richId);
                if (null == idCell) {
                    System.out.println(String.format("Can't find id cell in XLS file for id=%s in file: %s", richId.id, fname));
                    notFounds.add(new IdInFile(richId.id, fname));
                    failedMatches.incrementAndGet();
                } else {
                    // Теперь нужно обработать все поля, перечисленные в схеме из CSV-файла
                    for (String field : schema.fields) {

                        // Пытаемся найти ячейку по координам:
                        // по вертикали - та же, что у ID-ячейки,
                        // по горизонтали - в столбце с названием в заголовке,
                        // совпадающем с полем из схемы
                        Cell cell = findCellForIdCell(sheet, field, idCell);

                        // В экселе есть две возможнсти получить пустую ячейку -
                        // 1) Когда в ячейке реальная пустота, её просто не существует, и
                        // 2) Ячейка есть, но туда записаны пустые данные (пустая строка, например)
                        if (null == cell) {
                            // Это случай, когда ячейки не существует
                            System.out.println(String.format("Can't find cell in XLS file for field=%s for id=%s in file: %s", field, richId.id, fname));
                            failedMatches.incrementAndGet();
                        } else {
                            Optional<String> value = XLSHelper.getUniversalValue(cell);
                            if (null == value || !value.isPresent()) {
                                // Это случай, когда в ячейке лежат пустые данные
                                System.out.println(String.format("Cell exists, but can't find value for field=%s for id=%s in file: %s", field, richId.id, fname));
                            } else {
                                // А это - единственно правильный случаай, когда и ячейка и данные ок
                                richId.data.add(value.get());
                            }
                        }
                    }
                }
            }
        });
    }
}
