package com.stocks.aggregator.service;

import com.stocks.aggregator.db.repository.MonthTradeStatusRepository;
import com.stocks.aggregator.model.DayTradeStatus;
import com.stocks.aggregator.model.MonthTradeStatus;
import com.stocks.aggregator.domain.DayTradeStatusDomainService;
import com.stocks.aggregator.model.etoro.ClosedTradePosition;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.stocks.aggregator.service.DayTradeStatusService.*;

@Service
@AllArgsConstructor
public class MonthTradeStatusService {

    private final DayTradeStatusDomainService dayTradeStatusDomainService;
    private final MonthTradeStatusRepository monthTradeStatusRepository;
    private final AccountActivityService accountActivityService;

    public void syncMonthTradeStatus() {
        Map<Month, List<DayTradeStatus>> dayTradeStatusByMonth = dayTradeStatusDomainService.getDayTradeStatusGroupedByMonth();
        System.out.println(dayTradeStatusByMonth.keySet());

        dayTradeStatusByMonth.forEach(
                (month, dayTradeStatuses) -> {
                    MonthTradeStatus monthTradeStatus = new MonthTradeStatus();


                    DayTradeStatus lastDayTradeStatus = dayTradeStatuses.stream()
                            .max(Comparator.comparing(DayTradeStatus::getDate)).get();

                    monthTradeStatus.setId((long) month.getValue());
                    monthTradeStatus.setNrOfTrades(getNumberOfTradesByMonth(dayTradeStatuses));
                    monthTradeStatus.setMonth(month);
                    monthTradeStatus.setProfit(calculateTotalProfitByMonth(dayTradeStatuses, month));
                    monthTradeStatus.setNrWonTransactions(getWonTransactionsByMonth(dayTradeStatuses));
                    monthTradeStatus.setNrLostTransactions(getLostTransactionsByMonth(dayTradeStatuses));
                    monthTradeStatus.setMonth_name(month.getDisplayName(TextStyle.FULL, Locale.US));
                    monthTradeStatus.setWonValue(calculateWonValueByMonth(dayTradeStatuses, month));
                    monthTradeStatus.setLoseValue(calculateLoseValueByMonth(dayTradeStatuses, month));
                    monthTradeStatus.setBalance(lastDayTradeStatus.getBalance());
                    monthTradeStatus.setAverageLose(calculateAverageLoseValueByMonth(dayTradeStatuses, month));
                    monthTradeStatus.setAverageWin(calculateAverageWonValueByMonth(dayTradeStatuses, month));

                    monthTradeStatus.setTop_one_lose(getTopOneMin(dayTradeStatuses));
                    monthTradeStatus.setTop_second_lose(getTopSecondMin(dayTradeStatuses));
                    monthTradeStatus.setTop_third_lose(getTopThirdMin(dayTradeStatuses));

                    monthTradeStatus.setTop_one_win(getTopOneWin(dayTradeStatuses));
                    monthTradeStatus.setTop_second_win(getTopSecondWin(dayTradeStatuses));
                    monthTradeStatus.setTop_third_win(getTopThirdWin(dayTradeStatuses));

                    monthTradeStatus.setDeposit(accountActivityService.getSumByActionByMonth(YearMonth.of(Year.now().getValue(), month), AccountActivityService.DEPOSIT));
                    monthTradeStatus.setWithdraw(accountActivityService.getSumByActionByMonth(YearMonth.of(Year.now().getValue(), month), AccountActivityService.WITHDRAW));

                    monthTradeStatus.setDepositWithdrawFee(accountActivityService.getFeeByMonth(YearMonth.of(Year.now().getValue(), month), AccountActivityService.DEPOSIT_WITHDRAW_FEE));
                    monthTradeStatus.setPositionFee(accountActivityService.getFeeByMonth(YearMonth.of(Year.now().getValue(), month), AccountActivityService.POSITION_FEE));

                    monthTradeStatusRepository.save(monthTradeStatus);

                }
        );
    }
}
