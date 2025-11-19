import org.junit.jupiter.api.Test;

import com.jeopardy.core.GameState;
import com.jeopardy.core.Player;
import com.jeopardy.report.TxtReportWriter;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ReportTest {
    @Test
    public void reportContainsPlayerScoresandWinner() throws Exception{
        Player p1 = new Player("P1", "Alice"), p2 = new Player("P2", "Bob");
        GameState state = new GameState();

        p1.addScore(500);
        p2.addScore(300);

        List<Player> players = new ArrayList<>();
        players.add(p1);
        players.add(p2);

        state.setPlayers(players);

        File temp = File.createTempFile("report", ".txt");
        TxtReportWriter writer = new TxtReportWriter();

        writer.generate(state, temp.getAbsolutePath());

        String text = Files.readString(temp.toPath());

        assertTrue(text.contains("Alice"));
        assertTrue(text.contains("Bob"));
        assertTrue(text.contains("500"));
        assertTrue(text.contains("300"));
        assertTrue(text.contains("Winner"));

        temp.delete();
    }
}
