package com.stocks.aggregator.monitor_status;

import com.stocks.aggregator.position_monitor.TradePositionRecord;
import com.stocks.aggregator.position_monitor.TradePositionRecordRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class DayTradeStatusLoader {

    private TradePositionRecordRepository repository;

    void loadDayTradeStatus() {
        loadTradeRecordsGroupedByMonth().forEach((month, tradePositionRecords) -> {

        });
    }

    public Map<String, List<TradePositionRecord>> loadTradeRecordsGroupedByMonth() {
        List<TradePositionRecord> allRecords = repository.findAll();

        return allRecords.stream()
                .collect(Collectors.groupingBy(TradePositionRecord::getMonth));
    }

    void computeMonth(List<TradePositionRecord> transactionRecords) {
        DayTradeStatusRecord dayTradeStatusRecord = new DayTradeStatusRecord();

    }

}
