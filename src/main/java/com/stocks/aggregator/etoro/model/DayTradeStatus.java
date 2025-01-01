package com.stocks.aggregator.etoro.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "day_trade_status")
@Data
public class DayTradeStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id; // Primary key with auto-increment

    @Column(name = "nr_of_trades")
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

    @Column(name = "won_value")
    private Double wonValue;

    @Column(name = "lose_value")
    private Double loseValue;

    @Column(name = "average_lose")
    private Double averageLose;

    @Column(name = "average_win")
    private Double averageWin;

    @Column(name = "won_value_long")
    private Double wonValueLong;

    @Column(name = "lose_value_long")
    private Double loseValueLong;

    @Column(name = "win_value_short")
    private Double winValueShort;

    @Column(name = "lose_value_short")
    private Double loseValueShort;

    @Column(name = "balance")
    private Double balance;

    @Column(name = "deposit")
    private Double deposit;

    @Column(name = "withdraw")
    private Double withdraw;

    @Column(name = "deposit_withdraw_fee")
    private Double depositWithdrawFee;

    @Column(name = "position_fee")
    private Double positionFee;

    @Column(name = "balance_change")
    private Double balanceChange;
}
