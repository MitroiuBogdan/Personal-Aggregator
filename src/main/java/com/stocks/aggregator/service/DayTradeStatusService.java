package com.stocks.aggregator.service;

import com.stocks.aggregator.db.repository.ClosedTradePositionRepository;
import com.stocks.aggregator.db.repository.DayTradeStatusRepository;
import com.stocks.aggregator.model.DayTradeStatus;
import com.stocks.aggregator.model.etoro.ClosedTradePosition;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static com.stocks.aggregator.service.ClosedPositionService.*;

@Service
@AllArgsConstructor
public class DayTradeStatusService {

    private final ClosedTradePositionRepository closedTradePositionRepository;
    private final DayTradeStatusRepository dayTradeStatusRepository;


    public void syncDayTradingInfo() {
        Map<LocalDate, List<ClosedTradePosition>> closedPositionsGroupedByDay = closedTradePositionRepository.findClosedPositionsGroupedByDay();
        System.out.println(closedPositionsGroupedByDay.keySet());
        closedPositionsGroupedByDay.forEach(
                (day, closedPositions) -> {
                    DayTradeStatus dayTradeStatus = new DayTradeStatus();
                    dayTradeStatus.setNrOfTrades((long) closedPositions.size());
                    dayTradeStatus.setDate(day);
                    dayTradeStatus.setProfit(calculateTotalProfitUsd(closedPositions, day));
                    dayTradeStatus.setNrWonTransactions(getWonTransactions(closedPositions));
                    dayTradeStatus.setNrLostTransactions(getLostTransactions(closedPositions));
                    dayTradeStatus.setWonValue(calculateWonValue(closedPositions, day));
                    dayTradeStatus.setLoseValue(calculateLoseValue(closedPositions, day));
                    dayTradeStatus.setAverageLose(calculateAverageLoseValue(closedPositions, day));
                    dayTradeStatus.setAverageWin(calculateAverageWonValue(closedPositions, day));
                    dayTradeStatusRepository.save(dayTradeStatus);

                }
        );
    }


    public static Double getTopThirdWin(List<DayTradeStatus> dayTradeStatuses) {
        return dayTradeStatuses.stream()
                .map(DayTradeStatus::getProfit)
                .distinct() // Optional: Ensure that we consider unique profits
                .sorted(Double::compareTo) // Sort in ascending order
                .skip(dayTradeStatuses.size() - 3) // Skip to the second last (second max)
                .findFirst()
                .get(); // Get the first element after skipping
    }

    public static Double getTopSecondWin(List<DayTradeStatus> dayTradeStatuses) {
        return dayTradeStatuses.stream()
                .map(DayTradeStatus::getProfit)
                .distinct() // Optional: Ensure that we consider unique profits
                .sorted(Double::compareTo) // Sort in ascending order
                .skip(dayTradeStatuses.size() - 2) // Skip to the second last (second max)
                .findFirst()
                .get(); // Get the first element after skipping
    }

    public static Double getTopOneWin(List<DayTradeStatus> dayTradeStatuses) {
        return dayTradeStatuses.stream()
                .map(DayTradeStatus::getProfit)
                .max(Double::compareTo)
                .get();
    }

    public static Double getTopOneMin(List<DayTradeStatus> dayTradeStatuses) {
        return dayTradeStatuses.stream()
                .map(DayTradeStatus::getProfit)
                .min(Double::compareTo)
                .get();
    }

    public static Double getTopSecondMin(List<DayTradeStatus> dayTradeStatuses) {
        return dayTradeStatuses.stream()
                .map(DayTradeStatus::getProfit)
                .distinct() // Ensure that we consider unique profits
                .sorted() // Sort profits in ascending order
                .skip(1) // Skip the first (minimum)
                .findFirst()
                .get(); // Get the next value (second minimum)
    }

    public static Double getTopThirdMin(List<DayTradeStatus> dayTradeStatuses) {
        return dayTradeStatuses.stream()
                .map(DayTradeStatus::getProfit)
                .distinct() // Ensure that we consider unique profits
                .sorted() // Sort profits in ascending order
                .skip(2) // Skip the first (minimum)
                .findFirst()
                .get(); // Get the next value (second minimum)
    }


    public static Long getNumberOfTradesByMonth(List<DayTradeStatus> dayTradeStatuses) {
        AtomicReference<Long> profit = new AtomicReference<>(0L);
        dayTradeStatuses.stream()
                .map(DayTradeStatus::getNrOfTrades)
                .forEach(usd -> profit.set(profit.get() + usd));
        return profit.get();
    }

    public static Double getWonTransactionsByMonth(List<DayTradeStatus> dayTradeStatuses) {
        AtomicReference<Double> profit = new AtomicReference<>(0.0D);
        dayTradeStatuses.stream()
                .map(DayTradeStatus::getNrWonTransactions)
                .forEach(usd -> profit.set(profit.get() + usd));
        return profit.get();
    }

    public static Double getLostTransactionsByMonth(List<DayTradeStatus> dayTradeStatuses) {
        AtomicReference<Double> profit = new AtomicReference<>(0.0D);
        dayTradeStatuses.stream()
                .map(DayTradeStatus::getNrLostTransactions)
                .forEach(usd -> profit.set(profit.get() + usd));
        return profit.get();
    }

    public static Double calculateTotalProfitByMonth(List<DayTradeStatus> dayTradeStatuses, Month month) {
        AtomicReference<Double> profit = new AtomicReference<>(0.0D);
        dayTradeStatuses.stream()
                .map(DayTradeStatus::getProfit)
                .forEach(usd -> profit.set(profit.get() + usd));
        return profit.get();
    }

    public static Double calculateWonValueByMonth(List<DayTradeStatus> closedTradePositions, Month month) {
        AtomicReference<Double> profit = new AtomicReference<>(0.0D);
        closedTradePositions.stream()
                .map(DayTradeStatus::getWonValue)
                .forEach(usd -> profit.set(profit.get() + usd));
        return profit.get();
    }

    public static Double calculateLoseValueByMonth(List<DayTradeStatus> closedTradePositions, Month month) {
        AtomicReference<Double> profit = new AtomicReference<>(0.0D);
        closedTradePositions.stream()
                .map(DayTradeStatus::getLoseValue)
                .forEach(usd -> profit.set(profit.get() + usd));
        return profit.get();
    }


    public static Double calculateAverageLoseValueByMonth(List<DayTradeStatus> dayTradeStatuses, Month month) {
        AtomicReference<Double> totalLoss = new AtomicReference<>(0.0D);
        AtomicReference<Integer> losingTradeCount = new AtomicReference<>(0);

        dayTradeStatuses.stream()
                .map(DayTradeStatus::getAverageLose)
                .forEach(usd -> {
                    totalLoss.set(totalLoss.get() + usd);
                    losingTradeCount.set(losingTradeCount.get() + 1);
                });

        return losingTradeCount.get() > 0 ? totalLoss.get() / losingTradeCount.get() : 0.0D;
    }

    public static Double calculateAverageWonValueByMonth(List<DayTradeStatus> dayTradeStatuses, Month month) {
        AtomicReference<Double> totalProfit = new AtomicReference<>(0.0D);
        AtomicReference<Integer> winningTradeCount = new AtomicReference<>(0);

        dayTradeStatuses.stream()
                .map(DayTradeStatus::getAverageWin)
                .forEach(usd -> {
                    totalProfit.set(totalProfit.get() + usd);
                    winningTradeCount.set(winningTradeCount.get() + 1);
                });

        return winningTradeCount.get() > 0 ? totalProfit.get() / winningTradeCount.get() : 0.0D;
    }
}
