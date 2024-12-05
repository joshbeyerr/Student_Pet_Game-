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
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@code characterSelection} class is responsible for displaying a screen
 * where the player can select a character for their game. The screen includes
 * UI elements such as arrows for navigating between characters, a preview of
 * the current character, and a selection button to confirm the choice.
 *
 * <p>This class handles the loading of character textures, updating the displayed
 * character, and transitioning to the next screen upon selection.</p>
 *
 * @author group 44
 * @version 1.0
 */
public class CharacterSelection extends ScreenAdapter {

    /** Reference to the main game instance. */
    private final Main mainGame;

    /** Slot identifier associated with this instance. */
    private final String slot;

    /** Sprite batch used for rendering. */
    private final SpriteBatch spriteBatch;

    /** Stage for managing and rendering UI components. */
    private final Stage stage;

    /** Viewport for handling screen size and scaling. */
    private final Viewport viewport;

    /** Button used for navigating back in the UI. */
    private final ImageButton backButton;

    /** Table displaying error messages. */
    private final Table error;

    /** Map of textures used in the scene. */
    private Map<String, Texture> textures;

    /** Table containing character-related elements. */
    private final Table characterTable;

    /** Array holding the character images. */
    private final Image[] characters = new Image[5];

    /** Index of the currently selected character in the array. */
    private int curCharacterIndex = 0;

    /** Currently displayed character image. */
    private Image curChar;

    /** Bitmap font used for rendering text. */
    private BitmapFont font;


    /**
     * Constructs a new {@code characterSelection} screen.
     *
     * @param game       The main game instance to manage shared resources and transitions.
     * @param slotNumber The slot number representing the save slot for the character selection.
     */
    public CharacterSelection(Main game, String slotNumber) {
        mainGame = game;
        slot = slotNumber;

        spriteBatch = mainGame.getSharedBatch();
        viewport = mainGame.getViewport();

        backButton = mainGame.getBackButton();
        error = mainGame.getErrorMessage();

        stage = new Stage(viewport, spriteBatch);

        font = mainGame.resourceManager.getTitleFont();

        // Initialize the table and add it to the stage
        characterTable = new Table();
        characterTable.setFillParent(true);

        loadTextures();
        createUI();

    }

    /**
     * Resets the input processor and sets up the back button.
     */

    public void show() {
        super.show();
        setStage(); // Reset input processor

        // idk rn, this just works
        stage.addActor(backButton);
        stage.addActor(error);
    }

    /**
     * Sets up the UI elements for the character selection screen.
     */
    public void createUI(){

        Gdx.app.log("characterSelection", "createUI");

        stage.addActor(characterTable);

        setupSpriteChooseState();
    }

    /**
     * Retrieves the type of character based on the provided index.
     *
     * @param index The index of the character.
     * @return The type of character as a {@code String}.
     */
    public String getCharacterType(int index) {
        switch (index) {
            case 0: return "relaxed";
            case 1: return "quirky";
            case 2: return "hasty";
            case 3: return "brave";
            case 4: return "serious";
            default: throw new IllegalArgumentException("Invalid character index: " + index);
        }
    }

    /**
     * Retrieves the character {@code Image} for the given index, loading
     * the texture if necessary.
     *
     * @param index The index of the character to retrieve.
     * @return The {@code Image} of the character.
     */
    private Image loadChar(int index) {
        if (characters[index] == null) {
            String charKey = "char" + (index + 1); // Generate the key dynamically (e.g., "char1", "char2", ...)

            if (!textures.containsKey(charKey)) {
                textures.put(charKey, new Texture(Gdx.files.internal("characterSelect/" + getCharacterType(index) + "-btn.png")));
            }

            characters[index] = mainGame.createImage(textures.get(charKey));
        }
        return characters[index];
    }

    /**
     * Loads the textures required for character selection.
     */
    private void loadTextures() {
        textures = new HashMap<>();

        textures.put("charQuestion", new Texture(Gdx.files.internal("characterSelect/type-textbox.png")));
        textures.put("arrow", new Texture(Gdx.files.internal("characterSelect/character-arrow.png")));

        textures.put("select", new Texture(Gdx.files.internal("characterSelect/select-btn.png")));
    }

    /**
     * Sets up the character selection UI, including arrows for navigation,
     * character preview, and a select button.
     */
    private void setupSpriteChooseState() {

        // Character question
        Image charQuestion = mainGame.createImage(textures.get("charQuestion"));
        characterTable.add(charQuestion)
            .size(viewport.getWorldWidth() * 0.45f, viewport.getWorldHeight() * 0.15f)
            .colspan(3)
            .padBottom(viewport.getWorldHeight() * 0.05f)
            .padTop(viewport.getWorldHeight() * 0.1f)
            .center();

        characterTable.row(); // Move to the next row

        // Left arrow button
        ImageButton leftArrow = mainGame.createImageButton(textures.get("arrow"));
        leftArrow.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Switch to the previous character
                switchCharacter(true);
            }
        });

        // Right arrow button (flipped arrow)
        TextureRegion rightArrowRegion = new TextureRegion(textures.get("arrow"));
        rightArrowRegion.flip(true, false); // Flip horizontally
        ImageButton rightArrow = new ImageButton(new TextureRegionDrawable(rightArrowRegion));
        rightArrow.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Switch to the next character
                switchCharacter(false);
            }
        });

        float arrowWidth = viewport.getWorldWidth() * 0.1f;
        float arrowHeight = viewport.getWorldHeight() * 0.1f;

        // Add arrow buttons and current character image
        characterTable.add(leftArrow).size(arrowWidth, arrowHeight).padRight(10);

        curChar = loadChar(curCharacterIndex); // Load the initial character
        characterTable.add(curChar)
            .size(viewport.getWorldWidth() * 0.2f, viewport.getWorldHeight() * 0.25f)
            .padLeft(10)
            .padRight(10);

        characterTable.add(rightArrow).size(arrowWidth, arrowHeight).padLeft(10);

        characterTable.row(); // Move to the next row

        // Select button
        ImageButton charSelect = mainGame.createImageButton(textures.get("select"));

        charSelect.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Switch to the previous character
                mainGame.pushScreen(new NameInput(mainGame, slot));

            }
        });

        characterTable.add(charSelect)
            .size(viewport.getWorldWidth() * 0.35f, viewport.getWorldHeight() * 0.10f)
            .colspan(3)
            .padTop(viewport.getWorldHeight() * 0.05f)
            .center();
    }

    /**
     * Updates the displayed character in the character selection table
     * to reflect the current selection.
     */
    private void updateCharacterInTable() {
        // Retrieve the new character Image using getChar
        Image newChar = loadChar(curCharacterIndex);

        // Update curChar and add it to the table

        characterTable.getCell(curChar).setActor(newChar); // Replace the actor in the same cell
        curChar = newChar;
    }

    /**
     * Switches the currently displayed character to the next or previous character.
     *
     * @param left {@code true} to switch to the previous character, {@code false} for the next character.
     */
    public void switchCharacter(boolean left) {
        // Update the character index
        if (left) {
            curCharacterIndex = (curCharacterIndex == 0) ? characters.length - 1 : curCharacterIndex - 1;
        } else {
            curCharacterIndex = (curCharacterIndex == characters.length - 1) ? 0 : curCharacterIndex + 1;
        }

        // Update the character image in the table
        updateCharacterInTable();
    }

    /**
     * Retrieves the index of the currently selected character.
     *
     * @return The index of the currently selected character.
     */
    public int getCharacterIndex(){
        return curCharacterIndex;
    }


    /**
     * Renders the character selection screen, including the background
     * and stage elements.
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
     * Resizes the viewport to fit the new window dimensions.
     *
     * @param width  The new width of the window.
     * @param height The new height of the window.
     */
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    /**
     * Disposes of all resources used by the screen, including textures
     * and the stage.
     */
    @Override
    public void dispose() {

        for (Texture texture : textures.values()) {
            texture.dispose();
        }
        stage.dispose();
    }

    /**
     * Sets the input processor for the stage to handle input events.
     */
    public void setStage(){
        Gdx.input.setInputProcessor(stage);
    }


}
