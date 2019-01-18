Это программа для анализа и валидации данных, пришедших от дата-сатанистов после анализа с помощью XLSCoder

Протестировано только на macOS. Должно без изменений работать на Linux. На Windows могут быть какие-то обычные косяки вроде кодировок и переносов в CSV файле (проверье, что у вас там только `\n`, а не `\r\n`).

При запуске без параметров выдается справка об аргументах:

```
java -jar ./jsonrv.jar
usage: jsonrv
 -e,--excel <arg>    Directory with Excel files.
 -j,--json <arg>     JSON file.
 -o,--out <arg>      Output JSON file.
 -s,--schema <arg>   CSV schema file. Line:
                     filename,conferenceName,list,of,personal,data,fields
```

Все параметры - обязательные. Все параметры, кроме `out`, должны указывать на реально существующий файл. Файл `out` генерится по результату работы программы.

Результаты процессинга, статистика, инсайты, ошибки и предупреждения выдаются в консоли и в файл не пишутся. Если нужно записать в файл - изучи, как перенаправлять вывод скрипта в файл macOS.

Пример запуска из командной строки:

```
java -jar ./jsonrv.jar --schema=~/tmp/schema.csv --json=~/tmp/src.json --excel=~/tmp/excel --out=~/tmp/out.json
```

Исходных данных (`json` и `excel`) здесь, конечно, не будет, потому что они секретные :) Да, они находятся в необратимо зашифрованном формате, но лучше перебдеть, чем недобдеть. Один фиг эта утилита - "для своих", и у всех, кому надо, нужные файлы имеются. А если у тебя нет - значит или попроси у меня, или тебе не надо.

Ещё нужно иметь файл со схемой (`schema`) вот такого вида:

```
colnames_encrypted-Joker 2017.xlsx,Joker 2017,respondent_id,Name,Email,Phone
colnames_encrypted-Heisenbug 2017 Piter.xlsx,Heisenbug 2017 Piter,respondent_id,Name,Email,Phone
colnames_encrypted-DevOops 2017.xlsx,DevOops 2017,respondent_id,Name,Email,Phone
colnames_encrypted-Mobius 2017 Piter.xlsx,Mobius 2017 Piter,respondent_id,Name,Email,Phone
colnames_encrypted-DotNext 2018 Moscow.xlsx,DotNext 2018 Moscow,respondent_id,Name,Email,Phone
colnames_encrypted-HolyJS 2018 Piter.xlsx,HolyJS 2018 Piter,respondent_id,Name,Email,Phone
colnames_encrypted-HolyJS 2017 Piter.xlsx,HolyJS 2017 Piter,respondent_id,Name,Email,Phone
colnames_encrypted-HolyJS 2018 Moscow.xlsx,HolyJS 2018 Moscow,respondent_id,Name,Email,Phone
colnames_encrypted-DotNext 2017 Moscow.xlsx,DotNext 2017 Moscow,respondent_id,Name,Email,Phone
colnames_encrypted-DotNext 2017 Piter.xlsx,DotNext 2017 Piter,respondent_id,Name,Email,Phone
colnames_encrypted-TechTrain 2018.xlsx,TechTrain 2018,respondent_id,Name,Email,Phone
colnames_encrypted-JPoint 2018.xlsx,JPoint 2018,respondent_id,Name,Email,Phone
colnames_encrypted-HolyJS 2017 Moscow.xlsx,HolyJS 2017 Moscow,respondent_id,Name,Email,Phone
colnames_encrypted-DevOops 2018.xlsx,DevOops 2018,respondent_id,Name,Email,Phone
colnames_encrypted-Heisenbug 2018 Piter.xlsx,Heisenbug 2018 Piter,respondent_id,Name,Email,Phone
colnames_encrypted-Mobius 2018 Piter.xlsx,Mobius 2018 Piter,respondent_id,Name,Email,Phone
colnames_encrypted-Dotnext 2018 Piter.xlsx,Dotnext 2018 Piter,respondent_id,Name,Email,Phone
colnames_encrypted-JBreak 2018.xlsx,JBreak 2018,respondent_id,Name,Email,Phone
colnames_encrypted-JBreak 2017.xlsx,JBreak 2017,respondent_id,Name,Email,Phone
colnames_encrypted-SmartData 2017.xlsx,SmartData 2017,respondent_id,Name,Email,Phone
colnames_encrypted-Heisenbug 2017 Moscow.xlsx,Heisenbug 2017 Moscow,respondent_id,Name,Email,Phone
colnames_encrypted-JPoint 2017.xlsx,JPoint 2017,respondent_id,Name,Email,Phone
colnames_encrypted-Joker 2018.xlsx,Joker 2018,respondent_id,Name,Email,Phone
colnames_encrypted-Mobius 2017 Moscow.xlsx,Mobius 2017 Moscow,respondent_id,Name,Email,Phone
```
