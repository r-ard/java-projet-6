package com.paymybuddy.dto;

public class UserTransactionDTO {
    private boolean sent;

    private String nickname;

    private String email;

    private String description;

    private double amount;

    private double fee;

    private String date;

    public UserTransactionDTO() {

    }

    public UserTransactionDTO(
            boolean sent,
            String nickname,
            String email,
            String description,
            double amount,
            double fee,
            String date
    ) {
        this.sent = sent;
        this.nickname = nickname;
        this.email = email;
        this.description = description;
        this.amount = amount;
        this.fee = fee;
        this.date = date;
    }

    public boolean isSent() {
        return sent;
    }

    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    public double getFee() { return fee; }

    public String getDate() {
        return date;
    }
}
