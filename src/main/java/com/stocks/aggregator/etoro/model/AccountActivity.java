package com.stocks.aggregator.etoro.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "account_activity")
@Data
public class AccountActivity {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long activityId; // Primary key with auto-increment

    @Column(name = "user_id")
    private Integer userId; // User ID column as an integer

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "type", length = 50)
    private String type;

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "units")
    private Double units;

    @Column(name = "realized_equity_change")
    private Double realizedEquityChange;

    @Column(name = "realized_equity")
    private Double realizedEquity;

    @Column(name = "balance")
    private Double balance;

    @Column(name = "position_id")
    private Long positionId;

    @Column(name = "asset_type", length = 50)
    private String assetType;

    @Column(name = "nwa")
    private Double nwa;



}
