package com.example.pokeapi.entities;

import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.List;

public class Pokemon implements Serializable {
    @Id
    private String id;
    private String name;
    private int height;
    private int weight;
    private List<Object> games;


    public Pokemon() {

    }

    public Pokemon(String name, int height, int weight, List<Object> games) {
        this.name = name;
        this.height = height;
        this.weight = weight;
        this.games = games;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public List<Object> getGames() {
        return games;
    }

    public void setGames(List<Object> games) {
        this.games = games;
    }

}
