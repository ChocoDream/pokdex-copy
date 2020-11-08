package com.example.pokeapi.services;

import com.example.pokeapi.entities.User;
import com.example.pokeapi.repositories.UserRepository;
import org.json.HTTP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> findAll(String username) {
        if (username != null) {
            var user = userRepository.findByUsername(username).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Could not found user by username %s.", username)));
            return List.of(user);
        }
        return userRepository.findAll();
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(RuntimeException::new);
    }

    public User findById(String id) {
        return userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Username not found by id: " + id));
    }

    public User save(User user) {
        if (StringUtils.isEmpty(user.getPassword())) { // user.getPassword() == null || user.getPassword().isEmpty()
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username needs a password");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public void update(String id, User user) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found by id: " + id);
        }

        var currentUser = getCurrentUser();

        if(!isUsernameAdmin(currentUser)){
            if(!currentUser.equals(user.getUsername())){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not allowed to update someone else's username");
            }
        }
        user.setId(id);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public void delete(String id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found by id: " + id);
        }

        userRepository.deleteById(id);
    }

    private String getCurrentUser() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private boolean isUsernameAdmin(String username) {
        User user = findByUsername(username);
        return user.getRoles().contains("ADMIN");
    }
}
