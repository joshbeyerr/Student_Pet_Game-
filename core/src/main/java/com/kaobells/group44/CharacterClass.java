package com.kaobells.group44;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Timer;

import java.util.HashMap;
import java.util.Map;

public class CharacterClass {
    private transient final Main mainGame;
    private final String name;

    // 0 through to 4
    private final int characterNumber;
    // e.g relaxed, brave
    private final String characterType;

    private State state = State.NEUTRAL;

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

    private transient Item[] inventory;

    private transient Image currentHead;
    private transient Image currentBody;

    private transient final Map<String, Image> characterHeads;
    private transient final Map<String, Image> characterBodies;

    // a button has been clicked, no other animations or buttons are allowed to be clicked during this
    private transient Timer.Task blinkTask;

    //will be used when mini-game is running to halt all changes to scores and stats
    private boolean isGameRunning;


    // instead of vetCoolDown
    private float doctorCooldownRemaining = 0; // Remaining cooldown time in seconds
    private float playCooldownRemaining = 0; // Remaining cooldown time in seconds
    private float actionBlockCooldownRemaining  = 0;


    // Add default constructor for LibGDX Json Loader
    public CharacterClass() {
        this.name = "DefaultName";
        this.state = null;
        this.health = 100.0f;
        this.sleep = 100.0f;
        this.happiness = 100.0f;
        this.fullness = 100.0f;
        this.stress = 0.0f;
        this.mainGame = null;
        characterHeads = null;
        characterBodies = null;
        characterNumber = 100;
        characterType = "default";
    }


    // Constructor with default state (NEUTRAL)
    public CharacterClass(Main mainGameSession, String charName, int characterNumber, String characterTypeStr, Item[] inventory) {
        this(mainGameSession, charName, characterNumber, characterTypeStr, inventory, State.NEUTRAL);
    }

    // Constructor
    public CharacterClass(Main mainGameSession, String charName, int characterNumber, String characterTypeStr, Item[] inventory, State state) {
        this.mainGame = mainGameSession;

        this.name = charName;
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

    // Getter for name
    public String getName() { return name;}

    // Getter and Setter for health
    public float getHealth() { return health;}
    //Setter for health
    public void setHealth(float health) {
        if (health >= 0.0f && health <= 100.0f) {
            this.health = health;
        } else {
            System.out.println("Health value must be between 0 and 100.");
        } }

    // Getter for happiness
    public float getHappiness() { return happiness;}
    //Setter for happiness
    public void setHappiness(float happiness) {
        if (happiness >= 0.0f && happiness <= 100.0f) {
            this.happiness = happiness;
        } else {
            System.out.println("Happiness value must be between 0 and 100.");
        } }

    // Getter for hunger
    public float getHunger() { return fullness; }
    // Setter for hunger
    public void setHunger(float hunger) {
        if (hunger >= 0.0f && hunger <= 100.0f) {
            this.fullness = hunger;
        } else {
            System.out.println("fullness value must be between 0 and 100.");
        } }
    // Getter for sleep
    public float getSleep() { return sleep; }
    // Setter for sleep
    public void setSleep(float sleep) {
        if (sleep >= 0.0f && sleep <= 100.0f) {
            this.sleep = sleep;
        } else {
            System.out.println("sleep value must be between 0 and 100.");
        } }

    // Getter for stress
    public float getStress() { return stress; }
    // Setter for stress
    public void setStress(float stress) {
        if (stress >= 0.0f && stress <= 100.0f) {
            this.stress = stress;
        } else {
            System.out.println("stress value must be between 0 and 100.");
        } }


    public void statBarTick(){
        setHappiness(this.getHappiness() - happinessChange);
        setSleep(this.getSleep() - sleepChange);
        setHunger(this.getHunger() - fullnessChange);
        setStress(this.getStress() - stressChange);
    }

    // initialize character stats based on character type selected
    private void setUpCharacter(){
        //filling inventory with all the items with the count set to zero
        for (int i = 0; i < 6; i++) {
            this.inventory[i] = new Item(i,0);
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

    //Head Getter
    public Image getHead() { return currentHead; }

    //Head Setter
    public void setHead(Image newHead) {
        if (!actionBlocked()) { // Only allow setting the head if actions aren't blocked
            this.currentHead = newHead;
        }
    }

    // Overriding set head if action block flag is set
    public void setHead(Image newHead, boolean Override) {
        this.currentHead = newHead;
    }

    //Body Getter
    public Image getBody() { return currentBody; }

    //Body Setter
    public void setBody(Image newBody) {
        if (!actionBlocked()) { // Only allow setting the body if actions aren't blocked
            this.currentBody = newBody;
        }
    }

    // Overriding set body if action block flag is set
    public void setBody(Image newBody, boolean Override) {
        this.currentBody = newBody;
    }



    private Image headDetermine() {
        stateDetermine(); // Update the state before determining the head image

        switch (state) {
            case DEAD:
                return characterHeads.get("angry"); // CHANGE TO DEAD IF/WHEN WE HAVE DEAD HEAD SPRITE
            case SLEEPING:
                return characterHeads.get("angry"); // CHANGE TO SLEEPING IF/WHEN WE HAVE SLEEPING HEAD SPRITE
            case ANGRY:
                return characterHeads.get("angry");
            case HUNGRY:
                return characterHeads.get("angry"); // CHANGE TO HUNGRY IF/WHEN WE HAVE HUNGRY HEAD SPRITE
            default:
                return characterHeads.get("head");
        }
    }

    private Image bodyDetermine() {
        stateDetermine(); // Update the state before determining the body image

        switch (state) {
            case DEAD:
                return characterBodies.get("hungry1"); // CHANGE TO DEAD IF/WHEN WE HAVE DEAD BODY SPRITE
            case SLEEPING:
                return characterBodies.get("hungry1"); // CHANGE TO SLEEPING IF/WHEN WE HAVE SLEEPING BODY SPRITE
            case ANGRY:
                return characterBodies.get("hungry1"); // CHANGE TO ANGRY IF/WHEN WE HAVE ANGRY BODY SPRITE
            case HUNGRY:
                return characterBodies.get("hungry1");
            default:
                return characterBodies.get("neutral");
        }
    }



    public void stateDetermine() {
        if (getHealth() < 1.0f) {
            this.state = State.DEAD;
            crashedOut();
        } else if (getSleep() < 1.0f) {
            this.state = State.SLEEPING;
            goToSleep();
        } else if (getHappiness() < 1.0f) {
            this.state = State.ANGRY;
        } else if (getHunger() < 1.0f) {
            this.state = State.HUNGRY;
        } else {
            this.state = State.NEUTRAL;
        }
    }

    //State Getter
    public State getState(){ return state;}

    public void goToSleep(){
        /*
        placeholder for eventual sleep state function. Guideline for this is: "if triggered by Sleep hitting zero a health penalty is applied
        (as opposed to the goToSleep action the player can use), and the pet will fall asleep and can no longer be interacted with.
        In the sleeping state the sleep value will slowly increases until it hits the maximum value. Once
        the max is reached, the pet wakes and returns to its normal state. During the sleeping state the other statistics still decline normally."
         */
        if(getSleep() < 1.0f){
            setHealth(Math.min(0.0f, (getHealth()-10.0f)));
        }
        System.out.println("he just like me fr");
    }

    //palceholder function see comment
    public void crashedOut(){
        /*
        placeholder for eventual dead pet function. Will need to:
         1- change pet head/body to dead (or replace with a tombstone sprite)
         2- inform user their pet is dead and that they should now start a new game or load another save file
         */
    }


    public void startCharacter() {
        if (blinkTask != null) {
            blinkTask.cancel(); // Cancel existing task if it's running
        }

        float blinkInterval = 3.0f; // Blink every 3 seconds
        float blinkDuration = 0.5f; // Blink lasts for 0.5 seconds

        float hungerDuration = 1.5f;

        blinkTask = Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                if (!actionBlocked()) {

                    stateDetermine();

                    if (getState() == State.HUNGRY){

                        Timer.schedule(new Timer.Task() {
                            @Override
                            public void run() {
                                if (!actionBlocked()) {
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
                            if (!actionBlocked()) {
                                setHead(headDetermine());
                                setBody(bodyDetermine()); // Revert to normal head
                            }
                        }
                    }, blinkDuration); // Blink lasts for blinkDuration seconds
                }
            }
        }, blinkInterval, blinkInterval); // Repeat every blinkInterval seconds
    }

    public boolean actionBlocked(){
        return actionBlockCooldownRemaining != 0;
    }

    public void updateActionBlock(float deltaTime) {
        if (actionBlockCooldownRemaining > 0) {
            actionBlockCooldownRemaining = Math.max(0, actionBlockCooldownRemaining - deltaTime);
        }
    }

    public void resumeDefaultCharacterState(float duration){
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                // Reset to the normal head and body
                setHead(headDetermine(), true); // Set normal head
                setBody(bodyDetermine(), true); // Set normal body
            }
        }, duration); // Reset after the 5-second exercise duration
    }


    public void feedTriggered(Item selectedItem) {
        if(!actionBlocked() && (getState() != State.ANGRY)) {
            if (selectedItem.getItemCount() > 0){
                selectedItem.reduceCount();
                this.fullness = Math.max(100.0f,getHunger() + (selectedItem.getItemStatValue()*fullnessChange));
                feedVisual();
            }
            else{
                //Code to display that you do not have any of that item
            }
        }
        else{
            //Code to say that you cannot feed pet while in the state they are in
        }
    }

    public void feedVisual() {
        if (!actionBlocked()) {

            float actionLength = 5.0f;

            setHead(characterHeads.get("happy"));;

            actionBlockCooldownRemaining = (actionLength);

            resumeDefaultCharacterState(actionLength);
        }
    }


    public void exercise(){
        if(!actionBlocked() && (getState() != State.ANGRY)) {
            //Update Stats
            this.fullness = Math.max(0.0f, getHunger() - 10.0f);
            this.sleep = Math.max(0.0f, getSleep() - 10.0f);
            this.health = Math.min(100.0f, getHealth() + 20.0f);

            float actionLength = 5.0f;
            actionBlockCooldownRemaining = (actionLength + 1);

            setHead(characterHeads.get("exercise"));
            setBody(characterBodies.get("workout1"));

            Timer.schedule(new Timer.Task() {
                boolean toggle = true; // Track which body to show

                @Override
                public void run() {
                    if (actionBlockCooldownRemaining < 1) {
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
            resumeDefaultCharacterState(actionLength);
        }
        else{
            //Code to say that you cannot exercise pet while in the state they are in
        }
    }

    public void playVisual(){
        //tbd
    }

    public void play(){
        if(!actionBlocked()){
            if(!(playCooldownRemaining > 0)){
                this.happiness = Math.min(100.0f, getHappiness() + 20.0f);
                playVisual();

                playCooldownRemaining = 30.0f;

                actionBlockCooldownRemaining = (5f);
            }
            else{
                //code to tell player that play is on cooldown
            }
        }
        else{
            //Code to say that you cannot play with pet while in the state they are in
        }
    }

    public boolean takeToDoctor(){
        if(!actionBlocked() && (getState() != State.ANGRY)){
            if(!(doctorCooldownRemaining > 0)){
                float actionLength = 3.0f;

                setHead(headDetermine()); // Set normal head
                setBody(bodyDetermine()); // Set normal body

                doctorCooldownRemaining = 30.0f; // Reset cooldown
                this.health = Math.min(100.0f, getHealth() + 20.0f);

                actionBlockCooldownRemaining = (actionLength);

                resumeDefaultCharacterState(actionLength);
                return true;
            }
            else{
                return false;
                //code to tell player that take to vet is on cooldown
            }
        }
        else{
            return false;
            //Code to say that you cannot take pet to vet while in the state they are in
        }
    }

    public void updateCooldowns(float deltaTime) {
        if (doctorCooldownRemaining > 0) {
            doctorCooldownRemaining = Math.max(0, doctorCooldownRemaining - deltaTime);
        }
        if (playCooldownRemaining > 0){
            playCooldownRemaining = Math.max(0, playCooldownRemaining - deltaTime);
        }
    }

    public void feed(int inventoryIndex){
        if(inventory[inventoryIndex].reduceCount()){
            this.fullness = Math.min(100.0f, getHunger() + (inventory[inventoryIndex].getItemStatValue()*fullnessChange));
        }
        else{
            //need to throw an error for a player trying to use an item they don't have here
        }
    }

    public void giveGift(int inventoryIndex){
        if(inventory[inventoryIndex].reduceCount()){
            this.fullness = Math.min(100.0f, getHappiness() + (inventory[inventoryIndex].getItemStatValue()*happinessChange));
        }
        else{
            //need to throw an error for a player trying to use an item they don't have here
        }
    }

    public void gainItem(int index) {
        inventory[index].increaseCount();
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

    public enum State {
        DEAD, SLEEPING, ANGRY, HUNGRY, NEUTRAL
    }
}
