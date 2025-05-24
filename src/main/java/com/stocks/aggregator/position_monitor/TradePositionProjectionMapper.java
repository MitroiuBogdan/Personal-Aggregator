package com.stocks.aggregator.position_monitor;

import java.time.Duration;
import java.time.YearMonth;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.function.Function;

public class TradePositionProjectionMapper implements Function<TradePositionProjection, TradePositionRecordDto> {

    public static final TradePositionProjectionMapper INSTANCE = new TradePositionProjectionMapper();

    @Override
    public TradePositionRecordDto apply(TradePositionProjection p) {
        int holdingTimeHour = calculateHoldingHours(p);
        int pips = calculatePips(p);
        double profitPercent = calculateProfitPercent(p);
        YearMonth month = p.getCloseDate() != null ? YearMonth.from(p.getCloseDate()) : null;
        String week = formatWeek(p);

        return TradePositionRecordDto.builder()
                .positionId(p.getPositionId())
                .amount(p.getAmount())
                .openDate(p.getOpenDate())
                .closeDate(p.getCloseDate())
                .leverage(p.getLeverage())
                .action(p.getAction())
                .openRate(p.getOpenRate())
                .closeRate(p.getCloseRate())
                .takeProfitRate(p.getTakeProfitRate())
                .stopLossRate(p.getStopLossRate())
                .profitUsd(p.getProfitUsd())
                .longShort(p.getLongShort())
                .balance(p.getBalance())
                .realizedEquity(p.getRealizedEquity())
                .type(p.getType())
                .holdingTimeMinutes(calculateHoldingMinutes(p))
                .holdingTimeHour(holdingTimeHour)
                .pips(pips)
                .profitPercent(profitPercent)
                .profitPercentAccount(calculateProfitPercentAccount(p))
                .openPercentRisk(calculateOpenPercentRisk(p))
                .positionRisk(calculatePositionRisk(p))
                .riskToRewardRatio(calculateRiskToRewardRatio(p))
                .riskToReward(calculateRiskToReward(p))
                .month(month)
                .week(week)
                .lastPositionId(p.getLastPositionId())
                .day(formatDay(p))
                .build();
    }

    private double calculateProfitPercentAccount(TradePositionProjection p) {
        if (p.getProfitUsd() == null || p.getRealizedEquity() == null || p.getRealizedEquity() == 0) {
            return 0.0;
        }
        double percent = (p.getProfitUsd() / p.getRealizedEquity()) * 100;
        return Math.round(percent * 100.0) / 100.0; // round to 2 decimals
    }

    private double calculateOpenPercentRisk(TradePositionProjection p) {
        if (p.getAmount() == null || p.getRealizedEquity() == null || p.getRealizedEquity() == 0) {
            return 0.0;
        }
        double percent = (p.getAmount() / p.getRealizedEquity()) * 100;
        return Math.round(percent * 100.0) / 100.0; // round to 2 decimals
    }

    private String calculateRiskToReward(TradePositionProjection p) {
        if (p.getTakeProfitRate() == null || p.getStopLossRate() == null || p.getOpenRate() == null) {
            return "1:0";
        }

        double risk = Math.abs(p.getOpenRate() - p.getStopLossRate());
        if (risk == 0) return "1:0"; // avoid division by zero

        double reward = Math.abs(p.getTakeProfitRate() - p.getOpenRate());
        double ratio = reward / risk;

        return "1:" + String.format("%.2f", ratio);
    }

    private double calculatePositionRisk(TradePositionProjection p) {
        // Assumptions
        double contractSize = p.getUnits(); // If you have real value, inject it
        int positionSize = 1;      // Optional if you expand TradePositionProjection
        double portfolioValue = p.getRealizedEquity() != null ? p.getRealizedEquity() : 0.0;

        if (p.getStopLossRate() == null || p.getOpenRate() == null || portfolioValue == 0) {
            return 0.0;
        }

        double stopLossDistance = Math.abs(p.getOpenRate() - p.getStopLossRate());
        double totalRisk = stopLossDistance * contractSize * positionSize;

        double riskPercent = (totalRisk / portfolioValue) * 100;
        return Math.round(riskPercent * 100.0) / 100.0; // Rounded to 2 decimals
    }

    private double calculateRiskToRewardRatio(TradePositionProjection p) {
        if (p.getTakeProfitRate() == null || p.getStopLossRate() == null || p.getOpenRate() == null) {
            return 0.0;
        }

        double risk = Math.abs(p.getOpenRate() - p.getStopLossRate());
        if (risk == 0) return 0.0; // avoid division by zero

        double reward = Math.abs(p.getTakeProfitRate() - p.getOpenRate());
        double ratio = reward / risk;

        return Math.round(ratio * 100.0) / 100.0; // round to 2 decimals
    }


    private double calculateProfitPercent(TradePositionProjection p) {
        if (p.getAmount() != null && p.getAmount() != 0 && p.getProfitUsd() != null) {
            double rawPercent = (p.getProfitUsd() / p.getAmount()) * 100;
            return Math.round(rawPercent * 100.0) / 100.0; // Round to 2 decimal places
        }
        return 0.0;
    }

    private int calculateHoldingHours(TradePositionProjection p) {
        if (p.getOpenDate() != null && p.getCloseDate() != null) {
            return (int) Duration.between(p.getOpenDate(), p.getCloseDate()).toHours();
        }
        return 0;
    }

    private int calculateHoldingMinutes(TradePositionProjection p) {
        if (p.getOpenDate() != null && p.getCloseDate() != null) {
            return (int) Duration.between(p.getOpenDate(), p.getCloseDate()).toMinutes();
        }
        return 0;
    }

    private int calculatePips(TradePositionProjection p) {
        if (p.getOpenRate() != null && p.getCloseRate() != null) {
            double diff = p.getCloseRate() - p.getOpenRate();
            return (int) Math.abs(diff); // Assumes 4-digit pricing
        }
        return 0;
    }

    private String formatWeek(TradePositionProjection p) {
        if (p.getCloseDate() != null) {
            WeekFields weekFields = WeekFields.of(Locale.getDefault());
            int weekNum = p.getCloseDate().get(weekFields.weekOfWeekBasedYear());
            int year = p.getCloseDate().getYear();
            int month = p.getCloseDate().getMonth().getValue();
            int day = p.getCloseDate().getDayOfMonth();
            return String.format("W%d", weekNum);
        }
        return null;
    }

    private String formatDay(TradePositionProjection p) {
        if (p.getCloseDate() != null) {
            WeekFields weekFields = WeekFields.of(Locale.getDefault());
            int weekNum = p.getCloseDate().get(weekFields.weekOfWeekBasedYear());
            int year = p.getCloseDate().getYear();
            int month = p.getCloseDate().getMonth().getValue();
            int day = p.getCloseDate().getDayOfMonth();
            return String.format("D%d", day);
        }
        return null;
    }
}
