package com.stocks.aggregator.monitor_status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DayTradeStatusId implements Serializable {

    private LocalDate tradeDate;
    private long totalTrades;
}

