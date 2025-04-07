package ca.bcit.Comp2522.termProject.letterrushgame;

import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/*
 * GameUI manages and displays the user interface elements of the Letter Rush Game,
 * including the player's current score, level, remaining time in seconds, and the target word.
 * It handles both the visual creation of UI elements and dynamic updates throughout gameplay.
 * <p>
 * This class also provides functionality to load target words from a specified file.
 * The visual layout is static and positioned using fixed pixel values.
 * UI elements are styled using CSS class references to ensure consistency across the interface.
 * </p>
 *G
 * @author Sehaj Gill
 * @version 1.0
 */
final class GameUI
{

    // Constants for dimensions (all in pixels)
    private static final int UI_WIDTH_PIXELS    = 150;
    private static final int UI_HEIGHT_PIXELS   = 100;
    private static final int TEXT_X_PIXELS      = 10;
    private static final int SCORE_Y_PIXELS     = 30;
    private static final int LEVEL_Y_PIXELS     = 50;
    private static final int TIMER_Y_PIXELS     = 70;
    private static final int TARGET_Y_PIXELS    = 90;
    private static final int UI_LAYOUT_X_PIXELS = 10;
    private static final int UI_LAYOUT_Y_PIXELS = 10;

    private static final String SCORE_PREFIX  = "Score: ";
    private static final String LEVEL_PREFIX  = "Level: ";
    private static final String TIME_PREFIX   = "Time: ";
    private static final String TARGET_PREFIX = "Target: ";

    private static final String UI_PANE_STYLE = "ui-pane";
    private static final String UI_TEXT_STYLE = "ui-text";

    private final Pane uiPane;
    private final Text scoreText;
    private final Text levelText;
    private final Text timerText;
    private final Text targetWordText;

    /*
     * Constructs a new GameUI instance, initializing all user interface components
     * such as labels for score, level, timer, and target word. Positions and styles
     * are applied to ensure clear display during the game.
     */
    GameUI()
    {
        uiPane         = new Pane();
        scoreText      = new Text(SCORE_PREFIX + "0");
        levelText      = new Text(LEVEL_PREFIX + "1");
        timerText      = new Text(TIME_PREFIX + "30.0");
        targetWordText = new Text(TARGET_PREFIX);

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
     * Returns the Pane that contains all the game UI elements.
     *
     * @return the game UI pane to be added to the scene graph
     */
    Pane getUIPane()
    {
        return uiPane;
    }

    /*
     * Updates the on-screen score value with the latest player score.
     *
     * @param scorePoints the current player score in points
     */
    void updateScore(final int scorePoints)
    {
        scoreText.setText(SCORE_PREFIX + scorePoints);
    }

    /*
     * Updates the on-screen level value with the latest player level.
     *
     * @param levelNumber the current level number
     */
    void updateLevel(final int levelNumber)
    {
        levelText.setText(LEVEL_PREFIX + levelNumber);
    }

    /*
     * Updates the target word display with a new word.
     *
     * @param targetWord the word the player must type
     */
    void updateTargetWord(final String targetWord)
    {
        targetWordText.setText(TARGET_PREFIX + targetWord);
    }

    /*
     * Updates the countdown timer display shown in seconds (formatted to 1 decimal place).
     *
     * @param timeSeconds the remaining time in seconds
     */
    void updateTimer(final double timeSeconds)
    {
        timerText.setText(String.format(TIME_PREFIX + "%.1f",
                                        timeSeconds));
    }

    /*
     * Loads and returns a list of target words from a given resource file.
     * Blank or whitespace-only lines are ignored.
     *
     * @param pathToFile the relative path to the resource file (e.g., "/words.txt")
     * @return a list of trimmed, non-empty words from the file
     */
    List<String> loadWords(final String pathToFile)
    {
        final List<String> wordList = new ArrayList<>();

        try(BufferedReader reader = new BufferedReader
                (new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream(pathToFile)))))
        {
            String currentLine;
            while((currentLine = reader.readLine()) != null)
            {
                final String trimmedWord = currentLine.trim();
                if(!trimmedWord.isEmpty())
                {
                    wordList.add(trimmedWord);
                }
            }
        }
        catch(final Exception exception)
        {
            System.err.println("Error loading words from " + pathToFile + ": " + exception.getMessage());
        }

        return wordList;
    }

    /*
     * Configures the styling and Y-position of a given Text node used in the UI.
     *
     * @param textElement     the Text node to be styled and positioned
     * @param yPositionPixels the vertical Y-coordinate in pixels
     */
    private void setupText(final Text textElement,
                           final int yPositionPixels)
    {
        textElement.setX(TEXT_X_PIXELS);
        textElement.setY(yPositionPixels);
        textElement.getStyleClass().add(UI_TEXT_STYLE);
    }
}
