//package com.jeopardy;

import com.jeopardy.io.*;
import com.jeopardy.core.Category;
import com.jeopardy.core.GameService;
import com.jeopardy.core.Player;
import com.jeopardy.core.Clue;
import com.jeopardy.logging.EventLogger;

import java.util.Arrays;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class GameServiceTest {

    @Test
    public void correctAnswerIncreasesScoreAndRotatesPlayer() throws Exception{
        // made a simple board with 1 category n 1 clue.
        Category cat = new Category("TestCat");
        Map<String,String> opts = new LinkedHashMap<>();
        opts.put("A","AnsA");
        opts.put("B","AnsB");
        Clue clue = new Clue(100, "Q?", opts, "A");
        cat.addClue(clue);

        BoardLoader loader = new BoardLoader() {
            @Override
            public java.util.List<Category> load() {
                return Collections.singletonList(cat);
            }

            @Override
            public String getSourceDescription() { return "test"; }
        };

        EventLogger logger = new EventLogger("test.log", false) {
            @Override public void init() {}
            @Override public void log(String caseId, Player player, String activity, String category, Integer questionValue, String answerGiven, String result, Integer scoreAfter) {}
        };

        GameService svc = new GameService(loader, logger);
        svc.start(Arrays.asList("Alice","Bob"));

        // first player answers correctly
        Player p1 = svc.getState().getPlayers().get(0);
        Player p2 = svc.getState().getPlayers().get(1);
        assertEquals(p1, svc.getState().getCurrentPlayer());

        svc.answerClue(cat, clue, "A");

        assertEquals(100, p1.getScore());
        // to make sure after answering the current player rotates to player 2
        assertEquals(p2, svc.getState().getCurrentPlayer());
    }

    @Test
    public void wrongAnswerDecreasesScore() throws Exception{
        Category cat = new Category("TestCat");
        Map<String,String> opts = new LinkedHashMap<>();
        opts.put("A","AnsA");
        opts.put("B","AnsB");
        Clue clue = new Clue(50, "Q?", opts, "A");
        cat.addClue(clue);

        BoardLoader loader = new BoardLoader() {
            @Override
            public java.util.List<Category> load() {
                return Collections.singletonList(cat);
            }

            @Override
            public String getSourceDescription() { return "test"; }
        };

        EventLogger logger = new EventLogger("test2.log", false) {
            @Override public void init() {}
            @Override public void log(String caseId, Player player, String activity, String category, Integer questionValue, String answerGiven, String result, Integer scoreAfter) {}
        };

        GameService svc = new GameService(loader, logger);
        svc.start(Collections.singletonList("Solo"));

        Player p = svc.getState().getPlayers().get(0);
        assertEquals(p, svc.getState().getCurrentPlayer());

        svc.answerClue(cat, clue, "B");

        assertEquals(-50, p.getScore());
        assertTrue(clue.isUsed());
    }

    @Test
    public void playersRotateInOrder() throws Exception{
        Category cat = new Category("C");
        Map<String,String> opts = new LinkedHashMap<>();
        opts.put("A","X");
        Clue clue1 = new Clue(10, "Q1", opts, "A");
        Clue clue2 = new Clue(20, "Q2", opts, "A");
        Clue clue3 = new Clue(30, "Q3", opts, "A");
        cat.addClue(clue1);
        cat.addClue(clue2);
        cat.addClue(clue3);

        BoardLoader loader = new BoardLoader() {
            @Override
            public java.util.List<Category> load() {
                return Collections.singletonList(cat);
            }

            @Override
            public String getSourceDescription() { return "test"; }
        };

        EventLogger logger = new EventLogger("test3.log", false) {
            @Override public void init() {}
            @Override public void log(String caseId, Player player, String activity, String category, Integer questionValue, String answerGiven, String result, Integer scoreAfter) {}
        };

        GameService svc = new GameService(loader, logger);
        svc.start(Arrays.asList("A","B","C"));

        Player p1 = svc.getState().getPlayers().get(0);
        Player p2 = svc.getState().getPlayers().get(1);
        Player p3 = svc.getState().getPlayers().get(2);

        assertEquals(p1, svc.getState().getCurrentPlayer());
        svc.answerClue(cat, clue1, "A"); // to make sure it goes player 1 to player 2
        assertEquals(p2, svc.getState().getCurrentPlayer());
        svc.answerClue(cat, clue2, "A"); // now to make sure it goes player 2 to player 3
        assertEquals(p3, svc.getState().getCurrentPlayer());
        svc.answerClue(cat, clue3, "A"); // then to make sure it goes player 3 to player 1
        assertEquals(p1, svc.getState().getCurrentPlayer());
    }

}

