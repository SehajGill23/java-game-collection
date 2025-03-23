//package ca.bcit.Comp2522.termProject;
//
//import ca.bcit.Comp2522.termProject.Validate.Validation;
//
//public class Main
//{
//    public static void main(String[] args)
//    {
//        Validation validator = new Validation();
//
//        System.out.println("Press W to play the Word game.");
//        System.out.println("Press N to play the Number game.");
//        System.out.println("Press M to play the Custom game.");
//        System.out.println("Press Q to quit.");
//        System.out.print("Enter your choice: ");
//
//        while(true)
//        {
//            char choice = validator.getValidInput();
//            if(choice == 'Q')
//            {
//                break;
//            }
//            if(choice == 'W' && !validator.playAgain())
//            {
//                break;
//            }
//            System.out.println("\nPress W, N, M, or Q to continue:");
//        }
//    }

package ca.bcit.Comp2522.termProject;

import ca.bcit.Comp2522.termProject.Validate.Validation;
import java.util.Arrays;
import java.util.List;

public class Main
{
    public static void main(String[] args)
    {
        // Parameterized resource directory and file names (Issue 12)
        String resourceDir = "Resources";
        List<String> fileNames = Arrays.asList("a.txt", "b.txt", "z.txt");
        Validation   validator = new Validation(resourceDir, fileNames); // Pass parameters (Issue 12)

        System.out.println("Welcome to the Geography Trivia Game!");
        System.out.println("Press W to play the Word game.");
        System.out.println("Press N to play the Number game.");
        System.out.println("Press M to play the Custom game.");
        System.out.println("Press Q to quit.");

        try {
            while (true) {
                char choice = validator.getValidInput();
                if (choice == 'Q') {
                    break;
                }
            }
        } finally {
            // Ensure resources are released
            validator.close();
        }

    }
}