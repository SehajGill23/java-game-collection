package ca.bcit.Comp2522.termProject;

import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class WordGame
{
    private static final int INITIAL_SCORE      = 0;
    private static final int INITIAL_LENGTH     = 0;
    private static final int QUESTIONS_PER_GAME = 10;
    private static final int RANDOM_QUESTION    = 3;
    private static final int FIRST_ATTEMPT      = 1;
    private static final int SECOND_ATTEMPT     = 2;

    private int score;
    private int firstAttempts;
    private int secondAttempts;
    private int incorrectAttempts;
    private int totalGamesPlayed;
    private int totalFirstAttempts;
    private int totalSecondAttempts;
    private int totalIncorrectAttempts;

    private final World   world;
    private final Random  random;
    private final Scanner scanner;
    private       Country country;


    public WordGame(String resourceDir,
                    List<String> fileNames)
    {
        world = new World(resourceDir,
                          fileNames);
        try
        {
            world.loadCountries();
        }
        catch(IllegalStateException e)
        {
            // Replaced logger with System.out.println (Issue 14)
            System.out.println(e.getMessage());
            throw new RuntimeException("Game cannot start due to data loading failure.");
        }
        score                  = INITIAL_SCORE;
        firstAttempts          = INITIAL_SCORE;
        secondAttempts         = INITIAL_SCORE;
        incorrectAttempts      = INITIAL_SCORE;
        totalGamesPlayed       = INITIAL_SCORE;
        totalFirstAttempts     = INITIAL_SCORE;
        totalSecondAttempts    = INITIAL_SCORE;
        totalIncorrectAttempts = INITIAL_SCORE;
        random                 = new Random();
        scanner                = new Scanner(System.in);
    }

    public int getScore()
    {
        return score;
    }

    public int getFirstAttempts()
    {
        return firstAttempts;
    }

    public int getSecondAttempts()
    {
        return secondAttempts;
    }

    public int getIncorrectAttempts()
    {
        return incorrectAttempts;
    }

    public int getTotalGamesPlayed()
    {
        return totalGamesPlayed;
    }

    public void playWordGame()
    {
        firstAttempts     = INITIAL_SCORE;
        secondAttempts    = INITIAL_SCORE;
        incorrectAttempts = INITIAL_SCORE;
        score             = INITIAL_SCORE;

        System.out.println("Welcome to the Geography Trivia Game!");
        System.out.println("You will be asked 10 random questions. Try to answer correctly!");

        String[] countryNames = world.getCountries().keySet().toArray(new String[0]);
        if(countryNames.length == INITIAL_LENGTH)
        {
            // Replaced logger with System.out.println (Issue 14)
            System.out.println("Error: No countries loaded. Please check the Resources directory and files.");
            return;
        }

        for(int i = INITIAL_LENGTH; i < QUESTIONS_PER_GAME; i++)
        {
            String randomCountryName = countryNames[random.nextInt(countryNames.length)];
            country = world.getCountries().get(randomCountryName);

            int questionType = random.nextInt(RANDOM_QUESTION);
            int result       = askQuestion(questionType);

            if(result == FIRST_ATTEMPT)
            {
                firstAttempts++;
                score += 2;
                System.out.println("Correct First Attempts:\n" + firstAttempts);
            }
            else if(result == SECOND_ATTEMPT)
            {
                secondAttempts++;
                score += 1;
                System.out.println("Correct Second Attempts:\n" + secondAttempts);
            }
            else
            {
                incorrectAttempts++;
                System.out.println("Incorrect after two attempts.\n");
            }
        }

        totalGamesPlayed++;
        totalFirstAttempts += firstAttempts;
        totalSecondAttempts += secondAttempts;
        totalIncorrectAttempts += incorrectAttempts;

        System.out.println("\nGame Over! Here are your stats for this game:");
        System.out.println("- " + totalGamesPlayed + " word game" + (totalGamesPlayed == 1 ? "" : "s") + " played");
        System.out.println("- " + firstAttempts + " correct answers on the first attempt");
        System.out.println("- " + secondAttempts + " correct answers on the second attempt");
        System.out.println("- " + incorrectAttempts + " incorrect answers on two attempts each");

        System.out.println("\nTotal stats across all games:");
        System.out.println("- " + totalGamesPlayed + " word game" + (totalGamesPlayed == 1 ? "" : "s") + " played");
        System.out.println("- " + totalFirstAttempts + " correct answers on the first attempt");
        System.out.println("- " + totalSecondAttempts + " correct answers on the second attempt");
        System.out.println("- " + totalIncorrectAttempts + " incorrect answers on two attempts each");
    }

    private int askQuestion(int questionType)
    {
        String correctAnswer = "";
        switch(questionType)
        {
            case 0:
                System.out.println("Which country has the capital city: " + country.getCapitalCityName() + "?");
                correctAnswer = country.getName();
                break;
            case 1:
                System.out.println("What is the capital of " + country.getName() + "?");
                correctAnswer = country.getCapitalCityName();
                break;
            case 2:
                System.out.println("Which country is known for this fact: " + country.getRandomFact() + "?");
                correctAnswer = country.getName();
                break;
        }
        return getUserAnswer(correctAnswer);
    }

    private int getUserAnswer(String correctAnswer)
    {
        for(int attempt = FIRST_ATTEMPT; attempt <= SECOND_ATTEMPT; attempt++)
        {
            System.out.print("Your answer: ");
            String userAnswer = scanner.nextLine().trim();

            if(userAnswer.isEmpty())
            {
                System.out.println("Input cannot be empty. Please try again.");
                attempt--;
                continue;
            }
            if(userAnswer.equalsIgnoreCase(correctAnswer))
            {
                System.out.println("CORRECT!");
                return attempt;
            }
            else
            {
                System.out.println("INCORRECT. Try again!");
            }
        }
        System.out.println("The correct answer was: " + correctAnswer);
        return 0;
    }

    public void close()
    {
        scanner.close();
    }
}