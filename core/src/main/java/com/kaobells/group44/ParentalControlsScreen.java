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

/**
 * The screen for accessing and managing parental controls in the game.
 * Provides functionality to view and modify settings like playtime limits.
 *
 * @author group 44
 * @version 1.0
 * @see ParentalPlaytimeLimits
 * @see ParentalPlaytimeStatsScreen
 */
public class ParentalControlsScreen extends ScreenAdapter {

    /** Reference to the main game instance. */
    private final Main mainGame;

    /** Sprite batch used for rendering. */
    private final SpriteBatch spriteBatch;

    /** Stage for managing and rendering UI components. */
    private final Stage stage;

    /** Viewport for handling screen size and scaling. */
    private final Viewport viewport;

    /** Button for navigating back in the UI. */
    private final ImageButton backButton;

    /** Map containing textures used in the scene. */
    private Map<String, Texture> textures;

    /** Parent table for organizing UI components. */
    private final Table parentTable;

    /** Font used for rendering text in the UI. */
    private final BitmapFont font;


    /**
     * Initializes the parental controls screen with required resources.
     *
     * @param game The main game instance.
     */
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

    /**
     * Loads textures required for rendering UI elements.
     */
    private void loadTextures() {
        textures = new HashMap<>();
        textures.put("playtimeStatsButton", new Texture(Gdx.files.internal("parentalControlsScreen/parent-stats-btn.png")));
        textures.put("playtimeControlsButton", new Texture(Gdx.files.internal("parentalControlsScreen/parent-controls-btn.png")));
        textures.put("revivePetButton", new Texture(Gdx.files.internal("parentalControlsScreen/parent-revive-btn.png")));
        textures.put("backButton", new Texture(Gdx.files.internal("globalAssets/backButton.png")));
    }

    /**
     * Initializes the UI components for the parental controls screen,
     * including buttons for managing playtime settings and stats.
     */
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
                mainGame.pushScreen(new ParentalPlaytimeLimits(mainGame));

            }
        });

        ImageButton revivePetButton = createButton("revivePetButton");
        revivePetButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("ParentalControls", "Revive Pet clicked");
                mainGame.pushScreen(new GameSlots(mainGame, "revive"));
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

    /**
     * Renders the UI components and background for the parental controls screen.
     *
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1); // Clear with a consistent black background
        spriteBatch.begin();
        mainGame.drawBackground(spriteBatch, mainGame.resourceManager.get("mainBackground"), font, "Parental Controls");
        spriteBatch.end();

        stage.act(delta);
        stage.draw();
    }

    /**
     * Sets the input processor to the stage, loads textures, and initializes the UI
     * components. This method is called when the screen becomes visible.
     */
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        // idk rn, this just works
        stage.addActor(backButton);
    }

    /**
     * Adjusts the screen's viewport to handle window resizing.
     *
     * @param width  The new width of the screen.
     * @param height The new height of the screen.
     */
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    /**
     * Disposes of the textures and stage to free up resources.
     */
    @Override
    public void dispose() {
        for (Texture texture : textures.values()) {
            texture.dispose();
        }
        stage.dispose();
    }
}
