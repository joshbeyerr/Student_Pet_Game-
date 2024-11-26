package com.kaobells.group44;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class CharacterClass {

    private final Image characterImage;
    private final String name;

    // 0 through to 4
    private int characterNumber;
    // e.g relaxed, brave
    private String characterType;

    private int state;

    private float health;
    private float sleep;
    private float happiness;
    private float fullness;
    private float stress;

    private float healthChangeMultiplier;
    private float sleepChangeMultiplier;
    private float happinessChangeMultiplier;
    private float fullnessChangeMultiplier;
    private float stressChangeMultiplier;



    // Constructor
    public CharacterClass(String charName, Image charImage, int characterNumber, String characterType) {
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
    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        if (health >= 0.0f && health <= 100.0f) {
            this.health = health;
        }
        else {
            System.out.println("Health value must be between 0 and 100.");
        }
    }

    // Getter and Setter for happiness
    public float getHappiness() {
        return happiness;
    }

    public void setHappiness(float happiness) {
        if (happiness >= 0.0f && happiness <= 100.0f) {
            this.happiness = happiness;
        } else {
            System.out.println("Happiness value must be between 0 and 100.");
        }
    }

    // Getter and Setter for hunger
    public float getHunger() {
        return fullness;
    }

    public void setHunger(float hunger) {
        if (hunger >= 0.0f && hunger <= 100.0f) {
            this.fullness = hunger;
        } else {
            System.out.println("fullness value must be between 0 and 100.");
        }
    }

    public float getSleep() {
        return sleep;
    }

    public void setSleep(float sleep) {
        if (sleep >= 0.0f && sleep <= 100.0f) {
            this.sleep = sleep;
        } else {
            System.out.println("sleep value must be between 0 and 100.");
        }
    }

    public float getStress() {
        return stress;
    }

    public void setStress(int stress) {
        if (stress >= 0.0f && stress <= 100.0f) {
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

                setHealth(100.0f);
                setSleep(100.0f);
                setHappiness(100.0f);
                setHunger(100.0f);
                break;

            // case 1 = quirky
            case 1:
                setHealth(90.0f);
                setSleep(80.0f);
                setHappiness(100.0f);
                setHunger(100.0f);
                break;

            // case 2 = hasty
            case 2:
                setHealth(70.0f);
                setSleep(70.0f);
                setHappiness(80.0f);
                setHunger(80.0f);
                break;


                // case 3 == brave
            case 3:
                setHealth(60.0f);
                setSleep(60.0f);
                setHappiness(70.0f);
                setHunger(70.0f);
                break;

            // case 4 == serious
            case 4:
                setHealth(50.0f);
                setSleep(50.0f);
                setHappiness(50.0f);
                setHunger(50.0f);
                break;

            default: throw new IllegalArgumentException("Invalid character index: " + characterNumber);
        }
    }
}
