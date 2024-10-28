package com.stocks.aggregator.service;


import com.stocks.aggregator.db.repository.ClosedTradePositionRepository;
import com.stocks.aggregator.db.repository.DayTradeStatusRepository;
import com.stocks.aggregator.model.ClosedTradePosition;
import com.stocks.aggregator.model.DayTradeStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class ClosedTradePositionService {


    private final ClosedTradePositionRepository closedTradePositionRepository;
    private final DayTradeStatusRepository dayTradeStatusRepository;

    public ClosedTradePositionService(ClosedTradePositionRepository closedTradePositionRepository,
                                      DayTradeStatusRepository dayTradeStatusRepository) {
        this.closedTradePositionRepository = closedTradePositionRepository;
        this.dayTradeStatusRepository = dayTradeStatusRepository;
    }

    public List<ClosedTradePosition> getClosedPositionsByDay(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        return closedTradePositionRepository.findClosedPositionsByDay(startOfDay, endOfDay);
    }

    public void calculateDayProfit() {

    }

    public void calculateAllDayTradeStatus() {
        Map<LocalDate, List<ClosedTradePosition>> closedPositionsGroupedByDay = closedTradePositionRepository.findClosedPositionsGroupedByDay();
        closedPositionsGroupedByDay.forEach(
                (day, closedPositions) -> {
                    DayTradeStatus dayTradeStatus = new DayTradeStatus();

                    dayTradeStatus.setNrOfTrades(closedPositions.stream().count());
                    dayTradeStatus.setDate(day);
                    dayTradeStatus.setProfit(calculateTotalProfitUsd(closedPositions));
                    dayTradeStatus.setNrWonTransactions(getWonTransactions(closedPositions));
                    dayTradeStatus.setNrLostTransactions(getLostTransactions(closedPositions));

                    dayTradeStatusRepository.save(dayTradeStatus);

                }
        );
    }

    public static Double getWonTransactions(List<ClosedTradePosition> closedTradePositions) {
        return (double) closedTradePositions.stream()
                .filter(closedTradePosition -> closedTradePosition.getProfitUsd().doubleValue() > 0)
                .count();
    }

    public static Double getLostTransactions(List<ClosedTradePosition> closedTradePositions) {
        return (double) closedTradePositions.stream()
                .filter(closedTradePosition -> closedTradePosition.getProfitUsd().doubleValue() < 0)
                .count();
    }

    public Double calculateTotalProfitUsd(List<ClosedTradePosition> closedTradePositions) {
        return closedTradePositions.stream()
                .map(ClosedTradePosition::getProfitUsd)   // Map each position to its profitUsd
                .filter(Objects::nonNull)          // Ensure we don't have null values
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .doubleValue(); // Sum up all profits
    }
}
