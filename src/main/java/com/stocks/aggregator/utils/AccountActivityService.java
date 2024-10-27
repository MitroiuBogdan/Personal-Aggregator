package com.stocks.aggregator.utils;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.stocks.aggregator.db.repository.AccountActivityRepository;
import com.stocks.aggregator.model.AccountActivity;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

@Service
public class AccountActivityService {

    @Autowired
    private AccountActivityRepository repository;

    public void importCSV(String filePath) {
        try (CSVReader csvReader = new CSVReader(new FileReader(filePath))) {
            List<String[]> records = csvReader.readAll();

            System.out.println(records.size());
            List<List<String[]>> batches = splitIntoBatches(records, 100);
            for (int i = 0; i < batches.size(); i++) {
                System.out.println("BATCH " + i);
                processBatch(batches.get(i));
            }
            System.out.println("Import complete");

        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
    }

    @Transactional
    public void processBatch(List<String[]> records) {
        List<AccountActivity> AccountActivitys = new ArrayList<>();
        for (int i = 1; i < records.size(); i++) {
            String[] record = records.get(i);
            AccountActivity transaction = new AccountActivity();

            for (int j = 0; j < record.length; j++) {
                record[j]=record[j].trim();
                if ("-".equals(record[j])) {
                    record[j] = null;
                }
                if (nonNull(record[j]) && record[j].contains(".") && record[j].contains(",")) {
                    record[j] = record[j].replace(".", "");
                }
                if (nonNull(record[j]) && record[j].contains("(") && record[j].contains(")")) {
                    record[j] = record[j].replace("(", "").replace(")", "");
                }
            }

            for (String r:record){
                System.out.println(r);
            }

            transaction.setDate(parseDate(record[0]));
            transaction.setType(record[1]);
            transaction.setDetails(record[2]);
            transaction.setAmount(nonNull(record[3]) ? new BigDecimal(record[3].replace(",", ".")) : null);
            transaction.setUnits(nonNull(record[4]) ? new BigDecimal(record[4].replace(",", ".")) : null);
            transaction.setRealizedEquityChange(nonNull(record[5]) ? new BigDecimal(record[5].replace(",", ".")) : null);
            transaction.setRealizedEquity(nonNull(record[6]) ? new BigDecimal(record[6].replace(",", ".")) : null);
            transaction.setBalance(nonNull(record[7]) ? new BigDecimal(record[7].replace(",", ".")) : null);
            transaction.setPositionId(nonNull(record[8]) ? Long.parseLong(record[8]) : null);
            transaction.setAssetType(record[9]);
            transaction.setNwa(nonNull(record[10]) ? new BigDecimal(record[10].replace(",", ".")) : null);
            transaction.setUserId(1);
            AccountActivitys.add(transaction);
        }


        repository.saveAll(AccountActivitys);
    }

    private LocalDateTime parseDate(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        try {
            return LocalDateTime.parse(dateStr, formatter);
        } catch (DateTimeParseException e) {
            return null; // or handle the error as needed
        }
    }

    public static List<List<String[]>> splitIntoBatches(List<String[]> records, int batchSize) {
        List<List<String[]>> batches = new ArrayList<>();
        for (int i = 0; i < records.size(); i += batchSize) {
            int end = Math.min(i + batchSize, records.size());
            batches.add(records.subList(i, end));
        }
        return batches;
    }
}
