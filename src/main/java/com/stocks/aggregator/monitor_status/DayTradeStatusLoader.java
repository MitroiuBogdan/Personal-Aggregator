package com.stocks.aggregator.monitor_status;

import com.stocks.aggregator.position_monitor.TradePositionRecord;
import com.stocks.aggregator.position_monitor.TradePositionRecordRepository;
import com.stocks.aggregator.utils.MathUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
@Slf4j
public class DayTradeStatusLoader {

    private final TradePositionRecordRepository repository;
    private final DayTradeStatusRecordRepository dayTradeStatusRecordRepository;

    public void loadDayTradeStatus() {
        Map<String, List<TradePositionRecord>> recordsByMonth = repository.findAll().stream()
                .collect(Collectors.groupingBy(TradePositionRecord::getMonth));

        recordsByMonth.forEach((month, tradesInMonth) -> {
            log.info("Processing month: {}", month);

            Map<String, List<TradePositionRecord>> recordsByDay = tradesInMonth.stream()
                    .collect(Collectors.groupingBy(TradePositionRecord::getDay));

            List<Map.Entry<String, List<TradePositionRecord>>> sortedDays = new ArrayList<>(recordsByDay.entrySet());
            sortedDays.sort(Comparator.comparing(e -> e.getValue().get(0).getCloseDate()));

            double accumulatedProfit = 0.0;

            for (int i = 0; i < sortedDays.size(); i++) {
                String day = sortedDays.get(i).getKey();
                List<TradePositionRecord> dayTrades = sortedDays.get(i).getValue();

                double dailyProfit = dayTrades.stream()
                        .mapToDouble(TradePositionRecord::getProfitUsd)
                        .sum();

                accumulatedProfit += dailyProfit;

                log.debug("Day: {} | Daily Profit: {:.2f} | Monthly Accumulated: {:.2f}", day, dailyProfit, accumulatedProfit);

                saveDayTradeStatus(day, dayTrades, accumulatedProfit, i + 1);
            }
        });
    }

    private void saveDayTradeStatus(String day, List<TradePositionRecord> trades, double monthlyProfit, int dayIndex) {
        if (trades == null || trades.isEmpty()) {
            log.warn("No trades found for day: {}", day);
            return;
        }

        trades.sort(Comparator.comparing(TradePositionRecord::getCloseDate));

        TradePositionRecord first = trades.get(0);
        TradePositionRecord last = trades.get(trades.size() - 1);
        TradePositionRecord previous = repository.findByPositionId(first.getPositionId());

        double startingEquity = previous.getRealizedEquity();
        double endingEquity = last.getRealizedEquity();


        int totalTrades = trades.size();
        int wins = 0, losses = 0;
        double winUsd = 0.0, lossUsd = 0.0, profitTotal = 0.0;
        List<Integer> pips = new ArrayList<>();
        double pip_sum = pips.stream()
                .mapToInt(Integer::intValue)
                .sum();

        double averageOpening = 0.0;

        for (TradePositionRecord trade : trades) {

            averageOpening = averageOpening + trade.getAmount();
            double profit = trade.getProfitUsd();
            profitTotal += profit;
            pips.add(trade.getPips());

            if (profit > 0) {
                wins++;
                winUsd += profit;
            } else {
                losses++;
                lossUsd += profit;
            }
        }

        double winRate = (wins + losses) > 0 ? (wins * 100.0 / (wins + losses)) : 0.0;

        DayTradeStatusRecord status = DayTradeStatusRecord.builder()
                .day(day)
                .tradeDate(first.getCloseDate().toLocalDate())
                .totalTrades(totalTrades)
                .totalProfit(profitTotal)
                .profitRate(winRate)
                .averageMonthlyProfit(monthlyProfit / dayIndex)
                .averageWinDayPip(MathUtils.trimmedMean(pips, 0.1))
                .realisedEquity(endingEquity)
                .realisedEquityChange(MathUtils.calculatePercentChange(startingEquity, endingEquity))
                .wonTrades(wins)
                .lostTrades(losses)
                .totalWonValue(winUsd)
                .totalLostValue(lossUsd)
                .monthlyProfit(monthlyProfit)
                .pips(pip_sum)
                .averageOpeningSize(averageOpening / totalTrades)
                .build();

        dayTradeStatusRecordRepository.save(status);
        log.info("Saved day status for {}", day);
    }
}
