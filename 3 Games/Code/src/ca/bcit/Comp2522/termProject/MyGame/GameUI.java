package ca.bcit.Comp2522.termProject.MyGame;

import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * GameUI manages the user interface components for the game including score,
 * level, timer, and target word display.
 *
 * @author Sehaj Gill
 * @version 1.0
 */
public class GameUI
{
    private static final int    UI_WIDTH_PIXELS    = 150;
    private static final int    UI_HEIGHT_PIXELS   = 100;
    private static final int    TEXT_X_PIXELS      = 10;
    private static final int    SCORE_Y_PIXELS     = 30;
    private static final int    LEVEL_Y_PIXELS     = 50;
    private static final int    TIMER_Y_PIXELS     = 70;
    private static final int    TARGET_Y_PIXELS    = 90;
    private static final int    UI_LAYOUT_X_PIXELS = 10;
    private static final int    UI_LAYOUT_Y_PIXELS = 10;
    private static final String SCORE_PREFIX       = "Score: ";
    private static final String LEVEL_PREFIX       = "Level: ";
    private static final String TIME_PREFIX        = "Time: ";
    private static final String TARGET_PREFIX      = "Target: ";
    private static final String UI_PANE_STYLE      = "ui-pane";
    private static final String UI_TEXT_STYLE      = "ui-text";

    private final Pane uiPane         = new Pane();
    private final Text scoreText      = new Text(SCORE_PREFIX + "0");
    private final Text levelText      = new Text(LEVEL_PREFIX + "1");
    private final Text timerText      = new Text(TIME_PREFIX + "30.0");
    private final Text targetWordText = new Text(TARGET_PREFIX);

    /*
     * Constructs a new GameUI and initializes the UI components.
     */
    GameUI()
    {
        uiPane.setPrefSize(UI_WIDTH_PIXELS,
                           UI_HEIGHT_PIXELS);
        uiPane.setLayoutX(UI_LAYOUT_X_PIXELS);
        uiPane.setLayoutY(UI_LAYOUT_Y_PIXELS);
        uiPane.getStyleClass().add(UI_PANE_STYLE);

        setupText(scoreText,
                  SCORE_Y_PIXELS);
        setupText(levelText,
                  LEVEL_Y_PIXELS);
        setupText(timerText,
                  TIMER_Y_PIXELS);
        setupText(targetWordText,
                  TARGET_Y_PIXELS);

        uiPane.getChildren().addAll(scoreText,
                                    levelText,
                                    timerText,
                                    targetWordText);
    }

    /*
     * Returns the UI pane containing all game UI elements.
     *
     * @return the game UI pane
     */
    Pane getUIPane()
    {
        return uiPane;
    }

    /*
     * Updates the score display.
     *
     * @param score the current score
     */
    void updateScore(final int score)
    {
        scoreText.setText(SCORE_PREFIX + score);
    }

    /*
     * Updates the level display.
     *
     * @param level the current level
     */
    void updateLevel(final int level)
    {
        levelText.setText(LEVEL_PREFIX + level);
    }

    /*
     * Updates the displayed target word.
     *
     * @param targetWord the current target word
     */
    void updateTargetWord(final String targetWord)
    {
        targetWordText.setText(TARGET_PREFIX + targetWord);
    }

    /*
     * Updates the timer display.
     *
     * @param time the remaining time in seconds
     */
    void updateTimer(final double time)
    {
        timerText.setText(String.format(TIME_PREFIX + "%.1f",
                                        time));
    }

    /*
     * Loads a list of words from a resource file.
     *
     * @param path the path to the resource file
     * @return a list of non-empty trimmed words
     */
    List<String> loadWords(final String path)
    {
        final List<String> words;
        words = new ArrayList<>();
        BufferedReader reader;
        reader= new BufferedReader(
                new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream(path))));
        try(reader)
        {
            String line;
            while((line = reader.readLine()) != null)
            {
                if(!line.trim().isEmpty())
                {
                    words.add(line.trim());
                }
            }
        }
        catch(Exception e)
        {
            System.err.println("Error loading words from " + path + ": " + e.getMessage());
        }
        return words;
    }

    /*
     * Sets up a Text node with the specified position and style.
     *
     * @param text the Text node to set up
     * @param y    the y-coordinate of the Text node in pixels
     */
    private void setupText(final Text text,
                           final int y)
    {
        text.setX(TEXT_X_PIXELS);
        text.setY(y);
        text.getStyleClass().add(UI_TEXT_STYLE);
    }
}