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


import javax.swing.text.View;
import java.util.HashMap;
import java.util.Map;

public class CreditScreen  extends ScreenAdapter {
    private final Main mainGame;

    private final SpriteBatch spriteBatch;
    private final Stage stage;
    private final Viewport viewport;

    private final ImageButton backButton;
    private Map<String, Texture> textures;

    public CreditScreen(Main game) {
        mainGame = game;

        spriteBatch = mainGame.getSharedBatch();
        viewport = mainGame.getViewport();

        backButton = mainGame.getBackButton();
        stage = new Stage(viewport, spriteBatch);
    }

    public void show() {

        createUI();
        super.show();
        setStage(); // Reset input processor
    }

    public void setStage() {
        Gdx.input.setInputProcessor(stage);
    }

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


    private Table createTable() {
        Table newTable = new Table();
        newTable.setFillParent(true);
        newTable.center();
        newTable.padTop(viewport.getWorldHeight() * 0.1f);
        return newTable;
    }

    private void addToTable(Table table, Actor actor) {
        float width = viewport.getWorldWidth() * 0.4f;
        float height = viewport.getWorldHeight() * 0.3f;
        float pad = viewport.getWorldHeight() * 0.01f;

        table.add(actor).size(width, height).pad(pad).center();
        table.row();
    }


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

