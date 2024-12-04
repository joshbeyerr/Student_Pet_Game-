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
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@code StartScreen} class represents the starting screen of the game.
 * It provides options for starting a new game, loading an existing game,
 * accessing parental controls, and viewing credits or instructions.
 * This class also manages UI initialization and background music playback.
 *
 * <p>All UI components and assets for the start screen are loaded and
 * managed within this class, ensuring smooth transitions to other screens.</p>
 *
 * @author group 44
 * @version 1.0
 */
public class StartScreen extends ScreenAdapter {

    private final Main mainGame;
    private final SpriteBatch spriteBatch;
    private final Viewport viewport;
    private final Stage stage;

    private AssetManager assetManager;
    private final BitmapFont font;
    private Label errorLabel; // Label for displaying error messages

    /**
     * Constructs a new {@code StartScreen}.
     *
     * @param game The main game instance to manage shared resources and transitions.
     */
    private final float SWAP_INTERVAL = 5f; // Interval in seconds for swapping images
    private final Array<Container<Image>> containers = new Array<>(); // Containers for the heads
    private Map<Integer, Float> headTimers = new HashMap<>(); // Individual timers for each head
    private Map<Integer, Float> headSwapIntervals = new HashMap<>(); // Swap intervals for each head
    private Map<Integer, Boolean> isBlinking = new HashMap<>(); // Tracks whether each head is blinking
    private final float BLINK_DURATION = 0.5f; // How long the blink lasts (in seconds)
    private Map<Integer, Float> blinkDurations = new HashMap<>(); // Tracks blink duration for each hea

    public StartScreen(Main game) {
        mainGame = game;
        spriteBatch = mainGame.getSharedBatch();
        viewport = mainGame.getViewport();
        stage = new Stage(viewport, spriteBatch);

        font = mainGame.resourceManager.getTitleFont();

        if (game.jsonHandler.isEmptyParentalControls()) {
            game.jsonHandler.initializeParentalControls();
        }

        loadTextures();
        initializeUI();
    }



    /**
     * Initializes the UI components for the start screen.
     */
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
        instructionsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("Instructions Button", "instructions Button button clicked!");
                mainGame.pushScreen(new InstructionsScreens(mainGame));
            }
        });

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
//        backgroundMusic.play(); // Play the music

    }

    private void createContainer(Image head){
        Container<Image> newContainer = new Container<>(head);
        containers.add(newContainer);
    }

    /**
     * Creates the heads section of the start screen.
     *
     * @return A {@link Table} containing character heads.
     */
    private Table createHeads() {
        Table newTable = new Table();
//        newTable.setFillParent(true);
//        newTable.center();

        // Create Images for each Head
        createContainer(mainGame.createImage(assetManager.get("characters/relaxed-head.png")));
        createContainer(mainGame.createImage(assetManager.get("characters/quirky-head.png")));
        createContainer(mainGame.createImage(assetManager.get("characters/hasty-head.png")));
        createContainer(mainGame.createImage(assetManager.get("characters/brave-head.png")));
        createContainer(mainGame.createImage(assetManager.get("characters/serious-head.png")));


        for (int i = 0; i < containers.size; i++) {
            headTimers.put(i, i + 0f); // Initial elapsed time
            headSwapIntervals.put(i, SWAP_INTERVAL - i * 0.5f); // Different intervals for each head
            isBlinking.put(i, false); // Start with non-blinking
            blinkDurations.put(i, 0f); // Initialize blink duration
        }

        float padVal = viewport.getWorldWidth() * 0.002f;

        newTable.add(containers.get(0)).padLeft(padVal).padRight(padVal);
        newTable.add(containers.get(1)).padLeft(padVal).padRight(padVal);
        newTable.add(containers.get(2)).padLeft(padVal).padRight(padVal);
        newTable.add(containers.get(3)).padLeft(padVal).padRight(padVal);
        newTable.add(containers.get(4)).padLeft(padVal).padRight(padVal);

        return newTable;
    }


    /**
     * Creates a button for the start screen with specified properties.
     *
     * @param texturePath   The path to the button texture.
     * @param widthRatio    The width ratio for the button.
     * @param heightRatio   The height ratio for the button.
     * @param xOffsetRatio  The X offset ratio for the button position.
     * @param yOffsetRatio  The Y offset ratio for the button position.
     * @return The {@link ImageButton} created with the specified properties.
     */
    private ImageButton startScreenButton(String texturePath, float widthRatio, float heightRatio, float xOffsetRatio, float yOffsetRatio) {
        ImageButton button = mainGame.createImageButton(assetManager.get(texturePath));
        float buttonWidth = viewport.getWorldWidth() * widthRatio;
        float buttonHeight = viewport.getWorldHeight() * heightRatio;
        button.setSize(buttonWidth, buttonHeight);
        button.setPosition(viewport.getWorldWidth() * xOffsetRatio, viewport.getWorldHeight() * yOffsetRatio);
        return button;
    }

    /**
     * Adds an actor to a table and optionally creates a new row.
     *
     * @param table  The {@link Table} to add the actor to.
     * @param actor  The {@link Actor} to add.
     * @param newRow Whether to create a new row after adding the actor.
     */
    private void addActorToTable(Table table, Actor actor, boolean newRow) {
        table.add(actor).size(viewport.getWorldWidth() * 0.3f, viewport.getWorldWidth() * 0.11f).pad(viewport.getWorldWidth() * 0.01f);

        if (newRow) {
            table.row();
        }
    }

    /**
     * Loads textures and assets for the start screen.
     */
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

        assetManager.load("characters/brave-blink.png", Texture.class);
        assetManager.load("characters/hasty-blink.png", Texture.class);
        assetManager.load("characters/quirky-blink.png", Texture.class);
        assetManager.load("characters/relaxed-blink.png", Texture.class);
        assetManager.load("characters/serious-blink.png", Texture.class);

        assetManager.load("music/jb-sample-lowqual.mp3", Music.class);


        // Load assets synchronously
        assetManager.finishLoading();
    }

    /**
     * Renders the start screen.
     *
     * @param delta The time in seconds since the last frame.
     */
    @Override
    public void render(float delta) {
        // Clear the screen
        ScreenUtils.clear(0, 0, 0, 1);

        // Draw the background
        spriteBatch.begin();
        mainGame.drawBackground(spriteBatch, mainGame.resourceManager.get("mainBackground"), font, "Wiktor Simulator");

        spriteBatch.end();

        updateHeadImages(delta);

        // Draw the stage
        stage.act(delta);

        stage.draw();
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

    private void updateHeadImages(float delta) {
        for (int i = 0; i < containers.size; i++) {
            float elapsedTime = headTimers.get(i) + delta;
            float swapInterval = headSwapIntervals.get(i);
            boolean currentlyBlinking = isBlinking.get(i);
            float currentBlinkDuration = blinkDurations.get(i);

            if (currentlyBlinking) {
                // Handle blinking duration
                currentBlinkDuration += delta;
                if (currentBlinkDuration >= BLINK_DURATION) {
                    // Stop blinking after blink duration
                    Container<Image> container = containers.get(i);
                    container.setActor(mainGame.createImage(assetManager.get("characters/" + getCharacterType(i) + "-head.png")));
                    isBlinking.put(i, false);
                    blinkDurations.put(i, 0f); // Reset blink duration
                    headTimers.put(i, 0f); // Reset the timer for the next blink
                } else {
                    // Update blink duration if still blinking
                    blinkDurations.put(i, currentBlinkDuration);
                }
            } else {
                // Handle blinking start based on interval
                if (elapsedTime >= swapInterval) {
                    Container<Image> container = containers.get(i);
                    container.setActor(mainGame.createImage(assetManager.get("characters/" + getCharacterType(i) + "-blink.png")));
                    isBlinking.put(i, true);
                    headTimers.put(i, 0f); // Reset the timer for blinking
                    blinkDurations.put(i, 0f); // Start tracking blink duration
                } else {
                    // Update the timer if not blinking
                    headTimers.put(i, elapsedTime);
                }
            }
        }
    }


    /**
     * Resizes the viewport when the screen size changes.
     *
     * @param width  The new width of the screen.
     * @param height The new height of the screen.
     */
    @Override
    public void resize(int width, int height) {
        if (width != 0 && height != 0) {
            viewport.update(width, height, true);

        }

    }

    /**
     * Disposes of resources used by the start screen.
     */
    public void dispose() {
        if (stage != null) {
            stage.dispose();
        }
        if (assetManager != null) {
            assetManager.dispose();
        }
    }

    /**
     * Sets the input processor to the stage for handling user input.
     */
    public void setStage(){
        // Set the input processor to the stage
        Gdx.input.setInputProcessor(stage);

    }

    /**
     * Displays the start screen and sets the input processor.
     */
    @Override
    public void show() {

        setStage();
    }


}
