package com.paymybuddy.controller;

import com.paymybuddy.utils.AuthenticationUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LandingPageController {
    @GetMapping("/")
    public String showHome(Model model) {
        model.addAttribute("authenticated", AuthenticationUtils.isClientAuthenticated());
        return "landing-page/index";
    }
}
