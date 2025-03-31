package ca.bcit.Comp2522.termProject.MyGame;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class CursorWeaverGame
{
    
    /**
     * The CursorWeaverGame class is the JavaFX entry point for the custom game mode in the Geography Trivia Game.
     * It sets up the game window, custom cursor, and UI, delegating game logic to GameController.
     * Launched via the "M" option in Main.
     *
     * @author [Your Name]
     * @version 1.0
     */
    public class CursorWeaverGame extends Application {
        private static final int WINDOW_WIDTH = 800;
        private static final int WINDOW_HEIGHT = 600;
        private static final String CURSOR_IMAGE_PATH = "/cursor.png"; // Placeholder: Add your cursor image
        private static final String CSS_PATH = "/cursorStyles.css";
        private final GameController controller = new GameController();

        /**
         * Launches the JavaFX game in a separate thread, called from Main.
         */
        public static void launchGame() {
            new Thread(() -> Application.launch(CursorWeaverGame.class)).start();
        }

        /**
         * Sets up the JavaFX stage, scene, custom cursor, and UI elements.
         *
         * @param primaryStage The primary stage for this application.
         */
        @Override
        public void start(Stage primaryStage) {
            // Create root pane
            Pane root = new Pane();
            root.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT);

            // Add game pane from controller
            Pane gamePane = controller.getGamePane();
            root.getChildren().add(gamePane);

            // Set up custom cursor (assumes cursor.png exists in Resources)
            Image cursorImage = new Image(CURSOR_IMAGE_PATH);
            ImageCursor customCursor = new ImageCursor(cursorImage, cursorImage.getWidth() / 2, cursorImage.getHeight() / 2);
            Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
            scene.setCursor(customCursor);

            // Apply CSS styling
            scene.getStylesheets().add(CSS_PATH);

            // Configure stage
            primaryStage.setTitle("Cursorweaver's Challenge");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);

            // Handle window close to return to Main menu
            primaryStage.setOnCloseRequest(event -> {
                Main.waitingForConsoleInput = true; // Reset Main's console input flag
                Platform.exit();
            });

            // Show the stage
            primaryStage.show();

            // Start the game
            controller.startLevel();
        }
    }

}
