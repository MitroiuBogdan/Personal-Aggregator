package com.stocks.aggregator.revolut;

import com.stocks.aggregator.revolut.repo.RevolutStatementRepository;
import com.stocks.aggregator.revolut.model.RevolutStatement;
import com.stocks.aggregator.revolut.model.RevolutTransactionType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
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

}
