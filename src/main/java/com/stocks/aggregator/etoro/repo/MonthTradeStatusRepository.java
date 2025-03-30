package com.stocks.aggregator.etoro.repo;


import com.stocks.aggregator.etoro.model.DayTradeStatus;
import com.stocks.aggregator.etoro.model.MonthTradeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

public interface MonthTradeStatusRepository extends JpaRepository<MonthTradeStatus, Long> {

    @Query("SELECT d.month FROM MonthTradeStatus d GROUP BY d.month HAVING COUNT(d) > 1")
    List<Month> findDuplicateDates();

    List<MonthTradeStatus> findByMonth(Month month);
}
