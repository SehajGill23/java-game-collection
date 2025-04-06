package ca.bcit.Comp2522.termProject.LetterRushGame;

import ca.bcit.Comp2522.termProject.WordGame.Main;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * The `LetterRush` class represents the main game controller for the LetterRush game.
 * It manages the game lifecycle, including menu display, game start/stop, level management,
 * and user interface updates. This class integrates with `LetterEngine`, `Player`, `GameUI`,
 * and `LevelManager` to provide a complete gaming experience.
 *
 * @author Sehaj Gill
 * @version 1.0
 */
public class LetterRush
{
    private static final int      WINDOW_WIDTH            = 1000;
    private static final int      WINDOW_HEIGHT           = 600;
    private static final int      SCORE_X                 = 80;
    private static final int      SCORE_Y                 = 210;
    private static final int      BONUS_SCORE_X           = 400;
    private static final int      HIGH_SCORE_X            = 740;
    private static final int      INSTRUCTIONS_X          = 50;
    private static final int      INSTRUCTIONS_Y          = 450;
    private static final int      BUTTON_START_X          = 200;
    private static final int      BUTTON_RETURN_X         = 580;
    private static final int      BUTTON_Y                = 320;
    private static final int      BUTTON_WIDTH            = 200;
    private static final int      BUTTON_HEIGHT           = 50;
    private static final int      GAME_BUTTON_Y_OFFSET    = 80;
    private static final int      RESTART_BUTTON_X        = 90;
    private static final int      QUIT_BUTTON_X           = 420;
    private static final int      THEME_BUTTON_X          = 737;
    private static final int      THREAD_SLEEP_MS         = 100;
    private static final int      INCREMENTING_BY_ONE     = 1;
    private static final int      WINDOW_ADJUSTMENT       = 100;
    private static final int      INITIAL_SCORE           = 0;
    private static final String   CSS_PATH                = "/letterStyles.css";
    private static final String   TITLE                   = "LetterRush";
    private static final String   TITLE_MENU              = TITLE + " - Menu";
    private static final String   SCORE_TEXT_PREFIX       = "Score: ";
    private static final String   BONUS_SCORE_TEXT_PREFIX = "Bonus Score: ";
    private static final String   HIGH_SCORE_TEXT_PREFIX  = "High Score: ";
    private static final String   SCORE_TEXT_STYLE        = "score-text";
    private static final String   INSTRUCTIONS_TEXT_STYLE = "instructions-text";
    private static final String   MENU_BUTTON_STYLE       = "menu-button";
    private static final String   GAME_PANE_STYLE         = "game-pane";
    private static final String   GAME_BUTTON_STYLE       = "game-button";
    private static final String   ALERT_STYLE             = "alert";
    private static final String   BUTTON_START_TEXT       = "Start Letter Rush";
    private static final String   BUTTON_RESTART_TEXT     = "Restart Current Level";
    private static final String   BUTTON_QUIT_TEXT        = "Return to Game Menu";
    private static final String   BUTTON_THEME_TEXT       = "Change Current theme";
    private static final String   GAME_OVER_TITLE         = "Game Over";
    private static final String   GAME_OVER_HEADER        = "Game Over!";
    private static final String   OBSTACLE_MESSAGE        = "You hit an obstacle! Try again?";
    private static final String   TIME_UP_MESSAGE         = "Time’s up! Try again?";
    private static final String   ERROR_HEADER            = "Initialization Error";
    private static final String   ERROR_MESSAGE           = "LevelManager is null. Check resources and restart.";
    private static final String   CONGRATS_TITLE          = "Congratulations!";
    private static final String   BONUS_TITLE             = "Bonus Found!";
    private static final String   BONUS_HEADER            = "Bonus Word Found! Extra Points!";
    private static final String   PROCEED_BUTTON          = "Proceed";
    private static final String   RETURN_BUTTON           = "Return to Main Menu";
    private static final String   GOAT_MESSAGE            = "You are the GOAT 🐐, You've Won the Game! 🥳";
    private static final String   INSTRUCTIONS_TEXT       = """
                                                            Instructions:
                                                            - Click letters to form the target word (white).
                                                            - Complete the target word before time runs out.
                                                            - There’s a hidden bonus word—find it for extra points!
                                                            - Avoid obstacles (missile, bomb, cactus) that move randomly.
                                                            - Use buttons below the game to restart, quit, or change theme.
                                                            - Failure resets the level to 1 and score to 0,""" +
                                                            " regardless of current level";
    private static final String[] THEMES                  = {"/background2.png",
                                                             "/background1.png",
                                                             "/background3.png",
                                                             "/background4.png"};
    private              boolean  isRunning               = false;


    private static LetterRush   instance;
    private final  LetterEngine engine;
    private final  Player       player;
    private final  GameUI       ui;
    private final  LevelManager levelManager;
    private        Pane         root;
    private        Stage        stage;
    private        Scene        menuScene;
    private        Scene        gameScene;
    private        Text         scoreText;
    private        Text         bonusScoreText;
    private        Text         highScoreText;
    private        int          currentThemeIndex = 0;
    public         int          scoreAtLevelStart;



    /**
     * Constructs a new LetterRush instance, initializing the game engine, player, UI,
     * and level manager. Sets up the game environment with default values for the score,
     * and theme index. This constructor prepares the game for launch.
     */
    public LetterRush()
    {
        this.engine       = new LetterEngine(WINDOW_WIDTH,
                                             WINDOW_HEIGHT - WINDOW_ADJUSTMENT);
        this.player       = new Player();
        this.ui           = new GameUI();
        this.levelManager = new LevelManager();
        this.engine.setGame(this);
        this.scoreAtLevelStart = INITIAL_SCORE;
    }

    /**
     * Launches the LetterRush game by creating an instance (if none exists) and displaying
     * the menu. Ensures the operation runs on the JavaFX Application Thread using Platform.runLater
     * if necessary. This method serves as the entry point for starting the game.
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
     * Displays a loss alert with a custom header and content message.
     *
     * @param header the header text of the alert
     * @param content the content text of the alert
     */
    void showLossAlert(String header,
                       String content)
    {
        Platform.runLater(() -> {
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
     * Displays a loss alert when the player hits an obstacle.
     */
    void showLossAlertObstacle()
    {
        showLossAlert(GAME_OVER_HEADER, OBSTACLE_MESSAGE);
    }

    /*
     * Displays a loss alert when time runs out.
     */
    void showLossAlertTime()
    {
        showLossAlert(GAME_OVER_HEADER, TIME_UP_MESSAGE);
    }

    /*
     * Displays a win alert for completing a level, offering options to proceed or return.
     *
     * @param level the completed level number
     */
    //Interface Runnable, Concurrency
    void showWinAlert(int level)
    {
        Platform.runLater(() ->
                          {
                              Alert alert;
                              alert = new Alert(Alert.AlertType.CONFIRMATION);
                              alert.setTitle(CONGRATS_TITLE);
                              alert.setHeaderText("Congrats on finishing Level " + level + "!");
                              alert.setContentText("Would you like to proceed to Level " + (level + 1) + " " +
                                                   "or return to the main menu?");
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
     * Displays a bonus alert when a bonus word is found, offering options to proceed or return.
     *
     * @param level the current level number
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

    /**
     * Displays a game completion alert when all levels are won.
     */
    void showGameWonAlert()
    {
        Platform.runLater(() ->
                          {
                              Alert alert;
                              alert = new Alert(Alert.AlertType.INFORMATION);
                              alert.setTitle(CONGRATS_TITLE);
                              alert.setHeaderText(GOAT_MESSAGE);
                              alert.setContentText("You have completed all levels of LetterRush! Final Score: " +
                                                   player.getScore());
                              alert.getDialogPane().getStylesheets().add(CSS_PATH);
                              alert.getDialogPane().getStyleClass().add(ALERT_STYLE);
                              alert.showAndWait();
                              stopGame();
                          });
    }

    /*
     * Displays the main menu of the game, including score displays, instructions,
     * and start/return buttons. Initializes the stage and scene if not already set.
     */
    private void showMenu()
    {
        if(stage == null)
        {
            stage = new Stage();
        }

        final Pane menuPane;
        menuPane = new Pane();
        menuPane.setPrefSize(WINDOW_WIDTH,
                             WINDOW_HEIGHT);
        // Score Display
        scoreText = new Text(SCORE_X,
                             SCORE_Y,
                             SCORE_TEXT_PREFIX + player.getScore());
        scoreText.getStyleClass().add(SCORE_TEXT_STYLE);

        bonusScoreText = new Text(BONUS_SCORE_X,
                                  SCORE_Y,
                                  BONUS_SCORE_TEXT_PREFIX + player.getBonusPoints());
        bonusScoreText.getStyleClass().add(SCORE_TEXT_STYLE);

        highScoreText = new Text(HIGH_SCORE_X,
                                 SCORE_Y,
                                 HIGH_SCORE_TEXT_PREFIX + player.getHighScore());
        highScoreText.getStyleClass().add(SCORE_TEXT_STYLE);


        final Text instructionsText;
        instructionsText= new Text(INSTRUCTIONS_X,
                                   INSTRUCTIONS_Y,
                                   INSTRUCTIONS_TEXT);
        instructionsText.getStyleClass().add(INSTRUCTIONS_TEXT_STYLE);


        final Button startButton = createButton(BUTTON_START_X,
                                                BUTTON_Y,
                                                BUTTON_WIDTH,
                                                BUTTON_HEIGHT,
                                                BUTTON_START_TEXT,
                                                this::startGame);
        final Button returnButton = createButton(BUTTON_RETURN_X,
                                                 BUTTON_Y,
                                                 BUTTON_WIDTH,
                                                 BUTTON_HEIGHT,
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
                              WINDOW_WIDTH,
                              WINDOW_HEIGHT);
        menuScene.getStylesheets().add(CSS_PATH);

        stage.setTitle(TITLE_MENU);
        stage.setScene(menuScene);
        stage.setResizable(false);
        stage.show();
        isRunning = false;
    }

    /*
     * Creates a styled button with the specified position, size, text, and action.
     *
     * @param x      the x-coordinate of the button
     * @param y      the y-coordinate of the button
     * @param width  the width of the button
     * @param height the height of the button
     * @param text   the text to display on the button
     * @param action the action to perform when the button is clicked
     * @return a configured `Button` instance
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
     * Starts a new game by resetting the player, setting up the game scene,
     * and initiating the first level. Handles cursor setup and error conditions.
     */
    private void startGame()
    {
        player.reset();
        System.out.println("Starting new game. Player collectedTarget: " + player.getCollectedTarget());

        root = new Pane();
        root.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        root.getStyleClass().add(GAME_PANE_STYLE);
        applyTheme();

        root.getChildren().addAll(engine.getGamePane(), ui.getUIPane());

        final Button restartButton;
        restartButton= new Button(BUTTON_RESTART_TEXT);

        restartButton.setLayoutX(RESTART_BUTTON_X);
        restartButton.setLayoutY(WINDOW_HEIGHT - GAME_BUTTON_Y_OFFSET);
        restartButton.getStyleClass().add(GAME_BUTTON_STYLE);
        restartButton.setOnAction(event -> resetGame());

        final Button quitButton;
        quitButton= new Button(BUTTON_QUIT_TEXT);

        quitButton.setLayoutX(QUIT_BUTTON_X);
        quitButton.setLayoutY(WINDOW_HEIGHT - GAME_BUTTON_Y_OFFSET);
        quitButton.getStyleClass().add(GAME_BUTTON_STYLE);
        quitButton.setOnAction(event -> stopGame());

        final Button themeButton;
        themeButton = new Button(BUTTON_THEME_TEXT);

        themeButton.setLayoutX(THEME_BUTTON_X);
        themeButton.setLayoutY(WINDOW_HEIGHT - GAME_BUTTON_Y_OFFSET);
        themeButton.getStyleClass().add(GAME_BUTTON_STYLE);
        themeButton.setOnAction(event -> changeTheme());

        root.getChildren().addAll(restartButton,
                                  quitButton,
                                  themeButton);


        gameScene = new Scene(root,
                              WINDOW_WIDTH,
                              WINDOW_HEIGHT);
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
            System.err.println("Cannot start game: levelManager is null");
            showLossAlert(ERROR_HEADER,
                          ERROR_MESSAGE);
        }
    }

    /*
     * Applies the current theme to the game root pane by setting the background image.
     */
    private void applyTheme()
    {
        root.setStyle("-fx-background-image: url('" + THEMES[currentThemeIndex] + "');" +
                      " -fx-background-size: cover; -fx-background-position: center;");
    }

    /*
     * Changes the current theme to the next one in the THEMES array.
     */
    private void changeTheme()
    {
        currentThemeIndex = (currentThemeIndex + INCREMENTING_BY_ONE ) % THEMES.length;
        applyTheme();
    }

    /*
     * Resets the current game level using the engine.
     */
    private void resetGame()
    {

        if (levelManager != null)
        {
            player.setScore(scoreAtLevelStart);
            player.setBonusPoints(0);
            player.resetForNewLevel();
            engine.resetGame(player, ui, levelManager);
        }
        else
        {
            System.err.println("Cannot reset game: levelManager is null");
        }

    }
    /*
     * Stops the game, updates the menu scene, and resets the player state.
     */
    private void stopGame()
    {
        isRunning = false;
        System.out.println("Stopping game. Player collectedTarget: " + player.getCollectedTarget());
        scoreText.setText(SCORE_TEXT_PREFIX + player.getScore());
        bonusScoreText.setText(BONUS_SCORE_TEXT_PREFIX + player.getBonusPoints());
        highScoreText.setText(HIGH_SCORE_TEXT_PREFIX + player.getHighScore());
        stage.setScene(menuScene);
    }

    /*
     * Executes the game level startup process in a separate thread.
     * Delays the start of the level by a brief period (defined by THREAD_SLEEP_MS),
     * then schedules the level to start on the JavaFX Application Thread using Platform.runLater.
     * This method is intended for internal use within the class to handle asynchronous level initialization.
     */
    private void run()
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