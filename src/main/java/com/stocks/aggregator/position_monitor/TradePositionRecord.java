package com.stocks.aggregator.position_monitor;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "trade_position_records")
@IdClass(TradePositionKey.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TradePositionRecord {


    @Id
    private Long positionId;

    @Id
    private LocalDateTime closeDate;

    private double amount;
    private LocalDateTime openDate;
    private double leverage;
    private String action;
    private double openRate;
    private double closeRate;
    private double takeProfitRate;
    private double stopLossRate;
    private double profitUsd;
    private String longShort;

    private double balance;
    private double realizedEquity;
    private String type;

    private int holdingTimeHour;
    private int holdingTimeMinutes;
    private int pips;
    private double profitPercent;
    private double profitPercentAccount;
    private double openPercentRisk;

    private String month; // store as "YYYY-MM"
    private String week;

    private String day;

    private double positionRisk;
    private double riskToRewardRatio;
    private String riskToReward;

    private Long lastPosition;


}
