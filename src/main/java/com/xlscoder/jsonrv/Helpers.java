package com.xlscoder.jsonrv;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.xlscoder.jsonrv.XLSHelper2.withXLSSheet;

public class Helpers {
    public static String filteredFileName(String originalFilename) {
        return originalFilename.replace("colnames_encrypted-", "");
    }

    public static String originalFileName(String originalFilename) {
        return "colnames_encrypted-" + originalFilename;
    }

    public static Cell findCell(Sheet sheet, String columnName, Function<Cell,Boolean> check) {
        Cell idCell = null;
        XLSet values = XLSet.extractColumn(sheet, columnName);
        for (Cell cell: values.getItems()) {
            if (check.apply(cell)) {
                idCell = cell;
                break;
            }
        }
        return idCell;
    }

    public static Cell findCellWithValue(Sheet sheet, String columnName, String needle) {
        return findCell(sheet, columnName, cell -> {
            Optional<String> optionalValue = XLSHelper.getUniversalValue(cell);
            if (optionalValue.isPresent()) {
                String value = optionalValue.get();
                return value.equals(needle);
            } else {
                return false;
            }
        });
    }

    public static Cell findCellForIdCell(Sheet sheet, String columnName, Cell idCell) {
        if (null != idCell) {
            int rowIndex = idCell.getRowIndex();
            return findCellWithIndex(sheet, columnName, rowIndex);
        } else {
            return null;
        }
    }

    public static Cell findCellWithIndex(Sheet sheet, String columnName, int index) {
        Cell result = null;
        XLSet values = XLSet.extractColumn(sheet, columnName);
        try {
            result = values.getItems().get(index);
        } catch (IndexOutOfBoundsException e) {
            result = null;
        }
        return result;
    }

    public static Cell findIdCell(PSchema schema, Sheet sheet, RId richId) {
        return findCellWithValue(sheet, schema.idField, richId.id);
    }

    public static PSchema findSchemaForFile(File file, PSchemas schemas) {
        Optional<PSchema> optionalPSchema = schemas.items.stream().filter(s -> s.filename.equals(originalFileName(file.getName()))).findFirst();
        if (!optionalPSchema.isPresent()) {
            System.out.println(String.format("ERROR: can't find schema for file: %s", file.getAbsolutePath()));
            return null;
        }
        return optionalPSchema.get();
    }

    public static void forEachFile(Map<String, List<REntry>> fileToEntry, Collection<File> fileCache, TriConsumer<File, Sheet, List<REntry>> consumer) {
        for (Map.Entry<String, List<REntry>> curr: fileToEntry.entrySet()) {
            String fname = curr.getKey().replace("colnames_encrypted-", "");
            Optional<File> optionalFile = fileCache.stream().filter(f -> f.getName().equals(fname)).findFirst();
            if (optionalFile.isPresent()) {
                System.out.println(String.format("Processing file: %s", optionalFile.get().getAbsolutePath()));

                withXLSSheet(optionalFile.get().getAbsolutePath(), sheet -> {
                    consumer.accept(optionalFile.get(), sheet, curr.getValue());
                });

            } else {
                System.out.println(String.format("File not found: %s", fname));
            }
        }
    }

    public static Collection<File> findExcelFiles(POpts popts) {
        final String[] SUFFIX = {"xlsx"};
        File rootDir = new File(popts.getExcelFile());
        Collection<File> files = FileUtils.listFiles(rootDir, SUFFIX, true);
        files = files.stream().filter(f -> !f.getName().equals("CollectorList.xlsx")).collect(Collectors.toList());
        return files;
    }

    public static PEntries readJSON(POpts popts) throws IOException {
        PEntries data = new ObjectMapper().readValue(new File(popts.getJsonFile()), PEntries.class);
        System.out.println(String.format("Overall data size: %d", data.size()));
        return data;
    }

    public static void saveJSON(List<REntry> rdata, POpts popts) throws IOException {
        File outfile = new File(popts.getOutputJSONFile());
        if (outfile.exists()) {
            FileUtils.forceDelete(outfile);
        }

        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        writer.writeValue(outfile, rdata);
    }

    public static List<REntry> withAdditionalData(List<PEntry> fdata) {
        return fdata.stream()
                .map(REntry::createUnitialized)
                .collect(Collectors.toList());
    }

    public static List<PEntry> onlyFrequentClients(PEntries data) {
        List<PEntry> fdata = data.entrySet().stream()
                .map(e -> {
                    PEntry pEntry = e.getValue();
                    pEntry.setKey(e.getKey());
                    return pEntry;
                })
                .filter(pEntry -> pEntry.getNumberOfConferences() > 1)
                .collect(Collectors.toList());
        System.out.println(String.format("Customers with conferences > 1: %d", fdata.size()));
        return fdata;
    }

    public static Map<String, List<REntry>> mapFileToEntry(List<REntry> rdata) {
        Map<String, List<REntry>> fileToEntry = new HashMap<>();
        for (REntry entry: rdata) {
            for (String file: entry.files) {
                List<REntry> list = fileToEntry.get(file);
                if (null == list) {
                    list = new ArrayList<>();
                }
                list.add(entry);
                fileToEntry.put(file, list);
            }
        }
        return fileToEntry;
    }

    public static void printSchema(Map<String, List<REntry>> fileToEntry) {
        System.out.println("===========================================");
        System.out.println("Template for schemas");
        System.out.println("===========================================");
        for (Map.Entry<String, List<REntry>> curr: fileToEntry.entrySet()) {
            String fname = curr.getKey();
            //  colnames_encrypted-HolyJS 2018 Moscow.xlsx
            String confname = fname.replace("colnames_encrypted-", "").replace(".xlsx", "");
            System.out.println(String.format("%s,%s,respondent_id,Name,Email,Phone",fname,confname));
        }
        System.out.println("===========================================");
    }

    public static void printNotFounds(List<IdInFile> items) {
        System.out.println("===========================================");
        System.out.println(String.format("Critical errors: IDs that are not found (%d)", items.size()));
        System.out.println("===========================================");
        for (IdInFile item: items) {
            System.out.println(String.format("id=%s, file=%s",item.id,item.file));
        }
        System.out.println("===========================================");
    }

    public static void printInconsistentIds(List<InconsistentId> items) {
        System.out.println("===========================================");
        System.out.println(String.format("Critical errors: broken JSON structure, inconsistent IDs (%d)", items.size()));
        System.out.println("===========================================");
        for (InconsistentId item: items) {
            System.out.println(String.format("Position=%d, overall=%d, file=\"%s\", hash=\"%s\")", item.position, item.size, item.file, item.key));
        }
        System.out.println("===========================================");
    }

    public static void printFinalBanner(int overallDataSize, int numberOfItems, String outputFilePath, int failedMatches) {
        System.out.println("===========================================");
        System.out.println(String.format("Processing finished", numberOfItems));
        System.out.println(String.format("Overall data size: %d items", overallDataSize));
        System.out.println(String.format("Effectively processed: %d items", numberOfItems));
        System.out.println(String.format("See results at: %s", outputFilePath));
        System.out.println(String.format("Failed matches: %d", failedMatches));
        System.out.println(String.format("See errors below, if any."));
        System.out.println("===========================================");
    }
}
