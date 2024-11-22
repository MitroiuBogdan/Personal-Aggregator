package com.stocks.aggregator.service;

import com.stocks.aggregator.db.repository.ClosedTradePositionRepository;
import com.stocks.aggregator.db.repository.DayTradeStatusRepository;
import com.stocks.aggregator.model.DayTradeStatus;
import com.stocks.aggregator.model.etoro.AccountActivity;
import com.stocks.aggregator.model.etoro.ClosedTradePosition;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static com.stocks.aggregator.service.ClosedPositionService.*;

@Service
@AllArgsConstructor
public class DayTradeStatusService {

    private final ClosedTradePositionRepository closedTradePositionRepository;
    private final DayTradeStatusRepository dayTradeStatusRepository;
    private final AccountActivityService accountActivityService;


    public void syncDayTradingInfo() {
        Map<LocalDate, List<ClosedTradePosition>> closedPositionsGroupedByDay = closedTradePositionRepository.findClosedPositionsGroupedByDay();
        System.out.println(closedPositionsGroupedByDay.keySet());
        closedPositionsGroupedByDay.forEach(
                (day, closedPositions) -> {

                    ClosedTradePosition lastPosition = closedPositions.stream()
                            .max(Comparator.comparing(ClosedTradePosition::getCloseDate)).get();

                    AccountActivity accountActivity = accountActivityService.getClosedByPositionId(lastPosition.getPositionId());


                    DayTradeStatus dayTradeStatus = new DayTradeStatus();

                    dayTradeStatus.setId(day.toEpochDay());
                    dayTradeStatus.setNrOfTrades((long) closedPositions.size());
                    dayTradeStatus.setDate(day);
                    dayTradeStatus.setProfit(calculateTotalProfitUsd(closedPositions, day));
                    dayTradeStatus.setNrWonTransactions(getWonTransactions(closedPositions));
                    dayTradeStatus.setNrLostTransactions(getLostTransactions(closedPositions));
                    dayTradeStatus.setWonValue(calculateWonValue(closedPositions, day));
                    dayTradeStatus.setLoseValue(calculateLoseValue(closedPositions, day));
                    dayTradeStatus.setAverageLose(calculateAverageLoseValue(closedPositions, day));
                    dayTradeStatus.setAverageWin(calculateAverageWonValue(closedPositions, day));

                    dayTradeStatus.setWonValueLong(calculateWonValue(closedPositions, day, "Long"));
                    dayTradeStatus.setWinValueShort(calculateWonValue(closedPositions, day, "Short"));

                    dayTradeStatus.setLoseValueLong(calculateLoseValue(closedPositions, day, "Long"));
                    dayTradeStatus.setLoseValueShort(calculateLoseValue(closedPositions, day, "Short"));
                    dayTradeStatus.setBalance(accountActivity.getRealizedEquity());

                    dayTradeStatus.setDeposit(accountActivityService.getSumByActionByDate(day, AccountActivityService.DEPOSIT));
                    dayTradeStatus.setWithdraw(accountActivityService.getSumByActionByDate(day, AccountActivityService.WITHDRAW));

                    dayTradeStatus.setDepositWithdrawFee(accountActivityService.getFeeByDay(day, AccountActivityService.DEPOSIT_WITHDRAW_FEE));
                    dayTradeStatus.setPositionFee(accountActivityService.getFeeByDay(day, AccountActivityService.POSITION_FEE));
                    dayTradeStatusRepository.save(dayTradeStatus);
                }
        );

        List<DayTradeStatus> dayTradeStatuses = dayTradeStatusRepository.findAllByOrderByDateAsc();

        // Initialize previousBalance to null initially
        Double previousBalance = null;

        for (DayTradeStatus todayStatus : dayTradeStatuses) {
            // Calculate today's balance including deposits and withdrawals
            Double todayBalance = todayStatus.getBalance();

            // Calculate the daily balance change percentage
            Double balanceChange = getDailyBalanceChange(previousBalance, todayBalance, todayStatus);
            System.out.println("PrevBalance " + previousBalance + " TodayBalance " + todayBalance + " Change " + balanceChange);
            // Update the balance change in the current status
            todayStatus.setBalanceChange(balanceChange);

            // Save the updated status back to the database
            dayTradeStatusRepository.save(todayStatus);

            // Update previousBalance for the next iteration
            previousBalance = todayBalance;
        }

        ///////////////////////////////////////////
        // get all day-trade-statuses order by date ascending
        // if previousBalance is null return 0%
        //
    }

    public static Double getDailyBalanceChange(Double previousBalance, Double todayBalance, DayTradeStatus todayStatus) {
        // Check if the previousBalance is zero to avoid division by zero error
        if (previousBalance == null) {
            return 0D;
        }
        todayBalance = todayBalance - todayStatus.getDeposit() + todayStatus.getWithdraw();

        if (previousBalance == 0) {
            return 1D;
        }
        // Calculate the percentage change
        return ((todayBalance - previousBalance) / previousBalance) * 100;
    }

    public static Double getTopThirdWin(List<DayTradeStatus> dayTradeStatuses) {
        return dayTradeStatuses.stream()
                .map(DayTradeStatus::getProfit)
                .distinct() // Optional: Ensure that we consider unique profits
                .sorted(Double::compareTo) // Sort in ascending order
                .skip(dayTradeStatuses.size() - 3) // Skip to the second last (second max)
                .findFirst()
                .orElseGet(null);
    }

    public static Double getTopSecondWin(List<DayTradeStatus> dayTradeStatuses) {
        return dayTradeStatuses.stream()
                .map(DayTradeStatus::getProfit)
                .distinct() // Optional: Ensure that we consider unique profits
                .sorted(Double::compareTo) // Sort in ascending order
                .skip(dayTradeStatuses.size() - 2) // Skip to the second last (second max)
                .findFirst()
                .orElseGet(null);
    }

    public static Double getTopOneWin(List<DayTradeStatus> dayTradeStatuses) {
        return dayTradeStatuses.stream()
                .map(DayTradeStatus::getProfit)
                .max(Double::compareTo)
                .orElseGet(null);
    }

    public static Double getTopOneMin(List<DayTradeStatus> dayTradeStatuses) {
        return dayTradeStatuses.stream()
                .map(DayTradeStatus::getProfit)
                .min(Double::compareTo)
                .orElseGet(null);
    }

    public static Double getTopSecondMin(List<DayTradeStatus> dayTradeStatuses) {
        return dayTradeStatuses.stream()
                .map(DayTradeStatus::getProfit)
                .distinct() // Ensure that we consider unique profits
                .sorted() // Sort profits in ascending order
                .skip(1) // Skip the first (minimum)
                .findFirst()
                .orElseGet(null); // Get the next value (second minimum)
    }

    public static Double getTopThirdMin(List<DayTradeStatus> dayTradeStatuses) {
        return dayTradeStatuses.stream()
                .map(DayTradeStatus::getProfit)
                .distinct() // Ensure that we consider unique profits
                .sorted() // Sort profits in ascending order
                .skip(2) // Skip the first (minimum)
                .findFirst()
                .orElseGet(null); // Get the next value (second minimum)
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
