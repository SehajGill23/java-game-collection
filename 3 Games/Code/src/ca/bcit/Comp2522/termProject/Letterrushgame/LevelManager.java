package ca.bcit.Comp2522.termProject.Letterrushgame;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the levels in the LetterRush game, including level progression, timers,
 * and obstacle configurations. The LevelManager loads words from a file, organizes
 * them into levels, and provides access to the current level's configuration, such as
 * word pairs, obstacle counts, timers, and speed multipliers. It also handles timer
 * updates and level advancement.
 *
 * @author Sehaj Gill
 * @version 1.0
 */
final class LevelManager
{
    private static final String   WORDS_FILE_PATH         = "/words.txt";
    private static final String   ERROR_NO_WORDS_LOADED   = "LevelManager: No words loaded from ";
    private static final String   ERROR_INIT_FAILED       = "LevelManager initialization failed: ";
    private static final String   SUCCESS_INIT_PREFIX     = "LevelManager initialized with ";
    private static final String   SUCCESS_INIT_SUFFIX     = " levels";
    private static final String   ERROR_NO_MORE_LEVELS    = "No more levels available";
    private static final int      INITIAL_LEVEL_INDEX     = 0;
    private static final int      OBSTACLE_TYPE_COUNT     = 3;
    private static final int[]    OBSTACLE_COUNTS         = {9, 12, 15, 18, 21};
    private static final int[]    WORD_PAIRS_PER_LEVEL    = {6, 7, 8, 9, 12};
    private static final double   BASE_SPEED_MULTIPLIER   = 1.5;
    private static final double   SPEED_MULTIPLIER_OFFSET = 1.0;
    private static final double   NANOSECONDS_PER_SECOND  = 1_000_000_000.0;
    private static final double[] TIME_LIMITS             = {22.0, 20.0, 16.0, 14.0, 10.0};
    private static final double   MINIMUM_TIME_LEFT       = 0.0;
    static final         String   OBSTACLE_TYPE_MISSILE   = "MISSILE";
    static final         String   OBSTACLE_TYPE_BOMB      = "BOMB";
    static final         String   OBSTACLE_TYPE_SPIKE     = "SPIKE";
    private static final String[] OBSTACLE_TYPES          = {OBSTACLE_TYPE_MISSILE,
                                                             OBSTACLE_TYPE_BOMB,
                                                             OBSTACLE_TYPE_SPIKE};

    private       int                currentLevel = INITIAL_LEVEL_INDEX;
    private       long               startTime;
    private final List<List<String>> levels       = new ArrayList<>();
    private final GameUI             ui;

    /**
     * Constructs a new LevelManager, initializing the levels by loading words from
     * the words file and organizing them into levels based on predefined configurations.
     * Each level contains a subset of word pairs, and the number of word pairs per level
     * is defined by LEVEL_LINES. Prints a success message with the number of levels
     * initialized or an error message if initialization fails.
     */
    LevelManager()
    {
        this.ui = new GameUI();
        try
        {
            final List<String> allWords;
            allWords = ui.loadWords(WORDS_FILE_PATH);
            if(allWords.isEmpty())
            {
                System.err.println(ERROR_NO_WORDS_LOADED + WORDS_FILE_PATH);
            }
            int index;
            index = INITIAL_LEVEL_INDEX;
            for(final int lines : WORD_PAIRS_PER_LEVEL)
            {
                levels.add(allWords.subList(index,
                                            Math.min(index + lines,
                                                     allWords.size())));
                index += lines;
            }
            System.out.println(SUCCESS_INIT_PREFIX + levels.size() + SUCCESS_INIT_SUFFIX);
        }
        catch(Exception e)
        {
            System.err.println(ERROR_INIT_FAILED + e.getMessage());
        }
    }

    /*
     * Retrieves the current level's configuration, including word pairs, obstacle count,
     * timer, and speed multiplier. Throws an IllegalStateException if there are no more
     * levels available.
     *
     * @return the current Level object
     * @throws IllegalStateException if no more levels are available
     */
    Level getCurrentLevel()
    {
        if(currentLevel >= levels.size())
        {
            throw new IllegalStateException(ERROR_NO_MORE_LEVELS);
        }
        return new Level(levels.get(currentLevel),
                         OBSTACLE_COUNTS[currentLevel],
                         TIME_LIMITS[currentLevel],
                         BASE_SPEED_MULTIPLIER * (currentLevel + SPEED_MULTIPLIER_OFFSET));
    }

    /*
     * Gets the current level number (1-based index).
     *
     * @return the current level number (e.g., 1 for the first level)
     */
    int getCurrentLevelNumber()
    {
        return currentLevel + (int)SPEED_MULTIPLIER_OFFSET;
    }

    /*
     * Advances to the next level if there are more levels available.
     * Does nothing if the current level is the last one.
     */
    void advanceLevel()
    {
        if(currentLevel < levels.size() - (int)SPEED_MULTIPLIER_OFFSET)
        {
            currentLevel++;
        }
    }

    /*
     * Updates the game timer based on the elapsed time since the level started.
     * Updates the UI with the remaining time and returns whether the time has run out.
     *
     * @param now the current time in nanoseconds
     * @param ui  the GameUI instance to update the timer display
     * @return true if the time has run out (timeLeft <= 0), false otherwise
     */
    boolean updateTimer(final long now,
                        final GameUI ui)
    {
        final double timeLeft;
        timeLeft = TIME_LIMITS[currentLevel] - (now - startTime) / NANOSECONDS_PER_SECOND;
        ui.updateTimer(Math.max(MINIMUM_TIME_LEFT,
                                timeLeft));
        return timeLeft <= MINIMUM_TIME_LEFT;
    }

    /*
     * Starts the level timer by recording the current time in nanoseconds.
     */
    void startTimer()
    {
        startTime = System.nanoTime();
    }

    /*
     * Resets the level manager to the first level.
     */
    void resetLevel()
    {
        currentLevel = INITIAL_LEVEL_INDEX;
    }

    /*
     * Represents a single level in the LetterRush game, containing word pairs,
     * obstacle configurations, a timer, and a speed multiplier.
     */
    static final class Level
    {
        private final List<String> wordPairs;
        private final int          obstacleCount;
        final         double       timer;
        final         double       speedMultiplier;

        /*
         * Constructs a new Level with the specified word pairs, obstacle count,
         * timer, and speed multiplier.
         *
         * @param wordPairs       the list of word pairs for this level
         * @param obstacleCount   the number of obstacles in this level
         * @param timer           the time limit for this level in seconds
         * @param speedMultiplier the speed multiplier for letters and obstacles
         */
        private Level(final List<String> wordPairs,
                      final int obstacleCount,
                      final double timer,
                      final double speedMultiplier)
        {
            this.wordPairs       = wordPairs;
            this.obstacleCount   = obstacleCount;
            this.timer           = timer;
            this.speedMultiplier = speedMultiplier;
        }

        /*
         * Gets a copy of the word pairs for this level.
         *
         * @return a new ArrayList containing the word pairs
         */
        List<String> getWordPairs()
        {
            return new ArrayList<>(wordPairs);
        }

        /*
         * Generates the obstacle configuration for this level, cycling through
         * the available obstacle types ("MISSILE", "BOMB", "SPIKE").
         *
         * @return a list of obstacle type strings representing the obstacles
         */
        List<String> getObstacleConfig()
        {
            final List<String> config;
            config = new ArrayList<>();

            for(int i = INITIAL_LEVEL_INDEX; i < obstacleCount; i++)
            {
                config.add(OBSTACLE_TYPES[i % OBSTACLE_TYPE_COUNT]);
            }
            return config;
        }
    }
}