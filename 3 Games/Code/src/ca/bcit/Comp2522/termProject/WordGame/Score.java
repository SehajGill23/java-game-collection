package ca.bcit.Comp2522.termProject.WordGame;

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
 * The Score class represents a player's score in the Geography Trivia Game.
 * It stores the score, attempt counts, games played, and a timestamp, and provides
 * methods to calculate the average score, save scores to a file, and read scores from a file.
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
     * Constructs a new Score instance with the specified timestamp and game statistics.
     * This constructor is primarily used for testing purposes.
     *
     * @param timestamp         the timestamp of the score
     * @param totalGamesPlayed  the total number of games played
     * @param firstAttempts     the number of correct answers on the first attempt
     * @param secondAttempts    the number of correct answers on the second attempt
     * @param incorrectAttempts the number of incorrect answers after two attempts
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
     * Constructs a new Score instance with the specified game statistics and the current timestamp.
     * This constructor is used by the application to record a new score.
     *
     * @param score             the total score for the game
     * @param firstAttempts     the number of correct answers on the first attempt
     * @param secondAttempts    the number of correct answers on the second attempt
     * @param incorrectAttempts the number of incorrect answers after two attempts
     * @param totalGamesPlayed  the total number of games played
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
     * Returns the total score for the game.
     *
     * @return the total score
     */
    public int getScore()
    {
        return score;
    }

    /*
     * Calculates and returns the average score per game.
     * If no games have been played, returns 0.
     *
     * @return the average score per game
     */
    double getAverageScore()
    {
        return totalGamesPlayed == DEFAULT_ATTEMPT_VALUE ?
               DEFAULT_ATTEMPT_VALUE : (double) score / totalGamesPlayed;
    }

    /**
     * Returns a string representation of the score, including the timestamp, games played,
     * attempt counts, and total score.
     *
     * @return a formatted string representing the score
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
     * Appends the specified score to the given file.
     *
     * @param score    the Score object to save
     * @param filePath the path to the file where the score will be saved
     * @throws IOException if an error occurs while writing to the file
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
     * Reads all scores from the specified file and returns them as a list.
     * If the file does not exist, returns an empty list.
     *
     * @param filePath the path to the file containing the scores
     * @return a list of Score objects read from the file
     * @throws IOException if an error occurs while reading the file
     */
    public static List<Score> readScoresFromFile(final String filePath) throws IOException
    {
        final List<Score> scoreList;
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
     * Parses a string representation of a score and returns a corresponding Score object.
     * If the string is malformed, logs an error and returns a default Score object.
     *
     * @param line the string representation of the score
     * @return a Score object parsed from the string, or a default Score if parsing fails
     */
    private static Score fromString(final String line)
    {
        String[] parts = line.split(NEW_LINE);

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
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

            String[]          splitPart = parts[TIMESTAMP_INDEX].split(REGEX_COLON, SPLIT_LIMIT);

            LocalDateTime     timestamp = LocalDateTime.parse(splitPart[SPLIT_VALUE_INDEX]
                                                                      .trim(), formatter);
            int gamesPlayed       = Integer.parseInt(parts[GAMES_PLAYED_INDEX]
                                                             .split(REGEX_COLON, SPLIT_LIMIT)
                                                             [SPLIT_VALUE_INDEX]
                                                             .trim());
            int firstAttempts     = Integer.parseInt(parts[FIRST_ATTEMPTS_INDEX]
                                                             .split(REGEX_COLON, SPLIT_LIMIT)
                                                             [SPLIT_VALUE_INDEX]
                                                             .trim());
            int secondAttempts    = Integer.parseInt(parts[SECOND_ATTEMPTS_INDEX]
                                                             .split(REGEX_COLON, SPLIT_LIMIT)
                                                             [SPLIT_VALUE_INDEX]
                                                             .trim());
            int incorrectAttempts = Integer.parseInt(parts[INCORRECT_ATTEMPTS_INDEX]
                                                             .split(REGEX_COLON, SPLIT_LIMIT)
                                                             [SPLIT_VALUE_INDEX]
                                                             .trim());

            String scorePart = parts[SCORE_INDEX];
            if(scorePart.startsWith(TOTAL_SCORE_LABEL))
            {
                return new Score(timestamp,
                                 gamesPlayed,
                                 firstAttempts,
                                 secondAttempts,
                                 incorrectAttempts);
            }

            int parsedScore = Integer.parseInt(scorePart
                                                       .split(REGEX_COLON, SPLIT_LIMIT)
                                                       [SPLIT_VALUE_INDEX].trim()
                                                                          .replaceAll(REGEX_NON_NUMERIC
                                                                                  , ""));
            int calculatedScore = firstAttempts * FIRST_ATTEMPT_MULTIPLIER + secondAttempts
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
        catch(Exception e)
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