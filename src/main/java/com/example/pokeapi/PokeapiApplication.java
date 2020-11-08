package com.example.pokeapi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(
        title = "Pokedex API",
        version = "1.0",
        description = "Micro Pokedex service",
        contact = @Contact(
                url = "https://github.com/ChocoDream",
                name = "Daniel Hansson"
        )))
public class PokeapiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PokeapiApplication.class, args);
    }

}
