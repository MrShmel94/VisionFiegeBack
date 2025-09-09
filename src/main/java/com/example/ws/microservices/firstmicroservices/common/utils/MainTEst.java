package com.example.ws.microservices.firstmicroservices.common.utils;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainTEst {
    public static void main(String[] args) {

        Map<String, Map<String, ScheduleDay>> map = new HashMap<>();

        List<String> daysOff = List.of("W5", "SN", "WN", "WS", "WH", "W", "SW", "RS", "S", "RN");

        try (Workbook workbook = new HSSFWorkbook(new FileInputStream(new File("/Users/vision/Downloads/Plan_Grafik_Wykonanie_poziomo (1).xls")))) {
            Sheet planSheet = workbook.getSheet("Plan");
            Row headerRow = planSheet.getRow(6);

            Map<String, Integer> mapHeader = new HashMap<>();
            for (Cell cell : headerRow) {
                mapHeader.put(processRow(cell), cell.getColumnIndex());
            }

            System.out.println();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String processRow(Cell cell) {

        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
        }
        return "";
    }

    public static LocalDate parsePolishShortDate(String input, int year) {
        Map<String, Integer> polishMonthMap = Map.ofEntries(
                Map.entry("sty", 1),
                Map.entry("lut", 2),
                Map.entry("mar", 3),
                Map.entry("kwi", 4),
                Map.entry("maj", 5),
                Map.entry("cze", 6),
                Map.entry("lip", 7),
                Map.entry("sie", 8),
                Map.entry("wrz", 9),
                Map.entry("paź", 10),
                Map.entry("lis", 11),
                Map.entry("gru", 12)
        );

        String[] parts = input.toLowerCase().split("-");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid date format: " + input);
        }

        Integer month = polishMonthMap.get(parts[0]);
        if (month == null) {
            throw new IllegalArgumentException("Unknown month abbreviation: " + parts[0]);
        }

        int day = Integer.parseInt(parts[1]);

        return LocalDate.of(year, month, day);
    }
}
