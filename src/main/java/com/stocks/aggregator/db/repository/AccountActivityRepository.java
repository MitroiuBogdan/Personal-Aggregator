package com.stocks.aggregator.db.repository;

import com.stocks.aggregator.model.etoro.AccountActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountActivityRepository extends JpaRepository<AccountActivity, Long> {
    // Additional query methods can be defined here if needed

    Optional<AccountActivity> findByPositionIdAndType(Long positionId, String type);
}
