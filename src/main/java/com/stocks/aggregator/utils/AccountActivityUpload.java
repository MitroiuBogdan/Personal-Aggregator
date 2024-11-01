package com.stocks.aggregator.utils;

import com.stocks.aggregator.db.repository.AccountActivityRepository;
import com.stocks.aggregator.model.etoro.AccountActivity;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.stocks.aggregator.utils.EToroSheetExtractor.parseDateExcelEtoro;
import static com.stocks.aggregator.utils.EToroSheetExtractor.sanitizeExcelRecordsEToro;
import static java.util.Objects.nonNull;

@Service
@AllArgsConstructor
public class AccountActivityUpload implements Consumer<List<String[]>> {

    private final AccountActivityRepository repository;

    @Override
    @Transactional
    public void accept(List<String[]> records) {
        List<AccountActivity> accountActivities = new ArrayList<>();

        records.remove(0); // Skipping the header
        records.forEach(record -> {
            sanitizeExcelRecordsEToro(record);
            AccountActivity accountActivity = new AccountActivity();

            accountActivity.setDate(parseDateExcelEtoro(record[0]));
            accountActivity.setType(record[1]);
            accountActivity.setDetails(record[2]);
            accountActivity.setAmount(nonNull(record[3]) ? new BigDecimal(record[3].replace(",", ".")) : null);
            accountActivity.setUnits(nonNull(record[4]) ? new BigDecimal(record[4].replace(",", ".")) : null);
            accountActivity.setRealizedEquityChange(nonNull(record[5]) ? new BigDecimal(record[5].replace(",", ".")) : null);
            accountActivity.setRealizedEquity(nonNull(record[6]) ? new BigDecimal(record[6].replace(",", ".")) : null);
            accountActivity.setBalance(nonNull(record[7]) ? new BigDecimal(record[7].replace(",", ".")) : null);
            accountActivity.setPositionId(nonNull(record[8]) ? Long.parseLong(record[8]) : null);
            accountActivity.setAssetType(record[9]);
            accountActivity.setNwa(nonNull(record[10]) ? new BigDecimal(record[10].replace(",", ".")) : null);
            accountActivity.setUserId(1);
            accountActivities.add(accountActivity);

        });
        repository.saveAll(accountActivities);
    }
}
