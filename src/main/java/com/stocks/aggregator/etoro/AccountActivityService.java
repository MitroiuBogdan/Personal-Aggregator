package com.stocks.aggregator.etoro;


import com.stocks.aggregator.etoro.repo.AccountActivityRepository;
import com.stocks.aggregator.etoro.model.AccountActivity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Service
public class AccountActivityService {

    public static final String POSITION_CLOSED = "Position closed";
    public static final String OVERNIGHT_FEE = "Overnight fee";
    public static final String WEEKEND_FEE = "Weekend refund";
    public static final String DEPOSIT = "Deposit";
    public static final String WITHDRAW = "Withdraw Request";

    public static final String WITHDRAW_CONVERSION_FEE = "Withdrawal Conversion Fee";
    public static final String DEPOSIT_CONVERSION_FEE = "Deposit Conversion Fee";
    public static final String WITHDRAW_FEE = "Withdraw Fee";

    public static final Set<String> DEPOSIT_WITHDRAW_FEE = Set.of(WITHDRAW_CONVERSION_FEE, DEPOSIT_CONVERSION_FEE, WITHDRAW_FEE);

    public static final Set<String> POSITION_FEE = Set.of(OVERNIGHT_FEE, WEEKEND_FEE);

    private final AccountActivityRepository repository;

    public AccountActivity getClosedByPositionId(Long positionId) {
        return repository.findByPositionIdAndType(positionId, POSITION_CLOSED).orElse(null);
    }


    public Double getSumByActionByDate(LocalDate date, String type) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        List<AccountActivity> accountActivities = repository.findByDateBetweenAndType(startOfDay, endOfDay, type);

        return accountActivities.stream()
                .mapToDouble(AccountActivity::getAmount) // Map each activity to its amount
                .sum();
    }

    public double getSumByActionByMonth(YearMonth month, String type) {
        LocalDate startOfMonth = month.atDay(1);  // First day of the month
        LocalDate endOfMonth = month.atEndOfMonth();  // Last day of the month

        LocalDateTime startOfMonthDateTime = startOfMonth.atStartOfDay();  // Start of the first day
        LocalDateTime endOfMonthDateTime = endOfMonth.atTime(LocalTime.MAX);  // End of the last day

        List<AccountActivity> accountActivities = repository.findByDateBetweenAndType(startOfMonthDateTime, endOfMonthDateTime, type);

        return accountActivities.stream()
                .mapToDouble(AccountActivity::getAmount)  // Map each activity to its amount
                .sum();
    }

    public double getFeeByDay(LocalDate date, Set<String> feeTypes) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        List<AccountActivity> accountActivities = repository.findByDateBetweenAndTypeIn(startOfDay, endOfDay, feeTypes);

        return accountActivities.stream()
                .mapToDouble(AccountActivity::getAmount) // Map each activity to its amount
                .sum();
    }

    public double getFeeByMonth(YearMonth month, Set<String> feeTypes) {
        LocalDate startOfMonth = month.atDay(1);  // First day of the month
        LocalDate endOfMonth = month.atEndOfMonth();  // Last day of the month

        LocalDateTime startOfMonthDateTime = startOfMonth.atStartOfDay();  // Start of the first day
        LocalDateTime endOfMonthDateTime = endOfMonth.atTime(LocalTime.MAX);  // End of the last day

        List<AccountActivity> accountActivities = repository.findByDateBetweenAndTypeIn(startOfMonthDateTime, endOfMonthDateTime, feeTypes);

        return accountActivities.stream()
                .mapToDouble(AccountActivity::getAmount)  // Map each activity to its amount
                .sum();
    }
}
