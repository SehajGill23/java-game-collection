package ca.bcit.Comp2522.termProject.numbergame;

import ca.bcit.Comp2522.termProject.wordgame.Main;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Manages the JavaFX-based main menu for the 20-Number Challenge game.
 * This class is responsible for displaying the menu and transitioning
 * to the game or main menu.
 *
 * @author Sehaj Gill
 * @version 1.0
 */
public class GameMenu extends Application
{
    private static       Stage    mainStage;
    private static       String[] mainArgs;
    private static final int      SCENE_WIDTH            = 300;
    private static final int      SCENE_HEIGHT           = 100;
    private static final int      BUTTON_SPACING         = 10;
    private static final String   START_BUTTON_LABEL     = "Start Number Game";
    private static final String   MAIN_MENU_BUTTON_LABEL = "Return to Main Menu";
    private static final String   GAME_MENU_LABEL        = "Game Menu";
    private static final String   ACTION_BUTTON          = "action-button";
    private static final Insets   BUTTON_PADDING         = new Insets(20);

    /**
     * Initializes the JavaFX application and the main menu.
     *
     * @param primaryStage the main stage for the JavaFX application
     */
    @Override
    public void start(Stage primaryStage)
    {
        mainStage = primaryStage;
        initializeMenu(primaryStage);
        Platform.setImplicitExit(false);
    }

    /**
     * Sets up the user interface for the main menu with buttons to start the game
     * and return to the main menu.
     *
     * @param stage the primary stage of the application
     */
    private void initializeMenu(Stage stage)
    {
        VBox root;
        root = new VBox(BUTTON_SPACING);
        root.setAlignment(Pos.CENTER);
        root.setPadding(BUTTON_PADDING );
        Scene scene;
        scene = new Scene(root,
                          SCENE_WIDTH,
                          SCENE_HEIGHT);


        String cssPath = GameGUI.validateCss(GAME_MENU_LABEL);

        if(cssPath != null)
        {
            scene.getStylesheets().add(cssPath);
        }

        Button startButton;
        Button mainMenuButton;

        startButton    = new Button(START_BUTTON_LABEL);
        mainMenuButton = new Button(MAIN_MENU_BUTTON_LABEL);

        startButton.getStyleClass().add(ACTION_BUTTON);
        mainMenuButton.getStyleClass().add(ACTION_BUTTON);

        startButton.setOnAction(e ->
                                {
                                    stage.hide();
                                    GameController controller;
                                    Stage          gameStage;
                                    GameGUI        gameGUI;

                                    controller = new AscendingOrderGame();
                                    gameGUI    = new GameGUI(controller,
                                                             this);
                                    gameStage  = new Stage();
                                    gameGUI.start(gameStage);
                                });

        mainMenuButton.setOnAction(e ->
                                   {
                                       stage.hide();
                                       Main.ConsoleInput();
                                   });

        root.getChildren().addAll(startButton,
                                  mainMenuButton);

        stage.setTitle(GAME_MENU_LABEL);
        stage.setScene(scene);
    }

    /**
     * Sets the command-line arguments for the application.
     *
     * @param args the command-line arguments
     */
    public static void setMainArgs(String[] args)
    {
        mainArgs = args;
    }


    /**
     * Launches the JavaFX application.
     */
    public static void initializeJavaFX()
    {
        Application.launch(GameMenu.class,
                           mainArgs);
    }

    /**
     * Displays the number game menu.
     * This method is used to show or create a new stage for the game menu.
     */
    public static void showNumberGameMenu()
    {
        Platform.runLater(() ->
                          {
                              if(mainStage == null)
                              {
                                  mainStage = new Stage();
                                  new GameMenu().initializeMenu(mainStage);
                              }
                              else
                              {
                                  mainStage.show();
                              }
                          });
    }

    /**
     * Main entry point for launching the JavaFX application.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args)
    {
        launch(args);
    }
}