package com.kaobells.group44;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

/**
 * The {@code Database} class serves as a container for game data, including
 * game slots, character definitions, and parental controls.
 *
 * This class provides methods for managing data persistence and ensures that
 * all fields are initialized to prevent null references.
 *
 * @author group44
 * @version 1.0
 * @see JsonHandler
 */
class Database {
    /** Map representing game slots and their associated data. */
    public HashMap<String, HashMap<String, Object>> games;
    /** Map of available character classes and their attributes. */
    public HashMap<String, CharacterClass> characters;
    /** Map for parental controls data, such as playtime limits and password settings. */
    public HashMap<String, Object> parentalControls; // Field for "Parental Controls"
    /** Flag indicating whether a parental control password has been set. */
    private boolean isPasswordSet;

    /**
     * Constructs a new {@code Database} instance and initializes all fields.
     * Constructor for initializing fields if null
     */
    public Database() {
        games = new HashMap<>();
        characters = new HashMap<>();
        parentalControls = new HashMap<>();
    }


}

/**
 * The {@code JsonHandler} class handles data persistence for the application,
 * reading from and writing to a JSON file that stores the game's database.
 *
 * This class integrates with LibGDX utilities for file handling and JSON processing.
 *
 * @author group44
 * @version 1.0
 * @see Database
 */
public class JsonHandler {
    /** The local file used for storing the JSON database. */
    private FileHandle localFile;
    /** The in-memory representation of the game's database. */
    private Database database;
    /** JSON utility object for serialization and deserialization. */
    private final Json json = new Json(); // LibGDX JSON utility

    /**
     * Initializes the {@code JsonHandler} by ensuring the local file exists
     * and loading the database from it.
     */
    public JsonHandler() {
        initializeLocalFile();
        loadDatabase();
    }

    /**
     * Ensures that the local file exists, creating it if necessary.
     */
    private void initializeLocalFile() {
        FileHandle internalFile = Gdx.files.internal("database.json");
        localFile = Gdx.files.local("database.json");

        if (!localFile.exists()) {
            internalFile.copyTo(localFile);
            System.out.println("Database JSON created in local storage");
        } else {
            System.out.println("Local Database JSON previously created.");
        }
    }

    /**
     * Loads the database from the local file, initializing it with default values if the file is missing.
     */
    private void loadDatabase() {
        if (localFile.exists()) {
            String jsonString = localFile.readString();
            json.setIgnoreUnknownFields(true); // Ignore fields not present in the Database class
            database = json.fromJson(Database.class, jsonString);
        } else {
            database = new Database();
            initializeParentalControls(); // Ensure parentalControls is initialized
            saveDatabase();
        }
    }

    /**
     * Saves the current state of the database to the local JSON file.
     */
    public void saveDatabase() {
        // Serialize the database object to a string
        json.setOutputType(JsonWriter.OutputType.json);

        String jsonString = json.prettyPrint(database);

        // Write the properly formatted JSON to the file
        localFile.writeString(jsonString, false);

        System.out.println("Database saved successfully in proper JSON format.");
    }

    /**
     * Returns the current in-memory {@code Database} object.
     *
     * @return The game's database.
     */
    public Database getDatabase() {
        return database;
    }

    /**
     * Checks if there are any saved game files in the database.
     *
     * @return {@code true} if any game slots contain saved data, {@code false} otherwise.
     */
    public boolean isSavedFiles() {
        // Iterate over all game slots
        for (Map.Entry<String, HashMap<String, Object>> entry : database.games.entrySet()) {
            // Check if the slot contains any data
            if (!entry.getValue().isEmpty()) {
                return true; // Found a slot with saved data
            }
        }
        return false; // No saves found
    }

    /**
     * Checks if the parental controls are empty or uninitialized.
     *
     * @return {@code true} if parental controls are null or empty, {@code false} otherwise.
     */
    public boolean isEmptyParentalControls() {
        return database.parentalControls == null || database.parentalControls.isEmpty();
    }

    /**
     * Prints the current state of parental controls to the console.
     */
    public void printStuff(){

        System.out.println(database.parentalControls);
    }


    /**
     * Saves a character to a specified game slot in the database.
     *
     * @param slotId    The ID of the game slot where the character should be saved.
     * @param character The {@link CharacterClass} object representing the character to save.
     */
    // Method to save a CharacterClass to a specific game slot
    public void saveCharacterToGameSlot(String slotId, CharacterClass character) {
        if (database.games.containsKey(slotId)) {
            // Update the game slot with new data
            database.games.get(slotId).put("character", character);

            saveDatabase(); // Save the updated database to file
            System.out.println("Character saved to game slot: " + slotId);
        } else {
            System.out.println("Invalid game slot ID: " + slotId);
        }
    }

    /**
     * Retrieves a character from a specified game slot in the database.
     *
     * @param slotId The ID of the game slot from which to retrieve the character.
     * @return The {@link CharacterClass} object representing the character,
     * or {@code null} if no character is found in the specified slot.
     */
    // Optional: Retrieve a character from a specific game slot
    public CharacterClass getCharacterFromGameSlot(String slotId) {
        if (database.games.containsKey(slotId) && database.games.get(slotId).containsKey("character")) {
            return (CharacterClass) database.games.get(slotId).get("character");
        }
        return null; // Return null if no character is found
    }

    // Method to get a human-readable representation of a game slot
    /**
     * Provides a human-readable representation of the data in a game slot.
     *
     * @param slotId The ID of the game slot to convert to a string.
     * @return A string representation of the game slot's data, or an error message if the slot does not exist.
     */
    public String gameToString(String slotId) {
        if (database.games.containsKey(slotId)) {
            HashMap<String, Object> gameData = database.games.get(slotId);
            StringBuilder gameString = new StringBuilder();
            gameString.append("Game Slot ").append(slotId).append(":\n");

            for (Map.Entry<String, Object> entry : gameData.entrySet()) {
                gameString.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }

            return gameString.toString();
        } else {
            return "Game Slot " + slotId + " does not exist or is empty.";
        }
    }

    /**
     * Initializes the parental controls in the database with default values.
     * This includes settings for playtime blocks, total playtime, and sessions.
     */
    public void initializeParentalControls() {
        //database.parentalControls.put("Password", "");
        database.parentalControls.put("morningParentBlock", false);
        database.parentalControls.put("afternoonParentBlock", false);
        database.parentalControls.put("eveningParentBlock", false);
        database.parentalControls.put("weekdayParentBlock", false);
        database.parentalControls.put("weekendParentBlock", false);
        database.parentalControls.put("totalSecondsPlayed", 0);
        database.parentalControls.put("totalSessionsPlayed", 0);
        database.parentalControls.put("averagePlaytimePerSession", 0);
        saveDatabase();
        System.out.println("Parental controls initialized with default values.");
    }

    /**
     * Displays a message when playtime is blocked due to parental controls.
     *
     * @param stage    The stage on which the message should be displayed.
     * @param viewport The viewport used to size and position elements.
     * @param mainGame The main game instance, used for navigation.
     */
    public void showBlockedTimeMessage(Stage stage, Viewport viewport, Main mainGame) {
        // Clear previous actors
        stage.clear();
        // Create a semi-transparent overlay
        Texture overlayTexture = new Texture(Gdx.files.internal("parentalControlsScreen/times-up-txtbox.png"));
        Image overlayImage = new Image(new TextureRegionDrawable(new TextureRegion(overlayTexture)));
        // Center the overlay image
        overlayImage.setSize(viewport.getWorldWidth() * 0.7f, viewport.getWorldHeight() * 0.3f);
        overlayImage.setPosition(
            (viewport.getWorldWidth() - overlayImage.getWidth()) / 2,
            (viewport.getWorldHeight() - overlayImage.getHeight()) / 2
        );
        //add to stage
        stage.addActor(overlayImage);
        // Add the back button
        ImageButton backButton = mainGame.getBackButton();
        backButton.setSize(viewport.getWorldWidth() * 0.1f, viewport.getWorldHeight() * 0.1f);
        backButton.setPosition(
            (viewport.getWorldWidth() - backButton.getWidth()) / 2, // Center horizontally
            viewport.getWorldHeight() * 0.2f // Place below the message
        );

        // Clear any existing listeners on the back button to prevent stacking
        backButton.clearListeners();
        //on click clear stack then push a new StartScreen
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mainGame.clearStackExceptMain();
                mainGame.popScreen();
                mainGame.pushScreen(new StartScreen(mainGame)); // Go back to the main menu
            }
        });
        stage.addActor(backButton);

        //for pressing M to go back
        InputAdapter inputAdapter = new InputAdapter(){
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.M) {
                    mainGame.clearStackExceptMain();
                    mainGame.popScreen();
                    mainGame.pushScreen(new StartScreen(mainGame));
                    return true;
                }
                return true;
            }
        };

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);       // Stage for UI interactions
        multiplexer.addProcessor(inputAdapter); // InputAdapter for key presses
        Gdx.input.setInputProcessor(multiplexer);  //setting to multiplexer
    }


    /**
     * Updates the parental control password if the given password is not null or empty.
     *
     * @param password The new password for parental controls.
     */
    public void setParentalPassword(String password) {
        database.parentalControls.put("Password", password);
        saveDatabase();

    }

    /**
     * Retrieves the parental control password.
     *
     * @return The parental control password as a {@link String}.
     */
    public String getParentalPassword() {
        return (String) database.parentalControls.get("Password");
    }

    /**
     * Retrieves a boolean value for a specified parental control key.
     *
     * @param key The key of the parental control to retrieve.
     * @return The boolean value associated with the specified key.
     */
    public boolean getParentalControlBoolean(String key) {
        return (boolean) database.parentalControls.getOrDefault(key, false);
    }

    /**
     * Retrieves an integer value for a specified parental control key.
     *
     * @param key The key of the parental control to retrieve.
     * @return The integer value associated with the specified key.
     */
    public int getParentalControlInt(String key) {
        return (int) database.parentalControls.get(key);
    }

    /**
     * Updates the value of a specified parental control key with a new integer value.
     *
     * @param key   The key of the parental control to update.
     * @param value The new integer value to set.
     */
    public void setParentalControlInt(String key, int value) {
        database.parentalControls.put(key, value);
        saveDatabase();
    }


    // Method to save the parental control password
    /**
     * Sets the parental control password.
     *
     * @param password The password to set for parental controls.
     */
    public void setParentalControlPassword(String password) {
        if (password != null && !password.isEmpty()) {
            database.parentalControls.put("Password", password);
            saveDatabase(); // Save the updated password to the file
            System.out.println("Password updated successfully.");
        } else {
            System.out.println("Password cannot be null.");
        }
    }
}
