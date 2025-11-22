package com.jeopardy.io;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.jeopardy.core.Category;
import com.jeopardy.core.Clue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class XmlBoardLoader implements BoardLoader {
    private final String path;

    public XmlBoardLoader(String path) {
        this.path = path;
    }

    // Root tag: <JeopardyQuestions>
    public static class JeopardyQuestions {
        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "QuestionItem")
        public List<QuestionItem> questionItems;
    }

    // Each <QuestionItem> ... </QuestionItem>
    public static class QuestionItem {
        @JacksonXmlProperty(localName = "Category")
        public String category;

        @JacksonXmlProperty(localName = "Value")
        public int value;

        @JacksonXmlProperty(localName = "QuestionText")
        public String questionText;

        @JacksonXmlProperty(localName = "Options")
        public Options options;

        @JacksonXmlProperty(localName = "CorrectAnswer")
        public String correctAnswer;
    }

    // <Options><OptionA>...</OptionA>...</Options>
    public static class Options {
        @JacksonXmlProperty(localName = "OptionA")
        public String optionA;
        @JacksonXmlProperty(localName = "OptionB")
        public String optionB;
        @JacksonXmlProperty(localName = "OptionC")
        public String optionC;
        @JacksonXmlProperty(localName = "OptionD")
        public String optionD;
    }

    @Override
    public List<Category> load() throws IOException {
        byte[] data = Files.readAllBytes(Path.of(path));
        XmlMapper mapper = new XmlMapper();
        mapper.setDefaultUseWrapper(false); // ensures direct list mapping
        JeopardyQuestions root = mapper.readValue(data, JeopardyQuestions.class);

        if (root == null || root.questionItems == null) {
            throw new IOException("Invalid XML: missing QuestionItem elements");
        }

        Map<String, Category> byCategory = new LinkedHashMap<>();
        for (QuestionItem qi : root.questionItems) {
            if (qi == null) continue;
            String categoryName = qi.category == null ? "Unknown" : qi.category;
            Category cat = byCategory.computeIfAbsent(categoryName, Category::new);

            Map<String, String> options = new LinkedHashMap<>();
            if (qi.options != null) {
                options.put("A", qi.options.optionA);
                options.put("B", qi.options.optionB);
                options.put("C", qi.options.optionC);
                options.put("D", qi.options.optionD);
            }

            String correct = qi.correctAnswer == null ? "" : qi.correctAnswer.trim().toUpperCase();
            if (!Set.of("A", "B", "C", "D").contains(correct)) {
                throw new IOException("Invalid correct answer: " + correct);
            }

            cat.addClue(new Clue(qi.value, qi.questionText, options, correct));
        }

        return new ArrayList<>(byCategory.values());
    }

    @Override
    public String getSourceDescription() {
        return "XML: " + path;
    }
}
