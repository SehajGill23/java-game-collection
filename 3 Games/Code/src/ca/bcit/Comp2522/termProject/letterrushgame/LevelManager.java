package ca.bcit.Comp2522.termProject.letterrushgame;

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
    private static final String   ERROR_NO_MORE_LEVELS    = "No more levels available";
    private static final int      INITIAL_LEVEL_INDEX     = 0;
    private static final int      OBSTACLE_TYPE_COUNT     = 3;
    private static final int[]    OBSTACLE_COUNTS         = {9, 12, 15, 18, 21};
    private static final int[]    WORD_PAIRS_PER_LEVEL    = {6, 7, 8, 9, 12};
    private static final double   BASE_SPEED_MULTIPLIER   = 1.5;
    private static final double   SPEED_MULTIPLIER_OFFSET = 1.0;
    private static final double   NANOSECONDS_PER_SECOND  = 1_000_000_000.0;
    private static final double[] TIME_LIMITS             = {22.0, 20.0, 16.0, 14.0, 10.0};
    private static final double   MINIMUM_TIME_LEFT     = 0.0;
    static final         String   OBSTACLE_TYPE_MISSILE = "MISSILE";
    static final         String   OBSTACLE_TYPE_BOMB    = "BOMB";
    static final         String   OBSTACLE_TYPE_CACTUS  = "SPIKE";
    private static final String[] OBSTACLE_TYPES        = {OBSTACLE_TYPE_MISSILE,
                                                           OBSTACLE_TYPE_BOMB,
                                                           OBSTACLE_TYPE_CACTUS};

    private       int                currentLevel = INITIAL_LEVEL_INDEX;
    private       long               startTime;
    private final List<List<String>> levels       = new ArrayList<>();
    private final GameUI             ui;

    /*
     * Constructs a new LevelManager. This constructor initializes the game levels
     * by attempting to load word pairs from the file specified by {@link #WORDS_FILE_PATH}.
     * The loaded words are then divided into levels based on the number of word pairs
     * defined in the {@link #WORD_PAIRS_PER_LEVEL} array. Each element in this array
     * specifies the number of word pairs for the corresponding level.
     *
     * <p>The constructor also initializes a {@link GameUI} instance, which is used
     * to load the words from the file. If the word loading is successful and words
     * are found, the levels are populated. A success message indicating the number
     * of levels initialized is printed to the standard output.
     *
     * <p>If an exception occurs during the word loading process (e.g., {@link Exception}
     * if the file cannot be read), an error message is printed to the standard error
     * stream, detailing the initialization failure and the cause. If no words are
     * loaded from the file, a specific error message is also printed to the standard
     * error stream.
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
        }
        catch(Exception e)
        {
            System.err.println(ERROR_INIT_FAILED + e.getMessage());
        }
    }

    /*
     * Retrieves the configuration for the current game level. This includes
     * the list of word pairs for the level, the number of obstacles to be
     * spawned, the time limit for the level, and a speed multiplier that affects
     * the movement speed of letters and obstacles.
     *
     * <p>The method first checks if the {@code currentLevel} index is within
     * the bounds of the {@code levels} list. If {@code currentLevel} is greater
     * than or equal to the number of levels available, it signifies that the
     * game has progressed beyond the defined levels, and an {@link IllegalStateException}
     * is thrown with a message indicating that no more levels are available.
     * This ensures that the game does not attempt to access non-existent level
     * data.
     *
     * <p>If the {@code currentLevel} is valid, a new {@link LevelManager.Level}
     * object is created and returned. The parameters for this {@code Level}
     * object are derived from the current level index:
     * <ul>
     * <li>The word pairs for the current level are retrieved from the
     * {@code levels} list using the {@code currentLevel} as the index.
     * <li>The number of obstacles for the current level is determined by
     * accessing the {@code OBSTACLE_COUNTS} array at the {@code currentLevel}
     * index. This array defines the number of obstacles for each level.
     * <li>The time limit for the current level, in seconds, is obtained from
     * the {@code TIME_LIMITS} array at the {@code currentLevel} index.
     * <li>A speed multiplier for the current level is calculated. It starts
     * with a {@code BASE_SPEED_MULTIPLIER} and is increased based on the
     * current level number. The formula used is
     * {@code BASE_SPEED_MULTIPLIER * (currentLevel + SPEED_MULTIPLIER_OFFSET)}.
     * This ensures that the game becomes progressively more challenging as
     * the player advances through the levels.
     * </ul>
     *
     * @return a new {@link LevelManager.Level} object containing the configuration
     * for the current game level. This object encapsulates the word pairs,
     * obstacle count, time limit, and speed multiplier for the level.
     * @throws IllegalStateException if the game has progressed beyond the defined
     * levels (i.e., if there are no more levels available).
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
     * Returns the current level number. This method returns a 1-based index
     * representing the player's current progress through the game levels.
     * The first level is represented by the number 1, the second by 2, and so on.
     *
     * <p>The level number is calculated by adding the {@link #SPEED_MULTIPLIER_OFFSET}
     * (which is 1.0) to the {@link #currentLevel} index. Since {@code currentLevel}
     * is a 0-based index (starting at 0 for the first level), adding 1 provides
     * the human-readable 1-based level number.
     *
     * @return the current level number as an integer. For example, if the player
     * is on the first level ({@code currentLevel} is 0), this method will
     * return 1.
     */
    int getCurrentLevelNumber()
    {
        return currentLevel + (int)SPEED_MULTIPLIER_OFFSET;
    }

    /*
     * Advances the game to the next level. This method increments the
     * {@code currentLevel} index, effectively progressing the player to the
     * subsequent challenge.
     *
     * <p>The method first checks if there are more levels available in the
     * {@code levels} list. The condition for advancement is that the
     * {@code currentLevel} index must be strictly less than the size of the
     * {@code levels} list minus the {@code SPEED_MULTIPLIER_OFFSET} (which is 1.0
     * cast to an int, so effectively 1). This check ensures that the game does
     * not attempt to advance beyond the last defined level.
     *
     * <p>If the current level is not the last one, the {@code currentLevel}
     * index is incremented by one, moving the game to the next level in the
     * sequence. If the current level is already the last one (or beyond, though
     * the {@link #getCurrentLevel()} method should prevent this), this method
     * does nothing, and the game remains at the current level.
     *
     * <p>It is important to note that this method only updates the internal
     * level index. It does not handle any of the logic associated with starting
     * a new level, such as resetting the timer, spawning new letters and
     * obstacles, or updating the UI. These actions are typically performed
     * by other parts of the game engine in response to the level advancement.
     */
    void advanceLevel()
    {
        if(currentLevel < levels.size() - (int)SPEED_MULTIPLIER_OFFSET)
        {
            currentLevel++;
        }
    }

    /*
     * Updates the game timer and the UI display. This method calculates the
     * remaining time for the current level based on the provided current time
     * and the recorded start time of the level. It then updates the timer display
     * in the game's user interface.
     *
     * <p>The remaining time is calculated by subtracting the elapsed time (current
     * time minus the level's start time) from the time limit defined for the
     * current level in the {@link #TIME_LIMITS} array. The elapsed time is converted
     * from nanoseconds to seconds by dividing by {@link #NANOSECONDS_PER_SECOND}.
     * The {@link Math#max(double, double)} function is used to ensure that the
     * displayed remaining time does not go below {@link #MINIMUM_TIME_LEFT} (which is 0.0).
     *
     * <p>The calculated remaining time is then passed to the {@link GameUI#updateTimer(double)}
     * method to update the visual representation of the timer in the game.
     *
     * <p>Finally, the method returns a boolean value indicating whether the time
     * for the current level has run out. This is determined by checking if the
     * calculated {@code timeLeft} is less than or equal to {@link #MINIMUM_TIME_LEFT}.
     *
     * @param now the current time in nanoseconds, obtained from {@link System#nanoTime()}.
     * This value is used to calculate the elapsed time since the level started.
     * @param ui  the {@link GameUI} instance that is responsible for updating the
     * timer display in the game window. This instance is used to call
     * the {@link GameUI#updateTimer(double)} method.
     * @return {@code true} if the remaining time for the current level is less than
     * or equal to 0, indicating that the time has run out; otherwise,
     * returns {@code false}.
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
     * Starts the timer for the current level. This method records the current
     * system time in nanoseconds using {@link System#nanoTime()} and stores it
     * in the {@link #startTime} field. This recorded time serves as the reference
     * point for calculating the elapsed time during the level using the
     * {@link #updateTimer(long, GameUI)} method.
     *
     * <p>This method should be called at the beginning of each new level or when
     * a level is resumed after a pause, to ensure accurate tracking of the time
     * limit for that level.
     */
    void startTimer()
    {
        startTime = System.nanoTime();
    }

    /*
     * Resets the level manager to the beginning of the game by setting the
     * {@code currentLevel} index back to its initial value, which is defined
     * by {@link #INITIAL_LEVEL_INDEX} (typically 0).
     *
     * <p>This method is used to restart the game or return to the first level
     * after the game has ended or the player has chosen to start over. It effectively
     * undoes any level progression that has occurred.
     *
     * <p>Note that this method only resets the level index. It does not handle
     * any other game state that might need to be reset upon starting a new game,
     * such as the player's score, collected letters, or the state of the game timer.
     * These aspects are managed elsewhere in the game engine.
     */
    void resetLevel()
    {
        currentLevel = INITIAL_LEVEL_INDEX;
    }

    /*
     * Represents a single level in the LetterRush game, containing word pairs,
     * obstacle configurations, a timer, and a speed multiplier. Instances of
     * this static inner class are created by the {@link LevelManager} to encapsulate
     * the specific settings for each level of the game.
     */
    static final class Level
    {
        private final List<String> wordPairs;
        private final int          obstacleCount;
        final         double       timer;
        final         double       speedMultiplier;

        /*
         * Constructs a new {@code Level} object. This private constructor is used
         * to initialize a level with its specific configuration details. These details
         * include the set of word pairs that will be used in the level, the number
         * of obstacles that will appear, the time limit within which the player
         * must complete the level, and a speed multiplier that affects the game's
         * pace for this particular level.
         *
         * <p>The constructor directly assigns the provided values to the corresponding
         * final fields of the {@code Level} object. Once a {@code Level} object is
         * created, its configuration remains constant for the duration of that level.
         *
         * @param wordPairs       A {@link List} of {@link String} objects, where each
         * string represents a word pair (e.g., "target:bonus")
         * that the player will interact with in this level.
         * @param obstacleCount   An integer specifying the number of obstacles that
         * will be present in this level. The types of these
         * obstacles are determined elsewhere (e.g., in the
         * {@link #getObstacleConfig()} method).
         * @param timer           A double representing the time limit for completing
         * this level, measured in seconds. The player must
         * achieve the level's objectives within this time frame.
         * @param speedMultiplier A double value that acts as a multiplier for the
         * speed of moving game elements such as letters and
         * obstacles in this level. A higher multiplier indicates
         * a faster game pace.
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
         * Returns a new list containing the word pairs for this level. This method
         * provides access to the word challenges for the current level. By returning
         * a new {@link ArrayList}, it ensures that the original {@code wordPairs}
         * list within the {@code Level} object cannot be directly modified by the
         * caller, thus maintaining the immutability of the level's configuration.
         *
         * <p>Each string in the returned list represents a word pair, typically
         * in the format "targetWord:bonusWord". The game logic will then process
         * these pairs to spawn letters and determine scoring.
         *
         * @return a new {@link ArrayList} of {@link String} objects, where each
         * element is a word pair for the current level.
         */
        List<String> getWordPairs()
        {
            return new ArrayList<>(wordPairs);
        }

        /*
         * Generates the configuration for the obstacles in this level. This method
         * determines the types of obstacles that will be present based on the
         * {@code obstacleCount} for the level and cycles through the available
         * obstacle types defined in the {@code OBSTACLE_TYPES} array.
         *
         * <p>The method creates a new {@link ArrayList} of {@link String} objects
         * to store the configuration. It then iterates {@code obstacleCount} times.
         * In each iteration, it selects an obstacle type from the {@code OBSTACLE_TYPES}
         * array using the modulo operator (`%`). This ensures that the obstacle types
         * are distributed cyclically. For example, if {@code OBSTACLE_TYPES} contains
         * "MISSILE", "BOMB", and "SPIKE", the generated configuration for 5 obstacles
         * would be ["MISSILE", "BOMB", "SPIKE", "MISSILE", "BOMB"].
         *
         * <p>The generated list of obstacle type strings can then be used by other
         * parts of the game engine to create and manage the obstacle game objects
         * for this level.
         *
         * @return a new {@link List} of {@link String} objects, where each string
         * represents the type of obstacle for this level (e.g., "MISSILE",
         * "BOMB", "SPIKE"). The size of the list will be equal to the
         * {@code obstacleCount} for the level.
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