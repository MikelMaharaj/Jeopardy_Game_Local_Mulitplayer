package com.jeopardy.core;

import com.jeopardy.io.BoardLoader;
import com.jeopardy.logging.EventLogger;
import com.jeopardy.report.AbstractReportWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameService {
    private final GameState state = new GameState();
    private final ScoreService scoreService = new ScoreService();
    private final EventLogger logger;
    private final BoardLoader loader;

    public GameService(BoardLoader loader, EventLogger logger) {
        this.loader = loader;
        this.logger = logger;
    }

    public GameState getState() { return state; }

    public void start(List<String> playerNames) throws IOException {
        logger.init();
        logger.log(state.getCaseId(), null, "Start Game", null, null, null, null, null);
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < playerNames.size(); i++) {
            Player p = new Player("P" + (i+1), playerNames.get(i));
            players.add(p);
            logger.log(state.getCaseId(), p, "Enter Player Name", null, null, null, null, p.getScore());
        }
        state.setPlayers(players);
        logger.log(state.getCaseId(), null, "Load File", null, null, null, null, null);
        state.setCategories(loader.load());
        logger.log(state.getCaseId(), null, "File Loaded Successfully", null, null, null, null, null);
        state.start();
    }

    public void answerClue(Category category, Clue clue, String answer) {
        Player current = state.getCurrentPlayer();
        scoreService.applyAnswer(current, clue, answer);
        logger.log(state.getCaseId(), current, "Answer Question", category.getName(), clue.getValue(), answer,
                answer.equalsIgnoreCase(clue.getCorrectAnswer()) ? "Correct" : "Incorrect", current.getScore());
        state.nextPlayer();
    }

    public void generateReport(AbstractReportWriter writer, String path) throws IOException {
        writer.generate(state, path);
        logger.log(state.getCaseId(), null, "Generate Report", null, null, null, null, null);
    }
}
