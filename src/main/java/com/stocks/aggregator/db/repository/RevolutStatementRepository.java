package com.stocks.aggregator.db.repository;


import com.stocks.aggregator.model.revolut.RevolutExtract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RevolutStatementRepository extends JpaRepository<RevolutExtract, Long> {

}
