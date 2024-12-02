package com.kaobells.group44;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

class InputBox extends Actor {
    private char character; // The character typed in this box
    private Image background; // The box's background
    private final BitmapFont font; // Font for rendering the character
    private final GlyphLayout layout; // Helps center the character

    private boolean isGreen;
    private boolean matched;

    public InputBox(Texture backgroundTexture, BitmapFont font) {
        this.character = '\0'; // Empty by default
        this.background = new Image(new TextureRegionDrawable(backgroundTexture));
        this.font = font;
        this.layout = new GlyphLayout();

        isGreen = false;
        matched = false;

        setSize(80, 80); // Set default size for the input box
    }

    public void setCharacter(char c) {
        this.character = Character.toUpperCase(c);
    }
    public char getCharacter() {
        return Character.toLowerCase(this.character);
    }

    public void setImage(Texture backgroundTexture){
        this.background = new Image(new TextureRegionDrawable(backgroundTexture));
    }

    public boolean getIsGreen(){
        return isGreen;
    }
    public void setIsGreen(Boolean val){
        isGreen = val;
    }


    public boolean getMatched(){
        return matched;
    }
    public void setMatched(Boolean val){
        matched = val;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        // Draw the background
        background.setPosition(getX(), getY());
        background.setSize(getWidth(), getHeight());
        background.draw(batch, parentAlpha);

        // Draw the character centered in the box
        if (character != '\0') {
            String text = String.valueOf(character);
            layout.setText(font, text);

            float textX = getX() + (getWidth() - layout.width) / 2;
            float textY = getY() + (getHeight() + layout.height) / 2;
            font.draw(batch, text, textX, textY);
        }
    }
}


public class JBordle extends ScreenAdapter {
    private final Main mainGame;
    private final CharacterClass player;
    private final SpriteBatch spriteBatch;
    private final Stage stage;
    private final Viewport viewport;

    private final ImageButton backButton;
    private Map<String, Texture> textures;

    private final ArrayList<ArrayList<InputBox>> rows = new ArrayList<>();
    private int currentRowIndex = 0;
    private int currentBoxIndex = 0;

    private String targetWord; // The word the user needs to guess
    List<String> words;

    private BitmapFont font;

    // CONTAINER THING FOR EITHER HINT, WIN OR LOSE
    Container<Actor> container;
    private InputMultiplexer multiplexer;

    public JBordle(Main game,CharacterClass playa) {
        mainGame = game;
        player = playa;

        spriteBatch = mainGame.getSharedBatch();
        viewport = mainGame.getViewport();

        loadTextures();

        backButton = mainGame.getBackButton();
        stage = new Stage(viewport, spriteBatch);
    }

    @Override
    public void show() {
        createUI();
        super.show();
        setStage(); // Reset input processor
    }

    public void setStage() {
        if (multiplexer == null) {
            // Create an InputAdapter for handling keyboard input
            InputAdapter inputAdapter = new InputAdapter() {
                @Override
                public boolean keyTyped(char character) {
                    ArrayList<InputBox> currentRow = rows.get(currentRowIndex);

                    // Handle letter input
                    if (Character.isLetter(character) && currentBoxIndex < currentRow.size()) {
                        currentRow.get(currentBoxIndex).setCharacter(Character.toUpperCase(character));
                        currentBoxIndex++;
                        return true;
                    }

                    // Handle "Enter" key for validation
                    if (character == '\r' || character == '\n') { // 'Enter' key
                        if (currentBoxIndex == currentRow.size()) {
                            StringBuilder word = new StringBuilder();
                            for (InputBox inputBox : currentRow) {
                                word.append(inputBox.getCharacter());
                            }

                            if (validWord(word.toString())) {
                                validateWord(currentRowIndex);
                                currentRowIndex++;
                                currentBoxIndex = 0;
                            } else {
                                for (InputBox inputBox : currentRow) {
                                    inputBox.setCharacter('\0');
                                }
                                currentBoxIndex = 0;
                            }
                        }
                        return true;
                    }

                    // Handle "Backspace" key
                    if (character == '\b' && currentBoxIndex > 0) {
                        currentBoxIndex--;
                        currentRow.get(currentBoxIndex).setCharacter('\0');
                        return true;
                    }

                    return false;
                }
            };

            // Initialize InputMultiplexer
            multiplexer = new InputMultiplexer();
            multiplexer.addProcessor(stage);       // Allow Stage UI interactions
            multiplexer.addProcessor(inputAdapter); // Allow keyboard input handling
        }

        Gdx.input.setInputProcessor(multiplexer); // Set multiplexer as the input processor
    }


    public void createUI() {
        // Create and configure the background

        Image background = new Image(new TextureRegionDrawable(textures.get("computerScreenBg")));
        background.setSize(stage.getWidth(), stage.getHeight());
        background.setPosition(0, 0);
        stage.addActor(background);


        // Create a table for layout
        Table table = new Table();
        table.setFillParent(true); // Make the table fill the screen
        table.top().padTop(viewport.getWorldWidth() * 0.06f);   // Align the table to the top of the screen with some padding
        stage.addActor(table);

        float pad = viewport.getWorldWidth() * 0.0060f;

        table.add(createTitleTable()).padBottom(pad).row();
        table.add(createInputRowsTable()).row();
        table.add(createHintTable()).padTop(pad);

        randomWordSelector();

        float buttonWidth = viewport.getWorldWidth() * 0.1f; // 10% of the viewport width
        float buttonHeight = viewport.getWorldHeight() * 0.1f; // 10% of the viewport height
        float offsetX = viewport.getWorldWidth() * 0.08f; // Add 2% of the viewport width as offset

// Move the backButton slightly to the right by adding offsetX
        backButton.setPosition(buttonWidth + offsetX, viewport.getWorldHeight() - buttonHeight - buttonHeight);

        stage.addActor(backButton);
    }


    public Table createTitleTable(){
        Table table = new Table();
        Image title = mainGame.createImage(textures.get("title"));
        table.add(title);
        return table;
    }

    public Table createHintTable(){
        Table table = new Table();
        container = new Container<>();

        Image hint = mainGame.createImage(textures.get("hint"));
        container.setActor(hint);

        // Optional: Configure the container (e.g., alignment, padding, etc.)
        container.center(); // Center the actor within the contai

        table.add(container);
        return table;
    }

    public Table createInputRowsTable(){

        // Create a table for layout
        Table table = new Table();

        float width = viewport.getWorldWidth() * 0.045f;
        float height = viewport.getWorldWidth() * 0.045f;

        System.out.println(Float.toString(width));
        float pad = viewport.getWorldWidth() * 0.0075f;

        // Create rows and boxes for the Wordle grid
        for (int rowIndex = 0; rowIndex < 6; rowIndex++) { // Assuming 6 rows for Wordle
            ArrayList<InputBox> inputRow = new ArrayList<>();
            rows.add(inputRow); // Save the row for input handling

            for (int i = 0; i < 5; i++) { // 5 boxes per row
                InputBox inputBox = new InputBox(textures.get("tile"), font);
                inputRow.add(inputBox);

                // Add the `Image` actor (box) to the table
                table.add(inputBox).size(width, height).pad(pad); // Adjust size and padding as needed
            }

            // Move to the next row
            table.row();
        }
        return table;
    }


    // Selects a random word from the file
    public void randomWordSelector() {
        String filePath = "computerScreen/word-bank.csv"; // Replace with the path to your CSV file
        words = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) { // Skip empty lines
                    words.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
            return;
        }

        if (words.isEmpty()) {
            System.out.println("The file is empty or contains no valid words.");
            return;
        }

        Random random = new Random();
        targetWord = words.get(random.nextInt(words.size())); // Save the selected word
        targetWord = targetWord.toLowerCase();
        System.out.println("A word has been chosen! Try to guess it." + targetWord);
    }

    public boolean validWord(String word){
        word = word.toLowerCase();
        for (String s : words) {
            if (word.equals(s)) {
                return true;
            }
        }
        return false;
    }

    private void validateWord(int rowIndex) {
        // Extract the word from the row
        ArrayList<InputBox> row = rows.get(rowIndex);
        StringBuilder word = new StringBuilder();

        for (InputBox inputBox : row) {
            word.append(inputBox.getCharacter());
        }
        String wordGuessed = String.valueOf(word).toLowerCase();

        System.out.println("Validating word: " + wordGuessed);

        // Check for correct positions (green)
        for (int i = 0; i < row.size(); i++) {
            InputBox inputBox = row.get(i);
            char letter = inputBox.getCharacter();

            if (targetWord.charAt(i) == letter) {
                inputBox.setImage(textures.get("greenTile"));
                inputBox.setIsGreen(true);
            }
        }

        // Check for incorrect positions (yellow)
        for (int i = 0; i < row.size(); i++) {
            InputBox inputBox = row.get(i);

            if (!inputBox.getIsGreen()){
                for (int j = 0; j < row.size(); j++) {
                    InputBox inputBoxDeep = row.get(j);
                    char letter = inputBoxDeep.getCharacter();
                    if (!inputBoxDeep.getMatched() && !inputBoxDeep.getIsGreen() && targetWord.charAt(i) == letter){
                        inputBoxDeep.setImage(textures.get("yellowTile"));
                        inputBoxDeep.setMatched(true);
                        break;
                    }
            }
        }
    }

        if (wordGuessed.equals(targetWord)) {
            // Win case
            container.setActor(loadEndGame("win"));
            container.setTouchable(Touchable.enabled);
            player.gainItem((int) (Math.random() * 6));

        } else if (rowIndex == rows.size() - 1) {
            // Lose case
            container.setActor(loadEndGame("lose"));
            container.setTouchable(Touchable.enabled);
            player.setScore(Math.max(0, player.getScore()-20));
        }

    }

    public ImageButton loadEndGame(String status) {
        if (multiplexer != null) {
            multiplexer.clear();                // Remove all existing processors
            multiplexer.addProcessor(stage);    // Only allow Stage interactions
            Gdx.input.setInputProcessor(multiplexer); // Update the input processor
        }

        ImageButton endGameButton = null;

        if (status.equalsIgnoreCase("win")) {
            endGameButton = mainGame.createImageButton(textures.get("win"));
        } else if (status.equalsIgnoreCase("lose")) {
            endGameButton = mainGame.createImageButton(textures.get("lose"));
        }

        if (endGameButton != null) {
            endGameButton.setTouchable(Touchable.enabled); // Ensure the button is clickable

            endGameButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    System.out.println("End game button clicked!");

                    // Example: Return to main menu
                    mainGame.popScreen();
                }
            });
        }


        return endGameButton;
    }

    public void loadTextures() {
        textures = new HashMap<>();
        textures.put("computerScreenBg", new Texture(Gdx.files.internal("computerScreen/computerScreen-bg.png")));
        textures.put("tile", new Texture(Gdx.files.internal("computerScreen/jbordle-tile.png")));
        textures.put("yellowTile", new Texture(Gdx.files.internal("computerScreen/jbordle-yellow-tile.png")));
        textures.put("greenTile", new Texture(Gdx.files.internal("computerScreen/jbordle-green-tile.png")));

        textures.put("title", new Texture(Gdx.files.internal("computerScreen/jbordle-title.png")));
        textures.put("hint", new Texture(Gdx.files.internal("computerScreen/jbordle-hints.png")));

        textures.put("win", new Texture(Gdx.files.internal("computerScreen/jbordle-win-txt.png")));
        textures.put("lose", new Texture(Gdx.files.internal("computerScreen/jbordle-lose-txt.png")));

        // Initialize the font
        font = mainGame.resourceManager.getFont(true);// Replace with your font file
    }

    @Override
    public void render(float delta) {
        // Clear the screen with a specific color (e.g., black)
        ScreenUtils.clear(0, 0, 0, 1);

        // Update the stage (processes input and updates actors)
        stage.act(delta);

        // Draw all actors in the stage, including InputBoxes
        stage.draw();

        // Optionally: Draw custom overlays, debugging info, or other elements
        spriteBatch.begin();
        // Example: Draw custom text or images
        // font.draw(spriteBatch, "Your overlay text", 10, 20);
        spriteBatch.end();
    }


    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        for (Texture texture : textures.values()) {
            texture.dispose();
        }
        stage.dispose();
    }
}

