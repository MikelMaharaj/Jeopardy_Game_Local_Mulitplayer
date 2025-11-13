package com.jeopardy.core;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GameState {
    private final String caseId = UUID.randomUUID().toString();
    private List<Player> players = new ArrayList<>();
    private List<Category> categories = new ArrayList<>();
    private int currentPlayerIndex = 0;
    private boolean started = false;
    private boolean finished = false;

    public String getCaseId() { return caseId; }
    public List<Player> getPlayers() { return players; }
    public List<Category> getCategories() { return categories; }
    public int getCurrentPlayerIndex() { return currentPlayerIndex; }
    public boolean isStarted() { return started; }
    public boolean isFinished() { return finished; }

    public void setPlayers(List<Player> players) { this.players = players; }
    public void setCategories(List<Category> categories) { this.categories = categories; }

    public Player getCurrentPlayer() {
        if (players.isEmpty()) return null;
        return players.get(currentPlayerIndex);
    }

    public void nextPlayer() {
        if (players.isEmpty()) return;
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    public void start() { started = true; }
    public void finish() { finished = true; }
}
