package ca.bcit.Comp2522.termProject.NumberGame;
import ca.bcit.Comp2522.termProject.WordGame.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class MainMenu
{
    private JFrame frame;

    /**
     * Constructs the MainMenu and initializes its components.
     */
    public MainMenu()
    {
        frame = new JFrame("20-Number Challenge Menu");
        initializeMenu();
    }

    /**
     * Sets up the menu window with Start and Exit buttons.
     */
    public void initializeMenu()
    {
        frame = new JFrame("Game Menu");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new FlowLayout());

        JButton startButton    = new JButton("Start Number Game");
        JButton mainMenuButton = new JButton("Return to Main Menu");
        startButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                frame.dispose();
                GameController controller = new AscendingOrderGame();
                new GameGUI(controller,
                            MainMenu.this);
            }
        });

        mainMenuButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                System.exit(0);
            }
        });

        frame.add(startButton);
        frame.setSize(300,
                      100);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        frame.add(mainMenuButton);
        frame.setSize(300,
                      100);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
