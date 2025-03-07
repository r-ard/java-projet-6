package com.paymybuddy.service;

import com.paymybuddy.entity.Contact;
import com.paymybuddy.entity.User;
import com.paymybuddy.exception.UserNotFoundException;
import com.paymybuddy.exception.contact.ContactAlreadyExistsException;
import com.paymybuddy.exception.contact.NotContactOfUserException;
import com.paymybuddy.repository.ContactRepository;
import com.paymybuddy.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class ContactServiceTest {
    @Mock
    ContactRepository contactRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    ContactService contactService;

    @Test
    public void testGetContactsOfUser() {
        User user = new User();
        user.setId(1);

        User contactOfUser = new User();
        user.setId(2);

        Contact contact = new Contact(user, contactOfUser);

        List<Contact> contacts = new ArrayList<>();
        contacts.add(contact);

        when(contactRepository.findContactsOfUser(user)).thenReturn(contacts);

        List<Contact> result = contactService.getContactsOfUser(user);
        assertEquals(1, result.size());
        verify(contactRepository).findContactsOfUser(user);
    }

    @Test
    public void testGetUserContactOfUserNotFound() throws NotContactOfUserException {
        User user = new User();
        user.setId(1);

        User contactUser = new User();
        contactUser.setId(2);

        when(contactRepository.findUserContactOfUser(user, contactUser)).thenReturn(Optional.empty());

        Contact result = contactService.getUserContactOfUser(user, contactUser);
        assertNull(result);
    }

    @Test
    public void testGetUserContactOfUserNotMatchingUser() {
        User user = new User();
        user.setId(1);

        User contactUser = new User();
        contactUser.setId(2);

        User wrongUser = new User();
        wrongUser.setId(99);

        Contact contact = new Contact(wrongUser, contactUser);

        when(contactRepository.findUserContactOfUser(user, contactUser)).thenReturn(Optional.of(contact));

        assertThrows(NotContactOfUserException.class, () -> {
            contactService.getUserContactOfUser(user, contactUser);
        });
    }

    @Test
    public void testGetUserContactOfUserSuccess() throws NotContactOfUserException {
        User user = new User();
        user.setId(1);

        User contactUser = new User();
        contactUser.setId(2);

        Contact contact = new Contact();
        contact.setUser(user);

        when(contactRepository.findUserContactOfUser(user, contactUser)).thenReturn(Optional.of(contact));

        Contact result = contactService.getUserContactOfUser(user, contactUser);
        assertNotNull(result);
        assertEquals(user.getId(), result.getUser().getId());
    }

    @Test
    public void testGetContactOfUserByIdNotFound() throws NotContactOfUserException {
        User user = new User();
        user.setId(1);

        int contactId = 10;
        when(contactRepository.findById(contactId)).thenReturn(Optional.empty());

        Contact result = contactService.getContactOfUserById(user, contactId);
        assertNull(result);
    }

    @Test
    public void testGetContactOfUserByIdNotMatchingUser() {
        User user = new User();
        user.setId(1);

        User contactUser = new User();
        contactUser.setId(2);

        int fakeContactId = 10;
        User wrongUser = new User();
        wrongUser.setId(99);

        Contact contact = new Contact(wrongUser, contactUser);

        when(contactRepository.findById(fakeContactId)).thenReturn(Optional.of(contact));

        assertThrows(NotContactOfUserException.class, () -> {
            contactService.getContactOfUserById(user, fakeContactId);
        });
    }

    @Test
    public void testGetContactOfUserByIdSuccess() throws NotContactOfUserException {
        User user = new User();
        user.setId(1);
        int contactId = 10;
        Contact contact = new Contact();
        contact.setUser(user);

        when(contactRepository.findById(contactId)).thenReturn(Optional.of(contact));

        Contact result = contactService.getContactOfUserById(user, contactId);
        assertNotNull(result);
        assertEquals(user.getId(), result.getUser().getId());
    }

    @Test
    public void testCreateContactUserNotFound() {
        User user = new User();
        user.setId(1);
        int contactId = 2;
        when(userRepository.findById(contactId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            contactService.createContact(user, contactId, "Test");
        });
    }

    @Test
    public void testCreateContactContactAlreadyExists() {
        User user = new User();
        user.setId(1);

        int contactId = 2;
        User contactUser = new User();
        contactUser.setId(contactId);
        contactUser.setUsername("ContactUser");

        when(userRepository.findById(contactId)).thenReturn(Optional.of(contactUser));

        Contact existingContact = new Contact(user, contactUser);

        List<Contact> userContacts = new ArrayList<>();
        userContacts.add(existingContact);

        when(contactRepository.findContactsOfUser(user)).thenReturn(userContacts);

        assertThrows(ContactAlreadyExistsException.class, () -> {
            contactService.createContact(user, contactId, "Test");
        });
    }

    @Test
    public void testCreateContactWithNameProvided() throws Exception {
        User user = new User();
        user.setId(1);
        int contactId = 2;
        User contactUser = new User();
        contactUser.setId(contactId);
        contactUser.setUsername("ContactUser");

        when(userRepository.findById(contactId)).thenReturn(Optional.of(contactUser));
        when(contactRepository.findContactsOfUser(user)).thenReturn(Collections.emptyList());

        contactService.createContact(user, contactId, "CustomName");

        verify(contactRepository).save(argThat(contact ->
                "CustomName".equals(contact.getName())
                        && contact.getUser().getId() == user.getId()
                        && contact.getContact().getId() == contactId
        ));
    }

    @Test
    public void testCreateContactWithEmptyName() throws Exception {
        User user = new User();
        user.setId(1);
        int contactId = 2;
        User contactUser = new User();
        contactUser.setId(contactId);
        contactUser.setUsername("Test");

        when(userRepository.findById(contactId)).thenReturn(Optional.of(contactUser));
        when(contactRepository.findContactsOfUser(user)).thenReturn(Collections.emptyList());

        contactService.createContact(user, contactId, "   ");

        verify(contactRepository).save(argThat(contact ->
                contact.getName().equals("Test")
                        && contact.getUser().getId().equals(user.getId())
                        && contact.getContact().getId().equals(contactId)
        ));
    }

    @Test
    public void testDeleteContactContactNotFound() {
        User user = new User();
        user.setId(1);
        int contactId = 10;
        when(contactRepository.findById(contactId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            contactService.deleteContact(user, contactId);
        });
    }

    @Test
    public void testDeleteContactNotMatchingUser() {
        User user = new User();
        user.setId(1);
        int contactId = 10;
        Contact contact = new Contact();
        User wrongUser = new User();
        wrongUser.setId(99);
        contact.setUser(wrongUser);

        when(contactRepository.findById(contactId)).thenReturn(Optional.of(contact));

        assertThrows(NotContactOfUserException.class, () -> {
            contactService.deleteContact(user, contactId);
        });
    }

    @Test
    public void testDeleteContactSuccess() throws Exception {
        User user = new User();
        user.setId(1);

        int contactId = 10;
        Contact contact = new Contact();
        contact.setUser(user);

        when(contactRepository.findById(contactId)).thenReturn(Optional.of(contact));

        contactService.deleteContact(user, contactId);

        verify(contactRepository).delete(contact);
    }
}
