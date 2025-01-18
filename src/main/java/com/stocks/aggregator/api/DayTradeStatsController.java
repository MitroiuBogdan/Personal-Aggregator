package com.stocks.aggregator.api;


import com.stocks.aggregator.api.domain.DayTradeStatusResponse;
import com.stocks.aggregator.etoro.DayTradeStatusDomainService;
import com.stocks.aggregator.etoro.model.DayTradeStatus;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class DayTradeStatsController {

    final DayTradeStatusDomainService dayTradeStatusDomainService;

    @GetMapping(value = "/trade-status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DayTradeStatusResponse> getAllDayTradeStauses() {
        List<DayTradeStatus> statuses = dayTradeStatusDomainService.getAllDayTradeStatuses();
        DayTradeStatusResponse response = new DayTradeStatusResponse(statuses);
        return ResponseEntity.ok(response);
    }
}