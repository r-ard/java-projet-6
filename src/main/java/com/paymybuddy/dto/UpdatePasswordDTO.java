package com.paymybuddy.dto;

import jakarta.validation.constraints.NotBlank;

public class UpdatePasswordDTO {
    @NotBlank(message="Veuillez entrer le mot de passe actuel")
    private String oldpassword;

    @NotBlank(message="Veuillez entrer un nouveau mot de passe")
    private String newpassword;

    public UpdatePasswordDTO() {

    }

    public String getOldpassword() {
        return oldpassword;
    }

    public void setOldpassword(String oldpassword) {
        this.oldpassword = oldpassword;
    }

    public String getNewpassword() {
        return newpassword;
    }

    public void setNewpassword(String newpassword) {
        this.newpassword = newpassword;
    }
}
