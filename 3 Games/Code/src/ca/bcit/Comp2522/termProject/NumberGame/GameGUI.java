package ca.bcit.Comp2522.termProject.NumberGame;

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

/**
 * Manages the JavaFX-based user interface for the 20-Number Challenge game using a 4x5 grid.
 * @author Sehaj Gill
 * @version 1.0
 */
public class GameGUI extends Application
{
    private              boolean        gameOver;
    private final        GameController controller;
    private final        GameMenu       gameMenu;
    private              Button[][]     gridButtons;
    private              Label          currentNumberLabel;
    private              Label          statusLabel;
    private              Button         quitButton;
    private static final int            INVALID_PLACEMENT      = -1;
    private static final int            VALID_PLACEMENT        = 1;
    private static final int            NO_VALID_MOVES         = 2;
    private static final int            INCORRECT_ORDER        = 3;
    private static final int            GRID_ROWS              = 4;
    private static final int            GRID_COLS              = 5;
    private static final int            WINDOW_HEIGHT          = 350;
    private static final int            WINDOW_WIDTH           = 400;
    private static final int            BUTTON_SIZE            = 60;
    private static final int            BUTTON_SPACING         = 10;
    private static final int            GRID_GAP               = 5;
    private static final Insets         PADDING_LABEL          = new Insets(10);
    private static final String         GRID_BUTTON_DISPLAY    = "[]";
    private static final String         STATUS_WIN             = "win";
    private static final String         WIN_LABEL              = "You Won!";
    private static final String         QUIT_LABEL             = "Quit";
    private static final String         TRY_AGAIN_LABEL        = "Try Again";
    private static final String         GAME_OVER_LABEL        = "Game Over!";
    private static final String         SELECT_A_SLOT_LABEL    = "Select a slot";
    private static final String         SCORE_UPDATE_LABEL     = "Score Update";
    private static final String         FINAL_SCORE_LABEL      = "Final Score";
    private static final String         GAME_TITLE_LABEL       = "20-Number Challenge";
    private static final String         GAME_MENU_LABEL        = "Game Menu";
    private static final String         ACTION_BUTTON          = "action-button";
    private static final String         MSG_NO_VALID_MOVES     = " Impossible to place the next number: ";
    private static final String         MSG_INCORRECT_ORDER    = " Impossible to place the number: ";
    private static final String         MSG_TRY_AGAIN          = " Try again or Quit?";
    private static final String         CSS_DIRECTORY          = "/styles.css";
    private static final String         IN_SLOT_LABEL          = " in slot ";
    private static final String         ASC_ORDER_LABEL        = "Place the numbers in ascending order.";
    private static final String         NOT_IN_ASC_ORDER_LABEL = " Numbers are not in ascending order.";
    private static final String         WARNING_CSS_NOT_FOUND  = "Warning: %s not found for %s."
                                                                 + " Proceeding without CSS.";
    private static final String         FONT_STYLE             = "-fx-font-size: 16px; "
                                                                 + "-fx-font-weight: bold;";

    /**
     * Constructor for initializing the GameGUI with controller and menu.
     *
     * @param controller GameController instance
     * @param gameMenu GameMenu instance
     */
    public GameGUI(GameController controller,
                   GameMenu gameMenu)
    {
        this.controller = controller;
        this.gameMenu   = gameMenu;
        this.gameOver   = false;
    }

    /*
     * Validates the CSS file path.
     *
     * @param context the context where the CSS is being used
     * @return the CSS file path if found, null otherwise
     */
    public static String validateCss(String context)
    {
        String cssPath = GameGUI.class.getResource(CSS_DIRECTORY) != null
                         ? GameGUI.class.getResource(CSS_DIRECTORY).toExternalForm()
                         : null;

        if (cssPath == null) {
            System.err.println(String.format(WARNING_CSS_NOT_FOUND, CSS_DIRECTORY, context));
        }
        return cssPath;
    }

    /**
     * Starts the JavaFX application for the game UI.
     *
     * @param primaryStage the primary stage for the game window
     */
    @Override
    public void start(Stage primaryStage)
    {
        GridPane gridPane;
        BorderPane labelPane;
        HBox buttonPane;

        BorderPane root = new BorderPane();
        Scene scene = new Scene(root,
                                WINDOW_WIDTH,
                                WINDOW_HEIGHT);

        String cssPath;
        cssPath = validateCss("scene");

        if(cssPath != null)
        {
            scene.getStylesheets().add(cssPath);
        }



        gridPane = new GridPane();

        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(GRID_GAP);
        gridPane.setVgap(GRID_GAP);
        gridPane.setPadding(PADDING_LABEL);
        gridButtons = new Button[GRID_ROWS][GRID_COLS];

        for(int i = 0; i < GRID_ROWS; i++)
        {
            for(int j = 0; j < GRID_COLS; j++)
            {
                final int row = i;
                final int col = j;
                gridButtons[i][j] = new Button( GRID_BUTTON_DISPLAY);
                gridButtons[i][j].setPrefSize(BUTTON_SIZE,
                                              BUTTON_SIZE);
                gridButtons[i][j].setStyle(FONT_STYLE);
                gridButtons[i][j].setOnAction(e -> handleButtonClick(row,
                                                                     col));
                gridPane.add(gridButtons[i][j],
                             j,
                             i);
            }
        }

        currentNumberLabel = new Label(SELECT_A_SLOT_LABEL);
        statusLabel        = new Label(ASC_ORDER_LABEL);
        currentNumberLabel.setAlignment(Pos.CENTER);
        statusLabel.setAlignment(Pos.CENTER);

        labelPane = new BorderPane();

        labelPane.setTop(currentNumberLabel);
        labelPane.setCenter(statusLabel);
        labelPane.setPadding(PADDING_LABEL);

        quitButton = new Button(GAME_MENU_LABEL);

        quitButton.setDisable(false);

        quitButton.getStyleClass().add(ACTION_BUTTON);

        quitButton.setOnAction(e ->
                               {
                                   returnToMenu();
                               });

        buttonPane = new HBox(BUTTON_SPACING,
                                   quitButton);

        buttonPane.setAlignment(Pos.CENTER);
        buttonPane.setPadding(PADDING_LABEL);

        root.setCenter(gridPane);
        root.setTop(labelPane);
        root.setBottom(buttonPane);

        primaryStage.setMinHeight(WINDOW_HEIGHT);
        primaryStage.setMinWidth(WINDOW_WIDTH);
        primaryStage.setTitle(GAME_TITLE_LABEL);
        primaryStage.setScene(scene);
        primaryStage.show();

        resetGame();
    }

    /**
     * Updates the grid display after each move.
     */
    public void updateGridDisplay()
    {
        int[][] grid;
        grid = controller.getGrid();
        currentNumberLabel.setText(SELECT_A_SLOT_LABEL);

        for(int i = 0; i < GRID_ROWS; i++)
        {
            for(int j = 0; j < GRID_COLS; j++)
            {
                if(grid[i][j] == INVALID_PLACEMENT)
                {
                    gridButtons[i][j].setText(GRID_BUTTON_DISPLAY);
                    gridButtons[i][j].setDisable(false);
                }
                else
                {
                    gridButtons[i][j].setText(String.valueOf(grid[i][j]));
                    gridButtons[i][j].setDisable(true);
                }
            }
        }
    }

    /**
     * Displays a Game Over message and handles the end of the game.
     *
     * @param message the game over message to display
     */
    public void showGameOver(String message)
    {
        Alert gameOverAlert;

        gameOver = true;
        statusLabel.setText(GAME_OVER_LABEL);

        for(int i = 0; i < GRID_ROWS; i++)
        {
            for(int j = 0; j < GRID_COLS; j++)
            {
                gridButtons[i][j].setDisable(true);
            }
        }

        gameOverAlert = new Alert(Alert.AlertType.CONFIRMATION);
        gameOverAlert.setTitle(GAME_OVER_LABEL);
        gameOverAlert.setHeaderText(null);
        gameOverAlert.setContentText(message);

        ButtonType tryAgainButtonType = new ButtonType(TRY_AGAIN_LABEL);
        ButtonType quitButtonType     = new ButtonType(QUIT_LABEL);
        gameOverAlert.getButtonTypes().setAll(tryAgainButtonType,
                                              quitButtonType);

        String cssPath;
        cssPath = validateCss("alert");

        if(cssPath != null)
        {
            gameOverAlert.getDialogPane().getStylesheets().add(cssPath);
        }

        gameOverAlert.showAndWait().ifPresent(response ->
                                              {
                                                  if(response == tryAgainButtonType)
                                                  {
                                                      Alert scoreAlert;
                                                      scoreAlert = new Alert(Alert.AlertType.INFORMATION);
                                                      scoreAlert.setTitle(SCORE_UPDATE_LABEL);
                                                      scoreAlert.setHeaderText(null);
                                                      scoreAlert.setContentText(controller.getScore());
                                                      if(cssPath != null)
                                                      {
                                                          scoreAlert.getDialogPane().getStylesheets().add(cssPath);
                                                      }
                                                      scoreAlert.showAndWait();
                                                      resetGame();
                                                  }
                                                  else if(response == quitButtonType)
                                                  {
                                                      Alert scoreAlert;
                                                      scoreAlert = new Alert(Alert.AlertType.INFORMATION);
                                                      scoreAlert.setTitle(FINAL_SCORE_LABEL);
                                                      scoreAlert.setHeaderText(null);
                                                      scoreAlert.setContentText(controller.getScore());
                                                      if(cssPath != null)
                                                      {
                                                          scoreAlert.getDialogPane().getStylesheets().add(cssPath);
                                                      }
                                                      scoreAlert.showAndWait();
                                                      returnToMenu();
                                                  }
                                              });
    }

    /*
     * Handles button clicks on the game grid.
     *
     * @param row the row index of the clicked button
     * @param col the column index of the clicked button
     */
    private void handleButtonClick(final int row,
                                 final int col)
    {

        if(!controller.isValidPlacement(row,
                                        col))
        {
            gameOver = true;
            showGameOver( GAME_OVER_LABEL +  NOT_IN_ASC_ORDER_LABEL +  MSG_TRY_AGAIN);
            return;
        }
        int result = controller.placeNumber(row,
                                            col);
        updateGridDisplay();

        if(result != INVALID_PLACEMENT)
        {
            if(result == VALID_PLACEMENT)
            {
                String status;
                status = controller.checkGameStatus();
                if(status.equals(STATUS_WIN))
                {
                    statusLabel.setText(WIN_LABEL);
                    showGameOver(controller.getScore());
                }
            }

            else if(result == NO_VALID_MOVES)
            {
                gameOver = true;
                showGameOver(GAME_OVER_LABEL + MSG_NO_VALID_MOVES
                             + controller.getCurrentNumber() + MSG_TRY_AGAIN);
            }
            else if(result == INCORRECT_ORDER)
            {
                gameOver = true;
                showGameOver(GAME_OVER_LABEL + MSG_INCORRECT_ORDER
                             + controller.getGrid()[row][col] +  IN_SLOT_LABEL
                             + ((row * GRID_COLS) + col + 1) + MSG_TRY_AGAIN);
            }
        }
    }

    /**
     * Resets the game to its initial state.
     */
    public void resetGame()
    {
        controller.startGame();
        gameOver = false;
        updateGridDisplay();
        statusLabel.setText(ASC_ORDER_LABEL);
        quitButton.setDisable(false);
    }

    /*
     * Takes the user back to the game menu.
     */
    private void returnToMenu()
    {
        Stage stage;
        stage = (Stage) quitButton.getScene().getWindow();
        stage.hide();

        gameMenu.showNumberGameMenu();
    }

    /**
     * Launches the GameGUI application.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        launch(args);
    }
}



