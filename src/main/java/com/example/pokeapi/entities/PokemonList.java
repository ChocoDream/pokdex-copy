package com.example.pokeapi.entities;

import org.springframework.data.annotation.Id;

public class PokemonList {
    @Id
    private String id;
    private Object[] name;

    public PokemonList() {

    }

    public PokemonList(Object[] name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Object[] getName() {
        return name;
    }

    public void setName(Object[] name) {
        this.name = name;
    }
}
