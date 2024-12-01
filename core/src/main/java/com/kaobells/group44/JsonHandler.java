package com.kaobells.group44;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

import java.util.HashMap;
import java.util.Map;

class Database {
    public HashMap<String, HashMap<String, Object>> games;
    public HashMap<String, CharacterClass> characters;
    public HashMap<String, Object> parentalControls; // Field for "Parental Controls"

    // Constructor for initializing fields if null
    public Database() {
        games = new HashMap<>();
        characters = new HashMap<>();
        parentalControls = new HashMap<>();
    }

    // Initialize parentalControls with default values
    public void initializeParentalControls() {
        if (parentalControls == null || parentalControls.isEmpty()) {
            parentalControls.put("Password", "");
            parentalControls.put("morningParentBlock", false);
            parentalControls.put("afternoonParentBlock", false);
            parentalControls.put("eveningParentBlock", false);
            parentalControls.put("weekdayParentBlock", false);
            parentalControls.put("weekendParentBlock", false);
            parentalControls.put("totalSecondsPlayed", 0);
            parentalControls.put("totalSessionsPlayed", 0);
            parentalControls.put("averagePlaytimePerSession", 0);
        }
    }
}

public class JsonHandler {
    private FileHandle localFile;
    private Database database;
    private final Json json = new Json(); // LibGDX JSON utility

    public JsonHandler() {
        initializeLocalFile();
        loadDatabase();
    }

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

    private void loadDatabase() {
        if (localFile.exists()) {
            String jsonString = localFile.readString();
            json.setIgnoreUnknownFields(true); // Ignore fields not present in the Database class
            database = json.fromJson(Database.class, jsonString);
        } else {
            database = new Database();
            database.initializeParentalControls(); // Ensure parentalControls is initialized
            saveDatabase();
        }
    }

    public void saveDatabase() {
        // Serialize the database object to a string
        json.setOutputType(JsonWriter.OutputType.json);

        String jsonString = json.prettyPrint(database);

        // Write the properly formatted JSON to the file
        localFile.writeString(jsonString, false);

        System.out.println("Database saved successfully in proper JSON format.");
    }

    public Database getDatabase() {
        return database;
    }

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

    public boolean isEmptyParentalControls() {
        return database.parentalControls == null || database.parentalControls.isEmpty();
    }

    public void initializeParentalControls() {
        database.initializeParentalControls();
        saveDatabase();
    }

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

    // Optional: Retrieve a character from a specific game slot
    public CharacterClass getCharacterFromGameSlot(String slotId) {
        if (database.games.containsKey(slotId) && database.games.get(slotId).containsKey("character")) {
            return (CharacterClass) database.games.get(slotId).get("character");
        }
        return null; // Return null if no character is found
    }

    // Method to get a human-readable representation of a game slot
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
}
