package ca.bcit.Comp2522.termProject;

import ca.bcit.Comp2522.termProject.Validate.Validation;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The Main class serves as the entry point for the Geography Trivia Game application.
 * It initializes the game by dynamically loading data files from the Resources directory
 * and provides a menu for the user to select different game modes or quit the application.
 *
 * @author Sehaj Gill
 * @version 1.0
 */
public class Main
{
    public static void main(String[] args)
    {

        String       resourceDir = "Resources";
        List<String> fileNames   = getTextFilesInDirectory(resourceDir);

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

    /*
     * Retrieves a list of text file names from the specified Resources directory.
     * Only includes files matching the pattern [a-z].txt, excluding w.txt and x.txt,
     * to ensure only valid country data files are loaded.
     *
     * @param resourceDir the directory path where the data files are located
     * @return a sorted list of file names matching the pattern [a-z].txt (excluding w.txt and x.txt),
     *         or an empty list if the directory doesn't exist or contains no valid files
     */
    private static List<String> getTextFilesInDirectory(String resourceDir)
    {
        File res = new File(resourceDir);
        if(!res.exists() || !res.isDirectory())
        {
            System.out.println("Resources directory not found: " + resourceDir);
            return List.of();
        }

        // Get files that match [a-z].txt, excluding w.txt and x.txt
        return Arrays.stream(Objects.requireNonNull(res.listFiles((_, name) -> name.toLowerCase()
                                                                                   .matches("[a-z]\\.txt")
                                                                               && !name.equalsIgnoreCase("w.txt")
                                                                               && !name.equalsIgnoreCase("x.txt"))))
                     .map(File::getName)
                     .sorted()
                     .collect(Collectors.toList());
    }

}