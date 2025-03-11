package com.paymybuddy.controller;

import com.paymybuddy.dto.UserRegistrationDTO;
import com.paymybuddy.exception.user.EmailAlreadyTakenException;
import com.paymybuddy.exception.user.UsernameAlreadyTakenException;
import com.paymybuddy.service.UserService;
import com.paymybuddy.utils.AuthenticationUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Component
public class AuthController {
    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String showLoginForm(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "success", required = false) String success,
            @RequestParam(value = "logout", required = false) String logout,
            Model model
    ) {
        if(AuthenticationUtils.isClientAuthenticated()) {
            return "redirect:/dashboard";
        }

        model.addAttribute("pageTitle", "PayMyBuddy - Log in");

        if(error != null) {
            model.addAttribute("errorMessage", "Invalid email address or password");
        }

        if(success != null) {
            model.addAttribute("successMessage", "Your account has been created, please log in");
        }

        if(logout != null) {
            model.addAttribute("successMessage", "You have been successfully logged out");
        }

        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(
            @RequestParam(value = "error", required = false) String error,
            Model model
    ) {
        if(AuthenticationUtils.isClientAuthenticated()) {
            return "redirect:/dashboard";
        }

        model.addAttribute("pageTitle", "PayMyBuddy - Sign up");
        model.addAttribute("user", new UserRegistrationDTO());

        if(error != null) {
            model.addAttribute("errorMessage", error.length() > 0 ? error : "Please fill all fields");
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
            result.rejectValue("confirmPassword", "error.user", "Passwords don't match");
        }

        if (result.hasErrors()) {
            return "redirect:/register?error=" + result.getFieldError().getDefaultMessage();
        }

        try {
            userService.registerUser(userDto);
        }
        catch(EmailAlreadyTakenException exception) {
            return "redirect:/register?error=Email is already taken";
        }
        catch(UsernameAlreadyTakenException exception) {
            return "redirect:/register?error=Username is already taken";
        }

        return "redirect:/login?success";
    }
}
