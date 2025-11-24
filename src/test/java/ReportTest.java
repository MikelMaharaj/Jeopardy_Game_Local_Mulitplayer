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
    public void reportContainsPlayerScores() throws Exception{
        Player player1 = new Player("Player 1", "P1"), player2 = new Player("Player 2", "P2");
        GameState gameState = new GameState();

        player1.addScore(500);
        player2.addScore(300);

        List<Player> playerList = new ArrayList<>();
        playerList.add(player1);
        playerList.add(player2);

        gameState.setPlayers(playerList);

        File temp = File.createTempFile("report", ".txt");
        TxtReportWriter reportWriter = new TxtReportWriter();

        reportWriter.generate(gameState, temp.getAbsolutePath());

        String scores = Files.readString(temp.toPath());

        assertTrue(scores.contains("P1"));
        assertTrue(scores.contains("P2"));
        assertTrue(scores.contains("500"));
        assertTrue(scores.contains("300"));

        temp.delete();
    }
}
