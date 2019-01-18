package com.xlscoder.jsonrv;


import org.apache.poi.ss.usermodel.*;

import java.util.Date;
import java.util.Optional;

public class XLSHelper {

    public static Optional<String> getUniversalValue(Cell cell) {
        if (cell == null) {
            //Probably an empty cell, that's normal
            return Optional.of("");
        }

        CellType cellType = cell.getCellTypeEnum();
        Optional<String> value = Optional.empty();

        if (cellType.getCode() == CellType.STRING.getCode()) {
            value = Optional.of(cell.getRichStringCellValue().getString());
        } else if (cellType.getCode() == CellType.NUMERIC.getCode()) {
            if (DateUtil.isCellDateFormatted(cell)) {
                Date dateCellValue = cell.getDateCellValue();
                value = Optional.of(dateCellValue.toString());
            } else {
                value = Optional.of(Converters.universalValue(cell.getNumericCellValue()));
            }
        } else if (cellType.getCode() == CellType.BOOLEAN.getCode()) {
            value = Optional.of(Boolean.toString(cell.getBooleanCellValue()));
        } else if (cellType.getCode() == CellType.FORMULA.getCode()) {
            value = Optional.of(cell.getCellFormula());
        } else if (cellType.getCode() == CellType._NONE.getCode()) {
            value = Optional.empty();
        }

        return value;
    }


    public static Cell getCellAt(Row row, int colIndex) {
        if (null == row) {
            return null;
        }

        return row.getCell(colIndex);
    }

    public static Cell getCellAt(Sheet sheet, int rowIndex, int colIndex) {
        if (null == sheet) {
            return null;
        }

        Row row = sheet.getRow(rowIndex);
        if (null == row) {
            return null;
        }

        return row.getCell(colIndex);
    }
}
