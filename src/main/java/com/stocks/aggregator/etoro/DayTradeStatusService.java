package com.stocks.aggregator.etoro;

import com.stocks.aggregator.etoro.model.AccountActivity;
import com.stocks.aggregator.etoro.model.ClosedTradePosition;
import com.stocks.aggregator.etoro.model.DayTradeStatus;
import com.stocks.aggregator.etoro.repo.ClosedTradePositionRepository;
import com.stocks.aggregator.etoro.repo.DayTradeStatusRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.stocks.aggregator.etoro.ClosedPositionService.*;
import static com.stocks.aggregator.utils.MathUtils.updateCumulativeAverage;

@Service
@AllArgsConstructor
public class DayTradeStatusService {

    private final ClosedTradePositionRepository closedTradePositionRepository;
    private final DayTradeStatusRepository dayTradeStatusRepository;
    private final AccountActivityService accountActivityService;


    public void syncDayTradingInfo() {
//        addPositionsInDB();
        deleteDuplicates();
        calculateAndPopulateBalanceChange();
        calculateAndPopulateAvgProfitMonth();
    }

    private void addPositionsInDB() {
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
    }

    public void deleteDuplicates() {
        List<LocalDate> duplicateDates = dayTradeStatusRepository.findDuplicateDates();

        for (LocalDate date : duplicateDates) {
            List<DayTradeStatus> duplicates = dayTradeStatusRepository.findByDate(date);
            if (duplicates.size() > 1) {
                duplicates.stream()
                        .skip(1)
                        .forEach(dayTradeStatusRepository::delete);
            }
        }
    }

    public void calculateAndPopulateAvgProfitMonth() {
        // Fetch all records sorted by date
        List<DayTradeStatus> records = dayTradeStatusRepository.findAllByOrderByDateAsc();

        // Group records by month
        Map<String, List<DayTradeStatus>> recordsByMonth = groupRecordsByMonth(records);

        // Calculate and update avgProfitMonth for each record
        recordsByMonth.values().forEach(monthlyRecords ->
                updateCumulativeAverage(monthlyRecords,
                        DayTradeStatus::getProfit,
                        DayTradeStatus::setAvgProfitMonth));

        // Save all updated records in a single operation
        dayTradeStatusRepository.saveAll(records);
    }


    private Map<String, List<DayTradeStatus>> groupRecordsByMonth(List<DayTradeStatus> records) {
        return records.stream()
                .collect(Collectors.groupingBy(record -> record.getDate().getYear() + "-" + record.getDate().getMonthValue()));
    }


    public void calculateAndPopulateBalanceChange() {
        List<DayTradeStatus> records = dayTradeStatusRepository.findAllByOrderByDateAsc();

        Double previousBalance = null;

        for (DayTradeStatus record : records) {
            Double currentBalance = record.getBalance() != null ? record.getBalance() : 0.0;
            Double deposit = record.getDeposit() != null ? record.getDeposit() : 0.0;
            Double withdraw = record.getWithdraw() != null ? record.getWithdraw() : 0.0;

            if (previousBalance != null) {
                // Calculate total balance change
                double totalBalanceChange = ((currentBalance - previousBalance) / previousBalance) * 100;

                // Calculate deposit change
                double depositChange = (deposit / previousBalance) * 100;

                // Calculate withdraw change
                double withdrawChange = (withdraw / previousBalance) * 100;

                // Subtract deposit and withdraw contributions from total balance change
                double finalBalanceChange = totalBalanceChange - depositChange + withdrawChange;

                // Update balance change in the record
                record.setBalanceChange(finalBalanceChange);
            } else {
                // First record has no previous balance
                record.setBalanceChange(0.0);
            }

            // Update previous balance for the next iteration
            previousBalance = currentBalance;
        }

        // Save all updated records back to the database
        dayTradeStatusRepository.saveAll(records);
    }


}