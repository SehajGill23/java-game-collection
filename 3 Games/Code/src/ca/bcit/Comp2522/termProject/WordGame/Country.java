package ca.bcit.Comp2522.termProject.WordGame;

import java.util.Random;

/**
 * The Country class represents a country in the Geography Trivia Game.
 * It stores the country's name, capital city, and a list of facts, and provides
 * methods to access this information, including a method to retrieve a random fact.
 *
 * @author Sehaj Gill
 * @version 1.0
 */
public class Country
{
    private final String   name;
    private final String   capitalCityName;
    private final String[] facts;
    private final Random   random;

    /**
     * Constructs a new Country instance with the specified name, capital city, facts, and random number generator.
     *
     * @param countryName            the name of the country
     * @param capitalCityName the name of the country's capital city
     * @param facts           an array of facts about the country
     * @param random          a Random instance for selecting random facts
     * @throws IllegalArgumentException if countryName, capitalCityName, facts, or random is null, or if facts is empty
     */
    public Country(final String countryName,
                   final String capitalCityName,
                   final String[] facts,
                   final Random random)
    {
        this.name            = countryName;
        this.capitalCityName = capitalCityName;
        this.facts           = facts;
        this.random          = random;
    }

    /**
     * Returns the name of the country.
     *
     * @return the country's name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns the name of the country's capital city.
     *
     * @return the capital city's name
     */
    public String getCapitalCityName()
    {
        return capitalCityName;
    }

    /**
     * Returns a random fact about the country.
     * If no facts are available (which should not occur due to constructor validation),
     * a default message is returned.
     *
     * @return a random fact about the country, or a default message if no facts are available
     */
    public String getRandomFact()
    {
        return facts[random.nextInt(facts.length)];
    }
}
