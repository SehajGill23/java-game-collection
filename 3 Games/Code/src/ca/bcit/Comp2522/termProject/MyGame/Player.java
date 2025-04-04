package ca.bcit.Comp2522.termProject.MyGame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * The Player class represents a player in the LetterRush game.
 * It manages the player's score, high score, cursor position, collected letters,
 * bonus points, and incorrect clicks, and handles high score persistence.
 *
 * @version 1.0
 */
public class Player
{
    private static final int    INITIAL_SCORE_VALUE     = 0;
    private static final int    TARGET_POINTS           = 10;
    private static final int    BONUS_POINTS            = 30;
    private static final int    MAX_INCORRECT_CLICKS    = 1;
    private static final double CURSOR_SIZE             = 10.0;
    private static final String HIGH_SCORE_FILE_PATH    = "Resources/highScore.txt";
    private static final String ERROR_LOADING_MESSAGE   = "Error loading high score: ";
    private static final String ERROR_SAVING_MESSAGE    = "Error saving high score: ";
    private static final String CLICKED_LETTER_MESSAGE  = "Clicked letter: ";
    private static final String TARGET_SO_FAR_MESSAGE   = " | Target so far: ";
    private static final String BONUS_SO_FAR_MESSAGE    = " | Bonus so far: ";
    private static final String HAS_FAILED_MESSAGE      = "hasFailed: ";
    private static final String TOO_MANY_LETTERS_MSG    = "Too many letters clicked: ";
    private static final String GREATER_THAN_MESSAGE    = " > ";
    private static final String TARGET_COMPLETED_MSG    = "Target word completed, returning false.";
    private static final String TOO_MANY_CLICKS_MSG     = "Too many incorrect clicks: ";
    private static final String COLLECTED_MESSAGE       = "Collected: ";
    private static final String TARGET_MESSAGE          = ", Target: ";
    private static final String STARTS_WITH_MESSAGE     = ", Starts with: ";

    private int                 score;
    private int                 highScore;
    private double              cursorX;
    private double              cursorY;
    private List<Character>     collectedTarget;
    private List<Character>     collectedBonus;
    private int                 bonusPoints;
    private int                 incorrectClicks;

    /**
     * Constructs a new Player instance, initializing scores, collected letters,
     * and loading the high score from a file.
     */
    public Player()
    {
        score           = INITIAL_SCORE_VALUE;
        highScore       = loadHighScore();
        collectedTarget = new ArrayList<>();
        collectedBonus  = new ArrayList<>();
        bonusPoints     = INITIAL_SCORE_VALUE;
        incorrectClicks = INITIAL_SCORE_VALUE;
    }

    /**
     * Updates the player's cursor position.
     *
     * @param x the x-coordinate of the cursor
     * @param y the y-coordinate of the cursor
     */
    public final void updateCursorPosition(final double x,
                                           final double y)
    {
        cursorX = x;
        cursorY = y;
    }

    /**
     * Resets the player's state for a new level, clearing collected letters
     * and incorrect clicks, but preserving cumulative scores.
     */
    public final void resetForNewLevel()
    {
        collectedTarget.clear();
        collectedBonus.clear();
        incorrectClicks = INITIAL_SCORE_VALUE;
        // Note: score and bonusPoints are NOT reset here to maintain cumulative scoring
    }

    /**
     * Returns a copy of the collected target letters.
     *
     * @return a list of characters collected for the target word
     */
    public final List<Character> getCollectedTarget()
    {
        final List<Character> copy;
        copy = new ArrayList<>(collectedTarget);

        return copy;
    }

    /**
     * Returns the x-coordinate of the player's cursor.
     *
     * @return the cursor's x-coordinate
     */
    public final double getCursorX()
    {
        return cursorX;
    }

    /**
     * Returns the y-coordinate of the player's cursor.
     *
     * @return the cursor's y-coordinate
     */
    public final double getCursorY()
    {
        return cursorY;
    }

    /**
     * Returns the size of the player's cursor.
     *
     * @return the cursor size
     */
    public final double getCursorSize()
    {
        return CURSOR_SIZE;
    }

    /**
     * Sets the player's bonus points.
     *
     * @param bonusPoints the bonus points to set
     */
    public final void setBonusPoints(final int bonusPoints)
    {
        this.bonusPoints = bonusPoints;
    }

    /**
     * Sets the player's score and updates the high score if necessary.
     *
     * @param score the score to set
     */
    public final void setScore(final int score)
    {
        this.score = score;
        updateHighScore();
    }

    /**
     * Handles a letter click, updating collected letters, scores, and incorrect clicks
     * based on the target and bonus words.
     *
     * @param letter     the letter clicked
     * @param targetWord the target word to match
     * @param bonusWord  the bonus word to match
     */
    public final void clickLetter(final Letter letter,
                                  final String targetWord,
                                  final String bonusWord)
    {
        if (letter.isLocked())
        {
            return;
        }

        final char c;
        c = letter.getValue();

        letter.lock();

        if (targetWord.contains(String.valueOf(c)))
        {
            final StringBuilder collected;
            collected = new StringBuilder();

            for (final char ch : collectedTarget)
            {
                collected.append(ch);
            }

            collected.append(c);

            final boolean isCorrectClick;
            isCorrectClick = targetWord.startsWith(collected.toString());

            if (isCorrectClick)
            {
                collectedTarget.add(c);
                addTargetPoints();
            }
            else
            {
                incorrectClicks++;
            }
        }

        if (bonusWord.contains(String.valueOf(c)))
        {
            collectedBonus.add(c);
        }

        System.out.println(CLICKED_LETTER_MESSAGE + c +
                           TARGET_SO_FAR_MESSAGE + collectedTarget +
                           BONUS_SO_FAR_MESSAGE + collectedBonus);
    }

    /**
     * Checks if the player has completed the target word.
     *
     * @param targetWord the target word to match
     * @return true if the target word is completed, false otherwise
     */
    public final boolean hasCompletedTargetWord(final String targetWord)
    {
        if (collectedTarget.size() != targetWord.length())
        {
            return false;
        }

        final StringBuilder collected;
        collected = new StringBuilder();

        for (final char c : collectedTarget)
        {
            collected.append(c);
        }

        return collected.toString().equals(targetWord);
    }

    /**
     * Checks if the player has completed the bonus word.
     *
     * @param bonusWord the bonus word to match
     * @return true if the bonus word is completed, false otherwise
     */
    public final boolean hasCompletedBonusWord(final String bonusWord)
    {
        if (collectedBonus.size() != bonusWord.length())
        {
            return false;
        }

        final StringBuilder collected;
        collected = new StringBuilder();

        for (final char c : collectedBonus)
        {
            collected.append(c);
        }

        return collected.toString().equals(bonusWord);
    }

    /**
     * Loads the high score from a file.
     *
     * @return the high score, or 0 if loading fails
     */
    private int loadHighScore()
    {
        try
        {
            final List<LetterRushScore> scores;
            scores = LetterRushScore.readScoresFromFile(HIGH_SCORE_FILE_PATH);

            if (scores.isEmpty())
            {
                return INITIAL_SCORE_VALUE;
            }

            // Get the most recent high score
            final LetterRushScore latestScore;
            latestScore = scores.stream()
                                .max(Comparator.comparing(LetterRushScore::getTimestamp))
                                .orElse(new LetterRushScore(INITIAL_SCORE_VALUE,
                                                            INITIAL_SCORE_VALUE,
                                                            INITIAL_SCORE_VALUE));

            return latestScore.getHighScore();
        }
        catch (final IOException e)
        {
            System.err.println(ERROR_LOADING_MESSAGE + e.getMessage());
            return INITIAL_SCORE_VALUE;
        }
    }

    /**
     * Saves the current high score to a file.
     */
    private void saveHighScore()
    {
        try
        {
            final LetterRushScore scoreEntry;
            scoreEntry = new LetterRushScore(highScore,
                                             score,
                                             bonusPoints);

            LetterRushScore.appendScoreToFile(scoreEntry,
                                              HIGH_SCORE_FILE_PATH);
        }
        catch (final IOException e)
        {
            System.err.println(ERROR_SAVING_MESSAGE + e.getMessage());
        }
    }

    /**
     * Updates the high score if the current score is higher.
     */
    private void updateHighScore()
    {
        if (score > highScore)
        {
            highScore = score;
            saveHighScore();
        }
    }

    /**
     * Checks if the player has failed the level based on collected letters
     * and incorrect clicks.
     *
     * @param targetWord the target word to match
     * @return true if the player has failed, false otherwise
     */
    public final boolean hasFailed(final String targetWord)
    {
        if (collectedTarget.size() > targetWord.length())
        {
            System.out.println(HAS_FAILED_MESSAGE + TOO_MANY_LETTERS_MSG +
                               collectedTarget.size() + GREATER_THAN_MESSAGE +
                               targetWord.length());
            return true;
        }

        if (hasCompletedTargetWord(targetWord))
        {
            System.out.println(HAS_FAILED_MESSAGE + TARGET_COMPLETED_MSG);
            return false;
        }

        if (incorrectClicks > MAX_INCORRECT_CLICKS)
        {
            System.out.println(HAS_FAILED_MESSAGE + TOO_MANY_CLICKS_MSG +
                               incorrectClicks);
            return true;
        }

        final StringBuilder collected;
        collected = new StringBuilder();

        for (final char c : collectedTarget)
        {
            collected.append(c);
        }

        final boolean startsWith;
        startsWith = targetWord.startsWith(collected.toString());

        System.out.println(HAS_FAILED_MESSAGE + COLLECTED_MESSAGE +
                           collected.toString() + TARGET_MESSAGE +
                           targetWord + STARTS_WITH_MESSAGE + startsWith);

        return !startsWith;
    }

    /**
     * Returns the number of incorrect clicks made by the player.
     *
     * @return the number of incorrect clicks
     */
    public final int getIncorrectClicks()
    {
        return incorrectClicks;
    }

    /**
     * Returns the player's high score.
     *
     * @return the high score
     */
    public final int getHighScore()
    {
        return highScore;
    }

    /**
     * Returns the player's current score.
     *
     * @return the current score
     */
    public final int getScore()
    {
        return score;
    }

    /**
     * Returns the player's bonus points.
     *
     * @return the bonus points
     */
    public final int getBonusPoints()
    {
        return bonusPoints;
    }

    /**
     * Adds points for collecting a target letter.
     */
    public final void addTargetPoints()
    {
        score += TARGET_POINTS;
        updateHighScore();
    }

    /**
     * Adds points for completing a bonus word.
     */
    public final void addBonusPoints()
    {
        score += BONUS_POINTS;
        bonusPoints += BONUS_POINTS;
        updateHighScore();
    }

    /**
     * Resets the player's state, clearing all scores and collected letters.
     */
    public final void reset()
    {
        collectedTarget.clear();
        collectedBonus.clear();
        score           = INITIAL_SCORE_VALUE;
        bonusPoints     = INITIAL_SCORE_VALUE;
        incorrectClicks = INITIAL_SCORE_VALUE;
    }
}