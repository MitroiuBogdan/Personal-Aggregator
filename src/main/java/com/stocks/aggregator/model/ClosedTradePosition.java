package com.stocks.aggregator.model;



import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "closed_trade_positions")
@Data
public class ClosedTradePosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tradeId;  // Primary key

    private Long positionId;  // Original position ID, not a primary key


    @Column(name = "long_short")
    private String longShort;

    @Column(name = "user_id")
    private Integer userId;  // User ID to link the trade to a user

    private String action;  // Long/Short action

    private BigDecimal amount;  // Trade amount

    private BigDecimal units;  // Number of units traded


    @Column(name = "open_date")
    private LocalDateTime openDate;  // Opening date


    @Column(name = "close_date")
    private LocalDateTime closeDate;  // Closing date

    private BigDecimal leverage;  // Leverage

    @Column(name = "spread_fees_usd")
    private BigDecimal spreadFeesUsd;  // Spread fees in USD

    @Column(name = "market_spread_usd")
    private BigDecimal marketSpreadUsd;  // Market spread in USD


    @Column(name = "profit_usd")
    private BigDecimal profitUsd;  // Profit in USD


    @Column(name = "fx_rate_at_open")
    private BigDecimal fxRateAtOpen;  // FX rate at open

    @Column(name = "fx_rate_at_close")
    private BigDecimal fxRateAtClose;  // FX rate at close


    @Column(name = "open_rate")
    private BigDecimal openRate;  // Open rate


    @Column(name = "close_rate")
    private BigDecimal closeRate;  // Close rate


    @Column(name = "take_profit_rate")
    private BigDecimal takeProfitRate;  // Take profit rate


    @Column(name = "stop_loss_rate")
    private BigDecimal stopLossRate;  // Stop loss rate


    @Column(name = "overnight_fees_dividends")
    private BigDecimal overnightFeesDividends;  // Overnight fees/dividends


    @Column(name = "copied_from")
    private String copiedFrom;  // Copied from info

    private String type;  // Type of trade

    private String isin;  // ISIN code

    @Lob  // Large object for notes
    private String notes;  // Additional notes


}