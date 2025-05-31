package com.stocks.aggregator.monitor_status;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "week_trade_status_records")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@IdClass(WeekStatusId.class)
public class WeekStatusRecord {

    @Id
    @Column(name = "trade_week", nullable = false)
    String tradeWeek;
    @Id
    @Column(name = "trade_year", nullable = false)
    String tradeYear;


    @Column(name = "week_id")
    private String weekId;
    @Column(name = "previous_week_id")
    private String previousWeekId;
    @Column(name = "last_closed_date")
    private LocalDateTime lastClosedDate;

    @Column(name = "total_trades")
    private long totalTrades;

    @Column(name = "total_profit")
    private double totalProfit;

    @Column(name = "profit_rate")
    private double profitRate;

    @Column(name = "average_week_profit")
    private double averageWeekProfit;

    @Column(name = "average_win_week_pip")
    private double averageWinWeekPip;

    @Column(name = "realised_equity")
    private double realisedEquity;

    @Column(name = "realised_equity_change")
    private double realisedEquityChange;

    @Column(name = "lost_trades")
    private double lostTrades;

    @Column(name = "won_trades")
    private double wonTrades;

    @Column(name = "total_won_value")
    private double totalWonValue;

    @Column(name = "total_lost_value")
    private double totalLostValue;

    @Column(name = "total_deposit")
    private double totalDeposit;

    @Column(name = "total_withdraw")
    private double totalWithdraw;

    @Column(name = "deposit_withdraw_fee")
    private double depositWithdrawFee;

    @Column(name = "position_fee")
    private double positionFee;
}
