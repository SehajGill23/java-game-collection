package ca.bcit.Comp2522.termProject.MyGame;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class GameMenu extends Application
{
    private static final int WINDOW_WIDTH  = 800;
    private static final int WINDOW_HEIGHT = 600;

    @Override
    public void start(Stage primaryStage)
    {
        final Pane root = new Pane();
        root.setPrefSize(WINDOW_WIDTH,
                         WINDOW_HEIGHT);
        root.getStyleClass().add("main-menu");


        final Rectangle letterRushButton = new Rectangle(300,
                                                         200,
                                                         200,
                                                         50);
        letterRushButton.getStyleClass().add("menu-button");
        final Text letterRushText = new Text(350,
                                             235,
                                             "LetterRush");
        letterRushText.getStyleClass().add("menu-text");

        // NumberGame Button (assumed)
        final Rectangle numberGameButton = new Rectangle(300,
                                                         300,
                                                         200,
                                                         50);
        numberGameButton.getStyleClass().add("menu-button");
        final Text numberGameText = new Text(350,
                                             335,
                                             "NumberGame");
        numberGameText.getStyleClass().add("menu-text");

        // Rules Text for LetterRush
        final Text rulesText = new Text(150,
                                        400,
                                        "LetterRush Rules:\n" +
                                          "- Click letters to form the target word (coral) and bonus word (green).\n"
                                        + "- Avoid obstacles (missile, bomb, spike) that move randomly.\n"
                                        + "- Complete words before time runs out to advance levels.\n"
                                        + "- Press 'R' to restart, 'Q' to return to menu.");
        rulesText.getStyleClass().add("rules-text");

        root.getChildren().addAll(letterRushButton,
                                  letterRushText,
                                  numberGameButton,
                                  numberGameText,
                                  rulesText);

        letterRushButton.addEventHandler(MouseEvent.MOUSE_CLICKED,
                                         event -> LetterRush.launchGame());
        numberGameButton.addEventHandler(MouseEvent.MOUSE_CLICKED,
                                         event ->
                                         {
                                         });

        final Scene scene = new Scene(root,
                                      WINDOW_WIDTH,
                                      WINDOW_HEIGHT);
        scene.getStylesheets().add("/letterStyles.css");

        primaryStage.setTitle("Game Menu");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void initializeJavaFX(String[] args)
    {
        new Thread(() -> Application.launch(GameMenu.class,
                                            args)).start();
    }

    public static void main(String[] args)
    {
        initializeJavaFX(args);
    }
}
