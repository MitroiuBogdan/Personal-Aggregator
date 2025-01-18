package com.stocks.aggregator.revolut;

import com.stocks.aggregator.revolut.model.RevolutStatement;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RevolutService {

    private final static String RENT = "To LAURENTIU-IRINEL STAVARACHE";
    private final RevolutStatementService revolutStatementService;



    // Method to get rent expenses grouped by month
    public Map<Month, List<Double>> getRentExpensesByMonth() {
        List<RevolutStatement> rentStatements = revolutStatementService.getStatementsByDescription(RENT);
        System.out.println(rentStatements);
        return rentStatements.stream()
                .filter(statement -> statement.getAmount() != null && statement.getCompletedDate() != null)
                .collect(Collectors.groupingBy(
                        statement -> statement.getCompletedDate().getMonth(),
                        Collectors.mapping(RevolutStatement::getAmount, Collectors.toList())
                ));
    }

    // Method to print rent expenses grouped by month
    public void printRentExpensesByMonth() {
        Map<Month, List<Double>> rentExpenses = getRentExpensesByMonth();
        rentExpenses.forEach((month, amounts) -> {
            System.out.println("Month: " + month);
            amounts.forEach(amount -> System.out.println(" - Amount: " + amount));
        });
    }


}
