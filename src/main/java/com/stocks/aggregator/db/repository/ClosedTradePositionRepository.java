package com.stocks.aggregator.db.repository;

import com.stocks.aggregator.model.etoro.ClosedTradePosition;
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



}
