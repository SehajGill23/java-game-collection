package ca.bcit.Comp2522.termProject.MyGame;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Player
{
    // In Player class
    private static final String HIGH_SCORE_FILE_PATH = "Resources/highScore.txt";
    private static final int TARGET_POINTS = 10;
    private static final int BONUS_POINTS = 20;
    private static final double CURSOR_SIZE = 10.0;
    private int highScore;
    private double cursorX;
    private double cursorY;
    private List<Character> collectedTarget = new ArrayList<>();
    private List<Character> collectedBonus = new ArrayList<>();
    private int score = 0;
    private int bonusPoints = 0;
    private  int incorrectClicks = 0;


    public Player() {
        score = 0;
        highScore = loadHighScore();
        collectedTarget = new ArrayList<>();
        collectedBonus = new ArrayList<>();
    }

    public void updateCursorPosition(final double x, final double y) {
        cursorX = x;
        cursorY = y;
    }

    // Fix: Added getter for collectedBonus to allow access in LetterRush
    public List<Character> getCollectedBonus() {
        return new ArrayList<>(collectedBonus);
    }

    // Fix: Added setter for incorrectClicks to allow modification in LetterRush
    public void setIncorrectClicks(int incorrectClicks) {
        this.incorrectClicks = incorrectClicks;
    }

    public void resetForNewLevel() {
        collectedTarget.clear();
        collectedBonus.clear();
        incorrectClicks = 0;
        // Note: score and bonusPoints are NOT reset here to maintain cumulative scoring
    }

    //helper method
    public List<Character> getCollectedTarget() {
        return new ArrayList<>(collectedTarget);
    }


    public double getCursorX() {
        return cursorX;
    }

    public double getCursorY() {
        return cursorY;
    }

    public double getCursorSize() {
        return CURSOR_SIZE;
    }

    public void clickLetter(final Letter letter, final String targetWord, final String bonusWord) {
        if (letter.isLocked()) return;

        final char c;
        c = letter.getValue();
        letter.lock();

        StringBuilder collected;
        collected = new StringBuilder();

        for  (char ch : collectedTarget) {
            collected.append(ch);
        }

        collected.append(c);
        boolean isCorrectClick;
        isCorrectClick = targetWord.startsWith(collected.toString());


        if (isCorrectClick) {
            collectedTarget.add(c);
            addTargetPoints();
        } else {
            incorrectClicks++;
        }


        if (bonusWord.contains(String.valueOf(c))) {
            collectedBonus.add(c);
        }

        System.out.println("Clicked letter: " + c + " | Target so far: " + collectedTarget + " | Bonus so far: " + collectedBonus);
    }


    public boolean hasCompletedTargetWord(final String targetWord) {
        if (collectedTarget.size() != targetWord.length()) {
            return false;
        }

        StringBuilder collected = new StringBuilder();
        for (char c : collectedTarget) {
            collected.append(c);
        }
        return collected.toString().equals(targetWord);
    }

    public boolean hasCompletedBonusWord(final String bonusWord) {
        if (collectedBonus.size() != bonusWord.length()) {
            return false;
        }

        StringBuilder collected = new StringBuilder();
        for (char c : collectedBonus) {
            collected.append(c);
        }
        return collected.toString().equals(bonusWord);
    }

    private int loadHighScore() {
        try {
            List<LetterRushScore> scores = LetterRushScore.readScoresFromFile(HIGH_SCORE_FILE_PATH);
            if (scores.isEmpty()) {
                return 0;
            }
            // Get the most recent high score
            LetterRushScore latestScore = scores.stream()
                                      .max(Comparator.comparing(LetterRushScore::getTimestamp))
                                      .orElse(new LetterRushScore(0, 0, 0));
            return latestScore.getHighScore();
        } catch (IOException e) {
            System.err.println("Error loading high score: " + e.getMessage());
            return 0;
        }
    }



    private void saveHighScore() {

        try {
            LetterRushScore scoreEntry = new LetterRushScore(highScore, score, bonusPoints);
            LetterRushScore.appendScoreToFile(scoreEntry, HIGH_SCORE_FILE_PATH);
        } catch (IOException e) {
            System.err.println("Error saving high score: " + e.getMessage());
        }

    }

    private void updateHighScore() {
        if (score > highScore) {
            highScore = score;
            saveHighScore();
        }
    }


    public boolean hasFailed(final String targetWord) {
        // If the target word is already completed, don't mark as failed
        if (hasCompletedTargetWord(targetWord)) {
            System.out.println("hasFailed: Target word completed, returning false.");
            return false;
        }

        if(incorrectClicks > 1 ) {
            System.out.println("hasFailed: Too many incorrect clicks: " + incorrectClicks);
            return true;
        }

        // Check if too many letters have been clicked
        if (collectedTarget.size() > targetWord.length()) {
            System.out.println("hasFailed: Too many letters clicked: " + collectedTarget.size() + " > " + targetWord.length());
            return true;
        }

        // Check if the collected letters match the start of the target word
        StringBuilder collected = new StringBuilder();
        for (char c : collectedTarget) {
            collected.append(c);
        }
        boolean startsWith = targetWord.startsWith(collected.toString());
        System.out.println("hasFailed: Collected: " + collected.toString() + ", Target: " + targetWord + ", Starts with: " + startsWith);
        return !startsWith;
    }

    public int getIncorrectClicks() {
        return incorrectClicks;
    }

    public int getHighScore() {
        return highScore;
    }



    public int getScore() {
        return score;
    }

    public int getBonusPoints() {
        return bonusPoints;
    }

    public void addTargetPoints() {
        score += TARGET_POINTS;
        updateHighScore();
    }

    public void addBonusPoints() {
        score += BONUS_POINTS;
        bonusPoints += BONUS_POINTS;
        updateHighScore();
    }

    public void reset() {
        collectedTarget.clear();
        collectedBonus.clear();
        score = 0;
        bonusPoints = 0;
        incorrectClicks = 0;
    }

}



