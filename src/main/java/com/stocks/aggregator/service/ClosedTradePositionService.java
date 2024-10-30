package com.stocks.aggregator.service;


import com.stocks.aggregator.db.repository.ClosedTradePositionRepository;
import com.stocks.aggregator.db.repository.DayTradeStatusRepository;
import com.stocks.aggregator.model.ClosedTradePosition;
import com.stocks.aggregator.model.DayTradeStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

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
        System.out.println(closedPositionsGroupedByDay.keySet());
        closedPositionsGroupedByDay.forEach(
                (day, closedPositions) -> {
                    DayTradeStatus dayTradeStatus = new DayTradeStatus();

                    dayTradeStatus.setNrOfTrades(closedPositions.stream().count());
                    dayTradeStatus.setDate(day);
                    dayTradeStatus.setProfit(calculateTotalProfitUsd(closedPositions, day));
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

    public Double calculateTotalProfitUsd(List<ClosedTradePosition> closedTradePositions, LocalDate localDate) {
        System.out.println("START " + localDate);
        closedTradePositions.stream().map(closedTradePosition -> closedTradePosition.getProfitUsd()).forEach(usd -> {
            System.out.println(usd);
        });
        System.out.println("STOP");
        AtomicReference<Double> profit = new AtomicReference<>(0.0D);
        closedTradePositions.stream().map(closedTradePosition -> closedTradePosition.getProfitUsd()).forEach(usd -> {
            profit.set(profit.get() + usd);
        });
        return profit.get();
    }
}
