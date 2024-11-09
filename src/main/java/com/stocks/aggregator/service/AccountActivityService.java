package com.stocks.aggregator.service;


import com.stocks.aggregator.db.repository.AccountActivityRepository;
import com.stocks.aggregator.model.etoro.AccountActivity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class AccountActivityService {

    public static final String POSITION_CLOSED = "Position closed";

    private final AccountActivityRepository repository;

    AccountActivity getClosedByPositionId(Long positionId) {
        return repository.findByPositionIdAndType(positionId, POSITION_CLOSED).orElse(null);
    }

}
