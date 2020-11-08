package com.example.pokeapi.controllers;

import com.example.pokeapi.entities.Pokemon;
import com.example.pokeapi.services.PokeConsumerService;
import com.example.pokeapi.services.PokeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
import java.util.Map;

@RestController
@RequestMapping("/api/v1/pokemon")
public class PokeController {
    @Autowired
    private PokeService pokeService;
    @Autowired
    private PokeConsumerService pokeConsumerService;

    @Operation(summary = "Find pokemon using query. Allow partial name search",
            tags = {"Pokemon"},
            description = "Returns a list of pokemon depending on query argument. If no argument found, returns whole list of pokemon saved in Database. No Auth required")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found Pokemon"),
            @ApiResponse(responseCode = "400", description = "A name search must contain more than 3 characters"),
            @ApiResponse(responseCode = "400", description = "Pokemonlist couldn't be populated (Used in partial string search)"),
            @ApiResponse(responseCode = "404", description = "Could not find pokemon")
    })
    @GetMapping
    public ResponseEntity<List<Pokemon>> findPokemon(
            @Parameter(description = "QUERIES ALLOWED: name, minWeight, maxWeight, minHeight, maxHeight")
            @RequestParam Map<String, String> query) {
        var pokemon = pokeService.findAll(query);
        return ResponseEntity.ok(pokemon);
    }

    @Operation(summary = "Finds a pokemon by id. No Auth required",
            tags = {"Pokemon"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the Pokemon",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Pokemon.class))}),
            @ApiResponse(responseCode = "404", description = "Pokemon not found by id")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Pokemon> findPokemonById(@Parameter(description = "id of Pokemon to be searched") @PathVariable String id) {
        return ResponseEntity.ok(pokeService.findById(id));
    }

    @Operation(summary = "Adds a custom pokemon to database",
            tags = {"Pokemon"},
            description = "User, Admin restricted")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created new Pokemon"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
    })
    @SecurityRequirement(name = "basic")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public ResponseEntity<Pokemon> savePokemon(@RequestBody Pokemon pokemon) {
        var savedPokemon = pokeService.save(pokemon);
        var uri = URI.create("/api/v1/pokemon" + savedPokemon.getId());
        return ResponseEntity.created(uri).body(savedPokemon);
    }

    @Operation(summary = "Updates pokemon from database by id",
            tags = {"Pokemon"},
            description = "Admin restricted")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Pokemon has been updated"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Pokemon not found by id")
    })
    @SecurityRequirement(name = "basic")
    @PutMapping("/{id}")
    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePokemon(@PathVariable String id, @RequestBody() Pokemon pokemon) {
        pokeService.update(id, pokemon);
    }

    @Operation(summary = "Delete pokemon from database by id",
            tags = {"Pokemon"},
            description = "Admin restricted")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Pokemon has been deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Pokemon not found by id")
    })
    @SecurityRequirement(name = "basic")
    @DeleteMapping("/{id}")
    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePokemon(@PathVariable String id) {
        pokeService.delete(id);
    }

    @Operation(summary = "Delete all pokemon in Database",
            tags = {"Pokemon"},
            description = "Admin restricted")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "All Pokemon in Database has been deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
    })
    @SecurityRequirement(name = "basic")
    @DeleteMapping("/all")
    @Secured("ROLE_ADMIN")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllPokemon() {
        pokeService.deleteAllEntries();
    }
}