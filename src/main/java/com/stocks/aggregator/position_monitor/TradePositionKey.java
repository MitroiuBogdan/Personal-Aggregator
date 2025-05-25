package com.stocks.aggregator.position_monitor;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TradePositionKey implements Serializable {
    private Long positionId;
    private LocalDateTime closeDate;
}
