package ca.bcit.Comp2522.termProject.MyGame;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.ImageCursor;
import javafx.stage.Stage;


//public class LetterRush
//{
//    private static final int WINDOW_WIDTH = 1000;
//    private static final int WINDOW_HEIGHT = 600;
//    private static final String CURSOR_IMAGE_PATH = "/cursor.png";
//    private static final String CSS_PATH = "/letterStyles.css";
//    private static final String TITLE = "LetterRush";
//    private static final double CURSOR_SIZE = 32.0;
//    private static final String[] THEMES = {
//            "/background2.png",
//            "/background1.png",
//            "/background3.png",
//            };
//
//    private static LetterRush instance;
//    private final LetterEngine engine;
//    private final Player player;
//    private final GameUI ui;
//    private final LevelManager levelManager;
//    private Stage stage;
//    private boolean isRunning = false;
//    private Scene menuScene;
//    private Scene gameScene;
//    private Text scoreText;
//    private Text highScoreText;
//    private int highScore = 0;
//    private int currentThemeIndex = 0;
//    private Pane root;
//    private int bonusPoints = 0;
//    private boolean isShowingAlert = false;
//
//    public LetterRush() {
//        this.engine = LetterEngine.getInstance(WINDOW_WIDTH, WINDOW_HEIGHT - 100);
//        this.player = new Player();
//        this.ui = new GameUI();
//        this.levelManager = new LevelManager();
//        if (levelManager == null) {
//            System.err.println("LevelManager failed to initialize in LetterRush constructor!");
//        } else {
//            System.out.println("LetterRush initialized successfully");
//        }
//        this.engine.setGame(this);
//        System.out.println("LetterRush created with LetterEngine instance: " + engine);
//    }
//
//    public static void launchGame() {
//        if (instance == null) {
//            instance = new LetterRush();
//        }
//        if (!Platform.isFxApplicationThread()) {
//            Platform.runLater(instance::showMenu);
//        } else {
//            instance.showMenu();
//        }
//    }
//
//    private void showMenu() {
//        if (stage == null) {
//            stage = new Stage();
//        }
//
//        final Pane menuPane = new Pane();
//        menuPane.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT);
//
//        scoreText = new Text(400, 150, "Score: " + player.getScore() + " | Bonus: " + bonusPoints);
//        scoreText.getStyleClass().add("score-text");
//
//        highScoreText = new Text(400, 180, "High Score: " + highScore);
//        highScoreText.getStyleClass().add("score-text");
//
//        final Text instructionsText = new Text(300, 450,
//                                               "Instructions:\n" +
//                                               "- Click letters to form the target word (white).\n" +
//                                               "- There’s a hidden bonus word—find it for extra points!\n" +
//                                               "- Avoid obstacles (missile, bomb, spike) that move randomly.\n" +
//                                               "- Complete the target word before time runs out.\n" +
//                                               "- Use buttons below the game to restart, quit, or change theme.");
//        instructionsText.getStyleClass().add("instructions-text");
//
//        final Rectangle startButton = new Rectangle(400, 250, 200, 50);
//        startButton.setArcWidth(20);
//        startButton.setArcHeight(20);
//        startButton.getStyleClass().add("menu-button");
//        final Text startText = new Text(450, 280, "Start LetterRush");
//        startText.getStyleClass().add("menu-text");
//
//        final Rectangle returnButton = new Rectangle(400, 350, 200, 50);
//        returnButton.setArcWidth(20);
//        returnButton.setArcHeight(20);
//        returnButton.getStyleClass().add("menu-button");
//        final Text returnText = new Text(420, 380, "Return to Main Menu");
//        returnText.getStyleClass().add("menu-text");
//
//        menuPane.getChildren().addAll(scoreText, highScoreText, instructionsText, startButton, startText, returnButton, returnText);
//
//        startButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> startGame());
//        returnButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
//            stage.close();
//        });
//
//        menuScene = new Scene(menuPane, WINDOW_WIDTH, WINDOW_HEIGHT);
//        menuScene.getStylesheets().add(CSS_PATH);
//
//        stage.setTitle(TITLE + " - Menu");
//        stage.setScene(menuScene);
//        stage.setResizable(false);
//        stage.show();
//        isRunning = false;
//    }
//
//    private void startGame() {
//        root = new Pane();
//        root.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT);
//        root.getStyleClass().add("game-pane");
//        applyTheme();
//
//        final Pane gamePane = engine.getGamePane();
//        gamePane.setLayoutX((WINDOW_WIDTH - gamePane.getPrefWidth()) / 2);
//        gamePane.setLayoutY(0);
//
//        final Pane uiPane = ui.getUIPane();
//        uiPane.setLayoutX((WINDOW_WIDTH - uiPane.getPrefWidth()) / 2);
//        uiPane.setLayoutY(10);
//
//        root.getChildren().addAll(gamePane, uiPane);
//
//        final Button restartButton = new Button("Restart");
//        restartButton.setLayoutX(WINDOW_WIDTH / 4 - 50);
//        restartButton.setLayoutY(WINDOW_HEIGHT - 80);
//        restartButton.getStyleClass().add("game-button");
//        restartButton.setOnAction(event -> resetGame());
//
//        final Button quitButton = new Button("Quit");
//        quitButton.setLayoutX(WINDOW_WIDTH / 2 - 50);
//        quitButton.setLayoutY(WINDOW_HEIGHT - 80);
//        quitButton.getStyleClass().add("game-button");
//        quitButton.setOnAction(event -> stopGame());
//
//        final Button themeButton = new Button("Change Theme");
//        themeButton.setLayoutX(3 * WINDOW_WIDTH / 4 - 50);
//        themeButton.setLayoutY(WINDOW_HEIGHT - 80);
//        themeButton.getStyleClass().add("game-button");
//        themeButton.setOnAction(event -> changeTheme());
//
//        root.getChildren().addAll(restartButton, quitButton, themeButton);
//
//        Image cursorImage = null;
//        try {
//            cursorImage = new Image(CURSOR_IMAGE_PATH, CURSOR_SIZE, CURSOR_SIZE, true, true);
//            if (cursorImage.isError()) {
//                System.err.println("Cursor image failed to load: " + cursorImage.getException());
//            } else {
//                System.out.println("Cursor image loaded successfully: " + CURSOR_SIZE + "x" + CURSOR_SIZE);
//            }
//        } catch (Exception e) {
//            System.err.println("Error loading cursor image: " + e.getMessage());
//        }
//
//        final Cursor customCursor = (cursorImage != null && !cursorImage.isError())
//                                         ? new ImageCursor(cursorImage, CURSOR_SIZE / 2, CURSOR_SIZE / 2)
//                                         : ImageCursor.CROSSHAIR;
//
//        gameScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
//        gameScene.setCursor(customCursor);
//        gameScene.getStylesheets().add(CSS_PATH);
//
//        gameScene.setOnMouseMoved(event -> player.updateCursorPosition(event.getX(), event.getY()));
//        gameScene.setOnKeyPressed(event -> {
//            if (event.getCode() == KeyCode.Q) {
//                stopGame();
//            }
//        });
//
//        stage.setTitle(TITLE);
//        stage.setScene(gameScene);
//        stage.setOnCloseRequest(event -> stopGame());
//        stage.show();
//
//        if (levelManager != null) {
//            engine.startLevel(player, ui, levelManager);
//            isRunning = true;
//        } else {
//            System.err.println("Cannot start game: levelManager is null");
//            showLossAlert("Initialization Error", "LevelManager is null. Check resources and restart.", null);
//        }
//    }
//
//    private void applyTheme() {
//        try {
//            String themePath = THEMES[currentThemeIndex];
//            Image testImage = new Image(themePath);
//            if (testImage.isError()) {
//                throw new Exception("Image not found: " + themePath);
//            }
//            root.setStyle("-fx-background-image: url('" + themePath + "'); " +
//                          "-fx-background-size: cover; " +
//                          "-fx-background-position: center;");
//        } catch (Exception e) {
//            System.err.println("Failed to load theme: " + e.getMessage());
//            root.setStyle("-fx-background-color: #0f2027;");
//        }
//    }
//
//    private void changeTheme() {
//        currentThemeIndex = (currentThemeIndex + 1) % THEMES.length;
//        applyTheme();
//    }
//
//    private void resetGame() {
//        if (levelManager != null) {
//            System.out.println("Resetting game with LetterEngine instance: " + engine);
//            engine.resetGame(player, ui, levelManager);
//            isShowingAlert = false;
//        } else {
//            System.err.println("Cannot reset game: levelManager is null");
//        }
//    }
//
//    private void stopGame() {
//        isRunning = false;
//        int currentScore = player.getScore();
//        if (currentScore > highScore) {
//            highScore = currentScore;
//            highScoreText.setText("High Score: " + highScore);
//        }
//        scoreText.setText("Score: " + player.getScore() + " | Bonus: " + bonusPoints);
//        stage.setScene(menuScene);
//        isShowingAlert = false;
//    }
//
//    private void showAlert(Alert alert, Runnable onProceed, Runnable onClose, AnimationTimer timer) {
//        if (isShowingAlert) {
//            System.out.println("Alert already showing, skipping new alert.");
//            return;
//        }
//        isShowingAlert = true;
//        System.out.println("Showing alert: " + alert.getHeaderText());
//
//        if (timer != null) {
//            timer.stop();
//            System.out.println("AnimationTimer stopped before showing alert: " + timer);
//        }
//
//        Platform.runLater(() -> {
//            alert.getDialogPane().getStylesheets().add(CSS_PATH);
//            alert.getDialogPane().getStyleClass().add("alert");
//
//            alert.setOnCloseRequest(event -> {
//                isShowingAlert = false;
//                System.out.println("Alert closed via 'X' button, isShowingAlert reset to false.");
//                if (onClose != null) {
//                    onClose.run();
//                }
//                if (timer != null && onProceed != null) {
//                    timer.start();
//                    System.out.println("AnimationTimer resumed after 'X' button closure: " + timer);
//                }
//            });
//
//            ButtonType result = alert.showAndWait().orElse(null);
//            System.out.println("Alert closed with result: " + (result != null ? result.getText() : "null"));
//
//            isShowingAlert = false;
//            System.out.println("isShowingAlert reset to false.");
//
//            if (result != null) {
//                if (result.getText().equals("Proceed") || result.getText().equals("Try Again")) {
//                    if (onProceed != null) {
//                        onProceed.run();
//                        if (timer != null) {
//                            timer.start();
//                            System.out.println("AnimationTimer resumed after proceeding: " + timer);
//                        }
//                    }
//                } else {
//                    if (onClose != null) {
//                        onClose.run();
//                        System.out.println("Close action executed.");
//                    }
//                }
//            }
//        });
//    }
//
//    public void showLossAlert(String header, String content, AnimationTimer timer) {
//        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//        alert.setTitle("Game Over");
//        alert.setHeaderText(header);
//        alert.setContentText(content + "\nWould you like to try again or return to the main menu?");
//        alert.getButtonTypes().setAll(
//                new ButtonType("Try Again"),
//                new ButtonType("Return to Main Menu")
//                                     );
//        showAlert(alert, this::resetGame, this::stopGame, timer);
//    }
//
//    public void showLossAlertObstacle(AnimationTimer timer) {
//        showLossAlert("Game Over!", "You hit an obstacle! Try again?", timer);
//    }
//
//    public void showLossAlertTime(AnimationTimer timer) {
//        showLossAlert("Game Over!", "Time’s up! Try again?", timer);
//    }
//
//    public void showWinAlert(int level, boolean bonusFound, AnimationTimer timer) {
//        System.out.println("Showing win alert for level " + level + ", bonusFound: " + bonusFound);
//        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//        alert.setTitle("Congratulations!");
//        alert.setHeaderText("Congrats on finishing Level " + level + "!");
//        String content = "Would you like to proceed to Level " + (level + 1) + " or return to the main menu?";
//        if (bonusFound) {
//            content += "\nBonus Word Found! Extra Points!";
//        }
//        alert.setContentText(content);
//        alert.getButtonTypes().setAll(
//                new ButtonType("Proceed"),
//                new ButtonType("Return to Main Menu")
//                                     );
//        showAlert(alert, () -> {
//            System.out.println("Proceeding to next level after win alert.");
//            try {
//                levelManager.advanceLevel();
//                System.out.println("Advanced to level: " + levelManager.getCurrentLevelNumber());
//                engine.startLevel(player, ui, levelManager);
//            } catch (IllegalStateException e) {
//                System.err.println("Failed to advance level: " + e.getMessage());
//                showLossAlert("Level Error", "No more levels available. Returning to menu.", timer);
//            }
//        }, () -> {
//            System.out.println("Returning to main menu after win alert.");
//            stage.close();
//        }, timer);
//    }
//
//    public void setBonusPoints(int points) {
//        this.bonusPoints = points;
//    }
//}

public class LetterRush {
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 700; // Increased height for buttons
    private static final String CURSOR_IMAGE_PATH = "/cursor.png";
    private static final String CSS_PATH = "/letterStyles.css";
    private static final String TITLE = "LetterRush";
    private static final double CURSOR_SIZE = 32.0;
    private static final String[] THEMES = {
            "/background1.png", // Placeholder for Theme 1
            "/background2.png", // Placeholder for Theme 2
            "/background3.png"  // Placeholder for Theme 3
    };

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
private int currentThemeIndex = 0;
private Pane root; // To update theme

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

        menuPane.getChildren().addAll(scoreText, instructionsText, startButton, startText, returnButton, returnText);

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
    scoreText.setText("Score: " + player.getScore());
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
    });
}

public void showLossAlertObstacle() {
    showLossAlert("Game Over!", "You hit an obstacle! Try again?");
}

public void showLossAlertTime() {
    showLossAlert("Game Over!", "Time’s up! Try again?");
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
                engine.startLevel(player, ui, levelManager);
            } else {
                stage.close();
            }
        });
    });
}

public void showBonusAlert(int level) {
    Platform.runLater(() -> {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Bonus Found!");
        alert.setHeaderText("Bonus Word Found! Extra Points!");
        alert.setContentText("Proceed to Level " + (level + 1) + " or return to the main menu?");
        alert.getDialogPane().getStylesheets().add(CSS_PATH);
        alert.getDialogPane().getStyleClass().add("alert");

        alert.getButtonTypes().setAll(
                new javafx.scene.control.ButtonType("Proceed"),
                new javafx.scene.control.ButtonType("Return to Main Menu")
                                     );

        alert.showAndWait().ifPresent(response -> {
            if (response.getText().equals("Proceed")) {
                engine.startLevel(player, ui, levelManager);
            } else {
                stage.close();
            }
        });
    });
}
}