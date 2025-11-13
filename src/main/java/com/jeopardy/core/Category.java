package com.jeopardy.core;

import java.util.ArrayList;
import java.util.List;

public class Category {
    private final String name;
    private final List<Clue> clues = new ArrayList<>();

    public Category(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public List<Clue> getClues() { return clues; }

    public void addClue(Clue clue) { clues.add(clue); }
}
