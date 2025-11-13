package com.jeopardy.core;

public class Player {
    private final String id;
    private final String name;
    private int score = 0;

    public Player(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getScore() { return score; }

    public void addScore(int delta) { this.score += delta; }
}
