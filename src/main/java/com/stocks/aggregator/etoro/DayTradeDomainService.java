package com.stocks.aggregator.etoro;

import com.stocks.aggregator.etoro.model.DayTradeStatus;
import com.stocks.aggregator.etoro.repo.DayTradeStatusRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
@AllArgsConstructor
public class DayTradeDomainService {

    private final DayTradeStatusRepository repository;

    public Double getDailyBalanceChange(Double previousBalance, Double todayBalance, DayTradeStatus todayStatus) {
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

    public Double getTopThirdWin(List<DayTradeStatus> dayTradeStatuses) {
        return dayTradeStatuses.stream()
                .map(DayTradeStatus::getProfit)
                .distinct() // Optional: Ensure that we consider unique profits
                .sorted(Double::compareTo) // Sort in ascending order
                .skip(dayTradeStatuses.size() - 3) // Skip to the second last (second max)
                .findFirst()
                .orElse(0.0); // Get the first element after skipping
    }

    public Double getTopSecondWin(List<DayTradeStatus> dayTradeStatuses) {
        return dayTradeStatuses.stream()
                .map(DayTradeStatus::getProfit)
                .distinct() // Optional: Ensure that we consider unique profits
                .sorted(Double::compareTo) // Sort in ascending order
                .skip(dayTradeStatuses.size() - 2) // Skip to the second last (second max)
                .findFirst()
                .orElse(0.0); // Get the first element after skipping
    }

    public Double getTopOneWin(List<DayTradeStatus> dayTradeStatuses) {
        return dayTradeStatuses.stream()
                .map(DayTradeStatus::getProfit)
                .max(Double::compareTo)
                .orElse(0.0); // Get the first element after skipping
    }

    public Double getTopOneMin(List<DayTradeStatus> dayTradeStatuses) {
        return dayTradeStatuses.stream()
                .map(DayTradeStatus::getProfit)
                .min(Double::compareTo)
                .orElse(0.0); // Get the first element after skipping
    }

    public Double getTopSecondMin(List<DayTradeStatus> dayTradeStatuses) {
        return dayTradeStatuses.stream()
                .map(DayTradeStatus::getProfit)
                .distinct() // Ensure that we consider unique profits
                .sorted() // Sort profits in ascending order
                .skip(1) // Skip the first (minimum)
                .findFirst()
                .orElse(0.0); // Get the first element after skipping
    }

    public Double getTopThirdMin(List<DayTradeStatus> dayTradeStatuses) {
        return dayTradeStatuses.stream()
                .map(DayTradeStatus::getProfit)
                .distinct() // Ensure that we consider unique profits
                .sorted() // Sort profits in ascending order
                .skip(2) // Skip the first (minimum)
                .findFirst()
                .orElse(0.0); // Get the first element after skipping
    }


    public Long getNumberOfTradesByMonth(List<DayTradeStatus> dayTradeStatuses) {
        AtomicReference<Long> profit = new AtomicReference<>(0L);
        dayTradeStatuses.stream()
                .map(DayTradeStatus::getNrOfTrades)
                .forEach(usd -> profit.set(profit.get() + usd));
        return profit.get();
    }

    public Double getWonTransactionsByMonth(List<DayTradeStatus> dayTradeStatuses) {
        AtomicReference<Double> profit = new AtomicReference<>(0.0D);
        dayTradeStatuses.stream()
                .map(DayTradeStatus::getNrWonTransactions)
                .forEach(usd -> profit.set(profit.get() + usd));
        return profit.get();
    }

    public Double getLostTransactionsByMonth(List<DayTradeStatus> dayTradeStatuses) {
        AtomicReference<Double> profit = new AtomicReference<>(0.0D);
        dayTradeStatuses.stream()
                .map(DayTradeStatus::getNrLostTransactions)
                .forEach(usd -> profit.set(profit.get() + usd));
        return profit.get();
    }

    public Double calculateTotalProfitByMonth(List<DayTradeStatus> dayTradeStatuses, Month month) {
        AtomicReference<Double> profit = new AtomicReference<>(0.0D);
        dayTradeStatuses.stream()
                .map(DayTradeStatus::getProfit)
                .forEach(usd -> profit.set(profit.get() + usd));
        return profit.get();
    }

    public Double calculateWonValueByMonth(List<DayTradeStatus> closedTradePositions, Month month) {
        AtomicReference<Double> profit = new AtomicReference<>(0.0D);
        closedTradePositions.stream()
                .map(DayTradeStatus::getWonValue)
                .forEach(usd -> profit.set(profit.get() + usd));
        return profit.get();
    }

    public Double calculateLoseValueByMonth(List<DayTradeStatus> closedTradePositions, Month month) {
        AtomicReference<Double> profit = new AtomicReference<>(0.0D);
        closedTradePositions.stream()
                .map(DayTradeStatus::getLoseValue)
                .forEach(usd -> profit.set(profit.get() + usd));
        return profit.get();
    }


    public Double calculateAverageLoseValueByMonth(List<DayTradeStatus> dayTradeStatuses, Month month) {
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

    public Double calculateAverageWonValueByMonth(List<DayTradeStatus> dayTradeStatuses, Month month) {
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

    public List<DayTradeStatus> getAllOrderByDateAsc() {
        return repository.findAllByOrderByDateAsc();
    }
}
