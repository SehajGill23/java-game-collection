package ca.bcit.comp2522.termproject.letterrushgame;

import javafx.animation.AnimationTimer;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the core game mechanics for the LetterRush game, acting as the
 * primary engine driving all gameplay functionality. This class is responsible
 * for initializing and managing game levels, spawning letters
 * ({@link Letter}) and obstacles ({@link Obstacle}) within a game window
 * defined by pixel dimensions ({@code windowWidthPixels},
 * {@code windowHeightPixels}), updating the game state during each frame,
 * and detecting collisions between the player ({@link Player}) and game
 * elements. It integrates seamlessly with the {@code Player} class to track
 * user interactions and scores, the {@link GameUI} class to refresh the
 * visual interface, the {@link LevelManager} class to handle level
 * progression and timing, and the {@link LetterRush} class to coordinate
 * overarching game control, including displaying win/loss alerts. The engine
 * employs a JavaFX {@link AnimationTimer} to maintain a consistent game loop,
 * operating at a fixed frame rate determined by nanosecond intervals
 * ({@code FRAME_DURATION_NANOSECONDS}), ensuring smooth updates to letter
 * positions, obstacle movements, and timer countdowns. It evaluates win
 * conditions (e.g., completing {@code targetWord} or {@code bonusWord}),
 * loss conditions (e.g., obstacle collisions, time expiration, or excessive
 * incorrect clicks), and manages transitions between levels or game states,
 * providing a robust foundation for the LetterRush gameplay experience.
 *
 * @author Sehaj Gill
 * @version 1.0
 */
class LetterEngine
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
    private static final String OBSTACLE_TYPE_SPIKE              = LevelManager.OBSTACLE_TYPE_CACTUS;

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

    /*
     * Constructs a new {@code LetterEngine} instance with specified window
     * dimensions to initialize the game environment. This constructor sets up
     * the game pane ({@code gamePane}) as a JavaFX {@code Pane} object to hold
     * all visual game elements, initializes empty lists for letters
     * ({@code letters}) and obstacles ({@code obstacles}), and assigns the
     * provided width ({@code widthPixels}) and height ({@code heightPixels})
     * in pixels to define the game windows boundaries. The {@code bonusFound}
     * and {@code gameOver} flags are set to {@code false}, indicating the
     * game has not yet started or ended This method prepares the engine for
     * subsequent level initialization and gameplay updates, ensuring all core
     * components are ready to manage the LetterRush game mechanics.
     *
     * @param widthPixels  the width of the game window in pixels, defining the
     * horizontal boundary
     * @param heightPixels the height of the game window in pixels, defining the
     * vertical boundary
     */
    LetterEngine(final int widthPixels,
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
     * Sets the {@link LetterRush} game instance for this engine to enable
     * communication with the main game controller. This method assigns the
     * provided {@code LetterRush} object ({@code gameInstance}) to the
     * {@code game} field, allowing the engine to trigger alerts (e.g., win,
     * loss, bonus) and manage game-wide state transitions through the
     * {@code LetterRush} class. It must be invoked before starting a level
     * ({@link #startLevel(Player, GameUI, LevelManager)}) to ensure proper
     * integration and functionality, such as displaying game-over
     * notifications or advancing levels, enhancing the engine’s ability to
     * coordinate with the broader game structure.
     *
     * @param gameInstance the {@code LetterRush} instance to set for controlling
     * game-wide operations
     */
    public void setGame(final LetterRush gameInstance)
    {
        this.game = gameInstance;
    }

    /*
     * Retrieves the JavaFX {@code Pane} ({@code gamePane}) containing all game
     * elements, including letters ({@link Letter}), obstacles
     * ({@link Obstacle}), and the UI ({@link GameUI}), for integration into
     * the main scene. This method returns the {@code gamePane}, which serves
     * as the visual container for all dynamic elements managed by the
     * {@code LetterEngine}. It is used by the {@link LetterRush} class to
     * incorporate the engine’s graphical components into the overall game
     * scene graph, ensuring that letters, obstacles, and UI elements are
     * rendered correctly within the game window defined by
     * {@code windowWidthPixels} and {@code windowHeightPixels}.
     *
     * @return the {@code Pane} object ({@code gamePane}) containing all game
     * elements for display
     */
    Pane getGamePane()
    {
        return gamePane;
    }

    /*
     * Initiates a new game level by setting up the player ({@link Player}),
     * level manager ({@link LevelManager}), and game state, and starting the
     * game loop ({@link #startTimer(GameUI)}). This method is called to begin
     * a new level of the LetterRush game, performing the necessary initialization
     * and setup steps.
     *
     * <p>First, the provided {@link Player} instance ({@code playerInstance}) and
     * {@link LevelManager} instance ({@code levelManagerInstance}) are assigned
     * to the corresponding fields of this {@code LetterEngine}. These instances
     * are really important for tracking the player's progress and managing the
     * level-specific data and timing.
     *
     * <p>Next, a critical check is performed to ensure that the {@code levelManager}
     * instance is not null. If it is null, an error message ({@code LEVEL_MANAGER_NULL_ERROR_MESSAGE})
     * is printed to the standard error stream, and the method returns immediately.
     * A valid {@code LevelManager} is essential for retrieving level configurations.
     *
     * <p>If the {@code levelManager} is valid, the method proceeds to a {@code try-catch}
     * block to handle potential {@link IllegalStateException} that might occur during
     * level initialization (e.g., if the level data is invalid). Within the {@code try}
     * block, the following steps are executed:
     * <ol>
     * <li>The player's bonus points are reset to the initial value ({@code INITIAL_INDEX},
     * which is 0) using the {@link Player#setBonusPoints(int)} method. This ensures
     * that bonus points earned in previous levels do not carry over unintentionally.
     * <li>The current level's configuration is retrieved from the {@code levelManager}
     * using the {@link LevelManager#getCurrentLevel()} method, which returns a
     * {@link LevelManager.Level} object containing the level's word pairs and
     * obstacle configuration.
     * <li>The letters for the current level are spawned within the game window by
     * calling the private helper method {@link #spawnLetters(GameUI)}, passing
     * the {@link GameUI} instance ({@code uiInstance}) to allow the letters to
     * be added to the game's visual pane. This method determines the target
     * and bonus words for the level and creates the corresponding {@link Letter}
     * objects at random positions.
     * <li>The obstacles for the current level are spawned based on the level's
     * obstacle configuration by calling the private helper method
     * {@link #spawnObstacles(LevelManager.Level)}, passing the retrieved
     * {@link LevelManager.Level} object. This method creates and positions
     * the various {@link Obstacle} types for the level.
     * <li>The game UI is updated to display the current level number using the
     * {@link GameUI#updateLevel(int)} method, retrieving the current level
     * number from the {@link LevelManager} via {@link LevelManager#getCurrentLevelNumber()}.
     * <li>The game UI is updated to display the current target word using the
     * {@link GameUI#updateTargetWord(String)} method, passing the {@code targetWord}
     * determined during the letter spawning process.
     * <li>The game UI is updated to display the player's initial score for the
     * level using the {@link GameUI#updateScore(int)} method, retrieving the
     * player's current score via {@link Player#getScore()}.
     * <li>The level timer, managed by the {@link LevelManager}, is started using
     * the {@link LevelManager#startTimer()} method. This initiates the countdown
     * for the current level.
     * <li>The {@code gameOver} flag is reset to {@code false}, indicating that the
     * game is now active and not in a terminal state.
     * <li>The {@code bonusFound} flag is reset to {@code false}, indicating that
     * the bonus word has not yet been completed in this level.
     * <li>The game's animation timer is started by calling the private helper
     * method {@link #startTimer(GameUI)}, passing the {@link GameUI} instance.
     * This initiates the game loop, driving the continuous updates of the game
     * state and rendering.
     * </ol>
     * <p>If an {@link IllegalStateException} is caught during any of these initialization
     * steps, an error message ({@code LEVEL_START_ERROR_PREFIX} followed by the exception's
     * message) is printed to the standard error stream, and the method exits, preventing
     * the level from starting in an invalid state.
     *
     * @param playerInstance       the {@link Player} instance that will interact with
     * the game elements and whose score will be tracked.
     * @param uiInstance           the {@link GameUI} instance that will be updated
     * to reflect the current game state (level, score, etc.).
     * @param levelManagerInstance the {@link LevelManager} instance that provides
     * the level-specific data (word pairs, obstacle
     * configurations) and manages the level timer.
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
            final LevelManager.Level level;

            player.setBonusPoints(INITIAL_INDEX);
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

    /*
     * Resets the current game level by clearing existing elements and
     * restarting the level with updated state. This method assigns the provided
     * {@link Player} ({@code playerInstance}) and {@link LevelManager}
     * ({@code levelManagerInstance}) instances, checks for a null
     * {@code levelManager}, and proceeds if valid. It stops any active
     * {@link AnimationTimer} ({@code timer}), clears the letters
     * ({@code letters}) and obstacles ({@code obstacles}) lists, removes all
     * children from the {@code gamePane} except the UI pane (obtained from
     * {@code uiInstance}), resets the player for a new level ({@code player}),
     * sets bonus points to {@code INITIAL_INDEX}, and updates the UI with the
     * current score. The {@code gameOver} and {@code bonusFound} flags are
     * reset to {@code false}, and the level is restarted by calling
     * {@link #startLevel(Player, GameUI, LevelManager)} with the provided
     * instances. If the {@code levelManager} is null, it logs an error and
     * exits without resetting.
     *
     * @param playerInstance       the {@link Player} instance to reset and use
     * for the new level attempt
     * @param uiInstance           the {@link GameUI} instance to refresh with
     * updated game information
     * @param levelManagerInstance the {@link LevelManager} instance providing
     * level data for the restart
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

    /*
     * Spawns letters ({@link Letter}) for the current level based on the target
     * ({@code targetWord}) and bonus ({@code bonusWord}) words, positioning
     * them randomly within the game window while ensuring they do not overlap.
     * This private method is responsible for creating the interactive letter
     * elements that the player will click on to form words.
     *
     * <p>The method begins by clearing any existing letters from the {@code letters}
     * list and removing all child nodes from the {@code gamePane}, except for the
     * UI pane obtained from the {@code uiInstance}. This ensures a clean slate for
     * each new level or level restart.
     *
     * <p>Next, the method retrieves the list of word pairs for the current level
     * from the {@link LevelManager.Level} object obtained via
     * {@link LevelManager#getCurrentLevel()}. A random word pair is selected from
     * this list. Each word pair is expected to be in the format "targetWord:bonusWord"
     * (delimited by {@code WORD_PAIR_DELIMITER}). The selected pair is split into
     * the {@code targetWord} and {@code bonusWord} strings. The {@code targetWord}
     * is then logged to the standard output for debugging or informational purposes
     * using the {@code TARGET_WORD_LOG_PREFIX}. If the word pair does not contain a
     * bonus word (i.e., the split results in only one element), the {@code bonusWord}
     * is set to an empty string ({@code EMPTY_STRING}). Both the {@code targetWord}
     * and {@code bonusWord} are converted to uppercase to ensure case-insensitive
     * matching during gameplay.
     *
     * <p>The method then iterates through each character of the combined uppercase
     * {@code targetWord} and {@code bonusWord}. For each character, it performs the
     * following steps:
     * <ol>
     * <li>A check is made to ensure the character is not the {@code WORD_PAIR_DELIMITER}
     * itself. If it is, the iteration continues to the next character, preventing
     * the delimiter from being created as a letter.
     * <li>A random x-coordinate ({@code tempX}) and y-coordinate ({@code tempY})
     * within the game window are generated. The coordinates are offset by
     * {@code SPAWN_POSITION_OFFSET_PIXELS} to prevent letters from spawning
     * too close to the edges of the window.
     * <li>A collision detection loop ({@code do-while}) is entered to ensure that
     * the newly proposed letter position is not too close to any existing
     * letters in the {@code letters} list. For each existing letter, the
     * Euclidean distance between the existing letter's position and the
     * proposed new position is calculated. If this distance is less than
     * {@code MIN_DISTANCE_PIXELS}, the {@code tooClose} flag is set to {@code true},
     * and the inner loop breaks, causing new random coordinates to be generated.
     * <li>This collision detection loop continues until a valid position is found
     * where the new letter is not too close to any other letters.
     * <li>Once a valid position ({@code xPixels}, {@code yPixels}) is determined,
     * a new {@link Letter} object is created. The constructor is called with
     * the current character ({@code valueChar}), the calculated x and y
     * coordinates, and boolean flags indicating whether this letter is part
     * of the {@code targetWord} and/or the {@code bonusWord} (using the {@code contains()}
     * method of the respective strings).
     * <li>The newly created {@link Letter}'s JavaFX {@code Node} is styled by adding
     * the CSS classes {@code LETTER_BASE_STYLE} and {@code LETTER_REGULAR_STYLE}.
     * These styles likely define the basic appearance of the letters.
     * <li>The created {@link Letter} object is added to the {@code letters} list,
     * which keeps track of all active letters in the game.
     * <li>The JavaFX {@code Node} representing the letter is added as a child to
     * the {@code gamePane}, making it visible in the game scene.
     * <li>An event handler is attached to the letter's {@code Node} to respond to
     * mouse click events. When a letter is clicked, the {@link #handleLetterClick(Letter, GameUI)}
     * method is called, passing the clicked {@link Letter} instance and the
     * {@code uiInstance}. This handler is responsible for updating the game state
     * based on the player's interaction with the letter.
     * </ol>
     * This process ensures that all the necessary letters for the current level's
     * target and bonus words are created, positioned randomly but with sufficient
     * spacing, styled appropriately, and made interactive for the player.
     *
     * @param uiInstance the {@link GameUI} instance, which is used to access the
     * game's visual pane ({@code gamePane}) for adding the letter nodes.
     */
    private void spawnLetters(final GameUI uiInstance)
    {
        letters.clear();
        gamePane.getChildren().clear();
        gamePane.getChildren().add(uiInstance.getUIPane());

        final List<String> levelWords;
        final String       pair;
        final String[]     words;
        final String allLetters;

        levelWords = levelManager.getCurrentLevel().getWordPairs();
        pair       = levelWords.get((int) (Math.random() * levelWords.size()));
        words      = pair.split(WORD_PAIR_DELIMITER);

        targetWord = words[INITIAL_INDEX];
        System.out.println(TARGET_WORD_LOG_PREFIX + targetWord);

        bonusWord = words.length > BONUS_WORD_INDEX ? words[BONUS_WORD_INDEX] : EMPTY_STRING;
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

    /*
     * Spawns obstacles ({@link Obstacle}) for the current level based on the
     * provided level configuration ({@code levelInstance}), positioning them
     * randomly. This private method clears the {@code obstacles} list,
     * retrieves obstacle types from the level’s configuration, and creates new
     * obstacles at random positions within {@code windowWidthPixels} and
     * {@code windowHeightPixels}. Each obstacle is positioned to maintain a
     * {@code MIN_DISTANCE_PIXELS} spacing from all letters ({@code letters}),
     * ensuring no overlap at spawn. Obstacles are styled with
     * {@code OBSTACLE_STYLE}, added to the {@code obstacles} list, and their
     * nodes are incorporated into the {@code gamePane} for rendering.
     *
     * @param levelInstance the {@link LevelManager.Level} instance containing
     * the obstacle configuration for the current level
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

    /*
     * Creates an obstacle instance ({@link Obstacle}) based on the specified
     * {@code type} and position ({@code xPixels}, {@code yPixels}), supporting
     * predefined obstacle classes ({@link Missile}, {@link Bomb},
     * {@link Cactus}). This private method uses a switch expression to
     * instantiate the appropriate obstacle based on the provided {@code type}
     * string, matching against {@code OBSTACLE_TYPE_MISSILE},
     * {@code OBSTACLE_TYPE_BOMB}, or {@code OBSTACLE_TYPE_SPIKE}. The obstacle
     * is positioned at the given {@code xPixels} and {@code yPixels}
     * coordinates. If an unknown {@code type} is provided, it throws an
     * {@code IllegalArgumentException} with a descriptive message, ensuring
     * only valid obstacle types are created.
     *
     * @param type    the type of obstacle to create ("MISSILE", "BOMB", or
     * "SPIKE")
     * @param xPixels the x-coordinate of the obstacle in pixels
     * @param yPixels the y-coordinate of the obstacle in pixels
     * @return the newly created {@code Obstacle} instance
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

    /*
     * Starts the game timer ({@link AnimationTimer}) to drive the game loop,
     * ensuring continuous updates at a fixed frame rate
     * ({@code FRAME_DURATION_NANOSECONDS}). This private method stops any
     * existing {@code AnimationTimer} ({@code timer}), creates a new one with
     * the predefined frame duration, and starts it to call
     * {@link #updateGame(GameUI, long)} at regular intervals. The timer tracks
     * the last update time ({@code lastUpdateNanoSeconds}) to maintain
     * consistency, updating the game state and UI (via {@code uiInstance}) as
     * the game progresses, providing the heartbeat for real-time gameplay
     * mechanics.
     *
     * @param uiInstance the {@link GameUI} instance to update during the game
     * loop
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

    /*
     * Updates the game state during each frame of the game loop, managing
     * movement, win/loss conditions, and UI refreshes. This private method
     * is the central orchestrator of the gameplay logic executed at each tick
     * of the {@link AnimationTimer}. It performs a sequence of checks and
     * updates to ensure the game progresses according to the defined rules and
     * responds to player interactions and environmental events.
     *
     * <p>At the beginning of each update cycle, the method checks the {@code gameOver}
     * flag. If this flag is {@code true}, it signifies that a terminal game state
     * has been reached (e.g., win, loss), and therefore, no further updates to
     * the game state are necessary or should occur. The method returns immediately
     * in this scenario, halting any ongoing game logic until a new game or level
     * is initiated.
     *
     * <p>If the game is not over, the method proceeds to update the positions of
     * all active letters. It iterates through the {@code letters} list, and for
     * each {@link Letter} object, it calls the {@link Letter#updatePosition(int, int)}
     * method. This method is responsible for calculating the new position of the
     * letter based on its current velocity ({@code dxPixels}, {@code dyPixels})
     * and handling collisions with the game window boundaries (defined by
     * {@code windowWidthPixels} and {@code windowHeightPixels}), causing the
     * letters to bounce off the edges.
     *
     * <p>Following the letter updates, the method updates the state of all active
     * obstacles. It iterates through the {@code obstacles} list, and for each
     * {@link Obstacle} object, it calls the {@link Obstacle#update(int, int)}
     * method. This method is specific to each type of obstacle (e.g., {@link Missile},
     * {@link Bomb}, {@link Cactus}) and handles their movement patterns and any
     * internal state changes based on the game environment.
     *
     * <p>After updating the movable game elements, the method evaluates the win
     * condition related to the bonus word. It first checks if the bonus word has
     * not yet been found ({@code !bonusFound}). If this is the case, it then checks
     * if the player has successfully collected all the letters of the {@code bonusWord}
     * in the correct sequence, as determined by the {@link Player#hasCompletedBonusWord(String)}
     * method. If the bonus word is completed:
     * <ol>
     * <li>A notification message ({@code BONUS_WORD_COMPLETED_MESSAGE}) is printed
     * to the standard output for logging purposes.
     * <li>The {@code bonusFound} flag is set to {@code true} to ensure this
     * completion is only processed once per level.
     * <li>The player's score is increased by the bonus points awarded for
     * completing the bonus word, using the {@link Player#addBonusPoints()}
     * method.
     * <li>The game UI is updated to display the player's new score via the
     * {@link GameUI#updateScore(int)} method.
     * <li>The {@code gameOver} flag is set to {@code true}, effectively pausing
     * further gameplay updates for the current level.
     * <li>The game's animation timer ({@code timer}) is stopped to halt the
     * game loop.
     * <li>The current level number is retrieved from the {@link LevelManager}.
     * <li>The {@link LevelManager} is instructed to advance to the next level
     * using the {@link LevelManager#advanceLevel()} method.
     * <li>If the {@link LetterRush} game instance ({@code game}) is not null,
     * a bonus alert is displayed to the user, indicating the level at which
     * the bonus word was completed, via the {@link LetterRush#showBonusAlert(int)}
     * method.
     * <li>If the {@code game} instance is null, an error message ({@code GAME_NULL_ERROR_MESSAGE}
     * with {@code BONUS_ALERT_TYPE}) is printed to the standard error stream.
     * <li>The method then returns, as a significant game state change has occurred.
     * </ol>
     *
     * <p>Next, the method checks if the player has completed the primary target word.
     * This is determined by calling the {@link Player#hasCompletedTargetWord(String)}
     * method with the current {@code targetWord}. If the target word is completed:
     * <ol>
     * <li>A log message ({@code TARGET_WORD_COMPLETED_LOG_PREFIX} with the current
     * level number) is printed to the standard output.
     * <li>The {@code gameOver} flag is set to {@code true}.
     * <li>The game's animation timer ({@code timer}) is stopped.
     * <li>The current level number is retrieved.
     * <li>If the current level number is less than the maximum number of levels
     * ({@code MAX_LEVEL_NUMBER}), the {@link LevelManager} is advanced to the
     * next level. A "win" alert is then displayed to the user, indicating
     * the completed level, via the {@link LetterRush#showWinAlert(int)} method,
     * provided the {@code game} instance is not null. An error message is
     * logged if {@code game} is null.
     * <li>If the current level number is equal to or greater than the maximum number
     * of levels, a "game won" alert is displayed to the user via the
     * {@link LetterRush#showGameWonAlert()} method, again, only if the
     * {@code game} instance is not null. An error message is logged if
     * {@code game} is null.
     * <li>The method then returns.
     * </ol>
     *
     * <p>Following the target word completion check, the method determines if the
     * player has collided with any obstacle by calling the private helper method
     * {@link #checkObstacleCollision()}. If a collision is detected (returns {@code true}):
     * <ol>
     * <li>A collision message ({@code OBSTACLE_COLLISION_MESSAGE}) is printed to
     * the standard output.
     * <li>The {@code gameOver} flag is set to {@code true}.
     * <li>The game's animation timer is stopped.
     * <li>A loss alert specifically indicating an obstacle collision is displayed
     * to the user via the {@link LetterRush#showLossAlertObstacle()} method,
     * if the {@code game} instance is not null. An error message is logged
     * if {@code game} is null.
     * <li>The method then returns.
     * </ol>
     *
     * <p>The method then checks if the game timer, managed by the {@link LevelManager},
     * has expired. This is done by calling the {@link LevelManager#updateTimer(long, GameUI)}
     * method, which updates the timer display on the UI and returns {@code true} if the
     * time has run out. If the timer has expired:
     * <ol>
     * <li>A time-out message ({@code TIME_RAN_OUT_MESSAGE}) is printed to the
     * standard output.
     * <li>The {@code gameOver} flag is set to {@code true}.
     * <li>The game's animation timer is stopped.
     * <li>A loss alert specifically indicating that time ran out is displayed
     * via the {@link LetterRush#showLossAlertTime()} method, if the {@code game}
     * instance is not null. An error message is logged if {@code game} is null.
     * <li>The method then returns.
     * </ol>
     *
     * <p>Finally, the method checks if the player has failed the current level due to
     * incorrect actions. This is determined by checking if the player has collected
     * at least one target letter ({@code !player.getCollectedTarget().isEmpty()}) and
     * then calling the {@link Player#hasFailed(String, String)} method with the
     * {@code targetWord} and {@code bonusWord}. This method in the {@link Player}
     * class likely checks for conditions such as clicking letters out of order or
     * exceeding a maximum number of incorrect clicks. If the player has failed:
     * <ol>
     * <li>A detailed log message is printed to the standard output, including the
     * letters the player collected, the target word, and the number of incorrect
     * clicks (using {@code PLAYER_FAILED_LOG_PREFIX},
     * {@code TARGET_LOG_DELIMITER}, {@code LOG_DELIMITER}, and
     * {@code INCORRECT_CLICKS_LOG_PREFIX}).
     * <li>The {@code gameOver} flag is set to {@code true}.
     * <li>The game's animation timer is stopped.
     * <li>A generic "Game Over!" loss alert with a message prompting the player
     * to try again ({@code WRONG_ORDER_MESSAGE}) is displayed via the
     * {@link LetterRush#showLossAlert(String, String)} method, if the
     * {@code game} instance is not null. An error message is logged if
     * {@code game} is null.
     * </ol>
     *
     * @param uiInstance     the {@link GameUI} instance to refresh with current
     * game information (e.g., timer)
     * @param nowNanoSeconds the current timestamp in nanoseconds, used for timing
     * updates and passed to the {@link LevelManager} for
     * timer management.
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
            System.out.println(PLAYER_FAILED_LOG_PREFIX + player.getCollectedTarget() +
                               TARGET_LOG_DELIMITER
                               + targetWord + LOG_DELIMITER + INCORRECT_CLICKS_LOG_PREFIX +
                               player.getIncorrectClicks());
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

    /*
     * Checks for collisions between the player (cursor) ({@link Player}) and any
     * obstacles ({@link Obstacle}), determining if a game-over condition is
     * met. This private method uses a stream to evaluate whether any obstacle’s
     * {@link Obstacle#collidesWith(Player)} method returns {@code true} when
     * tested against the {@code player} instance, indicating a collision. If
     * a collision occurs, it returns {@code true}, triggering a loss scenario
     * in the game loop; otherwise, it returns {@code false}, allowing
     * gameplay to continue uninterrupted.
     *
     * @return {@code true} if the player collides with any obstacle,
     * {@code false} otherwise
     */
    private boolean checkObstacleCollision()
    {
        return obstacles.stream().anyMatch(obstacle -> obstacle.collidesWith(player));
    }

    /*
     * Handles a letter click event by updating the player’s ({@link Player})
     * state, the letter’s ({@link Letter}) appearance, and the UI
     * ({@link GameUI}) accordingly. This private method checks if the
     * {@code player} instance is non-null, then invokes the player’s
     * {@link Player#clickLetter(Letter, String, String)} method with the clicked
     * {@code letterInstance}, {@code targetWord}, and {@code bonusWord},
     * updating the player’s score and collected letters. It refreshes the UI
     * with the new score, removes the {@code LETTER_REGULAR_STYLE} from the
     * letter’s node, and adds {@code LETTER_LOCKED_STYLE} to visually indicate
     * the letter has been clicked and locked. If the player is null, it logs
     * an error message to {@code System.err}.
     *
     * @param letterInstance the {@link Letter} instance clicked by the player
     * @param uiInstance     the {@link GameUI} instance to update with the
     * player’s score
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