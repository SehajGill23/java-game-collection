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

    //new
    public String toFileString() {
        return String.format("%d,%d,%d,%d,%d", score, firstAttempts, secondAttempts, incorrectAttempts, totalGamesPlayed);
    }

    // Updated the toString() method to match the correct format
    @Override
    public String toString()
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("Date and Time: %s\nGames Played: %d\nCorrect First Attempts: " +
                             "%d\nCorrect Second Attempts: %d\nIncorrect Attempts: %d\nTotal Score: %d points\n",
                             timestamp.format(formatter),
                             totalGamesPlayed,
                             firstAttempts,
                             secondAttempts,
                             incorrectAttempts,
                             score);
    }

    public static Score fromString(String line)
    {
        String[] parts = line.split(",");
        if(parts.length < 5)
        {
            return new Score(0,
                             0,
                             0,
                             0,
                             1);
        }
        return new Score(Integer.parseInt(parts[0]),
                         Integer.parseInt(parts[1]),
                         Integer.parseInt(parts[2]),
                         Integer.parseInt(parts[3]),
                         Integer.parseInt(parts[4]));
    }

    //debug
    public int getScore() {
        return score;
    }

    public double getAverageScore()
    {
        return totalGamesPlayed == 0 ? 0 : (double) score / totalGamesPlayed;
    }

    //debug
    public int getTotalGamesPlayed() {
        return totalGamesPlayed;
    }
}
