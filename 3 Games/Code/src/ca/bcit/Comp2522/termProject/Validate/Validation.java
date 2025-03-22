package ca.bcit.Comp2522.termProject.Validate;
import java.util.Scanner;

public class Validation
{
    private static final int MAX_INPUT_LENGTH = 1;
    private static final int FIRST_CHAR       = 0;

    public static char getValidInput()
    {
        String gameChoiceInput;
        char choice;
        final Scanner sc;

        sc = new Scanner(System.in);

        while(true)
        {
            gameChoiceInput = sc.nextLine().trim();

            if(gameChoiceInput.length() == MAX_INPUT_LENGTH)
            {
                choice = Character.toUpperCase(gameChoiceInput.charAt(FIRST_CHAR));

                switch(choice)
                {
                    case 'W':
                        System.out.println("Starting the Word game...");
                        // Call your Word game function here
                        break;
                    case 'N':
                        System.out.println("Starting the Number game...");
                        // Call your Number game function here
                        break;
                    case 'M':
                        System.out.println("Starting the <your game's name> game...");
                        // Call your custom game function here
                        break;
                    case 'Q':
                        System.out.println("Quitting the game. Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid input. Please enter W, N, M, or Q.");
                }
            }
            else
            {
                System.out.println("Invalid input. Please enter a single character (W, N, M, or Q).");
            }
        }
    }
}


