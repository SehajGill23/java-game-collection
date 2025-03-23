package ca.bcit.Comp2522.termProject;

import java.util.Random;

public class Country
{
    private final String   name;
    private final String   capitalCityName;
    private final String[] facts;
    private final Random   random;

    public Country(final String name,
                   final String capitalCityName,
                   final String[] facts,
                  final Random random)
    {
        this.name            = name;
        this.capitalCityName = capitalCityName;
        this.facts           = facts;
        this.random          = random;
    }

    public String getName()
    {
        return name;
    }

    public String getCapitalCityName()
    {
        return capitalCityName;
    }

    public String getRandomFact()
    {
        return facts[random.nextInt(facts.length)];
    }
}
