package com.paymybuddy.dto;

import jakarta.validation.constraints.NotBlank;

public class DeleteUserContactDTO {
    @NotBlank(message = "L'id du contact est requis")
    private int contactId;

    public DeleteUserContactDTO() {

    }

    public DeleteUserContactDTO(int contactId) {
        this.contactId = contactId;
    }

    public int getContactId() {
        return contactId;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }
}
