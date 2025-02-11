package com.paymybuddy.controller.user;

import com.paymybuddy.dto.CreateUserTransactionDTO;
import com.paymybuddy.dto.UpdatePasswordDTO;
import com.paymybuddy.exception.user.InvalidOldPasswordUpdateException;
import com.paymybuddy.exception.user.NewEqualsOldPasswordUpdateException;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.security.CustomUserDetails;
import com.paymybuddy.service.UserService;
import com.paymybuddy.utils.AuthenticationUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/dashboard/account")
    public String showAccount(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "success", required = false) String success,
            Model model
    ) {
        CustomUserDetails user = (CustomUserDetails) AuthenticationUtils.getAuthenticatedUser();

        model.addAttribute("username", user.getUsername());
        model.addAttribute("email", user.getUser().getEmail());
        model.addAttribute("selectedTab", 4);

        if(error != null) {
            model.addAttribute("errorMessage", error.length() > 0 ? error : "Veuillez bien remplir tous les champs");
        }

        if(success != null) {
            model.addAttribute("successMessage", success.length() > 0 ? success : "Le contacte a été ajouté");
        }

        return "dashboard/account";
    }

    @PostMapping("/dashboard/account/change-password")
    public String changePassword(
            @Valid @ModelAttribute("user") UpdatePasswordDTO updatePasswordDTO,
            BindingResult result,
            Model model
    ) {
        CustomUserDetails user = (CustomUserDetails) AuthenticationUtils.getAuthenticatedUser();

        if(result.hasErrors()) {
            return "redirect:/dashboard/account?error=" + result.getFieldError().getDefaultMessage();
        }

        try {
            userService.updateUserPassword(user.getUser(), updatePasswordDTO.getOldpassword(), updatePasswordDTO.getNewpassword());
        }
        catch(InvalidOldPasswordUpdateException ex) {
            return "redirect:/dashboard/account?error=Invalid old password";
        }
        catch (NewEqualsOldPasswordUpdateException ex) {
            return "redirect:/dashboard/account?error=New and old password are the same";
        }

        return "redirect:/dashboard/account?success";
    }
}
