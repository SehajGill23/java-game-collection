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
 * This class extends {@code javafx.application.Application} and provides
 * the user interface for the number game's main menu. It includes options
 * to start the number game and to return to the overarching main menu
 * of the application (which might include other games).
 * <p>
 * The {@code GameMenu} is responsible for setting up the visual elements
 * of this specific menu, handling user interactions (button clicks), and
 * orchestrating the transitions to the number game itself or back to the
 * primary application menu. It utilizes JavaFX components like {@code Stage},
 * {@code Scene}, {@code VBox}, and {@code Button} to create the user interface.
 * </p>
 * <p>
 * This class also manages the lifecycle of the menu stage, ensuring it is
 * properly initialized and displayed when needed. It interacts with the
 * {@link GameGUI} to start the number game and with the {@code Main} class
 * from the word game package to return to the broader application menu.
 * </p>
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
     * Initializes the JavaFX application and sets up the main menu stage.
     * This method is called by the JavaFX runtime when the application is started.
     * It stores the primary stage and calls {@link #initializeMenu(Stage)} to
     * set up the menu's user interface. It also disables the implicit exit
     * of the platform to manage the application lifecycle explicitly.
     *
     * @param primaryStage the main stage for the JavaFX application, provided by the runtime.
     */
    @Override
    public void start(Stage primaryStage)
    {
        mainStage = primaryStage;
        initializeMenu(primaryStage);
        Platform.setImplicitExit(false);
    }

    /**
     * Sets up the user interface for the main menu with buttons to start the number game
     * and return to the main application menu. This method creates a vertical box layout
     * containing the buttons, sets up their actions, and applies styling.
     *
     * @param stage the primary stage of the application on which the menu is displayed.
     */
    private void initializeMenu(Stage stage)
    {
        VBox root;
        Scene scene;
        String cssPath;
        Button startButton;
        Button mainMenuButton;

        root = new VBox(BUTTON_SPACING);
        root.setAlignment(Pos.CENTER);
        root.setPadding(BUTTON_PADDING );
        scene = new Scene(root,
                          SCENE_WIDTH,
                          SCENE_HEIGHT);

        cssPath = GameGUI.validateCss(GAME_MENU_LABEL);

        if(cssPath != null)
        {
            scene.getStylesheets().add(cssPath);
        }

        startButton    = new Button(START_BUTTON_LABEL);
        mainMenuButton = new Button(MAIN_MENU_BUTTON_LABEL);

        startButton.getStyleClass().add(ACTION_BUTTON);
        mainMenuButton.getStyleClass().add(ACTION_BUTTON);

        startButton.setOnAction(_ ->
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

        mainMenuButton.setOnAction(_ ->
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
     * Sets the command-line arguments for the application. This method allows
     * other parts of the application to provide arguments that might be needed
     * when launching the JavaFX application.
     *
     * @param args the command-line arguments passed to the application.
     */
    public static void setMainArgs(final String[] args)
    {
        mainArgs = args;
    }


    /**
     * Initializes and launches the JavaFX application. This static method is
     * used to start the JavaFX application, specifically the {@code GameMenu} class.
     * It uses the stored main arguments if available.
     */
    public static void initializeJavaFX()
    {
        Application.launch(GameMenu.class,
                           mainArgs);
    }

    /**
     * Displays the number game menu. This method is used to show the menu stage.
     * If the stage has not been initialized yet, it creates a new one. It uses
     * {@code Platform.runLater} to ensure that UI updates are performed on the
     * JavaFX application thread, making it thread-safe.
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
     * Main entry point for launching the JavaFX application. This method is
     * the standard {@code main} method required for JavaFX applications that
     * are launched directly. It calls the {@code launch} method of the
     * {@code Application} class, which in turn calls the {@code start} method.
     *
     * @param args the command-line arguments passed to the application.
     */
    public static void main(final String[] args)
    {
        launch(args);
    }
}