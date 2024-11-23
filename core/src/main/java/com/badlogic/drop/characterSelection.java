package com.badlogic.drop;

import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.Screen;
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
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.HashMap;
import java.util.Map;


public class characterSelection extends ScreenAdapter {

    private Main mainGame;
    private Screen previousScreen;

    private SpriteBatch spriteBatch;
    private Stage stage;
    private Viewport viewport;

    private ImageButton backButton;

    private Map<String, Texture> textures;
    private Table characterTable;

    private Image[] characters = new Image[5];
    private int curCharacterIndex = 0;
    Image curChar;

    BitmapFont font;



    public characterSelection(Main game, Screen previousScreenn) {
        mainGame = game;
        previousScreen = previousScreenn;

        spriteBatch = mainGame.getSharedBatch();
        viewport = mainGame.getViewport();
        backButton = mainGame.getBackButton();

        stage = new Stage(viewport, spriteBatch);

        font = mainGame.resourceManager.getTitleFont();

        // Clear any actors from the stage before adding new ones
        stage.clear();

        setStage();

        // Initialize the table and add it to the stage
        characterTable = new Table();
        characterTable.setFillParent(true);

        loadTextures();
        createUI();
    }


    public void createUI(){
        stage.clear();
        characterTable.clear();

        stage.addActor(backButton);
        stage.addActor(characterTable);

        setupSpriteChooseState();
    }

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


    private void loadTextures() {
        textures = new HashMap<>();

        textures.put("charQuestion", new Texture(Gdx.files.internal("characterSelect/type-textbox.png")));
        textures.put("arrow", new Texture(Gdx.files.internal("characterSelect/character-arrow.png")));

        textures.put("select", new Texture(Gdx.files.internal("characterSelect/select-btn.png")));
    }

    private void createBackButton() {
        backButton.clearListeners(); // Clear any previous listeners to avoid stacking
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                // If we're at the first story text, go back to the StartScreen
                mainGame.setScreen(previousScreen);

                Gdx.app.log("ParentalButton", "BACKHERE!");

                // Ensure the previous screen resets its input processor
                if (previousScreen instanceof StoryScreen) {
                    ((StoryScreen) previousScreen).setStage();
                }
            }
        });
    }

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
                mainGame.setScreenNoDispose(new NameInput(mainGame, characterSelection.this));

            }
        });

        characterTable.add(charSelect)
            .size(viewport.getWorldWidth() * 0.35f, viewport.getWorldHeight() * 0.10f)
            .colspan(3)
            .padTop(viewport.getWorldHeight() * 0.05f)
            .center();
    }

    private void updateCharacterInTable() {
        // Retrieve the new character Image using getChar
        Image newChar = loadChar(curCharacterIndex);

        // Update curChar and add it to the table

        characterTable.getCell(curChar).setActor(newChar); // Replace the actor in the same cell
        curChar = newChar;
    }



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

    public int getCharacterIndex(){
        return curCharacterIndex;
    }



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


    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        stage.getViewport().update(width, height, true);

        backButton = mainGame.getBackButton();
        createBackButton();

        createUI();
    }

    @Override
    public void dispose() {

        for (Texture texture : textures.values()) {
            texture.dispose();
        }
//        currentBackground.dispose();
        stage.dispose();
    }

    public void setStage(){
        Gdx.input.setInputProcessor(stage);
    }

    public void show() {
        super.show();
        setStage(); // Reset input processor
        createUI(); // Recreate the UI elements
    }

}
