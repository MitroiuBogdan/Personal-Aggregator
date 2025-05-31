package com.stocks.aggregator.monitor_status;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WeekStatusRecordRepository extends JpaRepository<WeekStatusRecord, DayTradeStatusId> {

    Optional<WeekStatusRecord> findByTradeWeekAndTradeYear(String tradeWeek, String tradeYear);

    // Add any custom query methods as needed
}
