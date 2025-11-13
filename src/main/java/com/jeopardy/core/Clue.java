package com.jeopardy.core;

import java.util.LinkedHashMap;
import java.util.Map;

public class Clue {
    private final int value;
    private final String questionText;
    private final Map<String, String> options; // A,B,C,D -> text
    private final String correctAnswer; // "A".."D"
    private boolean used = false;

    public Clue(int value, String questionText, Map<String, String> options, String correctAnswer) {
        this.value = value;
        this.questionText = questionText;
        this.options = new LinkedHashMap<>(options);
        this.correctAnswer = correctAnswer;
    }

    public int getValue() { return value; }
    public String getQuestionText() { return questionText; }
    public Map<String, String> getOptions() { return options; }
    public String getCorrectAnswer() { return correctAnswer; }

    public boolean isUsed() { return used; }
    public void markUsed() { this.used = true; }
}
