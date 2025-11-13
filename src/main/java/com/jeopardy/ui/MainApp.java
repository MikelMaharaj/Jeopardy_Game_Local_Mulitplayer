package com.jeopardy.ui;

import com.jeopardy.core.*;
import com.jeopardy.io.LoaderFactory;
import com.jeopardy.logging.EventLogger;
import com.jeopardy.report.TxtReportWriter;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

public class MainApp extends Application {

    private GameService service;
    private ListView<String> categoriesList;
    private ListView<String> cluesList;
    private Label currentPlayerLabel;
    private TextArea questionArea;
    private ToggleGroup optionsGroup;

    @Override
    public void start(Stage stage) {
        stage.setTitle("Jeopardy (Starter)");
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // Top: controls
        ToolBar toolbar = new ToolBar();
        Button loadBtn = new Button("Load Board");
        Button startBtn = new Button("Start (Auto players)");
        Button reportBtn = new Button("Generate Report");
        toolbar.getItems().addAll(loadBtn, startBtn, reportBtn);
        root.setTop(toolbar);

        // Left: categories & clues
        GridPane left = new GridPane();
        left.setHgap(8); left.setVgap(8); left.setPadding(new Insets(8));
        categoriesList = new ListView<>();
        cluesList = new ListView<>();
        left.add(new Label("Categories"), 0, 0);
        left.add(categoriesList, 0, 1);
        left.add(new Label("Clues"), 0, 2);
        left.add(cluesList, 0, 3);
        root.setLeft(left);

        // Center: question + options
        GridPane center = new GridPane();
        center.setHgap(8); center.setVgap(8); center.setPadding(new Insets(8));
        currentPlayerLabel = new Label("Current: -");
        questionArea = new TextArea();
        questionArea.setEditable(false);
        optionsGroup = new ToggleGroup();
        RadioButton a = new RadioButton("A");
        RadioButton b = new RadioButton("B");
        RadioButton c = new RadioButton("C");
        RadioButton d = new RadioButton("D");
        a.setToggleGroup(optionsGroup);
        b.setToggleGroup(optionsGroup);
        c.setToggleGroup(optionsGroup);
        d.setToggleGroup(optionsGroup);
        Button submit = new Button("Submit Answer");
        center.add(currentPlayerLabel, 0, 0);
        center.add(new Label("Question"), 0, 1);
        center.add(questionArea, 0, 2);
        center.add(new Label("Options: choose A/B/C/D and click Submit"), 0, 3);
        center.add(a, 0, 4);
        center.add(b, 0, 5);
        center.add(c, 0, 6);
        center.add(d, 0, 7);
        center.add(submit, 0, 8);
        root.setCenter(center);

        // Wire actions
        loadBtn.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Board files", "*.json", "*.xml", "*.csv"),
                    new FileChooser.ExtensionFilter("All files", "*.*")
            );
            File f = fc.showOpenDialog(stage);
            if (f != null) {
                try {
                    // Initialize with SYSTEM logger and board loader
                    String eventsPath = Path.of(System.getProperty("user.home"), "jeopardy_event_log.csv").toString();
                    EventLogger logger = new EventLogger(eventsPath, true);
                    service = new GameService(LoaderFactory.fromPath(f.getAbsolutePath()), logger);

                    // Auto players for quick demo
                    service.start(promptForPlayers());
                    refreshBoard();
                    showInfo("Loaded: " + f.getName());
                } catch (Exception ex) {
                    showError(ex);
                }
            }
        });

        startBtn.setOnAction(e -> {
            try {
                // Auto-load sample JSON from resources if present
                Path temp = Files.createTempFile("board", ".json");
                var in = MainApp.class.getResourceAsStream("/board/sample_game.json");
                if (in == null) {
                    showInfo("No embedded sample found. Use Load Board.");
                    return;
                }
                Files.copy(in, temp, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                String eventsPath = Path.of(System.getProperty("user.home"), "jeopardy_event_log.csv").toString();
                EventLogger logger = new EventLogger(eventsPath, true);
                service = new GameService(LoaderFactory.fromPath(temp.toString()), logger);
                service.start(promptForPlayers());
                refreshBoard();
                showInfo("Auto-loaded embedded sample.");
            } catch (Exception ex) {
                showError(ex);
            }
        });

        categoriesList.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) populateClues(sel);
        });

        cluesList.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) showClue(sel);
        });

        submit.setOnAction(e -> {
            if (service == null) return;
            String selCat = categoriesList.getSelectionModel().getSelectedItem();
            String selClue = cluesList.getSelectionModel().getSelectedItem();
            if (selCat == null || selClue == null) {
                showInfo("Pick a category and a clue first.");
                return;
            }
            var cat = service.getState().getCategories().stream().filter(c1 -> c1.getName().equals(selCat)).findFirst().orElse(null);
            if (cat == null) return;
            int value = Integer.parseInt(selClue.replace("$",""));
            var clue = cat.getClues().stream().filter(cl -> cl.getValue() == value && !cl.isUsed()).findFirst().orElse(null);
            if (clue == null) { showInfo("Clue already used."); return; }
            Toggle t = optionsGroup.getSelectedToggle();
            if (t == null) { showInfo("Choose A/B/C/D."); return; }
            String answer = ((RadioButton)t).getText();
            service.answerClue(cat, clue, answer);
            refreshBoard();
        });

        reportBtn.setOnAction(e -> {
            if (service == null) return;
            try {
                FileChooser fc = new FileChooser();
                fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text", "*.txt"));
                fc.setInitialFileName("jeopardy_report.txt");
                File f = fc.showSaveDialog(stage);
                if (f != null) {
                    service.generateReport(new TxtReportWriter(), f.getAbsolutePath());
                    showInfo("Report written: " + f.getAbsolutePath());
                }
            } catch (Exception ex) {
                showError(ex);
            }
        });

        stage.setScene(new Scene(root, 950, 600));
        stage.show();
    }

    
    /**
     * Prompt the user for between 1 and 4 player names.
     */
    private List<String> promptForPlayers() {
        TextInputDialog countDialog = new TextInputDialog("2");
        countDialog.setTitle("Players");
        countDialog.setHeaderText("Enter number of players (1-4):");
        countDialog.setContentText("Number of players:");
        int count;
        try {
            String res = countDialog.showAndWait().orElse("2");
            count = Integer.parseInt(res.trim());
        } catch (NumberFormatException ex) {
            count = 2;
        }
        if (count < 1) count = 1;
        if (count > 4) count = 4;

        List<String> names = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            TextInputDialog nameDialog = new TextInputDialog("Player " + i);
            nameDialog.setTitle("Player " + i);
            nameDialog.setHeaderText("Enter name for player " + i + ":");
            nameDialog.setContentText("Name:");
            String name = nameDialog.showAndWait().orElse("Player " + i).trim();
            if (name.isEmpty()) {
                name = "Player " + i;
            }
            names.add(name);
        }
        return names;
    }

private void refreshBoard() {
        if (service == null) return;
        var state = service.getState();
        currentPlayerLabel.setText("Current: " + state.getCurrentPlayer().getName() + " | Score: " +
                state.getPlayers().stream().map(p -> p.getName()+":"+p.getScore()).collect(Collectors.joining(" | ")));
        categoriesList.getItems().setAll(state.getCategories().stream().map(Category::getName).toList());
        cluesList.getItems().clear();
        questionArea.clear();
        optionsGroup.selectToggle(null);
    }

    private void populateClues(String categoryName) {
        if (service == null) return;
        var cat = service.getState().getCategories().stream().filter(c -> c.getName().equals(categoryName)).findFirst().orElse(null);
        if (cat == null) return;
        cluesList.getItems().setAll(cat.getClues().stream().filter(cl -> !cl.isUsed()).map(cl -> "$"+cl.getValue()).toList());
    }

    private void showClue(String clueLabel) {
        if (service == null) return;
        String catName = categoriesList.getSelectionModel().getSelectedItem();
        var cat = service.getState().getCategories().stream().filter(c -> c.getName().equals(catName)).findFirst().orElse(null);
        if (cat == null) return;
        int value = Integer.parseInt(clueLabel.replace("$",""));
        var clue = cat.getClues().stream().filter(cl -> cl.getValue() == value && !cl.isUsed()).findFirst().orElse(null);
        if (clue == null) return;
        questionArea.setText(clue.getQuestionText() + "\n\nA) " + clue.getOptions().get("A") +
                "\nB) " + clue.getOptions().get("B") +
                "\nC) " + clue.getOptions().get("C") +
                "\nD) " + clue.getOptions().get("D"));
    }

    private void showInfo(String msg) { new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait(); }
    private void showError(Exception ex) { new Alert(Alert.AlertType.ERROR, ex.toString(), ButtonType.OK).showAndWait(); }

    public static void main(String[] args) { launch(args); }
}
