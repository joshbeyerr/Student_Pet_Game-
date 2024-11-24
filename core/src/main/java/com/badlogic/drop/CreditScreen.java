package com.badlogic.drop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.Viewport;

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
    private Image motivationBox;
    private Image contributorBox;

    public CreditScreen(Main game, Screen previousScreenn) {
        mainGame = game;
        previousScreen = previousScreenn;
        spriteBatch = mainGame.getSharedBatch();
        viewport = mainGame.getViewport();
        backButton = mainGame.getBackButton(game,previousScreenn);
        stage = new Stage(viewport, spriteBatch);
        show();
    }

    public void setStage(){
        // Set the input processor to the stage
        Gdx.input.setInputProcessor(stage);
    }

    public void createUI(){
        stage.clear();
        stage.addActor(backButton);
        table = createTable();
        textures.put("inputQuestion", new Texture(Gdx.files.internal("creditScreen/name-textbox.png")));
        textures.put("inputBegin", new Texture(Gdx.files.internal("NameInput/begin-btn.png")));
        addActorToTable(table,motivationBox);
        addActorToTable(table,contributorBox);
        stage.addActor(table);
    }

    public void show() {
        super.show();
        setStage(); // Reset input processor
        createUI(); // Recreate the UI elements
    }
    @Override
    public void dispose() {

        stage.dispose();
    }

    private Table createTable() {
        Table newTable = new Table();
        newTable.setFillParent(true);
        newTable.center();
        newTable.padTop(viewport.getWorldHeight() * 0.1f);
        return newTable;
    }
    private void addActorToTable(Table table, Actor actor) {
        float actorWidth = getDynamicWidth(); // For consistency, you can reuse button width/height for both
        float actorHeight = getDynamicHeight();
        float actorPadding = getDynamicPadding();
        table.add(actor).size(actorWidth, actorHeight).pad(actorPadding).center();
        table.row();
    }

    private float getDynamicWidth() {
        return viewport.getWorldWidth() * 0.75f; // 50% of viewport width
    }

    private float getDynamicHeight() {
        return viewport.getWorldHeight() * 0.30f; // 20% of viewport height
    }

    private float getDynamicPadding() {
        return viewport.getWorldHeight() * 0.02f; // Padding as 2% of viewport height
    }

    public void loadTextures(){
        textures.put("inputQuestion", new Texture(Gdx.files.internal("NameInput/name-txtbox.png")));
        textures.put("inputBegin", new Texture(Gdx.files.internal("NameInput/begin-btn.png")));
    }

}
