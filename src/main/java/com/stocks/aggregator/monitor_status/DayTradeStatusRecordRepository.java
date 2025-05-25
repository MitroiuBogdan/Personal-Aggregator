package com.stocks.aggregator.monitor_status;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface DayTradeStatusRecordRepository extends JpaRepository<DayTradeStatusRecord, DayTradeStatusId> {

    Optional<DayTradeStatusRecord> findByTradeDate(LocalDate tradeDate);

    // Add any custom query methods you need here
}
