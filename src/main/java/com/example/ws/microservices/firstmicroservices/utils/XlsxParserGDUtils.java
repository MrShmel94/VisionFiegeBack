package com.example.ws.microservices.firstmicroservices.utils;

import com.example.ws.microservices.firstmicroservices.customError.MissingHeadersException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
public final class XlsxParserGDUtils {

    private XlsxParserGDUtils() {}

    public static List<List<String>> parserXlsxFile(InputStream fileInputStream, List<String> requiredHeaders, int indexHeaderRow, String fileName) throws IOException, MissingHeadersException {

        IOUtils.setByteArrayMaxOverride(200_000_000);

        try (Workbook workbook = new XSSFWorkbook(fileInputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(indexHeaderRow);

            List<String> fileHeaders = new ArrayList<>();
            for (Cell cell : headerRow) {
                fileHeaders.add(cell.getStringCellValue().trim());
            }

            List<String> missingHeaders = requiredHeaders.stream()
                    .filter(header -> !fileHeaders.contains(header))
                    .toList();

            if (!missingHeaders.isEmpty()) {
                log.error("Missing required headers column: {}, in file name: {}", missingHeaders, fileName);
                throw new MissingHeadersException("Missing this required headers column: " + missingHeaders);
            }

            List<Integer> columnIndexes = requiredHeaders.stream().map(fileHeaders::indexOf).toList();
            List<List<String>> resultList = new ArrayList<>();

            for (int i = indexHeaderRow + 1; i <= sheet.getLastRowNum(); i++) {
                Row dataRow = sheet.getRow(i);
                if (dataRow != null) {
                    List<String> rowData = new ArrayList<>();
                    IntStream.range(0, columnIndexes.size())
                            .forEach(columnIndex -> {
                                rowData.add(processRow(dataRow.getCell(columnIndexes.get(columnIndex))));
                            });
                    resultList.add(rowData);
                }
            }

            log.info("File {} processed successfully, {} rows extracted.", fileName, resultList.size());

            return resultList;
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
}
