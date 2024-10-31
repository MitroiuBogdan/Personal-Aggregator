package com.stocks.aggregator.model.etoro;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "account_activity")
@Data
public class AccountActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long activityId; // Primary key with auto-increment

    @Column(name = "user_id")
    private Integer userId; // User ID column as an integer

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "type", length = 50)
    private String type;

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    @Column(name = "amount", precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "units", precision = 15, scale = 2)
    private BigDecimal units;

    @Column(name = "realized_equity_change", precision = 15, scale = 2)
    private BigDecimal realizedEquityChange;

    @Column(name = "realized_equity", precision = 15, scale = 2)
    private BigDecimal realizedEquity;

    @Column(name = "balance", precision = 15, scale = 2)
    private BigDecimal balance;

    @Column(name = "position_id")
    private Long positionId;

    @Column(name = "asset_type", length = 50)
    private String assetType;

    @Column(name = "nwa", precision = 15, scale = 2)
    private BigDecimal nwa;



}
