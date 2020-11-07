package com.example.pokeapi.entities;

import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

public class User {
    @Id
    private String id;
    @NotEmpty(message = "Username may not be empty")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters long")
    private String username;
    @Size(min = 8, message = "Password must contain at least 8 characters")
    private String password;
    private List<String> roles;

    public User() {

    }

    public User(@NotEmpty(message = "Username may not be empty") @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters long") String username, @Size(min = 8, message = "Password must contain at least 8 characters") String password, List<String> roles) {
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
