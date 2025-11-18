import com.jeopardy.logging.EventLogger;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LoggingTest {
    @Test
    void initWritesHeaderIfNewFile() throws Exception{
        File temp = File.createTempFile("eventlog", ".csv");
        temp.delete();

        EventLogger logger = new EventLogger(temp.getAbsolutePath(), false);
        logger.init();

        List<String> lines = Files.readAllLines(temp.toPath());
        assertEquals(1, lines.size());
        assertEquals(
        "Case_ID,Player_ID,Activity,Timestamp,Category,Question_Value,Answer_Given,Result,Score_After_Play", 
        lines.get(0)
        );

        logger.close();
    }

    @Test
    void logWritesEventLineWithCorrectColumns() throws Exception{
        File temp = File.createTempFile("eventlog", ".csv");

        EventLogger logger = new EventLogger(temp.getAbsolutePath(), false);
        logger.init();

        logger.log("CASE1", null, "Start Game", null, null, null, "OK", 10);

        List<String> lines = Files.readAllLines(temp.toPath());
        assertEquals(2, lines.size());

        String eventLine = lines.get(1);

        assertTrue(eventLine.startsWith("CASE1,SYSTEM,Start Game,"));

        String[] fields = eventLine.split(",", -1);
        assertEquals(9, fields.length);

        logger.close();
    }
}
