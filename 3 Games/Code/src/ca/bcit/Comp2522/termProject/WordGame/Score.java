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
public class Score
{
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
    public Score(LocalDateTime timestamp,
                 int totalGamesPlayed,
                 int firstAttempts,
                 int secondAttempts,
                 int incorrectAttempts)
    {
        this.timestamp         = timestamp;
        this.totalGamesPlayed  = totalGamesPlayed;
        this.firstAttempts     = firstAttempts;
        this.secondAttempts    = secondAttempts;
        this.incorrectAttempts = incorrectAttempts;
        this.score             = firstAttempts * 2 + secondAttempts * 1;
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
    public Score(int score,
                 int firstAttempts,
                 int secondAttempts,
                 int incorrectAttempts,
                 int totalGamesPlayed)
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

    /**
     * Calculates and returns the average score per game.
     * If no games have been played, returns 0.
     *
     * @return the average score per game
     */
    public double getAverageScore()
    {
        return totalGamesPlayed == 0 ? 0 : (double) score / totalGamesPlayed;
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("Date and Time: %s\nGames Played: %d\nCorrect First Attempts: %d\nCorrect Second Attempts: %d\nIncorrect Attempts: %d\nScore: %d points\n",
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
     * @throw IOException if an error occurs while writing to the file
     */
    public static void appendScoreToFile(Score score,
                                         String filePath) throws IOException
    {
        File scoreFile = new File(filePath);
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(scoreFile,
                                                                      true)))
        {
            writer.write(score.toString() + "\n");
        }
        catch(IOException e)
        {
            throw new IOException("Error: Unable to save score to " + scoreFile.getAbsolutePath() + ": " + e.getMessage());
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
    public static List<Score> readScoresFromFile(String filePath) throws IOException
    {
        List<Score> scoreList = new ArrayList<>();
        File        scoreFile = new File(filePath);

        if(!scoreFile.exists())
        {
            return scoreList;
        }

        try(BufferedReader reader = Files.newBufferedReader(scoreFile.toPath(),
                                                            StandardCharsets.UTF_8))
        {
            String        line;
            StringBuilder scoreEntry = new StringBuilder();
            while((line = reader.readLine()) != null)
            {
                if(line.trim().isEmpty() && scoreEntry.length() > 0)
                {
                    scoreList.add(fromString(scoreEntry.toString()));
                    scoreEntry.setLength(0);
                }
                else if(!line.trim().isEmpty())
                {
                    scoreEntry.append(line).append("\n");
                }
            }
            if(scoreEntry.length() > 0)
            {
                scoreList.add(fromString(scoreEntry.toString()));
            }
        }
        catch(IOException e)
        {
            throw new IOException("Error: Unable to read scores from " + filePath + ": " + e.getMessage());
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
    public static Score fromString(String line)
    {
        String[] parts = line.split("\n");
        if(parts.length != 6)
        {
            System.out.println("Invalid score format. Expected 6 lines, got " + parts.length);
            System.out.println("Entry: " + line);
            return new Score(LocalDateTime.now(),
                             1,
                             0,
                             0,
                             0);
        }

        try
        {
            DateTimeFormatter formatter         = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime     timestamp         = LocalDateTime.parse(parts[0].split(":",
                                                                                     2)[1].trim(),
                                                                      formatter);
            int               gamesPlayed       = Integer.parseInt(parts[1].split(":",
                                                                                  2)[1].trim());
            int               firstAttempts     = Integer.parseInt(parts[2].split(":",
                                                                                  2)[1].trim());
            int               secondAttempts    = Integer.parseInt(parts[3].split(":",
                                                                                  2)[1].trim());
            int               incorrectAttempts = Integer.parseInt(parts[4].split(":",
                                                                                  2)[1].trim());
            String            scorePart         = parts[5].split(":",
                                                                 2)[1].trim();
            int               parsedScore       = Integer.parseInt(scorePart.replaceAll("[^0-9]",
                                                                                        ""));


            int calculatedScore = firstAttempts * 2 + secondAttempts * 1;
            if(parsedScore != calculatedScore)
            {
                System.out.println("Warning: Parsed score (" + parsedScore + ") does not match calculated score (" + calculatedScore + ")");
            }

            return new Score(timestamp,
                             gamesPlayed,
                             firstAttempts,
                             secondAttempts,
                             incorrectAttempts);
        }
        catch(Exception e)
        {
            if(parts[5].startsWith("Total Score:"))
            {
                try
                {
                    DateTimeFormatter formatter         = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime     timestamp         = LocalDateTime.parse(parts[0].split(":",
                                                                                             2)[1].trim(),
                                                                              formatter);
                    int               gamesPlayed       = Integer.parseInt(parts[1].split(":",
                                                                                          2)[1].trim());
                    int               firstAttempts     = Integer.parseInt(parts[2].split(":",
                                                                                          2)[1].trim());
                    int               secondAttempts    = Integer.parseInt(parts[3].split(":",
                                                                                          2)[1].trim());
                    int               incorrectAttempts = Integer.parseInt(parts[4].split(":",
                                                                                          2)[1].trim());
                    return new Score(timestamp,
                                     gamesPlayed,
                                     firstAttempts,
                                     secondAttempts,
                                     incorrectAttempts);
                }
                catch(Exception ex)
                {
                    System.out.println("Error parsing score with old format: " + ex.getMessage());
                    return new Score(LocalDateTime.now(),
                                     1,
                                     0,
                                     0,
                                     0);
                }
            }
            System.out.println("Error parsing score: " + e.getMessage());
            return new Score(LocalDateTime.now(),
                             1,
                             0,
                             0,
                             0);
        }
    }
}