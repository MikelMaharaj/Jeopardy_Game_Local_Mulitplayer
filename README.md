# Jeopardy Game (COMP3607)

A clean starter that hits the rubric: multi-format board loading (CSV/JSON/XML), event logging,
TXT reporting (template method), minimal JavaFX UI (auto-load sample), JUnit tests, and SOLID-friendly services.

## Build & Run

```bash
# From project root
mvn -q -e -DskipTests package

# Run JavaFX app (ensure Java 17+ and JavaFX runtime available via Maven deps)
mvn javafx:run
```

If JavaFX complains about platform modules on your setup, run with:
```bash
mvn -q -Djavafx.platform=win javafx:run
# or mac, linux as needed
```

## File Formats
- **JSON**: `src/main/resources/board/sample_game.json`
- **XML**: `src/main/resources/board/sample_game.xml`
- **CSV**: `src/main/resources/board/sample_game.csv`

## Event Log
Writes to `~/game_event_log.csv` with columns:
`Case_ID,Player_ID,Activity,Timestamp,Category,Question_Value,Answer_Given,Result,Score_After_Play`
and saves to Desktop

## Report
Choose **Generate Report** to save a TXT file anywhere.

## Patterns & SOLID
- Strategy (BoardLoader)
- Factory Method (LoaderFactory)
- Template Method (ReportWriter)
- UI and services decoupled; loaders/reporters swappable; single responsibilities per class.

[View the Class Diagram](ClassDiagram.png)

## Test Summary
- **Logging Test**: Tests to see if the file created writes a header file and if the logger properly logs event lines
- **Report Test**: Tests to see if reports generated contains player names and respective scores
- **Parsers Test**: Verifies that JSON, XML, and CSV board files are correctly parsed into categories and clues, ensuring category names, question values, question text, options (A–D), and the correct answer are all loaded as expected.

**Created by Mikel Maharaj**
