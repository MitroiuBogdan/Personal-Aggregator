package com.stocks.aggregator.etoro.repo;

import com.stocks.aggregator.etoro.model.ClosedTradePosition;
import com.stocks.aggregator.position_monitor.TradePositionProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public interface ClosedTradePositionRepository extends JpaRepository<ClosedTradePosition, Long> {

    // Custom method to group by day using the closingDate
    @Query("SELECT c FROM ClosedTradePosition c")
    List<ClosedTradePosition> findAllClosedTradePositions();

    default Map<LocalDate, List<ClosedTradePosition>> findClosedPositionsGroupedByDay() {
        return findAllClosedTradePositions()
                .stream()
                .collect(Collectors.groupingBy(
                        position -> position.getCloseDate().toLocalDate()
                ));
    }

    @Query("SELECT c FROM ClosedTradePosition c WHERE c.closeDate BETWEEN :startOfDay AND :endOfDay")
    List<ClosedTradePosition> findClosedPositionsByDay(@Param("startOfDay") LocalDateTime startOfDay,
                                                       @Param("endOfDay") LocalDateTime endOfDay);


    List<ClosedTradePosition> findClosedTradePositionsByOpenDateAfter(LocalDateTime startOpenDay);

    @Query("""
                SELECT 
                    c.positionId AS positionId,
                    c.amount AS amount,
                    c.openDate AS openDate,
                    c.closeDate AS closeDate,
                    c.leverage AS leverage,
                    c.action AS action,
                    c.openRate AS openRate,
                    c.closeRate AS closeRate,
                    c.takeProfitRate AS takeProfitRate,
                    c.stopLossRate AS stopLossRate,
                    c.profitUsd AS profitUsd,
                    c.longShort AS longShort,
                    c.units AS units,
                    a.balance AS balance,
                    a.realizedEquity AS realizedEquity,
                    a.type AS type
                FROM ClosedTradePosition c
                JOIN AccountActivity a ON c.positionId = a.positionId
                WHERE c.openDate BETWEEN :startOpenDay AND :endOpenDay and a.type='Position closed'
                ORDER BY c.closeDate DESC
            """)
    List<TradePositionProjection> findProjectedByOpenDateBetween(LocalDateTime startOpenDay, LocalDateTime endOpenDay);



}
