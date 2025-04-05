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

    private static final int    UI_WIDTH       = 150;
    private static final int    UI_HEIGHT      = 100;
    private static final int    TEXT_X         = 10;
    private static final int    SCORE_Y        = 30;
    private static final int    LEVEL_Y        = 50;
    private static final int    TIMER_Y        = 70;
    private static final int    TARGET_Y       = 90;
    private static final int    UI_LAYOUT_X    = 10;
    private static final int    UI_LAYOUT_Y    = 10;
    private static final String SCORE_PREFIX   = "Score: ";
    private static final String LEVEL_PREFIX   = "Level: ";
    private static final String TIME_PREFIX    = "Time: ";
    private static final String TARGET_PREFIX  = "Target: ";
    private static final String UI_PANE_STYLE  = "ui-pane";
    private static final String UI_TEXT_STYLE  = "ui-text";
    private final        Pane   uiPane         = new Pane();
    private final        Text   scoreText      = new Text(SCORE_PREFIX + "0");
    private final        Text   levelText      = new Text(LEVEL_PREFIX + "1");
    private final        Text   timerText      = new Text(TIME_PREFIX + "30.0");
    private final        Text   targetWordText = new Text(TARGET_PREFIX);

    /**
     * Constructs a new GameUI and initializes the UI components.
     */
    public GameUI()
    {
        uiPane.setPrefSize(UI_WIDTH,
                           UI_HEIGHT);
        uiPane.setLayoutX(UI_LAYOUT_X);
        uiPane.setLayoutY(UI_LAYOUT_Y);
        uiPane.getStyleClass().add(UI_PANE_STYLE);

        setupText(scoreText,
                  SCORE_Y);
        setupText(levelText,
                  LEVEL_Y);
        setupText(timerText,
                  TIMER_Y);
        setupText(targetWordText,
                  TARGET_Y);

        uiPane.getChildren().addAll(scoreText,
                                    levelText,
                                    timerText,
                                    targetWordText);
    }

    private void setupText(final Text text,
                           final int y)
    {
        text.setX(TEXT_X);
        text.setY(y);
        text.getStyleClass().add(UI_TEXT_STYLE);
    }

    /**
     * Returns the UI pane containing all game UI elements.
     *
     * @return the game UI pane
     */
    Pane getUIPane()
    {
        return uiPane;
    }

    /**
     * Updates the score display.
     *
     * @param score the current score
     */
    public void updateScore(final int score)
    {
        scoreText.setText(SCORE_PREFIX + score);
    }

    /**
     * Updates the level display.
     *
     * @param level the current level
     */
    public void updateLevel(final int level)
    {
        levelText.setText(LEVEL_PREFIX + level);
    }

    /**
     * Updates the timer display.
     *
     * @param time the remaining time
     */
    public void updateTimer(final double time)
    {
        timerText.setText(String.format(TIME_PREFIX + "%.1f",
                                        time));
    }

    /**
     * Updates the displayed target word.
     *
     * @param targetWord the current target word
     */
    public void updateTargetWord(final String targetWord)
    {
        targetWordText.setText(TARGET_PREFIX + targetWord);
    }

    /**
     * Loads a list of words from a resource file.
     *
     * @param path the path to the resource file
     * @return a list of non-empty trimmed words
     */
    public List<String> loadWords(final String path)
    {
        final List<String> words = new ArrayList<>();
        try(BufferedReader reader = new BufferedReader(
                new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream(path)))))
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
}
