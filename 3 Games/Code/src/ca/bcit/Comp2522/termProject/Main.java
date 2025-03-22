package ca.bcit.Comp2522.termProject;
import ca.bcit.Comp2522.termProject.Validate.Validation;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main
{
    public static void main(String[] args)
    {
        final Path         countryPath;
        final List<String>  countries;
        final List <String> answers;
        countryPath = Paths.get("res", "a.txt", "b.txt");

        System.out.println("Press W to play the Word game.");
        System.out.println("Press N to play the Number game.");
        System.out.println("Press M to play the <your game's name> game.");
        System.out.println("Press Q to quit.");
        System.out.print("Enter your choice: ");
        Validation.getValidInput();
    }
}
