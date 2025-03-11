package com.paymybuddy.repository;

import com.paymybuddy.entity.Transaction;
import com.paymybuddy.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    @Query("SELECT t FROM Transaction t WHERE t.sender = :user OR t.receiver = :user ORDER BY t.createdAt DESC")
    List<Transaction> findTransactionsOfUser(@Param("user")User user);

    @Query("SELECT t FROM Transaction t WHERE t.sender = :user OR t.receiver = :user ORDER BY t.createdAt DESC LIMIT :limit")
    List<Transaction> findNumbersTransactionsOfUser(@Param("user")User user, @Param("limit") int limit);

    @Query(value = "SELECT t FROM Transaction t WHERE t.sender = :user OR t.receiver = :user ORDER BY t.createdAt DESC LIMIT :limit OFFSET :offset")
    List<Transaction> findPagedTransactionsOfUser(@Param("user")User user, @Param("limit") int limit, @Param("offset") int offset);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.sender = :user OR t.receiver = :user")
    long countTransactionsOfUser(@Param("user")User user);
}
