package com.kaobells.group44;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@code NameInput} class represents the screen where the user enters
 * the name of their character. It allows users to type in a name using a
 * custom text input field and transition to the next screen after confirming.
 *
 * <p>The screen includes UI components such as a back button, character image,
 * and a styled text input field for entering names. Validation ensures that
 * the name meets specific requirements before proceeding.</p>
 *
 * @author group 44
 * @version 1.0
 */
public class NameInput extends ScreenAdapter {

    private final Main mainGame;
    private final String slot;
    private final CharacterSelection previousScreenVar;

    private final SpriteBatch spriteBatch;
    private final Stage stage;
    private final Viewport viewport;

    private final ImageButton backButton;
    private final Table error;

    private final Map<String, Texture> textures;
    private TextField nameInputField;

    BitmapFont font;
    BitmapFont textFont;

    /**
     * Constructs a new {@code NameInput} screen.
     *
     * @param game       The main game instance, managing resources and transitions.
     * @param slotNumber The save slot for the character being created.
     */
    public NameInput(Main game, String slotNumber) {
        mainGame = game;
        slot = slotNumber;
        previousScreenVar = (CharacterSelection)mainGame.getPreviousScreen();

        font = mainGame.resourceManager.getTitleFont();
        textFont = mainGame.resourceManager.getFont(true);

        textures = new HashMap<>();

        spriteBatch = mainGame.getSharedBatch();
        viewport = mainGame.getViewport();
        backButton = mainGame.getBackButton();
        error = mainGame.getErrorMessage();

        stage = new Stage(viewport, spriteBatch);

    }

    /**
     * Called when the screen becomes visible. Loads textures, sets the input processor,
     * and creates the UI.
     */
    public void show() {
        super.show();
        loadTextures();
        setStage(); // Reset input processor
        createUI();
    }

    /**
     * Creates the user interface for the name input screen, including the
     * input field, question box, and confirmation button.
     */
    public void createUI() {
        stage.clear();

        stage.addActor(backButton);
        stage.addActor(error);

        // Create and configure the custom input box
        nameInputField = createInputBox();

        // Create a table to center the input box
        Table table = new Table();
        table.setFillParent(true);

        // Calculate dynamic padding
        float topPad = viewport.getWorldHeight() * 0.1f; // 5% of the viewport height
        float rowPadding = viewport.getWorldHeight() * 0.05f; // 5% of the viewport height

        Image nameQuestion = mainGame.createImage(textures.get("inputQuestion"));
        ImageButton nameBegin = mainGame.createImageButton(textures.get("inputBegin"));

        Image characterImage = mainGame.createImage(textures.get("character"));

        nameBegin.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!nameInputField.getText().isEmpty()){
                    // starting game

                    // Clear all screens except the main menu, memory saver
                    mainGame.clearStackExceptMain();

                    Item[] inventory = new Item[6];

                    boolean[] compoundingStates= new boolean[3];
                    CharacterClass newCharacter = new CharacterClass(mainGame, nameInputField.getText(), previousScreenVar.getCharacterIndex(), previousScreenVar.getCharacterType(previousScreenVar.getCharacterIndex()),inventory, compoundingStates, slot,0);

                    GameSession newGame = new GameSession(newCharacter,mainGame);
                    if(!(newGame.blockedPlayTimeCheck())){ //checks for playing during active parental block
                        mainGame.pushScreen(new GameScreen(mainGame, newGame));
                    } else {
                        mainGame.jsonHandler.showBlockedTimeMessage(stage, viewport, mainGame);
                    }
                }
                else{
                    mainGame.sendError("Sorry buster your not get away with today with XIAO HONG SHUUUUUUU ");
                }
            }
        });

        // Add elements with padding between rows
        table.add(nameQuestion)
            .width(viewport.getWorldWidth() * 0.45f) // Adjust width
            .height(viewport.getWorldHeight() * 0.15f) // Adjust height
            .center().padTop(topPad)
            .padBottom(rowPadding); // Add bottom padding
        table.row(); // Move to the next row

        Table rowTable = new Table();

        // Add the character image aligned to the left
        rowTable.add(characterImage)
            .width(viewport.getWorldWidth() * 0.1f) // Adjust width
            .height(viewport.getWorldHeight() * 0.15f) // Adjust height
            .left()
            .padRight(viewport.getWorldWidth() * 0.025f); // Add some padding to the right of the image

// Add the input box aligned with the question box
        rowTable.add(nameInputField)
            .width(viewport.getWorldWidth() * 0.5f) // Adjust width
            .height(viewport.getWorldHeight() * 0.15f) // Adjust height
            .center(); // Center the input field in the row

// Add the row table to the main table
        table.add(rowTable)
            .width(viewport.getWorldWidth()) // Span the full width
            .center().padRight(viewport.getWorldWidth() * 0.12f) // Push row further to the left
            .padBottom(rowPadding); // Add bottom padding
        table.row(); // Move to the next row

        table.add(nameBegin)
            .width(viewport.getWorldWidth() * 0.3f) // Adjust width
            .height(viewport.getWorldHeight() * 0.1f) // Adjust height
            .center();

        // Add the table to the stage
        stage.addActor(table);
    }


    /**
     * Creates and configures the custom text input field.
     *
     * @return A configured {@code TextField} instance.
     */
    private TextField createInputBox() {
        // Calculate scaling factors relative to the viewport
        float fontScale = viewport.getWorldHeight() * 0.002f; // Scale font size relative to viewport height
        float paddingLeft = viewport.getWorldWidth() * 0.02f; // Padding from the left (2% of viewport width)
        float cursorWidth = viewport.getWorldWidth() * 0.004f;  // Adjust the width to 1% of the viewport width

        // Load the custom font
        textFont.getData().setScale(fontScale); // Set font scale dynamically

        // Create the TextFieldStyle
        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = textFont; // Use the scaled font

        textFieldStyle.fontColor = com.badlogic.gdx.graphics.Color.BLACK; // Text color

        // Input box background
        textFieldStyle.background = new TextureRegionDrawable(new TextureRegion(textures.get("inputBox")));
        textFieldStyle.background.setLeftWidth(paddingLeft); // Dynamic left padding

        // Optional custom cursor
        Texture cursorTexture = textures.get("cursor"); // Replace with your cursor image path
        textFieldStyle.cursor = new TextureRegionDrawable(new TextureRegion(cursorTexture));
        textFieldStyle.cursor.setMinWidth(cursorWidth); // Set cursor width

        // Create the TextField
        TextField textField = new TextField("", textFieldStyle);
        textField.setMessageText("Enter a name..."); // Placeholder text

        // Set alignment or position text slightly to the right
        textField.setAlignment(com.badlogic.gdx.utils.Align.left); // Align text to the left

        // Add a custom filter to limit input to 12 characters
        textField.setTextFieldFilter(new TextField.TextFieldFilter() {
            @Override
            public boolean acceptChar(TextField textField, char c) {
                // Allow the character only if the text length is less than 15
                return textField.getText().length() < 10;
            }
        });

        return textField;
    }

    /**
     * Loads textures required for the name input screen.
     */
    public void loadTextures(){

        textures.put("inputQuestion", new Texture(Gdx.files.internal("NameInput/name-txtbox.png")));
        textures.put("inputBegin", new Texture(Gdx.files.internal("NameInput/begin-btn.png")));
        textures.put("inputBox", new Texture(Gdx.files.internal("NameInput/name-input-box.png"))); // Replace with your image path
        textures.put("cursor", new Texture(Gdx.files.internal("NameInput/cursor.png")));

        String charImagePath = "characters/" + previousScreenVar.getCharacterType(previousScreenVar.getCharacterIndex()) + "-head.png";
        textures.put("character", new Texture(Gdx.files.internal(charImagePath)));
    }


    /**
     * Renders the screen, including the background and UI elements.
     *
     * @param delta The time in seconds since the last frame.
     */
    @Override
    public void render(float delta) {
        // Clear the screen
        ScreenUtils.clear(0, 0, 0, 1);

        spriteBatch.begin();
        // Draw the current background
        mainGame.drawBackground(spriteBatch, mainGame.resourceManager.get("mainBackground"), font, "New Game");


        spriteBatch.end();

        // Render the stage (actors like buttons, character sprites, etc.)
        stage.act(delta);
        stage.draw();
    }

    /**
     * Updates the viewport size on window resize.
     *
     * @param width  The new width of the window.
     * @param height The new height of the window.
     */
    public void resize(int width, int height) {
        viewport.update(width, height, true);

    }

    /**
     * Disposes of resources used by the name input screen.
     */
    @Override
    public void dispose() {
        for (Texture texture : textures.values()) {
            texture.dispose();
        }

        stage.dispose();
    }

    /**
     * Sets the input processor for the stage.
     */
    public void setStage(){
        Gdx.input.setInputProcessor(stage);
    }
}
