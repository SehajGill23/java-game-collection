package ca.bcit.Comp2522.termProject.MyGame;

import java.util.ArrayList;
import java.util.List;

public class LevelManager
{
    private static final String WORDS_FILE_PATH = "/words.txt";
    private static final int[] OBSTACLE_COUNTS =  {9, 12, 15, 18, 21};
    private static final double[] TIMERS = {22.0, 20.0, 16.0, 14.0, 10.0};
    private static final int[] LEVEL_LINES = {6, 7, 8, 9, 12};
    private static final double SPEED_FACTOR = 1.5;

    private final List<List<String>> levels = new ArrayList<>();
    private int currentLevel = 0;
    private long startTime;
    private final GameUI ui;

    public LevelManager() {
        this.ui = new GameUI();
        try {
            final List<String> allWords = ui.loadWords(WORDS_FILE_PATH);
            if (allWords == null || allWords.isEmpty()) {
                System.err.println("LevelManager: No words loaded from " + WORDS_FILE_PATH);
            }
            int index = 0;
            for (final int lines : LEVEL_LINES) {
                levels.add(allWords.subList(index, Math.min(index + lines, allWords.size())));
                index += lines;
            }
            System.out.println("LevelManager initialized with " + levels.size() + " levels");
        } catch (Exception e) {
            System.err.println("LevelManager initialization failed: " + e.getMessage());
        }
    }

    public Level getCurrentLevel() {
        if (currentLevel >= levels.size()) {
            throw new IllegalStateException("No more levels available");
        }
        return new Level(levels.get(currentLevel), OBSTACLE_COUNTS[currentLevel],
                         TIMERS[currentLevel], SPEED_FACTOR * (currentLevel + 1));
    }

    public int getCurrentLevelNumber() {
        return currentLevel + 1;
    }

    public void advanceLevel() {
        if (currentLevel < levels.size() - 1) {
            currentLevel++;
        }
    }

    public void reset() {

    }

    public boolean updateTimer(final long now, final GameUI ui) {
        final double timeLeft = TIMERS[currentLevel] - (now - startTime) / 1_000_000_000.0;
        ui.updateTimer(Math.max(0, timeLeft));
        return timeLeft <= 0;
    }

    public void startTimer() {
        startTime = System.nanoTime();
    }

    public static final class Level {
        private final List<String> wordPairs;
        private final int obstacleCount;
        private final double timer;
        private final double speedMultiplier;

        private Level(final List<String> wordPairs, final int obstacleCount,
                      final double timer, final double speedMultiplier) {
            this.wordPairs = wordPairs;
            this.obstacleCount = obstacleCount;
            this.timer = timer;
            this.speedMultiplier = speedMultiplier;
        }

        public List<String> getWordPairs() {
            return new ArrayList<>(wordPairs);
        }

        public List<ObstacleType> getObstacleConfig() {
            final List<ObstacleType> config = new ArrayList<>();
            for (int i = 0; i < obstacleCount; i++) {
                config.add(ObstacleType.values()[i % 3]);
            }
            return config;
        }
    }
    public void resetLevel() {
        currentLevel = 0;
    }

    public enum ObstacleType {
        MISSILE, BOMB, SPIKE
    }
}