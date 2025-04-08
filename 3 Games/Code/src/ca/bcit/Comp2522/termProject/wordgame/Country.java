package ca.bcit.comp2522.termproject.wordgame;

import java.util.Random;

/**
 * The {@code Country} class represents a country in the Geography Trivia Game.
 * <p>
 * It encapsulates information about a specific country, including its name,
 * the name of its capital city, and a collection of interesting facts.
 * This class provides methods to access these attributes and a mechanism
 * to retrieve a random fact from the stored list.
 * </p>
 * <p>
 * Instances of the {@code Country} class are immutable after creation, as
 * all its fields are final. This ensures that once a country object is
 * created with its details, those details cannot be changed, promoting
 * data integrity and thread safety.
 * </p>
 * <p>
 * The class utilizes a {@link Random} object, which is provided during
 * construction, to select a fact randomly when the {@link #getRandomFact()}
 * method is called. This allows for a varied and engaging trivia experience.
 * </p>
 *
 * @author Sehaj Gill
 * @version 1.0
 */
final class Country
{
    private final String   name;
    private final String   capitalCityName;
    private final String[] facts;
    private final Random   random;

    /*
     * Constructs a new {@code Country} instance with the specified details.
     * <p>
     * This constructor initializes the country's name, capital city, facts,
     * and the random number generator. It performs basic validation to ensure
     * that the provided arguments are valid.
     * </p>
     *
     * @param countryName     the name of the country. Must not be null or empty.
     * @param capitalCityName the name of the country's capital city. Must not be null or empty.
     * @param facts           an array of facts about the country. Must not be null and must contain at least one fact.
     * @param random          a {@link Random} instance to be used for selecting random facts. Must not be null.
     * <ul>
     * <li>{@code countryName} is null or empty.</li>
     * <li>{@code capitalCityName} is null or empty.</li>
     * <li>{@code facts} is null or empty.</li>
     * <li>{@code random} is null.</li>
     * </ul>
     */
    Country(final String countryName,
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
     * @return the country's name as a {@code String}.
     */
    public String getName()
    {
        return name;
    }

    /*
     * Returns the name of the country's capital city.
     *
     * @return the capital city's name as a {@code String}.
     */
    String getCapitalCityName()
    {
        return capitalCityName;
    }

    /*
     * Returns a randomly selected fact about the country.
     * <p>
     * This method uses the internal {@link Random} object to generate a random
     * index within the bounds of the {@code facts} array and returns the fact
     * at that index. The constructor ensures that the {@code facts} array is
     * not empty, so this method is guaranteed to return a valid fact.
     * </p>
     *
     * @return a random fact about the country as a {@code String}.
     */
    String getRandomFact()
    {
        return facts[random.nextInt(facts.length)];
    }
}
