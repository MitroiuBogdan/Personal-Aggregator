package com.stocks.aggregator.etoro.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Month;

@Entity
@Table(name = "month_trade_status")
@Data
public class MonthTradeStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id; // Primary key with auto-increment

    @Column(name = "nr_of_trades")
    private Long nrOfTrades;

    @Column(name = "profit")
    private Double profit;

    @Column(name = "month")
    private Month month;

    @Column(name = "month_name")
    private String month_name;
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

    @Column(name = "top_one_win")
    private Double top_one_win;

    @Column(name = "top_second_win")
    private Double top_second_win;

    @Column(name = "top_third_win")
    private Double top_third_win;

    @Column(name = "top_one_lose")
    private Double top_one_lose;

    @Column(name = "top_second_lose")
    private Double top_second_lose;

    @Column(name = "top_third_lose")
    private Double top_third_lose;

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
