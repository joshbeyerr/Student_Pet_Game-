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

public class ParentalPlaytimeLimits extends ScreenAdapter {
    private final Main mainGame;
    private final Stage stage;
    private final Viewport viewport;
    private final Map<String, Texture> textures;
    private final SpriteBatch spriteBatch;
    private final BitmapFont font;

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

    private void loadTextures() {
        textures.put("allowedHoursTitle", new Texture(Gdx.files.internal("parentalControlsScreen/allowed-hrs-txtbox.png")));
        textures.put("checked", new Texture(Gdx.files.internal("parentalControlsScreen/checked-parent-btn.png")));
        textures.put("unchecked", new Texture(Gdx.files.internal("parentalControlsScreen/unchecked-parent-btn.png")));
    }

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

    private ImageButton createToggleButton(boolean isAllowed, String jsonKey) {
        TextureRegionDrawable initialDrawable = new TextureRegionDrawable(new TextureRegion(isAllowed ? textures.get("checked") : textures.get("unchecked")));
        ImageButton button = new ImageButton(initialDrawable);

        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean currentState = mainGame.jsonHandler.getParentalControlBoolean(jsonKey);
                boolean newState = !currentState; // Toggle the state
                mainGame.jsonHandler.getDatabase().parentalControls.put(jsonKey, newState);
                mainGame.jsonHandler.saveDatabase();

                // Update button texture
                TextureRegionDrawable newDrawable = new TextureRegionDrawable(new TextureRegion(newState ? textures.get("checked") : textures.get("unchecked")));
                button.getStyle().imageUp = newDrawable;
            }
        });

        return button;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

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

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        // Dispose of all textures
        for (Texture texture : textures.values()) {
            texture.dispose();
        }
        stage.dispose();
    }
}
