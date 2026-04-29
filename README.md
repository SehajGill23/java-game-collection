# 3 Games

A Java term project containing three playable games launched from one main menu:

- **Word Game**: a console-based geography trivia game.
- **Number Game**: a JavaFX grid game where numbers must be placed in ascending order.
- **Letter Rush**: a JavaFX word-building game with moving obstacles, levels, scoring, themes, and high-score tracking.

The project was built for BCIT COMP 2522 and uses object-oriented Java design with separate packages for each game mode.

## Features

### Main Menu

- Console menu for choosing a game mode.
- Starts JavaFX in the background for the GUI-based games.
- Options to launch Word Game, Number Game, Letter Rush, or quit.

### Word Game

- Asks 10 random geography trivia questions per round.
- Questions include:
  - country to capital
  - capital to country
  - country facts
- Gives the player two attempts per question.
- Awards more points for first-attempt answers.
- Tracks game statistics and score history.

### Number Game

- JavaFX-based 4x5 number placement game.
- Generates 20 random numbers from 1 to 1000.
- Player must place each number so the grid stays in ascending order from left to right and top to bottom.
- Tracks wins, losses, placements, and average placements per game.

### Letter Rush

- JavaFX-based word game.
- Player clicks letters to form target words before time runs out.
- Includes hidden bonus words for extra points.
- Moving obstacles include missiles, bombs, spikes, and cacti.
- Multiple levels, theme switching, score tracking, and high-score persistence.

## Project Structure

```text
3 Games/
├── Code/
│   └── src/
│       └── ca/bcit/comp2522/termproject/
│           ├── wordgame/
│           ├── numbergame/
│           └── letterrushgame/
├── Resources/
│   ├── wordgame/
│   ├── numbergame/
│   └── letterrush/
└── Test/
    ├── ScoreTest.java
    └── LetterRushTest.java
```

## Requirements

- Java 23, based on the IntelliJ project configuration.
- JavaFX SDK configured in the IDE or runtime environment.
- JUnit 5 for running the included tests.
- IntelliJ IDEA is recommended because the project includes `.iml` module files.

## How to Run

1. Open the project in IntelliJ IDEA.
2. Make sure the project SDK is set to Java 23.
3. Add JavaFX to the project if your JDK does not bundle it.
4. Set the working directory to:

```text
3 Games
```

5. Run the main class:

```text
ca.bcit.comp2522.termproject.wordgame.Main
```

6. Use the console menu:

```text
W - Word Game
N - Number Game
M - Letter Rush
Q - Quit
```

## Resources

The games depend on files in the `Resources` directory:

- `Resources/wordgame/textfiles/` contains country and score data.
- `Resources/numbergame/css/` contains Number Game styling.
- `Resources/letterrush/txtfiles/` contains words, prompts, applications, and high-score data.
- `Resources/letterrush/images/` contains Letter Rush backgrounds and obstacle sprites.
- `Resources/letterrush/css/` contains Letter Rush styling.

If resources do not load, check that the working directory is set to `3 Games`.

## Tests

The project includes JUnit tests for:

- Word Game score calculation and score-file handling.
- Letter Rush player logic, word completion, bonus words, incorrect clicks, and high-score behavior.

Tests are located in:

```text
3 Games/Test
```

## Notes

- The GUI games use JavaFX, so JavaFX must be available when running the project.
- Score and high-score files are stored under the `Resources` folder.
- The project currently uses IntelliJ module configuration instead of Maven or Gradle.
