package com.stocks.aggregator.monitor_status;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "day_trade_status_records")
@Data
@IdClass(DayTradeStatusId.class)
public class DayTradeStatusRecord {

    @Column(name = "trade_day", nullable = false)
    String day;
    @Id
    @Column(name = "trade_date", nullable = false)
    private LocalDate tradeDate;

    @Id
    @Column(name = "total_trades")
    private long totalTrades;

    @Column(name = "total_profit")
    private double totalProfit;

    @Column(name = "profit_rate")
    private double profitRate;

    @Column(name = "average_monthly_profit")
    private double averageMonthlyProfit;

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
