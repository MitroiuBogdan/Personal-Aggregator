package com.stocks.aggregator.position_monitor;

import com.stocks.aggregator.etoro.repo.ClosedTradePositionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TradePositionLoader {

    private final ClosedTradePositionRepository closedRepository;
    private final TradePositionRecordRepository repository;

    public void loadClosedPosition(LocalDateTime startLoadingDate, LocalDateTime endLoadingDate) {
        List<TradePositionProjection> projections = closedRepository.findProjectedByOpenDateBetween(startLoadingDate, endLoadingDate);

        // De-duplicate by positionId + closeDate, then sort by closeDate descending
        List<TradePositionProjection> sortedUniqueProjections = projections.stream()
                .collect(Collectors.toMap(
                        p -> p.getPositionId() + "|" + p.getCloseDate(),
                        p -> p,
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ))
                .values()
                .stream()
                .sorted(Comparator.comparing(TradePositionProjection::getCloseDate))
                .toList();

        // Track previous positionId to set as lastPositionId
        AtomicReference<Long> previousIdRef = new AtomicReference<>(null);

        List<TradePositionRecord> recordsToSave = sortedUniqueProjections.stream()
                .map(projection -> {
                    var dto = TradePositionProjectionMapper.INSTANCE.apply(projection);
                    if (projection.getLastPositionId() == null) {
                        dto.setLastPositionId(previousIdRef.get());
                        previousIdRef.set(projection.getPositionId());
                    }
                    return TradePositionMapper.toEntity(dto);
                })
                .toList();

        // Save all records
        repository.saveAll(recordsToSave);
    }
}

