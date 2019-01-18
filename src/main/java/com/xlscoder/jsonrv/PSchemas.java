package com.xlscoder.jsonrv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class PSchemas {
    public List<PSchema> items = new ArrayList<PSchema>();

    public PSchemas(String schemaFile) {
        try (Reader in = new FileReader(schemaFile)) {
            Iterable<CSVRecord> records = CSVFormat.EXCEL.withSystemRecordSeparator().withSkipHeaderRecord().parse(in);
            for (CSVRecord record : records) {
                if (record.size() > 2) {
                    PSchema item = new PSchema();
                    item.filename = record.get(0);
                    item.conference = record.get(1);
                    item.idField = record.get(2);
                    for (int i = 3; i < record.size(); i++) {
                        item.fields.add(record.get(i));
                    }
                    items.add(item);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
