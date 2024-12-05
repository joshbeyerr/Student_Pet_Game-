package com.kaobells.group44;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;


import java.util.HashMap;
import java.util.Map;

/**
 * The {@code CreditScreen} class represents a screen in the game
 * where credits are displayed, including motivation and contributors.
 *
 * <p>This class is responsible for creating and rendering the credit screen,
 * managing its layout and transitioning back to the main screen via a back button.</p>
 *
 * @author group 44
 * @version 1.0
 */
public class CreditScreen  extends ScreenAdapter {
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


    /**
     * Constructs a new {@code CreditScreen} instance.
     *
     * @param game The main game instance, used for shared resources and screen transitions.
     */
    public CreditScreen(Main game) {
        mainGame = game;

        spriteBatch = mainGame.getSharedBatch();
        viewport = mainGame.getViewport();

        backButton = mainGame.getBackButton();
        stage = new Stage(viewport, spriteBatch);
    }

    /**
     * Prepares the screen when it becomes visible, including UI setup
     * and resetting the input processor.
     */
    public void show() {

        createUI();
        super.show();
        setStage(); // Reset input processor
    }

    /**
     * Sets the input processor for the stage to handle user input.
     */
    public void setStage() {
        Gdx.input.setInputProcessor(stage);
    }

    /**
     * Creates the user interface for the credit screen, including
     * text boxes for motivation and contributors.
     */
    public void createUI() {

        stage.addActor(backButton);

        Table table = createTable();

        textures = new HashMap<>();

        textures.put("motivation", new Texture(Gdx.files.internal("creditScreen/motivation-textbox.png")));
        textures.put("contributors", new Texture(Gdx.files.internal("creditScreen/contributors-textbox.png")));

        Image motivationBox = mainGame.createImage(textures.get("motivation"));
        Image contributorBox = mainGame.createImage(textures.get("contributors"));

        addToTable(table, motivationBox);
        addToTable(table, contributorBox);

        stage.addActor(table);
    }


    /**
     * Creates a {@link Table} to manage the layout of the credit screen.
     *
     * @return A configured {@code Table} instance for the credit screen layout.
     */
    private Table createTable() {
        Table newTable = new Table();
        newTable.setFillParent(true);
        newTable.center();
        newTable.padTop(viewport.getWorldHeight() * 0.1f);
        return newTable;
    }

    /**
     * Adds an {@link Actor} to a {@link Table}, setting its size, padding, and alignment.
     *
     * @param table The table to which the actor will be added.
     * @param actor The actor to be added to the table.
     */
    private void addToTable(Table table, Actor actor) {
        float width = viewport.getWorldWidth() * 0.4f;
        float height = viewport.getWorldHeight() * 0.3f;
        float pad = viewport.getWorldHeight() * 0.01f;

        table.add(actor).size(width, height).pad(pad).center();
        table.row();
    }


    /**
     * Renders the credit screen, including the background and all UI elements.
     *
     * @param delta The time in seconds since the last frame.
     */
    @Override
    public void render(float delta) {
        // Clear the screen
        ScreenUtils.clear(0, 0, 0, 1);

        spriteBatch.begin();

        BitmapFont font = mainGame.resourceManager.getTitleFont();

        mainGame.drawBackground(spriteBatch, mainGame.resourceManager.get("mainBackground"), font, "Credits");         // Draw the current background
        spriteBatch.end();

        // Render the stage (actors like buttons, character sprites, etc.)
        stage.act(delta);
        stage.draw();
    }

    /**
     * Resizes the viewport to match the new window dimensions.
     *
     * @param width  The new width of the window.
     * @param height The new height of the window.
     */
    public void resize(int width, int height) {
        viewport.update(width, height, true);

    }

    /**
     * Disposes of resources used by the credit screen, including textures
     * and the stage.
     */
    @Override
    public void dispose() {
        for (Texture texture : textures.values()) {
            texture.dispose();
        }
        stage.dispose();
    }
}

