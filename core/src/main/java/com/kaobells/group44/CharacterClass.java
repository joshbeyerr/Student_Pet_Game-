package com.kaobells.group44;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import java.util.Objects;

public class CharacterClass {

    private final Image characterImage;
    private final String name;

    // 0 through to 4
    private int characterNumber;
    // e.g relaxed, brave
    private String characterType;

    private String state;

    private float health;
    private float sleep;
    private float happiness;
    private float fullness;
    private float stress;

    // JUST FOR NOW - making all characters health change by 1
    // In the future this will be unique to each characterType and will be set when character is created.
    private float healthChange = 1.0f;
    private float sleepChange = 1.0f;
    private float happinessChange = 1.0f;
    private float fullnessChange = 1.0f;
    private float stressChange = 1.0f;

    private Item[] inventory;



    // Constructor
    public CharacterClass(String charName, Image charImage, int characterNumber, String characterType, Item[] inventory, String state) {
        this.name = charName;
        this.characterImage = charImage;
        this.inventory = inventory;
        this.characterNumber = characterNumber;
        this.characterType = characterType;
        this.state = state;

        // initialize character stats based on character type selected THIS WILL NEED TO CHANGE WHEN WE START IMPLEMENTING SAVE FILES
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

    public void setStress(float stress) {
        if (stress >= 0.0f && stress <= 100.0f) {
            this.stress = stress;
        } else {
            System.out.println("stress value must be between 0 and 100.");
        }
    }

    //method tries to use an item
    public void useItem(int index) {
        // if the player has the item in the inventory it gets the item values and reduces the count in inventory by 1 then updates the relivent stat
        if (inventory[index].reduceCount()){
            Item usedItem = inventory[index];
            if (Objects.equals(usedItem.itemStat, "fullness")){
                fullness = fullness+(usedItem.itemStatValue*fullnessChange);
            }
            else{
                happiness = happiness+(usedItem.itemStatValue*happinessChange);
            }
        }
        //need to throw an error for a player trying to use an item they don't have here
        else {
        }
    }

    public void gainItem(int index) {
        inventory[index].increaseCount();
    }

    public void statBarTick(){
        setHealth(this.getHealth() - healthChange);
        setHappiness(this.getHappiness() - happinessChange);
        setSleep(this.getSleep() - sleepChange);
        setHunger(this.getHunger() - fullnessChange);
        setStress(this.getStress() - stressChange);
    }

    // initialize character stats based on character type selected
    private void setUpCharacter(){
        //filling inventory with all the items with the count set to zero
        for (int i = 0; i < 6; i++) {
            this.inventory[i] = new Item(i);
        }

        switch (characterNumber) {

            // case 0 = relaxed
            case 0:

                setHealth(100.0f);
                setSleep(100.0f);
                setHappiness(100.0f);
                setHunger(100.0f);
                setStress(100.0f);

                break;

            // case 1 = quirky
            case 1:
                setHealth(90.0f);
                setSleep(80.0f);
                setHappiness(100.0f);
                setHunger(100.0f);
                setStress(80.0f);
                break;

            // case 2 = hasty
            case 2:
                setHealth(70.0f);
                setSleep(70.0f);
                setHappiness(80.0f);
                setHunger(80.0f);
                setStress(70.0f);
                break;


                // case 3 == brave
            case 3:
                setHealth(60.0f);
                setSleep(60.0f);
                setHappiness(70.0f);
                setHunger(70.0f);
                setStress(60.0f);
                break;

            // case 4 == serious
            case 4:
                setHealth(50.0f);
                setSleep(50.0f);
                setHappiness(50.0f);
                setHunger(50.0f);
                setStress(50.0f);
                break;

            default: throw new IllegalArgumentException("Invalid character index: " + characterNumber);
        }
    }
}
