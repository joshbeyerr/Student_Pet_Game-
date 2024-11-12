package com.badlogic.drop;

import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class Character {

    private final Image characterImage;
    private final String name;
    private int health = 100;
    private int happiness = 100;
    private int hunger = 100;

    // Constructor
    public Character(String charName, Image charImage) {
        this.name = charName;
        this.characterImage = charImage;
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
        return hunger;
    }

    public void setHunger(int hunger) {
        if (hunger >= 0 && hunger <= 100) {
            this.hunger = hunger;
        } else {
            System.out.println("Hunger value must be between 0 and 100.");
        }
    }
}
