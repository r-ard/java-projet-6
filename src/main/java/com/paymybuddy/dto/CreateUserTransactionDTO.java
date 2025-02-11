package com.paymybuddy.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CreateUserTransactionDTO {
    @NotNull(message = "Le receveur est obligatoire")
    private int contactId;

    @NotNull
    @Positive
    private double amount;
    
    private String description;

    public CreateUserTransactionDTO() {

    }

    public CreateUserTransactionDTO(int contactId, double amount, String description) {
        this.contactId = contactId;
        this.amount = amount;
        this.description = description;
    }

    public int getContactId() {
        return contactId;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }
}
