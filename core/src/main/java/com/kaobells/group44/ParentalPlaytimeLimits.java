package com.kaobells.group44;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@code ParentalPlaytimeLimits} class represents a screen where users
 * can configure parental controls for playtime limits. It provides toggle
 * buttons to set restrictions for specific time periods and days of the week.
 *
 * <p>This screen uses a {@link Table} layout to organize UI components, such as
 * labels and toggle buttons, and interacts with the {@link JsonHandler} to persist
 * user configurations.</p>
 *
 * @author group 44
 * @version 1.0
 */
public class ParentalPlaytimeLimits extends ScreenAdapter {
    private final Main mainGame;
    private final Stage stage;
    private final Viewport viewport;
    private final Map<String, Texture> textures;
    private final SpriteBatch spriteBatch;
    private final BitmapFont font;

    /**
     * Constructs a new {@code ParentalPlaytimeLimits} screen.
     *
     * @param mainGame The main game instance to manage shared resources and transitions.
     */
    public ParentalPlaytimeLimits(Main mainGame) {
        this.mainGame = mainGame;
        this.stage = new Stage(mainGame.getViewport(), mainGame.getSharedBatch());
        this.viewport = mainGame.getViewport();
        this.spriteBatch = mainGame.getSharedBatch();
        this.font = mainGame.resourceManager.getTitleFont();
        this.textures = new HashMap<>();
        loadTextures();
        setupUI();
    }

    /**
     * Loads the required textures for the UI components.
     */
    private void loadTextures() {
        textures.put("allowedHoursTitle", new Texture(Gdx.files.internal("parentalControlsScreen/allowed-hrs-txtbox.png")));
        textures.put("checked", new Texture(Gdx.files.internal("parentalControlsScreen/checked-parent-btn.png")));
        textures.put("unchecked", new Texture(Gdx.files.internal("parentalControlsScreen/unchecked-parent-btn.png")));
    }

    /**
     * Sets up the user interface components, including title, labels, and toggle buttons.
     */
    private void setupUI() {
        stage.clear();
        stage.addActor(mainGame.getBackButton()); // Add back button

        // Root table to hold all UI components
        Table rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.top().padTop(viewport.getWorldHeight() * 0.25f); // Adjusted top padding to lower the content

        // Add title image with adjusted size
        Image titleImage = new Image(textures.get("allowedHoursTitle"));
        float titleWidth = viewport.getWorldWidth() * 0.6f; // Slightly smaller title width
        float titleHeight = viewport.getWorldHeight() * 0.12f; // Slightly smaller title height
        rootTable.add(titleImage).size(titleWidth, titleHeight).padBottom(viewport.getWorldHeight() * 0.02f).row();

        // Add playtime rows
        float labelWidth = viewport.getWorldWidth() * 0.5f;
        float buttonSize = viewport.getWorldWidth() * 0.03f; // Smaller buttons
        float rowPadding = viewport.getWorldHeight() * 0.01f; // Adjusted row padding

        addPlaytimeRow(rootTable, "Mornings (7:00am - 11:59am)", "morningParentBlock", labelWidth, buttonSize, rowPadding);
        addPlaytimeRow(rootTable, "Daytime (12:00pm - 7:59pm)", "afternoonParentBlock", labelWidth, buttonSize, rowPadding);
        addPlaytimeRow(rootTable, "Nights (After 8:00pm)", "eveningParentBlock", labelWidth, buttonSize, rowPadding);
        addPlaytimeRow(rootTable, "Weekdays (Monday - Thursday)", "weekdayParentBlock", labelWidth, buttonSize, rowPadding);
        addPlaytimeRow(rootTable, "Weekends (Friday - Sunday)", "weekendParentBlock", labelWidth, buttonSize, rowPadding);

        stage.addActor(rootTable);
    }

    /**
     * Adds a row for a specific playtime restriction in the table.
     *
     * @param table      The table to add the row to.
     * @param labelText  The label text for the row.
     * @param jsonKey    The JSON key associated with this restriction.
     * @param labelWidth The width of the label.
     * @param buttonSize The size of the toggle button.
     * @param rowPadding The padding between rows.
     */
    private void addPlaytimeRow(Table table, String labelText, String jsonKey, float labelWidth, float buttonSize, float rowPadding) {
        // Label configuration
        Label label = new Label(labelText, new Label.LabelStyle(mainGame.resourceManager.getFont(true), com.badlogic.gdx.graphics.Color.BLACK));
        label.setFontScale(1.8f); // Make the text bigger
        label.setAlignment(Align.left);

        // Create toggle button
        boolean isAllowed = mainGame.jsonHandler.getParentalControlBoolean(jsonKey);
        ImageButton toggleButton = createToggleButton(isAllowed, jsonKey);

        // Add row to the table
        table.add(label).width(labelWidth).align(Align.left).padRight(viewport.getWorldWidth() * 0.08f).padBottom(rowPadding);
        table.add(toggleButton).size(buttonSize).align(Align.center).padBottom(rowPadding).row();
    }

    /**
     * Creates a toggle button for enabling or disabling a specific playtime restriction.
     *
     * @param isAllowed Initial state of the restriction (enabled or disabled).
     * @param jsonKey   The JSON key associated with this restriction.
     * @return An {@link ImageButton} configured for toggling the playtime restriction.
     */
    private ImageButton createToggleButton(boolean isAllowed, String jsonKey) {
        TextureRegionDrawable initialDrawable = new TextureRegionDrawable(new TextureRegion(isAllowed ? textures.get("unchecked") : textures.get("checked")));
        ImageButton button = new ImageButton(initialDrawable);

        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean currentState = mainGame.jsonHandler.getParentalControlBoolean(jsonKey);
                boolean newState = !currentState; // Toggle the state
                mainGame.jsonHandler.getDatabase().parentalControls.put(jsonKey, newState);
                mainGame.jsonHandler.saveDatabase();

                // Update button texture
                TextureRegionDrawable newDrawable = new TextureRegionDrawable(new TextureRegion(newState ? textures.get("unchecked") : textures.get("checked")));
                button.getStyle().imageUp = newDrawable;
            }
        });

        return button;
    }

    /**
     * Sets the input processor for this screen.
     */
    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    /**
     * Renders the playtime limits screen, including the background and stage elements.
     *
     * @param delta Time elapsed since the last frame.
     */
    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1); // Clear the screen with a black background

        spriteBatch.begin();
        // Draw the background
        mainGame.drawBackground(spriteBatch, mainGame.resourceManager.get("mainBackground"), font, "Playtime Limits");
        spriteBatch.end();

        // Render the stage
        stage.act(delta);
        stage.draw();
    }

    /**
     * Resizes the viewport to match new screen dimensions.
     *
     * @param width  The new width of the screen.
     * @param height The new height of the screen.
     */
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    /**
     * Disposes of resources used by this screen.
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
