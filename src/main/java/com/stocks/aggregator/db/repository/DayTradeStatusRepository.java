package com.stocks.aggregator.db.repository;


import com.stocks.aggregator.model.DayTradeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DayTradeStatusRepository extends JpaRepository<DayTradeStatus, Long> {

}
