package com.example.pokeapi.services;

import com.example.pokeapi.dto.PokemonDto;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Service
@ConfigurationProperties(prefix = "example.pokeapi", ignoreInvalidFields = false)

public class PokeConsumerService {

    private final RestTemplate restTemplate;
    private String url;

    public PokeConsumerService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public PokemonDto search (String name){
        var urlWithNameQuery = url + "pokemon/" + name;
        var pokemon = restTemplate.getForObject(urlWithNameQuery, PokemonDto.class);
        if (pokemon == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No Pokemon found");
        }
        return pokemon;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
