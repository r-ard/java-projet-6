package com.paymybuddy.repository;

import com.paymybuddy.entity.Transaction;
import com.paymybuddy.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    @Query("SELECT t FROM Transaction t WHERE t.sender = :user OR t.receiver = :user")
    List<Transaction> findTransactionsOfUser(@Param("user")User user);
}
