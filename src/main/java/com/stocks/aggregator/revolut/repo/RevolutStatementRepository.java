package com.stocks.aggregator.revolut.repo;


import com.stocks.aggregator.revolut.model.RevolutStatement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RevolutStatementRepository extends JpaRepository<RevolutStatement, Long> {

    List<RevolutStatement> findByType(String type);

    List<RevolutStatement> findByDescription(String description);
}
