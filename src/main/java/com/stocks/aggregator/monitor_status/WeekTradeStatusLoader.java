package com.stocks.aggregator.monitor_status;

import com.stocks.aggregator.position_monitor.TradePositionRecord;
import com.stocks.aggregator.position_monitor.TradePositionRecordRepository;
import com.stocks.aggregator.utils.MathUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
@Slf4j
public class WeekTradeStatusLoader {

    private TradePositionRecordRepository repository;
    private WeekStatusRecordRepository weekStatusRecordRepository;

    private void updateRealisedEquityChanges() {
        List<WeekStatusRecord> records = weekStatusRecordRepository
                .findAll(Sort.by(Sort.Direction.ASC, "lastClosedDate"));

        log.info("Loaded {} records from week_trade_status_records", records.size());

        for (int i = 1; i < records.size(); i++) {
            WeekStatusRecord previous = records.get(i - 1);
            WeekStatusRecord current = records.get(i);

            double previousEquity = previous.getRealisedEquity();
            double currentEquity = current.getRealisedEquity();

            if (previousEquity != 0) {
                double change = ((currentEquity - previousEquity) / previousEquity) * 100;
                double roundedChange = roundToTwoDecimals(change);
                current.setRealisedEquityChange(roundedChange);

                log.info("Week [{}-{}] → previousEquity = {}, currentEquity = {}, change = {}%",
                        current.getTradeYear(), current.getTradeWeek(), previousEquity, currentEquity, roundedChange);
            } else {
                current.setRealisedEquityChange(0.0);
                log.warn("Previous realisedEquity is zero for [{}-{}], setting change to 0",
                        current.getTradeYear(), current.getTradeWeek());
            }
        }

        weekStatusRecordRepository.saveAll(records);
        log.info("Updated realisedEquityChange for {} records", records.size() - 1);
    }

    private double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }


    public void loadWeekTradeStatus() {
        loadTradeRecordsGroupedByWeek().forEach((week, tradePositionRecords) -> {

            System.out.println("Week: " + week);

            // Group by day and sort days chronologically based on closeDate of first trade
            Map<String, List<TradePositionRecord>> weekRecords = tradePositionRecords.stream()
                    .collect(Collectors.groupingBy(TradePositionRecord::getWeek));

            List<Map.Entry<String, List<TradePositionRecord>>> sortedEntries = new ArrayList<>(weekRecords.entrySet());

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

                computeDayRecords(day, tradesOfTheDay);
            }
        });
        updateRealisedEquityChanges();
    }


    public Map<String, List<TradePositionRecord>> loadTradeRecordsGroupedByWeek() {
        List<TradePositionRecord> allRecords = repository.findAll();

        return allRecords.stream()
                .collect(Collectors.groupingBy(TradePositionRecord::getWeek));
    }

    void computeDayRecords(String week, List<TradePositionRecord> tradeRecords) {
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

        WeekStatusRecord weekStatus = WeekStatusRecord.builder()
                .weekId(UuidGenerator.generateUuid().trim().substring(0, 5))
                .previousWeekId(UuidGenerator.generateUuid().substring(0, 5))
                .tradeWeek(week) // e.g., "W21"
                .tradeYear(String.valueOf(firstTrade.getCloseDate().getYear()))
                .lastClosedDate(lastTrade.getCloseDate())
                .totalTrades(totalTrades)
                .totalProfit(totalProfitUsd)
                .profitRate(winRatePercent)
                .averageWeekProfit(totalProfitUsd / 5) // or replace with weeklyProfit if available
                .averageWinWeekPip(MathUtils.trimmedMean(pipsPerTrade, 0.1))
                .realisedEquity(endingEquity)
                .wonTrades(winCount)
                .realisedEquityChange(0.0)
                .lostTrades(lossCount)
                .totalWonValue(totalWinUsd)
                .totalLostValue(totalLossUsd)
                .build();


        weekStatusRecordRepository.save(weekStatus);

    }

}