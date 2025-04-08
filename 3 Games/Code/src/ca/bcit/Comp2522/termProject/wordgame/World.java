package ca.bcit.Comp2522.termProject.wordgame;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * The {@code World} class is responsible for managing the collection of countries used in the
 * Geography Trivia Game. It handles the crucial task of loading country-specific information
 * from text files located within a designated resource directory. This class parses these files,
 * extracting essential details such as the country's name, its capital city, and a list of
 * interesting facts associated with it. The loaded country data is then stored in a {@link HashMap}
 * for efficient access during gameplay.
 * <p>
 * The process of loading countries involves iterating through a set of text files, each named after
 * a lowercase letter of the alphabet (e.g., 'a.txt', 'b.txt', ..., 'z.txt'), except
 * 'w.txt' and 'x.txt', which are intentionally skipped. Each file is expected to contain country
 * entries formatted in a specific way: the first line of an entry should be in the format
 * "CountryName:CapitalCity", followed by subsequent lines containing individual facts about the
 * country. The parsing logic within this class is designed to handle this format, extracting the
 * country name and capital from the first line and accumulating the facts from the following lines
 * until the next country entry (indicated by another line containing a colon) or the end of the file
 * is reached.
 * </p>
 * <p>
 * During the loading process, the class performs several checks to ensure data integrity and robustness.
 * It verifies the existence of each file before attempting to read it and skips files that are found
 * to be empty. Additionally, it checks if the lines intended to define a country and its capital adhere
 * to the expected "Country:Capital" format. If a line containing a colon does not split into exactly
 * two parts, the entire file is skipped, and a warning message is logged. Blank lines within the data
 * files are also ignored.
 * </p>
 * <p>
 * For each successfully parsed country entry, a new {@link Country} object is created, encapsulating
 * the country's name, capital, and the array of facts. This {@code Country} object is then stored in
 * the {@code countries} HashMap, with the country's name serving as the key, allowing for quick retrieval
 * of country-specific information by its name during the game. Any {@link IOException} that occurs
 * during the file reading process is caught, and an error message indicating the file and the nature
 * of the error is printed to the console, ensuring that file reading issues do not halt the entire
 * game initialization process.
 * </p>
 * <p>
 * Once all the designated files have been processed, the {@code World} class provides a method,
 * {@link #getCountries()}, to retrieve the populated {@code HashMap} of countries. This allows other
 * parts of the game, such as the {@link WordGame} class, to easily access and utilize the loaded
 * country data for generating trivia questions and managing the game flow.
 * </p>
 *
 * @author Sehaj Gill
 * @version 1.0
 */
public class World
{
    private static final String CAPITAL_SEPARATOR        = ":";
    private static final String FACT_SEPARATOR           = "\n";
    private static final String RESOURCES_DIRECTORY      = "Resources";
    private static final String FILE_EXTENSION           = ".txt";
    private static final char   FIRST_LETTER             = 'a';
    private static final char   LAST_LETTER              = 'z';
    private static final char   SKIP_LETTER_W            = 'w';
    private static final char   SKIP_LETTER_X            = 'x';
    private static final int    EXPECTED_CAPITAL_PARTS   = 2;
    private static final String FILE_NOT_FOUND_MESSAGE   = "File not found: Resources/%s";
    private static final String SKIPPING_EMPTY_FILE      = "Skipping empty file: %s";
    private static final String INCORRECT_FORMAT_MESSAGE = "Skipping file with incorrect format: %s";
    private static final String ERROR_READING_FILE       = "Error reading file: %s - %s";
    private static final String NEW_LINE                 = "%n";
    private static final int    INITIAL_COUNTER          = 0;
    private static final int    CAPITAL_INDEX            = 1;
    private static final int    COUNTRY_INDEX            = 0;
    private static final int    FACT_START_INDEX         = 1;

    private final HashMap<String, Country> countries;
    private final String                   resourceDir;
    private final List<String>             fileNames;
    private final Random                   random = new Random();


    /**
     * Constructs a new {@code World} instance with the specified resource directory and list of file names.
     *
     * @param resourceDir the directory containing the country data files
     * @param fileNames   the list of file names to load country data from
     */
    public World(final String resourceDir,
                 final List<String> fileNames)
    {
        this.resourceDir = resourceDir;
        this.fileNames   = fileNames;
        this.countries   = new HashMap<>();
    }

    /**
     * Loads country data from text files in the resource directory. Each file is expected to be named
     * [a-z].txt (excluding w.txt and x.txt) and contain country data in the format "Country:Capital"
     * followed by facts. Skips files that do not exist, are empty, or have an incorrect format.
     */
    void loadCountries()
    {
        char          ch;
        Path          path;
        String        countryName;
        String        countryCapital;
        String        line;
        String        filename;
        String        filePath;
        StringBuilder facts;
        String[]      split;
        String[]      countryFacts;
        List<String>  lines;
        int           i;
        int           j;

        for(ch = FIRST_LETTER; ch <= LAST_LETTER; ch++)
        {
            if(ch == SKIP_LETTER_W || ch == SKIP_LETTER_X)
            {
                continue;
            }

            filename = ch + FILE_EXTENSION;
            path = Paths.get(RESOURCES_DIRECTORY,
                                  filename);
            filePath = path.toString();

            try
            {
                if(!Files.exists(Paths.get(filePath)))
                {
                    System.out.printf(FILE_NOT_FOUND_MESSAGE + NEW_LINE,
                                      filename);
                    continue;
                }

                lines = Files.readAllLines(path);

                if(lines.isEmpty())
                {
                    System.out.printf(SKIPPING_EMPTY_FILE + NEW_LINE,
                                      filename);
                    continue;
                }

                for(i = INITIAL_COUNTER; i < lines.size(); i++)
                {
                    line = lines.get(i).trim();
                    if(line.isEmpty())
                    {
                        continue;
                    }

                    if(line.contains(CAPITAL_SEPARATOR))
                    {
                        split = line.split(CAPITAL_SEPARATOR);
                        if(split.length != EXPECTED_CAPITAL_PARTS)
                        {
                            System.out.printf(INCORRECT_FORMAT_MESSAGE + NEW_LINE,
                                              filename);
                            break;
                        }

                        countryName    = split[COUNTRY_INDEX].trim();
                        countryCapital = split[CAPITAL_INDEX].trim();

                        facts = new StringBuilder();
                        j     = i + FACT_START_INDEX;
                        while(j < lines.size() && !lines.get(j).contains(CAPITAL_SEPARATOR))
                        {
                            facts.append(lines.get(j).trim()).append(FACT_SEPARATOR);
                            j++;
                        }
                        i = j - FACT_START_INDEX;

                        countryFacts = facts.toString().split(FACT_SEPARATOR);

                        countries.put(countryName,
                                      new Country(countryName,
                                                  countryCapital,
                                                  countryFacts,
                                                  random));
                    }
                }
            }
            catch(final IOException e)
            {
                System.out.printf(ERROR_READING_FILE + NEW_LINE,
                                  filename,
                                  e.getMessage());
            }
        }
    }

    /**
     * Returns the HashMap containing all loaded countries, with country names as keys and Country objects as values.
     *
     * @return the HashMap of countries
     */
    public HashMap<String, Country> getCountries()
    {
        return countries;
    }
}