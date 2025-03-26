package ca.bcit.Comp2522.termProject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Score
{
    private final int           score;
    private final int           firstAttempts;
    private final int           secondAttempts;
    private final int           incorrectAttempts;
    private final int           totalGamesPlayed;
    private final LocalDateTime timestamp;

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


    @Override
    public String toString()
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("Date and Time: %s\nGames Played: %d\nCorrect First Attempts: " + "%d\nCorrect Second Attempts: %d\nIncorrect Attempts: %d\nTotal Score: %d points\n",
                             timestamp.format(formatter),
                             totalGamesPlayed,
                             firstAttempts,
                             secondAttempts,
                             incorrectAttempts,
                             score);
    }

    public static Score fromString(String line)
    {
        String[] parts = line.split("\n");
        if(parts.length != 6)
        {
            System.out.println("Invalid score format. Expected 6 lines, got " + parts.length);
            System.out.println("Entry: " + line);
            return new Score(0,
                             0,
                             0,
                             0,
                             1);
        }

        try
        {
            int    gamesPlayed       = Integer.parseInt(parts[1].split(":",
                                                                       2)[1].trim());
            int    firstAttempts     = Integer.parseInt(parts[2].split(":",
                                                                       2)[1].trim());
            int    secondAttempts    = Integer.parseInt(parts[3].split(":",
                                                                       2)[1].trim());
            int    incorrectAttempts = Integer.parseInt(parts[4].split(":",
                                                                       2)[1].trim());
            String scorePart         = parts[5].split(":",
                                                      2)[1].trim();
            int    score             = Integer.parseInt(scorePart.replaceAll("[^0-9]",
                                                                             ""));

            return new Score(score,
                             firstAttempts,
                             secondAttempts,
                             incorrectAttempts,
                             gamesPlayed);
        }
        catch(Exception e)
        {
            System.out.println("Error parsing score: " + e.getMessage());
            return new Score(0,
                             0,
                             0,
                             0,
                             1);
        }
    }


    public double getAverageScore()
    {
        return totalGamesPlayed == 0 ? 0 : (double) score / totalGamesPlayed;
    }

}
