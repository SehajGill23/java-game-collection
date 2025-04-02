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
 */
public class LetterRushScore {
    private final int highScore;
    private final int currentScore;
    private final int bonusScore;
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
    public LetterRushScore(int highScore, int currentScore, int bonusScore, LocalDateTime timestamp) {
        this.highScore = highScore;
        this.currentScore = currentScore;
        this.bonusScore = bonusScore;
        this.timestamp = timestamp;
    }

    /**
     * Constructs a new LetterRushScore instance with the specified high score, current score,
     * and bonus score, using the current timestamp.
     *
     * @param highScore    the high score achieved
     * @param currentScore the current score at the time of the high score
     * @param bonusScore   the bonus score at the time of the high score
     */
    public LetterRushScore(int highScore, int currentScore, int bonusScore) {
        this(highScore, currentScore, bonusScore, LocalDateTime.now());
    }

    /**
     * Returns the high score.
     *
     * @return the high score
     */
    public int getHighScore() {
        return highScore;
    }

    /**
     * Returns the current score at the time of the high score.
     *
     * @return the current score
     */
    public int getCurrentScore() {
        return currentScore;
    }

    /**
     * Returns the bonus score at the time of the high score.
     *
     * @return the bonus score
     */
    public int getBonusScore() {
        return bonusScore;
    }

    /**
     * Returns the timestamp when the high score was achieved.
     *
     * @return the timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Returns a string representation of the score entry, including the timestamp,
     * high score, current score, and bonus score.
     *
     * @return a formatted string representing the score entry
     */
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("Timestamp: %s\nHigh Score: %d\nCurrent Score: %d\nBonus Score: %d\n",
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
    public static void appendScoreToFile(LetterRushScore score, String filePath) throws IOException {
        File scoreFile = new File(filePath);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(scoreFile, true))) {
            writer.write(score.toString() + "\n");
        } catch (IOException e) {
            throw new IOException("Error: Unable to save score to " + scoreFile.getAbsolutePath() + ": " + e.getMessage());
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
    public static List<LetterRushScore> readScoresFromFile(String filePath) throws IOException {
        List<LetterRushScore> scoreList = new ArrayList<>();
        File scoreFile = new File(filePath);

        if (!scoreFile.exists()) {
            return scoreList;
        }

        try (BufferedReader reader = Files.newBufferedReader(scoreFile.toPath(), StandardCharsets.UTF_8)) {
            String line;
            StringBuilder scoreEntry = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() && scoreEntry.length() > 0) {
                    scoreList.add(fromString(scoreEntry.toString()));
                    scoreEntry.setLength(0);
                } else if (!line.trim().isEmpty()) {
                    scoreEntry.append(line).append("\n");
                }
            }
            if (scoreEntry.length() > 0) {
                scoreList.add(fromString(scoreEntry.toString()));
            }
        } catch (IOException e) {
            throw new IOException("Error: Unable to read scores from " + filePath + ": " + e.getMessage());
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
    public static LetterRushScore fromString(String entry) {
        String[] parts = entry.split("\n");
        if (parts.length != 4) {
            System.out.println("Invalid score format. Expected 4 lines, got " + parts.length);
            System.out.println("Entry: " + entry);
            return new LetterRushScore(0, 0, 0);
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime timestamp = LocalDateTime.parse(parts[0].split(":", 2)[1].trim(), formatter);
            int highScore = Integer.parseInt(parts[1].split(":", 2)[1].trim());
            int currentScore = Integer.parseInt(parts[2].split(":", 2)[1].trim());
            int bonusScore = Integer.parseInt(parts[3].split(":", 2)[1].trim());
            return new LetterRushScore(highScore, currentScore, bonusScore, timestamp);
        } catch (Exception e) {
            System.out.println("Error parsing score: " + e.getMessage());
            return new LetterRushScore(0, 0, 0);
        }
    }
}