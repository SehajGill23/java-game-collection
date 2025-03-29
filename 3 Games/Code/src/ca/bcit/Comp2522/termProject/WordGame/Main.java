package ca.bcit.Comp2522.termProject.WordGame;
import  ca.bcit.Comp2522.termProject.NumberGame.MainMenu;

import java.util.Scanner;

/**
 * The Main class serves as the entry point for the Geography Trivia Game application.
 * It handles user input, game mode selection, and delegates validation and game logic to the Validation class.
 *
 * @author Sehaj Gill
 * @version 1.0
 */
public final class Main
{
    private static final String RESOURCE_DIR = "Resources";
    private static final String      WORD_GAME_MODE       = "W";
    private static final String      NUMBER_GAME_MODE     = "N";
    private static final String      CUSTOM_GAME_MODE     = "M";
    private static final String      QUIT_MODE            = "Q";

    public static void main(final String[] args)
    {
        final Validation validator = new Validation(RESOURCE_DIR);
        final Scanner    sc        = new Scanner(System.in);

        System.out.println("---------------MAIN MENU------------------");
        System.out.println("Welcome to the Geography Trivia Game!");
        System.out.println("Press " + WORD_GAME_MODE + " to play the Word game.");
        System.out.println("Press " + NUMBER_GAME_MODE + " to play the Number game.");
        System.out.println("Press " + CUSTOM_GAME_MODE   + " to play the Custom game.");
        System.out.println("Press "  + QUIT_MODE  + " to quit.");

        try
        {
            while(true)
            {
                System.out.print("\nEnter your Choice: ");
                final String input = sc.nextLine().trim().toUpperCase();

                if(validator.isValidInput(input))
                {
                    switch(input)
                    {
                        case WORD_GAME_MODE:
                            System.out.println("Starting Word Game...");
                            validator.startWordGame();
                            validator.handlePlayAgain(sc);
                            break;
                        case NUMBER_GAME_MODE:
                            System.out.println("Starting Number Game...");
                            final MainMenu mainMenu = new MainMenu();
                            break;
                        case CUSTOM_GAME_MODE :
                            System.out.println("Starting Custom Game... (Not yet implemented)");
                            break;
                        case QUIT_MODE:
                            System.out.println("Quiting the Game...");
                            System.exit(0);
                    }
                }
            }
        }
        finally
        {
            sc.close();
            validator.close();
        }
    }
}