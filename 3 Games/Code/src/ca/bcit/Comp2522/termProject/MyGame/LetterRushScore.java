package ca.bcit.Comp2522.termProject.MyGame;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * The LetterRushScore class represents a high score entry in the LetterRush game.
 * It stores the high score, current score, bonus score, and timestamp,
 * and provides methods to save and read scores from a file.
 *
 * @author Sehaj Gill
 * @version 1.0
 */
public class LetterRushScore
{
    private static final int    DEFAULT_SCORE_VALUE = 0;
    private static final int    SCORE_ENTRY_LINES   = 4;
    private static final int    SPLIT_LIMIT         = 2;
    private static final int    SPLIT_VALUE_INDEX   = 1;
    private static final int    TIMESTAMP_INDEX     = 0;
    private static final int    HIGH_SCORE_INDEX    = 1;
    private static final int    CURRENT_SCORE_INDEX = 2;
    private static final int    BONUS_SCORE_INDEX   = 3;
    private static final String TIMESTAMP_LABEL     = "Timestamp: ";
    private static final String HIGH_SCORE_LABEL    = "High Score: ";
    private static final String CURRENT_SCORE_LABEL = "Current Score: ";
    private static final String BONUS_SCORE_LABEL   = "Bonus Score: ";
    private static final String DATE_TIME_PATTERN   = "yyyy-MM-dd HH:mm:ss";
    private static final String ERROR_MESSAGE       = "Error: Unable to save score to ";
    private static final String ERROR_READ_MESSAGE  = "Error: Unable to read scores from ";
    private static final String INVALID_FORMAT_MSG  = "Invalid score format. Expected 4 lines, got ";
    private static final String ENTRY_LABEL         = "Entry: ";
    private static final String ERROR_PARSING_MSG   = "Error parsing score: ";
    private static final String ERROR_MESSAGE_PART  = ": ";
    private static final String NEW_LINE            = "\n";
    private static final String SPLIT_COLON         = ":";

    private final int           highScore;
    private final int           currentScore;
    private final int           bonusScore;
    private final LocalDateTime timestamp;

    /**
     * Constructs a new LetterRushScore instance with the specified high score, current score,
     * bonus score, and timestamp.
     *
     * @param highScore    the high score achieved
     * @param currentScore the current score at the time of the high score
     * @param bonusScore   the bonus score at the time of the high score
     * @param timestamp    the timestamp when the high score was achieved
     */
    public LetterRushScore(final int highScore,
                           final int currentScore,
                           final int bonusScore,
                           final LocalDateTime timestamp)
    {
        this.highScore    = highScore;
        this.currentScore = currentScore;
        this.bonusScore   = bonusScore;
        this.timestamp    = timestamp;
    }

    /**
     * Constructs a new LetterRushScore instance with the specified high score, current score,
     * and bonus score, using the current timestamp.
     *
     * @param highScore    the high score achieved
     * @param currentScore the current score at the time of the high score
     * @param bonusScore   the bonus score at the time of the high score
     */
    public LetterRushScore(final int highScore,
                           final int currentScore,
                           final int bonusScore)
    {
        this(highScore,
             currentScore,
             bonusScore,
             LocalDateTime.now());
    }

    /**
     * Returns the high score.
     *
     * @return the high score
     */
    public final int getHighScore()
    {
        return highScore;
    }

    /**
     * Returns the timestamp when the high score was achieved.
     *
     * @return the timestamp
     */
    public final LocalDateTime getTimestamp()
    {
        return timestamp;
    }

    /**
     * Returns a string representation of the score entry, including the timestamp,
     * high score, current score, and bonus score.
     *
     * @return a formatted string representing the score entry
     */
    @Override
    public final String toString()
    {
        final DateTimeFormatter formatter;
        formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

        return String.format(TIMESTAMP_LABEL + "%s" + NEW_LINE + HIGH_SCORE_LABEL
                             + "%d" + NEW_LINE + CURRENT_SCORE_LABEL + "%d"
                             + NEW_LINE + BONUS_SCORE_LABEL + "%d" + NEW_LINE,
                             timestamp.format(formatter),
                             highScore,
                             currentScore,
                             bonusScore);
    }

    /**
     * Appends the specified score entry to the given file.
     *
     * @param score    the LetterRushScore object to save
     * @param filePath the path to the file where the score will be saved
     * @throws IOException if an error occurs while writing to the file
     */
    public static final void appendScoreToFile(final LetterRushScore score,
                                               final String filePath) throws IOException
    {
        final File scoreFile;
        scoreFile = new File(filePath);

        try(final BufferedWriter writer = new BufferedWriter(new FileWriter(scoreFile,
                                                                            true)))
        {
            writer.write(score.toString() + NEW_LINE);
        }
        catch(final IOException e)
        {
            throw new IOException(ERROR_MESSAGE + scoreFile.getAbsolutePath() + ERROR_MESSAGE_PART + e.getMessage());
        }
    }

    /**
     * Reads all score entries from the specified file and returns them as a list.
     * If the file does not exist, returns an empty list.
     *
     * @param filePath the path to the file containing the scores
     * @return a list of LetterRushScore objects read from the file
     * @throws IOException if an error occurs while reading the file
     */
    public static List<LetterRushScore> readScoresFromFile(final String filePath) throws IOException
    {
        final List<LetterRushScore> scoreList;
        scoreList = new ArrayList<>();

        final File scoreFile;
        scoreFile = new File(filePath);

        if(!scoreFile.exists())
        {
            return scoreList;
        }

        try(final BufferedReader reader = Files.newBufferedReader(scoreFile.toPath(),
                                                                  StandardCharsets.UTF_8))
        {
            String        line;
            StringBuilder scoreEntry;
            scoreEntry = new StringBuilder();

            while((line = reader.readLine()) != null)
            {
                if(line.trim().isEmpty() && scoreEntry.length() > DEFAULT_SCORE_VALUE)
                {
                    scoreList.add(fromString(scoreEntry.toString()));
                    scoreEntry.setLength(DEFAULT_SCORE_VALUE);
                }
                else if(!line.trim().isEmpty())
                {
                    scoreEntry.append(line).append(NEW_LINE);
                }
            }

            if(scoreEntry.length() > DEFAULT_SCORE_VALUE)
            {
                scoreList.add(fromString(scoreEntry.toString()));
            }
        }
        catch(final IOException e)
        {
            throw new IOException(ERROR_READ_MESSAGE + filePath + ERROR_MESSAGE_PART + e.getMessage());
        }

        return scoreList;
    }

    /**
     * Parses a string representation of a score entry and returns a corresponding LetterRushScore object.
     * If the string is malformed, logs an error and returns a default LetterRushScore object.
     *
     * @param entry the string representation of the score entry
     * @return a LetterRushScore object parsed from the string, or a default LetterRushScore if parsing fails
     */
    public static final LetterRushScore fromString(final String entry)
    {
        final String[] parts;
        parts = entry.split(NEW_LINE);

        if(parts.length != SCORE_ENTRY_LINES)
        {
            System.out.println(INVALID_FORMAT_MSG + parts.length);
            System.out.println(ENTRY_LABEL + entry);

            return new LetterRushScore(DEFAULT_SCORE_VALUE,
                                       DEFAULT_SCORE_VALUE,
                                       DEFAULT_SCORE_VALUE);
        }

        try
        {
            final DateTimeFormatter formatter;
            formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

            final LocalDateTime timestamp;
            timestamp = LocalDateTime.parse(parts[TIMESTAMP_INDEX].split( SPLIT_COLON,
                                                                         SPLIT_LIMIT)[SPLIT_VALUE_INDEX].trim(),
                                            formatter);

            final int highScore;
            highScore = Integer.parseInt(parts[HIGH_SCORE_INDEX].split( SPLIT_COLON,
                                                                       SPLIT_LIMIT)[SPLIT_VALUE_INDEX].trim());

            final int currentScore;
            currentScore = Integer.parseInt(parts[CURRENT_SCORE_INDEX].split( SPLIT_COLON,
                                                                             SPLIT_LIMIT)[SPLIT_VALUE_INDEX].trim());

            final int bonusScore;
            bonusScore = Integer.parseInt(parts[BONUS_SCORE_INDEX].split( SPLIT_COLON,
                                                                         SPLIT_LIMIT)[SPLIT_VALUE_INDEX].trim());

            return new LetterRushScore(highScore,
                                       currentScore,
                                       bonusScore,
                                       timestamp);
        }
        catch(final Exception e)
        {
            System.out.println(ERROR_PARSING_MSG + e.getMessage());

            return new LetterRushScore(DEFAULT_SCORE_VALUE,
                                       DEFAULT_SCORE_VALUE,
                                       DEFAULT_SCORE_VALUE);
        }
    }
}