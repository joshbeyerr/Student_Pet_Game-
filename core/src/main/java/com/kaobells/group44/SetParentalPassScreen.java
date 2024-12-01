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
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
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

    private TextField pinBox1, pinBox2, pinBox3, pinBox4;
    private boolean isPasswordSet;

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

        loadTextures();
        initializeState();
        initializeUI();
    }

    private void loadTextures() {
        textures = new HashMap<>();
        textures.put("passwordBox", new Texture(Gdx.files.internal("parentalControlsScreen/password-pin.png")));
        textures.put("submitButton", new Texture(Gdx.files.internal("parentalControlsScreen/set-pass-btn.png")));
        textures.put("textImageSet", new Texture(Gdx.files.internal("parentalControlsScreen/set-pass-txtbox.png")));
        textures.put("textImageEnter", new Texture(Gdx.files.internal("parentalControlsScreen/submit-pass-txtbox.png")));
    }

    private void initializeState() {
        String password = (String) jsonHandler.getDatabase().parentalControls.get("Password");
        isPasswordSet = password != null && !password.isEmpty();
    }

    private void initializeUI() {
        stage.clear();

        // Add the back button
        stage.addActor(backButton);

        // Title Image
        Image titleImage = new Image(isPasswordSet ? textures.get("textImageEnter") : textures.get("textImageSet"));

        // PIN Boxes
        pinBox1 = createPinBox(isPasswordSet);
        pinBox2 = createPinBox(isPasswordSet);
        pinBox3 = createPinBox(isPasswordSet);
        pinBox4 = createPinBox(isPasswordSet);

        // Navigation between PIN boxes
        setupPinBoxNavigation(pinBox1, null, pinBox2);
        setupPinBoxNavigation(pinBox2, pinBox1, pinBox3);
        setupPinBoxNavigation(pinBox3, pinBox2, pinBox4);
        setupPinBoxNavigation(pinBox4, pinBox3, null);

        // Submit Button
        ImageButton submitButton = mainGame.createImageButton(textures.get("submitButton"));
        submitButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                handleSubmit();
                return true;
            }
        });

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
        buttonTable.add(submitButton)
            .size(viewport.getWorldWidth() * 0.4f, viewport.getWorldHeight() * 0.14f)
            .padTop(viewport.getWorldHeight() * 0.05f);

        parentTable.add(titleTable).row();
        parentTable.add(pinTable).padTop(viewport.getWorldHeight() * 0.05f).row();
        parentTable.add(buttonTable).padTop(viewport.getWorldHeight() * 0.05f);

        stage.addActor(parentTable);
    }

    private void handleSubmit() {
        if (areAllBoxesFilled()) {
            String enteredPassword = pinBox1.getText() + pinBox2.getText() + pinBox3.getText() + pinBox4.getText();
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
            Gdx.app.log("ParentalControls", "All PIN boxes must be filled.");
        }
    }

    private boolean areAllBoxesFilled() {
        return !pinBox1.getText().isEmpty() && !pinBox2.getText().isEmpty() &&
            !pinBox3.getText().isEmpty() && !pinBox4.getText().isEmpty();
    }

    private TextField createPinBox(boolean isPasswordMasked) {
        TextField.TextFieldStyle style = new TextField.TextFieldStyle();
        style.font = font;
        style.fontColor = com.badlogic.gdx.graphics.Color.BLACK;
        style.background = new Image(textures.get("passwordBox")).getDrawable();

        TextField pinBox = new TextField("", style);
        pinBox.setMaxLength(1);
        pinBox.setAlignment(1);
        pinBox.setTextFieldFilter((textField, c) -> Character.isDigit(c));
        pinBox.setFocusTraversal(false); // Ensure manual navigation
        if (isPasswordMasked) {
            pinBox.setPasswordMode(true);
            pinBox.setPasswordCharacter('●'); // Mask input with dots
        }
        return pinBox;
    }

    private void setupPinBoxNavigation(final TextField currentBox, final TextField previousBox, final TextField nextBox) {
        currentBox.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                stage.setKeyboardFocus(currentBox); // Focus the clicked box
                return true;
            }

            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == com.badlogic.gdx.Input.Keys.BACKSPACE) {
                    if (currentBox.getText().isEmpty() && previousBox != null) {
                        stage.setKeyboardFocus(previousBox);
                        previousBox.setText("");
                    } else {
                        currentBox.setText("");
                    }
                    return true;
                }
                return false;
            }

            @Override
            public boolean keyTyped(InputEvent event, char character) {
                if (Character.isDigit(character)) {
                    currentBox.setText(String.valueOf(character));
                    if (nextBox != null) {
                        stage.setKeyboardFocus(nextBox);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        stage.setKeyboardFocus(pinBox1); // Autofocus on the first PIN box
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
        for (Texture texture : textures.values()) {
            texture.dispose();
        }
        stage.dispose();
    }
}
