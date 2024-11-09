package com.stocks.aggregator.db.repository;

import com.stocks.aggregator.model.etoro.AccountActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface AccountActivityRepository extends JpaRepository<AccountActivity, Long> {
    // Additional query methods can be defined here if needed

    Optional<AccountActivity> findByPositionIdAndType(Long positionId, String type);


    List<AccountActivity> findByDateBetweenAndType(LocalDateTime startOfDay, LocalDateTime endOfDay, String type);
    List<AccountActivity> findByDateBetweenAndTypeIn(LocalDateTime startOfDay, LocalDateTime endOfDay, Set<String> type);
}
