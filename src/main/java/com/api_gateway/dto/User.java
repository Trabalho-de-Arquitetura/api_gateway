package com.api_gateway.dto;

import com.api_gateway.dto.type.UserRole;

import java.util.UUID;

public class User {
    private UUID id;
    private String name;
    private String email;
    private String password;
    private String affiliatedSchool;
    private UserRole role;

    public User() {}
    public User(UUID id, String name, String email, String password, String affiliatedSchool, UserRole role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.affiliatedSchool = affiliatedSchool;
        this.role = role;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAffiliatedSchool() {
        return affiliatedSchool;
    }

    public void setAffiliatedSchool(String affiliatedSchool) {
        this.affiliatedSchool = affiliatedSchool;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}
