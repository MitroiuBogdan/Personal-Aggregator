package com.stocks.aggregator.db.repository;

import com.stocks.aggregator.model.ClosedTradePosition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClosedTradePositionRepository extends JpaRepository<ClosedTradePosition, Long> {
}
