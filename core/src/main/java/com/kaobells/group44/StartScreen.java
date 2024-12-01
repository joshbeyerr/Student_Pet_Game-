package com.kaobells.group44;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
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


public class StartScreen extends ScreenAdapter {

    private final Main mainGame;  // Main game class for shared resources transitioning screens
    private final SpriteBatch spriteBatch;
    private final Viewport viewport;
    private final Stage stage;

    private AssetManager assetManager;

    private final BitmapFont font;


    public StartScreen(Main game) {

        mainGame = game;

        spriteBatch = mainGame.getSharedBatch();
        viewport = mainGame.getViewport();

        stage = new Stage(viewport, spriteBatch);

        // load and set font color
        font = mainGame.resourceManager.getTitleFont();

        if(game.jsonHandler.isEmptyParentalControls()){
            game.jsonHandler.initializeParentalControls();
        }

        loadTextures();
        // Create UI components
        initializeUI();

    }



    private void initializeUI() {
        // Create a unified parent table for all elements
        Table parentTable = new Table();
        parentTable.setFillParent(true); // Makes the table fill the screen
        parentTable.center(); // Centers everything

        // Create the heads table and center it
        Table headsTable = createHeads();
        headsTable.center();

        // Create the buttons table and center it
        Table buttonsTable = new Table();
        buttonsTable.center();

        // creating start button
        ImageButton startButton = mainGame.createImageButton(assetManager.get("startScreen/start-game-btn.png"));
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("StartButton", "Start Game button clicked!");
                if (mainGame.jsonHandler.isSavedFiles()){
                    mainGame.pushScreen(new GameSlots(mainGame, "new"));
                }
                else{
                    // if no games exist, using slot 1
                    mainGame.pushScreen(new StoryScreen(mainGame, "1"));
                }


            }
        });

        ImageButton loadButton = mainGame.createImageButton(assetManager.get("startScreen/load-game-btn.png"));
        loadButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("loadButton", "loadButton clicked!");
                mainGame.pushScreen(new GameSlots(mainGame, "load"));
            }
        });

        ImageButton instructionsButton = mainGame.createImageButton(assetManager.get("startScreen/instructions-btn.png"));

        ImageButton creditsButton = mainGame.createImageButton(assetManager.get("startScreen/credits-btn.png"));
        creditsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("Credits Button", "Credits Button button clicked!");
                mainGame.pushScreen(new CreditScreen(mainGame));
            }
        });


        // Add buttons to the buttons table
        addActorToTable(buttonsTable, startButton, false); // Start Game button
        addActorToTable(buttonsTable, loadButton, true); // Load Game button
        addActorToTable(buttonsTable, instructionsButton, false); // Instructions button
        addActorToTable(buttonsTable, creditsButton, false); // Credits button

        // Add heads and buttons to the parent table
        parentTable.add(headsTable).size(viewport.getWorldWidth() * 0.38f, viewport.getWorldHeight() * 0.12f).padTop(viewport.getWorldHeight() * 0.15f).row();
        parentTable.add(buttonsTable).size(viewport.getWorldWidth() * 0.8f, viewport.getWorldHeight() * 0.5f);

        // Add the parent table to the stage
        stage.addActor(parentTable);

        // Add additional independent actors (exit and parental buttons)
        ImageButton exitButton = startScreenButton("globalAssets/exit-btn.png", 0.1f, 0.1f, 0.05f, 0.8f);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Perform an action, for example, print a message or switch screens
                Gdx.app.log("exitButton", "Exiting game...");
                Gdx.app.exit();
            }
        });

        ImageButton parentalButton = startScreenButton("startScreen/parental-controls-btn.png", 0.2f, 0.2f, 0.77f, 0.75f);
        parentalButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("ParentalButton", "Parental Controls button clicked!");
                mainGame.pushScreen(new SetParentalPassScreen(mainGame)); // Transition to ParentalControlsScreen
            }
        });


        stage.addActor(exitButton);
        stage.addActor(parentalButton);


        // MUSIC SECTION
        Music backgroundMusic = assetManager.get("music/jb-sample-lowqual.mp3", Music.class);
        backgroundMusic.setLooping(true); // Loop the music
        backgroundMusic.setVolume(0.5f); // Set volume (range: 0.0 to 1.0)
        backgroundMusic.play(); // Play the music

    }

    private Table createHeads() {
        Table newTable = new Table();
//        newTable.setFillParent(true);
//        newTable.center();

        // Create Images for each Head
        Image braveHead = mainGame.createImage(assetManager.get("characters/brave-head.png"));
        Image hastyHead = mainGame.createImage(assetManager.get("characters/hasty-head.png"));
        Image quirkyHead = mainGame.createImage(assetManager.get("characters/quirky-head.png"));
        Image relaxedHead = mainGame.createImage(assetManager.get("characters/relaxed-head.png"));
        Image seriousHead = mainGame.createImage(assetManager.get("characters/serious-head.png"));

        float padVal = viewport.getWorldWidth() * 0.002f;

        newTable.add(braveHead).padLeft(padVal).padRight(padVal);
        newTable.add(hastyHead).padLeft(padVal).padRight(padVal);
        newTable.add(quirkyHead).padLeft(padVal).padRight(padVal);
        newTable.add(relaxedHead).padLeft(padVal).padRight(padVal);
        newTable.add(seriousHead).padLeft(padVal).padRight(padVal);

        return newTable;
    }

    private ImageButton startScreenButton(String texturePath, float widthRatio, float heightRatio, float xOffsetRatio, float yOffsetRatio) {
        ImageButton button = mainGame.createImageButton(assetManager.get(texturePath));
        float buttonWidth = viewport.getWorldWidth() * widthRatio;
        float buttonHeight = viewport.getWorldHeight() * heightRatio;
        button.setSize(buttonWidth, buttonHeight);
        button.setPosition(viewport.getWorldWidth() * xOffsetRatio, viewport.getWorldHeight() * yOffsetRatio);
        return button;
    }

    private void addActorToTable(Table table, Actor actor, boolean newRow) {
        table.add(actor).size(viewport.getWorldWidth() * 0.3f, viewport.getWorldWidth() * 0.11f).pad(viewport.getWorldWidth() * 0.01f);

        if (newRow) {
            table.row();
        }
    }

    private void loadTextures() {
        assetManager = new AssetManager();

        // Queue textures for loading
        assetManager.load("startScreen/start-game-btn.png", Texture.class);
        assetManager.load("startScreen/load-game-btn.png", Texture.class);
        assetManager.load("startScreen/credits-btn.png", Texture.class);
        assetManager.load("startScreen/instructions-btn.png", Texture.class);

        assetManager.load("startScreen/parental-controls-btn.png", Texture.class);
        assetManager.load("globalAssets/exit-btn.png", Texture.class);

        assetManager.load("characters/brave-head.png", Texture.class);
        assetManager.load("characters/hasty-head.png", Texture.class);
        assetManager.load("characters/quirky-head.png", Texture.class);
        assetManager.load("characters/relaxed-head.png", Texture.class);
        assetManager.load("characters/serious-head.png", Texture.class);

        assetManager.load("music/jb-sample-lowqual.mp3", Music.class);


        // Load assets synchronously
        assetManager.finishLoading();
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

        }

    }

    public void dispose() {
        if (stage != null) {
            stage.dispose();
        }
        if (assetManager != null) {
            assetManager.dispose();
        }
    }

    public void setStage(){
        // Set the input processor to the stage
        Gdx.input.setInputProcessor(stage);

    }

    @Override
    public void show() {
        setStage();
    }


}
