package com.stocks.aggregator.etoro;

import com.stocks.aggregator.etoro.repo.MonthTradeStatusRepository;
import com.stocks.aggregator.etoro.model.DayTradeStatus;
import com.stocks.aggregator.etoro.model.MonthTradeStatus;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.stocks.aggregator.etoro.DayTradeStatusService.*;

@Service
@AllArgsConstructor
public class MonthTradeStatusService {

    private final DayTradeStatusDomainService dayTradeStatusDomainService;
    private final MonthTradeStatusRepository monthTradeStatusRepository;
    private final AccountActivityService accountActivityService;
    private final DayTradeDomainService dtDService;

    public void syncMonthTradeStatus(){
        deleteDuplicates();
    }
    public void addInDatabase() {
        Map<Month, List<DayTradeStatus>> dayTradeStatusByMonth = dayTradeStatusDomainService.getDayTradeStatusGroupedByMonth();
        System.out.println(dayTradeStatusByMonth.keySet());

        dayTradeStatusByMonth.forEach(
                (month, dayTradeStatuses) -> {
                    MonthTradeStatus monthTradeStatus = new MonthTradeStatus();


                    DayTradeStatus lastDayTradeStatus = dayTradeStatuses.stream()
                            .max(Comparator.comparing(DayTradeStatus::getDate)).get();

                    monthTradeStatus.setId((long) month.getValue());
                    monthTradeStatus.setNrOfTrades(dtDService.getNumberOfTradesByMonth(dayTradeStatuses));
                    monthTradeStatus.setMonth(month);
                    monthTradeStatus.setProfit(dtDService.calculateTotalProfitByMonth(dayTradeStatuses, month));
                    monthTradeStatus.setNrWonTransactions(dtDService.getWonTransactionsByMonth(dayTradeStatuses));
                    monthTradeStatus.setNrLostTransactions(dtDService.getLostTransactionsByMonth(dayTradeStatuses));
                    monthTradeStatus.setMonth_name(month.getDisplayName(TextStyle.FULL, Locale.US));
                    monthTradeStatus.setWonValue(dtDService.calculateWonValueByMonth(dayTradeStatuses, month));
                    monthTradeStatus.setLoseValue(dtDService.calculateLoseValueByMonth(dayTradeStatuses, month));
                    monthTradeStatus.setBalance(lastDayTradeStatus.getBalance());
                    monthTradeStatus.setAverageLose(dtDService.calculateAverageLoseValueByMonth(dayTradeStatuses, month));
                    monthTradeStatus.setAverageWin(dtDService.calculateAverageWonValueByMonth(dayTradeStatuses, month));

                    monthTradeStatus.setTop_one_lose(dtDService.getTopOneMin(dayTradeStatuses));
                    monthTradeStatus.setTop_one_win(dtDService.getTopOneWin(dayTradeStatuses));
                    monthTradeStatus.setDeposit(accountActivityService.getSumByActionByMonth(YearMonth.of(Year.now().getValue(), month), AccountActivityService.DEPOSIT));
                    monthTradeStatus.setWithdraw(accountActivityService.getSumByActionByMonth(YearMonth.of(Year.now().getValue(), month), AccountActivityService.WITHDRAW));

                    monthTradeStatus.setDepositWithdrawFee(accountActivityService.getFeeByMonth(YearMonth.of(Year.now().getValue(), month), AccountActivityService.DEPOSIT_WITHDRAW_FEE));
                    monthTradeStatus.setPositionFee(accountActivityService.getFeeByMonth(YearMonth.of(Year.now().getValue(), month), AccountActivityService.POSITION_FEE));

                    monthTradeStatusRepository.save(monthTradeStatus);

                }
        );
    }

    public void deleteDuplicates() {
        List<Month> duplicateMonth = monthTradeStatusRepository.findDuplicateDates();

        for (Month month : duplicateMonth) {
            List<MonthTradeStatus> duplicates = monthTradeStatusRepository.findByMonth(month);
            if (duplicates.size() > 1) {
                duplicates.stream()
                        .skip(1)
                        .forEach(monthTradeStatusRepository::delete);
            }
        }
    }
}
