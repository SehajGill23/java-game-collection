package ca.bcit.Comp2522.termProject.MyGame;

import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.ImageCursor;
import javafx.stage.Stage;


public class LetterRush {
    private static final int WINDOW_WIDTH = 1000;
    private static final int WINDOW_HEIGHT = 600;
    private static final String CURSOR_IMAGE_PATH = "/cursor.png";
    private static final String CSS_PATH = "/letterStyles.css";
    private static final String TITLE = "LetterRush";
    private static final double CURSOR_SIZE = 32.0;
    private static final String[] THEMES = {
            "/background2.png",
            "/background1.png",
            "/background3.png",};

private static LetterRush instance;
private final LetterEngine engine;
private final Player player;
private final GameUI ui;
private final LevelManager levelManager;
private Stage stage;
private boolean isRunning = false;
private Scene menuScene;
private Scene gameScene;
private Text scoreText;
private Text bonusScoreText;
private Text highScoreText;
private int currentThemeIndex = 0;
private Pane root;


    public LetterRush() {
        this.engine = new LetterEngine(WINDOW_WIDTH, WINDOW_HEIGHT - 100); // Adjust game area
        this.player = new Player();
        this.ui = new GameUI();
        this.levelManager = new LevelManager();
        if (levelManager == null) {
            System.err.println("LevelManager failed to initialize in LetterRush constructor!");
        } else {
            System.out.println("LetterRush initialized successfully");
        }
        this.engine.setGame(this);
    }

    public static void launchGame() {
        if (instance == null) {
            instance = new LetterRush();
        }
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(instance::showMenu);
        } else {
            instance.showMenu();
        }
    }

    private void showMenu() {
        if (stage == null) {
            stage = new Stage();
        }

        final Pane menuPane = new Pane();
        menuPane.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT);

        // Score Display
        scoreText = new Text(300, 150, "Score: " + player.getScore());
        scoreText.getStyleClass().add("score-text");

        bonusScoreText = new Text(300, 180, "Bonus Score: " + player.getBonusPoints());
        bonusScoreText.getStyleClass().add("score-text");

        highScoreText = new Text(300, 210, "High Score: " + player.getHighScore());
        highScoreText.getStyleClass().add("score-text");

        // Instructions
        final Text instructionsText = new Text(150, 450,
                                               "Instructions:\n" +
                                               "- Click letters to form the target word (white).\n" +
                                               "- There’s a hidden bonus word—find it for extra points!\n" +
                                               "- Avoid obstacles (missile, bomb, spike) that move randomly.\n" +
                                               "- Complete the target word before time runs out.\n" +
                                               "- Use buttons below the game to restart, quit, or change theme.");
        instructionsText.getStyleClass().add("instructions-text");

        // Start LetterRush Button
        final Rectangle startButton = new Rectangle(300, 250, 200, 50);
        startButton.setArcWidth(20);
        startButton.setArcHeight(20);
        startButton.getStyleClass().add("menu-button");
        final Text startText = new Text(350, 280, "Start LetterRush");
        startText.getStyleClass().add("menu-text");

        // Return to Main Menu Button
        final Rectangle returnButton = new Rectangle(300, 350, 200, 50);
        returnButton.setArcHeight(20);
        returnButton.getStyleClass().add("menu-button");
        final Text returnText = new Text(320, 380, "Return to Main Menu");
        returnText.getStyleClass().add("menu-text");

        menuPane.getChildren().addAll(scoreText, bonusScoreText, highScoreText, instructionsText, startButton, startText, returnButton, returnText);

        startButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> startGame());

        startButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> startGame());
        returnButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            stage.close();
        });

        menuScene = new Scene(menuPane, WINDOW_WIDTH, WINDOW_HEIGHT);
        menuScene.getStylesheets().add(CSS_PATH);

        stage.setTitle(TITLE + " - Menu");
        stage.setScene(menuScene);
        stage.setResizable(false);
        stage.show();
        isRunning = false;
    }

    private void startGame() {

        player.reset();
        System.out.println("Starting new game. Player collectedTarget: " + player.getCollectedTarget());

        root = new Pane();
        root.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        root.getStyleClass().add("game-pane");
        applyTheme(); // Apply initial theme

        // Add game pane and UI
        root.getChildren().addAll(engine.getGamePane(), ui.getUIPane());

        // Add buttons below the game canvas
        final Button restartButton = new Button("Restart");
        restartButton.setLayoutX(200);
        restartButton.setLayoutY(WINDOW_HEIGHT - 80);
        restartButton.getStyleClass().add("game-button");
        restartButton.setOnAction(event -> resetGame());

        final Button quitButton = new Button("Quit");
        quitButton.setLayoutX(350);
        quitButton.setLayoutY(WINDOW_HEIGHT - 80);
        quitButton.getStyleClass().add("game-button");
        quitButton.setOnAction(event -> stopGame());

        final Button themeButton = new Button("Change Theme");
        themeButton.setLayoutX(500);
        themeButton.setLayoutY(WINDOW_HEIGHT - 80);
        themeButton.getStyleClass().add("game-button");
        themeButton.setOnAction(event -> changeTheme());

        root.getChildren().addAll(restartButton, quitButton, themeButton);

        Image cursorImage = null;
        try {
            cursorImage = new Image(CURSOR_IMAGE_PATH, CURSOR_SIZE, CURSOR_SIZE, true, true);
            if (cursorImage.isError()) {
                System.err.println("Cursor image failed to load: " + cursorImage.getException());
            } else {
                System.out.println("Cursor image loaded successfully: " + CURSOR_SIZE + "x" + CURSOR_SIZE);
            }
        } catch (Exception e) {
            System.err.println("Error loading cursor image: " + e.getMessage());
        }

        final Cursor customCursor = (cursorImage != null && !cursorImage.isError())
                                    ? new ImageCursor(cursorImage, CURSOR_SIZE / 2, CURSOR_SIZE / 2)
                                    : ImageCursor.CROSSHAIR;

        gameScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        gameScene.setCursor(customCursor);
        gameScene.getStylesheets().add(CSS_PATH);

        gameScene.setOnMouseMoved(event -> player.updateCursorPosition(event.getX(), event.getY()));
        gameScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.Q) {
                stopGame();
            }
        });

        stage.setTitle(TITLE);
        stage.setScene(gameScene);
        stage.setOnCloseRequest(event -> stopGame());
        stage.show();

        if (levelManager != null) {
            engine.startLevel(player, ui, levelManager);
            isRunning = true;
        } else {
            System.err.println("Cannot start game: levelManager is null");
            showLossAlert("Initialization Error", "LevelManager is null. Check resources and restart.");
        }
    }

    private void applyTheme() {
        root.setStyle("-fx-background-image: url('" + THEMES[currentThemeIndex] + "'); " +
                      "-fx-background-size: cover; " +
                      "-fx-background-position: center;");
    }

    private void changeTheme() {
        currentThemeIndex = (currentThemeIndex + 1) % THEMES.length;
        applyTheme();
    }

    private void resetGame() {
        if (levelManager != null) {
            engine.resetGame(player, ui, levelManager);
        } else {
            System.err.println("Cannot reset game: levelManager is null");
        }
    }

    private void stopGame() {
        isRunning = false;
        System.out.println("Stopping game. Player collectedTarget: " + player.getCollectedTarget());
        scoreText.setText("Score: " + player.getScore());
        bonusScoreText.setText("Bonus Score: " + player.getBonusPoints());
        highScoreText.setText("High Score: " + player.getHighScore());
        stage.setScene(menuScene);
    }

    public void showLossAlert(String header, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText(header);
            alert.setContentText(content);
            alert.getDialogPane().getStylesheets().add(CSS_PATH);
            alert.getDialogPane().getStyleClass().add("alert");
            alert.showAndWait();
            stopGame();
            player.reset();
        });
    }


    public void showLossAlertObstacle() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText("Game Over!");
            alert.setContentText("You hit an obstacle! Try again?");
            alert.getDialogPane().getStylesheets().add(CSS_PATH);
            alert.getDialogPane().getStyleClass().add("alert");
            alert.showAndWait();
            // Reset the player's state before stopping the game
            player.reset();
            stopGame();
            player.reset();
        });
    }

    public void showLossAlertTime() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText("Game Over!");
            alert.setContentText("Time’s up! Try again?");
            alert.getDialogPane().getStylesheets().add(CSS_PATH);
            alert.getDialogPane().getStyleClass().add("alert");
            alert.showAndWait();
            // Reset the player's state before stopping the game
            player.reset();
            stopGame();
            player.reset();
        });
    }

    public void showWinAlert(int level) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Congratulations!");
            alert.setHeaderText("Congrats on finishing Level " + level + "!");
            alert.setContentText("Would you like to proceed to Level " + (level + 1) + " or return to the main menu?");
            alert.getDialogPane().getStylesheets().add(CSS_PATH);
            alert.getDialogPane().getStyleClass().add("alert");

            alert.getButtonTypes().setAll(
                    new javafx.scene.control.ButtonType("Proceed"),
                    new javafx.scene.control.ButtonType("Return to Main Menu")
                                         );

            alert.showAndWait().ifPresent(response -> {
                if (response.getText().equals("Proceed")) {

                    player.reset();

                    new Thread(() -> {
                        try {
                            Thread.sleep(100); // 100ms delay
                            Platform.runLater(() -> engine.startLevel(player, ui, levelManager));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }).start();


                } else {
                    stopGame();
//                    stage.close();
                }
            });
        });
    }

    public void showBonusAlert(int level) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Bonus Found!");
            alert.setHeaderText("Bonus Word Found! Extra Points!");
            alert.setContentText("You earned extra points! Proceed to Level " + (level + 1) + " or return to the main menu?");
            alert.getDialogPane().getStylesheets().add(CSS_PATH);
            alert.getDialogPane().getStyleClass().add("alert");

            alert.getButtonTypes().setAll(
                    new javafx.scene.control.ButtonType("Proceed"),
                    new javafx.scene.control.ButtonType("Return to Main Menu")
                                         );

            alert.showAndWait().ifPresent(response -> {
                if (response.getText().equals("Proceed")) {
                    player.reset();
                    engine.startLevel(player, ui, levelManager);
                } else {
                    stopGame();
                }
            });
        });
    }
}
