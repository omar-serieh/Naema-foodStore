package com.universityproject.webapp.foodstore.entity;

import jakarta.persistence.*;
import org.springframework.boot.context.properties.bind.Name;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_verification_code")
public class EmailVerificationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "code")
    private String code;
    @Column(name = "expiry")
    private LocalDateTime expiry;

    @OneToOne
    @JoinColumn(name = "user_user_id")
    private Users user;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocalDateTime getExpiry() {
        return expiry;
    }

    public void setExpiry(LocalDateTime expiry) {
        this.expiry = expiry;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }
}

