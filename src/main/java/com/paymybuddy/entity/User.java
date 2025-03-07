package com.paymybuddy.entity;

import java.time.LocalDateTime;
import java.util.Date;

import jakarta.persistence.*;
import org.springframework.cglib.core.Local;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name="users")
@EntityListeners(AuditingEntityListener.class)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 128, nullable = false)
    private String username;

    @Column(length = 128, nullable = false)
    private String email;

    @Column(length = 128, nullable = false)
    private String password;

    @Column(nullable = false)
    private double balance;

    @CreatedDate
    @Column(name="created_at")
    private LocalDateTime createdAt;

    public User() {

    }
  
    public User(String username, String email, String password, double balance) {
      this.username = username;
      this.email = email;
      this.password = password;
      this.balance = balance;
    }

    public void setId(int id) { this.id = id; }

    public Integer getId() {
      return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
