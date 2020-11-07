package com.example.pokeapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

public class PokemonListDto {

    @JsonProperty("results")
    private Object[] entities;


    public PokemonListDto() {

    }

    public PokemonListDto(Object[] entities) {
        this.entities = entities;
    }

    public ArrayList<String> getEntities() {
        ArrayList<String> names = new ArrayList<>();
        for(Object entity : entities) {
            HashMap<String, String> object = (HashMap<String, String>) entity;
            String name = object.get("name");
            if(name != null && !name.equals("")){
                names.add(name);
            }
        }

        Collections.sort(names);
        return names;
    }

    public void setEntities(Object[] entities) {
        this.entities = entities;
    }
}
