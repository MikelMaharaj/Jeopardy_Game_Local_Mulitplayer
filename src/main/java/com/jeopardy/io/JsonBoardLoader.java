package com.jeopardy.io;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeopardy.core.Category;
import com.jeopardy.core.Clue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class JsonBoardLoader implements BoardLoader {
    private final String path;

    public JsonBoardLoader(String path) {
        this.path = path;
    }

    @Override
    public List<Category> load() throws IOException {
        // Read whole JSON file
        String data = Files.readString(Path.of(path));
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(data);

        if (!root.isArray()) {
            throw new IOException("JSON root must be an array of question objects.");
        }

        Map<String, Category> byCategory = new LinkedHashMap<>();

        for (JsonNode node : root) {
            if (node == null || node.isNull()) continue;

            String categoryName = node.path("Category").asText("Unknown");
            int value = node.path("Value").asInt(0);
            String question = node.path("Question").asText("");

            // Options are nested under "Options": { "A": "...", "B": "...", ... }
            JsonNode optsNode = node.path("Options");
            Map<String,String> options = new LinkedHashMap<>();
            options.put("A", optsNode.path("A").asText(""));
            options.put("B", optsNode.path("B").asText(""));
            options.put("C", optsNode.path("C").asText(""));
            options.put("D", optsNode.path("D").asText(""));

            String correct = node.path("CorrectAnswer").asText("").trim().toUpperCase(Locale.ROOT);
            if (!Set.of("A","B","C","D").contains(correct)) {
                throw new IOException("Invalid correct answer: " + correct);
            }

            Category cat = byCategory.computeIfAbsent(categoryName, Category::new);
            cat.addClue(new Clue(value, question, options, correct));
        }

        return new ArrayList<>(byCategory.values());
    }

    @Override
    public String getSourceDescription() {
        return "JSON: " + path;
    }
}
