package com.paymybuddy.controller.user;

import com.paymybuddy.dto.UserTransactionDTO;
import com.paymybuddy.entity.Transaction;
import com.paymybuddy.security.CustomUserDetails;
import com.paymybuddy.service.TransactionService;
import com.paymybuddy.service.UserService;
import com.paymybuddy.utils.AuthenticationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class DashboardController {
    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserService userService;

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        CustomUserDetails user = AuthenticationUtils.getAuthenticatedUser();
        List<UserTransactionDTO> viewTransactions = transactionService.getViewUserTransactions(
                user.getUser(),
                5
        );

        // Only display 4 first transactions
        if(viewTransactions.size() > 4)
            viewTransactions = viewTransactions.subList(0, 4);

        model.addAttribute("username", user.getUsername());
        model.addAttribute("transactions", viewTransactions);
        model.addAttribute("balance", String.valueOf(user.getUser().getBalance()));
        model.addAttribute("selectedTab", 1);

        return "dashboard/index";
    }
}
