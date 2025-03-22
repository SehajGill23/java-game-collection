package ca.bcit.Comp2522.termProject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class World
{
    HashMap <String, Country> countries;

    public World()
    {
        countries = new HashMap<>();
    }

    public void loadCountries()
    {
        String filename;
        List<String> lines;
        String countryName;
        String countryCapital;
        String [] countryFacts;
        String [] splitCountryCapital;

        for (char ch = 'a'; ch <= 'z'; ch++)
        {
            if(ch == 'w' || ch == 'x') continue;
            filename = ch + ".txt";
            try
            {
                lines = Files.readAllLines(Paths.get(filename));
                if (lines.size() < 3) continue;
                splitCountryCapital = lines.get(0).split(":");

                if (splitCountryCapital.length != 2) continue;
                countryName = splitCountryCapital[0].trim();
                countryCapital = splitCountryCapital[1].trim();
                countryFacts = lines.subList(1, lines.size()).toArray(new String[0]);
                Country country = new Country(countryName, countryCapital, countryFacts);
                countries.put(countryName, country);
            }
            catch(IOException e)
            {
                System.out.println("Error reading file: " + filename);
            }
        }
    }

    public void addCountry(Country country)
    {
        countries.put(country.getName(), country);
    }

}
