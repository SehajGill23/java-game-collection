package ca.bcit.Comp2522.termProject.MyGame;

import javafx.animation.AnimationTimer;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class LetterEngine
{
    private static final long   FRAME_DURATION_NS = 16_666_666;
    private static final double MIN_DISTANCE      = 40.0;

    private final Pane           gamePane   = new Pane();
    private final List<Letter>   letters    = new ArrayList<>();
    private final List<Obstacle> obstacles  = new ArrayList<>();
    private final int            windowWidth;
    private final int            windowHeight;
    private       AnimationTimer timer;
    private       String         targetWord;
    private       String         bonusWord;
    private       Player         player;
    private       LevelManager   levelManager;
    private       boolean        bonusFound = false;
    private       LetterRush     game;


    public boolean isGameOver = false;


    public LetterEngine(final int width,
                        final int height)
    {
        this.windowWidth  = width;
        this.windowHeight = height;
    }

    public void setGame(LetterRush game)
    {
        this.game = game;
    }

    public final Pane getGamePane()
    {
        return gamePane;
    }

    public void startLevel(final Player player,
                           final GameUI ui,
                           final LevelManager levelManager)
    {
        this.player       = player;
        this.levelManager = levelManager;
        if(levelManager == null)
        {
            System.err.println("LevelManager is null in startLevel!");
            return;
        }
        try
        {
            player.setBonusPoints(0);
            final LevelManager.Level level = levelManager.getCurrentLevel();
            spawnLetters(ui);
            spawnObstacles(level);
            ui.updateLevel(levelManager.getCurrentLevelNumber());
            ui.updateTargetWord(targetWord);
            ui.updateScore(player.getScore());
            levelManager.startTimer();
            isGameOver = false;
            bonusFound = false;
            startTimer(ui);
        }
        catch(final IllegalStateException e)
        {
            System.err.println("Failed to start level: " + e.getMessage());
        }
    }

    private void spawnLetters(final GameUI ui)
    {
        letters.clear();
        gamePane.getChildren().clear();
        gamePane.getChildren().add(ui.getUIPane());

        final List<String> levelWords = levelManager.getCurrentLevel().getWordPairs();
        final String       pair       = levelWords.get((int) (Math.random() * levelWords.size()));
        final String[]     words      = pair.split(":");
        targetWord = words[0];
        System.out.println("Target word set to: " + targetWord);
        bonusWord = words.length > 1 ? words[1] : "";

        final String allLetters = (targetWord + bonusWord).toUpperCase();

        for(final char c : allLetters.toCharArray())
        {
            if(c == ':')
            {
                continue;
            }
            double  x, y;
            boolean tooClose;
            do
            {
                tooClose = false;
                x        = Math.random() * (windowWidth - 40);
                y        = Math.random() * (windowHeight - 40);
                for(Letter existing : letters)
                {
                    double dx = existing.getNode().getX() - x;
                    double dy = existing.getNode().getY() - y;
                    if(Math.sqrt(dx * dx + dy * dy) < MIN_DISTANCE)
                    {
                        tooClose = true;
                        break;
                    }
                }
            }
            while(tooClose);

            final Letter letter = new Letter(c,
                                             x,
                                             y,
                                             targetWord.contains(String.valueOf(c)),
                                             bonusWord.contains(String.valueOf(c)));
            letter.getNode().getStyleClass().add("letter");
            letter.getNode().getStyleClass().add("letter-regular");
            letters.add(letter);
            gamePane.getChildren().add(letter.getNode());
            letter.getNode().setOnMouseClicked(event -> handleLetterClick(letter,
                                                                          ui));
        }
    }

    private void spawnObstacles(final LevelManager.Level level)
    {
        obstacles.clear();
        final List<Obstacle> newObstacles = level.getObstacleConfig().stream().map(type ->
                                                                                   {
                                                                                       double  x, y;
                                                                                       boolean tooClose;
                                                                                       do
                                                                                       {
                                                                                           tooClose = false;
                                                                                           x        = Math.random() * windowWidth;
                                                                                           y        = Math.random() * windowHeight;
                                                                                           for(Letter letter : letters)
                                                                                           {
                                                                                               double dx = letter.getNode().getX() - x;
                                                                                               double dy = letter.getNode().getY() - y;
                                                                                               if(Math.sqrt(dx * dx + dy * dy) < MIN_DISTANCE)
                                                                                               {
                                                                                                   tooClose = true;
                                                                                                   break;
                                                                                               }
                                                                                           }
                                                                                       }
                                                                                       while(tooClose);

                                                                                       Obstacle obstacle = switch(type)
                                                                                       {
                                                                                           case MISSILE -> new Missile(x,
                                                                                                                       y);
                                                                                           case BOMB -> new Bomb(x,
                                                                                                                 y);
                                                                                           case SPIKE -> new Cactus(x,
                                                                                                                    y);
                                                                                       };
                                                                                       obstacle.getNode().getStyleClass().add("obstacle");
                                                                                       return obstacle;
                                                                                   }).collect(Collectors.toList());
        obstacles.addAll(newObstacles);
        gamePane.getChildren().addAll(newObstacles.stream().map(Obstacle::getNode).toList());
    }

    private void startTimer(final GameUI ui)
    {

        if(timer != null)
        {
            timer.stop();
        }

        timer = new AnimationTimer()
        {
            private long lastUpdate = 0;

            @Override
            public void handle(final long now)
            {
                if(now - lastUpdate >= FRAME_DURATION_NS)
                {
                    updateGame(ui,
                               now);
                    lastUpdate = now;
                }
            }
        };
        timer.start();
    }


    private void updateGame(final GameUI ui,
                            final long now)
    {
        if(isGameOver)
        {
            return;
        }


        letters.forEach(letter -> letter.updatePosition(windowWidth,
                                                        windowHeight));
        obstacles.forEach(obstacle -> obstacle.update(windowWidth,
                                                      windowHeight));

        if(!bonusFound && player.hasCompletedBonusWord(bonusWord))
        {
            System.out.println("Bonus word completed!");
            bonusFound = true;
            player.addBonusPoints();
            ui.updateScore(player.getScore());
            isGameOver = true;
            timer.stop();
            int currentLevel = levelManager.getCurrentLevelNumber();
            levelManager.advanceLevel();
            if(game != null)
            {
                game.showBonusAlert(currentLevel);
            }
            else
            {
                System.err.println("Cannot show bonus alert: game is null");
            }
            return;
        }


        if(player.hasCompletedTargetWord(targetWord))
        {
            System.out.println("Target word completed! Current Level: " + levelManager.getCurrentLevelNumber());
            isGameOver = true;
            timer.stop();
            int currentLevel = levelManager.getCurrentLevelNumber();
            if(currentLevel >= 5)
            {
                if(game != null)
                {
                    game.showGameWonAlert();
                }
                else
                {
                    System.err.println("Cannot show game won alert: game is null");
                }
            }
            else
            {
                levelManager.advanceLevel();
            }
            if(game != null)
            {
                game.showWinAlert(currentLevel);
            }
            else
            {
                System.err.println("Cannot show win alert: game is null");
            }
            return;
        }


        if(checkObstacleCollision())
        {
            System.out.println("Obstacle collision detected.");
            isGameOver = true;
            timer.stop();
            if(game != null)
            {
                game.showLossAlertObstacle();
            }
            else
            {
                System.err.println("Cannot show loss alert: game is null");
            }
            return;
        }

        if(levelManager.updateTimer(now,
                                    ui))
        {
            System.out.println("Time ran out.");
            isGameOver = true;
            timer.stop();
            if(game != null)
            {
                game.showLossAlertTime();
            }
            else
            {
                System.err.println("Cannot show loss alert: game is null");
            }
            return;
        }

        if(player.getCollectedTarget().size() > 0 && player.hasFailed(targetWord, bonusWord))
        {
            System.out.println("Player failed: wrong letter order or too many clicks. Collected: " + player.getCollectedTarget() + ", Target: " + targetWord + ", Incorrect clicks: " + player.getIncorrectClicks());
            isGameOver = true;
            timer.stop();
            if(game != null)
            {
                game.showLossAlert("Game Over!",
                                   "Wrong letter order or too many clicks! Try again?");
            }
            else
            {
                System.err.println("Cannot show loss alert: game is null");
            }
            return;
        }

    }


    private boolean checkObstacleCollision()
    {
        return obstacles.stream().anyMatch(obstacle -> obstacle.collidesWith(player));
    }

    private void handleLetterClick(final Letter letter,
                                   final GameUI ui)
    {
        if(player != null)
        {
            player.clickLetter(letter,
                               targetWord,
                               bonusWord);
            ui.updateScore(player.getScore());
            letter.getNode().getStyleClass().removeAll("letter-regular");
            letter.getNode().getStyleClass().add("letter-locked");
        }
        else
        {
            System.err.println("Player is null in handleLetterClick");
        }
    }

    public void resetGame(final Player player,
                          final GameUI ui,
                          final LevelManager levelManager)
    {
        this.player       = player;
        this.levelManager = levelManager;
        if(levelManager == null)
        {
            System.err.println("LevelManager is null in resetGame!");
            return;
        }

        if(timer != null)
        {
            timer.stop();
        }

        letters.clear();
        obstacles.clear();
        gamePane.getChildren().clear();
        gamePane.getChildren().add(ui.getUIPane());
        player.resetForNewLevel();
        player.setBonusPoints(0);
        ui.updateScore(player.getScore());
        isGameOver = false;
        bonusFound = false;


        System.out.println("Restarting level: " + levelManager.getCurrentLevelNumber());


        startLevel(player,
                   ui,
                   levelManager);
    }

}



