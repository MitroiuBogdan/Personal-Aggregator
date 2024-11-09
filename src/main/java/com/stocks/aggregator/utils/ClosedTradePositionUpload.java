package com.stocks.aggregator.utils;

import com.stocks.aggregator.db.repository.ClosedTradePositionRepository;
import com.stocks.aggregator.model.etoro.ClosedTradePosition;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.stocks.aggregator.utils.EToroSheetExtractor.parseDateExcelEtoro;
import static com.stocks.aggregator.utils.EToroSheetExtractor.sanitizeExcelRecordsEToro;
import static java.util.Objects.nonNull;


@Service
@AllArgsConstructor
public class ClosedTradePositionUpload implements Consumer<List<String[]>> {
    private final ClosedTradePositionRepository repository;

    @Override
    @Transactional
    public void accept(List<String[]> records) {
        List<ClosedTradePosition> closedTradePositions = new ArrayList<>();

        records.forEach(record -> {
            sanitizeExcelRecordsEToro(record);
            ClosedTradePosition position = new ClosedTradePosition();

            position.setTradeId(Long.valueOf(record[0]));
            position.setPositionId(Long.valueOf(record[0]));
            position.setAction((record[1]));
            position.setLongShort(record[2]);
            position.setAmount(nonNull(record[3]) ? Double.parseDouble(record[3].replace(",", ".")) : null);
            position.setUnits(nonNull(record[4]) ? Double.parseDouble(record[4].replace(",", ".")) : null);
            position.setOpenDate(parseDateExcelEtoro(record[5]));
            position.setCloseDate(parseDateExcelEtoro(record[6]));
            position.setLeverage(nonNull(record[7]) ? Double.parseDouble(record[7].replace(",", ".")) : null);
            position.setSpreadFeesUsd(nonNull(record[8]) ? Double.parseDouble(record[8].replace(",", ".")) : null);
            position.setMarketSpreadUsd(nonNull(record[9]) ? Double.parseDouble(record[9].replace(",", ".")) : null);
            position.setProfitUsd(nonNull(record[10]) ? Double.parseDouble(record[10].replace(",", ".")) : null);
            position.setFxRateAtOpen(nonNull(record[11]) ? Double.parseDouble(record[11].replace(",", ".")) : null);
            position.setFxRateAtClose(nonNull(record[12]) ? Double.parseDouble(record[12].replace(",", ".")) : null);
            position.setOpenRate(nonNull(record[13]) ? Double.parseDouble(record[13].replace(",", ".")) : null);
            position.setCloseRate(nonNull(record[14]) ? Double.parseDouble(record[14].replace(",", ".")) : null);
            position.setTakeProfitRate(nonNull(record[15]) ? Double.parseDouble(record[15].replace(",", ".")) : null);
            position.setStopLossRate(nonNull(record[16]) ? Double.parseDouble(record[16].replace(",", ".")) : null);
            position.setOvernightFeesDividends(nonNull(record[17]) ? Double.parseDouble(record[17].replace(",", ".")) : null);
            position.setCopiedFrom(record[18]);
            position.setType(record[19]);
            position.setIsin(record[20]);
            position.setNotes(record[21]);

            position.setUserId(1);
            closedTradePositions.add(position);
        });

        repository.saveAll(closedTradePositions);
    }
}
