package com.stocks.aggregator.position_monitor;

import java.time.LocalDateTime;

public interface TradePositionProjection {

    Long getPositionId();
    Double getAmount();
    LocalDateTime getOpenDate();
    LocalDateTime getCloseDate();
    Double getLeverage();
    String getAction();
    Double getOpenRate();
    Double getCloseRate();
    Double getTakeProfitRate();
    Double getStopLossRate();
    Double getProfitUsd();
    String getLongShort();
    Double getBalance();
    Double getRealizedEquity();
    String getType();
    Double getUnits();
    Long getLastPositionId();


    default String toStringFormatted() {
        return "TradePositionProjection{" +
                "positionId=" + getPositionId() +
                ", amount=" + getAmount() +
                ", openDate=" + getOpenDate() +
                ", closeDate=" + getCloseDate() +
                ", leverage=" + getLeverage() +
                ", action='" + getAction() + '\'' +
                ", openRate=" + getOpenRate() +
                ", closeRate=" + getCloseRate() +
                ", takeProfitRate=" + getTakeProfitRate() +
                ", stopLossRate=" + getStopLossRate() +
                ", profitUsd=" + getProfitUsd() +
                ", longShort='" + getLongShort() + '\'' +
                ", balance=" + getBalance() +
                ", realizedEquity=" + getRealizedEquity() +
                ", type='" + getType() + '\'' +
                '}';
    }
}
