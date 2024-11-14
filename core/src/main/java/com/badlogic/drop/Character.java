package com.badlogic.drop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class Character {

    private final Image characterImage;
    private final String name;

    // 0 through to 4
    private int characterNumber;
    // e.g relaxed, brave
    private String characterType;

    private int health;
    private int sleep;
    private int happiness;
    private int fullness;
    private int stress;

    private int healthChange;
    private int sleepChange;
    private int happinessChange;
    private int fullnessChange;
    private int stressChange;

    // Constructor
    public Character(String charName, Image charImage, int characterNumber, String characterType) {
        this.name = charName;
        this.characterImage = charImage;

        this.characterNumber = characterNumber;
        this.characterType = characterType;

        // initialize character stats based on character type selected
        setUpCharacter();

        Gdx.app.log("NAME", "health: " + health + "\nsleep: " + sleep + "\nhappiness: " + happiness + "\nfullness: " + fullness + "\nstress: " + stress);
    }

    public Image getImage() {
        return characterImage;
    }

    // Getter for name
    public String getName() {
        return name;
    }

    // Getter and Setter for health
    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        if (health >= 0 && health <= 100) {
            this.health = health;
        } else {
            System.out.println("Health value must be between 0 and 100.");
        }
    }

    // Getter and Setter for happiness
    public int getHappiness() {
        return happiness;
    }

    public void setHappiness(int happiness) {
        if (happiness >= 0 && happiness <= 100) {
            this.happiness = happiness;
        } else {
            System.out.println("Happiness value must be between 0 and 100.");
        }
    }

    // Getter and Setter for hunger
    public int getHunger() {
        return fullness;
    }

    public void setHunger(int hunger) {
        if (hunger >= 0 && hunger <= 100) {
            this.fullness = hunger;
        } else {
            System.out.println("fullness value must be between 0 and 100.");
        }
    }

    public int getSleep() {
        return sleep;
    }

    public void setSleep(int sleep) {
        if (sleep >= 0 && sleep <= 100) {
            this.sleep = sleep;
        } else {
            System.out.println("sleep value must be between 0 and 100.");
        }
    }

    public int getStress() {
        return stress;
    }

    public void setStress(int stress) {
        if (stress >= 0 && stress <= 100) {
            this.stress = stress;
        } else {
            System.out.println("stress value must be between 0 and 100.");
        }
    }

    // initialize character stats based on character type selected
    private void setUpCharacter(){
        switch (characterNumber) {

            // case 0 = relaxed
            case 0:

                setHealth(100);
                setSleep(100);
                setHappiness(100);
                setHunger(100);
                break;

            // case 1 = quirky
            case 1:
                setHealth(90);
                setSleep(80);
                setHappiness(100);
                setHunger(100);
                break;

            // case 2 = hasty
            case 2:
                setHealth(70);
                setSleep(70);
                setHappiness(80);
                setHunger(80);
                break;


                // case 3 == brave
            case 3:
                setHealth(60);
                setSleep(60);
                setHappiness(70);
                setHunger(70);
                break;

            // case 4 == serious
            case 4:
                setHealth(50);
                setSleep(50);
                setHappiness(50);
                setHunger(50);
                break;

            default: throw new IllegalArgumentException("Invalid character index: " + characterNumber);
        }
    }
}
