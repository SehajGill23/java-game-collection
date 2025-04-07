package ca.bcit.Comp2522.termProject.letterrushgame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Represents a player in the LetterRush game, managing their gameplay state and statistics.
 * This class meticulously tracks various aspects of the player's performance, including their
 * current score, the highest score achieved across all sessions, bonus points earned, the
 * current position of their cursor within the game window (in pixels), the letters they have
 * successfully collected towards forming the target and bonus words, and a count of their
 * incorrect letter selections.
 *
 * <p>The Player class provides a comprehensive set of methods to update these attributes in
 * response to different gameplay events. These include actions such as clicking on letters,
 * successfully completing target or bonus words, and the need to reset the player's progress
 * at the start of a new level.
 *
 * <p>A crucial responsibility of this class is the management of the player's high score.
 * It includes functionality to persistently store the high score to a file, ensuring that the
 * player's best performance is remembered between game sessions. Upon initialization, the
 * Player attempts to load the high score from this file. If loading fails (e.g., the file
 * does not exist or is corrupted), the high score is defaulted to zero.
 *
 * <p>The player's interaction with the game is partly modeled through their cursor, which is
 * treated as a point in the game window with a defined size in pixels. Collision detection
 * with interactive elements (like letters and obstacles) often involves the cursor's position
 * and size.
 *
 * <p>The class also implements logic to evaluate the player's progress against the target
 * and bonus words provided by the game. This evaluation determines if the player has successfully
 * completed a word, which can lead to score increases, bonus rewards, or progression to the
 * next level. Conversely, it also tracks errors (incorrect clicks) which can contribute to
 * a level failure condition.
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
     * Constructs a new {@code Player} object. This constructor initializes the player's
     * gameplay state to its default starting configuration. Specifically:
     * <ul>
     * <li>A new empty {@link ArrayList} is created to store the characters collected
     * towards the target word ({@code collectedTarget}).</li>
     * <li>Similarly, a new empty {@link ArrayList} is created for characters collected
     * towards the bonus word ({@code collectedBonus}).</li>
     * <li>The player's current score ({@code scorePoints}) is initialized to zero
     * ({@link #INITIAL_SCORE_POINTS}).</li>
     * <li>The player's high score ({@code highScorePoints}) is loaded from persistent
     * storage using the {@link #loadHighScore()} method. If no high score is found
     * or an error occurs during loading, it defaults to zero.</li>
     * <li>The initial position of the player's cursor ({@code cursorXPixels} and
     * {@code cursorYPixels}) is set to (0.0, 0.0).</li>
     * <li>The player's bonus points ({@code bonusPoints}) are initialized to zero
     * ({@link #INITIAL_SCORE_POINTS}).</li>
     * <li>The count of incorrect letter clicks ({@code incorrectClicksCount}) is set to zero
     * ({@link #INITIAL_CLICKS_COUNT}).</li>
     * <li>A flag indicating whether the bonus word has been completed ({@code bonusWordCompleted})
     * is set to {@code false}.</li>
     * </ul>
     * This constructor ensures that a new player starts with a clean slate, ready for a new game session
     * with their previously achieved high score loaded.
     */
    public Player()
    {
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
     * Retrieves the number of incorrect letter clicks the player has made during the current
     * level. An incorrect click is recorded when the player selects a letter that is not part
     * of the current target word. This count is a factor in determining if the player has failed
     * the level.
     *
     * @return an integer representing the number of incorrect clicks. This value is reset at the
     * beginning of each new level.
     */
    public final int getIncorrectClicks()
    {
        return incorrectClicksCount;
    }

    /**
     * Retrieves the player's all-time highest score achieved in the game. This score is loaded
     * from persistent storage when the {@code Player} object is created and is updated whenever
     * the player's current score surpasses it. It represents the player's best performance across
     * all game sessions.
     *
     * @return an integer representing the player's high score in points.
     */
    public final int getHighScore()
    {
        return highScorePoints;
    }

    /**
     * Retrieves the player's current score in the ongoing game session. This score is accumulated
     * by correctly clicking letters of the target word and by completing bonus words. It is reset
     * to zero when a new game is started.
     *
     * @return an integer representing the player's current score in points.
     */
    public final int getScore()
    {
        return scorePoints;
    }

    /**
     * Resets the player's progress for the start of a new level. This method clears any letters
     * collected towards the target and bonus words, resets the count of incorrect clicks to zero,
     * and marks the bonus word as not yet completed. Importantly, this method does not reset the
     * player's current score, high score, or bonus points, allowing for score accumulation across
     * multiple levels.
     */
    public final void resetForNewLevel()
    {
        collectedTarget.clear();
        collectedBonus.clear();
        incorrectClicksCount = INITIAL_SCORE_POINTS;
        bonusWordCompleted   = false;
    }

    /**
     * Returns a new {@link List} containing the characters the player has collected so far that
     * match letters in the target word. The returned list is a copy, ensuring that external
     * modifications do not affect the player's internal state.
     *
     * @return a new {@link List} of {@link Character} objects representing the collected target letters.
     * The order of characters in the list reflects the order in which they were collected.
     */
    public final List<Character> getCollectedTarget()
    {
        final List<Character> copy;
        copy = new ArrayList<>(collectedTarget);

        return copy;
    }

    /**
     * Sets the player's bonus points to a specified value. This method directly updates the
     * {@code bonusPoints} attribute and then calls {@link #updateHighScore()} to check if the
     * total score (current score + bonus points) now exceeds the current high score, potentially
     * updating it in persistent storage. This method is typically used when the game state needs
     * to be restored or when bonus points are awarded outside the normal bonus word completion process.
     *
     * @param bonusPointsCount the new value for the player's bonus points.
     */
    public final void setBonusPoints(final int bonusPointsCount)
    {
        this.bonusPoints = bonusPointsCount;
        updateHighScore();
    }

    /**
     * Sets the player's current score to a specified value. This method directly updates the
     * {@code scorePoints} attribute and then calls {@link #updateHighScore()} to check if the
     * new score exceeds the current high score, potentially updating it in persistent storage.
     * This method is used when the game state needs to be restored or when the score is adjusted
     * outside the normal gameplay events (e.g., for testing purposes).
     *
     * @param scorePointsCount the new value for the player's current score.
     */
    public final void setScore(final int scorePointsCount)
    {
        this.scorePoints = scorePointsCount;
        updateHighScore();
    }

    /**
     * Updates the player's high score if their current score (including any accumulated bonus points)
     * is greater than the currently recorded high score. If an update occurs, the new high score is
     * also saved to persistent storage using the {@link #saveHighScore()} method. This ensures that
     * the player's best performance is tracked and saved across game sessions.
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
     * Checks if the player has successfully completed the target word by comparing the letters
     * they have collected ({@code collectedTarget}) with the letters of the {@code targetWord}.
     * Completion is determined by an exact, case-sensitive match in both the sequence and the
     * number of characters. This method delegates the actual comparison to the private helper
     * method {@link #hasCompletedWord(List, String)}.
     *
     * @param targetWord the target word string to check against the collected letters.
     * @return {@code true} if the collected target letters exactly form the {@code targetWord},
     * {@code false} otherwise.
     */
    public final boolean hasCompletedTargetWord(final String targetWord)
    {
        return hasCompletedWord(collectedTarget,
                                targetWord);
    }

    /**
     * Checks if the player has successfully completed the bonus word by comparing the letters
     * they have collected ({@code collectedBonus}) with the letters of the {@code bonusWord}.
     * Completion requires an exact, case-sensitive match in both the sequence and the number
     * of characters. This method delegates the comparison to the private helper method
     * {@link #hasCompletedWord(List, String)}.
     *
     * @param bonusWord the bonus word string to check against the collected bonus letters.
     * @return {@code true} if the collected bonus letters exactly form the {@code bonusWord},
     * {@code false} otherwise.
     */
    public final boolean hasCompletedBonusWord(final String bonusWord)
    {
        return hasCompletedWord(collectedBonus,
                                bonusWord);
    }

    /**
     * Processes a click event on a letter in the game. This method updates the player's state
     * based on whether the clicked letter belongs to the current target word or the bonus word.
     *
     * <p>First, it checks if the clicked {@link Letter} is already locked (i.e., previously clicked).
     * If it is, the method returns immediately, preventing the same letter from being processed multiple times.
     *
     * <p>If the letter is not locked, it is immediately locked to prevent further interaction. The
     * character value of the clicked letter is then retrieved.
     *
     * <p>The clicked letter's value is checked against the {@code targetWord}. If it is found within
     * the {@code targetWord}, the character is added to the {@code collectedTarget} list, and the
     * player's score is increased by {@link #TARGET_POINTS_PER_LETTER} via the {@link #addTargetPoints()}
     * method. If the clicked letter is not in the {@code targetWord}, the {@code incorrectClicksCount}
     * is incremented.
     *
     * <p>Similarly, the clicked letter's value is checked against the {@code bonusWord}. If it is found,
     * the character is added to the {@code collectedBonus} list.
     *
     * <p>For debugging and feedback purposes, the method prints information about the clicked letter
     * and the current state of the collected target and bonus letters to the console.
     *
     * <p>Finally, the method checks if the player has now completed the {@code bonusWord} using the
     * {@link #hasCompletedBonusWord(String)} method. If the bonus word is completed, the player is
     * awarded bonus points via the {@link #addBonusPoints()} method.
     *
     * @param letter      the {@link Letter} object that was clicked by the player.
     * @param targetWord  the current target word for the level.
     * @param bonusWord   the current bonus word for the level.
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
     * Determines if the player has failed the current level based on several criteria:
     * <ul>
     * <li>If the number of correctly collected letters for the target word exceeds the length
     * of the target word.</li>
     * <li>If the number of correctly collected letters for the bonus word exceeds the length
     * of the bonus word.</li>
     * <li>If the number of incorrect letter clicks made by the player is equal to or greater
     * than the length of either the target word or the bonus word.</li>
     * </ul>
     *
     * <p>The method also checks if the player has successfully completed either the target word or
     * the bonus word. If either is completed, the player is considered not to have failed (returns {@code false}),
     * regardless of the other failure conditions. This is because completing a word typically signifies
     * success, even if errors were made along the way.
     *
     * <p>For debugging purposes, if a failure condition is met (and neither word is completed), a
     * message indicating the reason for failure is printed to the console.
     *
     * @param targetWord the target word for the current level.
     * @param bonusWord  the bonus word for the current level.
     * @return {@code true} if the player has failed the level according to the defined criteria,
     * {@code false} otherwise.
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

        final StringBuilder collectedBuilder;
        collectedBuilder = new StringBuilder();

        for(final char valueChar : collectedTarget)
        {
            collectedBuilder.append(valueChar);
        }
        return false;
    }

    /*
     * Updates the current position of the player's cursor within the game window.
     * The cursor's position is represented by its x and y coordinates in pixels.
     * This method is typically called in response to mouse movement events, allowing
     * the game to track the player's interaction with on-screen elements.
     *
     * @param xPixels the new x-coordinate of the cursor in pixels.
     * @param yPixels the new y-coordinate of the cursor in pixels.
     */
    final void updateCursorPosition(final double xPixels,
                                    final double yPixels)
    {
        cursorXPixels = xPixels;
        cursorYPixels = yPixels;
    }

    /*
     * Retrieves the current y-coordinate of the player's cursor in pixels.
     * This value represents the vertical position of the cursor within the game window.
     *
     * @return a double representing the cursor's current y-coordinate in pixels.
     */
    final double getCursorX()
    {
        return cursorXPixels;
    }

    /*
     * Retrieves the current y-coordinate of the player's cursor in pixels.
     * This value represents the vertical position of the cursor within the game window.
     *
     * @return a double representing the cursor's current y-coordinate in pixels.
     */
    final double getCursorY()
    {
        return cursorYPixels;
    }

    /*
     * Returns the size of the player's cursor in pixels. The cursor is treated as a
     * square with sides of this length. This size is used in collision detection
     * with other game elements, such as letters and obstacles.
     *
     * @return a double representing the size (width and height) of the cursor in pixels.
     */
    final double getCursorSize()
    {
        return CURSOR_SIZE_PIXELS;
    }

    /*
     * Retrieves the total bonus points accumulated by the player during the current
     * game session. Bonus points are typically awarded for completing bonus words
     * and are added to the player's overall score.
     *
     * @return an integer representing the player's current bonus points.
     */
    final int getBonusPoints()
    {
        return bonusPoints;
    }

    /*
     * Awards bonus points to the player for successfully completing a bonus word.
     * This method adds a predefined number of points ({@link #BONUS_POINTS_PER_WORD})
     * to both the player's current score and their bonus points total. To prevent
     * awarding bonus points multiple times for the same bonus word completion, it
     * checks a flag ({@code bonusWordCompleted}). If the bonus word has not been
     * completed yet, the points are awarded, the flag is set to {@code true}, the
     * new score is logged to the console, and the high score is updated.
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

    /*
     * Resets the player's state to its initial configuration, effectively starting
     * a new game. This method clears all collected target and bonus letters, resets
     * the current score, bonus points, and incorrect click count to zero, and marks
     * the bonus word as not completed. The high score achieved in previous sessions
     * is preserved and is not reset by this method.
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

    /*
     * Checks if a given list of collected characters exactly forms a specified word.
     * The comparison is case-sensitive, and the collected list must have the same
     * number of characters as the word.
     *
     * @param collectedList a {@link List} of {@link Character} objects collected by the player.
     * @param word          the {@link String} representing the word to check against.
     * @return {@code true} if the collected characters, in order, form the exact word,
     * {@code false} otherwise.
     */
    private boolean hasCompletedWord(final List<Character> collectedList,
                                     final String word)
    {
        if(collectedList.size() != word.length())
        {
            return false;
        }

        final StringBuilder builder;
        builder = new StringBuilder();

        for(final char valueChar : collectedList)
        {
            builder.append(valueChar);
        }

        return builder.toString().equals(word);
    }

    /*
     * Loads the player's high score from persistent storage. This method reads all
     * recorded scores from the file specified by {@link #HIGH_SCORE_FILE_PATH} using
     * the {@link LetterRushScore#readScoresFromFile(String)} method. It then determines
     * the highest score among all loaded entries, typically by finding the score with
     * the most recent timestamp. If no scores are found in the file or if an
     * {@link IOException} occurs during the reading process, the method returns the
     * initial score value ({@link #INITIAL_SCORE_POINTS}), effectively defaulting to zero.
     *
     * @return an integer representing the player's high score loaded from the file,
     * or {@link #INITIAL_SCORE_POINTS} if loading fails or no scores are found.
     */
    private int loadHighScore()
    {
        try
        {
            final List<LetterRushScore> scores;
            final LetterRushScore       latestScore;

            scores = LetterRushScore.readScoresFromFile(HIGH_SCORE_FILE_PATH);

            if(scores.isEmpty())
            {
                return INITIAL_SCORE_POINTS;
            }

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

    /*
     * Saves the player's current high score to persistent storage. This method creates
     * a new {@link LetterRushScore} object containing the current high score, the player's
     * current score, and their bonus points. It then appends this score entry to the file
     * specified by {@link #HIGH_SCORE_FILE_PATH} using the
     * {@link LetterRushScore#appendScoreToFile(LetterRushScore, String)} method. If an
     * {@link IOException} occurs during the saving process, an error message is printed
     * to the standard error stream, but the exception is not re-thrown, ensuring the
     * game can continue to run even if saving the high score fails.
     */
    private void saveHighScore()
    {
        try
        {
            final LetterRushScore scoreEntry;
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

    /*
     * Awards points to the player for correctly clicking a letter that belongs to
     * the target word. This method increments the player's current score ({@code scorePoints})
     * by a predefined number of points per letter ({@link #TARGET_POINTS_PER_LETTER}).
     * It also logs the updated score to the console for feedback and calls the
     * {@link #updateHighScore()} method to check if the new score has surpassed the
     * current high score.
     */
    private void addTargetPoints()
    {
        scorePoints += TARGET_POINTS_PER_LETTER;
        System.out.println("Target Points: " + scorePoints);
        updateHighScore();
    }
}