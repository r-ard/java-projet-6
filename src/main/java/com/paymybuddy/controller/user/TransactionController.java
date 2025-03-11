package com.paymybuddy.controller.user;

import com.paymybuddy.dto.CreateUserTransactionDTO;
import com.paymybuddy.dto.UserContactDTO;
import com.paymybuddy.entity.Contact;
import com.paymybuddy.exception.transaction.InsufficientBalanceException;
import com.paymybuddy.exception.transaction.TransactionUserNotFoundException;
import com.paymybuddy.security.CustomUserDetails;
import com.paymybuddy.service.ContactService;
import com.paymybuddy.service.TransactionService;
import com.paymybuddy.service.UserService;
import com.paymybuddy.utils.AuthenticationUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

    @Autowired
    private ContactService contactService;

    @Autowired
    private UserService userService;

    @GetMapping("/dashboard/transaction")
    public String showTransactionForm(
            @RequestParam(value = "error", required = false) String error,
            Model model
    ) {
        CustomUserDetails user = AuthenticationUtils.getAuthenticatedUser();
        List<Contact> userContacts = contactService.getContactsOfUser(user.getUser());

        if(error != null) {
            model.addAttribute("errorMessage", !error.isEmpty() ? error : "Veuillez bien remplir tous les champs");
        }

        model.addAttribute("username", user.getUsername());
        model.addAttribute("contacts", userContacts);
        model.addAttribute("selectedTab", 0);

        return "dashboard/transaction";
    }

    @PostMapping("/dashboard/transaction")
    public String createTransaction(
            @Valid @ModelAttribute("transaction")CreateUserTransactionDTO transactionDto,
            BindingResult result,
            Model model
    ) {
        CustomUserDetails user = AuthenticationUtils.getAuthenticatedUser();

        if(result.hasErrors()) {
            return "redirect:/dashboard/transaction?error=" + result.getFieldError().getDefaultMessage();
        }

        Contact contact = null;

        try {
            contact = contactService.getContactOfUserById(user.getUser(), transactionDto.getContactId());
            if(contact == null) {
                throw new Exception();
            }
        }
        catch(Exception ex) {
            return "redirect:/dashboard/transaction?error=Unknown contact";
        }

        try {
            transactionService.registerUserTransaction(user.getUser(), contact.getContact(), transactionDto.getAmount(), transactionDto.getDescription());
        }
        catch(InsufficientBalanceException ex) {
            return "redirect:/dashboard/transaction?error=Insufficient balance";
        }
        catch(TransactionUserNotFoundException ex) {
            return "redirect:/dashboard/transaction?error=A user of the transaction doesn't exist";
        }

        model.addAttribute("username", user.getUsername());
        model.addAttribute("name", contact.getName());
        model.addAttribute("email", contact.getContact().getEmail());
        model.addAttribute("amount", transactionDto.getAmount());

        return "/dashboard/transaction-success";
    }
}
