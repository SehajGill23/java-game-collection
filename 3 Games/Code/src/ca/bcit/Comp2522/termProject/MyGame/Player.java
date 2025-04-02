package ca.bcit.Comp2522.termProject.MyGame;

import java.util.ArrayList;
import java.util.List;

public class Player
{
    private static final int TARGET_POINTS = 10;
    private static final int BONUS_POINTS = 20;
    private static final double CURSOR_SIZE = 10.0;

    private double cursorX;
    private double cursorY;
    private List<Character> collectedTarget = new ArrayList<>();
    private List<Character> collectedBonus = new ArrayList<>();
    private int score = 0;
    private int bonusPoints = 0;

    public void updateCursorPosition(final double x, final double y) {
        cursorX = x;
        cursorY = y;
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

        final char c = letter.getValue();
        letter.lock();

        if (targetWord.contains(String.valueOf(c))) {
            collectedTarget.add(c);
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

    public boolean hasFailed(final String targetWord) {
        if (collectedTarget.size() > targetWord.length()) {
            return true;
        }
        StringBuilder collected = new StringBuilder();
        for (char c : collectedTarget) {
            collected.append(c);
        }
        return !targetWord.startsWith(collected.toString());
    }

    public int getScore() {
        return score;
    }

    public int getBonusPoints() {
        return bonusPoints;
    }

    public void addTargetPoints() {
        score += TARGET_POINTS;
    }

    public void addBonusPoints() {
        score += BONUS_POINTS;
        bonusPoints += BONUS_POINTS;
    }

    public void reset() {
        collectedTarget.clear();
        collectedBonus.clear();
        score = 0;
        bonusPoints = 0;
    }
}