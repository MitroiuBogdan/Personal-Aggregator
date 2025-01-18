package com.stocks.aggregator.revolut;

import com.stocks.aggregator.revolut.model.RevolutStatement;
import com.stocks.aggregator.revolut.model.RevolutTransactionType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class RevolutPrinter {

    private final RevolutStatementService statementService;


    // Print Statements by Week of Month
    public void printStatementByWeekOfMonth() {
        Map<String, List<RevolutStatement>> statements = statementService.getStatementsByMonth(RevolutTransactionType.CARD_PAYMENT.name(), 12);
        System.out.println(statements.size());
        statements.forEach((description, statementList) -> {
            System.out.println("Description: " + description);
            statementList.forEach(statement -> {
                System.out.println("Type: " + statement.getType());
                System.out.println("Date: " + statement.getCompletedDate());
                System.out.println("Amount: " + statement.getAmount());
                System.out.println("Currency: " + statement.getCurrency());
                System.out.println("-----------------------------");
            });
        });

        printExpenditure(statements);
    }

    public void printExpenditure(Map<String, List<RevolutStatement>> map) {
        Map<String, Double> expenditure = statementService.calculateExpenditure(map);
        expenditure.forEach((description, totalAmount) -> {
            System.out.println("Description: " + description);
            System.out.println("Total Expenditure: " + totalAmount);
            System.out.println("-----------------------------");
        });
    }


}
