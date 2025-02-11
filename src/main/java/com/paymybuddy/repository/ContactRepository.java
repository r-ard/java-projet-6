package com.paymybuddy.repository;

import com.paymybuddy.entity.Contact;
import com.paymybuddy.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
    @Query("SELECT c FROM Contact c WHERE c.user = :user")
    List<Contact> findContactsOfUser(@Param("user")User user);

    @Query("SELECT c FROM Contact c WHERE c.user = :user AND c.contact = :contact")
    Optional<Contact> findUserContactOfUser(@Param("user")User user, @Param("contact")User contact);
}
