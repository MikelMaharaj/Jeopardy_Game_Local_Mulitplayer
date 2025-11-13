package com.jeopardy.core;

public class ScoreService {
    public void applyAnswer(Player player, Clue clue, String answer) {
        if (answer != null && answer.equalsIgnoreCase(clue.getCorrectAnswer())) {
            player.addScore(clue.getValue());
        } else {
            player.addScore(-clue.getValue());
        }
        clue.markUsed();
    }
}
