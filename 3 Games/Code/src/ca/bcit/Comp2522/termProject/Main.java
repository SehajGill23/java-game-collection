package ca.bcit.Comp2522.termProject;

import ca.bcit.Comp2522.termProject.Validate.Validation;

import java.util.Arrays;
import java.util.List;

public class Main
{
    public static void main(String[] args)
    {

        String resourceDir = "Resources";
        List<String> fileNames = Arrays.asList("a.txt",
                                               "b.txt",
                                               "z.txt");
        Validation validator = new Validation(resourceDir,
                                              fileNames);

        System.out.println("Welcome to the Geography Trivia Game!");
        System.out.println("Press W to play the Word game.");
        System.out.println("Press N to play the Number game.");
        System.out.println("Press M to play the Custom game.");
        System.out.println("Press Q to quit.");

        try
        {
            while(true)
            {
                char choice = validator.getValidInput();
                if(choice == 'Q')
                {
                    break;
                }
            }
        }
        finally
        {

            validator.close();
        }

    }
}