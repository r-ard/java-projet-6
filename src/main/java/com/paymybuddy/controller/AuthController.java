package com.paymybuddy.controller;

import com.paymybuddy.dto.UserRegistrationDTO;
import com.paymybuddy.entity.User;
import com.paymybuddy.service.UserService;
import com.paymybuddy.utils.AuthenticationUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@Controller
@Component
public class AuthController {
    @Value("${app.defaultbalance}")
    private String defaultBalance;

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String showLoginForm(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "success", required = false) String success,
            Model model
    ) {
        if(AuthenticationUtils.isClientAuthenticated()) {
            return "redirect:/dashboard";
        }

        model.addAttribute("pageTitle", "PayMyBuddy - Connexion");

        if(error != null) {
            model.addAttribute("errorMessage", "Adresse email ou mot de passe invalide");
        }

        if(success != null) {
            model.addAttribute("successMessage", "Votre compte a bien été créer, veuillez vous connecter");
        }

        return "auth/login";
    }

    @GetMapping("/logout")
    public String logoutUser(HttpServletRequest request, HttpServletResponse response) {
        AuthenticationUtils.logoutClient(request, response);
        return "redirect:/login?logout";
    }

    @GetMapping("/register")
    public String showRegistrationForm(
            @RequestParam(value = "error", required = false) String error,
            Model model
    ) {
        if(AuthenticationUtils.isClientAuthenticated()) {
            return "redirect:/dashboard";
        }

        model.addAttribute("pageTitle", "PayMyBuddy - S'inscrire");
        model.addAttribute("user", new UserRegistrationDTO());

        if(error != null) {
            model.addAttribute("errorMessage", error.length() > 0 ? error : "Veuillez bien remplir tous les champs");
        }

        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(
            @Valid @ModelAttribute("user") UserRegistrationDTO userDto,
            BindingResult result,
            Model model
    ) {

        if (!userDto.getPassword().equals(userDto.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.user", "Les mots de passe ne correspondent pas");
        }

        if (result.hasErrors()) {
            return "register?error=" + result.getFieldError().getDefaultMessage();
        }

        if(userService.isEmailTaken(userDto.getEmail())) {
            return "register?error=email is already taken";
        }

        if(userService.isUsernameTaken(userDto.getUsername())) {
            return "register?error=username is already taken";
        }

        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        user.setBalance( this.getDefaultBalance() );

        userService.registerUser(user);
        return "redirect:/login?success";
    }

    protected double getDefaultBalance() {
        try {
            return Double.parseDouble(this.defaultBalance);
        }
        catch(Exception e) {
            return 50.0D;
        }
    }
}
