package com.jeopardy.io;

import com.jeopardy.core.Category;
import com.jeopardy.core.Clue;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class CsvBoardLoader implements BoardLoader {

    private final String path;

    public CsvBoardLoader(String path) {
        this.path = path;
    }

    @Override
    public List<Category> load() throws IOException {
        Map<String, Category> byCategory = new LinkedHashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String header = br.readLine();
            if (header == null) {
                throw new IOException("Empty CSV file: " + path);
            }

            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] parts = parseCsvLine(line);
                if (parts.length < 8) {
                    throw new IOException("Invalid CSV row (expected 8 columns): " + line);
                }

                String categoryName = parts[0].trim();
                int value = Integer.parseInt(parts[1].trim());
                String question = parts[2];

                Map<String,String> options = new LinkedHashMap<>();
                options.put("A", parts[3]);
                options.put("B", parts[4]);
                options.put("C", parts[5]);
                options.put("D", parts[6]);

                String correct = parts[7].trim().toUpperCase(Locale.ROOT);
                if (!Set.of("A","B","C","D").contains(correct)) {
                    throw new IOException("Invalid correct answer in CSV: " + correct);
                }

                Category cat = byCategory.computeIfAbsent(categoryName, Category::new);
                cat.addClue(new Clue(value, question, options, correct));
            }
        }

        return new ArrayList<>(byCategory.values());
    }

    /**
     * Simple CSV parser that respects double quotes.
     * It is enough for this assignment's data.
     */
    private static String[] parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        fields.add(sb.toString());
        return fields.toArray(new String[0]);
    }

    @Override
    public String getSourceDescription() {
        return "CSV: " + path;
    }
}
