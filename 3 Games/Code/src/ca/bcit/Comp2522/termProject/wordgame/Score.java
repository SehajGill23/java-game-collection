package ca.bcit.Comp2522.termProject.wordgame;

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
 * The {@code Score} class plays a crucial role in the Geography Trivia Game by recording and managing a player's
 * performance in a single game session. It goes beyond just storing the final score; it captures a detailed breakdown
 * of the game, including the timing, the player's progress over multiple games, and their success rates on different
 * attempts. This information is vital for providing feedback to the player and tracking their improvement.
 * <p>
 * At its core, a {@code Score} object holds specific statistics about one completed game. This includes: the exact
 *  {@link LocalDateTime} when the game ended, the total number of games the player has played up to this point,
 * the number of questions answered correctly on the first attempt (indicating strong initial knowledge), the number
 * answered correctly on the second attempt (showing learning or recall after a first miss), and the number of questions
 * that remained unanswered correctly after both attempts. Based on these attempt counts, the class calculates a
 * final score for the game, giving more weight to first-attempt successes.
 * </p>
 * <p>
 * A key feature of the {@code Score} class is its ability to save and load game records from a file named "score.txt".
 * The {@link #appendScoreToFile(Score, String)} method takes a {@code Score} object and writes its details to the end
 * of this file, ensuring that each game's outcome is preserved. Conversely, the {@link #readScoresFromFile(String)}
 * method reads the contents of "score.txt" and converts each saved game record back into a {@code Score} object,
 * allowing the game to access historical performance data. This is essential for features like calculating average
 * scores and determining if a player has achieved a new high score. The class includes error handling to manage
 * potential issues during file reading and writing.
 * </p>
 * <p>
 * Once a {@code Score} object is created, its data remains constant. This immutability ensures that the recorded
 * performance of a game cannot be accidentally changed, providing a reliable record of the player's efforts.
 * </p>
 * <p>
 * Internally, the {@code Score} class uses several predefined constants to manage how scores are calculated, how the
 * data is formatted when saved to the file, and what messages to display if errors or inconsistencies are found.
 * The {@link #fromString(String)} method is particularly important as it handles the process of taking a line (or
 * set of lines) read from the "score.txt" file and converting it back into a usable {@code Score} object.
 * This method also includes checks to validate the data read from the file, ensuring that it's in the expected format
 * and that the calculated score based on attempts matches the score that was saved. Warnings are generated if
 * discrepancies are detected, helping to maintain the integrity of the score data.
 * </p>
 *
 * @author Sehaj Gill
 * @version 1.0
 */
public final class Score
{
    private static final int    DEFAULT_ATTEMPT_VALUE     = 0;
    private static final int    DEFAULT_GAMES_PLAYED      = 1;
    private static final int    FIRST_ATTEMPT_MULTIPLIER  = 2;
    private static final int    SECOND_ATTEMPT_MULTIPLIER = 1;
    private static final int    SCORE_ENTRY_LINES         = 6;
    private static final int    SPLIT_LIMIT               = 2;
    private static final int    SPLIT_VALUE_INDEX         = 1;
    private static final int    TIMESTAMP_INDEX           = 0;
    private static final int    GAMES_PLAYED_INDEX        = 1;
    private static final int    FIRST_ATTEMPTS_INDEX      = 2;
    private static final int    SECOND_ATTEMPTS_INDEX     = 3;
    private static final int    INCORRECT_ATTEMPTS_INDEX  = 4;
    private static final int    SCORE_INDEX               = 5;
    private static final String DATE_TIME_PATTERN         = "yyyy-MM-dd HH:mm:ss";
    private static final String NEW_LINE                  = "\n";
    private static final String DATE_TIME_LABEL           = "Date and Time: ";
    private static final String GAMES_PLAYED_LABEL        = "Games Played: ";
    private static final String FIRST_ATTEMPTS_LABEL      = "Correct First Attempts: ";
    private static final String SECOND_ATTEMPTS_LABEL     = "Correct Second Attempts: ";
    private static final String INCORRECT_ATTEMPTS_LABEL  = "Incorrect Attempts: ";
    private static final String SCORE_LABEL               = "Score: ";
    private static final String SCORE_SUFFIX              = " points";
    private static final String ERROR_MESSAGE             = "Error: Unable to save score to ";
    private static final String ERROR_READ_MESSAGE        = "Error: Unable to read scores from ";
    private static final String ERROR_MESSAGE_PART        = ": ";
    private static final String INVALID_FORMAT_MSG        = "Invalid score format. Expected 6 lines, got ";
    private static final String ENTRY_LABEL               = "Entry: ";
    private static final String WARNING_MESSAGE           = "Warning: Parsed score (";
    private static final String DOES_NOT_MATCH_MSG        = ") does not match calculated score (";
    private static final String CLOSE_PARENTHESIS         = ")";
    private static final String TOTAL_SCORE_LABEL         = "Total Score:";
    private static final String ERROR_PARSING_MSG         = "Error parsing score: ";
    private static final String ERROR_PARSING_OLD_MSG     = "Error parsing score with old format: ";
    private static final String REGEX_NON_NUMERIC         = "[^0-9]";
    private static final String REGEX_COLON               = ":";

    private final int           score;
    private final int           firstAttempts;
    private final int           secondAttempts;
    private final int           incorrectAttempts;
    private final int           totalGamesPlayed;
    private final LocalDateTime timestamp;

    /**
     * Creates a new {@code Score} object with specific details about a game, including the time it was played.
     * This constructor is primarily used when loading scores from the "score.txt" file or for testing purposes where
     * a specific timestamp is needed. The total score for the game is automatically calculated based on the number
     * of correct first and second attempts.
     *
     * @param timestamp         The exact date and time when the game session ended.
     * @param totalGamesPlayed  The total number of games the player has played up to this point.
     * @param firstAttempts     The number of questions the player answered correctly on their first try.
     * @param secondAttempts    The number of questions the player answered correctly on their second try.
     * @param incorrectAttempts The number of questions the player did not answer correctly within two attempts.
     */
    public Score(final LocalDateTime timestamp,
                 final int totalGamesPlayed,
                 final int firstAttempts,
                 final int secondAttempts,
                 final int incorrectAttempts)
    {
        this.timestamp         = timestamp;
        this.totalGamesPlayed  = totalGamesPlayed;
        this.firstAttempts     = firstAttempts;
        this.secondAttempts    = secondAttempts;
        this.incorrectAttempts = incorrectAttempts;
        this.score             = firstAttempts * FIRST_ATTEMPT_MULTIPLIER +
                                 secondAttempts * SECOND_ATTEMPT_MULTIPLIER;
    }

    /**
     * Creates a new {@code Score} object for a game that has just been played. It automatically records the current
     * date and time when the game ends. The total score and other statistics are provided as arguments.
     *
     * @param score             The total score achieved in the game.
     * @param firstAttempts     The number of correct answers on the first attempt.
     * @param secondAttempts    The number of correct answers on the second attempt.
     * @param incorrectAttempts The number of incorrect answers after two attempts.
     * @param totalGamesPlayed  The total number of games played by the user after this game.
     */
    public Score(final int score,
                 final int firstAttempts,
                 final int secondAttempts,
                 final int incorrectAttempts,
                 final int totalGamesPlayed)
    {
        this.score             = score;
        this.firstAttempts     = firstAttempts;
        this.secondAttempts    = secondAttempts;
        this.incorrectAttempts = incorrectAttempts;
        this.totalGamesPlayed  = totalGamesPlayed;
        this.timestamp         = LocalDateTime.now();
    }

    /**
     * Returns the total score obtained in the game session represented by this {@code Score} object.
     *
     * @return The total score as an integer.
     */
    public int getScore()
    {
        return score;
    }

    /*
     * Calculates and returns the average score per game played by the user. This is computed by dividing the total
     * score across all recorded games by the total number of games played. It handles the case where no games have
     * been played to avoid division by zero.
     *
     * @return The average score per game as a double. Returns 0.0 if no games have been played.
     */
    double getAverageScore()
    {
        return totalGamesPlayed == DEFAULT_ATTEMPT_VALUE ?
               DEFAULT_ATTEMPT_VALUE : (double) score / totalGamesPlayed;
    }


    /**
     * Formats the score information into a string that is suitable for saving to the "score.txt" file.
     * It includes the timestamp, total games played, counts of correct first and second attempts, incorrect attempts,
     * and the final score, each labeled for easy parsing later.
     *
     * @return A formatted string representing the score details.
     */
    @Override
    public String toString()
    {
        final DateTimeFormatter formatter;
        formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

        return String.format(DATE_TIME_LABEL + "%s" + NEW_LINE + GAMES_PLAYED_LABEL + "%d" + NEW_LINE
                             + FIRST_ATTEMPTS_LABEL + "%d" + NEW_LINE + SECOND_ATTEMPTS_LABEL + "%d"
                             + NEW_LINE + INCORRECT_ATTEMPTS_LABEL + "%d" + NEW_LINE + SCORE_LABEL
                             + "%d" + SCORE_SUFFIX + NEW_LINE,
                             timestamp.format(formatter),
                             totalGamesPlayed,
                             firstAttempts,
                             secondAttempts,
                             incorrectAttempts,
                             score);
    }

    /**
     * Appends the details of the given {@code Score} object to the "score.txt" file. This method is used to save
     * the results of a new game. It handles potential errors that might occur while writing to the file.
     *
     * @param score    The {@code Score} object to be saved.
     * @param filePath The path to the "score.txt" file.
     * @throws IOException If an error occurs during file writing.
     */
    public static void appendScoreToFile(final Score score,
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
     * Reads all score records from the file at the specified path and returns them as a list
     * of {@code Score} objects. If the file does not exist, an empty list is returned without
     * throwing an exception. This method handles potential {@link IOException} during file reading.
     * Each score record in the file is expected to be formatted according to the {@link #toString()}
     * method, with each attribute on a new line and separated by labels.
     *
     * @param filePath the path to the file containing the saved score records.
     * @return a {@link List} of {@code Score} objects read from the file. Returns an empty list
     * if the file does not exist or if no valid score entries are found.
     * @throws IOException if an error occurs while reading from the file, such as file access
     * issues or read permissions. The exception will contain a descriptive
     * error message including the file path and the original error.
     */
    public static List<Score> readScoresFromFile(final String filePath) throws IOException
    {
        String        line;
        final StringBuilder scoreEntry;
        final List<Score> scoreList;
        final File scoreFile;

        scoreList = new ArrayList<>();
        scoreFile = new File(filePath);

        if(!scoreFile.exists())
        {
            return scoreList;
        }

        try(final BufferedReader reader = Files.newBufferedReader(scoreFile.toPath(),
                                                                  StandardCharsets.UTF_8))
        {

            scoreEntry = new StringBuilder();

            while((line = reader.readLine()) != null)
            {
                if(line.trim().isEmpty() && !scoreEntry.isEmpty())
                {
                    scoreList.add(fromString(scoreEntry.toString()));
                    scoreEntry.setLength(DEFAULT_ATTEMPT_VALUE);
                }
                else if(!line.trim().isEmpty())
                {
                    scoreEntry.append(line).append(NEW_LINE);
                }
            }

            if(!scoreEntry.isEmpty())
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
     * Parses a string representation of a score entry and returns a corresponding {@code Score} object.
     * The string is expected to be formatted with each score attribute on a new line, preceded by a label.
     * If the string does not conform to the expected format, an error message is logged to the console,
     * and a default {@code Score} object (with current timestamp and default values) is returned.
     * This method also performs a consistency check by recalculating the score based on the attempt counts
     * and comparing it with the parsed score, issuing a warning if they do not match.
     *
     * @param line the string representation of the score entry to be parsed.
     * @return a {@code Score} object parsed from the string. If parsing fails due to format issues,
     * a default {@code Score} object is returned.
     */
    private static Score fromString(final String line)
    {
        final int               gamesPlayed;
        final int               firstAttempts;
        final int               secondAttempts;
        final int               incorrectAttempts;
        final int               parsedScore;
        final int               calculatedScore;
        final String            scorePart;
        final String[]          parts;
        final String[]          splitPart;
        final DateTimeFormatter formatter;
        final LocalDateTime     timestamp;


        parts = line.split(NEW_LINE);

        if(parts.length != SCORE_ENTRY_LINES)
        {
            System.out.println(INVALID_FORMAT_MSG + parts.length);
            System.out.println(ENTRY_LABEL + line);
            return new Score(LocalDateTime.now(),
                             DEFAULT_GAMES_PLAYED,
                             DEFAULT_ATTEMPT_VALUE,
                             DEFAULT_ATTEMPT_VALUE,
                             DEFAULT_ATTEMPT_VALUE);
        }

        try
        {
             formatter       = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

             splitPart       = parts[TIMESTAMP_INDEX].split(REGEX_COLON, SPLIT_LIMIT);

             timestamp       = LocalDateTime.parse(splitPart[SPLIT_VALUE_INDEX]
                                                                      .trim(), formatter);
             gamesPlayed     = Integer.parseInt(parts[GAMES_PLAYED_INDEX]
                                                             .split(REGEX_COLON, SPLIT_LIMIT)
                                                             [SPLIT_VALUE_INDEX]
                                                             .trim());
            firstAttempts    = Integer.parseInt(parts[FIRST_ATTEMPTS_INDEX]
                                                             .split(REGEX_COLON, SPLIT_LIMIT)
                                                             [SPLIT_VALUE_INDEX]
                                                             .trim());
            secondAttempts   = Integer.parseInt(parts[SECOND_ATTEMPTS_INDEX]
                                                             .split(REGEX_COLON, SPLIT_LIMIT)
                                                             [SPLIT_VALUE_INDEX]
                                                             .trim());
           incorrectAttempts = Integer.parseInt(parts[INCORRECT_ATTEMPTS_INDEX]
                                                             .split(REGEX_COLON, SPLIT_LIMIT)
                                                             [SPLIT_VALUE_INDEX]
                                                             .trim());

            scorePart = parts[SCORE_INDEX];
            if(scorePart.startsWith(TOTAL_SCORE_LABEL))
            {
                return new Score(timestamp,
                                 gamesPlayed,
                                 firstAttempts,
                                 secondAttempts,
                                 incorrectAttempts);
            }

             parsedScore = Integer.parseInt(scorePart
                                                       .split(REGEX_COLON, SPLIT_LIMIT)
                                                       [SPLIT_VALUE_INDEX].trim()
                                                                          .replaceAll(REGEX_NON_NUMERIC
                                                                                  , ""));
            calculatedScore = firstAttempts * FIRST_ATTEMPT_MULTIPLIER + secondAttempts
                                                                             * SECOND_ATTEMPT_MULTIPLIER;

            if(parsedScore != calculatedScore)
            {
                System.out.println(WARNING_MESSAGE + parsedScore + DOES_NOT_MATCH_MSG
                                   + calculatedScore + CLOSE_PARENTHESIS);
            }

            return new Score(timestamp,
                             gamesPlayed,
                             firstAttempts,
                             secondAttempts,
                             incorrectAttempts);
        }
        catch(final Exception e)
        {
            System.out.println(ERROR_PARSING_OLD_MSG + e.getMessage());
            return new Score(LocalDateTime.now(),
                             DEFAULT_GAMES_PLAYED,
                             DEFAULT_ATTEMPT_VALUE,
                             DEFAULT_ATTEMPT_VALUE,
                             DEFAULT_ATTEMPT_VALUE);
        }

    }
}