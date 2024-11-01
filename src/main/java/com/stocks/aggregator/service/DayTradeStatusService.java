package com.stocks.aggregator.service;

import com.stocks.aggregator.db.repository.ClosedTradePositionRepository;
import com.stocks.aggregator.db.repository.DayTradeStatusRepository;
import com.stocks.aggregator.model.DayTradeStatus;
import com.stocks.aggregator.model.etoro.ClosedTradePosition;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.stocks.aggregator.service.ClosedPositionService.*;

@Service
@AllArgsConstructor
public class DayTradeStatusService {

    private final ClosedTradePositionRepository closedTradePositionRepository;
    private final DayTradeStatusRepository dayTradeStatusRepository;


    public void syncDayTradingInfo() {
        Map<LocalDate, List<ClosedTradePosition>> closedPositionsGroupedByDay = closedTradePositionRepository.findClosedPositionsGroupedByDay();
        System.out.println(closedPositionsGroupedByDay.keySet());
        closedPositionsGroupedByDay.forEach(
                (day, closedPositions) -> {
                    DayTradeStatus dayTradeStatus = new DayTradeStatus();
                    dayTradeStatus.setNrOfTrades((long) closedPositions.size());
                    dayTradeStatus.setDate(day);
                    dayTradeStatus.setProfit(calculateTotalProfitUsd(closedPositions, day));
                    dayTradeStatus.setNrWonTransactions(getWonTransactions(closedPositions));
                    dayTradeStatus.setNrLostTransactions(getLostTransactions(closedPositions));
                    dayTradeStatus.setWonValue(calculateWonValue(closedPositions, day));
                    dayTradeStatus.setLoseValue(calculateLoseValue(closedPositions, day));
                    dayTradeStatus.setAverageLose(calculateAverageLoseValue(closedPositions, day));
                    dayTradeStatus.setAverageWin(calculateAverageWonValue(closedPositions, day));
                    dayTradeStatusRepository.save(dayTradeStatus);

                }
        );
    }
}
