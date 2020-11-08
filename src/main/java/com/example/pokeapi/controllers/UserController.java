package com.example.pokeapi.controllers;

import com.example.pokeapi.entities.User;
import com.example.pokeapi.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    @Autowired
    UserService userService;

    @Operation(
            summary = "Search for user. No argument returns full list of Users",
            tags = {"User"},
            description = "User, Admin restricted"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User has been found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found by name")
    })
    @SecurityRequirement(name = "basic")
    @GetMapping
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<List<User>> getUser(@RequestParam(required = false) String name) {
        var user = userService.findAll(name);
        return ResponseEntity.ok(user);
    }

    @Operation(
            summary = "Find User by id",
            tags = {"User"},
            description = "Admin restricted"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found by id"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found by id")
    })
    @SecurityRequirement(name = "basic")
    @GetMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<User> findUserById(@PathVariable String id) {
        var user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    @Operation(
            summary = "Creates a new User",
            tags = {"User"},
            description = "Admin restricted"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "New User has been created"),
            @ApiResponse(responseCode = "400", description = "Requires JSON request"),
            @ApiResponse(responseCode = "400", description = "User needs a password"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Username must be between 3 and 20 characters long"),
            @ApiResponse(responseCode = "500", description = "Password must contain at least 8 characters")
    })
    @SecurityRequirement(name = "basic")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured("ROLE_ADMIN")
    public ResponseEntity<User> saveUser(@RequestBody User user) {
        var savedUser = userService.save(user);
        var uri = URI.create("/api/v1/user" + savedUser.getId());
        return ResponseEntity.created(uri).body(savedUser);
    }

    @Operation(
            summary = "Updates an existing User",
            tags = {"User"},
            description = "User (Only allowed to modify your own), Admin restricted"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User has been updated"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden to update another User's details (unless you're Admin)"),
            @ApiResponse(responseCode = "404", description = "User not found by id")
    })
    @SecurityRequirement(name = "basic")
    @PutMapping("/{id}")
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateUser(@PathVariable String id, @RequestBody User user) {
        userService.update(id, user);
    }

    @Operation(
            summary = "Deletes an existing User",
            tags = {"User"},
            description = "Admin restricted"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User has been deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found by id")
    })
    @SecurityRequirement(name = "basic")
    @DeleteMapping("/{id}")
    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable String id) {
        userService.delete(id);
    }
}
