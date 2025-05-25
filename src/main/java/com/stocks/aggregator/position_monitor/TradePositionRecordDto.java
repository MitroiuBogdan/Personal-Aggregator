package com.stocks.aggregator.position_monitor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.YearMonth;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Data
public class TradePositionRecordDto {


    private Long positionId;
    private double amount;
    private LocalDateTime openDate;
    private LocalDateTime closeDate;
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

    private YearMonth month;
    private String week;

    private String day;

    private double positionRisk;

    private double riskToRewardRatio;
    private String riskToReward;

    private Long lastPositionId;

}
