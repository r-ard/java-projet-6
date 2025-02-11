package com.paymybuddy.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserRegistrationDTO {
    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    @Size(min = 3, max = 128, message = "Le nom d'utilisateur doit contenir entre 3 et 128 caractères")
    private String username;

    @NotBlank(message = "L'adresse email est obligatoire")
    @Email(message = "Veuillez fournir une adresse email valide")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, max = 128, message = "Le mot de passe doit contenir entre 6 et 128 caractères")
    private String password;

    @NotBlank(message = "Veuillez confirmer le mot de passe")
    private String confirmPassword;

    public UserRegistrationDTO() {
    }

    public UserRegistrationDTO(String username, String email, String password, String confirmPassword) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
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

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}