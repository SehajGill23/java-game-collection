package ca.bcit.comp2522.termproject.wordgame;

import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * The {@code WordGame} class implements the word-based trivia mode for the Geography Trivia Game.
 * It manages the flow of a single game session, presenting the player with a series of randomly
 * selected questions about different countries. The questions can be about a country's capital city,
 * identifying a country given its capital, or identifying a country based on a provided fact.
 * <p>
 * The class keeps track of the player's performance throughout the game, including the score, the
 * number of questions answered correctly on the first attempt, the number answered correctly on the
 * second attempt, and the number of questions answered incorrectly after both attempts. At the end
 * of each game, it displays a summary of the player's performance in that session, as well as a
 * cumulative summary of their performance across all Word Game sessions played so far.
 * </p>
 * <p>
 * The game poses a fixed number of questions per session (currently 10), chosen randomly from the
 * loaded country data. For each question, the player is given up to two attempts to provide the
 * correct answer. Points are awarded for correct answers, with more points given for answering
 * correctly on the first attempt. Incorrect answers after two tries result in no points for that
 * question, and the correct answer is revealed to the player.
 * </p>
 * <p>
 * The {@code WordGame} class interacts with the {@link World} class to access the country data and
 * uses a {@link Random} instance to select countries and question types randomly. It also uses a
 * {@link Scanner} to read the player's input from the console. The game continues until the निर्धारित
 * number of questions have been asked, after which the game statistics are displayed.
 * </p>
 * <p>
 * The class maintains both the statistics for the current game session and the overall statistics
 * across all games played. This allows the player to see their immediate performance as well as their
 * long-term progress.
 * </p>
 *
 * @author Sehaj Gill
 * @version 1.0
 */
class WordGame
{
    private static final int    INITIAL_SCORE                    = 0;
    private static final int    COUNTRY_LENGTH                   = 0;
    private static final int    QUESTIONS_PER_GAME               = 10;
    private static final int    RANDOM_QUESTION_BOUND            = 3;
    private static final int    FIRST_ATTEMPT                    = 1;
    private static final int    SECOND_ATTEMPT                   = 2;
    private static final int    FIRST_ATTEMPT_SCORE              = 2;
    private static final int    SECOND_ATTEMPT_SCORE             = 1;
    private static final int    GAMES_PLAYED_INCREMENT           = 1;
    private static final int    QUESTION_TYPE_CAPITAL_TO_COUNTRY = 0;
    private static final int    QUESTION_TYPE_COUNTRY_TO_CAPITAL = 1;
    private static final int    QUESTION_TYPE_COUNTRY_FACT       = 2;
    private static final String NEW_LINE_CHARACTER               = "%n";
    private static final String START_NEW_GAME_MESSAGE           = "Starting new game with score: ";
    private static final String WELCOME_MESSAGE                  = "\nWelcome to the Geography Trivia Game!";
    private static final String CORRECT_FIRST_ATTEMPT_MESSAGE    = "Correct First Attempts:\n";
    private static final String CORRECT_SECOND_ATTEMPT_MESSAGE   = "Correct Second Attempts:\n";
    private static final String INCORRECT_MESSAGE                = "Incorrect after two attempts.\n";
    private static final String GAME_OVER_MESSAGE                = "\nGame Over! Here are your stats for this game:";
    private static final String WORD_GAME_PLAYED_STAT            = "- %d word game%s played";
    private static final String FIRST_ATTEMPT_STAT               = "- %d correct answers on the first attempt";
    private static final String SECOND_ATTEMPT_STAT              = "- %d correct answers on the second attempt";
    private static final String INCORRECT_ATTEMPT_STAT           = "- %d incorrect answers on two attempts each";
    private static final String TOTAL_STATS_MESSAGE              = "\nTotal stats across all games:";
    private static final String CAPITAL_TO_COUNTRY_PROMPT        = "Which country has the capital city: %s?";
    private static final String COUNTRY_TO_CAPITAL_PROMPT        = "What is the capital of %s?";
    private static final String FACT_TO_COUNTRY_PROMPT           = "Which country is known for this fact: %s?";
    private static final String YOUR_ANSWER_PROMPT               = "Your answer: ";
    private static final String EMPTY_INPUT_ERROR                = "Input cannot be empty. Please try again.";
    private static final String CORRECT_ANSWER_MESSAGE           = "CORRECT!";
    private static final String INCORRECT_ANSWER_MESSAGE         = "INCORRECT. Try again!";
    private static final String SHOW_CORRECT_ANSWER              = "The correct answer was: %s";
    private static final String INSTRUCTIONS_MESSAGE             = "You will be asked 10 random questions. " +
                                                                   "Try to answer" + " correctly!\n";
    private static final String NO_COUNTRIES_ERROR               = "Error: No countries loaded. Please check the " +
                                                                   "Resources " + "directory and files.";

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
     * Constructs a new {@code WordGame} instance, initializing the game world and resources.
     * Loads country data from the specified resource directory and file names.
     *
     * @param resourceDir the directory containing the country data files
     * @param fileNames   the list of file names to load country data from
     * @throws RuntimeException if country data cannot be loaded
     */
    WordGame(final String resourceDir,
             final List<String> fileNames)
    {
        world = new World(resourceDir,
                          fileNames);
        try
        {
            world.loadCountries();
        }
        catch (final IllegalStateException e)
        {
            System.out.println(e.getMessage());
            throw new RuntimeException("Game cannot start due to data loading failure.");
        }
        score = INITIAL_SCORE;
        firstAttempts = INITIAL_SCORE;
        secondAttempts = INITIAL_SCORE;
        incorrectAttempts = INITIAL_SCORE;
        totalGamesPlayed = INITIAL_SCORE;
        totalFirstAttempts = INITIAL_SCORE;
        totalSecondAttempts = INITIAL_SCORE;
        totalIncorrectAttempts = INITIAL_SCORE;
        random = new Random();
        scanner = new Scanner(System.in);
    }

    /**
     * Returns the player's score for the current game session.
     *
     * @return the current game score
     */
    public int getScore()
    {
        return score;
    }

    /**
     * Returns the number of questions answered correctly on the first attempt in the current game session.
     *
     * @return the number of first attempts
     */
    int getFirstAttempts()
    {
        return firstAttempts;
    }

    /**
     * Returns the number of questions answered correctly on the second attempt in the current game session.
     *
     * @return the number of second attempts
     */
    int getSecondAttempts()
    {
        return secondAttempts;
    }

    /**
     * Returns the number of questions answered incorrectly after two attempts in the current game session.
     *
     * @return the number of incorrect attempts
     */
    int getIncorrectAttempts()
    {
        return incorrectAttempts;
    }

    /**
     * Returns the total number of word games played across all sessions.
     *
     * @return the total number of games played
     */
    int getTotalGamesPlayed()
    {
        return totalGamesPlayed;
    }

    /**
     * Starts a new word game session, asking the player a set number of random questions about countries.
     * Tracks the player's score and attempts for the current game and updates the overall game statistics.
     * If no countries are loaded, the game will not proceed and an error message will be displayed.
     */
    void playWordGame()
    {
        firstAttempts = INITIAL_SCORE;
        secondAttempts = INITIAL_SCORE;
        incorrectAttempts = INITIAL_SCORE;
        score = INITIAL_SCORE;
        final String[] countryNames;
        String randomCountryName;
        int questionType;
        int result;

        System.out.println(START_NEW_GAME_MESSAGE + score);
        System.out.println(WELCOME_MESSAGE);
        System.out.println(INSTRUCTIONS_MESSAGE);

        countryNames = world.getCountries().keySet().toArray(new String[0]);

        if (countryNames.length == COUNTRY_LENGTH)
        {
            System.out.println(NO_COUNTRIES_ERROR);
            return;
        }

        for (int i = COUNTRY_LENGTH; i < QUESTIONS_PER_GAME; i++)
        {
            randomCountryName = countryNames[random.nextInt(countryNames.length)];
            country = world.getCountries().get(randomCountryName);

            questionType = random.nextInt(RANDOM_QUESTION_BOUND);
            result = askQuestion(questionType);

            if (result == FIRST_ATTEMPT)
            {
                firstAttempts++;
                score += FIRST_ATTEMPT_SCORE;
                System.out.println(CORRECT_FIRST_ATTEMPT_MESSAGE + firstAttempts);
            }
            else if (result == SECOND_ATTEMPT)
            {
                secondAttempts++;
                score += SECOND_ATTEMPT_SCORE;
                System.out.println(CORRECT_SECOND_ATTEMPT_MESSAGE + secondAttempts);
            }
            else
            {
                incorrectAttempts++;
                System.out.println(INCORRECT_MESSAGE);
            }
        }

        totalGamesPlayed += GAMES_PLAYED_INCREMENT;
        totalFirstAttempts += firstAttempts;
        totalSecondAttempts += secondAttempts;
        totalIncorrectAttempts += incorrectAttempts;

        System.out.println(GAME_OVER_MESSAGE);
        System.out.printf((WORD_GAME_PLAYED_STAT) + NEW_LINE_CHARACTER, totalGamesPlayed,
                          (totalGamesPlayed == GAMES_PLAYED_INCREMENT ? "" : "s"));
        System.out.printf((FIRST_ATTEMPT_STAT) + NEW_LINE_CHARACTER, firstAttempts);
        System.out.printf((SECOND_ATTEMPT_STAT) + NEW_LINE_CHARACTER, secondAttempts);
        System.out.printf((INCORRECT_ATTEMPT_STAT) + NEW_LINE_CHARACTER, incorrectAttempts);

        System.out.println(TOTAL_STATS_MESSAGE);
        System.out.printf((WORD_GAME_PLAYED_STAT) + NEW_LINE_CHARACTER, totalGamesPlayed,
                          (totalGamesPlayed == GAMES_PLAYED_INCREMENT ? "" : "s"));
        System.out.printf((FIRST_ATTEMPT_STAT) + NEW_LINE_CHARACTER, totalFirstAttempts);
        System.out.printf((SECOND_ATTEMPT_STAT) + NEW_LINE_CHARACTER, totalSecondAttempts);
        System.out.printf((INCORRECT_ATTEMPT_STAT) + NEW_LINE_CHARACTER, totalIncorrectAttempts);
    }

    /**
     * Asks a question about the current country based on the specified question type. The question can be
     * about the country's capital, the country given the capital, or the country given a fact.
     *
     * @param questionType the type of question to ask (0: capital to country, 1: country to capital,
     * 2: fact to country)
     * @return the result of the user's answer (1 for first attempt, 2 for second attempt, 0 for incorrect)
     */
    private int askQuestion(final int questionType)
    {
        String correctAnswer = "";
        switch (questionType)
        {
            case QUESTION_TYPE_CAPITAL_TO_COUNTRY:
                System.out.printf((CAPITAL_TO_COUNTRY_PROMPT) + NEW_LINE_CHARACTER, country.getCapitalCityName());
                correctAnswer = country.getName();
                break;
            case QUESTION_TYPE_COUNTRY_TO_CAPITAL:
                System.out.printf((COUNTRY_TO_CAPITAL_PROMPT) + NEW_LINE_CHARACTER, country.getName());
                correctAnswer = country.getCapitalCityName();
                break;
            case QUESTION_TYPE_COUNTRY_FACT:
                System.out.printf((FACT_TO_COUNTRY_PROMPT) + NEW_LINE_CHARACTER, country.getRandomFact());
                correctAnswer = country.getName();
                break;
            default:
                break;
        }
        return getUserAnswer(correctAnswer);
    }

    /**
     * Prompts the user for an answer and checks it against the correct answer. Allows up to two attempts
     * to answer correctly.
     *
     * @param correctAnswer the correct answer to the question
     * @return 1 if correct on the first attempt, 2 if correct on the second attempt, 0 if incorrect
     * after two attempts
     */
    private int getUserAnswer(final String correctAnswer)
    {
        String userAnswer;
        int attempt;

        for (attempt = FIRST_ATTEMPT; attempt <= SECOND_ATTEMPT; attempt++)
        {
            System.out.print(YOUR_ANSWER_PROMPT);
            userAnswer = scanner.nextLine().trim();

            if (userAnswer.isEmpty())
            {
                System.out.println(EMPTY_INPUT_ERROR);
                attempt--;
                continue;
            }
            if (userAnswer.equalsIgnoreCase(correctAnswer))
            {
                System.out.println(CORRECT_ANSWER_MESSAGE);
                return attempt;
            }
            else
            {
                System.out.println(INCORRECT_ANSWER_MESSAGE);
            }
        }
        System.out.printf((SHOW_CORRECT_ANSWER) + NEW_LINE_CHARACTER, correctAnswer);
        return 0;
    }

    /**
     * Closes the resources used by the {@code WordGame} instance.
     */
    void close()
    {
        scanner.close();
    }
}