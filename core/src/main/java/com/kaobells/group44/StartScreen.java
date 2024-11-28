package com.kaobells.group44;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.HashMap;
import java.util.Map;


public class StartScreen extends ScreenAdapter {

    private Main mainGame;  // Main game class for shared resources transitioning screens

    private SpriteBatch spriteBatch;
    private Viewport viewport;

    private Stage stage;

    private Table table;

    private Map<String, Texture> textures; // Store textures locally
    private UIState currentState = UIState.MAIN_MENU;

    private ImageButton startButton;
    private ImageButton loadButton;
    private ImageButton creditsButton;
    private ImageButton parentalButton;
    private ImageButton yesProceed;
    private ImageButton noBack;
    private Image parentQuestion;
    private ImageButton exitButton;

    private BitmapFont font;


    public StartScreen(Main game) {

        mainGame = game;

        spriteBatch = mainGame.getSharedBatch();
        viewport = mainGame.getViewport();

        stage = new Stage(viewport, spriteBatch);

        // load and set font color
        font = mainGame.resourceManager.getTitleFont();

        loadTextures();
        loadActors();

        // Create UI components
        initializeUI();

        setStage();

    }

    private void initializeUI() {

        // Add table to the stage
        stage.clear(); // Clear existing actors (if any)

        currentState = UIState.MAIN_MENU; // Update state

        if (table != null){
            table.clear();
        }
        else{
            table = createTable();
        }

        addActorToTable(table, startButton, false);
        addActorToTable(table, loadButton, true);
        addActorToTable(table, creditsButton, false);
        addActorToTable(table, parentalButton, false);

        exitButton = formatExitButton();

        stage.addActor(exitButton); // Add the exitButton to the stage

        stage.addActor(table);
    }

    private Table createTable() {
        Table newTable = new Table();
        newTable.setFillParent(true);
        newTable.center();
        newTable.padTop(viewport.getWorldHeight() * 0.1f);
        return newTable;
    }

    private void parentsButton(){

        currentState = UIState.PARENTAL_CONTROLS; // Update state

        table.clear();

        addCenteredActorToTable(table, parentQuestion, true);

        addActorToTable(table, noBack, false);
        addActorToTable(table, yesProceed, false);

        // Add table to the stage
        stage.clear(); // Clear existing actors (if any)
        stage.addActor(table);

    }

    private ImageButton formatExitButton(){
        // Set the size and position of the exitButton
        float buttonWidth = viewport.getWorldWidth() * 0.08f; // 8% of viewport width
        float buttonHeight = viewport.getWorldHeight() * 0.08f; // 8% of viewport height
        float padding = 100; // Padding from edges
        exitButton.setSize(buttonWidth, buttonHeight); // Set button size
        exitButton.setPosition(padding, viewport.getWorldHeight() - buttonHeight - padding); // Position button
        return exitButton;
    }

    private void addActorToTable(Table table, Actor actor, boolean newRow) {
        float actorWidth = getButtonWidth(); // For consistency, you can reuse button width/height for both
        float actorHeight = getButtonHeight();
        float actorPadding = getDynamicPadding();
        table.add(actor).size(actorWidth, actorHeight).pad(actorPadding);

        if (newRow) {
            table.row();
        }
    }

    private void addCenteredActorToTable(Table table, Actor actor, boolean newRow) {
        float actorWidth = getButtonWidth();
        float actorHeight = getButtonHeight();
        float actorPadding = getDynamicPadding();

        // Make the cell span the entire row and center the actor
        table.add(actor)
            .size(actorWidth, actorHeight) // Constrain size
            .pad(actorPadding)            // Add padding
            .colspan(2)                   // Span two columns to center in a wider area
            .center();                    // Align the actor to the center of its cell

        if (newRow) {
            table.row();
        }
    }

    private float getButtonWidth() {
        return viewport.getWorldWidth() * 0.375f; // 25% of viewport width
    }

    private float getButtonHeight() {
        return viewport.getWorldHeight() * 0.15f; // 10% of viewport height
    }

    private float getDynamicPadding() {
        return viewport.getWorldHeight() * 0.02f; // Padding as 2% of viewport height
    }

    private void loadTextures() {
        textures = new HashMap<>();

        // all textures that are native to StartScreen.java page
        textures.put("start", new Texture(Gdx.files.internal("startScreen/start-game-btn.png")));
        textures.put("load", new Texture(Gdx.files.internal("startScreen/load-game-btn.png")));
        textures.put("credits", new Texture(Gdx.files.internal("startScreen/credits-btn.png")));
        textures.put("parent", new Texture(Gdx.files.internal("startScreen/parental-controls-btn.png")));
        textures.put("confirm", new Texture(Gdx.files.internal("startScreen/parent-textbox.png")));
        textures.put("yes", new Texture(Gdx.files.internal("startScreen/proceed-parent-btn.png")));
        textures.put("no", new Texture(Gdx.files.internal("startScreen/back-parent-btn.png")));
        textures.put("exit", new Texture(Gdx.files.internal("startScreen/exit-btn.png")));
    }

    private void loadActors() {
        startButton = mainGame.createImageButton(textures.get("start"));
        loadButton = mainGame.createImageButton(textures.get("load"));
        creditsButton = mainGame.createImageButton(textures.get("credits"));
        parentalButton = mainGame.createImageButton(textures.get("parent"));
        yesProceed = mainGame.createImageButton(textures.get("yes"));
        noBack = mainGame.createImageButton(textures.get("no"));
        parentQuestion = mainGame.createImage(textures.get("confirm"));
        exitButton = mainGame.createImageButton(textures.get("exit"));


        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("StartButton", "Start Game button clicked!");
                mainGame.pushScreen(new StoryScreen(mainGame));

            }
        });

        creditsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("CreditButton", "Credits button clicked!");
                mainGame.pushScreen(new CreditScreen(mainGame));

            }
        });

        // Add action listener to the parental button
        parentalButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Perform an action, for example, print a message or switch screens
                Gdx.app.log("ParentalButton", "Parental Controls button clicked!");
                parentsButton();

            }
        });

        noBack.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Perform an action, for example, print a message or switch screens
                Gdx.app.log("noBack", "noBack clicked!");
                initializeUI();

            }
        });

        // Add action listener to the exit button
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Perform an action, for example, print a message or switch screens
                Gdx.app.log("exitButton", "Exiting game...");
                Gdx.app.exit();

            }
        });

    }

    @Override
    public void render(float delta) {
        // Clear the screen
        ScreenUtils.clear(0, 0, 0, 1);

        // Draw the background
        spriteBatch.begin();
        mainGame.drawBackground(spriteBatch, mainGame.resourceManager.get("mainBackground"), font, "Wiktor Simulator");

        spriteBatch.end();

        // Draw the stage
        stage.act(delta);

        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        if (width != 0 && height != 0) {
            viewport.update(width, height, true);
            stage.getViewport().update(width, height, true);

            // Reinitialize UI based on current state
            if (currentState == UIState.MAIN_MENU) {
                initializeUI();
            } else if (currentState == UIState.PARENTAL_CONTROLS) {
                parentsButton();
            }
        }

    }

    public void dispose() {
        // Dispose of all textures
        for (Texture texture : textures.values()) {
            texture.dispose();
        }
        textures.clear();

        // Dispose of the stage (clears all actors)
        stage.dispose();
    }

    public void setStage(){
        // Set the input processor to the stage
        Gdx.input.setInputProcessor(stage);

    }

    private enum UIState {
        MAIN_MENU,
        PARENTAL_CONTROLS
    }

    @Override
    public void show() {
        loadTextures();
        loadActors();
        initializeUI();
        setStage();
    }


}
