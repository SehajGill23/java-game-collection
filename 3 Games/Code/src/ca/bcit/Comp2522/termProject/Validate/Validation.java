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
        File scoreFile = new File("Resources/",
                                  "score.txt");

        if(!scoreFile.exists())
        {
            System.out.println("Score file not found at: " + scoreFile.getAbsolutePath());
            return new Score(0,
                             0,
                             0,
                             0,
                             1);
        }

        try(BufferedReader reader = Files.newBufferedReader(scoreFile.toPath(),
                                                            StandardCharsets.UTF_8))
        {
            String        line;
            StringBuilder scoreEntry = new StringBuilder();
            int           entryCount = 0;
            while((line = reader.readLine()) != null)
            {
                if(line.trim().isEmpty() && scoreEntry.length() > 0)
                {
                    entryCount++;
                    Score score = Score.fromString(scoreEntry.toString());
                    scoreList.add(score);
                    scoreEntry.setLength(0);
                }
                else if(!line.trim().isEmpty())
                {
                    scoreEntry.append(line).append("\n");
                }
            }
            if(scoreEntry.length() > 0)
            {
                entryCount++;
                Score score = Score.fromString(scoreEntry.toString());
                scoreList.add(score);
            }
            System.out.println("Total entries read: " + entryCount);
        }
        catch(IOException e)
        {
            System.out.println("Error reading score file: " + e.getMessage());
        }

        Score highestScore = scoreList.stream().max(Comparator.comparingDouble(Score::getAverageScore)).orElse(new Score(0,
                                                                                                                         0,
                                                                                                                         0,
                                                                                                                         0,
                                                                                                                         1));
        return highestScore;
    }

    public void close()
    {
        game.close();
        sc.close();
    }

}