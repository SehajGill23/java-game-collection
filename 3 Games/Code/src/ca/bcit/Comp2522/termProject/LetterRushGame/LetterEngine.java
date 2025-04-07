package ca.bcit.Comp2522.termProject.LetterRushGame;

import javafx.animation.AnimationTimer;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

/**
 * The LetterEngine class manages the core game mechanics for the LetterRush game.
 * It handles level initialization, letter and obstacle spawning, game updates, and collision detection.
 * This class integrates with Player, GameUI, LevelManager, and LetterRush to drive the game loop.
 *
 * @author Sehaj Gill
 * @version 1.0
 */
public class LetterEngine
{
    private static final long   FRAME_DURATION_NANO_SEC          = 16_666_666;
    private static final double MIN_DISTANCE_PIXELS              = 40.0;
    private static final int    SPAWN_POSITION_OFFSET_PIXELS     = 40;
    private static final int    MAX_LEVEL                        = 5;
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
    private static final String PLAYER_FAILED_LOG_PREFIX         = "Player failed: wrong letter order or too "
                                                                   + "many clicks. Collected: ";
    private static final String TARGET_LOG_DELIMITER             = ", Target: ";
    private static final String LOG_DELIMITER                    = ", ";
    private static final String INCORRECT_CLICKS_LOG_PREFIX      = "Incorrect clicks: ";
    private static final String WRONG_ORDER_MESSAGE              = "Wrong letter order or too many clicks! Try again?";
    private static final String PLAYER_NULL_ERROR_MESSAGE        = "Player is null in handleLetterClick";
    private static final String NEW_LINE_CHARACTER               = "%n";
    private static final String OBSTACLE_TYPE_MISSILE            = LevelManager.OBSTACLE_TYPE_MISSILE;
    private static final String OBSTACLE_TYPE_BOMB               = LevelManager.OBSTACLE_TYPE_BOMB;
    private static final String OBSTACLE_TYPE_SPIKE              = LevelManager.OBSTACLE_TYPE_SPIKE;

    private final Pane           gamePane;
    private final List<Letter>   letters;
    private final List<Obstacle> obstacles;
    private final int            windowWidth;
    private final int            windowHeight;
    private       AnimationTimer timer;
    private       String         targetWord;
    private       String         bonusWord;
    private       Player         player;
    private       LevelManager   levelManager;
    private       boolean        bonusFound;
    private       LetterRush     game;
    private       boolean        isGameOver;

    /**
     * Constructs a new LetterEngine instance with the specified window dimensions.
     * Initializes the game pane, letter and obstacle lists, and sets the game to a non-running state.
     * This constructor prepares the engine for level initialization and game updates.
     *
     * @param width  the width of the game window in pixels
     * @param height the height of the game window in pixels
     */
    public LetterEngine(final int width,
                        final int height)
    {
        this.windowWidth  = width;
        this.windowHeight = height;
        this.gamePane     = new Pane();
        this.letters      = new ArrayList<>();
        this.obstacles    = new ArrayList<>();
        this.bonusFound   = false;
        this.isGameOver   = false;
    }

    /**
     * Sets the LetterRush game instance for this engine.
     * This allows the engine to communicate with the main game controller to display alerts and manage game state.
     * Must be called before starting a level to ensure proper integration with the game.
     *
     * @param game the LetterRush instance to set
     */
    public void setGame(final LetterRush game)
    {
        this.game = game;
    }

    /*
     * Retrieves the game pane containing all game elements (letters, obstacles, UI).
     * The pane is used by the LetterRush class to integrate the game visuals into the main scene.
     * This method provides access to the visual components managed by the engine.
     *
     * @return the Pane containing the game elements
     */
    Pane getGamePane()
    {
        return gamePane;
    }

    /*
     * Starts a new level by initializing the player, level manager, and game state.
     * Spawns letters and obstacles, updates the UI, and starts the game timer.
     * Handles errors if the level manager is null or level data is invalid.
     *
     * @param player the Player instance to use for this level
     * @param ui the GameUI instance for updating the user interface
     * @param levelManager the LevelManager instance for accessing level data
     */
    void startLevel(final Player player,
                    final GameUI ui,
                    final LevelManager levelManager)
    {
        this.player       = player;
        this.levelManager = levelManager;
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
            spawnLetters(ui);
            spawnObstacles(level);
            ui.updateLevel(levelManager.getCurrentLevelNumber());
            ui.updateTargetWord(targetWord);
            ui.updateScore(player.getScore());
            levelManager.startTimer();
            isGameOver = false;
            bonusFound = false;
            startTimer(ui);
        }
        catch(final IllegalStateException e)
        {
            System.err.println(LEVEL_START_ERROR_PREFIX + e.getMessage());
        }
    }

    /*
     * Resets the current game level by clearing existing game elements and restarting the level.
     * Updates the player, UI, and level manager, and ensures the game state is fully reset.
     * Handles errors if the level manager is null and stops any running timer.
     *
     * @param player the Player instance to reset for the new level
     * @param ui the GameUI instance for updating the user interface
     * @param levelManager the LevelManager instance for accessing level data
     */
    void resetGame(final Player player,
                   final GameUI ui,
                   final LevelManager levelManager)
    {
        this.player       = player;
        this.levelManager = levelManager;
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
        gamePane.getChildren().add(ui.getUIPane());
        player.resetForNewLevel();
        player.setBonusPoints(INITIAL_INDEX);
        ui.updateScore(player.getScore());
        isGameOver = false;
        bonusFound = false;

        System.out.println(RESTART_LEVEL_LOG_PREFIX + levelManager.getCurrentLevelNumber());

        startLevel(player,
                   ui,
                   levelManager);
    }

    /*
     * Spawns letters for the current level based on the target and bonus words.
     * Clears existing letters, selects a random word pair from the level manager, and positions letters randomly.
     * Ensures letters are not too close to each other using MIN_DISTANCE_PIXELS for spacing.
     * This method is intended for internal use within the class to initialize level elements.
     *
     * @param ui the GameUI instance for integrating the UI pane into the game pane
     */
    private void spawnLetters(final GameUI ui)
    {
        letters.clear();
        gamePane.getChildren().clear();
        gamePane.getChildren().add(ui.getUIPane());

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

        for(final char c : allLetters.toCharArray())
        {
            if(c == WORD_PAIR_DELIMITER.charAt(INITIAL_INDEX))
            {
                continue;
            }
            double  x, y;
            boolean tooClose;
            do
            {
                tooClose = false;
                x        = Math.random() * (windowWidth - SPAWN_POSITION_OFFSET_PIXELS);
                y        = Math.random() * (windowHeight - SPAWN_POSITION_OFFSET_PIXELS);
                for(Letter existing : letters)
                {
                    double dx = existing.getNode().getX() - x;
                    double dy = existing.getNode().getY() - y;
                    if(Math.sqrt(dx * dx + dy * dy) < MIN_DISTANCE_PIXELS)
                    {
                        tooClose = true;
                        break;
                    }
                }
            }
            while(tooClose);

            final Letter letter;
            letter = new Letter(c,
                                x,
                                y,
                                targetWord.contains(String.valueOf(c)),
                                bonusWord.contains(String.valueOf(c)));
            letter.getNode().getStyleClass().add(LETTER_BASE_STYLE);
            letter.getNode().getStyleClass().add(LETTER_REGULAR_STYLE);
            letters.add(letter);
            gamePane.getChildren().add(letter.getNode());
            letter.getNode().setOnMouseClicked(event -> handleLetterClick(letter,
                                                                          ui));
        }
    }

    /*
     * Spawns obstacles for the current level based on the level configuration.
     * Clears existing obstacles, positions new obstacles randomly, and ensures they are not too close to letters.
     * Uses MIN_DISTANCE_PIXELS to maintain spacing between obstacles and letters.
     * This method is intended for internal use within the class to initialize level elements.
     *
     * @param level the Level instance containing the obstacle configuration
     */
    private void spawnObstacles(final LevelManager.Level level)
    {
        obstacles.clear();
        final List<String>   obstacleTypes;
        final List<Obstacle> newObstacles;

        obstacleTypes = level.getObstacleConfig();
        newObstacles  = new ArrayList<>();

        for(String type : obstacleTypes)
        {
            double  x;
            double  y;
            boolean tooClose;
            do
            {
                tooClose = false;
                x        = Math.random() * windowWidth;
                y        = Math.random() * windowHeight;
                for(final Letter letter : letters)
                {
                    double dx = letter.getNode().getX() - x;
                    double dy = letter.getNode().getY() - y;
                    if(Math.sqrt(dx * dx + dy * dy) < MIN_DISTANCE_PIXELS)
                    {
                        tooClose = true;
                        break;
                    }
                }
            }
            while(tooClose);

            Obstacle obstacle;
            obstacle = createObstacle(type,
                                      x,
                                      y);
            obstacle.getNode().getStyleClass().add(OBSTACLE_STYLE);
            newObstacles.add(obstacle);
        }

        obstacles.addAll(newObstacles);
        gamePane.getChildren().addAll(newObstacles.stream().map(Obstacle::getNode).toList());
    }

    /*
     * Creates an obstacle based on the specified type and position.
     * Supports missile, bomb, and spike (cactus) obstacles, and throws an exception for unknown types.
     * This method is intended for internal use within the class to instantiate obstacles.
     *
     * @param type the type of obstacle ("MISSILE", "BOMB", or "SPIKE")
     * @param x the x-coordinate of the obstacle
     * @param y the y-coordinate of the obstacle
     * @return the created Obstacle instance
     * @throws IllegalArgumentException if the obstacle type is unknown
     */
    private Obstacle createObstacle(final String type,
                                    final double x,
                                    final double y)
    {
        return switch(type)
        {
            case OBSTACLE_TYPE_MISSILE -> new Missile(x, y);
            case OBSTACLE_TYPE_BOMB -> new Bomb(x, y);
            case OBSTACLE_TYPE_SPIKE -> new Cactus(x, y);
            default -> throw new IllegalArgumentException("Unknown obstacle type: " + type);
        };
    }

    /*
     * Starts the game timer to drive the game loop.
     * Stops any existing timer, creates a new AnimationTimer, and starts it to update the game at regular intervals.
     * The update interval is controlled by FRAME_DURATION_NANO_SEC (nanoseconds).
     * This method is intended for internal use within the class to manage game updates.
     *
     * @param ui the GameUI instance for updating the user interface during the game loop
     */
    private void startTimer(final GameUI ui)
    {
        if(timer != null)
        {
            timer.stop();
        }

        timer = new AnimationTimer()
        {
            private long lastUpdate = INITIAL_INDEX;

            @Override
            public void handle(final long now)
            {
                if(now - lastUpdate >= FRAME_DURATION_NANO_SEC)
                {
                    updateGame(ui,
                               now);
                    lastUpdate = now;
                }
            }
        };
        timer.start();
    }

    /*
     * Updates the game state during each frame of the game loop.
     * Handles letter and obstacle movement, checks for win/loss conditions, and updates the UI.
     * Manages game-over scenarios, such as completing the target word, hitting an obstacle, or running out of time.
     * This method is intended for internal use within the class to drive the game loop.
     *
     * @param ui the GameUI instance for updating the user interface
     * @param now the current timestamp in nanoseconds
     */
    private void updateGame(final GameUI ui,
                            final long now)
    {
        if(isGameOver)
        {
            return;
        }

        letters.forEach(letter -> letter.updatePosition(windowWidth,
                                                        windowHeight));
        obstacles.forEach(obstacle -> obstacle.update(windowWidth,
                                                      windowHeight));

        if(!bonusFound && player.hasCompletedBonusWord(bonusWord))
        {
            System.out.println(BONUS_WORD_COMPLETED_MESSAGE);
            bonusFound = true;
            player.addBonusPoints();
            ui.updateScore(player.getScore());
            isGameOver = true;
            timer.stop();
            int currentLevel;
            currentLevel = levelManager.getCurrentLevelNumber();
            levelManager.advanceLevel();
            if(game != null)
            {
                game.showBonusAlert(currentLevel);
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
            isGameOver = true;
            timer.stop();
            int currentLevel;
            currentLevel = levelManager.getCurrentLevelNumber();
            if(currentLevel >= MAX_LEVEL)
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
            }
            if(game != null)
            {
                game.showWinAlert(currentLevel);
            }
            else
            {
                System.err.printf((GAME_NULL_ERROR_MESSAGE) + NEW_LINE_CHARACTER,
                                  WIN_ALERT_TYPE);
            }
            return;
        }

        if(checkObstacleCollision())
        {
            System.out.println(OBSTACLE_COLLISION_MESSAGE);
            isGameOver = true;
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

        if(levelManager.updateTimer(now,
                                    ui))
        {
            System.out.println(TIME_RAN_OUT_MESSAGE);
            isGameOver = true;
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
            System.out.println(PLAYER_FAILED_LOG_PREFIX + player.getCollectedTarget() +
                               TARGET_LOG_DELIMITER + targetWord + LOG_DELIMITER + INCORRECT_CLICKS_LOG_PREFIX
                               + player.getIncorrectClicks());
            isGameOver = true;
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

    /*
     * Checks for collisions between the player and any obstacles.
     * Returns true if a collision is detected, indicating a game-over condition.
     * This method is intended for internal use within the class to handle collision detection.
     *
     * @return true if the player collides with an obstacle, false otherwise
     */
    private boolean checkObstacleCollision()
    {
        return obstacles.stream().anyMatch(obstacle -> obstacle.collidesWith(player));
    }

    /*
     * Handles a letter click event by updating the player's state and the letter's appearance.
     * Updates the UI to reflect the player's score and changes the letter's style to indicate it has been clicked.
     * Handles errors if the player instance is null and logs them to the console.
     * This method is intended for internal use within the class to manage letter interactions.
     *
     * @param letter the Letter instance that was clicked
     * @param ui the GameUI instance for updating the user interface
     */
    private void handleLetterClick(final Letter letter,
                                   final GameUI ui)
    {
        if(player != null)
        {
            player.clickLetter(letter,
                               targetWord,
                               bonusWord);
            ui.updateScore(player.getScore());
            letter.getNode().getStyleClass().removeAll(LETTER_REGULAR_STYLE);
            letter.getNode().getStyleClass().add(LETTER_LOCKED_STYLE);
        }
        else
        {
            System.err.println(PLAYER_NULL_ERROR_MESSAGE);
        }
    }
}