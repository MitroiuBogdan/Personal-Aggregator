package com.stocks.aggregator.etoro.repo;


import com.stocks.aggregator.etoro.model.MonthTradeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonthTradeStatusRepository extends JpaRepository<MonthTradeStatus, Long> {

}
