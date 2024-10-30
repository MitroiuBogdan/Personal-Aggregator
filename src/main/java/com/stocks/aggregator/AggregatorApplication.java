package com.stocks.aggregator;

import com.stocks.aggregator.service.ClosedTradePositionService;
import com.stocks.aggregator.utils.AccountActivityUploadService;
import com.stocks.aggregator.utils.ClosedTradePositionUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AggregatorApplication implements CommandLineRunner {


    @Autowired
    ClosedTradePositionUpload closedTradePositionUpload;
    @Autowired
    AccountActivityUploadService accountActivityUploadService;
    @Autowired
    ClosedTradePositionService closedTradePositionService;

    public static void main(String[] args) {
        SpringApplication.run(AggregatorApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
//        closedTradePositionUpload.importCSV("src/main/resources/reports/etoro-account-statement-1-1-2024-10-29-2024 - Closed Positions.csv");
//        accountActivityService.importCSV("src/main/resources/reports/etoro-account-statement-1-1-2024-10-26-2024 - Account Activity.csv");
        closedTradePositionService.calculateAllDayTradeStatus();


    }
}
