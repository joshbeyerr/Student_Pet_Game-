package com.kaobells.group44;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.HashMap;
import java.util.Map;

public class ParentalControlsScreen extends ScreenAdapter {

    private final Main mainGame;
    private final SpriteBatch spriteBatch;
    private final Stage stage;
    private final Viewport viewport;
    private final ImageButton backButton;

    private Map<String, Texture> textures;
    private final Table parentTable;
    private final BitmapFont font;

    public ParentalControlsScreen(Main game) {
        this.mainGame = game;
        this.spriteBatch = mainGame.getSharedBatch();
        this.viewport = mainGame.getViewport();
        this.stage = new Stage(viewport, spriteBatch);
        this.backButton = mainGame.getBackButton();

        this.parentTable = new Table();
        this.parentTable.setFillParent(true);
        this.parentTable.center();

        this.font = mainGame.resourceManager.getTitleFont();

        loadTextures();
        initializeUI();
    }

    private void loadTextures() {
        textures = new HashMap<>();
        textures.put("playtimeStatsButton", new Texture(Gdx.files.internal("parentalControlsScreen/parent-stats-btn.png")));
        textures.put("playtimeControlsButton", new Texture(Gdx.files.internal("parentalControlsScreen/parent-controls-btn.png")));
        textures.put("revivePetButton", new Texture(Gdx.files.internal("parentalControlsScreen/parent-revive-btn.png")));
        textures.put("backButton", new Texture(Gdx.files.internal("globalAssets/backButton.png")));
    }

    private void initializeUI() {
        stage.clear();

        stage.addActor(backButton);

        Table topTable = new Table();
        topTable.top().left();
        topTable.setFillParent(true);

        stage.addActor(topTable);

        // Create Buttons for Options
        ImageButton playtimeStatsButton = createButton("playtimeStatsButton");
        playtimeStatsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("ParentalControls", "Playtime Stats clicked");
                // Logic for playtime stats
                mainGame.pushScreen(new ParentalPlaytimeStatsScreen(mainGame));
            }
        });

        ImageButton playtimeControlsButton = createButton("playtimeControlsButton");
        playtimeControlsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("ParentalControls", "Playtime Controls clicked");
                // Logic for playtime controls

            }
        });

        ImageButton revivePetButton = createButton("revivePetButton");
        revivePetButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("ParentalControls", "Revive Pet clicked");
                // Logic for revive pet
            }
        });

        // Arrange Buttons Horizontally
        Table buttonTable = new Table();
        buttonTable.center();

        float padding = viewport.getWorldWidth() * 0.01f;

        float buttonWidth = viewport.getWorldWidth() * 0.20f;
        float buttonHeight = viewport.getWorldWidth() * 0.4f;


        buttonTable.add(playtimeStatsButton).size(buttonWidth, buttonHeight).pad(padding);
        buttonTable.add(playtimeControlsButton).size(buttonWidth, buttonHeight).pad(padding);
        buttonTable.add(revivePetButton).size(buttonWidth, buttonHeight).pad(padding);

        parentTable.add(buttonTable).center().padTop(viewport.getWorldHeight() * 0.035f); // Adjust vertical spacing
        stage.addActor(parentTable);
    }

    private ImageButton createButton(String textureKey) {
        Texture texture = textures.get(textureKey);
        return mainGame.createImageButton(texture);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1); // Clear with a consistent black background
        spriteBatch.begin();
        mainGame.drawBackground(spriteBatch, mainGame.resourceManager.get("mainBackground"), font, "Parental Controls");
        spriteBatch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        // idk rn, this just works
        stage.addActor(backButton);
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
