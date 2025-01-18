package com.stocks.aggregator.global.repo;


import com.stocks.aggregator.global.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
