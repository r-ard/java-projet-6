package com.paymybuddy.repository;

import com.paymybuddy.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByEmail(String email);

    User findByUsername(String username);

    @Query("UPDATE User u SET u.balance = :balance WHERE u.id = :userId")
    @Modifying
    public int setUserBalance(int userId, double balance);
}
