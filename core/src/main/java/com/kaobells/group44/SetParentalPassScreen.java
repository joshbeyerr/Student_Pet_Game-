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

public class SetParentalPassScreen extends ScreenAdapter {

    private final Main mainGame;
    private final SpriteBatch spriteBatch;
    private final Stage stage;
    private final Viewport viewport;
    private final ImageButton backButton;
    private final JsonHandler jsonHandler;

    private Map<String, Texture> textures;
    private final Table parentTable;
    private final BitmapFont font;

    private Image pinBox1, pinBox2, pinBox3, pinBox4;
    private boolean isPasswordSet;
    private int currentIndex;
    private final Texture hiddenImageTexture;
    private final Texture defaultImageTexture;
    private String enteredPassword;

    public SetParentalPassScreen(Main game) {
        this.mainGame = game;
        this.spriteBatch = mainGame.getSharedBatch();
        this.viewport = mainGame.getViewport();
        this.stage = new Stage(viewport, spriteBatch);
        this.backButton = mainGame.getBackButton();
        this.jsonHandler = new JsonHandler();

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

    private void loadTextures() {
        textures = new HashMap<>();
        textures.put("submitButton", new Texture(Gdx.files.internal("parentalControlsScreen/set-pass-btn.png")));
        textures.put("loginPassButton", new Texture(Gdx.files.internal("parentalControlsScreen/submit-pass-btn.png")));
        textures.put("textImageSet", new Texture(Gdx.files.internal("parentalControlsScreen/set-pass-txtbox.png")));
        textures.put("textImageEnter", new Texture(Gdx.files.internal("parentalControlsScreen/submit-pass-txtbox.png")));

    }

    private void initializeState() {
        String password = (String) jsonHandler.getDatabase().parentalControls.get("Password");
        isPasswordSet = password != null && !password.isEmpty();
        currentIndex = 0; // Start at the first pin box
        enteredPassword = ""; // Initialize empty password
    }

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

    private Image createImageBox() {
        // Create an image box with the default texture
        return new Image(defaultImageTexture);
    }

    private void handleSubmit() {
        // Handle the submission logic
        if (enteredPassword.length() == 4) { // Ensure all 4 digits are entered
            if (isPasswordSet) {
                String storedPassword = (String) jsonHandler.getDatabase().parentalControls.get("Password");
                if (enteredPassword.equals(storedPassword)) {
                    Gdx.app.log("ParentalControls", "Password Correct!");
                } else {
                    Gdx.app.log("ParentalControls", "Incorrect Password!");
                }
            } else {
                jsonHandler.getDatabase().parentalControls.put("Password", enteredPassword);
                jsonHandler.saveDatabase();
                Gdx.app.log("ParentalControls", "Password Set: " + enteredPassword);
            }
        } else {
            Gdx.app.log("ParentalControls", "Incomplete PIN. Please fill all boxes.");
        }
    }

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

    private void changeImageToHidden(int index) {
        // Change the image of the pin box at the specified index to the hidden texture
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

    private void changeImageToDefault(int index) {
        // Change the image of the pin box at the specified index to the default texture
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

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        spriteBatch.begin();
        mainGame.drawBackground(spriteBatch, mainGame.resourceManager.get("mainBackground"), font, "Parental Controls");
        spriteBatch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

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
