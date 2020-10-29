package com.example.pokeapi.services;

import com.example.pokeapi.Entities.Pokemon;
import com.example.pokeapi.repositories.PokeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PokeService {
    @Autowired
    private PokeRepository pokeRepository;
    @Autowired
    private PokeConsumerService pokeConsumerService;

    public List<Pokemon> findAll(String name) {
        System.out.println("FRESH DATA...");
        var pokemon = pokeRepository.findAll();
            pokemon =   pokemon.stream()
                        .filter(poke -> poke.getName().toLowerCase().contains(name.toLowerCase()))
                        .collect(Collectors.toList());
        if (pokemon.isEmpty()) {
            var pokemonDto = pokeConsumerService.search(name);
            if (pokemonDto != null) {
                var _pokemon = new Pokemon(pokemonDto.getName(), pokemonDto.getHeight(), pokemonDto.getWeight(), pokemonDto.getGames());
                pokemon.add(this.save(_pokemon));
            }
        }

        return pokemon.stream()
                .filter(poke -> poke.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    public Pokemon findById(String id) {
        var pokemon = pokeRepository.findById(id);
        if (pokemon.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sorry, couldn't find Pokemon");
        }

        return pokemon.get();


    }

    public Pokemon save(Pokemon pokemon) {
        return pokeRepository.save(pokemon);
    }

    public void update(String id, Pokemon pokemon) {
        if (!pokeRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sorry, could not find Pokemon");
        }

        pokemon.setId(id);
        pokeRepository.save(pokemon);

    }

    public void delete(String id) {
        if (!pokeRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sorry, could not find Pokemon");
        }

        pokeRepository.deleteById(id);
    }
}


