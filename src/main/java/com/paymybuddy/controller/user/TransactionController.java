package com.paymybuddy.controller.user;

import com.paymybuddy.dto.CreateUserTransactionDTO;
import com.paymybuddy.dto.UserContactDTO;
import com.paymybuddy.dto.UserTransactionDTO;
import com.paymybuddy.dto.UserTransactionPageDTO;
import com.paymybuddy.entity.Contact;
import com.paymybuddy.entity.User;
import com.paymybuddy.exception.transaction.InsufficientBalanceException;
import com.paymybuddy.exception.transaction.TransactionUserNotFoundException;
import com.paymybuddy.security.CustomUserDetails;
import com.paymybuddy.service.ContactService;
import com.paymybuddy.service.TransactionService;
import com.paymybuddy.service.UserService;
import com.paymybuddy.utils.AuthenticationUtils;
import com.paymybuddy.utils.FormatUtils;
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

    private static final int pageSize = 3;

    @GetMapping("/dashboard/transaction")
    public String showTransactionForm(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "page", required = false, defaultValue = "0") String page,
            Model model
    ) {
        int parsedPageValue = FormatUtils.parseIntValue(page);

        CustomUserDetails user = AuthenticationUtils.getAuthenticatedUser();
        List<UserTransactionPageDTO> viewPages = this.getUserAvailableTransactionPages(user.getUser(), parsedPageValue);
        List<UserTransactionDTO> viewTransactions = transactionService.getViewUserTransactions(
                user.getUser(),
                pageSize,
                parsedPageValue
        );

        // Calculate primaryPage value
        {
            UserTransactionPageDTO primaryPage = viewPages.isEmpty() ? null : viewPages.get(0);
            model.addAttribute("primaryPage", (primaryPage == null || primaryPage.isCurrent()) ? null : primaryPage.getPageValue());
        }

        // Calculate latestPage value
        {
            UserTransactionPageDTO latestPage = viewPages.isEmpty() ? null : viewPages.get(viewPages.size()-1);
            model.addAttribute("latestPage", (latestPage == null || latestPage.isCurrent()) ? null : latestPage.getPageValue());
        }

        List<Contact> userContacts = contactService.getContactsOfUser(user.getUser());

        if(error != null) {
            model.addAttribute("errorMessage", !error.isEmpty() ? error : "Please fill all fields");
        }

        model.addAttribute("username", user.getUsername());
        model.addAttribute("contacts", userContacts);
        model.addAttribute("selectedTab", 2);
        model.addAttribute("transactions", viewTransactions);
        model.addAttribute("pages", viewPages);

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



    protected int getUserTransactionsPagesAmount(User user) {
        long transactionsAmount = transactionService.getUserTransactionsAmount(user);

        if(transactionsAmount > 0) {
            return (int)Math.ceil( transactionsAmount / pageSize );
        }

        return 0;
    }

    protected List<UserTransactionPageDTO> getUserAvailableTransactionPages(User user, int selectedPage) {
        int pagesAmount = this.getUserTransactionsPagesAmount(user);

        // Normalize selectedPage value
        if(selectedPage < 1) selectedPage = 1;
        else if(selectedPage > pagesAmount) selectedPage = pagesAmount;

        List<UserTransactionPageDTO> out = new ArrayList<>();

        for(int i = 1; i <= pagesAmount; i++) {
            UserTransactionPageDTO dto = new UserTransactionPageDTO(String.valueOf(i), i == selectedPage);
            out.add(dto);
        }

        return out;
    }
}
