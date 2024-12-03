package com.kaobells.group44;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Timer;

import java.beans.Transient;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CharacterClass {
    private transient Main mainGame;
    private final String name;

    // slot 1 through 3;
    private String slot;
    private int score;

    // 0 through to 4
    private final int characterNumber;
    // e.g relaxed, brave
    private final String characterType;
    //"prime state" meaning it is the state highest in the priority and the one that the character will display as
    private State state = State.NEUTRAL;
    //current stat values for a character
    private float health;
    private float sleep;
    private float happiness;
    private float fullness;
    private float stress;

    //variables uses as multipliers for tick rates
    private transient float healthChange;
    private transient float sleepChange;
    private transient float happinessChange;
    private transient float fullnessChange;
    private transient float stressChange = 1.0f;

    private Item[] inventory;

    //displayed sprite head and body
    private transient Image currentHead;
    private transient Image currentBody;
    private transient Sound munchSound;
    private transient Sound quackSound;

    //Hashmap of the heads and bodies
    private transient Map<String, Image> characterHeads;
    private transient Map<String, Image> characterBodies;
    private transient final Map<String, Texture> characterTextures = new HashMap<>();

    // a button has been clicked, no other animations or buttons are allowed to be clicked during this
    private transient Timer.Task blinkTask;

    //will be used when mini-game is running to halt all changes to scores and stats
    private boolean isGameRunning;
    //Array holding booleans representing the states that can have compounding effects with other states (sleeping,angry,hungry).
    private boolean[] compoundingStates;


    // instead of vetCoolDown
    private float doctorCooldownRemaining = 0; // Remaining cooldown time in seconds
    private float playCooldownRemaining = 0; // Remaining cooldown time in seconds
    private float actionBlockCooldownRemaining  = 0;
    private float saveTimer = 30f; // starts off at 30 so that game saves right when character is created

    private transient float blinkTimer = 0f;
    private float blinkDurationTimer = 0f;
    private float hungerDurationTimer = 0f;
    private boolean isBlinking = false;
    private boolean isHungry1 = true;

    private final float blinkInterval = 3.0f; // Blink every 3 seconds
    private final float blinkDuration = 0.5f; // Blink lasts for 0.5 seconds
    private final float hungerDuration = 1.5f; // Hunger effect lasts for 1.5 seconds
    private final float deadDuration = 100.0f;
    private float sleepTimer = 0f; // Tracks time for sleep animation
    private boolean isSleepState1 = true; // Tracks which sleep state is active
    private float hungerTimer = 0f;


    // Add default constructor for LibGDX Json Loader
    public CharacterClass() {
        this.name = "DefaultName";
        this.state = null;
        this.health = 100.0f;
        this.sleep = 100.0f;
        this.happiness = 100.0f;
        this.fullness = 100.0f;
        this.stress = 100.0f;
        this.mainGame = null;
        characterHeads = null;
        characterBodies = null;
        characterNumber = 100;
        characterType = "default";
    }
    //load in a character's data
    public void startLoadCharacter(Main mainG){
        mainGame = mainG;
        characterHeads = new HashMap<>();
        characterBodies = new HashMap<>();

        loadImages();

        setUpInventory();
        setHead(headDetermine());
        setBody(bodyDetermine());

        modifyModifiers(characterNumber);

        Gdx.app.log("NAME", "health: " + health + "\nsleep: " + sleep + "\nhappiness: " + happiness + "\nfullness: " + fullness + "\nstress: " + stress);
    }


    // Constructor with default state (NEUTRAL)
    public CharacterClass(Main mainGameSession, String charName, int characterNumber, String characterTypeStr, Item[] inventory, boolean[] compoundingStates, String slotNumber, int characterScore) {
        this(mainGameSession, charName, characterNumber, characterTypeStr, inventory, State.NEUTRAL, compoundingStates, slotNumber, characterScore);
    }

    // Constructor
    public CharacterClass(Main mainGameSession, String charName, int characterNumber, String characterTypeStr, Item[] inventory, State state, boolean[] compoundingStates, String slotNumber, int characterScore) {
        this.mainGame = mainGameSession;
        this.name = charName;
        this.inventory = inventory;
        this.characterNumber = characterNumber;
        this.characterType = characterTypeStr.toLowerCase();
        this.state = state;
        this.compoundingStates = compoundingStates;
        this.slot = slotNumber;
        this.score = characterScore;

        characterHeads = new HashMap<>();
        characterBodies = new HashMap<>();

        loadImages();


        setUpCharacter();
        setUpInventory();
        modifyModifiers(characterNumber);

        Gdx.app.log("NAME", "health: " + health + "\nsleep: " + sleep + "\nhappiness: " + happiness + "\nfullness: " + fullness + "\nstress: " + stress);
    }

    // Getter for name
    public String getName() { return name;}
    //Getter for character type
    public String getCharacterType(){return characterType;}
    //Getter for slot that character is saved in
    public String getSlotNumber(){ return slot;}

    //Getter for Score
    public int getScore() { return score;}
    //Method to increment score
    public void incrementScore() { this.score = score+1;}
    public void setScore(int score) { this.score = score;}

    // Getter for health
    public float getHealth() { return health;}
    //Setter for health
    public void setHealth(float health) {
        if (health >= 0.0f && health <= 100.0f) {
            this.health = health;
        } else if(health <= 0.0f){
            this.health = 0.0f;
        }else {
            this.health = 100.0f;
        }}

    // Getter for happiness
    public float getHappiness() { return happiness;}
    //Setter for happiness
    public void setHappiness(float happiness) {
        if (happiness >= 0.0f && happiness <= 100.0f) {
            this.happiness = happiness;
        } else if(happiness <= 0.0f){
            this.happiness = 0.0f;
        } else {
            this.happiness = 100.0f;
    }}

    // Getter for hunger
    public float getHunger() { return fullness; }
    // Setter for hunger
    public void setHunger(float hunger) {
        if (hunger >= 0.0f && hunger <= 100.0f) {
            this.fullness = hunger;
        } else if(hunger <= 0.0f){
            this.fullness = 0.0f;
        } else {
        this.fullness = 100.0f;
    }}

    // Getter for sleep
    public float getSleep() { return sleep; }
    // Setter for sleep
    public void setSleep(float sleep) {
        if (sleep >= 0.0f && sleep <= 100.0f) {
            this.sleep = sleep;
        } else if(sleep <= 0.0f){
            this.sleep = 0.0f;
        } else {
        this.sleep = 100.0f;
    }}

    // Getter for stress
    public float getStress() {
        return stress; }
    // Setter for stress
    public void setStress(float stress) {
        if (stress >= 0.0f && stress <= 100.0f) {
            this.stress = stress;
        } else if(stress <= 0.0f){
            this.stress = 0.0f;
        } else {
            this.stress = 100.0f;
    }}
    public void determineStress() {
        setStress((getHappiness()+getHunger()+getSleep()+getHealth())/4.0f);
    }

    public Item[] getInventory() {
        return this.inventory;
    }

    //Updates the stats based on characters change multipliers and any active states
    public void statBarTick(){
        setHappiness(this.getHappiness() - this.happinessChange);
        setHunger(this.getHunger() - this.fullnessChange);
        setStress(this.getStress() - this.stressChange);
        if (compoundingStates[2]){
            if (compoundingStates[1]) { //hangry
                setHealth(this.getHealth() - 2.0f*this.healthChange);
            } else {
                setHealth(this.getHealth() - this.healthChange);
            }
        }
        if(isSleeping()){
            setSleep(this.getSleep() + (5.0f*sleepChange));
        } else {
            setSleep(this.getSleep() - sleepChange);
        }
        determineStress();
    }

    //sets the stat change modifiers based on character type
    public void modifyModifiers(int characterTypeNumber){
        switch (characterTypeNumber){
            case 0:
                this.healthChange = 1.0f;
                this.sleepChange = 1.0f;
                this.happinessChange = 1.0f;
                this.fullnessChange = 1.0f;
                break;

            case 1:
                //health and sleep go down faster but happiness and hunger go down slower
                this.healthChange = 1.5f;
                this.sleepChange = 1.5f;
                this.happinessChange = 0.7f;
                this.fullnessChange = 0.8f;
                break;

            case 2:
                //sleep is twice as fast but happieness and slowness are slower
                this.healthChange = 1.0f;
                this.sleepChange = 2.0f;
                this.happinessChange = 0.7f;
                this.fullnessChange = 0.9f;
                break;

            case 3:
                //idea is that you start with a super low health but if you can pull it up then you have a 15% slower tick speed
                this.healthChange = 0.85f;
                this.sleepChange = 0.85f;
                this.happinessChange = 0.85f;
                this.fullnessChange = 0.85f;
                break;

            case 4:
                // idea is that it starts you in a rough spot but if you can pull up your stats the reduced tick rate will be crazy useful
                this.healthChange = 0.5f;
                this.sleepChange = 0.75f;
                this.happinessChange = 0.75f;
                this.fullnessChange = 0.75f;
                break;
        }
    }

    private void setUpInventory(){
        for (int i = 0; i < inventory.length; i++) {
            ImageButton invButton;

            if (i == 0){
                invButton = mainGame.createImageButton(characterTextures.get("appleFrame"));

            }
            else if (i == 1){
                invButton = mainGame.createImageButton(characterTextures.get("lemonFrame"));
            }
            else if (i == 2){
                invButton = mainGame.createImageButton(characterTextures.get("orangeFrame"));
            }

            else if (i == 3){
                invButton = mainGame.createImageButton(characterTextures.get("duckFrame"));
            }
            else if (i == 4){
                invButton = mainGame.createImageButton(characterTextures.get("orduckFrame"));
            }
            // i == 5
            else {
                invButton = mainGame.createImageButton(characterTextures.get("bluckFrame"));
            }
            inventory[i].setItemValues();
            inventory[i].setImage(invButton);
            if (inventory[i].isFood()){
                int finalI = i;
                invButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        feed(inventory[finalI]);
                    }
                });
            }
            else if (inventory[i].isGift()){
                int finalI = i;
                invButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        giveGift(inventory[finalI]);
                    }
                });
            }


        }
    }

    // initialize character stats based on character type selected
    private void setUpCharacter(){
        //filling inventory with all the items with the count set to zer0
        for (int i = 0; i < inventory.length; i++) {

            this.inventory[i] = new Item(i,0);
        }

//        for (int i = 0; i < inventory.length; i++) {
//            System.out.println(inventory[i].itemID);
//            System.out.println(inventory[i].itemCount);
//        }

        switch (characterNumber) {

            // case 0 = relaxed
            case 0:
                setHealth(80.0f);
                setSleep(80.0f);
                setHappiness(80.0f);
                setHunger(80.0f);
                setStress(80.0f);
                break;

            // case 1 = quirky
            case 1:
                setHealth(90.0f);
                setSleep(90.0f);
                setHappiness(90.0f);
                setHunger(90.0f);
                setStress(90.0f);
                break;

            // case 2 = hasty
            case 2:
                setHealth(100.0f);
                setSleep(100.0f);
                setHappiness(100.0f);
                setHunger(100.0f);
                setStress(100.0f);
                break;


            // case 3 == brave
            case 3:
                setHealth(10.0f);
                setSleep(100.0f);
                setHappiness(100.0f);
                setHunger(100.0f);
                setStress(77.5f);
                break;

            // case 4 == serious
            case 4:
                setHealth(50.0f);
                setSleep(50.0f);
                setHappiness(25.0f);
                setHunger(25.0f);
                setStress(37.5f);
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
    //determine head to display based on state variable
    private Image headDetermine() {
        stateDetermine(); // Update the state before determining the head image

        switch (state) {
            case DEAD:
                return characterHeads.get(""); // CHANGE TO DEAD IF/WHEN WE HAVE DEAD HEAD SPRITE
            case SLEEPING:
                return characterHeads.get("sleep1"); // CHANGE TO SLEEPING IF/WHEN WE HAVE SLEEPING HEAD SPRITE
            case ANGRY:
                return characterHeads.get("angry");
            case HUNGRY:
                return characterHeads.get("head");
            default:
                return characterHeads.get("head");
        }
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
    //determine body to display based on state variable
    private Image bodyDetermine() {
        stateDetermine(); // Update the state before determining the body image
        if (state == State.DEAD){
            return characterBodies.get("dead");
        }
        else if (compoundingStates[2]){
            return characterBodies.get("hungry1");
        }
        else{
            return characterBodies.get("neutral");
        }
    }

    //Method to evaluate if any states should be triggered or resolved
    //After checks sets state variable to highest priority state
    public void stateDetermine() {
        //Checks if dead, if not dead then move further in if not end here
        if (getHealth() < 1.0f) {
            crashedOut();
        } else {
            //check if sleeping should be triggered
            if (getSleep() < 1.0f && !this.compoundingStates[0]) {
                this.compoundingStates[0] = true;
                setHealth(Math.max(0.0f, (getHealth()-10.0f)));
                setScore(Math.max(0,getScore()-100));
            }
            //check if angry should be triggered
            if(getHappiness() < 1.0f && !this.compoundingStates[1]) {
                this.compoundingStates[1] = true;
                setScore(Math.max(0,getScore()-50));
            }
            //check if hungry should be triggered
            if(getHunger() < 1.0f && !this.compoundingStates[2]) {
                this.compoundingStates[2] = true;
                setScore(Math.max(0,getScore()-50));
            }
            //check if sleeping should be stopped
            if(compoundingStates[0] && getSleep() > 97.5f){
                this.compoundingStates[0] = false;
            }
            //check if angry should be stopped
            if(compoundingStates[1] && getHappiness() > 45.0f){
                this.compoundingStates[1] = false;
            }
            //check if hungry should be resolved
            if(compoundingStates[2] && getHunger() > 30.0f){
                this.compoundingStates[2] = false;
            }
            stateEvaluate();
        }
    }
    //Sets state variable to highst priority State
    public void stateEvaluate(){
        if (compoundingStates[0]){
            this.state = State.SLEEPING;
        } else if (compoundingStates[1]){
            this.state = State.ANGRY;
        } else if (compoundingStates[2]) {
            this.state = State.HUNGRY;
        } else {
            this.state = State.NEUTRAL;
        }
    }
    //State Getter
    public State getState(){ return state;}

    public void setState(State statee)
    {
        this.state = statee;
    }

    //Blink Loop
    public void updateCharacter(float deltaTime){
        stateDetermine();

        if (getState() == State.SLEEPING) {
            sleepTimer += deltaTime;
            if (compoundingStates[2]){
                setBody(characterBodies.get("hungry1"));
            }
            // Switch between sleep1 and sleep2 every 0.5 seconds
            if (sleepTimer >= 0.5f) {
                isSleepState1 = !isSleepState1; // Toggle sleep state
                sleepTimer = 0f; // Reset sleep timer

                if (isSleepState1) {
                    setHead(characterHeads.get("sleep1")); // Set to sleep1 head
                } else {
                    setHead(characterHeads.get("sleep2")); // Set to sleep2 head
                }
            }
        }

        else if (compoundingStates[2]){
            hungerTimer += deltaTime;
            if (hungerTimer >= 0.5f) {
                isHungry1 = !isHungry1; // Toggle sleep state
                hungerTimer = 0f; // Reset sleep timer

                if (isHungry1) {
                    setBody(characterBodies.get("hungry1")); // Set to hungry1 body
                } else {
                    setBody(characterBodies.get("hungry2")); // Set to hungry2 body
                }
                setHead(headDetermine());
            }
        }

        else if (getState() == State.DEAD){
            setHead(characterHeads.get(""));
            setBody(characterBodies.get("dead"));
        }

        else{
            blinkTimer += deltaTime;
            if (blinkTimer >= blinkInterval && !isBlinking && this.state != State.SLEEPING && this.state != State.ANGRY) {
                isBlinking = true;
                blinkTimer = 0f; // Reset blink interval timer
                blinkDurationTimer = 0f; // Start blink duration timer
                setHead(characterHeads.get("blink")); // Set to blinking head
            }

            if (isBlinking) {
                blinkDurationTimer += deltaTime;
                if (blinkDurationTimer >= blinkDuration) {
                    isBlinking = false;
                    setHead(headDetermine()); // Revert to normal head
                    setBody(bodyDetermine()); // Revert to normal body
                }
            }
        }


    }

    //Action Block Check
    public boolean actionBlocked(){
        return actionBlockCooldownRemaining != 0;
    }
    public void setActionBlocked(float val){
        if (actionBlockCooldownRemaining == 0){
            actionBlockCooldownRemaining = val;
        }

    }
    //update action block timer
    public void updateActionBlock(float deltaTime) {
        if (actionBlockCooldownRemaining > 0) {
            actionBlockCooldownRemaining = Math.max(0, actionBlockCooldownRemaining - deltaTime);
        }
    }

    //method to force re-evaluating state, body, and head
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

    //placeholder function see comment
    public void crashedOut(){
        setHealth(0.0f);
        setSleep(0.0f);
        setHunger(0.0f);
        setStress(0.0f);
        setHappiness(0.0f);
        this.state = State.DEAD;
        compoundingStates[0] = false;
        compoundingStates[1] = false;
        compoundingStates[2] = false;
        setHead(characterHeads.get(""));
        setBody(characterBodies.get("dead"));
    }

    //exercise action
    public void exercise(){
        if(!actionBlocked() && (!compoundingStates[1]) && state != State.SLEEPING && !isDead()) { //check if action is allowed
            //Update Stats
            this.fullness = Math.max(0.0f, getHunger() - 10.0f);
            this.sleep = Math.max(0.0f, getSleep() - 20.0f);
            if (!compoundingStates[2]) {
                this.health = Math.min(100.0f, getHealth() + 5.0f);
            } else {
                this.health = Math.min(100.0f, getHealth() + 5.0f);
                this.sleep = Math.max(0.0f, getSleep() - 10.0f);
            }
            stateDetermine();
            if (this.getHealth()>1.0f) {
                //if exercising forced character to go to sleep exercise body animation play's while head displays character entered sleep
                if (this.compoundingStates[0]) {
                    setHead(characterHeads.get("sleep1"));
                } else {
                    setHead(characterHeads.get("exercise"));
                }
                //starting exercise body
                setBody(characterBodies.get("workout1"));

                float actionLength = 5.0f;
                actionBlockCooldownRemaining = (actionLength + 0.5f);

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
            else {
                crashedOut();
            }
        }
    }

    //Play action method stat change
    //Will eventually be replaced with mini-game
    public boolean play(){
        if(!actionBlocked()){ //check if action is allowed
            if(!(playCooldownRemaining > 0)){
                this.happiness = Math.min(100.0f, getHappiness() + 20.0f);
                playCooldownRemaining = 30.0f;
                actionBlockCooldownRemaining = (5f);
                resumeDefaultCharacterState(1.0f);
                return true;
            }
            else{
                return false;
                //code to tell player that play is on cooldown
            }
        }
        else{
            return false;
            //Code to say that you cannot play with pet while in the state they are in
        }
    }

    //Take to doctor action
    public boolean takeToDoctor(){
        if(!actionBlocked() && !compoundingStates[1] && (!isDead() || Objects.equals(getName(), "Wiktor"))){ //check if action is allowed
            if(!(doctorCooldownRemaining > 0)){
                float actionLength = 8.0f;

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

    //cooldown timer update method
    public void updateCooldowns(float deltaTime) {
        if (doctorCooldownRemaining > 0) {
            doctorCooldownRemaining = Math.max(0, doctorCooldownRemaining - deltaTime);
        }
        if (playCooldownRemaining > 0){
            playCooldownRemaining = Math.max(0, playCooldownRemaining - deltaTime);
        }

        if (saveTimer < 30) {
            saveTimer = Math.min(30, saveTimer + deltaTime);
        }
        // every 30 seconds, save game
        else{
            mainGame.jsonHandler.saveCharacterToGameSlot(getSlotNumber(), this);
            saveTimer = 0;

        }
    }

    //feed action
    //may need to be tweaked to link to GameScreen and to display correct reason it could not be done
    public void feed(Item item){
        if(!actionBlocked() && !compoundingStates[1] && item.reduceCount()){ //check if action is allowed
            this.fullness = Math.min(100.0f, getHunger() + (item.getItemStatValue()));
            feedVisual();
        }
        else{
            //need to throw an error for a player trying to use an item they don't have here

        }
    }
    //placeholder visual effect for feed
    public void feedVisual() {
        if (!actionBlocked()) {

            float actionLength = 5.0f;

            munchSound.play();
            setHead(characterHeads.get("happy"));;


            actionBlockCooldownRemaining = (actionLength);

            resumeDefaultCharacterState(actionLength);
        }
    }

    //giveGift action
    //may need to be tweaked to link to GameScreen and to display correct reason it could not be done
    public void giveGift(Item item){
        if(!actionBlocked() && !isDead() && item.reduceCount()){ //check if action is allowed

            this.happiness = Math.min(100.0f, getHappiness() + item.getItemStatValue());
            giftVisual(item);

        }
        else{
           System.out.println("could not give gift");
        }
    }

    public void giftVisual(Item item) {
        if (!actionBlocked()) {

            float actionLength = 5.0f;

            setHead(characterHeads.get("happy"));;
            if (item.getItemID() == 3){
                setBody(characterBodies.get("duck"));;

            }
            else if (item.getItemID() == 4){
                setBody(characterBodies.get("orduck"));;
            }
            else if (item.getItemID() == 5){
                setBody(characterBodies.get("bluck"));;
            }

            quackSound.play();
            actionBlockCooldownRemaining = (actionLength + 0.5f);

            resumeDefaultCharacterState(actionLength);
        }
    }

    //go to sleep action
    public void sleep(){
        if(!actionBlocked() && !compoundingStates[1] && !isDead()) {
            this.compoundingStates[0] = true;
            this.state = State.SLEEPING;
        }
    }

    //method to increase count of item at index by 1
    public void gainItem(int index) {
        inventory[index].increaseCount();
    }

    //checks if player is sleeping
    public boolean isSleeping() {
        return (state == State.SLEEPING);
    }

    //checks if player is dead
    public boolean isDead() {
        return (state == State.DEAD);
    }
    public boolean isAngry() {
        return compoundingStates[1];
    }

    public void loadImages(){
        // Load textures into the texture map
        characterTextures.put("neutralBody", new Texture(Gdx.files.internal("game/character/body-neutral.png")));
        characterTextures.put("workout1Body", new Texture(Gdx.files.internal("game/character/body-workout1.png")));
        characterTextures.put("workout2Body", new Texture(Gdx.files.internal("game/character/body-workout2.png")));
        characterTextures.put("hungry1Body", new Texture(Gdx.files.internal("game/character/body-hungry1.png")));
        characterTextures.put("hungry2Body", new Texture(Gdx.files.internal("game/character/body-hungry2.png")));
        characterTextures.put("bluckBody", new Texture(Gdx.files.internal("game/character/body-hold-bluck.png")));
        characterTextures.put("duckBody", new Texture(Gdx.files.internal("game/character/body-hold-duck.png")));
        characterTextures.put("orduckBody", new Texture(Gdx.files.internal("game/character/body-hold-orduck.png")));


        characterTextures.put("head", new Texture(Gdx.files.internal("game/character/" + characterType + "-head.png")));
        characterTextures.put("blink", new Texture(Gdx.files.internal("game/character/" + characterType + "-blink.png")));
        characterTextures.put("exercise", new Texture(Gdx.files.internal("game/character/" + characterType + "-exercise.png")));
        characterTextures.put("happy", new Texture(Gdx.files.internal("game/character/" + characterType + "-happy.png")));
        characterTextures.put("angry", new Texture(Gdx.files.internal("game/character/" + characterType + "-angry.png")));
        characterTextures.put("sleep1", new Texture(Gdx.files.internal("game/character/" + characterType + "-sleep1.png")));
        characterTextures.put("sleep2", new Texture(Gdx.files.internal("game/character/" + characterType + "-sleep2.png")));
        characterTextures.put("dead", new Texture(Gdx.files.internal("game/character/" + characterType + "-dead.png")));



        // Create images from textures
        characterBodies.put("neutral", mainGame.createImage(characterTextures.get("neutralBody")));
        characterBodies.put("workout1", mainGame.createImage(characterTextures.get("workout1Body")));
        characterBodies.put("workout2", mainGame.createImage(characterTextures.get("workout2Body")));
        characterBodies.put("hungry1", mainGame.createImage(characterTextures.get("hungry1Body")));
        characterBodies.put("hungry2", mainGame.createImage(characterTextures.get("hungry2Body")));
        characterBodies.put("dead", mainGame.createImage(characterTextures.get("dead")));
        characterBodies.put("bluck", mainGame.createImage(characterTextures.get("bluckBody")));
        characterBodies.put("duck", mainGame.createImage(characterTextures.get("duckBody")));
        characterBodies.put("orduck", mainGame.createImage(characterTextures.get("orduckBody")));

        characterHeads.put("head", mainGame.createImage(characterTextures.get("head")));
        characterHeads.put("blink", mainGame.createImage(characterTextures.get("blink")));
        characterHeads.put("exercise", mainGame.createImage(characterTextures.get("exercise")));
        characterHeads.put("happy", mainGame.createImage(characterTextures.get("happy")));
        characterHeads.put("angry", mainGame.createImage(characterTextures.get("angry")));
        characterHeads.put("sleep1", mainGame.createImage(characterTextures.get("sleep1")));
        characterHeads.put("sleep2", mainGame.createImage(characterTextures.get("sleep2")));


        // inventory textures loaded here
        characterTextures.put("appleFrame", new Texture(Gdx.files.internal("game/inventory/apple-frame.png")));
        characterTextures.put("bluckFrame", new Texture(Gdx.files.internal("game/inventory/bluck-frame.png")));
        characterTextures.put("duckFrame", new Texture(Gdx.files.internal("game/inventory/duck-frame.png")));
        characterTextures.put("lemonFrame", new Texture(Gdx.files.internal("game/inventory/lemon-frame.png")));
        characterTextures.put("orangeFrame", new Texture(Gdx.files.internal("game/inventory/orange-frame.png")));
        characterTextures.put("orduckFrame", new Texture(Gdx.files.internal("game/inventory/orduck-frame.png")));

        munchSound = Gdx.audio.newSound(Gdx.files.internal("music/eating.mp3"));
        quackSound = Gdx.audio.newSound(Gdx.files.internal("music/quack-quack.mp3"));

    }

    public void dispose() {
        // Dispose textures
        for (Texture texture : characterTextures.values()) {
            texture.dispose();
        }
        characterTextures.clear();

        // Clear image maps
        characterHeads.clear();
        characterBodies.clear();

        // Cancel and nullify tasks
        if (blinkTask != null) {
            blinkTask.cancel();
            blinkTask = null;
        }

        // Reset transient fields
        currentHead = null;
        currentBody = null;

    }

}
