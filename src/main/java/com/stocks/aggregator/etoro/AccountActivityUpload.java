package com.stocks.aggregator.etoro;

import com.stocks.aggregator.etoro.repo.AccountActivityRepository;
import com.stocks.aggregator.etoro.model.AccountActivity;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.stocks.aggregator.utils.GoogleSheetExtractor.parseDateExcelEtoro;
import static com.stocks.aggregator.utils.GoogleSheetExtractor.sanitizeExcelRecordsEToro;
import static java.util.Objects.nonNull;

@Service
@AllArgsConstructor
public class AccountActivityUpload implements Consumer<List<String[]>> {

    private final AccountActivityRepository repository;

    @Override
    @Transactional
    public void accept(List<String[]> records) {
        List<AccountActivity> accountActivities = new ArrayList<>();

        records.forEach(record -> {
            sanitizeExcelRecordsEToro(record);
            AccountActivity accountActivity = new AccountActivity();

            accountActivity.setActivityId(nonNull(record[8]) ? Long.parseLong(record[8]) : null);
            accountActivity.setDate(parseDateExcelEtoro(record[0]));
            accountActivity.setType(record[1]);
            accountActivity.setDetails(record[2]);
            accountActivity.setAmount(nonNull(record[3]) ? Double.parseDouble(record[3].replace(",", ".")) : null);
            accountActivity.setUnits(nonNull(record[4]) ? Double.parseDouble(record[4].replace(",", ".")) : null);
            accountActivity.setRealizedEquityChange(nonNull(record[5]) ? Double.parseDouble(record[5].replace(",", ".")) : null);
            accountActivity.setRealizedEquity(nonNull(record[6]) ? Double.parseDouble(record[6].replace(",", ".")) : null);
            accountActivity.setBalance(nonNull(record[7]) ? Double.parseDouble(record[7].replace(",", ".")) : null);
            accountActivity.setPositionId(nonNull(record[8]) ? Long.parseLong(record[8]) : null);
            accountActivity.setAssetType(record[9]);
            accountActivity.setNwa(nonNull(record[10]) ? Double.parseDouble(record[10].replace(",", ".")) : null);
            accountActivity.setUserId(1);
            accountActivities.add(accountActivity);

        });
        repository.saveAll(accountActivities);
    }
}
