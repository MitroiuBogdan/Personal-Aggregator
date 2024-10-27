package com.stocks.aggregator.db.repository;


import com.stocks.aggregator.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
