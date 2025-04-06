package ca.bcit.Comp2522.termProject.LetterRushGame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * The Player class represents a player in the LetterRush game.
 * It manages the player's score, high score, cursor position, collected letters,
 * bonus points, and incorrect clicks, and handles high score persistence.
 *
 * @author Sehaj Gill
 * @version 1.0
 */
public class Player
{
    private static final int    INITIAL_CLICKS          = 0;
    private static final int    INITIAL_SCORE_VALUE     = 0;
    private static final int    TARGET_POINTS           = 10;
    private static final int    BONUS_POINTS            = 20;
    private static final double CURSOR_SIZE_PIXELS       = 10.0;
    private static final String HIGH_SCORE_FILE_PATH    = "Resources/highScore.txt";
    private static final String ERROR_LOADING_MESSAGE   = "Error loading high score: ";
    private static final String ERROR_SAVING_MESSAGE    = "Error saving high score: ";
    private static final String CLICKED_LETTER_MESSAGE  = "Clicked letter: ";
    private static final String TARGET_SO_FAR_MESSAGE   = " | Collected Target so far: ";
    private static final String BONUS_SO_FAR_MESSAGE    = " | Collected Bonus so far: ";
    private static final String HAS_FAILED_MESSAGE      = "hasFailed: ";
    private static final String TOO_MANY_LETTERS_MSG    = "Too many letters clicked: ";
    private static final String GREATER_THAN_MESSAGE    = " > ";
    private static final String TARGET_COMPLETED_MSG    = "Target word completed, returning false.";
    private static final String TOO_MANY_CLICKS_MSG     = "Too many incorrect clicks: ";

    private int                 score;
    private int                 highScore;
    private double              cursorX;
    private double              cursorY;
    private List<Character>     collectedTarget;
    private List<Character>     collectedBonus;
    private int                 bonusPoints;
    private int                 incorrectClicks;
    private boolean bonusWordCompleted;

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
        incorrectClicks = INITIAL_CLICKS;
        bonusWordCompleted = false;
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
     * Resets the player's state for a new level, clearing collected letters
     * and incorrect clicks, but preserving cumulative scores.
     */
    public final void resetForNewLevel()
    {
        collectedTarget.clear();
        collectedBonus.clear();
        incorrectClicks = INITIAL_SCORE_VALUE;
        bonusWordCompleted = false;
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
     * Sets the player's bonus points.
     *
     * @param bonusPoints the bonus points to set
     */
    public final void setBonusPoints(final int bonusPoints)
    {
        this.bonusPoints = bonusPoints;
        updateHighScore();
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
     * Updates the high score if the current score is higher.
     */
    public void updateHighScore()
    {
        if (score > highScore)
        {
            highScore = score;
            saveHighScore();
        }
    }

    /**
     * Checks if the player has completed the target word by matching the collected characters.
     *
     * @param targetWord the target word to match
     * @return true if the target word is completed, false otherwise
     */
    public final boolean hasCompletedTargetWord(final String targetWord)
    {
        return hasCompletedWord(collectedTarget, targetWord);
    }

    /**
     * Checks if the player has completed the bonus word by matching the collected characters.
     *
     * @param bonusWord the bonus word to match
     * @return true if the bonus word is completed, false otherwise
     */
    public final boolean hasCompletedBonusWord(final String bonusWord)
    {
        return hasCompletedWord(collectedBonus, bonusWord);
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
            collectedTarget.add(c);
            addTargetPoints();
        }
        else
        {
            incorrectClicks++;
        }

        if (bonusWord.contains(String.valueOf(c)))
        {
            collectedBonus.add(c);
        }

        System.out.println(CLICKED_LETTER_MESSAGE + c +
                           TARGET_SO_FAR_MESSAGE + collectedTarget +
                           BONUS_SO_FAR_MESSAGE + collectedBonus);

        if (hasCompletedBonusWord(bonusWord))
        {
            System.out.println("Bonus word completed!");
            addBonusPoints();
        }
    }

    /**
     * Checks if the player has failed the level based on collected letters
     * and incorrect clicks.
     *
     * @param targetWord the target word to match
     * @param bonusWord
     * @return true if the player has failed, false otherwise
     */
    public final boolean hasFailed(final String targetWord,
                                   final String bonusWord)
    {
        if (collectedTarget.size() > targetWord.length() || collectedBonus.size() > bonusWord.length())
        {
            System.out.println(HAS_FAILED_MESSAGE + TOO_MANY_LETTERS_MSG +
                               collectedTarget.size() + GREATER_THAN_MESSAGE +
                               targetWord.length());
            return true;
        }

        if (hasCompletedTargetWord(targetWord) || hasCompletedBonusWord(bonusWord))
        {
            System.out.println(HAS_FAILED_MESSAGE + TARGET_COMPLETED_MSG);
            return false;
        }

        if (incorrectClicks >= targetWord.length() || incorrectClicks >= bonusWord.length())
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
        return false;
    }

    /*
     * Updates the player's cursor position.
     *
     * @param x the x-coordinate of the cursor
     * @param y the y-coordinate of the cursor
     */
    final void updateCursorPosition(final double x,
                                    final double y)
    {
        cursorX = x;
        cursorY = y;
    }

    /*
     * Returns the x-coordinate of the player's cursor.
     *
     * @return the cursor's x-coordinate
     */
    final double getCursorX()
    {
        return cursorX;
    }

    /*
     * Returns the y-coordinate of the player's cursor.
     *
     * @return the cursor's y-coordinate
     */
    final double getCursorY()
    {
        return cursorY;
    }

    /*
     * Returns the size of the player's cursor.
     *
     * @return the cursor size
     */
    final double getCursorSize()
    {
        return CURSOR_SIZE_PIXELS;
    }

    /*
     * Returns the player's bonus points.
     *
     * @return the bonus points
     */
    final int getBonusPoints()
    {
        return bonusPoints;
    }

    /*
     * Adds points for completing a bonus word.
     */
    final void addBonusPoints()
    {
        if (!bonusWordCompleted)
        {
            score += BONUS_POINTS;
            bonusPoints += BONUS_POINTS;
            bonusWordCompleted = true;
            System.out.println("Bonus Points: " + score);
            updateHighScore();
        }

    }

    /*
     * Resets the player's state, clearing all scores and collected letters.
     */
    final void reset()
    {
        collectedTarget.clear();
        collectedBonus.clear();
        score           = INITIAL_SCORE_VALUE;
        bonusPoints     = INITIAL_SCORE_VALUE;
        incorrectClicks = INITIAL_SCORE_VALUE;
        bonusWordCompleted = false;
    }

    /*
     * Checks whether the collected characters match the given word exactly.
     *
     * @param collected the list of collected characters
     * @param word the word to compare against
     * @return true if the collected characters form the word exactly, false otherwise
     */
    private boolean hasCompletedWord(final List<Character> collected, final String word)
    {
        if (collected.size() != word.length())
        {
            return false;
        }

        final StringBuilder builder = new StringBuilder();
        for (final char c : collected)
        {
            builder.append(c);
        }

        return builder.toString().equals(word);
    }

    /*
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

    /*
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

    /*
     * Adds points for collecting a target letter.
     */
    private void addTargetPoints()
    {
        score += TARGET_POINTS;
        System.out.println("Target Points: " + score);
        updateHighScore();
    }

}