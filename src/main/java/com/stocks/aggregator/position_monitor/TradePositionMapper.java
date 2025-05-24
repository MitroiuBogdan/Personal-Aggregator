package com.stocks.aggregator.position_monitor;

public class TradePositionMapper {

    public static TradePositionRecord toEntity(TradePositionRecordDto dto) {
        return TradePositionRecord.builder()
                .positionId(dto.getPositionId())
                .amount(dto.getAmount())
                .openDate(dto.getOpenDate())
                .closeDate(dto.getCloseDate())
                .leverage(dto.getLeverage())
                .action(dto.getAction())
                .openRate(dto.getOpenRate())
                .closeRate(dto.getCloseRate())
                .takeProfitRate(dto.getTakeProfitRate())
                .stopLossRate(dto.getStopLossRate())
                .profitUsd(dto.getProfitUsd())
                .longShort(dto.getLongShort())

                .balance(dto.getBalance())
                .realizedEquity(dto.getRealizedEquity())
                .type(dto.getType())
                .day(dto.getDay())
                .holdingTimeHour(dto.getHoldingTimeHour())
                .holdingTimeMinutes(dto.getHoldingTimeMinutes())
                .pips(dto.getPips())
                .profitPercent(dto.getProfitPercent())
                .profitPercentAccount(dto.getProfitPercentAccount())
                .openPercentRisk(dto.getOpenPercentRisk())

                .month(dto.getMonth() != null ? dto.getMonth().toString() : null)
                .week(dto.getWeek())

                .positionRisk(dto.getPositionRisk())
                .riskToRewardRatio(dto.getRiskToRewardRatio())
                .riskToReward(dto.getRiskToReward())
                .build();
    }

}
