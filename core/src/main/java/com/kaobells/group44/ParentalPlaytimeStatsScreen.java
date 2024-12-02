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

public class ParentalPlaytimeStatsScreen extends ScreenAdapter {

    private final Main mainGame;
    private final SpriteBatch spriteBatch;
    private final Stage stage;
    private final Viewport viewport;
    private final ImageButton backButton;
    private final BitmapFont font;
    private final Map<String, Texture> textures; // Texture management

    public ParentalPlaytimeStatsScreen(Main game) {
        this.mainGame = game;
        this.spriteBatch = mainGame.getSharedBatch();
        this.viewport = mainGame.getViewport();
        this.stage = new Stage(viewport, spriteBatch);
        this.backButton = mainGame.getBackButton();
        this.font = mainGame.resourceManager.getTitleFont();
        this.textures = new HashMap<>(); // Initialize textures map
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        loadTextures(); // Load all textures
        initializeUI();
    }

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

    private void loadTextures() {
        textures.put("avgPlaytimeTextBox", new Texture(Gdx.files.internal("parentalControlsScreen/avg-playtime-txtbox.png")));
        textures.put("totalPlaytimeTextBox", new Texture(Gdx.files.internal("parentalControlsScreen/total-playtime-txtbox.png")));
        textures.put("resetButton", new Texture(Gdx.files.internal("parentalControlsScreen/reset-playtime-txtbox.png")));
    }

    private int getAveragePlaytime() {
        return mainGame.jsonHandler.getParentalControlInt("averagePlaytimePerSession");
    }

    private int getTotalPlaytime() {
        return mainGame.jsonHandler.getParentalControlInt("totalSecondsPlayed");
    }

    private void resetPlaytimeStats() {
        mainGame.jsonHandler.setParentalControlInt("averagePlaytimePerSession", 0);
        mainGame.jsonHandler.setParentalControlInt("totalSecondsPlayed", 0);
        mainGame.jsonHandler.setParentalControlInt("totalSessionsPlayed", 0);
    }

    private String formatTime(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1); // Clear the screen with a black background
        spriteBatch.begin();
        mainGame.drawBackground(spriteBatch, mainGame.resourceManager.get("mainBackground"), font, "Playtime Stats");
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
        // Dispose of all textures
        for (Texture texture : textures.values()) {
            texture.dispose();
        }
        stage.dispose();
    }
}
