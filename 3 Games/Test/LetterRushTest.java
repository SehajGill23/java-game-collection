import ca.bcit.Comp2522.termProject.MyGame.Letter;
import ca.bcit.Comp2522.termProject.MyGame.Missile;
import ca.bcit.Comp2522.termProject.MyGame.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LetterRushTest
{ // Lesson 10: Unit Tests
    @Test
    public void testLetterClickOrder()
    {
        final Player player = new Player();
        final Letter f      = new Letter('F',
                                         0,
                                         0,
                                         true,
                                         false);
        final Letter o      = new Letter('O',
                                         0,
                                         0,
                                         true,
                                         false);
        final Letter x      = new Letter('X',
                                         0,
                                         0,
                                         true,
                                         false);

        player.clickLetter(f);
        player.clickLetter(o);
        player.clickLetter(x);

        assertTrue(player.hasCompletedWords("FOX",
                                            ""));
    }

    @Test
    public void testObstacleCollision()
    {
        final Player  player  = new Player();
        final Missile missile = new Missile(0,
                                            0);
        player.updateCursorPosition(0,
                                    0);
        assertTrue(missile.collidesWith(player.getCursorBounds()));
    }
}
