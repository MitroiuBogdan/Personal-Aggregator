package com.stocks.aggregator.position_monitor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TradePositionRecordRepository extends JpaRepository<TradePositionRecord, TradePositionKey> {
    // Optional: Add custom query methods if needed

    TradePositionRecord findByPositionId(Long positionId);

}