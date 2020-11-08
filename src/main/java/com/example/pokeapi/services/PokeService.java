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


    @Cacheable(value = "pokemonCache")
    public List<Pokemon> findAll(Map<String, String> query) {
        System.out.println("FRESH DATA...");
        var pokemon = pokemonRepository.findAll();

        if (query.containsKey("name")) {
            pokemon = findByName(pokemon, query.get("name"));
        }

        if (query.containsKey("minWeight")) {
            pokemon = findPokemonByMinWeight(pokemon, Integer.parseInt(query.get("minWeight")));
        }

        if (query.containsKey("maxWeight")) {
            pokemon = findPokemonByMaxWeight(pokemon, Integer.parseInt(query.get("maxWeight")));
        }

        if (query.containsKey("minHeight")) {
            pokemon = findPokemonByMinHeight(pokemon, Integer.parseInt(query.get("minHeight")));
        }

        if (query.containsKey("maxHeight")) {
            pokemon = findPokemonByMaxHeight(pokemon, Integer.parseInt(query.get("maxHeight")));
        }

        if (pokemon.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No Pokemon found");
        }
        return pokemon;
    }

    @Cacheable(value = "pokemonCache", key = "#name")
    public List<Pokemon> findByName(List<Pokemon> pokemon, String name) {
        if (name.length() < 3) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A name search must contain more than 3 characters");
        }
        pokemon = pokemon.stream()
                .filter(poke -> poke.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
        if (pokemon.isEmpty()) {
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
        return pokemonRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Couldn't find Pokemon by id: " + id));
    }

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

    @CacheEvict(value = "pokemonCache", allEntries = true)
    public void deleteAllEntries() {
        if (pokemonRepository.findAll().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "MongoDB Collection is empty");
        }

        pokemonRepository.deleteAll();
    }

    @Cacheable(value = "pokemonNamesCache", key = "#name")
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

    public List<Pokemon> findPokemonByMinWeight(List<Pokemon> pokemon, int minWeight) {
        return pokemon.stream()
                .filter(poke -> poke.getWeight() >= minWeight)
                .collect(Collectors.toList());
    }

    public List<Pokemon> findPokemonByMaxWeight(List<Pokemon> pokemon, int maxWeight) {
        return pokemon.stream()
                .filter(poke -> poke.getWeight() <= maxWeight)
                .collect(Collectors.toList());
    }

    public List<Pokemon> findPokemonByMinHeight(List<Pokemon> pokemon, int minHeight) {
        return pokemon.stream()
                .filter(poke -> poke.getHeight() >= minHeight)
                .collect(Collectors.toList());
    }

    public List<Pokemon> findPokemonByMaxHeight(List<Pokemon> pokemon, int maxHeight) {
        return pokemon.stream()
                .filter(poke -> poke.getHeight() <= maxHeight)
                .collect(Collectors.toList());
    }

}


