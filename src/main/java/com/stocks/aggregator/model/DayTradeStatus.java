package com.stocks.aggregator.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "day_trade_status")
@Data
public class DayTradeStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dayTradeId; // Primary key with auto-increment

    @Column(name = "nrOfTrades")
    private Long nrOfTrades;

    @Column(name = "profit")
    private Double profit;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "profit_rate")
    private Double profitRate;
    @Column(name = "nr_lost_transactions")
    private Double nrLostTransactions;
    @Column(name = "nr_won_transactions")
    private Double nrWonTransactions;

}
