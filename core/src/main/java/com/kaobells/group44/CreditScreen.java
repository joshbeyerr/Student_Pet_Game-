package com.kaobells.group44;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;


import java.util.HashMap;
import java.util.Map;

public class CreditScreen  extends ScreenAdapter {
    private Main mainGame;
    private Screen previousScreen;

    private SpriteBatch spriteBatch;
    private Stage stage;
    private Viewport viewport;

    private ImageButton backButton;
    private Table table;
    private Map<String, Texture> textures;

    private BitmapFont font;

    public CreditScreen(Main game, Screen previousScreenn) {
        mainGame = game;
        previousScreen = previousScreenn;
        spriteBatch = mainGame.getSharedBatch();
        viewport = mainGame.getViewport();
        backButton = mainGame.getBackButton(game, previousScreenn);
        stage = new Stage(viewport, spriteBatch);
        show();
    }

    public void show() {
        super.show();
        setStage(); // Reset input processor
        createUI(); // Recreate the UI elements
    }

    public void setStage() {
        // Set the input processor to the stage
        Gdx.input.setInputProcessor(stage);
    }

    public void createUI() {
        stage.clear();
        stage.addActor(backButton);
        table = createTable();
        textures = new HashMap<>();
        textures.put("motivation", new Texture(Gdx.files.internal("creditScreen/motivation-textbox.png")));
        textures.put("contributors", new Texture(Gdx.files.internal("creditScreen/contributors-textbox.png")));
        Image motivationBox = mainGame.createImage(textures.get("motivation"));
        Image contributorBox = mainGame.createImage(textures.get("contributors"));
        addToTable(table, motivationBox);
        addToTable(table, contributorBox);
        stage.addActor(table);
    }


    private Table createTable() {
        Table newTable = new Table();
        newTable.setFillParent(true);
        newTable.center();
        newTable.padTop(viewport.getWorldHeight() * 0.2f);
        return newTable;
    }

    private void addToTable(Table table, Actor actor) {
        float actorWidth = getDynamicWidth(); // For consistency, you can reuse button width/height for both
        float actorHeight = getDynamicHeight();
        float actorPadding = getDynamicPadding();
        table.add(actor).size(actorWidth, actorHeight).pad(actorPadding).center();
        table.row();
    }

    private float getDynamicWidth() {
        return viewport.getWorldWidth() * 0.52f; // 50% of viewport width
    }
    private float getDynamicHeight() {
        return viewport.getWorldHeight() * 0.27f; // 20% of viewport height
    }
    private float getDynamicPadding() {
        return viewport.getWorldHeight() * 0.01f; // Padding as 2% of viewport height
    }


    @Override
    public void render(float delta) {
        // Clear the screen
        ScreenUtils.clear(0, 0, 0, 1);

        spriteBatch.begin();
        font = mainGame.resourceManager.getTitleFont();
        mainGame.drawBackground(spriteBatch, mainGame.resourceManager.get("mainBackground"), font, "Credits");         // Draw the current background
        spriteBatch.end();

        // Render the stage (actors like buttons, character sprites, etc.)
        stage.act(delta);
        stage.draw();
    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);
        stage.getViewport().update(width, height, true);
        createUI();
    }

    @Override
    public void dispose() {
        for (Texture texture : textures.values()) {
            texture.dispose();
        }
        stage.dispose();
    }
}

