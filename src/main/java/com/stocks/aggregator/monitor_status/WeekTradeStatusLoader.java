package com.stocks.aggregator.monitor_status;

import com.stocks.aggregator.position_monitor.TradePositionRecord;
import com.stocks.aggregator.position_monitor.TradePositionRecordRepository;
import com.stocks.aggregator.utils.MathUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
@Slf4j
public class WeekTradeStatusLoader {

    private static final int TYPICAL_TRADING_DAYS = 5;

    private final TradePositionRecordRepository repository;
    private final WeekStatusRecordRepository weekStatusRecordRepository;

    public void loadWeekTradeStatus() {
        Map<String, List<TradePositionRecord>> weekGroups = repository.findAll().stream()
                .collect(Collectors.groupingBy(TradePositionRecord::getWeek));

        weekGroups.forEach((week, trades) -> {
            log.info("Processing week: {}", week);
            processWeek(week, trades);
        });

        updateRealisedEquityChanges();
    }

    private void processWeek(String week, List<TradePositionRecord> tradeRecords) {
        if (tradeRecords == null || tradeRecords.isEmpty()) {
            log.warn("No trade records found for week: {}", week);
            return;
        }

        tradeRecords.sort(Comparator.comparing(TradePositionRecord::getCloseDate));

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

        for (TradePositionRecord trade : tradeRecords) {
            double profit = trade.getProfitUsd();
            totalProfitUsd += profit;
            pipsPerTrade.add(trade.getPips());

            if (profit > 0) {
                winCount++;
                totalWinUsd += profit;
            } else {
                lossCount++;
                totalLossUsd += profit;
            }
        }

        int totalTrades = tradeRecords.size();
        double winRate = (winCount + lossCount > 0)
                ? ((double) winCount / (winCount + lossCount)) * 100
                : 0.0;

        WeekStatusRecord weekStatus = WeekStatusRecord.builder()
                .weekId(UuidGenerator.generateUuid().substring(0, 5))
                .previousWeekId(UuidGenerator.generateUuid().substring(0, 5)) // Replace with actual logic if needed
                .tradeWeek(week)
                .tradeYear(String.valueOf(firstTrade.getCloseDate().getYear()))
                .lastClosedDate(lastTrade.getCloseDate())
                .totalTrades(totalTrades)
                .totalProfit(totalProfitUsd)
                .profitRate(winRate)
                .averageWeekProfit(totalProfitUsd / TYPICAL_TRADING_DAYS)
                .averageWinWeekPip(MathUtils.trimmedMean(pipsPerTrade, 0.1))
                .realisedEquity(endingEquity)
                .realisedEquityChange(0.0)
                .wonTrades(winCount)
                .lostTrades(lossCount)
                .totalWonValue(totalWinUsd)
                .totalLostValue(totalLossUsd)
                .build();

        weekStatusRecordRepository.save(weekStatus);
        log.info("Saved weekly status for week {}", week);
    }

    private void updateRealisedEquityChanges() {
        List<WeekStatusRecord> records = weekStatusRecordRepository
                .findAll(Sort.by(Sort.Direction.ASC, "lastClosedDate"));

        log.info("Loaded {} week records for equity change update", records.size());

        for (int i = 1; i < records.size(); i++) {
            WeekStatusRecord previous = records.get(i - 1);
            WeekStatusRecord current = records.get(i);

            double prevEquity = previous.getRealisedEquity();
            double currEquity = current.getRealisedEquity();

            double change = (prevEquity != 0)
                    ? ((currEquity - prevEquity) / prevEquity) * 100
                    : 0.0;

            double rounded = Math.round(change * 100.0) / 100.0;
            current.setRealisedEquityChange(rounded);

            if (prevEquity != 0) {
                log.debug("Week [{}-{}]: prev = {}, curr = {}, change = {}%%",
                        current.getTradeYear(), current.getTradeWeek(), prevEquity, currEquity, rounded);
            } else {
                log.warn("Zero equity base for [{}-{}], change defaulted to 0%%",
                        current.getTradeYear(), current.getTradeWeek());
            }
        }

        weekStatusRecordRepository.saveAll(records);
        log.info("Updated realisedEquityChange for {} records", records.size() - 1);
    }
}
