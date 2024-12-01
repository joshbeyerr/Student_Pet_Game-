package com.kaobells.group44;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import java.util.HashMap;
import java.util.Map;

public class ComputerScreen extends ScreenAdapter {
    private final Main mainGame;
    private final GameSession session;

    private final SpriteBatch spriteBatch;
    private final Stage stage;

    private Map<String, Texture> textures;
    private final Map<String, ImageButton> buttons;

    public ComputerScreen(Main mainGame, GameSession session) {
        this.mainGame = mainGame;
        this.session = session;

        spriteBatch = mainGame.getSharedBatch();
        stage = new Stage(mainGame.getViewport(), spriteBatch);

        buttons = new HashMap<>();
        loadTextures();
        createUI();
    }

    private void loadTextures() {
        textures = new HashMap<>();

        textures.put("computerScreenBg", new Texture(Gdx.files.internal("computerScreen/computerScreen-bg.png")));
        textures.put("minigamePrompt", new Texture(Gdx.files.internal("computerScreen/minigame-prompt-txt.png")));
        textures.put("bugdodgeBtn", new Texture(Gdx.files.internal("computerScreen/bugdodge-btn.png")));
        textures.put("jbordleBtn", new Texture(Gdx.files.internal("computerScreen/jbordle-btn.png")));
        textures.put("backBtn", new Texture(Gdx.files.internal("computerScreen/minigame-back-btn.png")));
    }

    private void createUI() {
        // Set the background
        Image background = new Image(new TextureRegionDrawable(textures.get("computerScreenBg")));
        background.setSize(stage.getWidth(), stage.getHeight());
        background.setPosition(0, 0);

        // Add background to the stage
        stage.addActor(background);

        // Add the prompt text image
        Image prompt = new Image(new TextureRegionDrawable(textures.get("minigamePrompt")));
        prompt.setSize(stage.getWidth() * 0.8f, stage.getHeight() * 0.1f); // Adjust size as needed
        prompt.setPosition(stage.getWidth() * 0.1f, stage.getHeight() * 0.8f); // Position near the top
        stage.addActor(prompt);

        // Create buttons
        createButton("bugdodgeBtn", "Bug Dodge", stage.getWidth() * 0.25f, stage.getHeight() * 0.5f, () -> {
            // Start Bug Dodge minigame
            Gdx.app.log("ComputerScreen", "Bug Dodge selected.");
            // Push BugDodgeScreen or similar
        });

        createButton("jbordleBtn", "Jbordle", stage.getWidth() * 0.55f, stage.getHeight() * 0.5f, () -> {
            // Start Jbordle minigame
            Gdx.app.log("ComputerScreen", "Jbordle selected.");
            // Push JbordleScreen or similar
        });

        createButton("backBtn", "Back", stage.getWidth() * 0.4f, stage.getHeight() * 0.2f, () -> {
            // Go back to the previous screen
            Gdx.app.log("ComputerScreen", "Returning to previous screen.");
            mainGame.popScreen();
        });
    }

    private void createButton(String textureKey, String logMessage, float x, float y, Runnable onClick) {
        ImageButton button = mainGame.createImageButton(textures.get(textureKey));
        button.setPosition(x, y, Align.center);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float screenX, float screenY) {
                Gdx.app.log("ComputerScreen", logMessage);
                onClick.run();
            }
        });
        stage.addActor(button);
        buttons.put(textureKey, button);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        for (Texture texture : textures.values()) {
            texture.dispose();
        }
        stage.dispose();
    }
}
