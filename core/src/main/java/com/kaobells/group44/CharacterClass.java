package com.kaobells.group44;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Timer;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CharacterClass {
    private final Main mainGame;

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

    private Image currentHead;
    private Image currentBody;

    private Map<String, Image> characterHeads;
    private Map<String, Image> characterBodies;

    // a button has been clicked, no other animations or buttons are allowed to be clicked during this
    private Timer.Task blinkTask;
    private boolean isActionBlocked = false; // Blocks other actions


    // Constructor
    public CharacterClass(Main mainGameSession, String charName, Image charImage, int characterNumber, String characterTypeStr, Item[] inventory, String state) {
        this.mainGame = mainGameSession;

        this.name = charName;
        this.characterImage = charImage;
        this.inventory = inventory;
        this.characterNumber = characterNumber;
        this.characterType = characterTypeStr.toLowerCase();
        this.state = state;

        characterHeads = new HashMap<>();
        characterBodies = new HashMap<>();

        // initialize character stats based on character type selected THIS WILL NEED TO CHANGE WHEN WE START IMPLEMENTING SAVE FILES
        loadImages();

        setUpCharacter();

        startCharacter();

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
                setHunger(25.0f);
                setStress(50.0f);
                break;

            default: throw new IllegalArgumentException("Invalid character index: " + characterNumber);
        }

        setHead(headDetermine());
        setBody(bodyDetermine());
    }

    public Image getHead() {
        return currentHead;
    }

    public void setHead(Image newHead) {
        if (!isActionBlocked) { // Only allow setting the head if actions aren't blocked
            this.currentHead = newHead;
        }
    }

    public Image getBody() {
        return currentBody;
    }

    public void setBody(Image newBody) {
        if (!isActionBlocked) { // Only allow setting the body if actions aren't blocked
            this.currentBody = newBody;
        }
    }

    // Overriding set body if action block flag is set
    public void setBody(Image newBody, boolean Override) {
        this.currentBody = newBody;
    }

    private Image headDetermine(){
        if (getHunger() < 20 || getHappiness() < 20 || getHealth() < 20 || getSleep() < 20 || getStress() < 20){
            return characterHeads.get("angry");
        }
        else{
            return characterHeads.get("head");
        }
    }
    private Image bodyDetermine(){
        if (getHunger() < 20){
            return characterBodies.get("hungry1");
        }
        else{
            return characterBodies.get("neutral");
        }
    }

    public String getState(){
        return state;
    }


    public void startCharacter() {
        if (blinkTask != null) {
            blinkTask.cancel(); // Cancel existing task if it's running
        }

        float blinkInterval = 3f; // Blink every 3 seconds
        float blinkDuration = 0.5f; // Blink lasts for 0.5 seconds

        float hungerDuration = 1.5f;

        blinkTask = Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                if (!isActionBlocked) {

                    setBody(bodyDetermine());

                    if (getHunger() < 20){
                        Timer.schedule(new Timer.Task() {
                            @Override
                            public void run() {
                                if (!isActionBlocked) {
                                    setBody(characterBodies.get("hungry2")); // Revert to normal head
                                    setHead(headDetermine());
                                }
                            }
                        }, hungerDuration);

                    }

                    // Set to blinking head
                    setHead(characterHeads.get("blink"));

                    // Schedule reverting to normal head after blinkDuration
                    Timer.schedule(new Timer.Task() {
                        @Override
                        public void run() {
                            if (!isActionBlocked) {
                                setHead(headDetermine()); // Revert to normal head
                            }
                        }
                    }, blinkDuration); // Blink lasts for blinkDuration seconds
                }
            }
        }, blinkInterval, blinkInterval); // Repeat every blinkInterval seconds
    }

    public void blockActions(float duration) {
        isActionBlocked = true; // Block actions

        // Schedule unblock after the specified duration
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                isActionBlocked = false; // Unblock actions
            }
        }, duration);
    }

    public void returnCharacterState(float duration){
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                // Reset to the normal head and body
                setHead(headDetermine()); // Set normal head
                setBody(bodyDetermine()); // Set normal body
            }
        }, duration); // Reset after the 5-second exercise duration
    }

    public void feed() {

        if (!isActionBlocked) {

            float actionLength = 5.0f;

            setHead(characterHeads.get("happy"));;
            blockActions(actionLength);
            returnCharacterState(actionLength);
        }
    }

    public void exercise(){

        if (!isActionBlocked){
            // Set the "feed" head
            setHead(characterHeads.get("exercise"));
            setBody(characterBodies.get("workout1"));

            float actionLength = 5.0f;

            blockActions(actionLength);

            Timer.schedule(new Timer.Task() {
                boolean toggle = true; // Track which body to show

                @Override
                public void run() {
                    if (!isActionBlocked) {
                        // Stop the task when actions are unblocked (after 5 seconds)
                        this.cancel();
                        return;
                    }

                    // Toggle between workout1 and workout2
                    if (toggle) {
                        setBody(characterBodies.get("workout1"), true);
                    } else {
                        setBody(characterBodies.get("workout2"), true);
                    }
                    toggle = !toggle; // Switch the toggle state
                }
            }, 0, 0.5f); // Start immediately, repeat every 0.5 seconds

            // important
            returnCharacterState(actionLength);
        }

    }


    public void loadImages(){

        characterBodies.put("neutral", mainGame.createImage(new Texture(Gdx.files.internal("game/character/body-neutral.png"))));
        characterBodies.put("workout1", mainGame.createImage(new Texture(Gdx.files.internal("game/character/body-workout1.png"))));
        characterBodies.put("workout2", mainGame.createImage(new Texture(Gdx.files.internal("game/character/body-workout2.png"))));
        characterBodies.put("hungry1", mainGame.createImage(new Texture(Gdx.files.internal("game/character/body-hungry1.png"))));
        characterBodies.put("hungry2", mainGame.createImage(new Texture(Gdx.files.internal("game/character/body-hungry2.png"))));

        characterHeads.put("head", mainGame.createImage(new Texture(Gdx.files.internal("game/character/" + characterType + "-head.png"))));
        characterHeads.put("blink", mainGame.createImage(new Texture(Gdx.files.internal("game/character/" + characterType + "-blink.png"))));
        characterHeads.put("exercise", mainGame.createImage(new Texture(Gdx.files.internal("game/character/" + characterType + "-exercise.png"))));
        characterHeads.put("happy", mainGame.createImage(new Texture(Gdx.files.internal("game/character/" + characterType + "-happy.png"))));
        characterHeads.put("angry", mainGame.createImage(new Texture(Gdx.files.internal("game/character/" + characterType + "-angry.png"))));

    }
}
