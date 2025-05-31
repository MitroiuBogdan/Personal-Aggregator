package com.stocks.aggregator.monitor_status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeekStatusId implements Serializable {


    String tradeWeek;
    String tradeYear;

}

