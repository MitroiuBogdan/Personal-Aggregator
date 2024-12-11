package com.stocks.aggregator.utils;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.function.Consumer;

import static com.stocks.aggregator.utils.CollectionUtils.splitIntoBatches;
import static java.util.Objects.nonNull;

public class EToroSheetExtractor {

    public static void importCSV(String filePath, Consumer<List<String[]>> extracted) {
        try (CSVReader csvReader = new CSVReader(new FileReader(filePath))) {
            List<String[]> records = csvReader.readAll();
            records.remove(0); // Remove header row
            splitIntoBatches(records, 100).forEach(batch -> {
                System.out.println("Processing batch of size: " + batch.size());
                extracted.accept(batch);
            });
            System.out.println("Import completed with " + records.size() + " records.");
        } catch (IOException | CsvException e) {
            throw new RuntimeException("Error reading CSV file: " + e.getMessage(), e);
        }
    }

    public static String convertNegativeNumbersExcel(String str) {
        str = str.trim();
        if (str.startsWith("(") && str.endsWith(")")) {
            try {
                return String.valueOf(-Double.parseDouble(str.substring(1, str.length() - 1).replace(",", ".")));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number format: " + str);
            }
        }
        return str;
    }

    public static String[] sanitizeExcelRecordsEToro(String[] record) {
        for (int j = 0; j < record.length; j++) {
            if ("-".equalsIgnoreCase(record[j])) {
                record[j] = null;
            } else if (nonNull(record[j])) {
                record[j] = record[j].contains(".") && record[j].contains(",")
                        ? record[j].replace(".", "")
                        : convertNegativeNumbersExcel(record[j]);
            }
        }
        return record;
    }

    public static LocalDateTime parseDateExcelEtoro(String dateStr) {
        try {
            return LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
