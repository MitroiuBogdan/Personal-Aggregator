package com.stocks.aggregator.external.index;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@NoArgsConstructor
public class YahooFinanceAggregator {

    public void showFinancialData() {
//        try {
//            // Get a specific stock (e.g., Apple)
//            Stock stock = YahooFinance.get("AAPL");
//
//            // Print current stock price
//            System.out.println("Current price: " + stock.getQuote().getPrice());
//
//            // Get historical data (e.g., minute-level for one day - limited support)
//            Calendar from = Calendar.getInstance();
//            Calendar to = Calendar.getInstance();
//            from.add(Calendar.DAY_OF_MONTH, -1);
//
//            List<Double> historicalPrices = stock.getHistory(from, to).stream()
//                    .map(quote -> quote.getClose().doubleValue())
//                    .collect(Collectors.toList());
//
//            System.out.println("Historical prices: " + historicalPrices);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
    }
}