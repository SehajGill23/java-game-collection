package ca.bcit.Comp2522.termProject.Validate;

import ca.bcit.Comp2522.termProject.WordGame;
import ca.bcit.Comp2522.termProject.Score;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Validation
{
    private WordGame game;
    private Scanner  sc;

    public Validation(String resourceDir,
                      List<String> fileNames)
    {
        game = new WordGame(resourceDir,
                            fileNames);
        sc   = new Scanner(System.in);
    }

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
                        System.out.println("Starting Number Game...");
                        break;
                    case 'M':
                        System.out.println("Starting Custom Game...");
                        break;
                    case 'Q':
                        System.out.println("Quitting game...");
                        return choice;
                    default:
                        System.out.println("Invalid input. Please enter W, N, M, or Q.");
                }
            }
            else
            {
                System.out.println("Invalid input. Please enter a single character.");
            }
        }
    }

    public boolean playAgain()
    {
        String response;
        while(true)
        {
            System.out.print("Do you want to play again? (Yes/No): ");
            response = sc.nextLine().trim().toUpperCase();
            if(response.equals("YES") || response.equals("Y"))
            {
                return true;
            }
            else if(response.equals("NO") || response.equals("N"))
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


    private void saveAndPrintHighScore()
    {
        // Get the current score from the game
        Score currentScore = new Score(game.getScore(),
                                       game.getFirstAttempts(),
                                       game.getSecondAttempts(),
                                       game.getIncorrectAttempts(),
                                       game.getTotalGamesPlayed());

        // Retrieve the highest score ever recorded
        Score highestScore = getHighestScore();

        System.out.println("Current Score: " + currentScore.getAverageScore());
        System.out.println("Highest Score: " + highestScore.getAverageScore());

        // Check if the current score is higher, equal, or lower than the highest score
        if(currentScore.getAverageScore() > highestScore.getAverageScore())
        {
            System.out.println("Congratulations! You've set a new high score!");
        }
        else
        {
            // The player did not surpass the highest score
            System.out.println("No new high score this time.");
        }

        // Save the current score to the score file
        saveScoreToFile(currentScore);
    }


    private void saveScoreToFile(Score score)
    {
        File scoreFile = new File("Resources/",
                                  "score.txt");
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(scoreFile,
                                                                      true)))
        {
            writer.write(score.toString() + "\n");
        }
        catch(IOException e)
        {
            System.out.println("Error: Unable to save score to " + scoreFile.getAbsolutePath() + ": " + e.getMessage());
        }
    }


    private Score getHighestScore()
    {
        List<Score> scoreList = new ArrayList<>();

        File scoreFile = new File("Resources/score.txt");

        if(!scoreFile.exists())
        {
            System.out.println("No previous scores found. Creating a new score file.");
            try
            {
                // Create the file manually
                scoreFile.createNewFile();
                System.out.println("New score file created at: " + scoreFile.getAbsolutePath());
            }
            catch(IOException e)
            {
                System.out.println("Error creating score file: " + e.getMessage());
            }
            return new Score(0,
                             0,
                             0,
                             0,
                             1);
        }


        try(BufferedReader reader = Files.newBufferedReader(Paths.get("Resources/score.txt"),
                                                            StandardCharsets.UTF_8))
        {
            String line;
            while((line = reader.readLine()) != null)
            {
                if(!line.trim().isEmpty())
                {
                    scoreList.add(Score.fromString(line));
                }
            }
        }
        catch(IOException e)
        {
            System.out.println("Error: Unable to read scores from score.txt: " + e.getMessage());
        }

        return scoreList.stream().max(Comparator.comparingDouble(Score::getAverageScore)).orElse(new Score(0,
                                                                                                           0,
                                                                                                           0,
                                                                                                           0,
                                                                                                           1));
    }


    public void close()
    {
        game.close();
        sc.close();
    }

}