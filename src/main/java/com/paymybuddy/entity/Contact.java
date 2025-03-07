package com.paymybuddy.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "contacts")
@EntityListeners(AuditingEntityListener.class)
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user", referencedColumnName="id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "contact", referencedColumnName="id", nullable = false)
    private User contact;

    @Column(length = 128, nullable = false)
    private String name;

    @CreatedDate
    @Column(name="created_at")
    private LocalDateTime createdAt;

    public Contact() {

    }

    public Contact(User user, User contact, String name) {
        this.user = user;
        this.contact = contact;
        this.name = name;
    }

    public Contact(User user, User contact) {
        this(user, contact, null);
    }

    public Integer getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getContact() {
        return contact;
    }

    public void setContact(User contact) {
        this.contact = contact;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
