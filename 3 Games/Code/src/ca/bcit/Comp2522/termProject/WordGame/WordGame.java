package ca.bcit.Comp2522.termProject.WordGame;

import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * The WordGame class implements the word-based game mode for the Geography Trivia Game.
 * It asks the player a series of questions about countries, tracks their score and attempts,
 * and provides game statistics at the end of each game.
 *
 * @author Sehaj Gill
 * @version 1.0
 */
public class WordGame
{
    private static final int INITIAL_SCORE      = 0;
    private static final int COUNTRY_LENGTH     = 0;
    private static final int QUESTIONS_PER_GAME = 10;
    private static final int RANDOM_QUESTION    = 3;
    private static final int FIRST_ATTEMPT      = 1;
    private static final int SECOND_ATTEMPT     = 2;
    private static final int FIRST_ATTEMPT_SCORE = 2;
    private static final int SECOND_ATTEMPT_SCORE = 1;
    private static final int GAMES_PLAYED = 1;

    private int  score;
    private int firstAttempts;
    private int secondAttempts;
    private int incorrectAttempts;
    private int totalGamesPlayed;
    private int totalFirstAttempts;
    private int totalSecondAttempts;
    private int totalIncorrectAttempts;

    private final World  world;
    private final Random random;
    private final Scanner scanner;
    private       Country country;


    /**
     * Constructs a new WordGame instance, initializing the game world and resources.
     * Loads country data from the specified resource directory and file names.
     *
     * @param resourceDir the directory containing the country data files
     * @param fileNames   the list of file names to load country data from
     * @throws RuntimeException if country data cannot be loaded
     */
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

    /**
     * Returns the player's score for the current game.
     *
     * @return the current game score
     */
    public int getScore()
    {
        return score;
    }

    /*
     * Returns the number of questions answered correctly on the first attempt in the current game.
     *
     * @return the number of first attempts
     */
    int getFirstAttempts()
    {
        return firstAttempts;
    }

    /*
     * Returns the number of questions answered correctly on the second attempt in the current game.
     *
     * @return the number of second attempts
     */
    int getSecondAttempts()
    {
        return secondAttempts;
    }

    /*
     * Returns the number of questions answered incorrectly after two attempts in the current game.
     *
     * @return the number of incorrect attempts
     */
    int getIncorrectAttempts()
    {
        return incorrectAttempts;
    }

    /*
     * Returns the total number of word games played across all sessions.
     *
     * @return the total number of games played
     */
    int getTotalGamesPlayed()
    {
        return totalGamesPlayed;
    }

    /*
     * Starts a new word game session, asking the player 10 random questions about countries.
     * Tracks the player's score, attempts, and provides game statistics at the end.
     * If no countries are loaded, the game will not proceed and an error message will be displayed.
     */
    void playWordGame()
    {
        firstAttempts     = INITIAL_SCORE;
        secondAttempts    = INITIAL_SCORE;
        incorrectAttempts = INITIAL_SCORE;
        score             = INITIAL_SCORE;

        System.out.println("Starting new game with score: " + score);

        System.out.println("Welcome to the Geography Trivia Game!");
        System.out.println("You will be asked 10 random questions. Try to answer correctly!");

        String[] countryNames = world.getCountries().keySet().toArray(new String[0]);
        if(countryNames.length == COUNTRY_LENGTH)
        {

            System.out.println("Error: No countries loaded. Please check the Resources directory and files.");
            return;
        }

        for(int i = COUNTRY_LENGTH; i < QUESTIONS_PER_GAME; i++)
        {
            String randomCountryName = countryNames[random.nextInt(countryNames.length)];
            country = world.getCountries().get(randomCountryName);

            int questionType = random.nextInt(RANDOM_QUESTION);
            int result       = askQuestion(questionType);

            if(result == FIRST_ATTEMPT)
            {
                firstAttempts++;
                score += FIRST_ATTEMPT_SCORE;
                System.out.println("Correct First Attempts:\n" + firstAttempts);
            }
            else if(result == SECOND_ATTEMPT)
            {
                secondAttempts++;
                score += SECOND_ATTEMPT_SCORE;
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
        System.out.println("- " + totalGamesPlayed + " word game" +
                           (totalGamesPlayed == GAMES_PLAYED ? "" : "s") + " played");
        System.out.println("- " + firstAttempts + " correct answers on the first attempt");
        System.out.println("- " + secondAttempts + " correct answers on the second attempt");
        System.out.println("- " + incorrectAttempts + " incorrect answers on two attempts each");

        System.out.println("\nTotal stats across all games:");
        System.out.println("- " + totalGamesPlayed + " word game" +
                           (totalGamesPlayed == GAMES_PLAYED ? "" : "s") + " played");
        System.out.println("- " + totalFirstAttempts + " correct answers on the first attempt");
        System.out.println("- " + totalSecondAttempts + " correct answers on the second attempt");
        System.out.println("- " + totalIncorrectAttempts + " incorrect answers on two attempts each");
    }

    /*
     * Asks a random question about the current country based on the question type.
     * The question can be about the country's capital, the country given the capital,
     * or the country given a fact.
     *
     * @param questionType the type of question to ask (0: capital to country, 1: country to capital, 2: fact to country)
     * @return the result of the user's answer (1 for first attempt, 2 for second attempt, 0 for incorrect)
     */
    private int askQuestion(int questionType)
    {
        String correctAnswer = switch(questionType)
        {
            case 0 ->
            {
                System.out.println("Which country has the capital city: " + country.getCapitalCityName() + "?");
                yield country.getName();
            }
            case 1 ->
            {
                System.out.println("What is the capital of " + country.getName() + "?");
                yield country.getCapitalCityName();
            }
            case 2 ->
            {
                System.out.println("Which country is known for this fact: " + country.getRandomFact() + "?");
                yield country.getName();
            }
            default -> "";
        };
        return getUserAnswer(correctAnswer);
    }

    /*
     * Prompts the user for an answer and checks it against the correct answer.
     * Allows up to two attempts to answer correctly.
     *
     * @param correctAnswer the correct answer to the question
     * @return 1 if correct on the first attempt, 2 if correct on the second attempt, 0 if incorrect after two attempts
     */
    private int getUserAnswer(String correctAnswer)
    {
        String userAnswer;
        for(int attempt = FIRST_ATTEMPT; attempt <= SECOND_ATTEMPT; attempt++)
        {
            System.out.print("Your answer: ");
            userAnswer = scanner.nextLine().trim();

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

    /*
     * Closes the resources used by the WordGame instance.
     */
    void close()
    {
        scanner.close();
    }
}