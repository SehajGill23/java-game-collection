package ca.bcit.Comp2522.termProject;
import java.util.Random;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.List;
//import java.util.Scanner;
//import java.util.Arrays;


public class Country
{
    private  String name;
    private String capitalCityName;
    private  String[]  facts;

    public Country(String name, String capitalCityName, String[] facts) {
        this.name = name;
        this.capitalCityName = capitalCityName;
        this.facts = facts;
    }

    public String getName() {
        return name;
    }

    public String getCapitalCityName()
    {
        return capitalCityName;
    }

    public String[] getFacts()
    {
        return facts;
    }

    public String getRandomFact()
    {
        Random rand;
        rand = new Random();
        return facts[rand.nextInt(facts.length)];
    }
}
