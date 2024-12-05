package com.kaobells.group44;

//imports
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

/**
 * The {@code CharacterClass} represents a game character with specific attributes,
 * states, and functionality for interacting with the game environment.
 *
 * <p>This class manages a character's health, happiness, hunger, sleep, and stress,
 * and supports various actions such as feeding, exercising, playing, and visiting a doctor.
 * It also handles animations and visual updates for the character's states.</p>
 *
 * @author group 44
 * @version 1.0
 */
public class CharacterClass {

    /** Reference to the main game instance. */
    private transient Main mainGame;

    /** Name of the character. */
    private final String name;

    /** Slot used, values range from 1 through 3. */
    private String slot;

    /** Current score of the character. */
    private int score;

    /** Character number, ranging from 0 through 4. */
    private final int characterNumber;

    /** Type of character, e.g., relaxed, brave. */
    private final String characterType;

    /** Prime state of the character, highest priority state displayed. */
    private State state = State.NEUTRAL;

    /** Current health value for the character. */
    private float health;

    /** Current sleep value for the character. */
    private float sleep;

    /** Current happiness value for the character. */
    private float happiness;

    /** Current fullness value for the character. */
    private float fullness;

    /** Current stress value for the character. */
    private float stress;

    /** Health change multiplier for tick rates. */
    private transient float healthChange;

    /** Sleep change multiplier for tick rates. */
    private transient float sleepChange;

    /** Happiness change multiplier for tick rates. */
    private transient float happinessChange;

    /** Fullness change multiplier for tick rates. */
    private transient float fullnessChange;

    /** Stress change multiplier for tick rates. */
    private transient float stressChange = 1.0f;

    /** Inventory array filled with Item objects. */
    private Item[] inventory;

    /** Displayed sprite head image. */
    private transient Image currentHead;

    /** Displayed sprite body image. */
    private transient Image currentBody;

    /** Sound effect played when the character eats. */
    private transient Sound munchSound;

    /** Sound effect played when the character quacks. */
    private transient Sound quackSound;

    /** Map of character head images. */
    private transient Map<String, Image> characterHeads;

    /** Map of character body images. */
    private transient Map<String, Image> characterBodies;

    /** Map of character textures. */
    private transient final Map<String, Texture> characterTextures = new HashMap<>();

    /** Timer task for the blinking animation, blocking other actions while active. */
    private transient Timer.Task blinkTask;

    /** Array representing states that can compound effects with other states (e.g., sleeping, angry, hungry). */
    private boolean[] compoundingStates;

    /** Remaining cooldown time for doctor action, in seconds. */
    private float doctorCooldownRemaining = 0;

    /** Remaining cooldown time for play action, in seconds. */
    private float playCooldownRemaining = 0;

    /** Remaining cooldown time for blocking actions. */
    private float actionBlockCooldownRemaining = 0;

    /** Timer for auto-save functionality, starts at 30 seconds for initial save. */
    private float saveTimer = 30f;

    /** Timer for tracking the blinking interval. */
    private transient float blinkTimer = 0f;

    /** Timer for tracking the duration of a single blink. */
    private float blinkDurationTimer = 0f;

    /** Indicates whether the character is currently blinking. */
    private boolean isBlinking = false;

    /** Indicates whether the character is in a hungry state. */
    private boolean isHungry1 = true;

    /** Interval between blinks, in seconds. */
    private final float blinkInterval = 3.0f;

    /** Duration of a single blink, in seconds. */
    private final float blinkDuration = 0.5f;

    /** Duration of hunger effects, in seconds. */
    private final float hungerDuration = 1.5f;

    /** Duration of the dead state, in seconds. */
    private final float deadDuration = 100.0f;

    /** Timer for tracking sleep animation. */
    private float sleepTimer = 0f;

    /** Indicates which sleep state is active. */
    private boolean isSleepState1 = true;

    /** Timer for tracking the duration of the hunger effect. */
    private float hungerTimer = 0f;


    // Add default constructor for LibGDX Json Loader
    /**
     * Default constructor for {@code CharacterClass} for LibGDX JSON loader compatibility.
     */
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
    /**
     * Initializes character's data, textures, and inventory.
     *
     * @param mainG The main game instance used for loading resources.
     */
    public void startLoadCharacter(Main mainG){
        mainGame = mainG;
        characterHeads = new HashMap<>();
        characterBodies = new HashMap<>();
        loadImages();
        //methods used to set up character's other stats/variables/sprites
        setUpInventory();
        setHead(headDetermine());
        setBody(bodyDetermine());
        modifyModifiers(characterNumber);
        Gdx.app.log("NAME", "health: " + health + "\nsleep: " + sleep + "\nhappiness: " + happiness + "\nfullness: " + fullness + "\nstress: " + stress);
    }

    // Constructor with default state (NEUTRAL)
    /**
     * Constructs a new {@link CharacterClass} instance with the specified parameters, defaulting
     * the character's state to {@link State#NEUTRAL}.
     *
     * @param mainGameSession  The {@link Main} game instance managing shared resources and transitions.
     * @param charName         The name of the character.
     * @param characterNumber  The unique number identifying the character's type.
     * @param characterTypeStr The type of the character as a {@link String}.
     * @param inventory        An array of {@link Item} objects representing the character's inventory.
     * @param compoundingStates A boolean array representing the character's compounding states
     *                          (e.g., sleeping, angry, hungry).
     * @param slotNumber       The save slot number for the character.
     * @param characterScore   The initial score of the character.
     */
    public CharacterClass(Main mainGameSession, String charName, int characterNumber, String characterTypeStr, Item[] inventory, boolean[] compoundingStates, String slotNumber, int characterScore) {
        this(mainGameSession, charName, characterNumber, characterTypeStr, inventory, State.NEUTRAL, compoundingStates, slotNumber, characterScore);
    }

    /**
     * Constructs a new {@code CharacterClass} instance with specified attributes.
     *
     * @param mainGameSession   The main game instance.
     * @param charName          The name of the character.
     * @param characterNumber   The character's unique identifier.
     * @param characterTypeStr  The type of the character.
     * @param inventory         The character's inventory.
     * @param state             The initial state of the character.
     * @param compoundingStates An array representing compounding states.
     * @param slotNumber        The slot number for saving the character.
     * @param characterScore    The initial score of the character.
     */
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
        //set up character sprite maps
        characterHeads = new HashMap<>();
        characterBodies = new HashMap<>();
        loadImages();
        //methods used to set up character's other stats/variables
        setUpCharacter();
        setUpInventory();
        modifyModifiers(characterNumber);
        Gdx.app.log("NAME", "health: " + health + "\nsleep: " + sleep + "\nhappiness: " + happiness + "\nfullness: " + fullness + "\nstress: " + stress);
    }

    // Getter for name
    /**
     * Gets the character's name.
     *
     * @return The name of the character.
     */
    public String getName() { return name;}

    //Getter for character type
    /**
     * Gets the character type.
     *
     * @return The type of the character.
     */
    public String getCharacterType(){return characterType;}

    //Getter for slot that character is saved in

    /**
     * Gets the character's save slot.
     *
     * @return The save slot as a string.
     */
    public String getSlotNumber(){ return slot;}

    //Getter for Score
    /**
     * Gets the character's current score.
     *
     * @return The character's score.
     */
    public int getScore() { return score;}

    //Method to increment score
    /**
     * Increments the character's score by 1.
     */
    public void incrementScore() { this.score = score+1;}

    /**
     * Sets the character's score to the specified value.
     *
     * @param score The new score value.
     */
    public void setScore(int score) { this.score = score;}

    // Getter for health
    /**
     * Gets the character's health.
     *
     * @return The current health value.
     */
    public float getHealth() { return health;}

    //Setter for health
    /**
     * Sets the character's health to the specified value, ensuring it remains within
     * valid bounds (0-100).
     *
     * @param health The new health value.
     */
    public void setHealth(float health) {
        if (health >= 0.0f && health <= 100.0f) {
            this.health = health;
        } else if(health <= 0.0f){
            this.health = 0.0f;
        }else {
            this.health = 100.0f;
        }}

    // Getter for happiness
    /**
     * Gets the character's happiness.
     *
     * @return The current happiness value.
     */
    public float getHappiness() { return happiness;}

    //Setter for happiness
    /**
     * Sets the character's happiness to the specified value, ensuring it remains
     * within valid bounds (0-100).
     *
     * @param happiness The new happiness value.
     */
    public void setHappiness(float happiness) {
        if (happiness >= 0.0f && happiness <= 100.0f) {
            this.happiness = happiness;
        } else if(happiness <= 0.0f){
            this.happiness = 0.0f;
        } else {
            this.happiness = 100.0f;
    }}

    // Getter for hunger
    /**
     * Gets the character's hunger level.
     *
     * @return The current hunger value.
     */
    public float getHunger() { return fullness; }

    // Setter for hunger
    /**
     * Sets the character's hunger level to the specified value, ensuring it remains
     * within valid bounds (0-100).
     *
     * @param hunger The new hunger value.
     */
    public void setHunger(float hunger) {
        if (hunger >= 0.0f && hunger <= 100.0f) {
            this.fullness = hunger;
        } else if(hunger <= 0.0f){
            this.fullness = 0.0f;
        } else {
        this.fullness = 100.0f;
    }}

    // Getter for sleep
    /**
     * Gets the character's sleep level.
     *
     * @return The current sleep value.
     */
    public float getSleep() { return sleep; }

    // Setter for sleep
    /**
     * Sets the character's sleep level to the specified value, ensuring it remains
     * within valid bounds (0-100).
     *
     * @param sleep The new sleep value.
     */
    public void setSleep(float sleep) {
        if (sleep >= 0.0f && sleep <= 100.0f) {
            this.sleep = sleep;
        } else if(sleep <= 0.0f){
            this.sleep = 0.0f;
        } else {
        this.sleep = 100.0f;
    }}

    // Getter for stress
    /**
     * Gets the character's stress level.
     *
     * @return The current stress value.
     */
    public float getStress() {
        return stress; }

    // Setter for stress
    /**
     * Sets the character's stress level to the specified value, ensuring it remains
     * within valid bounds (0-100).
     *
     * @param stress The new stress value.
     */
    public void setStress(float stress) {
        if (stress >= 0.0f && stress <= 100.0f) {
            this.stress = stress;
        } else if(stress <= 0.0f){
            this.stress = 0.0f;
        } else {
            this.stress = 100.0f;
    }}
    //stress is calculated using the average of the values of the other stats
    /**
     * Determines the character's stress level based on other stats.
     */
    public void determineStress() {
        setStress((getHappiness()+getHunger()+getSleep()+getHealth())/4.0f);
    }

    //Inventory Getter

    public Item[] getInventory() {
        return this.inventory;
    }

    //Updates the stats based on characters change multipliers and any active states
    /**
     * Updates the character's stats based on time progression and compounding states.
     */
    public void statBarTick(){
        setHappiness(this.getHappiness() - this.happinessChange);
        setHunger(this.getHunger() - this.fullnessChange);
        setStress(this.getStress() - this.stressChange);
        if (compoundingStates[2]){ //if hungry then health starts going down
            if (compoundingStates[1]) { // if "hangry" (hungry & angry) then health goes down at double speed
                setHealth(this.getHealth() - 2.0f*this.healthChange);
            } else { //case for hungry but not angry
                setHealth(this.getHealth() - this.healthChange);
            }
        }
        if(isSleeping()){ //when asleep sleep bar goes up at 5 times speed
            setSleep(this.getSleep() + (5.0f*sleepChange));
        } else { //if not asleep then sleep will be going down
            setSleep(this.getSleep() - sleepChange);
        }
        determineStress(); //stress is calculated using the average of the values of the other stats
    }

    //sets the stat change modifiers based on character type
    /**
     * Modifies the stat change rates (modifiers) based on the character type.
     *
     * @param characterTypeNumber The character type as an integer.
     */
    public void modifyModifiers(int characterTypeNumber){
        switch (characterTypeNumber){
            // case 0 == relaxed character type selected, the "default" character
            case 0:
                this.healthChange = 0.5f;
                this.sleepChange = 0.5f;
                this.happinessChange = 0.5f;
                this.fullnessChange = 0.5f;
                break;
            // case 1 == quirky character type selected, health and sleep go down faster but happiness and hunger go down slower
            case 1:
                this.healthChange = 1.0f;
                this.sleepChange = 1.0f;
                this.happinessChange = 0.7f;
                this.fullnessChange = 0.8f;
                break;
            // case 2 == hasty character type selected, sleep is twice as fast but happiness and slowness are slower
            case 2:
                this.healthChange = 1.0f;
                this.sleepChange = 2.0f;
                this.happinessChange = 0.7f;
                this.fullnessChange = 0.9f;
                break;
            // case 3 == brave character type selected, start with a super low health but if you can pull it up then you have a 15% slower tick speed
            case 3:
                this.healthChange = 0.85f;
                this.sleepChange = 0.85f;
                this.happinessChange = 0.85f;
                this.fullnessChange = 0.85f;
                break;
            // case 4 == serious character type selected, starts you in a rough spot but if you can pull up your stats the reduced tick rate will be crazy useful
            case 4:
                this.healthChange = 0.5f;
                this.sleepChange = 0.75f;
                this.happinessChange = 0.75f;
                this.fullnessChange = 0.75f;
                break;
        }
    }

    //function to set up inventory with each item's relivent image and gift/food indicator
    /**
     * Sets up the character's inventory with default items.
     */
    private void setUpInventory(){
        for (int i = 0; i < inventory.length; i++) {
            ImageButton invButton;
            //apple item
            if (i == 0){
                invButton = mainGame.createImageButton(characterTextures.get("appleFrame"));
            //lemon item
            }
            else if (i == 1){
                invButton = mainGame.createImageButton(characterTextures.get("lemonFrame"));
            }
            //orange item
            else if (i == 2){
                invButton = mainGame.createImageButton(characterTextures.get("orangeFrame"));
            }
            //duck item
            else if (i == 3){
                invButton = mainGame.createImageButton(characterTextures.get("duckFrame"));
            }
            //orange duck item
            else if (i == 4){
                invButton = mainGame.createImageButton(characterTextures.get("orduckFrame"));
            }
            //used for i == 5, blue duck item
            else {
                invButton = mainGame.createImageButton(characterTextures.get("bluckFrame"));
            }
            //set up items and image buttons to use them.
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

    // initialize a new character's stats based on character type selected
    /**
     * Sets up the character's initial stats and visuals based on the type.
     */
    private void setUpCharacter(){
        //filling inventory with all the items with the count set to zer0
        for (int i = 0; i < inventory.length; i++) {

            this.inventory[i] = new Item(i,0);
        }
        //take character number and set relivent stats
        switch (characterNumber) {
            // case 0 = relaxed character type selected
            case 0:
                setHealth(80.0f);
                setSleep(80.0f);
                setHappiness(80.0f);
                setHunger(80.0f);
                setStress(80.0f);
                break;
            // case 1 = quirky character type selected
            case 1:
                setHealth(90.0f);
                setSleep(90.0f);
                setHappiness(90.0f);
                setHunger(90.0f);
                setStress(90.0f);
                break;
            // case 2 = hasty character type selected
            case 2:
                setHealth(100.0f);
                setSleep(100.0f);
                setHappiness(100.0f);
                setHunger(100.0f);
                setStress(100.0f);
                break;
            // case 3 == brave character type selected
            case 3:
                setHealth(10.0f);
                setSleep(100.0f);
                setHappiness(100.0f);
                setHunger(100.0f);
                setStress(77.5f);
                break;
            // case 4 == serious character type selected
            case 4:
                setHealth(50.0f);
                setSleep(50.0f);
                setHappiness(25.0f);
                setHunger(25.0f);
                setStress(37.5f);
                break;
            // catch
            default: throw new IllegalArgumentException("Invalid character index: " + characterNumber);
        }
        //sets head and body sprites
        setHead(headDetermine());
        setBody(bodyDetermine());
    }

    //Head Getter
    /**
     * Gets the character's head image.
     *
     * @return The character's head image.
     */
    public Image getHead() { return currentHead; }

    //Head Setter
    /**
     * Sets the character's head image.
     *
     * @param newHead The new head image.
     */
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
    /**
     * Determines the appropriate head image based on the character's current state.
     *
     * @return The {@link Image} representing the character's head.
     */
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
    /**
     * Gets the character's body image.
     *
     * @return The character's body image.
     */
    public Image getBody() { return currentBody; }

    //Body Setter
    /**
     * Sets the character's body image.
     *
     * @param newBody The new body image.
     */
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
    /**
     * Determines the appropriate body image based on the character's current state.
     *
     * @return The {@link Image} representing the character's body.
     */
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
    /**
     * Determines the character's state based on its attributes.
     */
    public void stateDetermine() {
        //Checks if dead, if not dead then move further in if not go into the death sequence
        if (getHealth() < 1.0f) {
            killPet();
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
    //Sets state variable to highest priority State
    /**
     * Evaluates and assigns the highest-priority state to the character.
     */
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
    /**
     * Retrieves the current state of the character.
     *
     * @return The {@link State} of the character.
     */
    public State getState(){ return state;}

    //State Setter
    /**
     * Sets the state of the character.
     *
     * @param statee The new {@link State} to be assigned to the character.
     */
    public void setState(State statee)
    {
        this.state = statee;
    }

    //State Animation Loops
    /**
     * Updates the character's attributes and state over time.
     *
     * @param deltaTime The time elapsed since the last update, in seconds.
     */
    public void updateCharacter(float deltaTime){
        //start by determining state
        stateDetermine();
        //if state variable is set to sleep go into sleep animation loop
        if (getState() == State.SLEEPING) {
            sleepTimer += deltaTime;
            if (compoundingStates[2]){  //if also hungry set body to hungry
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
        //if state variable is set to hungry go into hungry animation loop
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
        //if dead set to tombstone
        else if (getState() == State.DEAD){
            setHead(characterHeads.get(""));
            setBody(characterBodies.get("dead"));
        }
        //if neutral or hungry go into a blinking animation loop
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
    /**
     * Checks if the character's actions are currently blocked.
     *
     * @return {@code true} if actions are blocked, otherwise {@code false}.
     */
    public boolean actionBlocked(){
        return actionBlockCooldownRemaining != 0;
    }
    //Action Block Setter
    /**
     * Blocks character actions for a specified duration.
     *
     * @param val The duration in seconds.
     */
    public void setActionBlocked(float val){
        if (actionBlockCooldownRemaining == 0){
            actionBlockCooldownRemaining = val;
        }
    }
    //update action block timer
    /**
     * Updates the action block timer to track when actions can be performed again.
     *
     * @param deltaTime The time elapsed since the last update, in seconds.
     */
    public void updateActionBlock(float deltaTime) {
        if (actionBlockCooldownRemaining > 0) {
            actionBlockCooldownRemaining = Math.max(0, actionBlockCooldownRemaining - deltaTime);
        }
    }

    //method to force re-evaluating state, body, and head after a timer
    /**
     * Resumes the character's default state after a given duration.
     *
     * @param duration The duration in seconds after which to reset the state.
     */
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

    //function to update character to be dead
    /**
     * Sets the character as dead, resetting all attributes to zero and disabling interactions.
     */
    public void killPet(){
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
    /**
     * Allows the character to perform an exercise action, updating relevant stats.
     */
    public void exercise(){
        if(!actionBlocked() && (!compoundingStates[1]) && state != State.SLEEPING && !isDead()) { //check if action is allowed
            //Update Stats
            this.fullness = Math.max(0.0f, getHunger() - 10.0f);
            this.sleep = Math.max(0.0f, getSleep() - 20.0f);
            if (!compoundingStates[2]) { //if not hungry health goes up by 10
                this.health = Math.min(100.0f, getHealth() + 5.0f);
            } else { //for game balencing if exercising while hungry makes the health gained only 5 and the sleep lost 10
                this.health = Math.min(100.0f, getHealth() + 5.0f);
                this.sleep = Math.max(0.0f, getSleep() - 10.0f);
            }
            stateDetermine(); //state check
            if (this.getHealth()>1.0f) {
                //if exercising forced character to go to sleep exercise body animation play's while head displays character entered sleep
                if (this.compoundingStates[0]) {
                    setHead(characterHeads.get("sleep1"));
                } else {
                    setHead(characterHeads.get("exercise"));
                }
                //starting exercise body
                setBody(characterBodies.get("workout1"));
                //timers for excercise animation
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
                // resetting sprite after animation
                resumeDefaultCharacterState(actionLength);
            }
            else { //if exercising leads to a pet falling asleep which leads to their death cut exercise short and kill pet
                killPet();
            }
        }
    }

    //Play action method stat change and cooldown
    /**
     * Allows the character to play, increasing happiness.
     *
     * @return {@code true} if the play action was successful, otherwise {@code false}.
     */
    public boolean play(){
        if(!actionBlocked()){ //check if action is allowed
            if(!(playCooldownRemaining > 0)){
                playCooldownRemaining = 30.0f;
                actionBlockCooldownRemaining = (5f);
                resumeDefaultCharacterState(1.0f);
                return true; //return true to let gamescreen know that play method was used and mini-game screen can be pushed
            } else{ return false;}//return false to let gamescreen know that play method cannot be used and mini-game screen cannot be pushed
        } else{ return false; }//return false to let gamescreen know that play method cannot be used and mini-game screen cannot be pushed
    }

    // give this boy some play gah damnit
    public void givePlay(){
        this.happiness = Math.min(100.0f, getHappiness() + 20.0f);
    }

    //Take to doctor action
    /**
     * Allows the character to visit the doctor, increasing health.
     *
     * @return {@code true} if the doctor action was successful, otherwise {@code false}.
     */
    public boolean takeToDoctor(){
        if(!actionBlocked() && !compoundingStates[1] && (!isDead() || Objects.equals(getName(), "Wiktor"))){ //check if action is allowed or easter egg name
            if(!(doctorCooldownRemaining > 0)){ //check if on cooldown
                float actionLength = 8.0f;

                setHead(headDetermine()); // Set normal head
                setBody(bodyDetermine()); // Set normal body

                doctorCooldownRemaining = 30.0f; // Reset cooldown
                this.health = Math.min(100.0f, getHealth() + 20.0f);
                //action block for animation
                actionBlockCooldownRemaining = (actionLength);
                //reset sprite post-animation
                resumeDefaultCharacterState(actionLength);
                return true; //return true to let gamescreen know that doctor method was used and animation should be played
            } else { return false; } //return false to let gamescreen know that doctor method was not used and animation should not be played
        }  else { return false; } //return false to let gamescreen know that doctor method was not used and animation should not be played
    }

    //cooldown timer update method
    /**
     * Updates timers related to character cooldowns and auto-saving.
     *
     * @param deltaTime The time elapsed since the last update, in seconds.
     */
    public void updateCooldowns(float deltaTime) {
        if (doctorCooldownRemaining > 0) { //update doctor cooldown
            doctorCooldownRemaining = Math.max(0, doctorCooldownRemaining - deltaTime);
        }
        if (playCooldownRemaining > 0){ //update play cooldown
            playCooldownRemaining = Math.max(0, playCooldownRemaining - deltaTime);
        }
        if (saveTimer < 30) { //update save cooldown
            saveTimer = Math.min(30, saveTimer + deltaTime);
        } else { // every 30 seconds, save game
            mainGame.jsonHandler.saveCharacterToGameSlot(getSlotNumber(), this);
            saveTimer = 0; //reset save timer
        }
    }

    //feed action
    //may need to be tweaked to link to GameScreen and to display correct reason it could not be done
    /**
     * Feeds the character using the specified {@code Item}, increasing hunger levels.
     *
     * @param item The item used to feed the character.
     */
    public void feed(Item item){
        if(!actionBlocked() && !compoundingStates[1] && item.reduceCount() && !isDead() && !isSleeping()){ //check if action is allowed
            this.fullness = Math.min(100.0f, getHunger() + (item.getItemStatValue()));
            feedVisual();
        }
    }
    //Visual/audio effect for feed action
    /**
     * Provides visual and audio feedback when the character is fed.
     */
    public void feedVisual() {
        if (!actionBlocked()) { // check if an action block is active
            float actionLength = 5.0f;
            //play sound and set head
            munchSound.play();
            setHead(characterHeads.get("happy"));;
            //update actionBlock
            actionBlockCooldownRemaining = (actionLength);
            //reset sprite after action complete
            resumeDefaultCharacterState(actionLength);
        }
    }

    //giveGift action
    /**
     * Allows the character to receive a gift, adjusting stats based on the gift's properties.
     *
     * @param item The {@link Item} used as a gift.
     */
    public void giveGift(Item item){
        if(!actionBlocked() && !isDead() && item.reduceCount() && !isSleeping()){ //check if action is allowed
            //if allowed adjust stats and play effect
            this.happiness = Math.min(100.0f, getHappiness() + item.getItemStatValue());
            giftVisual(item);

        }
    }
    //Visual/audio effect for gift action
    /**
     * Plays a visual and audio effect when a gift is given to the character.
     *
     * @param item The {@link Item} used as a gift.
     */
    public void giftVisual(Item item) {
        if (!actionBlocked()) { //check if action is allowed
            float actionLength = 5.0f;
            //set body to relivent sprite for gift item used
            setHead(characterHeads.get("happy"));
            if (item.getItemID() == 3){
                setBody(characterBodies.get("duck")); //for using normal duck sprite
            }
            else if (item.getItemID() == 4){
                setBody(characterBodies.get("orduck")); //for using orange duck sprite
            }
            else if (item.getItemID() == 5){
                setBody(characterBodies.get("bluck")); //for using blue duck sprite
            }
            //play sound and set action timer for animation
            quackSound.play();
            actionBlockCooldownRemaining = (actionLength + 0.5f);
            //reset sprite after action complete
            resumeDefaultCharacterState(actionLength);
        }
    }

    //go to sleep action
    /**
     * Allows the character to sleep, replenishing sleep levels.
     */
    public void sleep(){
        if(!actionBlocked() && !compoundingStates[1] && !isDead()) { //if action is allowed set state to sleeping
            this.compoundingStates[0] = true;
            this.state = State.SLEEPING;
        }
    }

    //method to increase count of item at index by 1
    /**
     * Increases the count of a specific item in the character's inventory.
     *
     * @param index The index of the item in the inventory to increase the count of.
     */
    public void gainItem(int index) {
        inventory[index].increaseCount();
    }

    //checks if player is sleeping
    /**
     * Checks if the character is currently in a sleeping state.
     *
     * @return {@code true} if the character is sleeping, otherwise {@code false}.
     */
    public boolean isSleeping() {
        return (state == State.SLEEPING);
    }
    //checks if player is dead
    /**
     * Checks if the character is currently in a dead state.
     *
     * @return {@code true} if the character is dead, otherwise {@code false}.
     */
    public boolean isDead() {
        return (state == State.DEAD);
    }
    //checks if player is angry
    /**
     * Checks if the character is currently in an angry state.
     *
     * @return {@code true} if the character is angry, otherwise {@code false}.
     */
    public boolean isAngry() {
        return compoundingStates[1];
    }

    //loads and names sprites used for pet-type selected
    /**
     * Loads and assigns textures for character animations and inventory items.
     */
    public void loadImages(){
        // Load body textures into the texture map
        characterTextures.put("neutralBody", new Texture(Gdx.files.internal("game/character/body-neutral.png")));
        characterTextures.put("workout1Body", new Texture(Gdx.files.internal("game/character/body-workout1.png")));
        characterTextures.put("workout2Body", new Texture(Gdx.files.internal("game/character/body-workout2.png")));
        characterTextures.put("hungry1Body", new Texture(Gdx.files.internal("game/character/body-hungry1.png")));
        characterTextures.put("hungry2Body", new Texture(Gdx.files.internal("game/character/body-hungry2.png")));
        characterTextures.put("bluckBody", new Texture(Gdx.files.internal("game/character/body-hold-bluck.png")));
        characterTextures.put("duckBody", new Texture(Gdx.files.internal("game/character/body-hold-duck.png")));
        characterTextures.put("orduckBody", new Texture(Gdx.files.internal("game/character/body-hold-orduck.png")));
        // Load head textures into the texture map
        characterTextures.put("head", new Texture(Gdx.files.internal("game/character/" + characterType + "-head.png")));
        characterTextures.put("blink", new Texture(Gdx.files.internal("game/character/" + characterType + "-blink.png")));
        characterTextures.put("exercise", new Texture(Gdx.files.internal("game/character/" + characterType + "-exercise.png")));
        characterTextures.put("happy", new Texture(Gdx.files.internal("game/character/" + characterType + "-happy.png")));
        characterTextures.put("angry", new Texture(Gdx.files.internal("game/character/" + characterType + "-angry.png")));
        characterTextures.put("sleep1", new Texture(Gdx.files.internal("game/character/" + characterType + "-sleep1.png")));
        characterTextures.put("sleep2", new Texture(Gdx.files.internal("game/character/" + characterType + "-sleep2.png")));
        characterTextures.put("dead", new Texture(Gdx.files.internal("game/character/" + characterType + "-dead.png")));

        // Create images from textures for bodies
        characterBodies.put("neutral", mainGame.createImage(characterTextures.get("neutralBody")));
        characterBodies.put("workout1", mainGame.createImage(characterTextures.get("workout1Body")));
        characterBodies.put("workout2", mainGame.createImage(characterTextures.get("workout2Body")));
        characterBodies.put("hungry1", mainGame.createImage(characterTextures.get("hungry1Body")));
        characterBodies.put("hungry2", mainGame.createImage(characterTextures.get("hungry2Body")));
        characterBodies.put("dead", mainGame.createImage(characterTextures.get("dead")));
        characterBodies.put("bluck", mainGame.createImage(characterTextures.get("bluckBody")));
        characterBodies.put("duck", mainGame.createImage(characterTextures.get("duckBody")));
        characterBodies.put("orduck", mainGame.createImage(characterTextures.get("orduckBody")));
        // Create images from textures for heads
        characterHeads.put("head", mainGame.createImage(characterTextures.get("head")));
        characterHeads.put("blink", mainGame.createImage(characterTextures.get("blink")));
        characterHeads.put("exercise", mainGame.createImage(characterTextures.get("exercise")));
        characterHeads.put("happy", mainGame.createImage(characterTextures.get("happy")));
        characterHeads.put("angry", mainGame.createImage(characterTextures.get("angry")));
        characterHeads.put("sleep1", mainGame.createImage(characterTextures.get("sleep1")));
        characterHeads.put("sleep2", mainGame.createImage(characterTextures.get("sleep2")));

        // Load inventory textures into the texture map
        characterTextures.put("appleFrame", new Texture(Gdx.files.internal("game/inventory/apple-frame.png")));
        characterTextures.put("bluckFrame", new Texture(Gdx.files.internal("game/inventory/bluck-frame.png")));
        characterTextures.put("duckFrame", new Texture(Gdx.files.internal("game/inventory/duck-frame.png")));
        characterTextures.put("lemonFrame", new Texture(Gdx.files.internal("game/inventory/lemon-frame.png")));
        characterTextures.put("orangeFrame", new Texture(Gdx.files.internal("game/inventory/orange-frame.png")));
        characterTextures.put("orduckFrame", new Texture(Gdx.files.internal("game/inventory/orduck-frame.png")));

        //load sound effects
        munchSound = Gdx.audio.newSound(Gdx.files.internal("music/eating.mp3"));
        quackSound = Gdx.audio.newSound(Gdx.files.internal("music/quack-quack.mp3"));
    }

    //dispose method for textures
    /**
     * Disposes of textures, images, and other resources used by the character.
     */
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
