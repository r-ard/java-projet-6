package com.paymybuddy.service;

import com.paymybuddy.entity.Contact;
import com.paymybuddy.entity.User;
import com.paymybuddy.exception.UserNotFoundException;
import com.paymybuddy.exception.contact.ContactAlreadyExistsException;
import com.paymybuddy.exception.contact.NotContactOfUserException;
import com.paymybuddy.repository.ContactRepository;
import com.paymybuddy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContactService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    public List<Contact> getContactsOfUser(User user) {
        return contactRepository.findContactsOfUser(user);
    }

    public Contact getUserContactOfUser(User user, User contactUser) throws NotContactOfUserException {
        Optional<Contact> contact = contactRepository.findUserContactOfUser(user, contactUser);

        if(!contact.isPresent()) {
            return null;
        }

        if(!contact.get().getUser().getId().equals(user.getId())) {
            throw new NotContactOfUserException(contactUser.getId());
        }

        return contact.get();
    }

    public Contact getContactOfUserById(User user, int contactId) throws NotContactOfUserException {
        Optional<Contact> contact = contactRepository.findById(contactId);

        if(!contact.isPresent()) {
            return null;
        }

        if(!contact.get().getUser().getId().equals(user.getId())) {
            throw new NotContactOfUserException(contactId);
        }

        return contact.get();
    }

    public void createContact(User user, int contactId, String name) throws ContactAlreadyExistsException, UserNotFoundException {
        Optional<User> contactUser = userRepository.findById(contactId);

        if(!contactUser.isPresent()) {
            throw new UserNotFoundException(contactId);
        }

        List<Contact> existingContacts = contactRepository.findContactsOfUser(user);

        for(Contact contact : existingContacts) {
            if(contact.getContact().getId() == contactId) {
                throw new ContactAlreadyExistsException(contactId);
            }
        }

        String contactName = (name == null || name.trim().isEmpty()) ? contactUser.get().getUsername() : name;

        Contact contact = new Contact();
        contact.setName(contactName);
        contact.setUser(user);
        contact.setContact(contactUser.get());
        contactRepository.save(contact);
    }

    public void deleteContact(User user, int contactId) throws UserNotFoundException, NotContactOfUserException {
        Optional<Contact> contact = contactRepository.findById(contactId);

        if(!contact.isPresent()) {
            throw new UserNotFoundException(contactId);
        }

        if(!contact.get().getUser().getId().equals(user.getId())) {
            throw new NotContactOfUserException(contactId);
        }

        contactRepository.delete(contact.get());
    }
}
