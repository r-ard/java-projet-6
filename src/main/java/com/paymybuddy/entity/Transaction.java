package com.paymybuddy.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name="transactions")
@EntityListeners(AuditingEntityListener.class)
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "sender", referencedColumnName="id", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver", referencedColumnName="id", nullable = false)
    private User receiver;

    @Column(length = 256, nullable = true)
    private String description;

    @Column(nullable = false)
    private double amount;

    @Column(nullable = false)
    private double fee;

    @CreatedDate
    @Column(name="created_at")
    private LocalDateTime createdAt;

    public Transaction() {

    }

    public Transaction(User from, User to, String description, double amount, double fee) {
        this.sender = from;
        this.receiver = to;
        this.description = description;
        this.amount = amount;
        this.fee = fee;
    }

    public Integer getId() {
        return id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User from) {
        this.sender = from;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User to) {
        this.receiver = to;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getFee() { return fee; }

    public void setFee(double fee) { this.fee = fee; }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
