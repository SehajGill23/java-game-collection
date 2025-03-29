package ca.bcit.Comp2522.termProject.NumberGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Manages the graphical user interface for the 20-Number Challenge game using a 4x5 grid of buttons,
 * labels for game status, and menu options.
 */
public class GameGUI
{

    private final        GameController controller;
    private final        JButton[][]    gridButtons;
    private final        JLabel         currentNumberLabel;
    private final        JLabel         statusLabel;
    private final        JButton        tryAgainButton;
    private final        JButton        quitButton;
    private final        JFrame         frame;
    private final        MainMenu       mainMenu;
    private              boolean        gameOver;
    private static final int            ROWS = 4;
    private static final int            COLS = 5;

    public GameGUI(final GameController controller,
                   final MainMenu mainMenu)
    {
        this.controller    = controller;
        this.mainMenu      = mainMenu;
        gridButtons        = new JButton[ROWS][COLS];
        currentNumberLabel = new JLabel("Select a slot");
        statusLabel        = new JLabel("Place the numbers in ascending order.");
        tryAgainButton     = new JButton("Try Again");
        quitButton         = new JButton("Quit");
        frame              = new JFrame("20-Number Challenge");
        gameOver           = false;
        initializeUI();
        resetGame();
    }

    public void initializeUI()
    {
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        final JPanel gridPanel = new JPanel(new GridLayout(ROWS,
                                                           COLS,
                                                           5,
                                                           5));
        for(int i = 0; i < ROWS; i++)
        {
            for(int j = 0; j < COLS; j++)
            {
                final int row = i;
                final int col = j;
                gridButtons[i][j] = new JButton("[]");
                gridButtons[i][j].setFont(new Font("Montserrat",
                                                   Font.PLAIN,
                                                   25));
                gridButtons[i][j].addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(final ActionEvent e)
                    {
                        handleButtonClick(row,
                                          col);
                    }
                });
                gridPanel.add(gridButtons[i][j]);
            }
        }

        final JPanel labelPanel = new JPanel(new GridLayout(2,
                                                            1));
        currentNumberLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        labelPanel.add(currentNumberLabel);
        labelPanel.add(statusLabel);

        final JPanel buttonPanel = new JPanel(new FlowLayout());
        tryAgainButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(final ActionEvent e)
            {
                resetGame();
            }
        });
        quitButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(final ActionEvent e)
            {
                returnToMenu();
            }
        });
        tryAgainButton.setEnabled(false);
        quitButton.setEnabled(true);
        buttonPanel.add(tryAgainButton);
        buttonPanel.add(quitButton);

        frame.add(gridPanel,
                  BorderLayout.CENTER);
        frame.add(labelPanel,
                  BorderLayout.NORTH);
        frame.add(buttonPanel,
                  BorderLayout.SOUTH);

        frame.setSize(400,
                      300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void updateGridDisplay()
    {
        final int[][] grid = controller.getGrid();
        currentNumberLabel.setText("Select a slot");

        for(int i = 0; i < ROWS; i++)
        {
            for(int j = 0; j < COLS; j++)
            {
                if(grid[i][j] == -1)
                {
                    gridButtons[i][j].setText("[]");
                    gridButtons[i][j].setEnabled(true);
                }
                else
                {
                    gridButtons[i][j].setText(String.valueOf(grid[i][j]));
                    gridButtons[i][j].setEnabled(false);
                }
            }
        }

        frame.revalidate();
        frame.repaint();
    }

    public void showGameOver(final String message)
    {
        gameOver = true;
        statusLabel.setText("Game Over");

        for(int i = 0; i < ROWS; i++)
        {
            for(int j = 0; j < COLS; j++)
            {
                gridButtons[i][j].setEnabled(false);
            }
        }

        Object[] options = {"Try Again", "Quit"};
        int choice = JOptionPane.showOptionDialog(frame,
                                                  message,
                                                  "Game Over",
                                                  JOptionPane.YES_NO_OPTION,
                                                  JOptionPane.INFORMATION_MESSAGE,
                                                  null,
                                                  options,
                                                  options[0]);

        if(choice == JOptionPane.YES_OPTION)
        {
            resetGame();
        }
        else
        {
            returnToMenu();
        }
    }

    public void handleButtonClick(final int row,
                                  final int col)
    {
        if(gameOver)
        {
            return;
        }
        if(!controller.isValidPlacement(row,
                                        col))
        {
//            JOptionPane.showMessageDialog(frame,
//                                          "Invalid placement! The number must be greater than its left and above neighbors, and less than its right and below neighbors.");
            gameOver = true;
            showGameOver("Game Over! Numbers are not in ascending order. Try again or Quit?");
        }
        int result = controller.placeNumber(row,
                                            col);

        updateGridDisplay();

        if(result != -1)
        {
            if(result == 1)
            {
                final String status = controller.checkGameStatus();
                if(status.equals("win"))
                {
                    statusLabel.setText("You Won!");
                    showGameOver(controller.getScore());
                }
            }
            else if(result == 2)
            {
                gameOver = true;
                showGameOver("Game Over! Impossible to place the next number: " + controller.getCurrentNumber() +
                             ". Try again or Quit?");
            }
            else if(result == 3)
            {
                gameOver = true;
                showGameOver("Game Over! Impossible to place the number: " + controller.getGrid()[row][col]+
                             " in slot " + ((row * COLS) + col + 1) + ". Try again or Quit?");
            }
        }
    }

    public void resetGame()
    {
        controller.startGame();
        gameOver = false;
        updateGridDisplay();
        statusLabel.setText("Place the numbers in ascending order.");
        tryAgainButton.setEnabled(false);
        quitButton.setEnabled(true);
    }

    public void returnToMenu()
    {
        frame.dispose();
        mainMenu.initializeMenu();
    }
}
