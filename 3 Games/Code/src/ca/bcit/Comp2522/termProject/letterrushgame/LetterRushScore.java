package ca.bcit.Comp2522.termProject.letterrushgame;

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
 * The {@code LetterRushScore} class represents a high score entry in the
 * LetterRush game. It encapsulates the high score achieved, the current
 * score at the time the high score was recorded, the bonus score earned,
 * and the timestamp indicating when the score was achieved. This class
 * also provides static methods for reading and writing score entries to
 * a persistent storage file, allowing the game to maintain a record of
 * player achievements across sessions. Each score entry is formatted
 * with labels for clarity when stored in the file.
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

    /*
     * Constructs a new {@code LetterRushScore} instance with the specified
     * high score, current score, bonus score, and timestamp. This private
     * constructor is used internally when creating score objects, typically
     * from data read from persistent storage or when a new score object is
     * programmatically created with a specific timestamp. It directly initializes
     * the immutable fields of the {@code LetterRushScore} object with the
     * provided values.
     *
     * <p>This constructor assumes that the provided score values and timestamp
     * are valid and does not perform any explicit validation on the input.
     * It is the responsibility of the calling code to ensure the integrity
     * of the data being used to create a {@code LetterRushScore} object.
     * Negative score values, while potentially illogical in the context of
     * a scoring system, will be accepted and stored. Similarly, {@code null}
     * for the {@code timestamp} argument should be avoided as the class relies
     * on a valid timestamp for its functionality, particularly for the
     * {@link #toString()} method.
     *
     * @param highScore    the high score achieved by the player. This value
     * represents the peak score attained.
     * @param currentScore the current score of the player at the precise moment
     * when the {@code highScore} was recorded. This can
     * provide context about the game state at the time
     * of the high score.
     * @param bonusScore   the total bonus score accumulated by the player up to
     * the point when the {@code highScore} was achieved.
     * Bonus scores are typically awarded for special
     * achievements within the game.
     * @param timestamp    the exact date and time when the {@code highScore}
     * was achieved. This is recorded as a {@link LocalDateTime}
     * object, providing a precise record of when the score
     * was attained.
     */
    private LetterRushScore(final int highScore,
                            final int currentScore,
                            final int bonusScore,
                            final LocalDateTime timestamp)
    {
        this.highScore    = highScore;
        this.currentScore = currentScore;
        this.bonusScore   = bonusScore;
        this.timestamp    = timestamp;
    }

    /*
     * Constructs a new {@code LetterRushScore} instance with the specified
     * high score, current score, and bonus score. This constructor is typically
     * used when a new high score is achieved during gameplay and needs to be
     * recorded. It automatically captures the current system date and time as
     * the timestamp for this score entry.
     *
     * <p>Similar to the private constructor, this constructor assumes the
     * validity of the provided score values and does not perform any input
     * validation. Negative score values will be accepted. The timestamp is
     * automatically generated using {@link LocalDateTime#now()} at the moment
     * of object creation, ensuring an accurate record of when the score was
     * achieved.
     *
     * @param highScore    the high score achieved by the player. This value
     * represents the peak score attained.
     * @param currentScore the current score of the player at the precise moment
     * when the {@code highScore} is being recorded.
     * @param bonusScore   the total bonus score accumulated by the player up to
     * the point when the {@code highScore} is being recorded.
     */
    LetterRushScore(final int highScore,
                    final int currentScore,
                    final int bonusScore)
    {
        this(highScore,
             currentScore,
             bonusScore,
             LocalDateTime.now());
    }



    /**
     * Reads all score entries from the specified file path and returns them as
     * a {@link List} of {@code LetterRushScore} objects. If the file does not
     * exist at the given path, this method returns an empty list without throwing
     * an exception. Each score entry in the file is expected to be formatted
     * across four lines, representing the timestamp, high score, current score,
     * and bonus score, each preceded by a label and a colon. Empty lines in
     * the file are used as delimiters between score entries.
     *
     * <p>The method attempts to open and read the file specified by {@code filePath}.
     * It uses a {@link BufferedReader} for efficient reading of lines. Each score
     * entry is assumed to span four consecutive non-empty lines in the file. An
     * empty line is treated as a separator between individual score entries.
     *
     * <p>For each potential score entry (a sequence of four non-empty lines), the
     * method calls the {@link #fromString(String)} method to parse the string
     * representation into a {@code LetterRushScore} object. If the file does not
     * exist, the method gracefully returns an empty list, indicating that no scores
     * could be read.
     *
     * <p><b>File Format:</b> Each score entry in the file should adhere to the
     * following four-line format:
     * <pre>
     * Timestamp: yyyy-MM-dd HH:mm:ss
     * High Score: value
     * Current Score: value
     * Bonus Score: value
     * </pre>
     * Where {@code yyyy-MM-dd HH:mm:ss} is the timestamp in the format defined by
     * {@code DATE_TIME_PATTERN}, and {@code value} is an integer representing the
     * respective score. An empty line should follow each complete score entry to
     * separate it from the next.
     *
     * @param filePath the path to the file containing the saved LetterRush scores.
     * This path should be a valid file location accessible by the
     * application.
     * @return a {@link List} of {@code LetterRushScore} objects read from the
     * file. Returns an empty list if the file does not exist or if no valid
     * score entries are found.
     * @throws IOException if an error occurs while attempting to read from the
     * file (e.g., due to insufficient permissions or the file being corrupted
     * during the read operation). The exception message will provide details
     * about the error and the file path.
     *
     * <p><b>Error Handling:</b> If an {@link IOException} occurs during file
     * reading, it is caught, and a new {@link IOException} with a more specific
     * error message (including the file path and the original exception's message)
     * is thrown to the caller. Malformed score entries within the file will be
     * handled by the {@link #fromString(String)} method, which logs an error and
     * returns a default {@code LetterRushScore} object for that entry, allowing
     * the reading process to continue.
     */
    public static List<LetterRushScore> readScoresFromFile(final String filePath) throws IOException
    {
        final List<LetterRushScore> scoreList = new ArrayList<>();
        final File scoreFile = new File(filePath);

        if (!scoreFile.exists())
        {
            return scoreList;
        }

        try (final BufferedReader reader = Files.newBufferedReader(scoreFile.toPath(),
                                                                   StandardCharsets.UTF_8))
        {
            String line;
            final StringBuilder scoreEntry = new StringBuilder();

            while ((line = reader.readLine()) != null)
            {
                if (line.trim().isEmpty() && !scoreEntry.isEmpty())
                {
                    final LetterRushScore score = fromString(scoreEntry.toString());
                    if (score != null)
                    {
                        scoreList.add(score);
                    }
                    scoreEntry.setLength(DEFAULT_SCORE_VALUE);
                }
                else if (!line.trim().isEmpty())
                {
                    scoreEntry.append(line).append(NEW_LINE);
                }
            }

            if (!scoreEntry.isEmpty())
            {
                final LetterRushScore score = fromString(scoreEntry.toString());
                if (score != null)
                {
                    scoreList.add(score);
                }
            }
        }
        catch (final IOException e)
        {
            throw new IOException(ERROR_READ_MESSAGE + filePath + ERROR_MESSAGE_PART + e.getMessage());
        }

        return scoreList;
    }

    /**
     * Returns a formatted string representation of the score entry. The string
     * includes the timestamp, high score, current score, and bonus score, each
     * on a new line and preceded by a descriptive label. The timestamp is
     * formatted according to the {@code DATE_TIME_PATTERN}. This method is
     * primarily used when saving a {@code LetterRushScore} object to a file.
     *
     * @return a string representation of the score entry, suitable for file storage
     * and human readability. The format is:
     * <pre>
     * Timestamp: yyyy-MM-dd HH:mm:ss
     * High Score: value
     * Current Score: value
     * Bonus Score: value
     * </pre>
     * Each line ends with a newline character.
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
     * Returns the high score value recorded in this score entry. This represents
     * the highest score achieved by the player at a particular point in time.
     *
     * @return the high score as an integer.
     */
    public final int getHighScore()
    {
        return highScore;
    }

    /*
     * Appends the string representation of the specified {@code LetterRushScore}
     * object to the file located at the given file path. Each score entry is
     * written as a block of four lines, as defined by the {@link #toString()}
     * method, followed by an additional empty line to separate it from subsequent
     * entries. This method is used to persist new high scores to the storage file.
     *
     * <p>The method opens the file specified by {@code filePath} in append mode using
     * a {@link BufferedWriter}. It then writes the string representation of the
     * provided {@code score} object (obtained from its {@link #toString()} method)
     * to the file, followed by a newline character to ensure proper formatting
     * and separation of score entries.
     *
     * @param score    the {@code LetterRushScore} object to be saved to the file.
     * The {@link #toString()} method of this object will be used
     * to generate the file content.
     * @param filePath the path to the file where the score entry should be appended.
     * If the file does not exist, it will be created. If it does
     * exist, the new score entry will be added to the end of the
     * file.
     * @throws IOException if an error occurs while attempting to write to the file
     * (e.g., due to insufficient permissions, disk space issues, or the file
     * being locked by another process). The exception message will provide
     * details about the error and the file path.
     */
    static void appendScoreToFile(final LetterRushScore score,
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


    /*
     * Returns the timestamp indicating when the high score was achieved. This
     * allows retrieval of the exact date and time a particular high score was
     * recorded.
     *
     * @return the timestamp as a {@link LocalDateTime} object.
     */
    final LocalDateTime getTimestamp()
    {
        return timestamp;
    }

    /*
     * Parses a string representation of a score entry and returns a corresponding
     * {@code LetterRushScore} object. This method is designed to take a multi-line
     * string, typically read from a score file, and extract the timestamp, high
     * score, current score, and bonus score to construct a {@code LetterRushScore}
     * instance. The input string is expected to adhere to a specific format,
     * where each piece of information is on a new line and preceded by a label
     * and a colon.
     *
     * <p>The method begins by splitting the input {@code entry} string into an array
     * of strings based on newline characters ({@code NEW_LINE}). It then performs
     * a crucial check to ensure that the resulting array contains exactly
     * {@code SCORE_ENTRY_LINES} (which is expected to be 4). If the number of lines
     * is not as expected, it indicates a malformed score entry. In such cases,
     * an error message is printed to the standard output, detailing the expected
     * number of lines and the actual number found, along with the problematic
     * entry itself. To prevent further issues, the method then returns a default
     * {@code LetterRushScore} object, initialized with all score values set to
     * {@code DEFAULT_SCORE_VALUE} (likely 0) and the timestamp set to the current
     * system time (as the original context of the malformed entry is lost).
     *
     * <p>If the input string has the correct number of lines, the method proceeds
     * to parse each line to extract the required data. This parsing is done within
     * a {@code try-catch} block to handle potential exceptions that might occur
     * if the string data is not in the expected format (e.g., non-numeric score
     * values, incorrect timestamp format).
     *
     * <p>Within the {@code try} block:
     * <ol>
     * <li>A {@link DateTimeFormatter} is created based on the {@code DATE_TIME_PATTERN}
     * constant. This formatter is used to parse the timestamp string.
     * <li>The first line of the {@code parts} array (at {@code TIMESTAMP_INDEX},
     * which is 0) is expected to contain the timestamp. This line is split
     * at the colon ({@code SPLIT_COLON}) with a limit of {@code SPLIT_LIMIT}
     * (likely 2, to handle potential colons in the value, though not expected
     * in the timestamp). The second part of this split (at {@code SPLIT_VALUE_INDEX},
     * which is 1) is then trimmed of any leading or trailing whitespace and
     * parsed into a {@link LocalDateTime} object using the previously created
     * {@code formatter}.
     * <li>Similarly, the second line (at {@code HIGH_SCORE_INDEX}, which is 1) is
     * split at the colon, the value part is trimmed, and then parsed into
     * an integer representing the high score using {@link Integer#parseInt(String)}.
     * <li>The third line (at {@code CURRENT_SCORE_INDEX}, which is 2) undergoes
     * the same process to extract and parse the current score as an integer.
     * <li>Finally, the fourth line (at {@code BONUS_SCORE_INDEX}, which is 3) is
     * processed to extract and parse the bonus score as an integer.
     * <li>If all parsing steps are successful, a new {@code LetterRushScore} object
     * is created using the parsed high score, current score, bonus score,
     * and timestamp, and this object is returned.
     * </ol>
     *
     * <p>If any exception occurs during the parsing process within the {@code try}
     * block (such as a {@link java.time.format.DateTimeParseException} if the timestamp
     * is in the wrong format, or a {@link NumberFormatException} if the score values
     * are not valid integers), the {@code catch} block is executed. Within the
     * {@code catch} block, an error message is printed to the standard output,
     * including the specific error message from the caught exception. Similar to
     * the case of an incorrect number of lines, the method then returns a default
     * {@code LetterRushScore} object to indicate that the parsing failed and to
     * provide a fallback value.
     *
     * <p>In summary, this method attempts to safely parse a string representation
     * of a score entry, handling cases of incorrect formatting and returning a
     * default {@code LetterRushScore} object. It relies on the input string adhering
     * to a specific four-line structure with labeled values separated by colons.
     *
     * @param entry the string representation of the score entry. This string is
     * expected to contain four lines, each with a label followed by
     * a colon and the corresponding value (timestamp, high score,
     * current score, bonus score).
     * @return a {@code LetterRushScore} object parsed from the input string. If
     * the string is malformed or if any parsing error occurs, a default
     * {@code LetterRushScore} object with zero scores and the current
     * timestamp is returned.
     */
    private static LetterRushScore fromString(final String entry)
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