package com.jeopardy.report;

import com.jeopardy.core.GameState;
import com.jeopardy.core.Player;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class TxtReportWriter extends AbstractReportWriter {
    private final StringBuilder sb = new StringBuilder();

    @Override
    protected void startDocument(GameState state) {
        sb.append("JEOPARDY GAME REPORT\n");
        sb.append("Case ID: ").append(state.getCaseId()).append("\n\n");
    }

    @Override
    protected void writeTurnByTurn(GameState state) throws IOException {
        sb.append("TURNS SUMMARY\n");

        // Map player ID -> player name (ID is what EventLogger writes)
        Map<String, String> idToName = new HashMap<>();
        for (Player p : state.getPlayers()) {
            idToName.put(p.getId(), p.getName());
        }

        // Same event log path used in MainApp when creating EventLogger
        String eventsPath = System.getProperty("user.home") + File.separator + "jeopardy_event_log.csv";
        File f = new File(eventsPath);
        if (!f.exists()) {
            sb.append("(No event log found at ").append(eventsPath).append(")\n\n");
            return;
        }

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))) {

            // Skip header
            String line = br.readLine();
            boolean any = false;

            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] parts = parseCsvLine(line);
                // Case_ID,Player_ID,Activity,Timestamp,Category,Question_Value,Answer_Given,Result,Score_After_Play
                if (parts.length < 9) continue;

                String caseId      = parts[0];
                String playerId    = parts[1];
                String activity    = parts[2];
                String timestamp   = parts[3];
                String category    = parts[4];
                String value       = parts[5];
                String answerGiven = parts[6];
                String result      = parts[7];
                String scoreAfter  = parts[8];

                // Only show actual question answers for this game
                if (!state.getCaseId().equals(caseId)) continue;
                if (!"Answer Question".equals(activity)) continue;

                String playerName = idToName.getOrDefault(playerId, playerId);

                sb.append(String.format(
                        "%s | %s | %s %s | Answer: %s | %s | Score after: %s%n",
                        timestamp,
                        playerName,
                        category,
                        value,
                        answerGiven,
                        result,
                        scoreAfter
                ));
                any = true;
            }

            if (!any) {
                sb.append("(No per-turn events recorded for this game.)\n");
            }
            sb.append("\n");
        }
    }

    /**
     * Simple CSV parser that understands quotes.
     * Same idea as in CsvBoardLoader.
     */
    private static String[] parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder sbField = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '\"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(sbField.toString());
                sbField.setLength(0);
            } else {
                sbField.append(c);
            }
        }
        fields.add(sbField.toString());
        return fields.toArray(new String[0]);
    }


    @Override
    protected void writeFinalScores(GameState state) {
        sb.append("FINAL SCORES\n");
        for (Player p : state.getPlayers()) {
            sb.append(p.getName()).append(": ").append(p.getScore()).append("\n");
        }
        sb.append("\n");
    }

    @Override
    protected void finish(String outputPath) throws IOException {
        try (Writer w = new OutputStreamWriter(new FileOutputStream(outputPath), StandardCharsets.UTF_8)) {
            w.write(sb.toString());
        }
    }
}
