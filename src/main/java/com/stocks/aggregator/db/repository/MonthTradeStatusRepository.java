package com.stocks.aggregator.db.repository;


import com.stocks.aggregator.model.MonthTradeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonthTradeStatusRepository extends JpaRepository<MonthTradeStatus, Long> {

}
