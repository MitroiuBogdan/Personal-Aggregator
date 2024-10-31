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

    public  static Double calculateTotalProfitUsd(List<ClosedTradePosition> closedTradePositions, LocalDate localDate) {
        AtomicReference<Double> profit = new AtomicReference<>(0.0D);
        closedTradePositions.stream()
                .map(ClosedTradePosition::getProfitUsd)
                .forEach(usd -> profit.set(profit.get() + usd));
        return profit.get();
    }

}
