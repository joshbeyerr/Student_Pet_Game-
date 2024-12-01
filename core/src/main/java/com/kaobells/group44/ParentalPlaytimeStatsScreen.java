package com.kaobells.group44;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Map;

public class ParentalPlaytimeStatsScreen extends ScreenAdapter {

    private final Main mainGame;
    private final SpriteBatch spriteBatch;
    private final Stage stage;
    private final Viewport viewport;
    private final ImageButton backButton;
    private final BitmapFont font;

    private final JsonHandler jsonHandler;

    public ParentalPlaytimeStatsScreen(Main game) {
        this.mainGame = game;
        this.spriteBatch = mainGame.getSharedBatch();
        this.viewport = mainGame.getViewport();
        this.stage = new Stage(viewport, spriteBatch);
        this.backButton = mainGame.getBackButton();
        this.font = mainGame.resourceManager.getTitleFont();

        this.jsonHandler = new JsonHandler();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        initializeUI();
    }

    private void initializeUI() {
        stage.clear();

        // Back Button
        stage.addActor(backButton);

        // Load heading and reset button textures
        Texture avgPlaytimeTextBox = new Texture(Gdx.files.internal("parentalControlsScreen/avg-playtime-txtbox.png"));
        Texture totalPlaytimeTextBox = new Texture(Gdx.files.internal("parentalControlsScreen/total-playtime-txtbox.png"));
        Texture resetButtonTexture = new Texture(Gdx.files.internal("parentalControlsScreen/reset-playtime-txtbox.png"));


        // Main Content Table
        Table mainTable = new Table();
        mainTable.center();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        // Heading Labels
        Image avgPlaytimeLabelImage = new Image(avgPlaytimeTextBox);
        Image totalPlaytimeLabelImage = new Image(totalPlaytimeTextBox);

        // Playtime Values
        Label avgPlaytimeValue = new Label(formatTime(getAveragePlaytime()), new Label.LabelStyle(font, com.badlogic.gdx.graphics.Color.BLACK));
        avgPlaytimeValue.setAlignment(Align.center);

        Label totalPlaytimeValue = new Label(formatTime(getTotalPlaytime()), new Label.LabelStyle(font, com.badlogic.gdx.graphics.Color.BLACK));
        totalPlaytimeValue.setAlignment(Align.center);

        // Reset Playtime Button
        ImageButton resetButton = new ImageButton(new Image(resetButtonTexture).getDrawable());
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

    private int getAveragePlaytime() {
        // Retrieve average playtime in seconds from the database
        Object avgTime = jsonHandler.getDatabase().parentalControls.get("averagePlaytimePerSession");
        if (avgTime instanceof Map) {
            Object value = ((Map<?, ?>) avgTime).get("value");
            if (value instanceof Integer) {
                return (Integer) value; // Return the integer value directly
            }
        }
        return 0; // Default value if not found
    }

    private int getTotalPlaytime() {
        // Retrieve total playtime in seconds from the database
        Object totalTime = jsonHandler.getDatabase().parentalControls.get("totalSecondsPlayed");
        if (totalTime instanceof Map) {
            Object value = ((Map<?, ?>) totalTime).get("value");
            if (value instanceof Integer) {
                return (Integer) value; // Return the integer value directly
            }
        }
        return 0; // Default value if not found
    }

    private void resetPlaytimeStats() {
        jsonHandler.getDatabase().parentalControls.put("averagePlaytimePerSession", 0);
        jsonHandler.getDatabase().parentalControls.put("totalSecondsPlayed", 0);
        jsonHandler.saveDatabase();
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
        mainGame.drawBackground(spriteBatch, mainGame.resourceManager.get("mainBackground"), font, "Playtime Controls");
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
        stage.dispose();
    }
}
