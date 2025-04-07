package ca.bcit.Comp2522.termProject.letterrushgame;

import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Manages and displays the user interface elements for the LetterRush game, providing a visual dashboard
 * for gameplay information. This class creates and updates a fixed-layout UI pane containing text elements
 * for the player’s current score in points, current level number, remaining time in seconds, and the target
 * word to be matched. It utilizes JavaFX’s Pane and Text classes to construct a static layout with predefined
 * pixel-based positioning and applies CSS styling for consistent visual presentation. Additionally, it offers
 * functionality to load a list of target words from a specified resource file, filtering out blank lines and
 * trimming whitespace. The UI is designed to be added to a JavaFX scene graph and dynamically updated during
 * gameplay to reflect the player’s progress and status.
 *
 * @author Sehaj Gill
 * @version 1.0
 */
final class GameUI
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

    private final Pane uiPane;
    private final Text scoreText;
    private final Text levelText;
    private final Text timerText;
    private final Text targetWordText;

    /**
     * Constructs a new GameUI instance, initializing all user interface components for display in the LetterRush game.
     * This constructor creates a JavaFX Pane (uiPane) to serve as the container for all UI elements, setting its size
     * to UI_WIDTH_PIXELS by UI_HEIGHT_PIXELS and positioning it at (UI_LAYOUT_X_PIXELS, UI_LAYOUT_Y_PIXELS) within
     * the game window. It initializes four Text objects: scoreText (displaying the player’s score), levelText (showing
     * the current level), timerText (indicating remaining time), and targetWordText (presenting the target word).
     * Each Text object is pre-populated with initial values (score at 0 points, level at 1, timer at 30.0 seconds,
     * and target word as an empty prefix) and positioned vertically at predefined Y-coordinates (SCORE_Y_PIXELS,
     * LEVEL_Y_PIXELS, TIMER_Y_PIXELS, TARGET_Y_PIXELS) with a shared X-coordinate (TEXT_X_PIXELS). The method applies
     * CSS styling via UI_PANE_STYLE for the pane and UI_TEXT_STYLE for text elements, ensuring a consistent look.
     * Finally, it adds all Text objects to the uiPane’s children for rendering in the game scene.
     */
    GameUI()
    {
        this.uiPane         = new Pane();
        this.scoreText      = new Text(SCORE_PREFIX + "0");
        this.levelText      = new Text(LEVEL_PREFIX + "1");
        this.timerText      = new Text(TIME_PREFIX + "30.0");
        this.targetWordText = new Text(TARGET_PREFIX);

        this.uiPane.setPrefSize(UI_WIDTH_PIXELS,
                                UI_HEIGHT_PIXELS);
        this.uiPane.setLayoutX(UI_LAYOUT_X_PIXELS);
        this.uiPane.setLayoutY(UI_LAYOUT_Y_PIXELS);
        this.uiPane.getStyleClass().add(UI_PANE_STYLE);

        setupText(this.scoreText,
                  SCORE_Y_PIXELS);
        setupText(this.levelText,
                  LEVEL_Y_PIXELS);
        setupText(this.timerText,
                  TIMER_Y_PIXELS);
        setupText(this.targetWordText,
                  TARGET_Y_PIXELS);

        this.uiPane.getChildren().addAll(this.scoreText,
                                         this.levelText,
                                         this.timerText,
                                         this.targetWordText);
    }

    /*
     * Retrieves the JavaFX Pane containing all user interface elements for the LetterRush game.
     * This method returns the uiPane, which encapsulates the score, level, timer, and target word Text objects,
     * fully configured with their initial values, positions, and styles. The returned pane is intended to be
     * added to a JavaFX scene graph to display the game’s UI within the game window, providing a visual
     * interface for the player’s status and objectives.
     *
     * @return the Pane object containing all UI elements
     */
    Pane getUIPane()
    {
        return uiPane;
    }

    /*
     * Updates the displayed score on the UI with the player’s current score in points.
     * This method sets the text of scoreText to a string composed of SCORE_PREFIX followed by the provided
     * scorePoints value (e.g., "Score: 50"), reflecting the player’s latest score. It ensures the UI accurately
     * represents the player’s progress as they earn points during gameplay.
     *
     * @param scorePoints the current player score in points to display
     */
    void updateScore(final int scorePoints)
    {
        scoreText.setText(SCORE_PREFIX + scorePoints);
    }

    /*
     * Updates the displayed level number on the UI with the player’s current level.
     * This method sets the text of levelText to a string composed of LEVEL_PREFIX followed by the provided
     * levelNumber (e.g., "Level: 3"), indicating the player’s current stage in the game. It keeps the UI
     * synchronized with the game’s progression as the player advances through levels.
     *
     * @param levelNumber the current level number to display
     */
    void updateLevel(final int levelNumber)
    {
        levelText.setText(LEVEL_PREFIX + levelNumber);
    }

    /*
     * Updates the displayed target word on the UI with the current word the player must match.
     * This method sets the text of targetWordText to a string composed of TARGET_PREFIX followed by the
     * provided targetWord (e.g., "Target: APPLE"), informing the player of their current objective.
     * It ensures the UI reflects the active target word for the level or game state.
     *
     * @param targetWord the target word string to display
     */
    void updateTargetWord(final String targetWord)
    {
        targetWordText.setText(TARGET_PREFIX + targetWord);
    }

    /*
     * Updates the displayed countdown timer on the UI with the remaining time in seconds.
     * This method sets the text of timerText to a string composed of TIME_PREFIX followed by the provided
     * timeSeconds, formatted to one decimal place (e.g., "Time: 15.7"). It uses String.format() to ensure
     * precise display of the remaining time, keeping the player informed of the time constraint for the
     * current level or task.
     *
     * @param timeSeconds the remaining time in seconds to display
     */
    void updateTimer(final double timeSeconds)
    {
        timerText.setText(String.format(TIME_PREFIX + "%.1f",
                                        timeSeconds));
    }

    /*
     * Loads a list of target words from a specified resource file for use in the LetterRush game.
     * This method reads the file located at pathToFile (e.g., "/words.txt") using a BufferedReader wrapped
     * around an InputStreamReader, retrieving lines from the resource stream via getClass().getResourceAsStream().
     * It processes each line, trims whitespace, and adds non-empty strings to a new ArrayList of words. Blank
     * or whitespace-only lines are skipped to ensure only valid words are included. If an exception occurs
     * during file reading (e.g., file not found, I/O error), it logs an error message with the exception details
     * to System.err and returns the list as-is (potentially empty). The returned list provides the game with
     * a set of target words for gameplay.
     *
     * @param pathToFile the relative path to the resource file containing target words (e.g., "/words.txt")
     * @return a List of String objects containing trimmed, non-empty words from the file
     */
    List<String> loadWords(final String pathToFile)
    {
        final List<String> wordList;
        wordList = new ArrayList<>();

        try(BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream(pathToFile)))))
        {
            final String currentLine;

            String tempLine;
            while((tempLine = reader.readLine()) != null)
            {
                final String trimmedWord;
                trimmedWord = tempLine.trim();
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
     * Configures the position and styling of a specified Text element for inclusion in the UI pane.
     * This private helper method sets the X-coordinate of textElement to TEXT_X_PIXELS and the Y-coordinate
     * to the provided yPositionPixels, ensuring consistent horizontal alignment and vertical spacing within
     * the UI layout. It also applies the UI_TEXT_STYLE CSS class to the textElement, linking it to external
     * styling rules for font, color, or other visual properties defined in the game’s CSS file. This method
     * is used during construction to position and style scoreText, levelText, timerText, and targetWordText.
     *
     * @param textElement     the Text object to configure
     * @param yPositionPixels the vertical Y-coordinate in pixels for positioning the text
     */
    private void setupText(final Text textElement,
                           final int yPositionPixels)
    {
        textElement.setX(TEXT_X_PIXELS);
        textElement.setY(yPositionPixels);
        textElement.getStyleClass().add(UI_TEXT_STYLE);
    }
}