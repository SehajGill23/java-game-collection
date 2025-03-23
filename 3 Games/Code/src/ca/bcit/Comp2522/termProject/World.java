package ca.bcit.Comp2522.termProject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Random;
import java.util.List;


public class World
{
    HashMap<String, Country> countries;
    private final String       resourceDir;
    private final List<String> fileNames;
    private final Random       random = new Random();


    public World(String resourceDir,
                 List<String> fileNames)
    {
        this.resourceDir = resourceDir;
        this.fileNames   = fileNames;
        countries        = new HashMap<>();
    }

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

                List<String> lines = Files.readAllLines(Paths.get("Resources",
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
                            break;  // Exit this file's processing
                        }

                        countryName    = split[0].trim();
                        countryCapital = split[1].trim();

                        // Collect facts
                        StringBuilder facts = new StringBuilder();
                        int j = i + 1;
                        while(j < lines.size() && !lines.get(j).contains(":"))
                        {
                            facts.append(lines.get(j).trim()).append("\n");
                            j++;
                        }
                        i = j - 1; // Move to the last processed line

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

    public HashMap<String, Country> getCountries()
    {
        return countries;
    }
}

