package com.stocks.aggregator.utils;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.stocks.aggregator.db.repository.ClosedTradePositionRepository;
import com.stocks.aggregator.model.ClosedTradePosition;
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
public class ClosedTradePositionUpload {

    @Autowired
    private ClosedTradePositionRepository repository;


    public void importCSV(String filePath) {
        try (CSVReader csvReader = new CSVReader(new FileReader(filePath))) {
            List<String[]> records = csvReader.readAll();

            System.out.println(records.size());
            // Skip header row
            List<List<String[]>> batches = splitIntoBatches(records, 100);
            for (int i = 0; i < batches.size(); i++) {
                System.out.println("BATCH " + i);
                extracted(batches.get(i));
            }

            System.out.println("END");


        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        } catch (CsvException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void extracted(List<String[]> records) {
        List<ClosedTradePosition> closedTradePositions = new ArrayList<>();
        for (int i = 1; i < records.size(); i++) {
            String[] record = records.get(i);
            ClosedTradePosition position = new ClosedTradePosition();

            for (int j = 0; j < record.length; j++) {

                if (record[j].equalsIgnoreCase("-")) {
                    record[j] = null;
                }
                if (nonNull(record[j]) && record[j].contains(".") && record[j].contains(",")) {
                    record[j] = record[j].replace(".", "");
                }

                if (nonNull(record[j]) && record[j].contains("(") && record[j].contains(")")) {
                    record[j] = record[j].replace("(", "").replace(")", "");
                }
            }

            position.setPositionId(Long.valueOf(record[0]));
            position.setAction((record[1]));
            position.setLongShort(record[2]);
            position.setAmount(nonNull(record[3]) ? BigDecimal.valueOf(Double.parseDouble(record[3].replace(",", "."))) : null);
            position.setUnits(nonNull(record[4]) ? BigDecimal.valueOf(Double.parseDouble(record[4].replace(",", "."))) : null);
            position.setOpenDate(parseDate(record[5]));
            position.setCloseDate(parseDate(record[6]));
            position.setLeverage(nonNull(record[7]) ? BigDecimal.valueOf(Double.parseDouble(record[7].replace(",", "."))) : null);
            position.setSpreadFeesUsd(nonNull(record[8]) ? BigDecimal.valueOf(Double.parseDouble(record[8].replace(",", "."))) : null);
            position.setMarketSpreadUsd(nonNull(record[9]) ? BigDecimal.valueOf(Double.parseDouble(record[9].replace(",", "."))) : null);
            position.setProfitUsd(nonNull(record[10]) ? BigDecimal.valueOf(Double.parseDouble(record[10].replace(",", "."))) : null);
            position.setFxRateAtOpen(nonNull(record[11]) ? BigDecimal.valueOf(Double.parseDouble(record[11].replace(",", "."))) : null);
            position.setFxRateAtClose(nonNull(record[12]) ? BigDecimal.valueOf(Double.parseDouble(record[12].replace(",", "."))) : null);
            position.setOpenRate(nonNull(record[13]) ? BigDecimal.valueOf(Double.parseDouble(record[13].replace(",", "."))) : null);
            position.setCloseRate(nonNull(record[14]) ? BigDecimal.valueOf(Double.parseDouble(record[14].replace(",", "."))) : null);
            position.setTakeProfitRate(nonNull(record[15]) ? BigDecimal.valueOf(Double.parseDouble(record[15].replace(",", "."))) : null);
            position.setStopLossRate(nonNull(record[16]) ? BigDecimal.valueOf(Double.parseDouble(record[16].replace(",", "."))) : null);
            position.setOvernightFeesDividends(nonNull(record[17]) ? BigDecimal.valueOf(Double.parseDouble(record[17].replace(",", "."))) : null);
            position.setCopiedFrom(record[18]);
            position.setType(record[19]);
            position.setIsin(record[20]);
            position.setNotes(record[21]);

            position.setUserId(1); // This is hardcoded
            closedTradePositions.add(position);
        }
        repository.saveAll(closedTradePositions);
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
