package ca.bcit.Comp2522.termProject.WordGame;

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
 * The Validation class manages game logic, validation, and score tracking for the Geography Trivia Game.
 * It handles input validation, game execution, and resource initialization.
 *
 * @author Sehaj Gill
 * @version 1.0
 */
class Validation
{
    private static final String      SCORE_FILE_PATH      = "Resources/score.txt";
    private static final String      WORD_GAME_MODE       = "W";
    private static final String      NUMBER_GAME_MODE     = "N";
    private static final String      CUSTOM_GAME_MODE     = "M";
    private static final String      QUIT_MODE            = "Q";
    private static final Set<String> VALID_GAME_MODES     = Set.of(WORD_GAME_MODE,
                                                                   NUMBER_GAME_MODE,
                                                                   CUSTOM_GAME_MODE,
                                                                   QUIT_MODE);
    private static final Set<String> YES_RESPONSES        = Set.of("YES",
                                                                   "Y");
    private static final Set<String> NO_RESPONSES         = Set.of("NO",
                                                                   "N");
    private static final String      INVALID_MODE_MESSAGE = "Invalid input. Please enter " + WORD_GAME_MODE +
                                                            ", " + NUMBER_GAME_MODE + ", " + CUSTOM_GAME_MODE +
                                                            ", or " + QUIT_MODE + ".";

    private final WordGame game;

    /*
     * Constructs a new Validation instance, initializing the WordGame with files from the resource directory.
     *
     * @param resourceDir the directory containing the country data files
     */
    Validation(final String resourceDir)
    {
        final List<String> fileNames = getTextFilesInDirectory(resourceDir);
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

    /*
     * Starts the Word Game by invoking the playWordGame method on the WordGame instance.
     */
    void startWordGame()
    {
        game.playWordGame();
    }


    /*
     * Prompts the user to decide if they want to play another Word Game.
     * If Yes, starts a new Word Game; if No, returns to the main menu.
     *
     * @param sc the Scanner instance to read user input
     */
    void handlePlayAgain(final Scanner sc)
    {
        while(true)
        {
            System.out.print("Do you want to play again? (Yes/No): ");
            final String response = sc.nextLine().trim().toUpperCase();
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
     * Calculates, displays, and compares the current game score with the highest score.
     * Prints the raw score, games played, current average score, and highest average score,
     * and provides feedback on whether a new high score was achieved.
     * Saves the current score to the score file.
     */
    private void saveAndPrintHighScore()
    {
        final int rawScore    = game.getScore();
        final int gamesPlayed = game.getTotalGamesPlayed();

        System.out.println("\nRaw score from game: " + rawScore);
        System.out.println("Games played: " + gamesPlayed);
        final Score currentScore = new Score(rawScore,
                                             game.getFirstAttempts(),
                                             game.getSecondAttempts(),
                                             game.getIncorrectAttempts(),
                                             gamesPlayed);
        final Score highestScore = getHighestScore();
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
        catch(IOException e)
        {
            System.out.println("Error: Unable to save score to " + SCORE_FILE_PATH + ": " + e.getMessage());
        }
    }

    /*
     * Retrieves the highest score from the score file based on the average score.
     * If the file does not exist or an error occurs, returns a default Score object.
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
        catch(IOException e)
        {
            System.out.println("Error reading score file: " + e.getMessage());
            return new Score(0,
                             0,
                             0,
                             0,
                             1);
        }

        return scoreList.stream().max(Comparator.comparingDouble(Score::getAverageScore)).orElse(new Score(0,
                                                                                                           0,
                                                                                                           0,
                                                                                                           0,
                                                                                                           1));
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
        final File res = new File(resourceDir);
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