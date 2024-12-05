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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@code ParentalPlaytimeStatsScreen} class represents the screen
 * for displaying and managing parental control statistics, such as
 * average and total playtime.
 *
 * <p>This screen includes functionality for resetting playtime statistics
 * and visualizes them in a structured layout.</p>
 *
 * @author group 44
 * @version 1.0
 */
public class ParentalPlaytimeStatsScreen extends ScreenAdapter {

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

    /** Font used for rendering text in the UI. */
    private final BitmapFont font;

    /** Map containing textures for texture management. */
    private final Map<String, Texture> textures;


    /**
     * Constructs a new {@code ParentalPlaytimeStatsScreen}.
     *
     * @param game The main game instance managing resources and transitions.
     */
    public ParentalPlaytimeStatsScreen(Main game) {
        this.mainGame = game;
        this.spriteBatch = mainGame.getSharedBatch();
        this.viewport = mainGame.getViewport();
        this.stage = new Stage(viewport, spriteBatch);
        this.backButton = mainGame.getBackButton();
        this.font = mainGame.resourceManager.getTitleFont();
        this.textures = new HashMap<>(); // Initialize textures map
    }

    /**
     * Prepares the screen for display by setting the input processor,
     * loading textures, and initializing the UI.
     */
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        loadTextures(); // Load all textures
        initializeUI();
    }

    /**
     * Initializes the UI components of the screen, including labels,
     * buttons, and layout tables.
     */
    private void initializeUI() {
        stage.clear();

        // Back Button
        stage.addActor(backButton);

        // Main Content Table
        Table mainTable = new Table();
        mainTable.center();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        // Heading Labels
        Image avgPlaytimeLabelImage = new Image(textures.get("avgPlaytimeTextBox"));
        Image totalPlaytimeLabelImage = new Image(textures.get("totalPlaytimeTextBox"));

        // Playtime Values
        Label avgPlaytimeValue = new Label(formatTime(getAveragePlaytime()), new Label.LabelStyle(font, com.badlogic.gdx.graphics.Color.BLACK));
        avgPlaytimeValue.setAlignment(Align.center);

        Label totalPlaytimeValue = new Label(formatTime(getTotalPlaytime()), new Label.LabelStyle(font, com.badlogic.gdx.graphics.Color.BLACK));
        totalPlaytimeValue.setAlignment(Align.center);

        // Reset Playtime Button
        ImageButton resetButton = mainGame.createImageButton(textures.get("resetButton"));
        resetButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                resetPlaytimeStats();
                avgPlaytimeValue.setText(formatTime(getAveragePlaytime()));
                totalPlaytimeValue.setText(formatTime(getTotalPlaytime()));
            }
        });


        // Add Components to Main Table
        float labelWidth = viewport.getWorldWidth() * 0.4f;
        float labelHeight = viewport.getWorldHeight() * 0.1f;
        float valueHeight = viewport.getWorldHeight() * 0.08f;

        mainTable.add(avgPlaytimeLabelImage).size(labelWidth, labelHeight).padBottom(10).padRight(10);
        mainTable.add(totalPlaytimeLabelImage).size(labelWidth, labelHeight).padBottom(10);
        mainTable.row();

        mainTable.add(avgPlaytimeValue).size(labelWidth, valueHeight).padBottom(20).padRight(10);
        mainTable.add(totalPlaytimeValue).size(labelWidth, valueHeight).padBottom(20);
        mainTable.row();

        mainTable.add(resetButton).colspan(2).size(viewport.getWorldWidth() * 0.6f, viewport.getWorldHeight() * 0.1f).padTop(20);
    }

    /**
     * Loads the textures required for the screen's UI components.
     */
    private void loadTextures() {
        textures.put("avgPlaytimeTextBox", new Texture(Gdx.files.internal("parentalControlsScreen/avg-playtime-txtbox.png")));
        textures.put("totalPlaytimeTextBox", new Texture(Gdx.files.internal("parentalControlsScreen/total-playtime-txtbox.png")));
        textures.put("resetButton", new Texture(Gdx.files.internal("parentalControlsScreen/reset-playtime-txtbox.png")));
    }

    /**
     * Retrieves the average playtime per session.
     *
     * @return The average playtime in seconds.
     */
    private int getAveragePlaytime() {
        return mainGame.jsonHandler.getParentalControlInt("averagePlaytimePerSession");
    }

    /**
     * Retrieves the total playtime of all sessions.
     *
     * @return The total playtime in seconds.
     */
    private int getTotalPlaytime() {
        return mainGame.jsonHandler.getParentalControlInt("totalSecondsPlayed");
    }

    /**
     * Resets the playtime statistics to zero.
     */
    private void resetPlaytimeStats() {
        mainGame.jsonHandler.setParentalControlInt("averagePlaytimePerSession", 0);
        mainGame.jsonHandler.setParentalControlInt("totalSecondsPlayed", 0);
        mainGame.jsonHandler.setParentalControlInt("totalSessionsPlayed", 0);
    }

    /**
     * Formats a time value from seconds into HH:mm:ss format.
     *
     * @param seconds The time value in seconds.
     * @return A formatted string representing the time in HH:mm:ss format.
     */
    private String formatTime(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }

    /**
     * Renders the screen, including the background and UI components.
     *
     * @param delta The time in seconds since the last frame.
     */
    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1); // Clear the screen with a black background
        spriteBatch.begin();
        mainGame.drawBackground(spriteBatch, mainGame.resourceManager.get("mainBackground"), font, "Playtime Stats");
        spriteBatch.end();

        stage.act(delta);
        stage.draw();
    }

    /**
     * Adjusts the viewport to the new screen dimensions.
     *
     * @param width  The new width of the screen.
     * @param height The new height of the screen.
     */
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    /**
     * Disposes of resources used by the screen, including textures and the stage.
     */
    @Override
    public void dispose() {
        // Dispose of all textures
        for (Texture texture : textures.values()) {
            texture.dispose();
        }
        stage.dispose();
    }
}
