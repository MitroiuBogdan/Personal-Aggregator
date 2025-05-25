package com.stocks.aggregator.monitor_status;

import com.stocks.aggregator.position_monitor.TradePositionRecord;
import com.stocks.aggregator.position_monitor.TradePositionRecordRepository;
import com.stocks.aggregator.utils.MathUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class DayTradeStatusLoader {

    private TradePositionRecordRepository repository;
    private DayTradeStatusRecordRepository dayTradeStatusRecordRepository;

    public void loadDayTradeStatus() {
        loadTradeRecordsGroupedByMonth().forEach((month, tradePositionRecords) -> {

            System.out.println("Month: " + month);

            // Group by day and sort days chronologically based on closeDate of first trade
            Map<String, List<TradePositionRecord>> dayRecords = tradePositionRecords.stream()
                    .collect(Collectors.groupingBy(TradePositionRecord::getDay));

            List<Map.Entry<String, List<TradePositionRecord>>> sortedEntries = new ArrayList<>(dayRecords.entrySet());

            // Sort by the closeDate of the first trade in each day
            sortedEntries.sort(Comparator.comparing(e -> e.getValue().get(0).getCloseDate()));

            double monthlyProfit = 0.0;

            for (int i = 0; i < sortedEntries.size(); i++) {
                Map.Entry<String, List<TradePositionRecord>> entry = sortedEntries.get(i);
                String day = entry.getKey();
                List<TradePositionRecord> tradesOfTheDay = entry.getValue();

                double dailyProfit = tradesOfTheDay.stream()
                        .mapToDouble(TradePositionRecord::getProfitUsd)
                        .sum();

                monthlyProfit += dailyProfit;

                System.out.printf("  Day: %s | Daily Profit: %.2f | Monthly Accumulated: %.2f%n", day, dailyProfit, monthlyProfit);

                computeDayRecords(day, tradesOfTheDay, monthlyProfit, i + 1);
            }
        });
    }


    public Map<String, List<TradePositionRecord>> loadTradeRecordsGroupedByMonth() {
        List<TradePositionRecord> allRecords = repository.findAll();

        return allRecords.stream()
                .collect(Collectors.groupingBy(TradePositionRecord::getMonth));
    }

    void computeDayRecords(String day, List<TradePositionRecord> tradeRecords, double monthlyProfit, int monthlyCount) {
        if (tradeRecords == null || tradeRecords.isEmpty()) {
            throw new IllegalArgumentException("Trade records must not be null or empty");
        }

        TradePositionRecord firstTrade = tradeRecords.get(0);
        TradePositionRecord lastTrade = tradeRecords.get(tradeRecords.size() - 1);
        TradePositionRecord previousDayTrade = repository.findByPositionId(firstTrade.getPositionId());

        double startingEquity = previousDayTrade.getRealizedEquity();
        double endingEquity = lastTrade.getRealizedEquity();

        double totalProfitUsd = 0.0;
        List<Integer> pipsPerTrade = new ArrayList<>();
        int winCount = 0;
        int lossCount = 0;
        double totalWinUsd = 0.0;
        double totalLossUsd = 0.0;

        for (TradePositionRecord record : tradeRecords) {
            double profitUsd = record.getProfitUsd();
            totalProfitUsd += profitUsd;
            pipsPerTrade.add(record.getPips());

            if (profitUsd > 0) {
                winCount++;
                totalWinUsd += profitUsd;
            } else {
                lossCount++;
                totalLossUsd += profitUsd;
            }
        }

     //   monthlyProfit += totalProfitUsd;
        int totalTrades = tradeRecords.size();
        double winRatePercent = (winCount + lossCount > 0)
                ? ((double) winCount / (winCount + lossCount)) * 100
                : 0.0;

        DayTradeStatusRecord dayStatus = DayTradeStatusRecord.builder()
                .day(day)
                .tradeDate(firstTrade.getCloseDate().toLocalDate())
                .totalTrades(totalTrades)
                .totalProfit(totalProfitUsd)
                .profitRate(winRatePercent)
                .averageMonthlyProfit(monthlyProfit / monthlyCount)
                .averageWinDayPip(MathUtils.calculateMean(pipsPerTrade))
                .realisedEquity(endingEquity)
                .realisedEquityChange(MathUtils.calculatePercentChange(startingEquity, endingEquity))
                .wonTrades(winCount)
                .lostTrades(lossCount)
                .totalWonValue(totalWinUsd)
                .totalLostValue(totalLossUsd)
                .monthlyProfit(monthlyProfit)
                .build();

        dayTradeStatusRecordRepository.save(dayStatus);

    }

}