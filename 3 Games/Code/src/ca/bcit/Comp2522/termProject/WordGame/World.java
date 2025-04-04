package ca.bcit.Comp2522.termProject.WordGame;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Random;
import java.util.List;

/**
 * The World class manages a collection of countries for the Geography Trivia Game.
 * It loads country data from text files in the specified resource directory and provides
 * access to the country data through a HashMap.
 *
 * @author Sehaj Gill
 * @version 1.0
 */
public class World
{
    HashMap<String, Country> countries;
    public final  String       resourceDir;
    public final  List<String> fileNames;
    private final Random       random = new Random();


    /**
     * Constructs a new World instance with the specified resource directory and list of file names.
     *
     * @param resourceDir the directory containing the country data files
     * @param fileNames   the list of file names to load country data from
     */
    public World(String resourceDir,
                 List<String> fileNames)
    {
        this.resourceDir = resourceDir;
        this.fileNames   = fileNames;
        countries        = new HashMap<>();
    }

    /**
     * Loads country data from text files in the resource directory.
     * Each file is expected to be named [a-z].txt (excluding w.txt and x.txt) and contain
     * country data in the format "Country:Capital" followed by facts.
     * Skips files that do not exist, are empty, or have an incorrect format.
     */
    public void loadCountries()
    {
        String countryName;
        String countryCapital;

        for(char ch = 'a'; ch <= 'z'; ch++)
        {
            if(ch == 'w' || ch == 'x')
            {
                continue; // Skipping w and x
            }

            String filename = ch + ".txt";
            String filePath = Paths.get("Resources",
                                        filename).toString();

            try
            {
                if(!Files.exists(Paths.get(filePath)))
                {
                    System.out.println("File not found: Resources/" + filename);
                    continue;
                }

                List<String> lines = Files.readAllLines(Paths.get("Resources/",
                                                                  filename));

                if(lines.isEmpty())
                {
                    System.out.println("Skipping empty file: " + filename);
                    continue;
                }

                for(int i = 0; i < lines.size(); i++)
                {
                    String line = lines.get(i).trim();
                    if(line.isEmpty())
                    {
                        continue; // Skip blank lines
                    }

                    if(line.contains(":"))
                    {
                        String[] split = line.split(":");
                        if(split.length != 2)
                        {
                            System.out.println("Skipping file with incorrect format: " + filename);
                            break;
                        }

                        countryName    = split[0].trim();
                        countryCapital = split[1].trim();


                        StringBuilder facts = new StringBuilder();
                        int           j     = i + 1;
                        while(j < lines.size() && !lines.get(j).contains(":"))
                        {
                            facts.append(lines.get(j).trim()).append("\n");
                            j++;
                        }
                        i = j - 1;

                        String[] countryFacts = facts.toString().split("\n");

                        // Store country data
                        countries.put(countryName,
                                      new Country(countryName,
                                                  countryCapital,
                                                  countryFacts,
                                                  random));
                    }
                }
            }
            catch(IOException e)
            {
                System.out.println("Error reading file: " + filename + " - " + e.getMessage());
            }

        }
    }

    /**
     * Returns the HashMap containing all loaded countries, with country names as keys
     * and Country objects as values.
     *
     * @return the HashMap of countries
     */
    public HashMap<String, Country> getCountries()
    {
        return countries;
    }
}

