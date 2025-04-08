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
    private static final int    LETTER_X_POSITION_1_PIXELS      = 100;
    private static final int    LETTER_Y_POSITION_1_PIXELS      = 100;
    private static final int    LETTER_X_POSITION_2_PIXELS      = 150;
    private static final int    LETTER_Y_POSITION_2_PIXELS      = 150;
    private static final int    LETTER_X_POSITION_3_PIXELS      = 200;
    private static final int    LETTER_Y_POSITION_3_PIXELS      = 200;
    private static final int    EXPECTED_COUNT_1                = 1;
    private static final int    SCORE_PER_LETTER_POINTS         = 10;
    private static final int    EXPECTED_COUNT_2                = 2;
    private static final int    SCORE_FOR_TWO_LETTERS_POINTS    = 20;
    private static final int    EXPECTED_COUNT_3                = 3;
    private static final int    SCORE_FOR_THREE_LETTERS_POINTS  = 30;
    private static final int    SCORE_WITH_BONUS_POINTS         = 40;
    private static final int    INITIAL_VALUE                   = 0;
    private static final int    INITIAL_LETTER_POSITION_PIXELS  = 50;
    private static final int    WINDOW_WIDTH_PIXELS             = 1000;
    private static final int    WINDOW_HEIGHT_PIXELS            = 500;
    private static final int    OUT_OF_BOUNDS_X_PIXELS          = -10;
    private static final int    OUT_OF_BOUNDS_OFFSET_PIXELS     = 10;
    private static final int    LETTER_SIZE_PIXELS              = 20;
    private static final int    SCORE_AFTER_LEVEL_2_POINTS      = 60;
    private static final int    HIGH_SCORE_50_POINTS            = 50;
    private static final int    HIGH_SCORE_75_POINTS            = 75;
    private static final int    SCORE_30_POINTS                 = 30;
    private static final String TARGET_WORD_HEN                 = "HEN";
    private static final String BONUS_WORD_HUN                  = "HUN";
    private static final String SCORE_FILE                      = "Resources/letterrush/txtfiles/highScore.txt";
    private static final String COLLECTED_1_LETTER_MESSAGE      = "Should have collected 1 letter for target word.";
    private static final String FIRST_LETTER_H_MESSAGE          = "First collected letter should be 'H'.";
    private static final String SCORE_10_POINTS_MESSAGE         = "Score should be 10 points (1 letter * 10 points).";
    private static final String COLLECTED_2_LETTERS_MESSAGE     = "Should have collected 2 letters for target word.";
    private static final String SECOND_LETTER_E_MESSAGE         = "Second collected letter should be 'E'.";
    private static final String SCORE_20_POINTS_MESSAGE         = "Score should be 20 points (2 letters * 10 points).";
    private static final String COLLECTED_3_LETTERS_MESSAGE     = "Should have collected 3 letters for target word.";
    private static final String THIRD_LETTER_N_MESSAGE          = "Third collected letter should be 'N'.";
    private static final String SCORE_30_POINTS_MESSAGE         = "Score should be 30 points (3 letters * 10 points).";
    private static final String TARGET_WORD_COMPLETED_MESSAGE   = "Target word 'HEN' should be completed.";
    private static final String BONUS_WORD_COMPLETED_MESSAGE    = "Bonus word 'HUN' should be completed.";
    private static final String NO_LETTERS_COLLECTED_MESSAGE    = "No letters should be collected for target word.";
    private static final String INCORRECT_CLICKS_1_MESSAGE      = "Incorrect clicks should be 1.";
    private static final String SCORE_0_POINTS_MESSAGE          = "Score should be 0 (no correct letters).";
    private static final String INCORRECT_CLICKS_2_MESSAGE      = "Incorrect clicks should be 2.";
    private static final String NOT_FAILED_YET_MESSAGE          = "Player should not have failed yet (2 < 3).";
    private static final String INCORRECT_CLICKS_3_MESSAGE      = "Incorrect clicks should be 3.";
    private static final String PLAYER_FAILED_MESSAGE           = "Player should have failed (3 incorrect clicks).";
    private static final String X_POSITION_CHANGED_MESSAGE      = "Letter X position should have changed.";
    private static final String Y_POSITION_CHANGED_MESSAGE      = "Letter Y position should have changed.";
    private static final String X_NOT_LESS_THAN_ZERO_MESSAGE    = "Letter X position should not be less than 0.";
    private static final String X_NOT_EXCEED_WIDTH_MESSAGE      = "Letter X position should not exceed (width - SIZE).";
    private static final String SCORE_60_POINTS_MESSAGE         = "Score should be 60 points before reset.";
    private static final String SCORE_RESET_40_POINTS_MESSAGE   = "Score should reset to 40 points (start of level 2).";
    private static final String HIGH_SCORE_75_POINTS_MESSAGE    = "High score should now be 75 points.";
    private static final String THREE_ENTRIES_MESSAGE           = "There should be 3 high score entries in the file.";
    private static final String HIGHEST_SCORE_50_POINTS_MESSAGE = "The highest score should be 50 points.";
    private static final String SCORE_40_POINTS_MESSAGE         = "Score should be 40 points (H: 10 + N: 10 +" +
                                                                  " bonus: 40).";
    private static final String HIGH_SCORE_50_POINTS_MESSAGE    = "High score should be 50 points after loading from" +
                                                                  " file.";
    private static final String EMPTY_FILE_MESSAGE              = "Reading from an empty high score file should return "
                                                                  + "an empty list.";
    private static final String SCORE_10_NO_BONUS_MESSAGE       = "Score should be 10 points (no additional " +
                                                                  "points yet).";

    private Player     player;
    private LetterRush letterRush;


    @BeforeEach
    void setUp() throws IOException
    {
        // Clear the high score file before each test to ensure no leftover data
        new FileWriter(SCORE_FILE,
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
                                LETTER_X_POSITION_1_PIXELS,
                                LETTER_Y_POSITION_1_PIXELS,
                                true,
                                false);
        letterE    = new Letter('E',
                                LETTER_X_POSITION_2_PIXELS,
                                LETTER_Y_POSITION_2_PIXELS,
                                true,
                                false);
        letterN    = new Letter('N',
                                LETTER_X_POSITION_3_PIXELS,
                                LETTER_Y_POSITION_3_PIXELS,
                                true,
                                false);
        targetWord = TARGET_WORD_HEN;
        bonusWord  = BONUS_WORD_HUN;

        player.clickLetter(letterH,
                           targetWord,
                           bonusWord);
        assertEquals(EXPECTED_COUNT_1,
                     player.getCollectedTarget().size(),
                     COLLECTED_1_LETTER_MESSAGE);
        assertEquals('H',
                     player.getCollectedTarget().getFirst(),
                     FIRST_LETTER_H_MESSAGE);
        assertEquals(SCORE_PER_LETTER_POINTS,
                     player.getScore(),
                     SCORE_10_POINTS_MESSAGE);

        player.clickLetter(letterE,
                           targetWord,
                           bonusWord);
        assertEquals(EXPECTED_COUNT_2,
                     player.getCollectedTarget().size(),
                     COLLECTED_2_LETTERS_MESSAGE);
        assertEquals('E',
                     player.getCollectedTarget().get(EXPECTED_COUNT_1),
                     SECOND_LETTER_E_MESSAGE);
        assertEquals(SCORE_FOR_TWO_LETTERS_POINTS,
                     player.getScore(),
                     SCORE_20_POINTS_MESSAGE);

        player.clickLetter(letterN,
                           targetWord,
                           bonusWord);
        assertEquals(EXPECTED_COUNT_3,
                     player.getCollectedTarget().size(),
                     COLLECTED_3_LETTERS_MESSAGE);
        assertEquals('N',
                     player.getCollectedTarget().get(EXPECTED_COUNT_2),
                     THIRD_LETTER_N_MESSAGE);
        assertEquals(SCORE_FOR_THREE_LETTERS_POINTS,
                     player.getScore(),
                     SCORE_30_POINTS_MESSAGE);
        assertTrue(player.hasCompletedTargetWord(targetWord),
                   TARGET_WORD_COMPLETED_MESSAGE);
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
                                LETTER_X_POSITION_1_PIXELS,
                                LETTER_Y_POSITION_1_PIXELS,
                                true,
                                true);
        letterU    = new Letter('U',
                                LETTER_X_POSITION_2_PIXELS,
                                LETTER_Y_POSITION_2_PIXELS,
                                false,
                                true);
        letterN    = new Letter('N',
                                LETTER_X_POSITION_3_PIXELS,
                                LETTER_Y_POSITION_3_PIXELS,
                                true,
                                true);
        targetWord = TARGET_WORD_HEN;
        bonusWord  = BONUS_WORD_HUN;

        // Click letters to form "HUN"
        player.clickLetter(letterH,
                           targetWord,
                           bonusWord);
        assertEquals(SCORE_PER_LETTER_POINTS,
                     player.getScore(),
                     SCORE_10_POINTS_MESSAGE);

        player.clickLetter(letterU,
                           targetWord,
                           bonusWord);
        assertEquals(SCORE_PER_LETTER_POINTS,
                     player.getScore(),
                     SCORE_10_NO_BONUS_MESSAGE);

        player.clickLetter(letterN,
                           targetWord,
                           bonusWord);
        assertTrue(player.hasCompletedBonusWord(bonusWord),
                   BONUS_WORD_COMPLETED_MESSAGE);
        assertEquals(SCORE_WITH_BONUS_POINTS,
                     player.getScore(),
                     SCORE_40_POINTS_MESSAGE);
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
                                LETTER_X_POSITION_1_PIXELS,
                                LETTER_Y_POSITION_1_PIXELS,
                                false,
                                false);
        letterY    = new Letter('Y',
                                LETTER_X_POSITION_2_PIXELS,
                                LETTER_Y_POSITION_2_PIXELS,
                                false,
                                false);
        letterZ    = new Letter('Z',
                                LETTER_X_POSITION_3_PIXELS,
                                LETTER_Y_POSITION_3_PIXELS,
                                false,
                                false);
        targetWord = TARGET_WORD_HEN;
        bonusWord  = BONUS_WORD_HUN;

        // Click incorrect letter 'X'
        player.clickLetter(letterX,
                           targetWord,
                           bonusWord);
        assertEquals(INITIAL_VALUE,
                     player.getCollectedTarget().size(),
                     NO_LETTERS_COLLECTED_MESSAGE);
        assertEquals(EXPECTED_COUNT_1,
                     player.getIncorrectClicks(),
                     INCORRECT_CLICKS_1_MESSAGE);
        assertEquals(INITIAL_VALUE,
                     player.getScore(),
                     SCORE_0_POINTS_MESSAGE);

        // Click a different incorrect letter 'Y'
        player.clickLetter(letterY,
                           targetWord,
                           bonusWord);
        assertEquals(EXPECTED_COUNT_2,
                     player.getIncorrectClicks(),
                     INCORRECT_CLICKS_2_MESSAGE);
        assertFalse(player.hasFailed(targetWord,
                                     bonusWord),
                    NOT_FAILED_YET_MESSAGE);

        // Click another incorrect letter 'Z'
        player.clickLetter(letterZ,
                           targetWord,
                           bonusWord);
        assertEquals(EXPECTED_COUNT_3,
                     player.getIncorrectClicks(),
                     INCORRECT_CLICKS_3_MESSAGE);
        assertTrue(player.hasFailed(targetWord,
                                    bonusWord),
                   PLAYER_FAILED_MESSAGE);
    }


    @Test
    void testLetterMovement()
    {
        Letter letter;
        int    width;
        int    height;

        letter = new Letter('A',
                            INITIAL_LETTER_POSITION_PIXELS,
                            INITIAL_LETTER_POSITION_PIXELS,
                            true,
                            false);
        width  = WINDOW_WIDTH_PIXELS;
        height = WINDOW_HEIGHT_PIXELS;

        // Move within bounds
        double initialX;
        double initialY;
        initialX = letter.getNode().getX();
        initialY = letter.getNode().getY();
        letter.updatePosition(width,
                              height);
        assertNotEquals(initialX,
                        letter.getNode().getX(),
                        X_POSITION_CHANGED_MESSAGE);
        assertNotEquals(initialY,
                        letter.getNode().getY(),
                        Y_POSITION_CHANGED_MESSAGE);

        // Move to boundary (x < 0)
        letter.getNode().setX(OUT_OF_BOUNDS_X_PIXELS);
        letter.updatePosition(width,
                              height);
        assertTrue(letter.getNode().getX() >= INITIAL_VALUE,
                   X_NOT_LESS_THAN_ZERO_MESSAGE);

        // Move to boundary (x > width - SIZE)
        letter.getNode().setX(width + OUT_OF_BOUNDS_OFFSET_PIXELS);
        letter.updatePosition(width,
                              height);
        assertTrue(letter.getNode().getX() <= width - LETTER_SIZE_PIXELS,
                   X_NOT_EXCEED_WIDTH_MESSAGE);
    }

    @Test
    void testLetterRushResetGame()
    {
        // Set initial state
        letterRush.scoreAtLevelStart = INITIAL_VALUE; // Set initial score
        player.setScore(SCORE_WITH_BONUS_POINTS); // Earn 40 points in level 1

        // Simulate proceeding to level 2
        player.resetForNewLevel();
        letterRush.scoreAtLevelStart = player.getScore(); // 40
        player.setScore(SCORE_AFTER_LEVEL_2_POINTS); // Earn 20 more points in level 2
        assertEquals(SCORE_AFTER_LEVEL_2_POINTS,
                     player.getScore(),
                     SCORE_60_POINTS_MESSAGE);

        // Simulate resetGame() logic without calling JavaFX methods
        player.setScore(letterRush.scoreAtLevelStart);
        player.setBonusPoints(INITIAL_VALUE);
        player.resetForNewLevel();
        assertEquals(SCORE_WITH_BONUS_POINTS,
                     player.getScore(),
                     SCORE_RESET_40_POINTS_MESSAGE);
    }

    @Test
    void testHighScorePersistence() throws IOException
    {
        player.setScore(HIGH_SCORE_50_POINTS);
        player.updateHighScore();

        Player newPlayer;
        newPlayer = new Player();
        assertEquals(HIGH_SCORE_50_POINTS,
                     newPlayer.getHighScore(),
                     HIGH_SCORE_50_POINTS_MESSAGE);

        newPlayer.setScore(HIGH_SCORE_75_POINTS);
        newPlayer.updateHighScore();

        List<LetterRushScore> scores;
        int                   highScore;
        scores    = LetterRushScore.readScoresFromFile(SCORE_FILE);
        highScore = scores.stream().mapToInt(LetterRushScore::getHighScore).max().orElse(INITIAL_VALUE);
        assertEquals(HIGH_SCORE_75_POINTS,
                     highScore,
                     HIGH_SCORE_75_POINTS_MESSAGE);
    }

    @Test
    void testMultipleHighScoreEntries() throws IOException
    {
        player.setScore(SCORE_30_POINTS);
        player.updateHighScore();

        player.setScore(SCORE_WITH_BONUS_POINTS);
        player.updateHighScore();

        player.setScore(HIGH_SCORE_50_POINTS);
        player.updateHighScore();

        List<LetterRushScore> scores;
        scores = LetterRushScore.readScoresFromFile(SCORE_FILE);
        assertEquals(EXPECTED_COUNT_3,
                     scores.size(),
                     THREE_ENTRIES_MESSAGE);

        int highScore;
        highScore = scores.stream().mapToInt(LetterRushScore::getHighScore).max().orElse(INITIAL_VALUE);
        assertEquals(HIGH_SCORE_50_POINTS,
                     highScore,
                     HIGHEST_SCORE_50_POINTS_MESSAGE);
    }

    @Test
    void testEmptyHighScoreFile() throws IOException
    {
        List<LetterRushScore> scores;
        scores = LetterRushScore.readScoresFromFile(SCORE_FILE);
        assertTrue(scores.isEmpty(),
                   EMPTY_FILE_MESSAGE);
    }

    @AfterEach
    void tearDown()
    {
        new File(SCORE_FILE).delete();
    }
}