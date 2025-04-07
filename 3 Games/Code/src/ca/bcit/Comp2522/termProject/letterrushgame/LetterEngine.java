package ca.bcit.Comp2522.termProject.letterrushgame;

import javafx.animation.AnimationTimer;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the core game mechanics for the LetterRush game, acting as the primary engine driving all
 * gameplay functionality.
 * This class is responsible for initializing and managing game levels, spawning letters and obstacles within
 * a game window defined by pixel dimensions, updating the game state during each frame, and detecting collisions
 * between the player and game elements. It integrates seamlessly with the Player class to track user interactions
 * and scores, the GameUI class to refresh the visual interface, the LevelManager class to handle level progression
 * and timing, and the LetterRush class to coordinate overarching game control, including displaying win/loss alerts.
 * The engine employs a JavaFX AnimationTimer to maintain a consistent game loop, operating at a fixed frame rate
 * determined by nanosecond intervals,ensuring smooth updates to letter positions, obstacle movements, and timer
 * countdowns. It evaluates win conditions (e.g., completing target or bonus words), loss conditions (e.g., obstacle
 * collisions, time expiration, or excessive incorrect clicks), and manages transitions between levels or game states,
 * providing a robust foundation for the LetterRush gameplay experience.
 *
 * @author Sehaj Gill
 * @version 1.0
 */
public class LetterEngine
{
    private static final long   FRAME_DURATION_NANOSECONDS       = 16_666_666L;
    private static final double MIN_DISTANCE_PIXELS              = 40.0;
    private static final int    SPAWN_POSITION_OFFSET_PIXELS     = 40;
    private static final int    MAX_LEVEL_NUMBER                 = 5;
    private static final int    INITIAL_INDEX                    = 0;
    private static final int    BONUS_WORD_INDEX                 = 1;
    private static final String LEVEL_MANAGER_NULL_ERROR_MESSAGE = "LevelManager is null in %s!";
    private static final String GAME_NULL_ERROR_MESSAGE          = "Cannot show %s alert: game is null";
    private static final String GAME_OVER_HEADER                 = "Game Over!";
    private static final String LETTER_BASE_STYLE                = "letter";
    private static final String LETTER_REGULAR_STYLE             = "letter-regular";
    private static final String LETTER_LOCKED_STYLE              = "letter-locked";
    private static final String OBSTACLE_STYLE                   = "obstacle";
    private static final String WORD_PAIR_DELIMITER              = ":";
    private static final String EMPTY_STRING                     = "";
    private static final String TARGET_WORD_LOG_PREFIX           = "Target word set to: ";
    private static final String LEVEL_START_ERROR_PREFIX         = "Failed to start level: ";
    private static final String RESTART_LEVEL_LOG_PREFIX         = "Restarting level: ";
    private static final String BONUS_WORD_COMPLETED_MESSAGE     = "Bonus word completed!";
    private static final String BONUS_ALERT_TYPE                 = "bonus";
    private static final String TARGET_WORD_COMPLETED_LOG_PREFIX = "Target word completed! Current Level: ";
    private static final String GAME_WON_ALERT_TYPE              = "game won";
    private static final String WIN_ALERT_TYPE                   = "win";
    private static final String OBSTACLE_COLLISION_MESSAGE       = "Obstacle collision detected.";
    private static final String LOSS_ALERT_TYPE                  = "loss";
    private static final String TIME_RAN_OUT_MESSAGE             = "Time ran out.";
    private static final String TARGET_LOG_DELIMITER             = ", Target: ";
    private static final String LOG_DELIMITER                    = ", ";
    private static final String INCORRECT_CLICKS_LOG_PREFIX      = "Incorrect clicks: ";
    private static final String WRONG_ORDER_MESSAGE              = "Wrong letter order or too many clicks! Try again?";
    private static final String PLAYER_NULL_ERROR_MESSAGE        = "Player is null in handleLetterClick";
    private static final String NEW_LINE_CHARACTER               = "%n";
    private static final String PLAYER_FAILED_LOG_PREFIX         = "Player failed: wrong letter order or too many " +
                                                                   "clicks. Collected: ";
    private static final String OBSTACLE_TYPE_MISSILE            = LevelManager.OBSTACLE_TYPE_MISSILE;
    private static final String OBSTACLE_TYPE_BOMB               = LevelManager.OBSTACLE_TYPE_BOMB;
    private static final String OBSTACLE_TYPE_SPIKE              = LevelManager.OBSTACLE_TYPE_SPIKE;

    private final Pane           gamePane;
    private final List<Letter>   letters;
    private final List<Obstacle> obstacles;
    private final int            windowWidthPixels;
    private final int            windowHeightPixels;
    private       AnimationTimer timer;
    private       String         targetWord;
    private       String         bonusWord;
    private       Player         player;
    private       LevelManager   levelManager;
    private       boolean        bonusFound;
    private       LetterRush     game;
    private       boolean        gameOver;

    /**
     * Constructs a new LetterEngine instance with specified window dimensions to initialize the game environment.
     * This constructor sets up the game pane as a JavaFX Pane object to hold all visual game elements, initializes
     * empty lists for letters and obstacles, and assigns the provided width and height in pixels to define the game
     * windows boundaries. The bonusFound and gameOver flags are set to false, indicating the game has not yet started
     * or ended This method prepares the engine for subsequent level initialization and gameplay updates, ensuring all
     * core components are ready to manage the LetterRush game mechanics.
     *
     * @param widthPixels  the width of the game window in pixels, defining the horizontal boundary
     * @param heightPixels the height of the game window in pixels, defining the vertical boundary
     */
    public LetterEngine(final int widthPixels,
                        final int heightPixels)
    {
        this.gamePane           = new Pane();
        this.letters            = new ArrayList<>();
        this.obstacles          = new ArrayList<>();
        this.windowWidthPixels  = widthPixels;
        this.windowHeightPixels = heightPixels;
        this.bonusFound         = false;
        this.gameOver           = false;
    }

    /**
     * Sets the LetterRush game instance for this engine to enable communication with the main game controller.
     * This method assigns the provided LetterRush object to the game field, allowing the engine to trigger alerts (e.g.,
     * win, loss, bonus) and manage game-wide state transitions through the LetterRush class. It must be invoked before
     * starting a level to ensure proper integration and functionality, such as displaying game-over notifications or
     * advancing levels, enhancing the engine’s ability to coordinate with the broader game structure.
     *
     * @param gameInstance the LetterRush instance to set for controlling game-wide operations
     */
    public void setGame(final LetterRush gameInstance)
    {
        this.game = gameInstance;
    }

    /**
     * Retrieves the JavaFX Pane containing all game elements, including letters, obstacles, and the UI, for
     * integration into the main scene. This method returns the gamePane, which serves as the visual container for all
     * dynamic elements managed by the LetterEngine. It is used by the LetterRush class to incorporate the engine’s
     * graphical components into the overall game scene graph, ensuring that letters, obstacles, and UI elements are
     * rendered correctly within the game window defined by windowWidthPixels and windowHeightPixels.
     *
     * @return the Pane object containing all game elements for display
     */
    Pane getGamePane()
    {
        return gamePane;
    }

    /**
     * Initiates a new game level by setting up the player, level manager, and game state, and starting the game loop.
     * This method assigns the provided Player and LevelManager instances, validates the levelManager for nullity,
     * and proceeds to initialize the level if valid. It resets the player’s bonus points to INITIAL_INDEX, retrieves
     * the current level configuration from the levelManager, spawns letters and obstacles, updates the GameUI with the
     * level number, target word, and player score, and starts the level timer via LevelManager. The gameOver and
     * bonusFound flags are reset to false, and the AnimationTimer is started to drive gameplay updates. If the
     * levelManager is null or an IllegalStateException occurs (e.g., invalid level data), it logs an error and exits
     * without starting the level.
     *
     * @param playerInstance       the Player instance to track interactions and scores for this level
     * @param uiInstance           the GameUI instance to update with level-specific information
     * @param levelManagerInstance the LevelManager instance providing level data and timing
     */
    void startLevel(final Player playerInstance,
                    final GameUI uiInstance,
                    final LevelManager levelManagerInstance)
    {
        this.player       = playerInstance;
        this.levelManager = levelManagerInstance;

        if(levelManager == null)
        {
            System.err.printf((LEVEL_MANAGER_NULL_ERROR_MESSAGE) + NEW_LINE_CHARACTER,
                              "startLevel");
            return;
        }

        try
        {
            player.setBonusPoints(INITIAL_INDEX);
            final LevelManager.Level level;
            level = levelManager.getCurrentLevel();
            spawnLetters(uiInstance);
            spawnObstacles(level);
            uiInstance.updateLevel(levelManager.getCurrentLevelNumber());
            uiInstance.updateTargetWord(targetWord);
            uiInstance.updateScore(player.getScore());
            levelManager.startTimer();
            gameOver   = false;
            bonusFound = false;
            startTimer(uiInstance);
        }
        catch(final IllegalStateException e)
        {
            System.err.println(LEVEL_START_ERROR_PREFIX + e.getMessage());
        }
    }

    /**
     * Resets the current game level by clearing existing elements and restarting the level with updated state.
     * This method assigns the provided Player and LevelManager instances, checks for a null levelManager, and
     * proceeds if valid. It stops any active AnimationTimer, clears the letters and obstacles lists, removes all
     * children from the gamePane except the UI pane, resets the player for a new level, sets bonus points to
     * INITIAL_INDEX, and updates the UI with the current score. The gameOver and bonusFound flags are reset to false,
     * and the level is restarted by calling startLevel with the provided instances. If the levelManager is null, it
     * logs an error and exits without resetting.
     *
     * @param playerInstance       the Player instance to reset and use for the new level attempt
     * @param uiInstance           the GameUI instance to refresh with updated game information
     * @param levelManagerInstance the LevelManager instance providing level data for the restart
     */
    void resetGame(final Player playerInstance,
                   final GameUI uiInstance,
                   final LevelManager levelManagerInstance)
    {
        this.player       = playerInstance;
        this.levelManager = levelManagerInstance;

        if(levelManager == null)
        {
            System.err.printf((LEVEL_MANAGER_NULL_ERROR_MESSAGE) + NEW_LINE_CHARACTER,
                              "resetGame");
            return;
        }

        if(timer != null)
        {
            timer.stop();
        }

        letters.clear();
        obstacles.clear();
        gamePane.getChildren().clear();
        gamePane.getChildren().add(uiInstance.getUIPane());
        player.resetForNewLevel();
        player.setBonusPoints(INITIAL_INDEX);
        uiInstance.updateScore(player.getScore());
        gameOver   = false;
        bonusFound = false;

        System.out.println(RESTART_LEVEL_LOG_PREFIX + levelManager.getCurrentLevelNumber());

        startLevel(playerInstance,
                   uiInstance,
                   levelManagerInstance);
    }

    /**
     * Spawns letters for the current level based on target and bonus words, positioning them randomly within the game
     * window. This private method clears the existing letters list and gamePane children, adds the UI pane, and
     * retrieves a random word pair from the LevelManager’s current level. It splits the pair into targetWord and
     * bonusWord using WORD_PAIR_DELIMITER,logs the target word, and generates letters for all characters in both words
     * (converted to uppercase). Each letter is positioned randomly within windowWidthPixels and windowHeightPixels,
     * offset by SPAWN_POSITION_OFFSET_PIXELS, ensuring a minimum distance of MIN_DISTANCE_PIXELS from other letters to
     * prevent overlap. Letters are styled with LETTER_BASE_STYLE and LETTER_REGULAR_STYLE, added to the letters list
     * and gamePane, and assigned a mouse click handler via handleLetterClick.
     *
     * @param uiInstance the GameUI instance to integrate its pane into the gamePane during spawning
     */
    private void spawnLetters(final GameUI uiInstance)
    {
        letters.clear();
        gamePane.getChildren().clear();
        gamePane.getChildren().add(uiInstance.getUIPane());

        final List<String> levelWords;
        final String       pair;
        final String[]     words;

        levelWords = levelManager.getCurrentLevel().getWordPairs();
        pair       = levelWords.get((int) (Math.random() * levelWords.size()));
        words      = pair.split(WORD_PAIR_DELIMITER);

        targetWord = words[INITIAL_INDEX];
        System.out.println(TARGET_WORD_LOG_PREFIX + targetWord);

        bonusWord = words.length > BONUS_WORD_INDEX ? words[BONUS_WORD_INDEX] : EMPTY_STRING;
        final String allLetters;
        allLetters = (targetWord + bonusWord).toUpperCase();

        for(final char valueChar : allLetters.toCharArray())
        {
            if(valueChar == WORD_PAIR_DELIMITER.charAt(INITIAL_INDEX))
            {
                continue;
            }

            final double  xPixels;
            final double  yPixels;

            double  tempX;
            double  tempY;
            boolean TooClose;

            do
            {
                TooClose = false;
                tempX      = Math.random() * (windowWidthPixels - SPAWN_POSITION_OFFSET_PIXELS);
                tempY      = Math.random() * (windowHeightPixels - SPAWN_POSITION_OFFSET_PIXELS);

                for(final Letter existingLetter : letters)
                {
                    final double dxPixels;
                    final double dyPixels;

                    dxPixels = existingLetter.getNode().getX() - tempX;
                    dyPixels = existingLetter.getNode().getY() - tempY;

                    if(Math.sqrt(dxPixels * dxPixels + dyPixels * dyPixels) < MIN_DISTANCE_PIXELS)
                    {
                        TooClose = true;
                        break;
                    }
                }
            }
            while(TooClose);

            xPixels = tempX;
            yPixels = tempY;

            final Letter letter;
            letter = new Letter(valueChar,
                                xPixels,
                                yPixels,
                                targetWord.contains(String.valueOf(valueChar)),
                                bonusWord.contains(String.valueOf(valueChar)));
            letter.getNode().getStyleClass().add(LETTER_BASE_STYLE);
            letter.getNode().getStyleClass().add(LETTER_REGULAR_STYLE);
            letters.add(letter);
            gamePane.getChildren().add(letter.getNode());
            letter.getNode().setOnMouseClicked(_ -> handleLetterClick(letter,
                                                                      uiInstance));
        }
    }

    /**
     * Spawns obstacles for the current level based on the provided level configuration, positioning them randomly.
     * This private method clears the obstacles list, retrieves obstacle types from the level’s configuration, and
     * creates new obstacles at random positions within windowWidthPixels and windowHeightPixels. Each obstacle is
     * positioned to maintain a MIN_DISTANCE_PIXELS spacing from all letters, ensuring no overlap at spawn.
     * Obstacles are styled with OBSTACLE_STYLE, added to the obstacles list, and their nodes are incorporated
     * into the gamePane for rendering.
     *
     * @param levelInstance the LevelManager.Level instance containing the obstacle configuration for the current level
     */
    private void spawnObstacles(final LevelManager.Level levelInstance)
    {
        obstacles.clear();

        final List<String>   obstacleTypes;
        final List<Obstacle> newObstacles;

        obstacleTypes = levelInstance.getObstacleConfig();
        newObstacles  = new ArrayList<>();

        for(final String type : obstacleTypes)
        {
            final double xPixels;
            final double yPixels;

            double  tempX;
            double  tempY;
            boolean tooClose;

            do
            {
                tooClose = false;
                tempX    = Math.random() * windowWidthPixels;
                tempY    = Math.random() * windowHeightPixels;

                for(final Letter letter : letters)
                {
                    final double dxPixels;
                    final double dyPixels;

                    dxPixels = letter.getNode().getX() - tempX;
                    dyPixels = letter.getNode().getY() - tempY;

                    if(Math.sqrt(dxPixels * dxPixels + dyPixels * dyPixels) < MIN_DISTANCE_PIXELS)
                    {
                        tooClose = true;
                        break;
                    }
                }
            }
            while(tooClose);

            xPixels = tempX;
            yPixels = tempY;

            final Obstacle obstacle;
            obstacle = createObstacle(type,
                                      xPixels,
                                      yPixels);
            obstacle.getNode().getStyleClass().add(OBSTACLE_STYLE);
            newObstacles.add(obstacle);
        }

        obstacles.addAll(newObstacles);
        gamePane.getChildren().addAll(newObstacles.stream().map(Obstacle::getNode).toList());
    }

    /**
     * Creates an obstacle instance based on the specified type and position, supporting predefined obstacle classes.
     * This private method uses a switch expression to instantiate a Missile, Bomb, or Cactus obstacle based on the
     * provided type string, matching against OBSTACLE_TYPE_MISSILE, OBSTACLE_TYPE_BOMB, or OBSTACLE_TYPE_SPIKE.
     * The obstacle is positioned at the given xPixels and yPixels coordinates. If an unknown type is provided,
     * it throws an IllegalArgumentException with a descriptive message, ensuring only valid obstacle types are
     * created.
     *
     * @param type    the type of obstacle to create ("MISSILE", "BOMB", or "SPIKE")
     * @param xPixels the x-coordinate of the obstacle in pixels
     * @param yPixels the y-coordinate of the obstacle in pixels
     * @return the newly created Obstacle instance
     * @throws IllegalArgumentException if the obstacle type is not recognized
     */
    private Obstacle createObstacle(final String type,
                                    final double xPixels,
                                    final double yPixels)
    {
        return switch(type)
        {
            case OBSTACLE_TYPE_MISSILE -> new Missile(xPixels,
                                                      yPixels);
            case OBSTACLE_TYPE_BOMB -> new Bomb(xPixels,
                                                yPixels);
            case OBSTACLE_TYPE_SPIKE -> new Cactus(xPixels,
                                                   yPixels);
            default -> throw new IllegalArgumentException("Unknown obstacle type: " + type);
        };
    }

    /**
     * Starts the game timer to drive the game loop, ensuring continuous updates at a fixed frame rate.
     * This private method stops any existing AnimationTimer, creates a new one with a frame duration of
     * FRAME_DURATION_NANOSECONDS, and starts it to call updateGame at regular intervals. The timer tracks
     * the last update time to maintain consistency, updating the game state and UI as the game progresses,
     * providing the heartbeat for
     * real-time gameplay mechanics.
     *
     * @param uiInstance the GameUI instance to update during the game loop
     */
    private void startTimer(final GameUI uiInstance)
    {
        if(timer != null)
        {
            timer.stop();
        }

        timer = new AnimationTimer()
        {
            private long lastUpdateNanoSeconds;

            {
                lastUpdateNanoSeconds = INITIAL_INDEX;
            }

            @Override
            public void handle(final long nowNanoSeconds)
            {
                if(nowNanoSeconds - lastUpdateNanoSeconds >= FRAME_DURATION_NANOSECONDS)
                {
                    updateGame(uiInstance,
                               nowNanoSeconds);
                    lastUpdateNanoSeconds = nowNanoSeconds;
                }
            }
        };
        timer.start();
    }

    /**
     * Updates the game state during each frame of the game loop, managing movement, win/loss conditions, and UI
     * refreshes.This private method exits if gameOver is true, otherwise updates all letters and obstacles within the
     * window boundaries, checks for bonus or target word completion, obstacle collisions, timer expiration, or player
     * failure, and adjusts the game state accordingly. On bonus word completion, it awards points, updates the UI,
     * stops the timer, and shows a bonus alert. On target word completion, it advances the level or shows a game-won
     * alert if MAX_LEVEL_NUMBER is reached. On collisions or time-outs, it triggers loss alerts. If the player fails
     * (e.g., wrong order), it logs details and shows a loss alert. The method ensures all game dynamics are processed
     * in real-time.
     *
     * @param uiInstance     the GameUI instance to refresh with current game information
     * @param nowNanoSeconds the current timestamp in nanoseconds for timing updates
     */
    private void updateGame(final GameUI uiInstance,
                            final long nowNanoSeconds)
    {
        if(gameOver)
        {
            return;
        }

        letters.forEach(letter -> letter.updatePosition(windowWidthPixels,
                                                        windowHeightPixels));
        obstacles.forEach(obstacle -> obstacle.update(windowWidthPixels,
                                                      windowHeightPixels));

        if(!bonusFound && player.hasCompletedBonusWord(bonusWord))
        {
            System.out.println(BONUS_WORD_COMPLETED_MESSAGE);
            bonusFound = true;
            player.addBonusPoints();
            uiInstance.updateScore(player.getScore());
            gameOver = true;
            timer.stop();

            final int currentLevelNumber;
            currentLevelNumber = levelManager.getCurrentLevelNumber();
            levelManager.advanceLevel();

            if(game != null)
            {
                game.showBonusAlert(currentLevelNumber);
            }
            else
            {
                System.err.printf((GAME_NULL_ERROR_MESSAGE) + NEW_LINE_CHARACTER,
                                  BONUS_ALERT_TYPE);
            }
            return;
        }

        if(player.hasCompletedTargetWord(targetWord))
        {
            System.out.println(TARGET_WORD_COMPLETED_LOG_PREFIX + levelManager.getCurrentLevelNumber());
            gameOver = true;
            timer.stop();

            final int currentLevelNumber;
            currentLevelNumber = levelManager.getCurrentLevelNumber();

            if(currentLevelNumber >= MAX_LEVEL_NUMBER)
            {
                if(game != null)
                {
                    game.showGameWonAlert();
                }
                else
                {
                    System.err.printf((GAME_NULL_ERROR_MESSAGE) + NEW_LINE_CHARACTER,
                                      GAME_WON_ALERT_TYPE);
                }
            }
            else
            {
                levelManager.advanceLevel();
                if(game != null)
                {
                    game.showWinAlert(currentLevelNumber);
                }
                else
                {
                    System.err.printf((GAME_NULL_ERROR_MESSAGE) + NEW_LINE_CHARACTER,
                                      WIN_ALERT_TYPE);
                }
            }
            return;
        }

        if(checkObstacleCollision())
        {
            System.out.println(OBSTACLE_COLLISION_MESSAGE);
            gameOver = true;
            timer.stop();

            if(game != null)
            {
                game.showLossAlertObstacle();
            }
            else
            {
                System.err.printf((GAME_NULL_ERROR_MESSAGE) + NEW_LINE_CHARACTER,
                                  LOSS_ALERT_TYPE);
            }
            return;
        }

        if(levelManager.updateTimer(nowNanoSeconds,
                                    uiInstance))
        {
            System.out.println(TIME_RAN_OUT_MESSAGE);
            gameOver = true;
            timer.stop();

            if(game != null)
            {
                game.showLossAlertTime();
            }
            else
            {
                System.err.printf((GAME_NULL_ERROR_MESSAGE) + NEW_LINE_CHARACTER,
                                  LOSS_ALERT_TYPE);
            }
            return;
        }

        if(!player.getCollectedTarget().isEmpty() && player.hasFailed(targetWord,
                                                                      bonusWord))
        {
            System.out.println(PLAYER_FAILED_LOG_PREFIX + player.getCollectedTarget() + TARGET_LOG_DELIMITER
                               + targetWord + LOG_DELIMITER + INCORRECT_CLICKS_LOG_PREFIX + player.getIncorrectClicks());
            gameOver = true;
            timer.stop();

            if(game != null)
            {
                game.showLossAlert(GAME_OVER_HEADER,
                                   WRONG_ORDER_MESSAGE);
            }
            else
            {
                System.err.printf((GAME_NULL_ERROR_MESSAGE) + NEW_LINE_CHARACTER,
                                  LOSS_ALERT_TYPE);
            }
        }
    }

    /**
     * Checks for collisions between the player and any obstacles, determining if a game-over condition is met.
     * This private method uses a stream to evaluate whether any obstacle’s collidesWith method returns true when tested
     * against the player instance, indicating a collision. If a collision occurs, it returns true, triggering a loss
     * scenario in the game loop; otherwise, it returns false, allowing gameplay to continue uninterrupted.
     *
     * @return true if the player collides with any obstacle, false otherwise
     */
    private boolean checkObstacleCollision()
    {
        return obstacles.stream().anyMatch(obstacle -> obstacle.collidesWith(player));
    }

    /**
     * Handles a letter click event by updating the player’s state, the letter’s appearance, and the UI accordingly.
     * This private method checks if the player instance is non-null, then invokes the player’s clickLetter method
     * with the clicked letter, targetWord, and bonusWord, updating the player’s score and collected letters.
     * It refreshes the UI with the new score, removes the LETTER_REGULAR_STYLE from the letter’s node, and adds
     * LETTER_LOCKED_STYLE to visually indicate the letter has been clicked and locked. If the player is null,
     * it logs an error message to System.err.
     *
     * @param letterInstance the Letter instance clicked by the player
     * @param uiInstance     the GameUI instance to update with the player’s score
     */
    private void handleLetterClick(final Letter letterInstance,
                                   final GameUI uiInstance)
    {
        if(player != null)
        {
            player.clickLetter(letterInstance,
                               targetWord,
                               bonusWord);
            uiInstance.updateScore(player.getScore());
            letterInstance.getNode().getStyleClass().removeAll(LETTER_REGULAR_STYLE);
            letterInstance.getNode().getStyleClass().add(LETTER_LOCKED_STYLE);
        }
        else
        {
            System.err.println(PLAYER_NULL_ERROR_MESSAGE);
        }
    }
}