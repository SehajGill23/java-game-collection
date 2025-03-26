package ca.bcit.Comp2522.termProject;

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

public class Score
{
    private final int           score;
    private final int           firstAttempts;
    private final int           secondAttempts;
    private final int           incorrectAttempts;
    private final int           totalGamesPlayed;
    private final LocalDateTime timestamp;

    // Constructor for ScoreTest
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

    // Constructor for the application
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

    public int getScore()
    {
        return score;
    }

    public double getAverageScore()
    {
        return totalGamesPlayed == 0 ? 0 : (double) score / totalGamesPlayed;
    }

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

            // Recalculate the score to ensure consistency
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