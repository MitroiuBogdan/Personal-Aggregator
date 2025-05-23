package com.stocks.aggregator.etoro;


import com.stocks.aggregator.etoro.repo.DayTradeStatusRepository;
import com.stocks.aggregator.etoro.model.DayTradeStatus;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DayTradeStatusDomainService {

    private final DayTradeStatusRepository repository;

    public Map<Month, List<DayTradeStatus>> getDayTradeStatusGroupedByMonth() {
        List<DayTradeStatus> dayTradeStatuses = repository.findAllOrderByDate();
        return dayTradeStatuses.stream()
                .collect(Collectors.groupingBy(d -> d.getDate().getMonth()));
    }

    @Transactional
    public List<DayTradeStatus> getAllDayTradeStatuses() {
        return repository.findAllByOrderByDateDesc();
    }
}


