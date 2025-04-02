package ca.bcit.Comp2522.termProject.MyGame;

import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameUI {
    private static final int UI_WIDTH = 250;
    private static final int UI_HEIGHT = 150;

    private final Pane uiPane = new Pane();
    private final Text scoreText = new Text("Score: 0");
    private final Text levelText = new Text("Level: 1");
    private final Text timerText = new Text("Time: 30.0");
    private final Text targetWordText = new Text("Target: ");

    public GameUI() {
        uiPane.setPrefSize(UI_WIDTH, UI_HEIGHT);
        uiPane.setLayoutX(10);
        uiPane.setLayoutY(10);
        uiPane.getStyleClass().add("ui-pane");

        scoreText.setX(10);
        scoreText.setY(30);
        scoreText.getStyleClass().add("ui-text");

        levelText.setX(10);
        levelText.setY(50);
        levelText.getStyleClass().add("ui-text");

        timerText.setX(10);
        timerText.setY(70);
        timerText.getStyleClass().add("ui-text");

        targetWordText.setX(10);
        targetWordText.setY(90);
        targetWordText.getStyleClass().add("ui-text");

        uiPane.getChildren().addAll(scoreText, levelText, timerText, targetWordText);
    }

    public final Pane getUIPane() {
        return uiPane;
    }

    public void updateScore(final int score) {
        scoreText.setText("Score: " + score);
    }

    public void updateLevel(final int level) {
        levelText.setText("Level: " + level);
    }

    public void updateTimer(final double time) {
        timerText.setText(String.format("Time: %.1f", time));
    }

    public void updateTargetWord(final String targetWord) {
        targetWordText.setText("Target: " + targetWord);
    }

    public final List<String> loadWords(final String path) {
        final List<String> words;
        words = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream(path))))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    words.add(line.trim());
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading words from " + path + ": " + e.getMessage());
        }
        return words;
    }
}