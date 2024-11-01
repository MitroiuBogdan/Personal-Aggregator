package com.stocks.aggregator.service;

import com.stocks.aggregator.db.repository.ClosedTradePositionRepository;
import com.stocks.aggregator.model.etoro.ClosedTradePosition;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


@Service
@AllArgsConstructor
public class ClosedPositionService {
    private final ClosedTradePositionRepository closedTradePositionRepository;

    public List<ClosedTradePosition> getClosedPositionsByDay(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        return closedTradePositionRepository.findClosedPositionsByDay(startOfDay, endOfDay);
    }

    public static Double getWonTransactions(List<ClosedTradePosition> closedTradePositions) {
        return (double) closedTradePositions.stream()
                .filter(closedTradePosition -> closedTradePosition.getProfitUsd() > 0)
                .count();
    }

    public static Double getLostTransactions(List<ClosedTradePosition> closedTradePositions) {
        return (double) closedTradePositions.stream()
                .filter(closedTradePosition -> closedTradePosition.getProfitUsd() < 0)
                .count();
    }

    public static Double calculateTotalProfitUsd(List<ClosedTradePosition> closedTradePositions, LocalDate localDate) {
        AtomicReference<Double> profit = new AtomicReference<>(0.0D);
        closedTradePositions.stream()
                .map(ClosedTradePosition::getProfitUsd)
                .forEach(usd -> profit.set(profit.get() + usd));
        return profit.get();
    }

    public static Double calculateWonValue(List<ClosedTradePosition> closedTradePositions, LocalDate localDate) {
        AtomicReference<Double> profit = new AtomicReference<>(0.0D);
        closedTradePositions.stream()
                .map(ClosedTradePosition::getProfitUsd)
                .filter(p -> p > 0.0d)
                .forEach(usd -> profit.set(profit.get() + usd));
        return profit.get();
    }

    public static Double calculateWonValue(List<ClosedTradePosition> closedTradePositions, LocalDate localDate, String positionType) {
        AtomicReference<Double> profit = new AtomicReference<>(0.0D);
        closedTradePositions.stream()
                .filter(closedTradePosition -> closedTradePosition.getLongShort().equals(positionType))
                .map(ClosedTradePosition::getProfitUsd)
                .filter(p -> p > 0.0d)
                .forEach(usd -> profit.set(profit.get() + usd));
        return profit.get();
    }

    public static Double calculateLoseValue(List<ClosedTradePosition> closedTradePositions, LocalDate localDate) {
        AtomicReference<Double> profit = new AtomicReference<>(0.0D);
        closedTradePositions.stream()
                .map(ClosedTradePosition::getProfitUsd)
                .filter(p -> p < 0.0d)
                .forEach(usd -> profit.set(profit.get() + usd));
        return profit.get();
    }


    public static Double calculateLoseValue(List<ClosedTradePosition> closedTradePositions, LocalDate localDate, String positionType) {
        AtomicReference<Double> profit = new AtomicReference<>(0.0D);
        closedTradePositions.stream()
                .filter(closedTradePosition -> closedTradePosition.getLongShort().equals(positionType))
                .map(ClosedTradePosition::getProfitUsd)
                .filter(p -> p < 0.0d)
                .forEach(usd -> profit.set(profit.get() + usd));
        return profit.get();
    }

    public static Double calculateAverageLoseValue(List<ClosedTradePosition> closedTradePositions, LocalDate localDate) {
        AtomicReference<Double> totalLoss = new AtomicReference<>(0.0D);
        AtomicReference<Integer> losingTradeCount = new AtomicReference<>(0);

        closedTradePositions.stream()
                .map(ClosedTradePosition::getProfitUsd)
                .filter(p -> p < 0.0d)
                .forEach(usd -> {
                    totalLoss.set(totalLoss.get() + usd);
                    losingTradeCount.set(losingTradeCount.get() + 1);
                });

        return losingTradeCount.get() > 0 ? totalLoss.get() / losingTradeCount.get() : 0.0D;
    }

    public static Double calculateAverageWonValue(List<ClosedTradePosition> closedTradePositions, LocalDate localDate) {
        AtomicReference<Double> totalProfit = new AtomicReference<>(0.0D);
        AtomicReference<Integer> winningTradeCount = new AtomicReference<>(0);

        closedTradePositions.stream()
                .map(ClosedTradePosition::getProfitUsd)
                .filter(p -> p > 0.0d)
                .forEach(usd -> {
                    totalProfit.set(totalProfit.get() + usd);
                    winningTradeCount.set(winningTradeCount.get() + 1);
                });

        return winningTradeCount.get() > 0 ? totalProfit.get() / winningTradeCount.get() : 0.0D;
    }

}
