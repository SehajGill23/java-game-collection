//package ca.bcit.Comp2522.termProject;
//
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//public class Score
//{
//    private String dateTimePlayed;
//    private int    numGamesPlayed;
//    private int    numCorrectFirstAttempt;
//    private int    numCorrectSecondAttempt;
//    private int    numIncorrectTwoAttempts;
//    private int    totalScore;
//
//    // Constructor
//    public Score(String dateTimePlayed,
//                 int numGamesPlayed,
//                 int numCorrectFirstAttempt,
//                 int numCorrectSecondAttempt,
//                 int numIncorrectTwoAttempts)
//    {
//        this.dateTimePlayed          = dateTimePlayed;
//        this.numGamesPlayed          = numGamesPlayed;
//        this.numCorrectFirstAttempt  = numCorrectFirstAttempt;
//        this.numCorrectSecondAttempt = numCorrectSecondAttempt;
//        this.numIncorrectTwoAttempts = numIncorrectTwoAttempts;
//        this.totalScore              = (numCorrectFirstAttempt * 2) + (numCorrectSecondAttempt * 1);
//    }
//
//    // Overloaded constructor for saving the score to file
//    public Score(String dateTimePlayed,
//                 int numGamesPlayed,
//                 int numCorrectFirstAttempt,
//                 int numCorrectSecondAttempt,
//                 int numIncorrectTwoAttempts,
//                 int totalScore)
//    {
//        this.dateTimePlayed          = dateTimePlayed;
//        this.numGamesPlayed          = numGamesPlayed;
//        this.numCorrectFirstAttempt  = numCorrectFirstAttempt;
//        this.numCorrectSecondAttempt = numCorrectSecondAttempt;
//        this.numIncorrectTwoAttempts = numIncorrectTwoAttempts;
//        this.totalScore              = totalScore;
//    }
//
//    public Score (int score, int firstAttempts, int secondAttempts, int incorrectAttempts)
//    {
//        this.totalScore = score;
//        this.numCorrectFirstAttempt = firstAttempts;
//        this.numCorrectSecondAttempt = secondAttempts;
//        this.numIncorrectTwoAttempts = incorrectAttempts;
//        this.numGamesPlayed = numGamesPlayed;
//        this.dateTimePlayed = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()); // Setting the current date and time
//    }
//    // Method to create a Score object from a string (from file)
//    public static Score fromString(String str)
//    {
//        String[] parts = str.split(",");
//        return new Score(parts[0],
//                         Integer.parseInt(parts[1]),
//                         Integer.parseInt(parts[2]),
//                         Integer.parseInt(parts[3]),
//                         Integer.parseInt(parts[4]));
//    }
//
//    // Method to get the average score per game
//    public double getAverageScore()
//    {
//        return (double) totalScore / numGamesPlayed;
//    }
//
//    // Getters and setters
//    public int getTotalScore()
//    {
//        return totalScore;
//    }
//
//    public String getDateTimePlayed()
//    {
//        return dateTimePlayed;
//    }
//
//
//
//    @Override
//    public String toString()
//    {
//        return dateTimePlayed + ", " + numGamesPlayed + ", " + numCorrectFirstAttempt + ", " + numCorrectSecondAttempt + ", " + numIncorrectTwoAttempts + ", " + totalScore;
//    }
//}


package ca.bcit.Comp2522.termProject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Score
{
    private String dateTimePlayed;
    private int    numGamesPlayed;
    private int    numCorrectFirstAttempt;
    private int    numCorrectSecondAttempt;
    private int    numIncorrectTwoAttempts;
    private int    totalScore;

    public Score(int score,
                 int firstAttempts,
                 int secondAttempts,
                 int incorrectAttempts,
                 int numGamesPlayed)
    {
        this.totalScore              = score;
        this.numCorrectFirstAttempt  = firstAttempts;
        this.numCorrectSecondAttempt = secondAttempts;
        this.numIncorrectTwoAttempts = incorrectAttempts;
        this.numGamesPlayed          = numGamesPlayed;
        LocalDateTime     currentTime = LocalDateTime.now();
        DateTimeFormatter formatter   = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.dateTimePlayed = currentTime.format(formatter);
    }

    public static Score fromString(String str)
    {
        String[] parts = str.split(",");
        return new Score(Integer.parseInt(parts[5].trim()),
                         // totalScore
                         Integer.parseInt(parts[2].trim()),
                         // numCorrectFirstAttempt
                         Integer.parseInt(parts[3].trim()),
                         // numCorrectSecondAttempt
                         Integer.parseInt(parts[4].trim()),
                         // numIncorrectTwoAttempts
                         Integer.parseInt(parts[1].trim())); // numGamesPlayed
    }

    public double getAverageScore()
    {
        return (numGamesPlayed == 0) ? 0 : (double) totalScore / numGamesPlayed;
    }

    public int getTotalScore()
    {
        return totalScore;
    }

    public String getDateTimePlayed()
    {
        return dateTimePlayed;
    }

    @Override
    public String toString()
    {
        return dateTimePlayed + ", " + numGamesPlayed + ", " + numCorrectFirstAttempt +
                ", " + numCorrectSecondAttempt + ", " + numIncorrectTwoAttempts + ", " + totalScore;
    }
}
