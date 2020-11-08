package com.example.pokeapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Map;

public class PokemonDto {
    @JsonProperty("name")
    private String name;
    @JsonProperty("height")
    private int height;
    @JsonProperty("weight")
    private int weight;
    @JsonProperty("game_indices")
    private Object[] games;

    public PokemonDto() {
    }

    public PokemonDto(String name, int height, int weight, Object[] games) {
        this.name = name;
        this.height = height;
        this.weight = weight;
        this.games = games;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public ArrayList<String> getGames() {
        ArrayList<String> parsedGames = new ArrayList<>();
        for (Object baseObject : games) {
            Map<String, String> object = (Map<String, String>) baseObject;
            Map<String, String> childObject = (Map<String, String>) object.values().toArray()[1];
            if (!childObject.get("name").isEmpty()) {
                parsedGames.add(childObject.get("name"));
            }
        }
        return parsedGames;
    }

    public void setGames(Object[] games) {
        this.games = games;
    }
}

