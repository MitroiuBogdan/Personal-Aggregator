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

public class GoogleSheetExtractor {

    public static void importCSV(String filePath, Consumer<List<String[]>> extracted) {
        try (CSVReader csvReader = new CSVReader(new FileReader(filePath))) {
            List<String[]> records = csvReader.readAll();
            records.remove(0);
            System.out.println(records.size());
            // Skip header row
            List<List<String[]>> batches = splitIntoBatches(records, 100);
            for (int i = 0; i < batches.size(); i++) {
                System.out.println("BATCH " + i);
                extracted.accept(batches.get(i));
            }
            System.out.println("END");
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        } catch (CsvException e) {
            throw new RuntimeException(e);
        }
    }

    public static String convertNegativeNumbersExcel(String str) {
        str = str.trim();
        System.out.println(str);
        // Check if the string is in the format "(number)"
        if (str.startsWith("(") && str.endsWith(")")) {
            // Remove the parentheses
            String numberStr = str.substring(1, str.length() - 1);
            // Parse the number and make it negative
            try {
                numberStr = numberStr.replace(",", ".");
                double number = Double.parseDouble(numberStr);
                return String.valueOf(-number);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number format: " + str);
            }
        } else {
            return str;
        }
    }

    public static String[] sanitizeExcelRecordsEToro(String[] record) {
        for (int j = 0; j < record.length; j++) {

            if (record[j].equalsIgnoreCase("-")) {
                record[j] = null;
            }
            if (nonNull(record[j]) && record[j].contains(".") && record[j].contains(",")) {
                record[j] = record[j].replace(".", "");
            }

            if (nonNull(record[j])) {
                record[j] = convertNegativeNumbersExcel(record[j]);
            }
        }
        return record;
    }

    public static LocalDateTime parseDateExcelEtoro(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        try {
            return LocalDateTime.parse(dateStr, formatter);
        } catch (DateTimeParseException e) {
            return null; // or handle the error as needed
        }
    }

}
