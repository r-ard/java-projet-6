package com.paymybuddy.controller.user;

import com.paymybuddy.dto.CreateUserContactDTO;
import com.paymybuddy.dto.DeleteUserContactDTO;
import com.paymybuddy.dto.UserContactDTO;
import com.paymybuddy.dto.UserRegistrationDTO;
import com.paymybuddy.entity.Contact;
import com.paymybuddy.entity.User;
import com.paymybuddy.exception.UserNotFoundException;
import com.paymybuddy.exception.contact.ContactAlreadyExistsException;
import com.paymybuddy.exception.contact.NotContactOfUserException;
import com.paymybuddy.security.CustomUserDetails;
import com.paymybuddy.service.ContactService;
import com.paymybuddy.service.UserService;
import com.paymybuddy.utils.AuthenticationUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ContactController {
    @Autowired
    private ContactService contactService;

    @Autowired
    private UserService userService;

    @GetMapping("/dashboard/contacts")
    public String showContacts(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "success", required = false) String success,
            Model model
    ) {
        CustomUserDetails user = AuthenticationUtils.getAuthenticatedUser();
        List<Contact> contacts = contactService.getContactsOfUser(user.getUser());

        List<UserContactDTO> viewContacts = new ArrayList<>();

        for(Contact contact : contacts) {
            viewContacts.add(new UserContactDTO(contact.getName(), contact.getContact().getEmail()));
        }

        model.addAttribute("username", user.getUsername());
        model.addAttribute("contacts", viewContacts);
        model.addAttribute("selectedTab", 3);

        if(error != null) {
            model.addAttribute("errorMessage", error.length() > 0 ? error : "Veuillez bien remplir tous les champs");
        }

        if(success != null) {
            model.addAttribute("successMessage", success.length() > 0 ? success : "Le contacte a été ajouté");
        }

        return "dashboard/contacts";
    }

    @DeleteMapping("/dashboard/contact")
    public String deleteContact(
            @Valid @ModelAttribute("contact") DeleteUserContactDTO contactDto,
            BindingResult result,
            Model model
    ) {
        CustomUserDetails user = AuthenticationUtils.getAuthenticatedUser();

        if(result.hasErrors()) {
            return "redirect:/dashboard/contacts?error=" + result.getFieldError().getDefaultMessage();
        }

        try {
            contactService.deleteContact(user.getUser(), contactDto.getContactId());
        }
        catch(UserNotFoundException ex) {
            return "redirect:/dashboard/contacts?error=contact not found";
        }
        catch(NotContactOfUserException ex) {
            return "redirect:/dashboard/contacts?error=contact not found";
        }

        return "redirect:/dashboard/contacts?deleted";
    }

    @PostMapping("/dashboard/contacts")
    public String createContact(
            @Valid @ModelAttribute("contact") CreateUserContactDTO contactDto,
            BindingResult result,
            Model model
    ) {
        CustomUserDetails user = AuthenticationUtils.getAuthenticatedUser();

        if(result.hasErrors()) {
            return "redirect:/dashboard/contacts?error=" + result.getFieldError().getDefaultMessage();
        }

        User matchingContactUser = userService.getByEmail(contactDto.getEmail());

        // Handle if user doesn't exist
        if(matchingContactUser == null) {
            return "redirect:/dashboard/contacts?error=contact user not found";
        }

        // Handle self contact
        if(matchingContactUser.getId() == user.getId()) {
            return "redirect:/dashboard/contacts?error=contact can not be yourself";
        }

        try {
            contactService.createContact(user.getUser(), matchingContactUser.getId(), contactDto.getName());
        }
        catch(UserNotFoundException ex) {
            return "redirect:/dashboard/contacts?error=contact user not found";
        }
        catch(ContactAlreadyExistsException ex) {
            return "redirect:/dashboard/contacts?error=contact already exists";
        }

        return "redirect:/dashboard/contacts?success";
    }
}
