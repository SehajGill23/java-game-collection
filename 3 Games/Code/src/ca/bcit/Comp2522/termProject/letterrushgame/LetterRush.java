package ca.bcit.Comp2522.termProject.letterrushgame;

import ca.bcit.Comp2522.termProject.wordgame.Main;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * The {@code LetterRush} class represents the main game controller for the
 * LetterRush game. It orchestrates the various components of the game,
 * including the game engine, player, user interface, and level management.
 * This class is responsible for initializing these components, managing the
 * game flow (from the main menu to gameplay and game overstates), and
 * handling user interactions such as starting a new game, restarting a level,
 * quitting the game, and changing the visual theme. It leverages JavaFX for
 * its graphical user interface and manages the transitions between different
 * scenes (menu and game). The class also handles alerts for game over
 * conditions, level completion, bonus word discovery, and overall game victory.
 * It adheres to a singleton pattern to ensure that only one instance of the
 * game controller exists throughout the application's lifecycle.
 *
 * <p>The {@code LetterRush} class implements the {@code Runnable} interface,
 * which is used to execute the level start process on a separate thread,
 * providing a small delay before the game screen is presented to the user.
 * This helps in managing the application flow and ensures that the JavaFX
 * application thread remains responsive. The {@code run()} method, inherited
 * from {@code Runnable}, contains the logic for this asynchronous operation.
 *
 * <p>The class utilizes several constants to define the dimensions and layout
 * of the game window and UI elements, as well as text messages and CSS paths.
 * These constants promote maintainability and readability of the code.
 *
 * @author Sehaj Gill
 * @version 1.0
 */
public final class LetterRush implements Runnable
{
    private static final int      WINDOW_WIDTH_PIXELS         = 1000;
    private static final int      WINDOW_HEIGHT_PIXELS        = 600;
    private static final int      SCORE_X_PIXELS              = 80;
    private static final int      SCORE_Y_PIXELS              = 210;
    private static final int      BONUS_SCORE_X_PIXELS        = 400;
    private static final int      HIGH_SCORE_X_PIXELS         = 740;
    private static final int      INSTRUCTIONS_X_PIXELS       = 50;
    private static final int      INSTRUCTIONS_Y_PIXELS       = 450;
    private static final int      BUTTON_START_X_PIXELS       = 200;
    private static final int      BUTTON_RETURN_X_PIXELS      = 580;
    private static final int      BUTTON_Y_PIXELS             = 320;
    private static final int      BUTTON_WIDTH_PIXELS         = 200;
    private static final int      BUTTON_HEIGHT_PIXELS        = 50;
    private static final int      GAME_BUTTON_Y_OFFSET_PIXELS = 80;
    private static final int      RESTART_BUTTON_X_PIXELS     = 90;
    private static final int      QUIT_BUTTON_X_PIXELS        = 420;
    private static final int      THEME_BUTTON_X_PIXELS       = 737;
    private static final int      THREAD_SLEEP_MS             = 100;
    private static final int      INCREMENTING_BY_ONE         = 1;
    private static final int      WINDOW_ADJUSTMENT_PIXELS    = 100;
    private static final int      INITIAL_SCORE               = 0;
    private static final String   CSS_PATH                    = "/letterStyles.css";
    private static final String   TITLE                       = "LetterRush";
    private static final String   TITLE_MENU                  = TITLE + " - Menu";
    private static final String   SCORE_TEXT_PREFIX           = "Score: ";
    private static final String   BONUS_SCORE_TEXT_PREFIX     = "Bonus Score: ";
    private static final String   HIGH_SCORE_TEXT_PREFIX      = "High Score: ";
    private static final String   SCORE_TEXT_STYLE            = "score-text";
    private static final String   INSTRUCTIONS_TEXT_STYLE     = "instructions-text";
    private static final String   MENU_BUTTON_STYLE           = "menu-button";
    private static final String   GAME_PANE_STYLE             = "game-pane";
    private static final String   GAME_BUTTON_STYLE           = "game-button";
    private static final String   ALERT_STYLE                 = "alert";
    private static final String   BUTTON_START_TEXT           = "Start Letter Rush";
    private static final String   BUTTON_RESTART_TEXT         = "Restart Current Level";
    private static final String   BUTTON_QUIT_TEXT            = "Return to Game Menu";
    private static final String   BUTTON_THEME_TEXT           = "Change Current theme";
    private static final String   GAME_OVER_TITLE             = "Game Over";
    private static final String   GAME_OVER_HEADER            = "Game Over!";
    private static final String   OBSTACLE_MESSAGE            = "You hit an obstacle! Try again?";
    private static final String   TIME_UP_MESSAGE             = "Time’s up! Try again?";
    private static final String   ERROR_HEADER                = "Initialization Error";
    private static final String   ERROR_MESSAGE               = "LevelManager is null. Check resources and restart.";
    private static final String   CONGRATS_TITLE              = "Congratulations!";
    private static final String   BONUS_TITLE                 = "Bonus Found!";
    private static final String   BONUS_HEADER                = "Bonus Word Found! Extra Points!";
    private static final String   PROCEED_BUTTON              = "Proceed";
    private static final String   RETURN_BUTTON               = "Return to Main Menu";
    private static final String   NULL_LEVEL_MANAGER          = "levelManager is null";
    private static final String   GOAT_MESSAGE                = "You are the GOAT 🐐, You've Won the Game! 🥳";
    private static final String   INSTRUCTIONS_TEXT           = """
                                                                Instructions:
                                                                - Click letters to form the target word (white).
                                                                - Complete the target word before time runs out.
                                                                - There’s a hidden bonus word—find it for extra points!
                                                                - Avoid obstacles (missile, bomb, cactus) that move randomly.
                                                                - Use buttons below the game to restart, quit, or change theme.
                                                                - Failure resets the level to 1 and score to 0,"""
                                                                + " regardless of current level";
    private static final String[] THEMES                      = {"/background2.png",
                                                                 "/background1.png",
                                                                 "/background3.png",
                                                                 "/background4.png"};
    private              boolean  isRunning                   = false;

    private static LetterRush   instance;
    private final  LetterEngine engine;
    private final  Player       player;
    private final  GameUI       ui;
    private final  LevelManager levelManager;
    private        Pane         root;
    private        Stage        stage;
    private        Scene        menuScene;
    private        Text         scoreText;
    private        Text         bonusScoreText;
    private        Text         highScoreText;
    private        int          currentThemeIndex = 0;
    public         int          scoreAtLevelStart;

    /**
     * Constructs a new {@code LetterRush} instance, initializing all the
     * core game components. This includes creating instances of the
     * {@code LetterEngine}, {@code Player}, {@code GameUI}, and
     * {@code LevelManager}. It also establishes the connection between the
     * {@code LetterEngine} and this {@code LetterRush} instance, allowing
     * the engine to communicate game events. The initial score at the start
     * of any level is set to the default {@code INITIAL_SCORE}. This
     * constructor is private to enforce the singleton pattern.
     */
    public LetterRush()
    {
        this.engine       = new LetterEngine(WINDOW_WIDTH_PIXELS,
                                             WINDOW_HEIGHT_PIXELS - WINDOW_ADJUSTMENT_PIXELS);
        this.player       = new Player();
        this.ui           = new GameUI();
        this.levelManager = new LevelManager();
        this.engine.setGame(this);
        this.scoreAtLevelStart = INITIAL_SCORE;
    }

    /**
     * Launches the LetterRush game application. This static method serves as the
     * primary entry point for initiating the game's user interface. It ensures
     * that a single instance of the {@code LetterRush} game controller exists
     * throughout the application's lifecycle by implementing a singleton pattern.
     * It then proceeds to display the main game menu to the user.
     *
     * <p>The method first checks if an instance of {@code LetterRush} ({@code instance})
     * has already been created. If not, a new instance is created using the private
     * constructor. This ensures that only one {@code LetterRush} object manages the
     * game flow and state.
     *
     * <p>Next, the method checks if the current thread of execution is the JavaFX
     * Application Thread. This is crucial because all updates to the JavaFX user
     * interface must be performed on this specific thread to avoid concurrency
     * issues and ensure proper rendering.
     *
     * <p>If the current thread is NOT the JavaFX Application Thread, the method uses
     * {@code Platform.runLater()} to schedule the execution of the {@code showMenu()}
     * method on the JavaFX Application Thread. The {@code showMenu()} method is
     * responsible for initializing and displaying the main game menu to the user.
     * By using {@code Platform.runLater()}, we ensure that the menu is created and
     * shown at the appropriate time and on the correct thread.
     *
     * <p>If the current thread IS already the JavaFX Application Thread, the
     * {@code showMenu()} method is called directly. This scenario might occur if
     * {@code launchGame()} is called from within the JavaFX application lifecycle.
     *
     * <p>In summary, this method guarantees that the {@code LetterRush} game controller
     * is initialized as a singleton and that the main game menu is displayed correctly
     * on the JavaFX Application Thread, regardless of the thread from which
     * {@code launchGame()} is initially called. This setup is essential for the
     * proper functioning of the JavaFX-based game.
     */
    public static void launchGame()
    {
        if(instance == null)
        {
            instance = new LetterRush();
        }
        if(!Platform.isFxApplicationThread())
        {
            Platform.runLater(instance::showMenu);
        }
        else
        {
            instance.showMenu();
        }
    }

    /*
     * Displays a standard loss alert to the player. This alert is used to
     * inform the player that they have failed the current game level, either
     * by colliding with an obstacle or by running out of time. The alert
     * presents a title and a content message to the player. Upon the player
     * acknowledging the alert, their current game score is reset to zero,
     * their progress within the current level is cleared, and the game is
     * stopped, returning the player to the main menu. This method ensures
     * that all UI updates are performed on the JavaFX Application Thread.
     *
     * <p>The alert is created as a JavaFX {@code Alert} of type
     * {@code INFORMATION}, which typically presents a simple "OK" button for
     * the user to acknowledge. The title of the alert is set to the
     * predefined constant {@code GAME_OVER_TITLE}, and the header text is set
     * to the {@code header} parameter provided to this method. The main content
     * of the alert is the {@code content} parameter, which should provide
     * specific information about why the player lost the level (e.g., "You hit
     * an obstacle!" or "Time's up!").
     *
     * <p>To maintain a consistent visual style throughout the game, the
     * stylesheets of the alert's dialog pane are updated to include the game's
     * main CSS file ({@code CSS_PATH}), and the {@code ALERT_STYLE} CSS class
     * is added to the dialog pane's style class list.
     *
     * <p>After the alert is displayed and the player closes it (by clicking "OK"),
     * the following critical game state updates occur:
     * <ol>
     * <li>The player's current score, which might have accumulated during the
     * failed level attempt, is immediately reset to zero using the
     * {@code player.setScore(0)} method. This ensures that when the player
     * restarts or starts a new game, they begin with a clean slate in terms
     * of score.</li>
     * <li>The player's state related to the current level is reset using the
     * {@code player.resetForNewLevel()} method. This typically involves
     * clearing any progress made within the level, such as collected items,
     * partially formed words, or any other level-specific data that the
     * player might have interacted with. This prepares the player object for
     * the start of a new level attempt.</li>
     * <li>The game is stopped and the application returns to the main menu.
     * This is achieved by calling the {@code stopGame()} method, which handles
     * the necessary UI transitions and resets any game-related flags or states.</li>
     * </ol>
     *
     * <p>It is crucial that this method is executed on the JavaFX Application
     * Thread. The use of {@code Platform.runLater(() -> { ... })} ensures that
     * all the UI-related operations (alert creation, styling, and display)
     * are performed on the correct thread, preventing potential concurrency
     * issues and ensuring a responsive user interface.
     *
     * @param header  the main title or heading text to be displayed on the alert.
     * This should be a concise message indicating the game over
     * condition (e.g., "Game Over!").
     * @param content the detailed message providing specific information about
     * the reason for the player's loss in the current level
     * (e.g., "You hit an obstacle!" or "Time ran out!").
     */
    void showLossAlert(String header,
                       String content)
    {
        Platform.runLater(() ->
                          {
                              Alert alert;
                              alert = new Alert(Alert.AlertType.INFORMATION);

                              alert.setTitle(GAME_OVER_TITLE);
                              alert.setHeaderText(header);
                              alert.setContentText(content);
                              alert.getDialogPane().getStylesheets().add(CSS_PATH);
                              alert.getDialogPane().getStyleClass().add(ALERT_STYLE);
                              alert.showAndWait();
                              player.setScore(0);
                              player.resetForNewLevel();
                              stopGame();
                          });
    }


    /*
     * Displays a specific loss alert that is shown when the player's game
     * entity collides with an obstacle within the game. This method uses
     * predefined constants for the alert's header and content, specifically
     * {@code GAME_OVER_HEADER} and {@code OBSTACLE_MESSAGE}, to inform the
     * player about the nature of their failure. It delegates the actual
     * display of the alert to the more general {@code showLossAlert} method.
     * This method should be called from the JavaFX Application Thread.
     */
    void showLossAlertObstacle()
    {
        showLossAlert(GAME_OVER_HEADER,
                      OBSTACLE_MESSAGE);
    }

    /*
     * Displays a specific loss alert that is shown when the player fails a
     * level due to the game timer expiring. This method utilizes the
     * predefined constants {@code GAME_OVER_HEADER} and {@code TIME_UP_MESSAGE}
     * to communicate the reason for the game over to the player. The actual
     * presentation of the alert is handled by the {@code showLossAlert} method.
     * This method must be executed on the JavaFX Application Thread.
     */
    void showLossAlertTime()
    {
        showLossAlert(GAME_OVER_HEADER,
                      TIME_UP_MESSAGE);
    }

    /*
     * Displays a congratulatory alert to the player when they successfully complete a
     * game level. This alert informs the player of their achievement on the current
     * level and presents them with two distinct options for continuing their game
     * experience: to proceed directly to the next challenging level or to return to
     * the main game menu.
     *
     * <p>The alert is presented using a JavaFX {@code Alert} of type
     * {@code Confirmation}, which includes buttons corresponding to the "Proceed"
     * and "Return to Main Menu" options. The visual styling of this alert,
     * including its stylesheets and CSS classes, is managed to maintain a
     * consistent look and feel with the rest of the game's user interface.
     *
     * <p>When the player interacts with the alert by clicking one of the buttons,
     * a specific action is performed based on their choice. If the player selects
     * the "Proceed" button, the following sequence of actions occurs:
     * <ol>
     * <li>The player's game state, specifically any level-specific progress
     * (such as partially formed words or collected items), is reset to its
     * initial state for a new level. This ensures that the next level starts
     * fresh without any carry-over from the completed level.</li>
     * <li>The player's current score is recorded as the starting score for the
     * new level. This is achieved by assigning the current value of the
     * player's score to the {@code scoreAtLevelStart} instance variable.
     * This is important for scenarios where a level restart might occur,
     * in which case the score should revert to this recorded starting value.</li>
     * <li>A new thread is created and started to initiate the next game level.
     * This is done by creating a new {@code Thread} with a reference to
     * the {@code run()} method of this {@code LetterRush} instance.
     * Executing the level start process on a separate thread prevents
     * the JavaFX application thread from being blocked, ensuring a smooth
     * and responsive user interface. The {@code run()} method itself
     * introduces a small delay before actually starting the level, which
     * can be useful for visual transitions or to ensure that the UI has
     * fully updated.</li>
     * </ol>
     *
     * <p>If the player chooses the "Return to Main Menu" button on the alert, the
     * game is stopped. This is accomplished by calling the {@code stopGame()}
     * method, which handles the necessary cleanup and transitions back to the
     * main menu scene.
     *
     * <p>This method must be called from the JavaFX Application Thread to ensure
     * that all UI updates and interactions are handled correctly and safely. It
     * leverages {@code Platform.runLater()} to encapsulate the alert creation
     * and handling logic, although in this specific implementation, it is assumed
     * that the calling context is already within the JavaFX Application Thread
     * (as indicated by the previous use of {@code Platform.runLater()} in methods
     * that call this one).
     *
     * @param level the number of the game level that the player has just successfully
     * completed. This number is used in the alert's header to inform
     * the player of their achievement.
     */
    void showWinAlert(int level)
    {
        Platform.runLater(() ->
                          {
                              Alert alert;
                              alert = new Alert(Alert.AlertType.CONFIRMATION);
                              alert.setTitle(CONGRATS_TITLE);
                              alert.setHeaderText("Congrats on finishing Level " + level + "!");
                              alert.setContentText("Would you like to proceed to Level " + (level + 1) + " "
                                                   + "or return to the main menu?");
                              alert.getDialogPane().getStylesheets().add(CSS_PATH);
                              alert.getDialogPane().getStyleClass().add(ALERT_STYLE);

                              alert.getButtonTypes().setAll(new javafx.scene.control.ButtonType(PROCEED_BUTTON),
                                                            new javafx.scene.control.ButtonType(RETURN_BUTTON));

                              alert.showAndWait().ifPresent(response ->
                                                            {
                                                                if(response.getText().equals(PROCEED_BUTTON))
                                                                {
                                                                    player.resetForNewLevel();
                                                                    scoreAtLevelStart = player.getScore();
                                                                    new Thread(this::run).start();
                                                                }
                                                                else
                                                                {
                                                                    stopGame();
                                                                }
                                                            });
                          });
    }

    /*
     * Displays an alert to the player when a bonus word is successfully identified
     * during gameplay. This alert informs the player that they have earned extra
     * points as a reward for finding the hidden bonus word and presents them with
     * options to either continue to the next level of the game or to return to
     * the main menu.
     *
     * <p>The alert is implemented using a JavaFX {@code Alert} of type
     * {@code Confirmation}, providing the player with two choices represented by
     * buttons: "Proceed" and "Return to Main Menu". The visual presentation
     * of the alert is styled using the game's CSS file ({@code CSS_PATH}) and the
     * specific {@code ALERT_STYLE} CSS class to ensure consistency with the
     * overall user interface design.
     *
     * <p>Upon the player's interaction with the alert, the game responds based on
     * the selected option:
     * <ul>
     * <li>If the player clicks the "Proceed" button:
     * <ul>
     * <li>The player's game state, specifically any level-dependent
     * progress, is reset to its initial configuration for a new level
     * using the {@code player.resetForNewLevel()} method. This ensures
     * a clean start to the subsequent level.</li>
     * <li>The player's current score is recorded as the starting score
     * for the next level by assigning it to the {@code scoreAtLevelStart}
     * instance variable. This is crucial for maintaining score integrity
     * in case of level restarts.</li>
     * <li>The next level of the game is initiated by calling the
     * {@code engine.startLevel()} method, passing the current
     * {@code player}, {@code ui} (GameUI), and {@code levelManager}
     * instances. This starts the gameplay for the subsequent level.</li>
     * </ul>
     * </li>
     * <li>If the player clicks the "Return to Main Menu" button:
     * <ul>
     * <li>The current game session is terminated, and the application
     * returns to the main menu. This is achieved by invoking the
     * {@code stopGame()} method, which handles the necessary UI
     * transitions and game state resets.</li>
     * </ul>
     * </li>
     * </ul>
     *
     * <p>This method is designed to be executed on the JavaFX Application Thread.
     * The use of {@code Platform.runLater(() -> { ... })} ensures that all UI-related
     * operations, such as creating, styling, and displaying the alert, as well as
     * handling button clicks and their subsequent actions, are performed on the
     * appropriate thread. This is essential for maintaining a responsive and
     * stable user interface.
     *
     * @param level the number of the current game level at the time the bonus word
     * was found. This information is used in the alert's content
     * to inform the player about the progression of the game.
     */
    void showBonusAlert(int level)
    {
        Platform.runLater(() ->
                          {
                              Alert alert;
                              alert = new Alert(Alert.AlertType.CONFIRMATION);
                              alert.setTitle(BONUS_TITLE);
                              alert.setHeaderText(BONUS_HEADER);
                              alert.setContentText("You earned extra points! Proceed to Level " + (level + 1) +
                                                   " or return to the main menu?");
                              alert.getDialogPane().getStylesheets().add(CSS_PATH);
                              alert.getDialogPane().getStyleClass().add(ALERT_STYLE);

                              alert.getButtonTypes().setAll(new javafx.scene.control.ButtonType(PROCEED_BUTTON),
                                                            new javafx.scene.control.ButtonType(RETURN_BUTTON));

                              alert.showAndWait().ifPresent(response ->
                                                            {
                                                                if(response.getText().equals(PROCEED_BUTTON))
                                                                {
                                                                    player.resetForNewLevel();
                                                                    scoreAtLevelStart = player.getScore();
                                                                    engine.startLevel(player,
                                                                                      ui,
                                                                                      levelManager);
                                                                }
                                                                else
                                                                {
                                                                    stopGame();
                                                                }
                                                            });
                          });
    }

    /*
     * Displays an alert to the player signifying that they have successfully
     * completed all the levels of the LetterRush game. This alert serves as the
     * ultimate congratulatory message, informing the player of their victory and
     * displaying their final accumulated score across all the levels.
     *
     * <p>The alert is presented using a JavaFX {@code Alert} of type
     * {@code INFORMATION}, which typically includes a simple "OK" button for
     * the player to acknowledge their achievement. The title of the alert is set
     * to the predefined constant {@code CONGRATS_TITLE}, and the header text
     * proudly announces the player's triumph using the {@code GOAT_MESSAGE}.
     * The main content of the alert displays a congratulatory message indicating
     * that all levels have been completed, along with the player's final game
     * score, retrieved using the {@code player.getScore()} method.
     *
     * <p>To ensure a consistent visual experience, the alert's dialog pane is
     * styled using the game's primary CSS file ({@code CSS_PATH}) and the
     * {@code ALERT_STYLE} CSS class.
     *
     * <p>Once the player acknowledges the alert by closing it, the game session
     * is terminated, and the application returns to the main menu. This action
     * is performed by calling the {@code stopGame()} method, which handles the
     * necessary cleanup of the game state and the transition back to the menu
     * scene.
     *
     * <p>It is essential that this method is executed on the JavaFX Application
     * Thread. The use of {@code Platform.runLater(() -> { ... })} ensures that
     * all UI-related operations, including the creation, styling, and display
     * of the alert, are performed safely and correctly on the appropriate thread,
     * maintaining the responsiveness of the application.
     */
    void showGameWonAlert()
    {
        Platform.runLater(() ->
                          {
                              Alert alert;
                              alert = new Alert(Alert.AlertType.INFORMATION);
                              alert.setTitle(CONGRATS_TITLE);
                              alert.setHeaderText(GOAT_MESSAGE);
                              alert.setContentText("You have completed all levels of LetterRush! "
                                                   + "Final Score: " + player.getScore());
                              alert.getDialogPane().getStylesheets().add(CSS_PATH);
                              alert.getDialogPane().getStyleClass().add(ALERT_STYLE);
                              alert.showAndWait();
                              stopGame();
                          });
    }


    /*
     * Initializes and displays the main menu of the game. This menu serves as
     * the entry point for players to start a new game or potentially navigate
     * to other sections of the application (though currently, the only other
     * functionality is to return to console input, likely for exiting). The
     * menu displays the player's current score, accumulated bonus points, and
     * the highest score achieved in past games. It also provides instructions
     * on how to play the LetterRush game and includes interactive buttons for
     * starting a new game and returning to the console.
     *
     * <p>The method first checks if the JavaFX {@code Stage} object ({@code stage})
     * has been initialized. If it hasn't, a new {@code Stage} is created. This
     * stage will be the window in which the menu is displayed.
     *
     * <p>A new JavaFX {@code Pane} ({@code menuPane}) is created to serve as the
     * layout container for all the elements of the menu. Its preferred width and
     * height are set to the game's window dimensions ({@code WINDOW_WIDTH_PIXELS}
     * and {@code WINDOW_HEIGHT_PIXELS}).
     *
     * <p>{@code Text} objects are created to display the player's current score,
     * bonus score, and high score. Each of these {@code Text} objects is positioned
     * at predefined coordinates ({@code SCORE_X_PIXELS}, {@code SCORE_Y_PIXELS} for
     * the main score; {@code BONUS_SCORE_X_PIXELS}, {@code SCORE_Y_PIXELS} for the
     * bonus score; and {@code HIGH_SCORE_X_PIXELS}, {@code SCORE_Y_PIXELS} for the
     * high score) and their text content is set using the respective prefixes
     * ({@code SCORE_TEXT_PREFIX}, {@code BONUS_SCORE_TEXT_PREFIX},
     * {@code HIGH_SCORE_TEXT_PREFIX}) concatenated with the player's current
     * score, bonus points, and high score, retrieved from the {@code player} object.
     * Each of these {@code Text} objects is also styled using the {@code SCORE_TEXT_STYLE}
     * CSS class.
     *
     * <p>Another {@code Text} object ({@code instructionsText}) is created to
     * display the game's instructions. It is positioned at
     * {@code INSTRUCTIONS_X_PIXELS} and {@code INSTRUCTIONS_Y_PIXELS}, and its
     * text content is set to the predefined {@code INSTRUCTIONS_TEXT} constant.
     * This text object is styled using the {@code INSTRUCTIONS_TEXT_STYLE} CSS class.
     *
     * <p>Two interactive {@code Button} objects are created using the private
     * {@code createButton()} helper method:
     * <ol>
     * <li>A "Start Letter Rush" button, positioned at
     * {@code BUTTON_START_X_PIXELS} and {@code BUTTON_Y_PIXELS}, with the text
     * {@code BUTTON_START_TEXT}. When this button is clicked, the {@code startGame()}
     * method of this {@code LetterRush} instance is executed, initiating a new
     * game session.</li>
     * <li>A "Return to Game Menu" button, positioned at
     * {@code BUTTON_RETURN_X_PIXELS} and {@code BUTTON_Y_PIXELS}, with the text
     * {@code RETURN_BUTTON}. When this button is clicked, an anonymous {@code Runnable}
     * is executed, which closes the current game stage (window) and calls the
     * {@code Main.ConsoleInput()} method. This likely returns the user to a
     * console-based interface or allows exiting the application.</li>
     * </ol>
     *
     * <p>All the created UI elements (score texts, instructions text, and buttons)
     * are added as children to the {@code menuPane}, making them part of the menu's
     * visual layout.
     *
     * <p>A new JavaFX {@code Scene} ({@code menuScene}) is created with the
     * {@code menuPane} as its root node and the game's window dimensions. The
     * game's main CSS stylesheet ({@code CSS_PATH}) is applied to this scene to
     * style all its elements according to the defined styles.
     *
     * <p>The title of the game stage is set to the {@code TITLE_MENU} constant,
     * the {@code menuScene} is set as the current scene for the stage, and the
     * stage's resizability is disabled to maintain a consistent window size.
     * Finally, the game stage is made visible to the user by calling {@code stage.show()}.
     *
     * <p>The {@code running} flag of this {@code LetterRush} instance is set to
     * false, indicating that the game is currently in the menu state and not actively
     * running a game level.
     */
    private void showMenu()
    {
        if(stage == null)
        {
            stage = new Stage();
        }

        final Pane menuPane;
        menuPane = new Pane();
        menuPane.setPrefSize(WINDOW_WIDTH_PIXELS,
                             WINDOW_HEIGHT_PIXELS);
        // Score Display
        scoreText = new Text(SCORE_X_PIXELS,
                             SCORE_Y_PIXELS,
                             SCORE_TEXT_PREFIX + player.getScore());
        scoreText.getStyleClass().add(SCORE_TEXT_STYLE);

        bonusScoreText = new Text(BONUS_SCORE_X_PIXELS,
                                  SCORE_Y_PIXELS,
                                  BONUS_SCORE_TEXT_PREFIX + player.getBonusPoints());
        bonusScoreText.getStyleClass().add(SCORE_TEXT_STYLE);

        highScoreText = new Text(HIGH_SCORE_X_PIXELS,
                                 SCORE_Y_PIXELS,
                                 HIGH_SCORE_TEXT_PREFIX + player.getHighScore());
        highScoreText.getStyleClass().add(SCORE_TEXT_STYLE);

        final Text instructionsText;
        instructionsText = new Text(INSTRUCTIONS_X_PIXELS,
                                    INSTRUCTIONS_Y_PIXELS,
                                    INSTRUCTIONS_TEXT);
        instructionsText.getStyleClass().add(INSTRUCTIONS_TEXT_STYLE);

        final Button startButton = createButton(BUTTON_START_X_PIXELS,
                                                BUTTON_Y_PIXELS,
                                                BUTTON_WIDTH_PIXELS,
                                                BUTTON_HEIGHT_PIXELS,
                                                BUTTON_START_TEXT,
                                                this::startGame);
        final Button returnButton = createButton(BUTTON_RETURN_X_PIXELS,
                                                 BUTTON_Y_PIXELS,
                                                 BUTTON_WIDTH_PIXELS,
                                                 BUTTON_HEIGHT_PIXELS,
                                                 RETURN_BUTTON,
                                                 () ->
                                                 {
                                                     stage.close();
                                                     Main.ConsoleInput();
                                                 });

        menuPane.getChildren().addAll(scoreText,
                                      bonusScoreText,
                                      highScoreText,
                                      instructionsText,
                                      startButton,
                                      returnButton);

        menuScene = new Scene(menuPane,
                              WINDOW_WIDTH_PIXELS,
                              WINDOW_HEIGHT_PIXELS);
        menuScene.getStylesheets().add(CSS_PATH);

        stage.setTitle(TITLE_MENU);
        stage.setScene(menuScene);
        stage.setResizable(false);
        stage.show();
        isRunning = false;
    }

    /*
     * Creates a styled JavaFX {@code Button} with the specified position,
     * dimensions, text, and an action to be performed when the button is
     * clicked. This method encapsulates the common steps involved in creating
     * a button for the game's user interface, ensuring a consistent style
     * by applying the {@code MENU_BUTTON_STYLE} CSS class.
     *
     * <p>The button is instantiated with the provided {@code text} as its label.
     * Its layout within its parent container is set using the {@code x} and
     * {@code y} parameters, which define the coordinates of the button's
     * top-left corner in pixels. The preferred width and height of the button
     * are set according to the {@code width} and {@code height} parameters,
     * respectively, also in pixels. These dimensions dictate the visual size
     * of the button on the screen.
     *
     * <p>To apply a uniform visual style to all menu buttons, the
     * {@code MENU_BUTTON_STYLE} CSS class is added to the button's style class
     * list. This class is expected to be defined in the game's CSS stylesheet
     * ({@code CSS_PATH}) and will control properties such as font, colors,
     * padding, and other visual attributes of the button.
     *
     * <p>The functionality of the button is defined through an event handler
     * that is triggered when the button is clicked. This method takes a
     * {@code Runnable} object as the {@code action} parameter. When the button
     * is clicked, the {@code setOnAction()} method is used to set an event
     * handler that, in turn, calls the {@code run()} method of the provided
     * {@code Runnable} object. This allows for flexible and encapsulated
     * actions to be associated with each button created using this method.
     *
     * <p>Finally, the configured {@code Button} instance is returned, ready to
     * be added to a scene graph and displayed as part of the game's user
     * interface. This utility method promotes code reusability and helps in
     * maintaining a consistent look and feel for interactive elements within
     * the game's menus.
     *
     * @param x      the x-coordinate in pixels of the button's top-left corner.
     * @param y      the y-coordinate in pixels of the button's top-left corner.
     * @param width  the width of the button in pixels.
     * @param height the height of the button in pixels.
     * @param text   the text to be displayed on the button.
     * @param action the {@code Runnable} object whose {@code run()} method will
     * be executed when the button is clicked. This encapsulates
     * the specific functionality associated with the button.
     * @return a configured {@code Button} instance with the specified position,
     * dimensions, text, style, and action.
     */
    private Button createButton(double x,
                                double y,
                                double width,
                                double height,
                                String text,
                                Runnable action)
    {
        final Button button;
        button = new Button(text);

        button.setLayoutX(x);
        button.setLayoutY(y);
        button.setPrefWidth(width);
        button.setPrefHeight(height);
        button.getStyleClass().add(MENU_BUTTON_STYLE);
        button.setOnAction(event -> action.run());
        return button;
    }

    /*
     * Initiates a new game session. This method performs a sequence of crucial
     * steps to set up the game environment and start the first level. These
     * steps include resetting the player's state, creating and configuring the
     * main game scene, applying the current visual theme, initializing and
     * positioning essential UI elements such as restart, quit, and theme
     * change buttons, and finally, starting the game engine for the first level.
     *
     * <p>The process begins by resetting the {@code player} object to its initial
     * state. This ensures that any progress or state from previous game sessions
     * is cleared, providing a clean starting point for a new game. A message is
     * also printed to the console indicating the start of a new game and the
     * current state of the player's collected target (which is likely used for
     * debugging or tracking game progress).
     *
     * <p>Next, a new JavaFX {@code Pane} is created and assigned to the
     * {@code root} instance variable. This pane serves as the main container
     * for all visual elements of the game scene. Its preferred size is set to
     * the game's window dimensions ({@code WINDOW_WIDTH_PIXELS} and
     * {@code WINDOW_HEIGHT_PIXELS}), and the {@code GAME_PANE_STYLE} CSS class
     * is applied to it for initial styling. The current visual theme, selected
     * by the {@code currentThemeIndex}, is then applied to the root pane using
     * the {@code applyTheme()} method.
     *
     * <p>The game engine's display pane (obtained via {@code engine.getGamePane()})
     * and the user interface pane (obtained via {@code ui.getUIPane()}) are added
     * as children to the root pane. These panes contain the dynamic elements of
     * the game world and the static UI components, respectively.
     *
     * <p>Three functional buttons are then created and configured:
     * <ol>
     * <li>A "Restart Current Level" button, which is positioned at
     * {@code RESTART_BUTTON_X_PIXELS} on the x-axis and
     * {@code WINDOW_HEIGHT_PIXELS - GAME_BUTTON_Y_OFFSET_PIXELS} on the y-axis.
     * The {@code GAME_BUTTON_STYLE} CSS class is applied for styling, and an
     * event handler is set up to call the {@code resetGame()} method when the
     * button is clicked.</li>
     * <li>A "Return to Game Menu" button, positioned at
     * {@code QUIT_BUTTON_X_PIXELS} on the x-axis and the same y-coordinate as
     * the restart button. It is also styled with {@code GAME_BUTTON_STYLE} and
     * is set to call the {@code stopGame()} method when clicked, which returns
     * the player to the main menu.</li>
     * <li>A "Change Current theme" button, located at
     * {@code THEME_BUTTON_X_PIXELS} on the x-axis and the same y-coordinate.
     * It uses {@code GAME_BUTTON_STYLE} for styling and is configured to call
     * the {@code changeTheme()} method upon being clicked, allowing the player
     * to alter the game's visual appearance.</li>
     * </ol>
     * These buttons are subsequently added as children to the root pane, making
     * them visible and interactive within the game scene.
     *
     * <p>A new JavaFX {@code Scene} is created with the root pane as its content
     * and the game's window dimensions. The game's main CSS stylesheet
     * ({@code CSS_PATH}) is applied to this scene to style its elements.
     *
     * <p>Event handlers are set up for the game scene to handle user input:
     * <ol>
     * <li>{@code setOnMouseMoved}: This handler is attached to track mouse
     * movements within the game scene. Whenever the mouse is moved, the
     * {@code player.updateCursorPosition()} method is called, passing the
     * current x and y coordinates of the mouse. This is likely used to control
     * the player's in-game avatar or interaction point.</li>
     * <li>{@code setOnKeyPressed}: This handler listens for key presses. If the
     * pressed key's code is {@code KeyCode.Q}, the {@code stopGame()} method is
     * called, providing a keyboard shortcut for quitting the game and returning
     * to the menu.</li>
     * </ol>
     *
     * <p>The title of the game window ({@code stage}) is set to the base game
     * title ({@code TITLE}), and the newly created game scene is set as the
     * current scene for the stage. An event handler is also set for the stage's
     * close request. If the player attempts to close the window (e.g., by
     * clicking the window's close button), the {@code stopGame()} method is
     * called to ensure a clean shutdown of the game. Finally, the game stage
     * is made visible to the user.
     *
     * <p>Before starting the game engine, a crucial check is performed to ensure
     * that the {@code levelManager} has been properly initialized (i.e., it is
     * not null). If the level manager is valid, its level count is reset using
     * {@code levelManager.resetLevel()}, the player's current score is recorded
     * as the starting score for the first level, and the {@code engine.startLevel()}
     * method is called to begin the first level of the game, passing the player,
     * UI, and level manager instances. The {@code isRunning} flag is then set to
     * true.
     *
     * <p>However, if the {@code levelManager} is found to be null, an error
     * message is printed to the system error stream, and a loss alert is shown
     * to the player using the {@code showLossAlert()} method, informing them
     * of an initialization error and the need to restart the application. This
     * prevents the game from proceeding without a properly configured level
     * management system.
     */
    private void startGame()
    {
        player.reset();
        System.out.println("\n----Starting new game----\n");

        root = new Pane();
        root.setPrefSize(WINDOW_WIDTH_PIXELS,
                         WINDOW_HEIGHT_PIXELS);
        root.getStyleClass().add(GAME_PANE_STYLE);
        applyTheme();

        root.getChildren().addAll(engine.getGamePane(),
                                  ui.getUIPane());

        final Button restartButton;
        restartButton = new Button(BUTTON_RESTART_TEXT);

        restartButton.setLayoutX(RESTART_BUTTON_X_PIXELS);
        restartButton.setLayoutY(WINDOW_HEIGHT_PIXELS - GAME_BUTTON_Y_OFFSET_PIXELS);
        restartButton.getStyleClass().add(GAME_BUTTON_STYLE);
        restartButton.setOnAction(event -> resetGame());

        final Button quitButton;
        quitButton = new Button(BUTTON_QUIT_TEXT);

        quitButton.setLayoutX(QUIT_BUTTON_X_PIXELS);
        quitButton.setLayoutY(WINDOW_HEIGHT_PIXELS - GAME_BUTTON_Y_OFFSET_PIXELS);
        quitButton.getStyleClass().add(GAME_BUTTON_STYLE);
        quitButton.setOnAction(event -> stopGame());

        final Button themeButton;
        themeButton = new Button(BUTTON_THEME_TEXT);

        themeButton.setLayoutX(THEME_BUTTON_X_PIXELS);
        themeButton.setLayoutY(WINDOW_HEIGHT_PIXELS - GAME_BUTTON_Y_OFFSET_PIXELS);
        themeButton.getStyleClass().add(GAME_BUTTON_STYLE);
        themeButton.setOnAction(event -> changeTheme());

        root.getChildren().addAll(restartButton,
                                  quitButton,
                                  themeButton);

        Scene gameScene;
        gameScene = new Scene(root,
                              WINDOW_WIDTH_PIXELS,
                              WINDOW_HEIGHT_PIXELS);
        gameScene.getStylesheets().add(CSS_PATH);

        gameScene.setOnMouseMoved(event -> player.updateCursorPosition(event.getX(),
                                                                       event.getY()));
        gameScene.setOnKeyPressed(event ->
                                  {
                                      if(event.getCode() == KeyCode.Q)
                                      {
                                          stopGame();
                                      }
                                  });

        stage.setTitle(TITLE);
        stage.setScene(gameScene);
        stage.setOnCloseRequest(event -> stopGame());
        stage.show();

        if(levelManager != null)
        {
            levelManager.resetLevel();
            scoreAtLevelStart = player.getScore();
            engine.startLevel(player,
                              ui,
                              levelManager);
            isRunning = true;
        }
        else
        {
            System.err.println("Cannot start game: " + NULL_LEVEL_MANAGER);
            showLossAlert(ERROR_HEADER,
                          ERROR_MESSAGE);
        }
    }

    /*
     * Applies the currently selected visual theme to the game's root pane.
     * The theme is chosen from the {@code THEMES} array based on the
     * {@code currentThemeIndex}. This method sets the background image of the
     * root pane using inline CSS styles, ensuring that the background image
     * scales to cover the entire pane and is positioned at the center.
     * This provides a way to customize the look and feel of the game.
     */
    private void applyTheme()
    {
        root.setStyle("-fx-background-image: url('" + THEMES[currentThemeIndex] + "');" +
                      " -fx-background-size: cover; -fx-background-position: center;");
    }


    /*
     * Switches the game's visual theme to the next available theme in the
     * {@code THEMES} array. The {@code currentThemeIndex} is incremented, and
     * the modulo operator (%) is used to wrap around to the beginning of the
     * array if the current index reaches the end. After updating the index,
     * the {@code applyTheme()} method is called to apply the new theme to the
     * game's root pane. This allows the player to change the game's appearance
     * during gameplay.
     */
    private void changeTheme()
    {
        currentThemeIndex = (currentThemeIndex + INCREMENTING_BY_ONE) % THEMES.length;
        applyTheme();
    }

    /*
     * Resets the current game level to its initial state. This method is invoked
     * when the player chooses to restart the current level, typically through a
     * user interface action such as clicking a "Restart" button. The primary
     * goal of this method is to restore the game to the exact conditions it was
     * in when the current level began, allowing the player to attempt the level
     * again from the start.
     *
     * <p>The method begins by checking if the {@code levelManager} instance is
     * not null. This check is crucial to ensure that the level management system
     * is properly initialized and available before attempting to reset the game
     * state. If the {@code levelManager} is null, it indicates a critical error
     * in the game's initialization, and an error message is printed to the
     * system error stream to alert developers to this issue.
     *
     * <p>If the {@code levelManager} is valid, the following actions are performed
     * to reset the game state for the current level:
     * <ol>
     * <li>The player's current score is reset to the score they had at the
     * beginning of the current level. This starting score was previously
     * recorded in the {@code scoreAtLevelStart} instance variable when the
     * level was first loaded or when proceeding from a previous level. This
     * ensures that any points gained or lost during the failed attempt of the
     * level are discarded, and the player starts the level again with the
     * appropriate score.</li>
     * <li>The player's accumulated bonus points are reset to zero using the
     * {@code player.setBonusPoints(0)} method. This ensures that any bonus
     * points earned during the previous attempt of the level do not carry over
     * to the new attempt.</li>
     * <li>The player's level-specific state is reset using the
     * {@code player.resetForNewLevel()} method. This method, likely defined
     * in the {@code Player} class, is responsible for clearing any progress
     * the player made within the level, such as partially formed words,
     * collected items, or any other level-specific data. This ensures a
     * completely fresh start to the level.</li>
     * <li>Finally, the game engine ({@code engine}) is instructed to reset its
     * state for the current level by calling the {@code engine.resetGame()}
     * method. This method in the {@code LetterEngine} class is responsible
     * for resetting any dynamic elements within the game world, such as the
     * positions and states of letters, obstacles, timers, and any other
     * game entities that change during gameplay. It is passed the current
     * {@code player}, {@code ui} (GameUI), and {@code levelManager} instances
     * so that the engine can reset the game state in coordination with these
     * components.</li>
     * </ol>
     *
     * <p>By performing these steps, the {@code resetGame()} method ensures that
     * the player can retry the current level without any lingering effects from
     * their previous attempt, providing a fair and consistent experience.
     */
    private void resetGame()
    {

        if(levelManager != null)
        {
            player.setScore(scoreAtLevelStart);
            player.setBonusPoints(0);
            player.resetForNewLevel();
            engine.resetGame(player,
                             ui,
                             levelManager);
        }
        else
        {
            System.err.println("Cannot reset game: " + NULL_LEVEL_MANAGER);
        }

    }


    /*
     * Stops the current game session and returns the player to the main menu.
     * This method performs several key actions to ensure a clean termination
     * of the gameplay and a smooth transition back to the menu interface.
     *
     * <p>First, the {@code running} flag of this {@code LetterRush} instance
     * is set to {@code false}. This flag is likely used internally to control
     * any ongoing game loops or update processes, and setting it to false signals
     * that the game is no longer active.
     *
     * <p>Next, a message is printed to the console indicating that the game is
     * stopping. This message also includes the current state of the player's
     * collected target (retrieved using {@code player.getCollectedTarget()}).
     * This is primarily for debugging purposes, allowing developers to track
     * the player's progress or the game's state at the point of termination.
     *
     * <p>The method then updates the information displayed on the main menu to
     * reflect the player's current game statistics. This involves:
     * <ol>
     * <li>Updating the score displayed on the menu by setting the text of the
     * {@code scoreText} object to the {@code SCORE_TEXT_PREFIX} concatenated
     * with the player's current score (obtained via {@code player.getScore()}).
     * This ensures that the menu shows the player's final score from the
     * just-ended game session.</li>
     * <li>Updating the bonus score displayed on the menu by setting the text of
     * the {@code bonusScoreText} object to the {@code BONUS_SCORE_TEXT_PREFIX}
     * followed by the player's current bonus points (retrieved using
     * {@code player.getBonusPoints()}). This keeps the menu's bonus score
     * display current.</li>
     * <li>Updating the high score displayed on the menu by setting the text of
     * the {@code highScoreText} object to the {@code HIGH_SCORE_TEXT_PREFIX}
     * combined with the player's highest recorded score (obtained via
     * {@code player.getHighScore()}). This ensures that the menu always shows
     * the best performance achieved by the player across all game sessions.</li>
     * </ol>
     *
     * <p>Finally, the scene displayed on the game's stage ({@code stage}) is
     * switched back to the main menu scene ({@code menuScene}). This effectively
     * transitions the user interface from the active gameplay screen back to the
     * game's starting or menu screen, allowing the player to start a new game
     * or perform other menu-related actions.
     */
    private void stopGame()
    {
        isRunning = false;
        System.out.println("\nStopping game...");
        scoreText.setText(SCORE_TEXT_PREFIX + player.getScore());
        bonusScoreText.setText(BONUS_SCORE_TEXT_PREFIX + player.getBonusPoints());
        highScoreText.setText(HIGH_SCORE_TEXT_PREFIX + player.getHighScore());
        stage.setScene(menuScene);
    }

    /*
     * Executes the game level startup process in a separate thread.
     * This method is called when a new level needs to be started, typically
     * after the player has either started a new game or successfully completed
     * a previous level and chosen to proceed. To ensure smooth transitions
     * and avoid blocking the JavaFX application thread, this method introduces
     * a small delay, defined by the constant {@code THREAD_SLEEP_MS}, before
     * initiating the level. After the delay, it uses {@code Platform.runLater()}
     * to schedule the actual starting of the game level on the JavaFX
     * application thread. This ensures that all UI updates and game logic
     * that affect the user interface are executed on the appropriate thread.
     * The level is started by calling the {@code startLevel()} method of the
     * {@code engine}, passing the current {@code player}, {@code ui}, and
     * {@code levelManager} instances. Any interruption of the thread's sleep
     * is caught and its stack trace is printed for debugging purposes.
     *
     * <p>This method is part of the {@code Runnable} interface implementation,
     * allowing instances of {@code LetterRush} to be executed by a {@code Thread}.
     * It is specifically used to handle the asynchronous initialization of a
     * new game level.
     */
    public void run()
    {
        try
        {
            Thread.sleep(THREAD_SLEEP_MS);
            Platform.runLater(() -> engine.startLevel(player,
                                                      ui,
                                                      levelManager));
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}