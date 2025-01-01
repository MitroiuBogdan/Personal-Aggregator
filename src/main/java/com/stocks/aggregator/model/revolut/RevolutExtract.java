package com.stocks.aggregator.model.revolut;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "revolut_statement")
public class RevolutExtract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String product;

    @Column(name = "started_date")
    private LocalDateTime startedDate;

    @Column(name = "completed_date")
    private LocalDateTime completedDate;

    @Column(length = 1000)
    private String description;

    private Double amount;

    private Double fee;

    private String currency;

    private String state;

    private Double balance;

}
