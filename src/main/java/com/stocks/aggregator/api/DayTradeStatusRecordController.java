package com.stocks.aggregator.api;

import com.stocks.aggregator.monitor_status.DayTradeStatusId;
import com.stocks.aggregator.monitor_status.DayTradeStatusRecord;
import com.stocks.aggregator.monitor_status.DayTradeStatusRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/day-trade-status")
@RequiredArgsConstructor
public class DayTradeStatusRecordController {

    private final DayTradeStatusRecordRepository repository;

    @GetMapping
    public List<DayTradeStatusRecord> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{tradeDate}/{totalTrades}")
    public ResponseEntity<DayTradeStatusRecord> getById(@PathVariable String tradeDate,
                                                        @PathVariable long totalTrades) {
        var id = new DayTradeStatusId(java.time.LocalDate.parse(tradeDate), totalTrades);
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
