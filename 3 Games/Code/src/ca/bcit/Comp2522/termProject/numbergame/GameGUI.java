package ca.bcit.comp2522.termproject.numbergame;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * Manages the JavaFX-based user interface for the 20-Number Challenge game using a 4x5 grid.
 * <p>
 * This class extends {@code javafx.application.Application} and serves as the visual
 * representation of the 20-Number Challenge game. It is responsible for constructing
 * and updating the graphical elements that the user interacts with to play the game.
 * These elements include a grid of buttons representing the game board, labels that
 * provide feedback and instructions to the player, and a button to navigate back to
 * the main game menu.
 * </p>
 * <p>
 * The {@code GameGUI} acts as a mediator between the user's actions and the underlying
 * game logic, which is handled by the {@link GameController}. When a user clicks on a
 * button in the grid, this class captures the event and communicates the player's
 * intended move (the row and column of the clicked button) to the controller. Based on
 * the controller's evaluation of the move, the {@code GameGUI} updates the visual
 * state of the game, such as changing the text on the buttons to reflect placed numbers
 * or displaying messages about the game's status.
 * </p>
 * <p>
 * Error handling and user feedback are also key responsibilities of this class. It
 * validates the loading of the CSS stylesheet to ensure proper styling of the UI.
 * Furthermore, it displays alert dialogs to inform the player about significant game
 * events, such as winning, losing due to no valid moves, or attempting to place a
 * number out of sequence. These alerts often provide options for the player to either
 * try the game again or to quit and return to the main menu. The score is also displayed
 * at the end of the game.
 * </p>
 * <p>
 * The layout of the game UI is structured using JavaFX layout panes like {@code BorderPane},
 * {@code GridPane}, and {@code HBox} to organize the game board, labels, and buttons
 * effectively within the application window. The visual appearance is further enhanced
 * through CSS styling applied to the scene and alert dialogs.
 * </p>
 *
 * @author Sehaj Gill
 * @version 1.0
 */
public class GameGUI extends Application
{
    private static final int    INVALID_PLACEMENT           = -1;
    private static final int    VALID_PLACEMENT             = 1;
    private static final int    NO_VALID_MOVES              = 2;
    private static final int    INCORRECT_ORDER             = 3;
    private static final int    GRID_ROWS                   = 4;
    private static final int    GRID_COLS                   = 5;
    private static final int    DEFAULT_WINDOW_HEIGHT       = 350;
    private static final int    DEFAULT_WINDOW_WIDTH        = 400;
    private static final int    GRID_BUTTON_SIZE            = 60;
    private static final int    GRID_BUTTON_SPACING         = 10;
    private static final int    GRID_ELEMENT_GAP            = 5;
    private static final int    CURRENT_BASE_INDEX          = 0;
    private static final Insets LABEL_PADDING               = new Insets(10);
    private static final String DISPLAY_EMPTY_GRID_SLOT     = "[]";
    private static final String GAME_STATUS_WIN             = "win";
    private static final String LABEL_WIN_MESSAGE           = "You Won!";
    private static final String SCENE_PATH                  = "scene";
    private static final String ALERT_PATH                  = "alert";
    private static final String LABEL_QUIT_BUTTON           = "Quit";
    private static final String LABEL_TRY_AGAIN             = "Try Again";
    private static final String LABEL_GAME_OVER             = "Game Over!";
    private static final String LABEL_SELECT_SLOT           = "Select a slot";
    private static final String LABEL_SCORE_UPDATE          = "Score Update";
    private static final String LABEL_FINAL_SCORE           = "Final Score";
    private static final String LABEL_GAME_TITLE            = "20-Number Challenge";
    private static final String LABEL_GAME_MENU             = "Game Menu";
    private static final String STYLE_ACTION_BUTTON         = "action-button";
    private static final String MESSAGE_NO_VALID_MOVE       = " Impossible to place the next number: ";
    private static final String MESSAGE_INCORRECT_PLACEMENT = " Impossible to place the number: ";
    private static final String MESSAGE_TRY_AGAIN_OR_QUIT   = " Try again or Quit?";
    private static final String CSS_FILE_PATH               = "/styles.css";
    private static final String MESSAGE_IN_SLOT             = " in slot ";
    private static final String PLATFORM_NEWLINE            = "%n";
    private static final String INSTRUCTION_ASCENDING_ORDER = "Place the numbers in ascending order.";
    private static final String MESSAGE_NOT_ASCENDING       = " Numbers are not in ascending order.";
    private static final String WARNING_CSS_NOT_FOUND       = "Warning: %s not found for %s. Proceeding without CSS.";
    private static final String FONT_STYLE                  = "-fx-font-size: 16px; -fx-font-weight: bold;";

    private              boolean        gameOver;
    private final        GameController controller;
    private final        GameMenu       gameMenu;
    private              Button[][]     gridButtons;
    private              Label          currentNumberLabel;
    private              Label          statusLabel;
    private              Button         quitButton;

    /*
     * Constructor for initializing the {@code GameGUI} with a {@link GameController}
     * instance to handle the game logic and a {@link GameMenu} instance for navigation.
     * This constructor establishes the necessary links between the UI and the game's
     * core functionalities and the application's overall menu system.
     *
     * @param controller The {@code GameController} that provides the game's rules, state,
     * and move validation logic.
     * @param gameMenu   The {@code GameMenu} instance that allows the game UI to trigger
     * a return to the main application menu.
     */
    GameGUI(final GameController controller,
           final GameMenu gameMenu)
    {
        this.controller = controller;
        this.gameMenu = gameMenu;
        this.gameOver = false;
    }


    /**
     * Starts the JavaFX application, setting up the primary stage with the game's user interface.
     * This method is the main entry point for the UI and is called by the JavaFX runtime. It performs
     * the following steps:
     * <ol>
     * <li>Creates the main window ({@code Stage}).</li>
     * <li>Sets up the primary layout using {@code BorderPane} to organize different UI sections.</li>
     * <li>Loads and applies CSS styling to the scene using {@link #validateCss(String)}.</li>
     * <li>Creates a {@code GridPane} to represent the 4x5 game board and populates it with {@code Button} objects.</li>
     * <li>Initializes the {@code Label} for displaying instructions/status and the {@code Button} for quitting.</li>
     * <li>Sets up event handlers for the grid buttons to detect player moves ({@link #handleButtonClick(int, int)}).</li>
     * <li>Arranges the grid, labels, and the quit button within the {@code BorderPane}.</li>
     * <li>Sets the minimum dimensions and title of the primary stage.</li>
     * <li>Displays the game window to the user.</li>
     * <li>Calls {@link #resetGame()} to initialize the game state and UI.</li>
     * </ol>
     *
     * @param primaryStage the primary stage for the game window, provided by the JavaFX runtime.
     */
    @Override
    public void start(final Stage primaryStage)
    {
        GridPane gridPane;
        BorderPane labelPane;
        HBox buttonPane;
        BorderPane root;
        Scene scene;
        String cssPath;


        root = new BorderPane();
        scene = new Scene(root,
                                DEFAULT_WINDOW_WIDTH,
                                DEFAULT_WINDOW_HEIGHT);

        cssPath = validateCss(SCENE_PATH);
        if (cssPath != null)
        {
            scene.getStylesheets().add(cssPath);
        }

        gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(GRID_ELEMENT_GAP);
        gridPane.setVgap(GRID_ELEMENT_GAP);
        gridPane.setPadding(LABEL_PADDING);
        gridButtons = new Button[GRID_ROWS][GRID_COLS];

        for (int i =  CURRENT_BASE_INDEX; i < GRID_ROWS; i++)
        {
            for (int j =  CURRENT_BASE_INDEX; j < GRID_COLS; j++)
            {
                final int row;
                final int col;

                row = i;
                col = j;
                gridButtons[i][j] = new Button(DISPLAY_EMPTY_GRID_SLOT);
                gridButtons[i][j].setPrefSize(GRID_BUTTON_SIZE,
                                              GRID_BUTTON_SIZE);
                gridButtons[i][j].setStyle(FONT_STYLE);
                gridButtons[i][j].setOnAction(_ -> handleButtonClick(row,
                                                                     col));
                gridPane.add(gridButtons[i][j],
                             j,
                             i);
            }
        }

        currentNumberLabel = new Label(LABEL_SELECT_SLOT);
        statusLabel = new Label(INSTRUCTION_ASCENDING_ORDER);
        currentNumberLabel.setAlignment(Pos.CENTER);
        statusLabel.setAlignment(Pos.CENTER);

        labelPane = new BorderPane();
        labelPane.setTop(currentNumberLabel);
        labelPane.setCenter(statusLabel);
        labelPane.setPadding(LABEL_PADDING);

        quitButton = new Button(LABEL_GAME_MENU);
        quitButton.setDisable(false);
        quitButton.getStyleClass().add(STYLE_ACTION_BUTTON);
        quitButton.setOnAction(_ -> returnToMenu());

        buttonPane = new HBox(GRID_BUTTON_SPACING,
                              quitButton);
        buttonPane.setAlignment(Pos.CENTER);
        buttonPane.setPadding(LABEL_PADDING);

        root.setCenter(gridPane);
        root.setTop(labelPane);
        root.setBottom(buttonPane);

        primaryStage.setMinHeight(DEFAULT_WINDOW_HEIGHT);
        primaryStage.setMinWidth(DEFAULT_WINDOW_WIDTH);
        primaryStage.setTitle(LABEL_GAME_TITLE);
        primaryStage.setScene(scene);
        primaryStage.show();

        resetGame();
    }

    /*
     * Validates the provided CSS file path by attempting to locate the resource.
     * If the CSS file is not found, a warning message is printed to the error stream.
     * This method is crucial for ensuring that the visual styling of the game is applied.
     * It checks if the CSS file exists in the application's resources and, if not,
     * logs a warning indicating that the UI will proceed without the intended styling.
     *
     * @param context A string describing the context where the CSS is being used (e.g., "scene", "alert").
     * This helps in providing more informative warnings if the CSS is missing.
     * @return The external form of the CSS file path if the resource is found; otherwise, {@code null}.
     */
    static String validateCss(final String context)
    {
        String cssPath;
        cssPath = GameGUI.class.getResource(CSS_FILE_PATH) != null
                         ? Objects.requireNonNull(GameGUI.class.getResource(CSS_FILE_PATH)).toExternalForm()
                         : null;

        if (cssPath == null)
        {
            System.err.printf((WARNING_CSS_NOT_FOUND) + PLATFORM_NEWLINE, CSS_FILE_PATH, context);
        }
        return cssPath;
    }

    /*
     * Updates the visual representation of the game grid based on the current state
     * provided by the {@link GameController}. Empty slots are displayed with the
     * default {@link #DISPLAY_EMPTY_GRID_SLOT}, while filled slots show the placed number.
     * Buttons corresponding to filled slots are disabled to prevent further interaction.
     * This method iterates through the 2D array representing the game grid obtained from
     * the controller and updates the text and disabled status of each button in the UI
     * to reflect the underlying game state.
     */
    private void updateGridDisplay()
    {
        int[][] grid;
        grid = controller.getGrid();
        currentNumberLabel.setText(LABEL_SELECT_SLOT);

        for (int i = CURRENT_BASE_INDEX; i < GRID_ROWS; i++)
        {
            for (int j = CURRENT_BASE_INDEX; j < GRID_COLS; j++)
            {
                if (grid[i][j] == INVALID_PLACEMENT)
                {
                    gridButtons[i][j].setText(DISPLAY_EMPTY_GRID_SLOT);
                    gridButtons[i][j].setDisable(false);
                } else
                {
                    gridButtons[i][j].setText(String.valueOf(grid[i][j]));
                    gridButtons[i][j].setDisable(true);
                }
            }
        }
    }

    /*
     * Displays a "Game Over" alert dialog with a specified message and options for
     * the player to try again or quit. Based on the player's choice, it either
     * resets the game or navigates back to the game menu, showing the final score.
     * This method is called when the game ends due to a win, loss (no valid moves),
     * or an incorrect placement.
     *
     * @param message the game over message to display in the alert.
     */
    private void showGameOver(final String message)
    {
        Alert gameOverAlert;
        ButtonType tryAgainButtonType;
        ButtonType quitButtonType;
        String cssPath;

        gameOver = true;
        statusLabel.setText(LABEL_GAME_OVER);

        for (int i = CURRENT_BASE_INDEX; i < GRID_ROWS; i++)
        {
            for (int j = CURRENT_BASE_INDEX; j < GRID_COLS; j++)
            {
                gridButtons[i][j].setDisable(true);
            }
        }

        gameOverAlert = new Alert(Alert.AlertType.CONFIRMATION);
        gameOverAlert.setTitle(LABEL_GAME_OVER);
        gameOverAlert.setHeaderText(null);
        gameOverAlert.setContentText(message);

        tryAgainButtonType = new ButtonType(LABEL_TRY_AGAIN);
        quitButtonType = new ButtonType(LABEL_QUIT_BUTTON);

        gameOverAlert.getButtonTypes().setAll(tryAgainButtonType,
                                              quitButtonType);
        cssPath = validateCss(ALERT_PATH);

        if (cssPath != null)
        {
            gameOverAlert.getDialogPane().getStylesheets().add(cssPath);
        }

        gameOverAlert.showAndWait().ifPresent(response ->
                                              {
                                                  if (response == tryAgainButtonType)
                                                  {
                                                      Alert scoreAlert;

                                                      scoreAlert = new Alert(Alert.AlertType.INFORMATION);
                                                      scoreAlert.setTitle(LABEL_SCORE_UPDATE);
                                                      scoreAlert.setHeaderText(null);
                                                      scoreAlert.setContentText(controller.getScore());
                                                      if (cssPath != null)
                                                      {
                                                          scoreAlert.getDialogPane().getStylesheets().add(cssPath);
                                                      }
                                                      scoreAlert.showAndWait();
                                                      resetGame();
                                                  } else if (response == quitButtonType)
                                                  {
                                                      Alert scoreAlert;

                                                      scoreAlert = new Alert(Alert.AlertType.INFORMATION);
                                                      scoreAlert.setTitle(LABEL_FINAL_SCORE);
                                                      scoreAlert.setHeaderText(null);
                                                      scoreAlert.setContentText(controller.getScore());
                                                      if (cssPath != null)
                                                      {
                                                          scoreAlert.getDialogPane().getStylesheets().add(cssPath);
                                                      }
                                                      scoreAlert.showAndWait();
                                                      returnToMenu();
                                                  }
                                              });
    }

    /*
     * Handles clicks on the game grid buttons. It first checks if the attempted placement
     * is valid according to the {@link GameController}. If not, it triggers a game over
     * scenario. If the placement is valid, it calls the {@code placeNumber} method of the
     * controller and updates the grid display. It then checks for win/loss conditions
     * based on the result of the placement.
     *
     * @param row the row index of the clicked button.
     * @param col the column index of the clicked button.
     */
    private void handleButtonClick(final int row,
                                   final int col)
    {
        int result;

        if (!controller.isValidPlacement(row,
                                         col))
        {
            gameOver = true;
            showGameOver(LABEL_GAME_OVER + MESSAGE_NOT_ASCENDING + MESSAGE_TRY_AGAIN_OR_QUIT);
            return;
        }
        result = controller.placeNumber(row,
                                        col);
        updateGridDisplay();

        if (result != INVALID_PLACEMENT)
        {
            if (result == VALID_PLACEMENT)
            {
                String status;
                status = controller.checkGameStatus();

                if (status.equals(GAME_STATUS_WIN))
                {
                    statusLabel.setText(LABEL_WIN_MESSAGE);
                    showGameOver(controller.getScore());
                }
            } else if (result == NO_VALID_MOVES)
            {
                gameOver = true;
                showGameOver(LABEL_GAME_OVER + MESSAGE_NO_VALID_MOVE
                             + controller.getCurrentNumber() + MESSAGE_TRY_AGAIN_OR_QUIT);
            } else if (result == INCORRECT_ORDER)
            {
                gameOver = true;
                showGameOver(LABEL_GAME_OVER + MESSAGE_INCORRECT_PLACEMENT
                             + controller.getGrid()[row][col] + MESSAGE_IN_SLOT
                             + ((row * GRID_COLS) + col + 1) + MESSAGE_TRY_AGAIN_OR_QUIT);
            }
        }
    }

    /*
     * Resets the game to its initial state by calling the {@code startGame} method
     * of the {@link GameController}, resetting the {@code gameOver} flag, updating
     * the grid display to show empty slots, and resetting the status label to the
     * initial instruction. The quit button is also re-enabled.
     * This method is typically called at the beginning of a new game or when the
     * player chooses to try again after a game over.
     */
    private void resetGame()
    {
        controller.startGame();
        gameOver = false;
        updateGridDisplay();
        statusLabel.setText(INSTRUCTION_ASCENDING_ORDER);
        quitButton.setDisable(false);
    }
    /*
     * Navigates the user back to the game menu by hiding the current game window
     * and calling the {@code showNumberGameMenu} method of the {@link GameMenu} class.
     * This method is invoked when the player clicks the quit button or chooses to quit
     * from the game over alert.
     */
    private void returnToMenu()
    {
        Stage stage;
        stage = (Stage)
                quitButton.getScene().getWindow();
        stage.hide();
        GameMenu.showNumberGameMenu();
    }

    /**
     * Launches the GameGUI application. This is the entry point for the JavaFX UI.
     * The {@code main} method is required for all JavaFX applications.
     *
     * @param args command line arguments passed to the application.
     */
    public static void main(final String[] args)
    {
        launch(args);
    }
}