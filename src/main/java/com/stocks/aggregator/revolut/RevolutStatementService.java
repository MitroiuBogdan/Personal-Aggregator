package com.stocks.aggregator.revolut;

import com.stocks.aggregator.revolut.repo.RevolutStatementRepository;
import com.stocks.aggregator.revolut.model.RevolutStatement;
import com.stocks.aggregator.revolut.model.RevolutTransactionType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RevolutStatementService {

    private final RevolutStatementRepository repository;


    // Method to retrieve statements based on TransactionType
    public List<RevolutStatement> getStatementsByTransactionType(RevolutTransactionType type) {
        return repository.findByType(type.name());
    }

    public List<RevolutStatement> getCardPayments() {
        return getStatementsByTransactionType(RevolutTransactionType.CARD_PAYMENT);
    }

    public List<RevolutStatement> getTopUp() {
        return getStatementsByTransactionType(RevolutTransactionType.TOPUP);
    }

    public List<RevolutStatement> getRewardFromInvestments() {
        return repository.findByType(RevolutTransactionType.EXCHANGE.name())
                .stream().filter(revolutStatement -> revolutStatement.getDescription()
                        .equalsIgnoreCase("Exchanged to RON"))
                .collect(Collectors.toList());
    }

    // Method to retrieve statements based on description
    public List<RevolutStatement> getStatementsByDescription(String description) {
        return repository.findByDescription(description);
    }

    // Base Method
    public Map<String, List<RevolutStatement>> getStatementsMappedByDescription(String type) {
        List<RevolutStatement> statementList = repository.findAllByType(type);
        return statementList.stream()
                .collect(Collectors.groupingBy(RevolutStatement::getDescription));
    }

    // Filter by Week of Month
// Filter by Week of Month
    public Map<String, List<RevolutStatement>> getStatementsByWeekOfMonth(String type, int weekOfMonth) {
        List<RevolutStatement> statementList = repository.findAllByType(type);
        return statementList.stream()
                .filter(statement -> {
                    LocalDateTime completedDate = statement.getCompletedDate();
                    if (completedDate == null) return false;

                    // Get the week of the month using ISO week fields
                    int statementWeekOfMonth = completedDate.get(WeekFields.of(Locale.getDefault()).weekOfMonth());
                    return statementWeekOfMonth == weekOfMonth;
                })
                .collect(Collectors.groupingBy(RevolutStatement::getDescription));
    }


    // Filter by Month
    public Map<String, List<RevolutStatement>> getStatementsByMonth(String type, int monthNumber) {
        List<RevolutStatement> statementList = repository.findAllByType(type);
        return statementList.stream()
                .filter(statement -> {
                    LocalDateTime completedDate = statement.getCompletedDate();
                    if (completedDate == null) return false;

                    int statementMonth = completedDate.getMonthValue();
                    return statementMonth == monthNumber;
                })
                .collect(Collectors.groupingBy(RevolutStatement::getDescription));
    }

    // Calculate Expenditure
    public Map<String, Double> calculateExpenditure(Map<String, List<RevolutStatement>> map) {
        return map.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .mapToDouble(RevolutStatement::getAmount)
                                .sum()
                ));
    }
}
