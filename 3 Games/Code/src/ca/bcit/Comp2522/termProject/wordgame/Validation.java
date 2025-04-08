package ca.bcit.Comp2522.termProject.wordgame;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The {@code Validation} class arranges the gameplay, handles user input validation, and manages
 * score tracking for the Geography Trivia Game. It takes charge of ensuring that user-provided game
 * mode selections are valid, initiates the Word Game when requested, and manages the flow for playing
 * multiple rounds. Furthermore, it is responsible for calculating, displaying, and persisting game
 * scores, as well as determining and reporting high scores achieved by the player. This class acts as
 * the central control point for the game's interactive elements and performance recording.
 * <p>
 * Upon creation, the {@code Validation} class initializes the {@link WordGame} instance by scanning
 * the specified resource directory for text files that adhere to the naming convention of single
 * lowercase letters followed by ".txt" (excluding 'w.txt' and 'x.txt'). These files are presumed to
 * contain the country data used by the trivia game. The identified file names are then used to set up
 * the game environment within the {@code WordGame} class.
 * </p>
 * <p>
 * The class provides methods to validate user input for selecting the game mode. The
 * {@link #isValidInput(String)} method checks if the user's input is a single character and if that
 * character corresponds to one of the valid game modes (Word Game, Number Game - currently not
 * implemented, Custom Game - currently not implemented, or Quit). It provides feedback to the user if
 * their input is not recognized.
 * </p>
 * <p>
 * When the user chooses to play the Word Game, the {@link #startWordGame()} method is invoked. This
 * method simply calls the {@code playWordGame()} method of the associated {@link WordGame} instance,
 * thereby beginning a new round of the trivia game.
 * </p>
 * <p>
 * After a Word Game session concludes, the {@link #handlePlayAgain(Scanner)} method prompts the user
 * to decide whether they wish to play another round. It accepts "Yes" or "No" (case-insensitive) as
 * valid responses. If the user indicates they want to play again, a new Word Game is started. If they
 * choose not to continue, the game proceeds to save the current score and compare it against the
 * existing high scores. Any other input from the user results in an error message, and the prompt is
 * repeated until a valid response is given.
 * </p>
 * <p>
 * The {@link #saveAndPrintHighScore()} method is responsible for calculating the score from the most
 * recently played Word Game, along with the total number of games played. It then displays these raw
 * statistics and the current average score achieved by the player. This method also retrieves the
 * highest average score recorded so far and compares it with the current average. Feedback is provided
 * to the user indicating whether they have achieved a new high score or matched the existing one.
 * Finally, the details of the current game's score are saved to the "score.txt" file using the
 * {@link Score#appendScoreToFile(Score, String)} method. Any {@link IOException} that occurs during
 * the file saving process is caught and an error message is displayed.
 * </p>
 * <p>
 * To determine the highest score, the {@link #getHighestScore()} method reads all the previously
 * recorded scores from the "score.txt" file using {@link Score#readScoresFromFile(String)}. It then
 * processes this list of scores to find the one with the highest average score. If the score file
 * does not exist or if an error occurs during the reading process, this method returns a default
 * {@link Score} object to prevent further issues. The highest score is determined by comparing the
 * average score of each {@link Score} object in the list.
 * </p>
 * <p>
 * The {@link #close()} method ensures that any resources held by the {@code Validation} instance,
 * specifically those managed by the {@link WordGame} instance, are properly released when the game
 * session ends or when the program is shutting down.
 * </p>
 * <p>
 * The static utility method {@link #getTextFilesInDirectory(String)} is used during the initialization
 * of the {@code Validation} class to identify the country data files. It scans the provided resource
 * directory and returns a sorted list of file names that match the pattern of a single lowercase
 * letter followed by ".txt", while explicitly excluding "w.txt" and "x.txt". This ensures that only
 * the intended data files are loaded for the Word Game. If the specified directory is not found or
 * contains no valid files, an empty list is returned, and an error message is printed to the console.
 * </p>
 *
 * @author Sehaj Gill
 * @version 1.0
 */
class Validation
{
    private static final int         INITIAL_SCORE             = 0;
    private static final int         INITIAL_FIRST_ATTEMPT     = 0;
    private static final int         INITIAL_SECOND_ATTEMPT    = 0;
    private static final int         INITIAL_INCORRECT_ATTEMPT = 0;
    private static final int         INITIAL_GAME_PlAY         = 1;
    private static final String      SCORE_FILE_PATH           = "Resources/score.txt";
    private static final String      WORD_GAME_MODE            = "W";
    private static final String      NUMBER_GAME_MODE          = "N";
    private static final String      CUSTOM_GAME_MODE          = "M";
    private static final String      QUIT_MODE                 = "Q";
    private static final Set<String> VALID_GAME_MODES          = Set.of(WORD_GAME_MODE,
                                                                        NUMBER_GAME_MODE,
                                                                        CUSTOM_GAME_MODE,
                                                                        QUIT_MODE);
    private static final Set<String> YES_RESPONSES             = Set.of("YES",
                                                                        "Y");
    private static final Set<String> NO_RESPONSES              = Set.of("NO",
                                                                        "N");
    private static final String      INVALID_MODE_MESSAGE      = "Invalid input. Please enter " +
                                                                 WORD_GAME_MODE + ", " + NUMBER_GAME_MODE + ", "
                                                                 + CUSTOM_GAME_MODE + ", or " + QUIT_MODE + ".";
    private final        WordGame    game;

    /*
     * Constructs a new Validation instance, initializing the WordGame with files from the resource directory.
     *
     * @param resourceDir the directory containing the country data files
     */
    Validation(final String resourceDir)
    {
        final List<String> fileNames;
        fileNames = getTextFilesInDirectory(resourceDir);
        this.game = new WordGame(resourceDir,
                                 fileNames);
    }

    /*
     * Validates the user's game mode input.
     *
     * @param input the raw input from the user (trimmed and uppercased)
     * @return true if the input is valid, false otherwise
     */
    boolean isValidInput(final String input)
    {
        if(input.length() != 1)
        {
            System.out.println("Invalid input. Please enter a single character.");
            return false;
        }
        if(!VALID_GAME_MODES.contains(input))
        {
            System.out.println(INVALID_MODE_MESSAGE);
            return false;
        }
        return true;
    }


    /**]
     * Starts the Word Game by invoking the playWordGame method on the WordGame instance.
     */
    void startWordGame()
    {
        game.playWordGame();
    }

    /*
     * Prompts the user to decide if they want to play another Word Game. If Yes, starts a new Word Game;
     * if No, returns to the main menu.
     *
     * @param sc the Scanner instance to read user input
     */
    void handlePlayAgain(final Scanner sc)
    {
        while(true)
        {
            System.out.print("Do you want to play again? (Yes/No): ");
            final String response;
            response = sc.nextLine().trim().toUpperCase();

            if(YES_RESPONSES.contains(response))
            {
                System.out.println("Starting Word Game...");
                startWordGame();
                Main.ConsoleInput();
                return;
            }
            else if(NO_RESPONSES.contains(response))
            {
                saveAndPrintHighScore();
                Main.ConsoleInput();
                return;
            }
            else
            {
                System.out.println("Invalid input. Please enter Yes or No.");
            }
        }
    }

    /*
     * Calculates, displays, and compares the current game score with the highest score. Prints the raw score,
     * games played, current average score, and highest average score, and provides feedback on whether a new
     * high score was achieved. Saves the current score to the score file.
     */
    private void saveAndPrintHighScore()
    {
        final int rawScore;
        final int gamesPlayed;
        final Score currentScore;
        final Score highestScore;

        rawScore = game.getScore();
        gamesPlayed = game.getTotalGamesPlayed();

        System.out.println("\nRaw score from game: " + rawScore);
        System.out.println("Games played: " + gamesPlayed);
        currentScore = new Score(rawScore,
                                             game.getFirstAttempts(),
                                             game.getSecondAttempts(),
                                             game.getIncorrectAttempts(),
                                             gamesPlayed);
        highestScore = getHighestScore();
        System.out.println("\nCurrent Average Score: " + currentScore.getAverageScore());
        System.out.println("Highest Average Score: " + highestScore.getAverageScore());

        if(currentScore.getAverageScore() > highestScore.getAverageScore())
        {
            System.out.println("Congratulations! You've set a new high score!");
        }
        else if(currentScore.getAverageScore() == highestScore.getAverageScore())
        {
            System.out.println("You have matched the high score!");
        }
        else
        {
            System.out.println("\nNo new high score this time.");
        }

        try
        {
            Score.appendScoreToFile(currentScore,
                                    SCORE_FILE_PATH);
        }
        catch(final
        IOException e)
        {
            System.out.println("Error: Unable to save score to " + SCORE_FILE_PATH + ": " + e.getMessage());
        }
    }

    /**
     * Retrieves the highest score from the score file based on the average score. If the file does not exist
     * or an error occurs, returns a default Score object.
     *
     * @return the Score object with the highest average score, or a default Score if none are found
     */
    private Score getHighestScore()
    {
        final List<Score> scoreList;
        try
        {
            scoreList = Score.readScoresFromFile(SCORE_FILE_PATH);
        }
        catch(final IOException e)
        {
            System.out.println("Error reading score file: " + e.getMessage());
            return new Score( INITIAL_SCORE ,
                             INITIAL_FIRST_ATTEMPT,
                             INITIAL_SECOND_ATTEMPT,
                             INITIAL_INCORRECT_ATTEMPT,
                             INITIAL_GAME_PlAY);
        }

        return scoreList.stream().max(Comparator.comparingDouble
                                                        (Score::getAverageScore))
                        .orElse(new Score( INITIAL_SCORE ,
                                           INITIAL_FIRST_ATTEMPT,
                                           INITIAL_SECOND_ATTEMPT,
                                           INITIAL_INCORRECT_ATTEMPT,
                                           INITIAL_GAME_PlAY));
    }

    /*
     * Closes the resources used by the Validation instance, including the WordGame.
     */
    void close()
    {
        game.close();
    }

    /*
     * Retrieves a list of text file names from the specified Resources directory.
     * Only includes files matching the pattern [a-z].txt, excluding w.txt and x.txt,
     * to ensure only valid country data files are loaded.
     *
     * @param resourceDir the directory path where the data files are located
     * @return a sorted list of file names matching the pattern [a-z].txt (excluding w.txt and x.txt),
     * or an empty list if the directory doesn't exist or contains no valid files
     */
    private static List<String> getTextFilesInDirectory(final String resourceDir)
    {
        final File res;
        res = new File(resourceDir);
        if(!res.exists() || !res.isDirectory())
        {
            System.out.println("Resources directory not found: " + resourceDir);
            return List.of();
        }

        return Arrays.stream(Objects.requireNonNull(res.listFiles((_, name) -> name.toLowerCase()
                                                                              .matches("[a-z]\\.txt") &&
                                                                          !name.equalsIgnoreCase("w.txt") &&
                                                                          !name.equalsIgnoreCase("x.txt"))))
                     .map(File::getName)
                     .sorted()
                     .collect(Collectors.toList());
    }
}