package com.stocks.aggregator;

import com.stocks.aggregator.service.DayTradeStatusService;
import com.stocks.aggregator.service.MonthTradeStatusService;
import com.stocks.aggregator.utils.AccountActivityUpload;
import com.stocks.aggregator.utils.ClosedTradePositionUpload;
import com.stocks.aggregator.utils.EToroSheetExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AggregatorApplication implements CommandLineRunner {


    @Autowired
    ClosedTradePositionUpload closedTradePositionUpload;
    @Autowired
    AccountActivityUpload accountActivityUpload;

    @Autowired
    DayTradeStatusService dayTradeStatusService;

    @Autowired
    MonthTradeStatusService monthTradeStatusService;

    public static void main(String[] args) {
        SpringApplication.run(AggregatorApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
//        closedTradePositionUpload.importCSV("src/main/resources/reports/Closed Positions.csv");
//        accountActivityService.importCSV("src/main/resources/reports/etoro-account-statement-1-1-2024-10-26-2024 - Account Activity.csv");
//        dayTradeStatusService.syncDayTradingInfo();
//        monthTradeStatusService.syncMonthTradeStatus();
        EToroSheetExtractor.importCSV("src/main/resources/reports/etoro-account-statement-1-1-2024-12-21-2024 - Account Activity.csv", accountActivityUpload);
        Thread.sleep(1000);
        EToroSheetExtractor.importCSV("src/main/resources/reports/etoro-account-statement-1-1-2024-12-21-2024 - Closed Positions.csv", closedTradePositionUpload);

        Thread.sleep(1000);

        dayTradeStatusService.syncDayTradingInfo();

        Thread.sleep(1000);

        monthTradeStatusService.syncMonthTradeStatus();
    }
}
