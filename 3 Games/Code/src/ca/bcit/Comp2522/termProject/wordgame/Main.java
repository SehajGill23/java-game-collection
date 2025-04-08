package ca.bcit.Comp2522.termProject.wordgame;

import ca.bcit.Comp2522.termProject.letterrushgame.LetterRush;
import ca.bcit.Comp2522.termProject.numbergame.GameMenu;

import java.util.Scanner;

/**
 * The Main class serves as the central entry point for different game modes: Word Game, Number Game,
 * and Letter Rush. It manages user interaction for game mode selection and orchestrates the launch
 * of the chosen game.
 *
 * @author Sehaj Gill
 * @version 1.0
 */
public final class Main
{
    private static final String  RESOURCE_DIR                 = "Resources";
    private static final String  WORD_GAME_MODE               = "W";
    private static final String  NUMBER_GAME_MODE             = "N";
    private static final String  LETTER_RUSH_GAME_MODE        = "M";
    private static final String  QUIT_MODE                    = "Q";
    private static final String  MAIN_MENU_HEADER             = "\n---------------MAIN MENU---------------";
    private static final String  WELCOME_MESSAGE              = "Welcome to the Geography Trivia Game!";
    private static final String  INPUT_PROMPT                 = "\nEnter your Choice: ";
    private static final String  STARTING_WORD_GAME_MESSAGE   = "Starting Word Game...";
    private static final String  STARTING_NUMBER_GAME_MESSAGE = "Starting Number Game...";
    private static final String  STARTING_LETTER_RUSH_MESSAGE = "Starting Letter Rush Game... ";
    private static final String  QUITTING_MESSAGE             = "Quitting the Game...";
    private static final String  PRESS_PREFIX                 = "Press ";
    private static final String  WORD_GAME_PROMPT_SUFFIX      = " to play the Word game.";
    private static final String  NUMBER_GAME_PROMPT_SUFFIX    = " to play the Number game.";
    private static final String  LETTER_RUSH_PROMPT_SUFFIX    = " to play the Letter Rush game.";
    private static final String  QUIT_PROMPT_SUFFIX           = " to quit.";
    private static final long    SLEEP_DURATION_MS            = 100;
    private static final int     SYSTEM_EXIT                  = 0;
    private static       boolean waitingForConsoleInput       = true;

    /**
     * The main entry point for 3 games, Word Game, Number Game,
     * and Letter Rush. It delegates to runMainMenu to handle game
     * mode selection and execution.
     *
     * @param args command-line arguments (not used in this application)
     */
    public static void main(final String[] args)
    {
        runMainMenu(args);
    }

    /**
     * Displays the main menu in the console and prompts the user for input.
     * Sets the waitingForConsoleInput flag to true to indicate that the program
     * is ready to accept user input.
     */
    public static void ConsoleInput()
    {
        waitingForConsoleInput = true;
        System.out.println(MAIN_MENU_HEADER);
        System.out.println(WELCOME_MESSAGE);
        System.out.println(PRESS_PREFIX + WORD_GAME_MODE + WORD_GAME_PROMPT_SUFFIX);
        System.out.println(PRESS_PREFIX + NUMBER_GAME_MODE + NUMBER_GAME_PROMPT_SUFFIX);
        System.out.println(PRESS_PREFIX + LETTER_RUSH_GAME_MODE + LETTER_RUSH_PROMPT_SUFFIX);
        System.out.println(PRESS_PREFIX + QUIT_MODE + QUIT_PROMPT_SUFFIX);
        System.out.print(INPUT_PROMPT);
    }

    /*
     * Runs the main menu loop, handling user input and launching the selected game mode.
     * Initializes JavaFX for the Number game and manages resource cleanup.
     * This method uses a polling loop with Thread.sleep, which may not be optimal for responsiveness;
     * consider using a more event-driven approach in future iterations.
     *
     * @param args command-line arguments passed to the application
     */
    private static void runMainMenu(final String[] args)
    {
        final Validation validator;
        final Scanner sc;

        sc = new Scanner(System.in);
        validator = new Validation(RESOURCE_DIR);

        try(sc)
        {
            GameMenu.setMainArgs(args);
            new Thread(GameMenu::initializeJavaFX).start();
            ConsoleInput();
            while(true)
            {
                if(waitingForConsoleInput)
                {
                    final String input;
                    input = sc.nextLine().trim().toUpperCase();

                    if(validator.isValidInput(input))
                    {
                        switch(input)
                        {
                            case WORD_GAME_MODE:
                                System.out.println(STARTING_WORD_GAME_MESSAGE);
                                validator.startWordGame();
                                validator.handlePlayAgain(sc);
                                break;
                            case NUMBER_GAME_MODE:
                                System.out.println(STARTING_NUMBER_GAME_MESSAGE);
                                waitingForConsoleInput = false;
                                GameMenu.showNumberGameMenu();
                                break;
                            case LETTER_RUSH_GAME_MODE:
                                System.out.println(STARTING_LETTER_RUSH_MESSAGE);
                                LetterRush.launchGame();
                                break;
                            case QUIT_MODE:
                                System.out.println(QUITTING_MESSAGE);
                                System.exit(SYSTEM_EXIT);
                        }
                    }
                }
                else
                {
                    try
                    {
                        Thread.sleep(SLEEP_DURATION_MS);
                    }
                    catch(InterruptedException e)
                    {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
        finally
        {
            validator.close();
        }
    }
}