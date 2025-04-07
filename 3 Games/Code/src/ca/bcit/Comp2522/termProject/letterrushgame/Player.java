package ca.bcit.Comp2522.termProject.letterrushgame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Represents a player in the LetterRush game, managing their gameplay state and statistics.
 * This class tracks the player’s current score, high score, bonus points, cursor position in pixels,
 * collected letters for target and bonus words, and the number of incorrect clicks. It provides
 * methods to update these attributes based on gameplay events, such as clicking letters, completing
 * words, or resetting levels. Additionally, it handles persistence of the high score by loading from
 * and saving to a file, ensuring the player’s best performance is preserved across sessions. The
 * player’s cursor is modeled with a fixed size in pixels, and their progress is evaluated against
 * target and bonus words to determine success or failure conditions.
 *
 * @author Sehaj Gill
 * @version 1.0
 */
public class Player
{
    private static final int    INITIAL_CLICKS_COUNT     = 0;
    private static final int    INITIAL_SCORE_POINTS     = 0;
    private static final int    TARGET_POINTS_PER_LETTER = 10;
    private static final int    BONUS_POINTS_PER_WORD    = 20;
    private static final double CURSOR_SIZE_PIXELS       = 10.0;
    private static final String HIGH_SCORE_FILE_PATH     = "Resources/highScore.txt";
    private static final String ERROR_LOADING_MESSAGE    = "Error loading high score: ";
    private static final String ERROR_SAVING_MESSAGE     = "Error saving high score: ";
    private static final String CLICKED_LETTER_MESSAGE   = "Clicked letter: ";
    private static final String TARGET_SO_FAR_MESSAGE    = " | Collected Target so far: ";
    private static final String BONUS_SO_FAR_MESSAGE     = " | Collected Bonus so far: ";
    private static final String HAS_FAILED_MESSAGE       = "hasFailed: ";
    private static final String TOO_MANY_LETTERS_MESSAGE = "Too many letters clicked: ";
    private static final String GREATER_THAN_MESSAGE     = " > ";
    private static final String TARGET_COMPLETED_MESSAGE = "Target word completed, returning false.";
    private static final String TOO_MANY_CLICKS_MESSAGE  = "Too many incorrect clicks: "; 

    // Declaring instance variables
    private final List<Character> collectedTarget;
    private final List<Character> collectedBonus;
    private       int             scorePoints;
    private       int             highScorePoints;
    private       double          cursorXPixels;
    private       double          cursorYPixels;
    private       int             bonusPoints;
    private       int             incorrectClicksCount;
    private       boolean         bonusWordCompleted;

    /**
     * Constructs a new Player instance, initializing all gameplay-related attributes to their starting values.
     * The player begins with zero points for score, high score (loaded from file), and bonus points, with empty
     * lists for collected target and bonus letters. The cursor position is initialized to (0,0) pixels, and the
     * number of incorrect clicks is set to zero. The bonus word completion flag is set to false, indicating no
     * bonus word has been completed yet. The high score is retrieved from a file using loadHighScore(), defaulting
     * to zero if loading fails. This constructor prepares the player for a fresh game session.
     */
    public Player()
    {
        // Initializing instance variables
        this.collectedTarget      = new ArrayList<>();
        this.collectedBonus       = new ArrayList<>();
        this.scorePoints          = INITIAL_SCORE_POINTS;
        this.highScorePoints      = loadHighScore();
        this.cursorXPixels        = 0.0;
        this.cursorYPixels        = 0.0;
        this.bonusPoints          = INITIAL_SCORE_POINTS;
        this.incorrectClicksCount = INITIAL_CLICKS_COUNT;
        this.bonusWordCompleted   = false;
    }

    /**
     * Retrieves the number of incorrect clicks made by the player during the current level attempt.
     * Incorrect clicks occur when the player selects a letter that does not belong to the target word,
     * and this count is used to evaluate failure conditions. The value is reset when starting a new level.
     *
     * @return the number of incorrect clicks as an integer
     */
    public final int getIncorrectClicks()
    {
        return incorrectClicksCount;
    }

    /**
     * Retrieves the player’s highest score achieved across all game sessions, stored persistently in a file.
     * This value is updated whenever the current score exceeds the previous high score and is loaded from
     * file during player construction. It represents the player’s best performance to date.
     *
     * @return the high score in points as an integer
     */
    public final int getHighScore()
    {
        return highScorePoints;
    }

    /**
     * Retrieves the player’s current score accumulated during the game session, including points from
     * target letters and bonus words. This score resets to zero when the game is fully restarted and
     * increases with successful letter clicks or bonus word completions.
     *
     * @return the current score in points as an integer
     */
    public final int getScore()
    {
        return scorePoints;
    }

    /**
     * Resets the player’s state for a new level attempt while preserving cumulative scores.
     * This method clears the lists of collected target and bonus letters, resets the incorrect clicks
     * count to zero, and sets the bonus word completion flag to false. The current score, high score,
     * and bonus points remain unchanged, allowing the player to continue accumulating points across levels.
     */
    public final void resetForNewLevel()
    {
        collectedTarget.clear();
        collectedBonus.clear();
        incorrectClicksCount = INITIAL_SCORE_POINTS;
        bonusWordCompleted   = false;
    }

    /**
     * Provides a copy of the list of characters collected by the player toward the target word.
     * The returned list is a new instance to prevent external modification of the player’s internal state.
     * These characters are accumulated as the player clicks letters matching the target word during gameplay.
     *
     * @return a new List of Character objects representing the collected target letters
     */
    public final List<Character> getCollectedTarget()
    {
        // Declaring and initializing local variable
        final List<Character> copy;
        copy = new ArrayList<>(collectedTarget);

        return copy;
    }

    /**
     * Sets the player’s bonus points to the specified value and updates the high score if necessary.
     * This method assigns the provided bonus points directly, replacing the previous value, and triggers
     * an update to the high score file if the total score (including bonus points) exceeds the current
     * high score. It is typically called when resetting or manually adjusting bonus points.
     *
     * @param bonusPointsCount the number of bonus points to set, in points
     */
    public final void setBonusPoints(final int bonusPointsCount)
    {
        this.bonusPoints = bonusPointsCount;
        updateHighScore();
    }

    /**
     * Sets the player’s current score to the specified value and updates the high score if necessary.
     * This method directly assigns the provided score, replacing the previous value, and checks if the
     * new score exceeds the current high score, triggering a file update if so. It is used to manually
     * adjust the score, such as during game resets or external updates.
     *
     * @param scorePointsCount the number of score points to set, in points
     */
    public final void setScore(final int scorePointsCount)
    {
        this.scorePoints = scorePointsCount;
        updateHighScore();
    }

    /**
     * Updates the player’s high score if the current score exceeds the existing high score.
     * This method compares the current scorePoints to highScorePoints; if the former is greater,
     * it updates highScorePoints to match scorePoints and saves the new high score to the file
     * specified by HIGH_SCORE_FILE_PATH. This ensures the player’s best performance is persistently
     * recorded across game sessions.
     */
    public void updateHighScore()
    {
        if(scorePoints > highScorePoints)
        {
            highScorePoints = scorePoints;
            saveHighScore();
        }
    }

    /**
     * Determines whether the player has successfully completed the target word by matching the collected
     * target letters against the provided target word. This method delegates to hasCompletedWord() to
     * compare the collectedTarget list with the targetWord string, returning true if they match exactly
     * in both content and length (case-sensitive). Completion of the target word typically advances
     * the player to the next level or ends the current level successfully.
     *
     * @param targetWord the target word string to match against the collected target letters
     * @return true if the collected target letters form the target word exactly, false otherwise
     */
    public final boolean hasCompletedTargetWord(final String targetWord)
    {
        return hasCompletedWord(collectedTarget,
                                targetWord);
    }

    /**
     * Determines whether the player has successfully completed the bonus word by matching the collected
     * bonus letters against the provided bonus word. This method delegates to hasCompletedWord() to
     * compare the collectedBonus list with the bonusWord string, returning true if they match exactly
     * in both content and length (case-sensitive). Completing the bonus word awards additional points
     * and enhances the player’s score without necessarily ending the level.
     *
     * @param bonusWord the bonus word string to match against the collected bonus letters
     * @return true if the collected bonus letters form the bonus word exactly, false otherwise
     */
    public final boolean hasCompletedBonusWord(final String bonusWord)
    {
        return hasCompletedWord(collectedBonus,
                                bonusWord);
    }

    /**
     * Processes a letter click event, updating the player’s state based on the clicked letter and the
     * target and bonus words. If the letter is already locked, the method exits immediately. Otherwise,
     * it locks the letter, retrieves its character value, and checks if it belongs to the targetWord or
     * bonusWord. If it matches the targetWord, the letter is added to collectedTarget and target points
     * are awarded; if not, incorrectClicksCount is incremented. If it matches the bonusWord, it’s added
     * to collectedBonus. The method logs the click event and checks for bonus word completion, awarding
     * bonus points if achieved. This method drives the core interaction mechanic of the game.
     *
     * @param letter     the Letter object clicked by the player
     * @param targetWord the target word string to match against for scoring
     * @param bonusWord  the bonus word string to match against for bonus points
     */
    public final void clickLetter(final Letter letter,
                                  final String targetWord,
                                  final String bonusWord)
    {
        if(letter.lockedLetter())
        {
            return;
        }

        final char valueChar;

        valueChar = letter.getValue();

        letter.lock();

        if(targetWord.contains(String.valueOf(valueChar)))
        {
            collectedTarget.add(valueChar);
            addTargetPoints();
        }
        else
        {
            incorrectClicksCount++;
        }

        if(bonusWord.contains(String.valueOf(valueChar)))
        {
            collectedBonus.add(valueChar);
        }

        System.out.println(CLICKED_LETTER_MESSAGE + valueChar + TARGET_SO_FAR_MESSAGE +
                           collectedTarget + BONUS_SO_FAR_MESSAGE + collectedBonus);

        if(hasCompletedBonusWord(bonusWord))
        {
            System.out.println("Bonus word completed!");
            addBonusPoints();
        }
    }

    /**
     * Evaluates whether the player has failed the current level based on their collected letters and
     * incorrect clicks. Failure occurs if: (1) the number of collected target letters exceeds the
     * target word’s length, (2) the number of collected bonus letters exceeds the bonus word’s length,
     * or (3) the number of incorrect clicks equals or exceeds the length of either word. If the target
     * or bonus word is completed correctly, the method returns false (no failure). The method logs
     * the specific failure condition for debugging purposes. This check determines if the player must
     * restart the level due to errors.
     *
     * @param targetWord the target word string to evaluate against collected target letters
     * @param bonusWord  the bonus word string to evaluate against collected bonus letters
     * @return true if the player has failed the level, false otherwise
     */
    public final boolean hasFailed(final String targetWord,
                                   final String bonusWord)
    {
        if(collectedTarget.size() > targetWord.length() || collectedBonus.size() > bonusWord.length())
        {
            System.out.println(HAS_FAILED_MESSAGE + TOO_MANY_LETTERS_MESSAGE + collectedTarget.size() +
                               GREATER_THAN_MESSAGE + targetWord.length());
            return true;
        }

        if(hasCompletedTargetWord(targetWord) || hasCompletedBonusWord(bonusWord))
        {
            System.out.println(HAS_FAILED_MESSAGE + TARGET_COMPLETED_MESSAGE);
            return false;
        }

        if(incorrectClicksCount >= targetWord.length() || incorrectClicksCount >= bonusWord.length())
        {
            System.out.println(HAS_FAILED_MESSAGE + TOO_MANY_CLICKS_MESSAGE + incorrectClicksCount);
            return true;
        }

        // Critical comment: StringBuilder is used here to check collected letters, but result is unused
        final StringBuilder collectedBuilder;
        collectedBuilder = new StringBuilder();

        for(final char valueChar : collectedTarget)
        {
            collectedBuilder.append(valueChar);
        }
        return false;
    }

    /**
     * Updates the player’s cursor position to the specified coordinates in pixels.
     * This method sets cursorXPixels and cursorYPixels to the provided values, reflecting the player’s
     * current mouse position in the game window. The cursor position is used to detect collisions
     * with game elements like letters or obstacles.
     *
     * @param xPixels the x-coordinate of the cursor in pixels
     * @param yPixels the y-coordinate of the cursor in pixels
     */
    final void updateCursorPosition(final double xPixels,
                                    final double yPixels)
    {
        cursorXPixels = xPixels;
        cursorYPixels = yPixels;
    }

    /**
     * Retrieves the x-coordinate of the player’s cursor in pixels.
     * This value represents the horizontal position of the cursor within the game window, updated
     * via updateCursorPosition().
     *
     * @return the cursor’s x-coordinate in pixels as a double
     */
    final double getCursorX()
    {
        return cursorXPixels;
    }

    /**
     * Retrieves the y-coordinate of the player’s cursor in pixels.
     * This value represents the vertical position of the cursor within the game window, updated
     * via updateCursorPosition().
     *
     * @return the cursor’s y-coordinate in pixels as a double
     */
    final double getCursorY()
    {
        return cursorYPixels;
    }

    /**
     * Retrieves the fixed size of the player’s cursor in pixels.
     * This constant value (CURSOR_SIZE_PIXELS) defines the cursor’s dimensions, used for collision
     * detection or rendering purposes within the game.
     *
     * @return the cursor size in pixels as a double
     */
    final double getCursorSize()
    {
        return CURSOR_SIZE_PIXELS;
    }

    /**
     * Retrieves the player’s current bonus points accumulated during the game session.
     * Bonus points are awarded for completing the bonus word and persist across levels until reset.
     *
     * @return the bonus points in points as an integer
     */
    final int getBonusPoints()
    {
        return bonusPoints;
    }

    /**
     * Awards bonus points to the player for completing a bonus word, if not already awarded.
     * If bonusWordCompleted is false, this method adds BONUS_POINTS_PER_WORD to both scorePoints and
     * bonusPoints, sets bonusWordCompleted to true to prevent duplicate awards, logs the new score,
     * and updates the high score. This enhances the player’s total score for finding the hidden bonus word.
     */
    final void addBonusPoints()
    {
        if(!bonusWordCompleted)
        {
            scorePoints += BONUS_POINTS_PER_WORD;
            bonusPoints += BONUS_POINTS_PER_WORD;
            bonusWordCompleted = true;
            System.out.println("Bonus Points: " + scorePoints);
            updateHighScore();
        }
    }

    /**
     * Resets the player’s entire state to initial values, clearing all progress.
     * This method empties the collectedTarget and collectedBonus lists, resets scorePoints,
     * bonusPoints, and incorrectClicksCount to zero, and sets bonusWordCompleted to false.
     * The high score remains unchanged, as it is preserved across resets. Used for a full game restart.
     */
    final void reset()
    {
        collectedTarget.clear();
        collectedBonus.clear();
        scorePoints          = INITIAL_SCORE_POINTS;
        bonusPoints          = INITIAL_SCORE_POINTS;
        incorrectClicksCount = INITIAL_CLICKS_COUNT;
        bonusWordCompleted   = false;
    }

    /**
     * Checks whether the provided list of collected characters exactly matches the given word.
     * This private helper method compares the size of the collected list to the word’s length; if
     * unequal, it returns false. Otherwise, it builds a string from the collected characters and
     * checks for an exact, case-sensitive match with the word. Used by hasCompletedTargetWord() and
     * hasCompletedBonusWord() to verify word completion.
     *
     * @param collectedList the list of Character objects collected by the player
     * @param word          the word string to compare against
     * @return true if the collected characters form the word exactly, false otherwise
     */
    private boolean hasCompletedWord(final List<Character> collectedList,
                                     final String word)
    {
        if(collectedList.size() != word.length())
        {
            return false;
        }

        // Declaring local variable
        final StringBuilder builder;
        builder = new StringBuilder();

        for(final char valueChar : collectedList)
        {
            builder.append(valueChar);
        }

        return builder.toString().equals(word);
    }

    /**
     * Loads the player’s high score from the file specified by HIGH_SCORE_FILE_PATH.
     * This private method reads all score entries using LetterRushScore.readScoresFromFile(), then
     * finds the entry with the latest timestamp using a stream-based max operation with a Comparator.
     * If the file is empty or an IOException occurs, it returns INITIAL_SCORE_POINTS (0). The loaded
     * high score initializes the player’s highScorePoints during construction or updates.
     *
     * @return the high score in points as an integer, or 0 if loading fails
     */
    private int loadHighScore()
    {
        try
        {
            // Declaring local variables
            final List<LetterRushScore> scores;
            final LetterRushScore       latestScore;

            // Loading scores from file
            scores = LetterRushScore.readScoresFromFile(HIGH_SCORE_FILE_PATH);

            if(scores.isEmpty())
            {
                return INITIAL_SCORE_POINTS;
            }

            // Finding latest score
            latestScore = scores.stream().max(Comparator.comparing(LetterRushScore::getTimestamp))
                                .orElse(new LetterRushScore(INITIAL_SCORE_POINTS,
                                                            INITIAL_SCORE_POINTS,
                                                            INITIAL_SCORE_POINTS));

            return latestScore.getHighScore();
        }
        catch(final IOException e)
        {
            System.err.println(ERROR_LOADING_MESSAGE + e.getMessage());
            return INITIAL_SCORE_POINTS;
        }
    }

    /**
     * Saves the current high score to the file specified by HIGH_SCORE_FILE_PATH.
     * This private method creates a new LetterRushScore object with the current highScorePoints,
     * scorePoints, and bonusPoints, then appends it to the file using LetterRushScore.appendScoreToFile().
     * If an IOException occurs, it logs an error message but does not throw an exception, ensuring the
     * game can continue running despite file issues.
     */
    private void saveHighScore()
    {
        try
        {
            // Declaring local variable
            final LetterRushScore scoreEntry;

            // Initializing score entry
            scoreEntry = new LetterRushScore(highScorePoints,
                                             scorePoints,
                                             bonusPoints);

            LetterRushScore.appendScoreToFile(scoreEntry,
                                              HIGH_SCORE_FILE_PATH);
        }
        catch(final IOException e)
        {
            System.err.println(ERROR_SAVING_MESSAGE + e.getMessage());
        }
    }

    /**
     * Awards points to the player for collecting a letter that matches the target word.
     * This private method increments scorePoints by TARGET_POINTS_PER_LETTER, logs the updated score,
     * and calls updateHighScore() to check if the new score exceeds the high score. It is invoked
     * within clickLetter() when a target letter is successfully clicked.
     */
    private void addTargetPoints()
    {
        scorePoints += TARGET_POINTS_PER_LETTER;
        System.out.println("Target Points: " + scorePoints);
        updateHighScore();
    }
}