package com.stocks.aggregator.etoro.repo;


import com.stocks.aggregator.etoro.model.DayTradeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface DayTradeStatusRepository extends JpaRepository<DayTradeStatus, Long> {
    @Query("SELECT d FROM DayTradeStatus d ORDER BY d.date")
    List<DayTradeStatus> findAllOrderByDate();

    List<DayTradeStatus> findAllByOrderByDateDesc();

    List<DayTradeStatus> findAllByOrderByDateAsc();

    @Query("SELECT d.date FROM DayTradeStatus d GROUP BY d.date HAVING COUNT(d) > 1")
    List<LocalDate> findDuplicateDates();

    List<DayTradeStatus> findByDate(LocalDate date);


}
