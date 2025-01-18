package com.stocks.aggregator.api.domain;

import com.stocks.aggregator.etoro.model.DayTradeStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class DayTradeStatusResponse {

    private List<DayTradeStatus> statuses;


}
