package com.xlscoder.jsonrv;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.function.Consumer;

public class XLSHelper2 {
    public static void withXLS(String filename, Consumer<Workbook> consumer) {
        try(InputStream inputStream = new FileInputStream(new File(filename))) {
            Workbook wb = WorkbookFactory.create(inputStream);
            consumer.accept(wb);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void withXLSSheet(String filename, Consumer<Sheet> consumer) {
        withXLSSheet(filename, 0, consumer);
    }

    public static void withXLSSheet(String filename, int sheetNumber, Consumer<Sheet> consumer) {
        withXLS(filename, wb -> {
            Sheet sheet = wb.getSheetAt(sheetNumber);
            consumer.accept(sheet);
        });
    }
}
