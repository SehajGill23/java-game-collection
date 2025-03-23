//package ca.bcit.Comp2522.termProject.Validate;
//
//import ca.bcit.Comp2522.termProject.WordGame;
//import ca.bcit.Comp2522.termProject.Score;
//
//import java.io.*;
//import java.util.*;
//
//public class Validation
//{
//    private static final int MAX_INPUT_LENGTH = 1;
//    private static final int FIRST_CHAR       = 0;
//
//    private WordGame game;
//    private Scanner  sc;
//
//    public Validation()
//    {
//        game = new WordGame();
//        sc   = new Scanner(System.in);
//    }
//
//    public void startGame()
//    {
//        System.out.println("Welcome to the Geography Trivia Game!");
//        System.out.println("Press W to play the Word game.");
//        System.out.println("Press N to play the Number game.");
//        System.out.println("Press M to play the Custom game.");
//        System.out.println("Press Q to quit.");
//        System.out.print("Enter your choice: ");
//    }
//
//    public char getValidInput()
//    {
//        String gameChoiceInput;
//        while(true)
//        {
//            System.out.print("Enter your choice (W, N, M, Q): ");
//            gameChoiceInput = sc.nextLine().trim();
//
//            if(gameChoiceInput.length() == MAX_INPUT_LENGTH)
//            {
//                char choice = Character.toUpperCase(gameChoiceInput.charAt(FIRST_CHAR));
//
//                switch(choice)
//                {
//                    case 'W':
//                        System.out.println("Starting the Word game...");
//                        game.playWordGame();
//                        if (!playAgain()) { // Change: Moved playAgain logic here (Issue 10)
//                            return 'Q'; // Quit after playing if user chooses not to play again
//                        }
//                        break;
//                    case 'N':
//                        System.out.println("Starting the Number game...");
//                        break;
//                    case 'M':
//                        System.out.println("Starting the Custom game...");
//                        break;
//                    case 'Q':
//                        System.out.println("Quitting the game. Goodbye!");
//                        return choice;
//                    default:
//                        System.out.println("Invalid input. Please enter W, N, M, or Q.");
//                }
//            }
//            else
//            {
//                System.out.println("Invalid input. Please enter a single character (W, N, M, or Q).");
//            }
//        }
//    }
//
//    public boolean playAgain() {
//        String response;
//        while (true) {
//            System.out.print("\nWould you like to play again? (Yes/No): ");
//            response = sc.nextLine().trim();
//            if (response.equalsIgnoreCase("Yes")) {
//                return true;
//            } else if (response.equalsIgnoreCase("No")) {
//                saveAndPrintHighScore();
//                return false;
//            } else {
//                System.out.println("Invalid input. Please enter Yes or No.");
//            }
//        }
//    }
//
//    private void saveAndPrintHighScore() {
//        Score currentScore = new Score(game.getScore(),
//                                       game.getFirstAttempts(),
//                                       game.getSecondAttempts(),
//                                       game.getIncorrectAttempts(),
//                                       game.getTotalGamesPlayed());
//        saveScoreToFile(currentScore);
//
//        Score highestScore = getHighestScore();
//
//        if (currentScore.getAverageScore() > highestScore.getAverageScore()) {
//            System.out.println("CONGRATULATIONS! You have the new high score!");
//        } else {
//            System.out.println("Your score didn't beat the high score.");
//        }
//
//    }
//
//    private void saveScoreToFile(Score score)
//    {
//        try(BufferedWriter writer = new BufferedWriter(new FileWriter("score.txt",
//                                                                      true)))
//        {
//            writer.write(score.toString() + "\n");
//        }
//        catch(IOException e)
//        {
//            System.out.println("Error saving score: " + e.getMessage());
//        }
//    }
//
//    private Score getHighestScore()
//    {
//        List<Score> scoreList = new ArrayList<>();
//        try(BufferedReader reader = new BufferedReader(new FileReader("score.txt")))
//        {
//            String line;
//            while((line = reader.readLine()) != null)
//            {
//                scoreList.add(Score.fromString(line));
//            }
//        }
//        catch(IOException e)
//        {
//            System.out.println("Error reading scores: " + e.getMessage());
//        }
//
//        return scoreList.stream()
//                        .max(Comparator.comparingDouble(Score::getAverageScore))
//                        .orElse(new Score(0, 0, 0, 0, 1));
//    }
//}
//


package ca.bcit.Comp2522.termProject.Validate;

import ca.bcit.Comp2522.termProject.WordGame;
import ca.bcit.Comp2522.termProject.Score;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
        Score currentScore = new Score(game.getScore(),
                                       game.getFirstAttempts(),
                                       game.getSecondAttempts(),
                                       game.getIncorrectAttempts(),
                                       game.getTotalGamesPlayed());
        saveScoreToFile(currentScore);
        Score highestScore = getHighestScore();
        if(currentScore.getAverageScore() > highestScore.getAverageScore())
        {
            System.out.println("Congratulations! You've set a new high score!");
        }
        else
        {
            System.out.println("No new high score this time.");
        }
    }

    private void saveScoreToFile(Score score)
    {
        File scoreFile = new File("Resources/","score.txt");
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
        try(BufferedReader reader = new BufferedReader(new FileReader("score.txt")))
        {
            String line;
            while((line = reader.readLine()) != null)
            {
                scoreList.add(Score.fromString(line));
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