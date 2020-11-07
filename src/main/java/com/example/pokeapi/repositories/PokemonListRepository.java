package com.example.pokeapi.repositories;

import com.example.pokeapi.entities.PokemonList;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface PokemonListRepository extends MongoRepository<PokemonList, String> {
    ArrayList<String> findByNames(String names);
}
