import ca.bcit.comp2522.termproject.letterrushgame.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Test class for the LetterRush game, focusing on core logic and interactions
 * between Player, Letter, Obstacle, LetterEngine, and LetterRush.
 * Avoids JavaFX-specific operations and tests high score persistence.
 *
 * @author Sehaj Gill
 * @version 1.0
 */
class LetterRushTest
{
    private Player     player;
    private LetterRush letterRush;

    @BeforeEach
    void setUp() throws IOException
    {
        // Clear the high score file before each test to ensure no leftover data
        new FileWriter("Resources/letterrush/txtfiles/highScore.txt",
                       false).close();

        player     = new Player();
        letterRush = new LetterRush();
    }

    @Test
    void testPlayerClickLetterTargetWord()
    {
        Letter letterH;
        Letter letterE;
        Letter letterN;
        String targetWord;
        String bonusWord;

        letterH    = new Letter('H',
                                100,
                                100,
                                true,
                                false);
        letterE    = new Letter('E',
                                150,
                                150,
                                true,
                                false);
        letterN    = new Letter('N',
                                200,
                                200,
                                true,
                                false);
        targetWord = "HEN";
        bonusWord  = "HUN";

        player.clickLetter(letterH,
                           targetWord,
                           bonusWord);
        assertEquals(1,
                     player.getCollectedTarget().size(),
                     "Should have collected 1 letter for target word.");
        assertEquals('H',
                     player.getCollectedTarget().getFirst(),
                     "First collected letter should be 'H'.");
        assertEquals(10,
                     player.getScore(),
                     "Score should be 10 points (1 letter * 10 points).");

        player.clickLetter(letterE,
                           targetWord,
                           bonusWord);
        assertEquals(2,
                     player.getCollectedTarget().size(),
                     "Should have collected 2 letters for target word.");
        assertEquals('E',
                     player.getCollectedTarget().get(1),
                     "Second collected letter should be 'E'.");
        assertEquals(20,
                     player.getScore(),
                     "Score should be 20 points (2 letters * 10 points).");

        player.clickLetter(letterN,
                           targetWord,
                           bonusWord);
        assertEquals(3,
                     player.getCollectedTarget().size(),
                     "Should have collected 3 letters for target word.");
        assertEquals('N',
                     player.getCollectedTarget().get(2),
                     "Third collected letter should be 'N'.");
        assertEquals(30,
                     player.getScore(),
                     "Score should be 30 points (3 letters * 10 points).");
        assertTrue(player.hasCompletedTargetWord(targetWord),
                   "Target word 'HEN' should be completed.");
    }

    @Test
    void testPlayerClickLetterBonusWord()
    {
        Letter letterH;
        Letter letterU;
        Letter letterN;
        String targetWord;
        String bonusWord;

        letterH    = new Letter('H',
                                100,
                                100,
                                true,
                                true);
        letterU    = new Letter('U',
                                150,
                                150,
                                false,
                                true);
        letterN    = new Letter('N',
                                200,
                                200,
                                true,
                                true);
        targetWord = "HEN";
        bonusWord  = "HUN";

        // Click letters to form "HUN"
        player.clickLetter(letterH,
                           targetWord,
                           bonusWord);
        assertEquals(10,
                     player.getScore(),
                     "Score should be 10 points (1 letter * 10 points).");

        player.clickLetter(letterU,
                           targetWord,
                           bonusWord);
        assertEquals(10,
                     player.getScore(),
                     "Score should be 10 points (no additional points yet).");

        player.clickLetter(letterN,
                           targetWord,
                           bonusWord);
        assertTrue(player.hasCompletedBonusWord(bonusWord),
                   "Bonus word 'HUN' should be completed.");
        assertEquals(40,
                     player.getScore(),
                     "Score should be 40 points (H: 10 + N: 10 + bonus: 40).");
    }

    @Test
    void testPlayerIncorrectClicks()
    {
        Letter letterX;
        Letter letterY;
        Letter letterZ;
        String targetWord;
        String bonusWord;

        letterX    = new Letter('X',
                                100,
                                100,
                                false,
                                false);
        letterY    = new Letter('Y',
                                150,
                                150,
                                false,
                                false);
        letterZ    = new Letter('Z',
                                200,
                                200,
                                false,
                                false);
        targetWord = "HEN";
        bonusWord  = "HUN";

        // Click incorrect letter 'X'
        player.clickLetter(letterX,
                           targetWord,
                           bonusWord);
        assertEquals(0,
                     player.getCollectedTarget().size(),
                     "No letters should be collected for target word.");
        assertEquals(1,
                     player.getIncorrectClicks(),
                     "Incorrect clicks should be 1.");
        assertEquals(0,
                     player.getScore(),
                     "Score should be 0 (no correct letters).");

        // Click a different incorrect letter 'Y'
        player.clickLetter(letterY,
                           targetWord,
                           bonusWord);
        assertEquals(2,
                     player.getIncorrectClicks(),
                     "Incorrect clicks should be 2.");
        assertFalse(player.hasFailed(targetWord,
                                     bonusWord),
                    "Player should not have failed yet (2 < 3).");

        // Click another incorrect letter 'Z'
        player.clickLetter(letterZ,
                           targetWord,
                           bonusWord);
        assertEquals(3,
                     player.getIncorrectClicks(),
                     "Incorrect clicks should be 3.");
        assertTrue(player.hasFailed(targetWord,
                                    bonusWord),
                   "Player should have failed (3 incorrect clicks).");
    }

    @Test
    void testLetterMovement()
    {
        Letter letter;
        int    width;
        int    height;

        letter = new Letter('A',
                            50,
                            50,
                            true,
                            false);
        width  = 1000;
        height = 500;

        // Move within bounds
        double initialX;
        double initialY;
        initialX = letter.getNode().getX();
        initialY = letter.getNode().getY();
        letter.updatePosition(width,
                              height);
        assertNotEquals(initialX,
                        letter.getNode().getX(),
                        "Letter X position should have changed.");
        assertNotEquals(initialY,
                        letter.getNode().getY(),
                        "Letter Y position should have changed.");

        // Move to boundary (x < 0)
        letter.getNode().setX(-10);
        letter.updatePosition(width,
                              height);
        assertTrue(letter.getNode().getX() >= 0,
                   "Letter X position should not be less than 0.");

        // Move to boundary (x > width - SIZE)
        letter.getNode().setX(width + 10);
        letter.updatePosition(width,
                              height);
        assertTrue(letter.getNode().getX() <= width - 20,
                   "Letter X position should not exceed (width - SIZE).");
    }

    @Test
    void testLetterRushResetGame()
    {
        // Set initial state
        letterRush.scoreAtLevelStart = 0; // Set initial score
        player.setScore(40); // Earn 40 points in level 1

        // Simulate proceeding to level 2
        player.resetForNewLevel();
        letterRush.scoreAtLevelStart = player.getScore(); // 40
        player.setScore(60); // Earn 20 more points in level 2
        assertEquals(60,
                     player.getScore(),
                     "Score should be 60 points before reset.");

        // Simulate resetGame() logic without calling JavaFX methods
        player.setScore(letterRush.scoreAtLevelStart);
        player.setBonusPoints(0);
        player.resetForNewLevel();
        assertEquals(40,
                     player.getScore(),
                     "Score should reset to 40 points (start of level 2).");
    }

    @Test
    void testHighScorePersistence() throws IOException
    {
        player.setScore(50);
        player.updateHighScore();

        Player newPlayer;
        newPlayer = new Player();
        assertEquals(50,
                     newPlayer.getHighScore(),
                     "High score should be 50 points after loading from file.");

        newPlayer.setScore(75);
        newPlayer.updateHighScore();

        List<LetterRushScore> scores;
        int                   highScore;
        scores    = LetterRushScore.readScoresFromFile("Resources/letterrush/txtfiles/highScore.txt");
        highScore = scores.stream().mapToInt(LetterRushScore::getHighScore).max().orElse(0);
        assertEquals(75,
                     highScore,
                     "High score should now be 75 points.");
    }

    @Test
    void testMultipleHighScoreEntries() throws IOException
    {
        player.setScore(30);
        player.updateHighScore();

        player.setScore(40);
        player.updateHighScore();

        player.setScore(50);
        player.updateHighScore();

        List<LetterRushScore> scores;
        scores = LetterRushScore.readScoresFromFile("Resources/letterrush/txtfiles/highScore.txt");
        assertEquals(3,
                     scores.size(),
                     "There should be 3 high score entries in the file.");

        int highScore;
        highScore = scores.stream().mapToInt(LetterRushScore::getHighScore).max().orElse(0);
        assertEquals(50,
                     highScore,
                     "The highest score should be 50 points.");
    }

    @Test
    void testEmptyHighScoreFile() throws IOException
    {
        List<LetterRushScore> scores;
        scores = LetterRushScore.readScoresFromFile("Resources/letterrush/txtfiles/highScore.txt");
        assertTrue(scores.isEmpty(),
                   "Reading from an empty high score file should return an empty list.");
    }

    @AfterEach
    void tearDown()
    {
        new File("Resources/letterrush/txtfiles/highScore.txt").delete();
    }
}