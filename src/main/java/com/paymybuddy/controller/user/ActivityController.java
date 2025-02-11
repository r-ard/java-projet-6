package com.paymybuddy.controller.user;

import com.paymybuddy.dto.UserTransactionDTO;
import com.paymybuddy.entity.Transaction;
import com.paymybuddy.security.CustomUserDetails;
import com.paymybuddy.service.TransactionService;
import com.paymybuddy.utils.AuthenticationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ActivityController {
    @Autowired
    private TransactionService transactionService;

    @GetMapping("/dashboard/activity")
    public String showActivity(Model model) {
        CustomUserDetails user = (CustomUserDetails) AuthenticationUtils.getAuthenticatedUser();
        List<UserTransactionDTO> viewTransactions = transactionService.getViewUserTransactions(
                user.getUser(),
                -1
        );

        model.addAttribute("username", user.getUsername());
        model.addAttribute("transactions", viewTransactions);
        model.addAttribute("selectedTab", 2);

        return "dashboard/activity";
    }
}
