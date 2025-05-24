package com.stocks.aggregator.position_monitor;

import com.stocks.aggregator.etoro.repo.ClosedTradePositionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.LinkedHashSet;

@Component
@AllArgsConstructor
public class TradePositionLoader {

    private ClosedTradePositionRepository closedRepository;
    private TradePositionRecordRepository repository;

    public void loadClosedPosition(LocalDateTime startLoadingDate, LocalDateTime endLoadingDate) {
        List<TradePositionProjection> positionProjections = closedRepository.findProjectedByOpenDateBetween(startLoadingDate, endLoadingDate);

        Set<TradePositionProjection> uniqueProjections = positionProjections.stream()
                .collect(Collectors.toMap(
                        p -> p.getPositionId() + "|" + p.getCloseDate(), // Composite key
                        p -> p,
                        (existing, replacement) -> existing // Keep first
                ))
                .values()
                .stream()
                .sorted(Comparator.comparing(TradePositionProjection::getCloseDate).reversed())
                .collect(Collectors.toCollection(LinkedHashSet::new));

        List<TradePositionRecordDto> tradePositionRecordDtos = uniqueProjections.stream()
                .map(TradePositionProjectionMapper.INSTANCE)
                .toList();


        tradePositionRecordDtos.forEach(System.out::println);

        tradePositionRecordDtos.forEach(tradePositionRecordDto -> repository.save(TradePositionMapper.toEntity(tradePositionRecordDto)));

        System.out.println(uniqueProjections.size());
//        uniqueProjections.stream().forEach(tradePositionProjection -> System.out.println(tradePositionProjection.toStringFormatted()));


        // Calculate Trade position record


    }


}
