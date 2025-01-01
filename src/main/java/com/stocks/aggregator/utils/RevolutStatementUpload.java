package com.stocks.aggregator.utils;

import com.stocks.aggregator.db.repository.RevolutStatementRepository;
import com.stocks.aggregator.model.revolut.RevolutExtract;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Service
@AllArgsConstructor
public class RevolutStatementUpload implements Consumer<List<String[]>> {

    private final RevolutStatementRepository repository;

    @Override
    @Transactional
    public void accept(List<String[]> records) {
        List<RevolutExtract> revolutExtracts = new ArrayList<>();

        records.forEach(record -> {
            sanitizeExcelRecordsRevolut(record);
            RevolutExtract extract = new RevolutExtract();

            extract.setType(record[0]);
            extract.setProduct(record[1]);
            extract.setStartedDate(nonNull(record[2]) ? parseDateTime(record[2]) : null);
            extract.setCompletedDate(nonNull(record[3]) ? parseDateTime(record[3]) : null);
            extract.setDescription(record[4]);
            extract.setAmount(nonNull(record[5]) ? Double.parseDouble(record[5].replace(",", ".")) : null);
            extract.setFee(nonNull(record[6]) ? Double.parseDouble(record[6].replace(",", ".")) : null);
            extract.setCurrency(record[7]);
            extract.setState(record[8]);
            extract.setBalance(nonNull(record[9]) ? Double.parseDouble(record[9].replace(",", ".")) : null);

            revolutExtracts.add(extract);
        });

        repository.saveAll(revolutExtracts);
    }

    private void sanitizeExcelRecordsRevolut(String[] record) {
        for (int i = 0; i < record.length; i++) {
            record[i] = record[i] != null ? record[i].trim() : null;
        }
    }


    private boolean nonNull(String value) {
        return value != null && !value.isEmpty();
    }

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static LocalDateTime parseDateTime(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.isEmpty()) {
            throw new IllegalArgumentException("The date-time string cannot be null or empty");
        }
        return LocalDateTime.parse(dateTimeString, FORMATTER);
    }
}