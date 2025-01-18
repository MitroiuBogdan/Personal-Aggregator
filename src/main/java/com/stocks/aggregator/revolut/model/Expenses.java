package com.stocks.aggregator.revolut.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "expenses")
public class Expenses {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String type;

    @Column(name = "date")
    private LocalDateTime completedDate;

    @Column(length = 1000)
    private String description;

    private Double amount;

    private String currency;
}
