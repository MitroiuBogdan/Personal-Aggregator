package com.stocks.aggregator;

import com.stocks.aggregator.api.influx.InfluxDBService;
import com.stocks.aggregator.monitor_status.DayTradeStatusLoader;
import com.stocks.aggregator.position_monitor.TradePositionLoader;
import com.stocks.aggregator.revolut.RevolutPrinter;
import com.stocks.aggregator.revolut.RevolutService;
import com.stocks.aggregator.etoro.DayTradeStatusService;
import com.stocks.aggregator.etoro.MonthTradeStatusService;
import com.stocks.aggregator.etoro.AccountActivityUpload;
import com.stocks.aggregator.etoro.ClosedTradePositionUpload;
import com.stocks.aggregator.revolut.RevolutStatementUpload;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootApplication
@AllArgsConstructor
public class AggregatorApplication implements CommandLineRunner {

    //ADD D3JS
    private final ClosedTradePositionUpload closedTradePositionUpload;
    private final AccountActivityUpload accountActivityUpload;
    private final DayTradeStatusService dayTradeStatusService;
    private final MonthTradeStatusService monthTradeStatusService;
    private final RevolutStatementUpload revolutStatementUpload;
    private final RevolutService revolutService;
    private final InfluxDBService influxDBService;
    private final RevolutPrinter revolutPrinter;
    private final TradePositionLoader tradePositionLoader;
    private final DayTradeStatusLoader dayTradeStatusLoader;

    public static void main(String[] args) {
        SpringApplication.run(AggregatorApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        List<Runnable> tasks = List.of(
//                () -> GoogleSheetExtractor.importCSV("src/main/resources/reports/ac_24.csv", accountActivityUpload),
//                () -> GoogleSheetExtractor.importCSV("src/main/resources/reports/cp_24.csv", closedTradePositionUpload),
//                dayTradeStatusService::syncDayTradingInfo,
//                dayTradeStatusService::deleteDuplicates\
//                dayTradeStatusService::calculateAndPopulateBalanceChange,
//                dayTradeStatusService::calculateAndPopulateAvgProfitMonth
//                dayTradeStatusService::calculateAndPopulateAvgBalanceChange
//                monthTradeStatusService::syncMonthTradeStatus
//                () -> GoogleSheetExtractor.importCSV("src/main/resources/reports/revolut_2.csv", revolutStatementUpload),
//                () -> revolutService.getRentExpensesByMonth()
        );
//        tradePositionLoader.loadClosedPosition(LocalDateTime.now().minusMonths(10), LocalDateTime.now());
        dayTradeStatusLoader.loadDayTradeStatus();


//        for (Runnable task : tasks) {
//            task.run();
//            Thread.sleep(1000); // Optional delay between tasks
//        }
//        revolutService.printRentExpensesByMonth();
//
//        List<DayTradeStatus> dayTradeStatuses = dayTradeStatusService.getAllOrderByDateAsc();
//        influxDBService.writeBatchData(dayTradeStatuses);

//        System.exit(0);
//        revolutPrinter.printStatementByWeekOfMonth();
    }
}
