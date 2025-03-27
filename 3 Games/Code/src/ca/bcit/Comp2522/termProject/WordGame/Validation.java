package ca.bcit.Comp2522.termProject.WordGame;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;


/**
 * The Validation class manages user input validation and game flow for the Geography Trivia Game.
 * It handles game mode selection, score tracking, and saving high scores to a file.
 *
 * @author Sehaj Gill
 * @version 1.0
 */
public class Validation
{
    private static final String         SCORE_FILE_PATH  = "Resources/score.txt";
    private static final Set<Character> VALID_GAME_MODES = Set.of('W',
                                                                  'N',
                                                                  'M',
                                                                  'Q');
    private static final Set<String>    YES_RESPONSES    = Set.of("YES",
                                                                  "Y");
    private static final Set<String>    NO_RESPONSES     = Set.of("NO",
                                                                  "N");

    private WordGame game;
    private Scanner  sc;

    /**
     * Constructs a new Validation instance, initializing the WordGame and Scanner for user input.
     *
     * @param resourceDir the directory containing the country data files
     * @param fileNames   the list of file names to load country data from
     */
    public Validation(String resourceDir,
                      List<String> fileNames)
    {
        game = new WordGame(resourceDir,
                            fileNames);
        sc   = new Scanner(System.in);
    }

    /**
     * Prompts the user to select a game mode and validates the input.
     * The user can choose to play the Word Game (W), Number Game (N), Custom Game (M), or Quit (Q).
     * Returns the validated choice, or 'Q' if the user chooses not to play again after a game.
     *
     * @return the validated user choice ('W', 'N', 'M', or 'Q')
     */
    public char getValidInput()
    {
        String gameChoiceInput;
        while(true)
        {
            System.out.print("Please choose a game (W = Word Game, N = Number Game, M = Custom Game, Q = Quit): ");
            gameChoiceInput = sc.nextLine().trim();
            if(gameChoiceInput.length() == 1)
            {
                char choice = Character.toUpperCase(gameChoiceInput.charAt(0));
                if(!VALID_GAME_MODES.contains(choice))
                {
                    System.out.println("Invalid input. Please enter W, N, M, or Q.");
                    continue;
                }
                switch(choice)
                {
                    case 'W':
                        System.out.println("Starting Word Game...");
                        game.playWordGame();
                        if(!playAgain())
                        {
                            return 'Q';
                        }
                        break;
                    case 'N':
                        System.out.println("Starting Number Game... (Not yet implemented)");
                        break;
                    case 'M':
                        System.out.println("Starting Custom Game... (Not yet implemented)");
                        break;
                    case 'Q':
                        System.out.println("Quitting game...");
                        return choice;
                }
            }
            else
            {
                System.out.println("Invalid input. Please enter a single character.");
            }
        }
    }

    /**
     * Prompts the user to decide if they want to play another game.
     * Accepts "Yes", "Y", "No", or "N" (case-insensitive) as valid inputs.
     * If the user chooses not to play again, saves and prints the high score.
     *
     * @return true if the user wants to play again, false otherwise
     */
    public boolean playAgain()
    {
        String response;
        while(true)
        {
            System.out.print("Do you want to play again? (Yes/No): ");
            response = sc.nextLine().trim().toUpperCase();
            if(YES_RESPONSES.contains(response))
            {
                return true;
            }
            else if(NO_RESPONSES.contains(response))
            {
                saveAndPrintHighScore();
                return false;
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
        int rawScore    = game.getScore();
        int gamesPlayed = game.getTotalGamesPlayed();

        System.out.println("\nRaw score from game: " + rawScore);
        System.out.println("Games played: " + gamesPlayed);
        Score currentScore = new Score(rawScore,
                                       game.getFirstAttempts(),
                                       game.getSecondAttempts(),
                                       game.getIncorrectAttempts(),
                                       gamesPlayed);
        Score highestScore = getHighestScore();
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
            System.out.println("No new high score this time.");
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
        List<Score> scoreList;
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

    /**
     * Closes the resources used by the Validation instance, including the WordGame.
     */
    public void close()
    {
        game.close();
    }
}