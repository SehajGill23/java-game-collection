package ca.bcit.Comp2522.termProject;
import ca.bcit.Comp2522.termProject.Validate.Validation;

public class Main
{
    public static void main(String[] args)
    {

        System.out.println("Press W to play the Word game.");
        System.out.println("Press N to play the Number game.");
        System.out.println("Press M to play the <your game's name> game.");
        System.out.println("Press Q to quit.");
        System.out.print("Enter your choice: ");
        Validation.getValidInput();
    }
}
