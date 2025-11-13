package com.jeopardy.logging;

import com.jeopardy.core.Player;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

public class EventLogger implements Closeable {
    private final File file;
    private final boolean append;

    public EventLogger(String path, boolean append) {
        this.file = new File(path);
        this.append = append;
    }

    public void init() throws IOException {
        boolean needHeader = !file.exists() || !append;
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file, append), StandardCharsets.UTF_8))) {
            if (needHeader) {
                pw.println("Case_ID,Player_ID,Activity,Timestamp,Category,Question_Value,Answer_Given,Result,Score_After_Play");
            }
        }
    }

    public void log(String caseId, Player player, String activity,
                    String category, Integer questionValue,
                    String answerGiven, String result, Integer scoreAfter) {
        String playerId = player == null ? "SYSTEM" : player.getId();
        String ts = DateTimeFormatter.ISO_INSTANT.format(Instant.now());
        String line = String.join("," ,
                escape(caseId),
                escape(playerId),
                escape(activity),
                escape(ts),
                escape(category == null ? "" : category),
                escape(questionValue == null ? "" : questionValue.toString()),
                escape(answerGiven == null ? "" : answerGiven),
                escape(result == null ? "" : result),
                escape(scoreAfter == null ? "" : scoreAfter.toString())
        );
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file, true), StandardCharsets.UTF_8))) {
            pw.println(line);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static String escape(String s) {
        if (s.contains(",") || s.contains("\"")) {
            return "\"" + s.replace("\"","\"\"") + "\"";
        }
        return s;
    }

    @Override
    public void close() { /* nothing */ }
}
