package com.example.pokeapi.services;

import com.example.pokeapi.entities.Pokemon;
import com.example.pokeapi.entities.PokemonList;
import com.example.pokeapi.repositories.PokemonListRepository;
import com.example.pokeapi.repositories.PokemonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PokeService {
    @Autowired
    private PokemonRepository pokemonRepository;
    @Autowired
    private PokemonListRepository pokemonListRepository;
    @Autowired
    private PokeConsumerService pokeConsumerService;

    public List<Pokemon> findAll(Map<String, String> query) {
        List<Pokemon> pokemons;
        if (query.containsKey("name")) {
            System.out.println("QUERY FOUND: name");
            pokemons = findByName(query.get("name"));
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Couldn't find any valid queries");
        }

        return pokemons;
    }

    @Cacheable(value = "pokemonCache", key = "#name")
    public List<Pokemon> findByName(String name) {
        if (name.length() < 3) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A name search must contain 3 characters");
        }

        System.out.println("FRESH DATA...");
        var pokemon = pokemonRepository.findAll();
        pokemon = pokemon.stream()
                .filter(poke -> poke.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
        if (pokemon.isEmpty()) {
            /*var pokemonDto = pokeConsumerService.search(name);
            if (pokemonDto != null) {
                var _pokemon = new Pokemon(pokemonDto.getName(), pokemonDto.getHeight(), pokemonDto.getWeight(), pokemonDto.getGames());
                pokemon.add(this.save(_pokemon));
            } OLD WAY TO FIND ONE*/
            var list = this.findPokemonByPartialString(name); //Implemented the way to find several pokemon
            for (var pokemonName : list) {
                var pokemonDto = pokeConsumerService.search(pokemonName);
                if (pokemonDto != null) {
                    var _pokemon = new Pokemon(pokemonDto.getName(), pokemonDto.getHeight(), pokemonDto.getWeight(), pokemonDto.getGames());
                    pokemon.add(this.save(_pokemon));
                }
            }
        }

        return pokemon.stream()
                .filter(poke -> poke.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }


    @Cacheable(value = "pokemonCache", key = "#id")
    public Pokemon findById(String id) {
        var pokemon = pokemonRepository.findById(id);
        if (pokemon.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sorry, couldn't find Pokemon");
        }

        return pokemon.get();
    }

    //@CacheEvict(value = "pokeCache", allEntries = true)
    @CachePut(value = "pokemonCache", key = "#result.id")
    public Pokemon save(Pokemon pokemon) {
        return pokemonRepository.save(pokemon);
    }

    @CachePut(value = "pokemonCache", key = "#id")
    public void update(String id, Pokemon pokemon) {
        if (!pokemonRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sorry, could not find Pokemon");
        }

        pokemon.setId(id);
        pokemonRepository.save(pokemon);
    }

    @CacheEvict(value = "pokemonCache", allEntries = true)
    public void delete(String id) {
        if (!pokemonRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sorry, could not find Pokemon");
        }

        pokemonRepository.deleteById(id);
    }

    public PokemonList save(PokemonList pokemonList) {
        return pokemonListRepository.save(pokemonList);
    }

    public ArrayList<String> findPokemonByPartialString(String name) {
        ArrayList<String> pokemonList = pokemonListRepository.findByNames("names"); //Doesn't work, HOW!!!
        ArrayList<String> listOfPokemon = new ArrayList<>();
        if (pokemonList.isEmpty()) {
            System.out.println("HARVESTING NEW LIST...");
            var pokemonListDto = pokeConsumerService.getList();
            var list = new PokemonList(pokemonListDto.getEntities());
            //this.save(list);
            pokemonList = list.getNames();
        }
        for (String pokemon : pokemonList) {
            if (pokemon.contains(name)) {
                listOfPokemon.add(pokemon);
            }
        }

        if (listOfPokemon.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Couldn't find pokemon by name: " + name);
        }
        return listOfPokemon;
    }

    
}


