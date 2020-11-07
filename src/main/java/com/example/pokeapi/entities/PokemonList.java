package com.example.pokeapi.entities;

import org.springframework.data.annotation.Id;

import java.util.ArrayList;

public class PokemonList {
    @Id
    private String id;
    private ArrayList<String> names;

    public PokemonList() {

    }

    public PokemonList(ArrayList<String> names) {
        this.names = names;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<String> getNames() {
        return names;
    }

    public void setNames(ArrayList<String> names) {
        this.names = names;
    }
}
