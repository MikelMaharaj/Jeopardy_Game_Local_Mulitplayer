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
Writes to `~/jeopardy_event_log.csv` with columns:
`Case_ID,Player_ID,Activity,Timestamp,Category,Question_Value,Answer_Given,Result,Score_After_Play`

## Report
Choose **Generate Report** to save a TXT file anywhere. The template can be extended or replaced with PDF/DOCX later.

## Patterns & SOLID
- Strategy (BoardLoader)
- Factory Method (LoaderFactory)
- Template Method (ReportWriter)
- UI and services decoupled; loaders/reporters swappable; single responsibilities per class.

#Created by Mikel Maharaj
