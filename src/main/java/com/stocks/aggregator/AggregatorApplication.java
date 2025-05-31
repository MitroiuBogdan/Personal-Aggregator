package com.stocks.aggregator;

import com.stocks.aggregator.api.influx.InfluxDBService;
import com.stocks.aggregator.monitor_status.DayTradeStatusLoader;
import com.stocks.aggregator.monitor_status.WeekStatusRecordRepository;
import com.stocks.aggregator.monitor_status.WeekTradeStatusLoader;
import com.stocks.aggregator.position_monitor.TradePositionLoader;
import com.stocks.aggregator.revolut.RevolutPrinter;
import com.stocks.aggregator.revolut.RevolutService;
import com.stocks.aggregator.etoro.DayTradeStatusService;
import com.stocks.aggregator.etoro.MonthTradeStatusService;
import com.stocks.aggregator.etoro.AccountActivityUpload;
import com.stocks.aggregator.etoro.ClosedTradePositionUpload;
import com.stocks.aggregator.revolut.RevolutStatementUpload;
import com.stocks.aggregator.utils.GoogleSheetExtractor;
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
    private final WeekTradeStatusLoader weekTradeStatusLoader;
    private final WeekStatusRecordRepository weekStatusRecordRepository;

    public static void main(String[] args) {
        SpringApplication.run(AggregatorApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        List<Runnable> tasks = List.of(
                () -> GoogleSheetExtractor.importCSV("src/main/resources/reports/aa_1.csv", accountActivityUpload),
                () -> GoogleSheetExtractor.importCSV("src/main/resources/reports/cc_1.csv", closedTradePositionUpload),
                () -> tradePositionLoader.loadClosedPosition(LocalDateTime.now().minusMonths(2), LocalDateTime.now()),
                dayTradeStatusLoader::loadDayTradeStatus,
                weekTradeStatusLoader::loadWeekTradeStatus
        );

        for (Runnable task : tasks) {
            task.run();
            Thread.sleep(1000); // Optional delay between tasks
        }
    }

}
