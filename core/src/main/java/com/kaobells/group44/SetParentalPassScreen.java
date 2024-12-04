package com.kaobells.group44;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@code SetParentalPassScreen} class is responsible for handling the
 * parental password setup and login functionality in the game. This screen
 * allows users to either set a new parental password or validate an existing one.
 *
 * <p>The UI includes pin boxes for entering a 4-digit password, a title indicating
 * the current operation (set or login), and a submit button to confirm the action.</p>
 *
 * @author group 44
 * @version 1.0
 */
public class SetParentalPassScreen extends ScreenAdapter {

    private final Main mainGame;
    private final SpriteBatch spriteBatch;
    private final Stage stage;
    private final Viewport viewport;
    private final ImageButton backButton;

    private Map<String, Texture> textures;
    private final Table parentTable;
    private final BitmapFont font;

    private Image pinBox1, pinBox2, pinBox3, pinBox4;
    private boolean isPasswordSet;
    private int currentIndex;
    private final Texture hiddenImageTexture;
    private final Texture defaultImageTexture;
    private String enteredPassword;

    /**
     * Constructs a new {@code SetParentalPassScreen}.
     *
     * @param game The main game instance for accessing shared resources.
     */
    public SetParentalPassScreen(Main game) {
        this.mainGame = game;
        this.spriteBatch = mainGame.getSharedBatch();
        this.viewport = mainGame.getViewport();
        this.stage = new Stage(viewport, spriteBatch);
        this.backButton = mainGame.getBackButton();

        this.parentTable = new Table();
        this.parentTable.setFillParent(true);
        this.parentTable.center();

        this.font = mainGame.resourceManager.getTitleFont();

        // Load textures for hidden and default pin images
        this.hiddenImageTexture = new Texture(Gdx.files.internal("parentalControlsScreen/hidden-pass-pin.png"));
        this.defaultImageTexture = new Texture(Gdx.files.internal("parentalControlsScreen/password-pin.png"));

        loadTextures();
        initializeState();
        initializeUI();
    }

    /**
     * Loads textures for UI elements such as buttons and text images.
     */
    private void loadTextures() {
        textures = new HashMap<>();
        textures.put("submitButton", new Texture(Gdx.files.internal("parentalControlsScreen/set-pass-btn.png")));
        textures.put("loginPassButton", new Texture(Gdx.files.internal("parentalControlsScreen/submit-pass-btn.png")));
        textures.put("textImageSet", new Texture(Gdx.files.internal("parentalControlsScreen/set-pass-txtbox.png")));
        textures.put("textImageEnter", new Texture(Gdx.files.internal("parentalControlsScreen/submit-pass-txtbox.png")));
    }

    /**
     * Initializes the password state and resets the entered password.
     */
    private void initializeState() {
        String password = mainGame.jsonHandler.getParentalPassword();
        isPasswordSet = password != null && !password.isEmpty();
        currentIndex = 0; // Start at the first pin box
        enteredPassword = ""; // Initialize empty password
    }

    /**
     * Sets up the UI components for the screen, including pin boxes, title, and submit button.
     */
    private void initializeUI() {
        stage.clear();

        // Add the back button to the stage
        stage.addActor(backButton);

        // Title image based on whether a password is set or not
        Image titleImage = new Image(isPasswordSet ? textures.get("textImageEnter") : textures.get("textImageSet"));

        // Initialize the pin boxes
        pinBox1 = createImageBox();
        pinBox2 = createImageBox();
        pinBox3 = createImageBox();
        pinBox4 = createImageBox();

        // Submit button with a listener
        ImageButton submissionButton = mainGame.createImageButton(isPasswordSet ? textures.get("loginPassButton") : textures.get("submitButton"));
        submissionButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                handleSubmit();
                return true;
            }
        });

        // Layout the components using tables
        Table titleTable = new Table();
        float titleWidth = viewport.getWorldWidth() * 0.7f;
        float titleHeight = viewport.getWorldHeight() * 0.18f;
        titleTable.add(titleImage).size(titleWidth, titleHeight).padTop(viewport.getWorldHeight() * 0.1f).row();

        Table pinTable = new Table();
        float pinBoxSize = viewport.getWorldWidth() * 0.1f;
        float pinSpacing = viewport.getWorldWidth() * 0.04f;
        pinTable.add(pinBox1).size(pinBoxSize).padRight(pinSpacing);
        pinTable.add(pinBox2).size(pinBoxSize).padRight(pinSpacing);
        pinTable.add(pinBox3).size(pinBoxSize).padRight(pinSpacing);
        pinTable.add(pinBox4).size(pinBoxSize);

        Table buttonTable = new Table();
        buttonTable.add(submissionButton)
                .size(viewport.getWorldWidth() * 0.4f, viewport.getWorldHeight() * 0.14f)
                .padTop(viewport.getWorldHeight() * 0.05f);

        // Add the tables to the parent table and stage
        parentTable.add(titleTable).row();
        parentTable.add(pinTable).padTop(viewport.getWorldHeight() * 0.05f).row();
        parentTable.add(buttonTable).padTop(viewport.getWorldHeight() * 0.05f);
        stage.addActor(parentTable);

        // Set the input listener for keyboard interaction
        setInputListener();
    }

    /**
     * Creates an image box for the pin input.
     *
     * @return An {@link Image} initialized with the default pin texture.
     */
    private Image createImageBox() {
        // Create an image box with the default texture
        return new Image(defaultImageTexture);
    }

    /**
     * Handles submission of the entered password, either validating it
     * or saving it as a new password.
     */
    private void handleSubmit() {
        if (enteredPassword.length() == 4) { // Ensure all 4 digits are entered
            if (isPasswordSet) {
                String storedPassword = mainGame.jsonHandler.getParentalPassword();

                if (enteredPassword.equals(storedPassword)) {
                    Gdx.app.log("ParentalControls", "Password Correct!");
                    mainGame.popScreen();
                    mainGame.pushScreen(new ParentalControlsScreen(mainGame));
                } else {
                    Gdx.app.log("ParentalControls", "Incorrect Password!");
                }
            } else {

                mainGame.jsonHandler.setParentalPassword(enteredPassword);

                Gdx.app.log("ParentalControls", "Password Set: " + enteredPassword);
                mainGame.popScreen();
                mainGame.pushScreen(new SetParentalPassScreen(mainGame));
            }
        } else {
            Gdx.app.log("ParentalControls", "Incomplete PIN. Please fill all boxes.");
        }
    }

    /**
     * Sets up the input listener to handle key events for password entry.
     */
    private void setInputListener() {
        Gdx.input.setInputProcessor(stage);
        stage.addListener(new InputListener() {
            @Override
            public boolean keyTyped(InputEvent event, char character) {
                if (Character.isDigit(character) && currentIndex < 4) {
                    enteredPassword += character; // Append digit to password
                    changeImageToHidden(currentIndex);
                    currentIndex++;
                } else if (character == '\b' && currentIndex > 0) { // Backspace
                    currentIndex--;
                    enteredPassword = enteredPassword.substring(0, enteredPassword.length() - 1); // Remove last character
                    changeImageToDefault(currentIndex);
                }
                return true;
            }
        });
    }

    /**
     * Changes the pin box at the given index to a hidden image.
     *
     * @param index The index of the pin box to update.
     */
    private void changeImageToHidden(int index) {
        switch (index) {
            case 0:
                pinBox1.setDrawable(new Image(hiddenImageTexture).getDrawable());
                break;
            case 1:
                pinBox2.setDrawable(new Image(hiddenImageTexture).getDrawable());
                break;
            case 2:
                pinBox3.setDrawable(new Image(hiddenImageTexture).getDrawable());
                break;
            case 3:
                pinBox4.setDrawable(new Image(hiddenImageTexture).getDrawable());
                break;
        }
    }

    /**
     * Changes the pin box at the given index to the default image.
     *
     * @param index The index of the pin box to update.
     */
    private void changeImageToDefault(int index) {
        switch (index) {
            case 0:
                pinBox1.setDrawable(new Image(defaultImageTexture).getDrawable());
                break;
            case 1:
                pinBox2.setDrawable(new Image(defaultImageTexture).getDrawable());
                break;
            case 2:
                pinBox3.setDrawable(new Image(defaultImageTexture).getDrawable());
                break;
            case 3:
                pinBox4.setDrawable(new Image(defaultImageTexture).getDrawable());
                break;
        }
    }


    /**
     * Prepares the stage and sets the input processor to handle user interactions.
     */
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        stage.addActor(backButton);
    }

    /**
     * Renders the screen by clearing the background, drawing the main game background,
     * and rendering the stage elements such as the back button and pin input UI.
     *
     * @param delta Time elapsed since the last frame, in seconds.
     */
    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        spriteBatch.begin();
        mainGame.drawBackground(spriteBatch, mainGame.resourceManager.get("mainBackground"), font, "Parental Controls");
        spriteBatch.end();

        stage.act(delta);
        stage.draw();
    }

    /**
     * Updates the viewport dimensions when the window is resized.
     *
     * @param width  The new width of the window.
     * @param height The new height of the window.
     */
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    /**
     * Releases all resources allocated by this screen, including textures and stage objects.
     */
    @Override
    public void dispose() {
        hiddenImageTexture.dispose();
        defaultImageTexture.dispose();
        for (Texture texture : textures.values()) {
            texture.dispose();
        }
        stage.dispose();
    }
}
