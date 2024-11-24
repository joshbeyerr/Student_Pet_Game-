package com.kaobells.group44;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.HashMap;
import java.util.Map;

public class GameScreen extends ScreenAdapter{

    private Main mainGame;
    private GameSession session;

    private SpriteBatch spriteBatch;
    private Stage stage;
    private Viewport viewport;

    Label.LabelStyle labelStyle;

    private Map<String, Texture> textures;
    private Map<String, Table> tables;

    // stores in images and imagebuttons
    private Map<String, Actor> images;


    // Root table for storing the sidebar section, and the game section
    Table rootTable;

    public GameScreen(Main game, GameSession sessionn){
        this.mainGame = game;
        this.session = sessionn;

        spriteBatch = mainGame.getSharedBatch();
        viewport = mainGame.getViewport();

        stage = new Stage(viewport, spriteBatch);
        tables = new HashMap<>();
        images = new HashMap<>();

        // handling font shit
        BitmapFont textFont = mainGame.resourceManager.getFont(true);
        labelStyle = new Label.LabelStyle();
        labelStyle.font = textFont; // Set the font for the label
        labelStyle.fontColor = Color.PURPLE;

        setStage();

        loadTextures();
        loadImageButtons();

        createUI();

    }

    public void createUI() {
        stage.clear();

        if (rootTable == null) {
            rootTable = new Table();
            rootTable.setFillParent(true);
        } else {
            rootTable.clear();
        }

        stage.addActor(rootTable);

        // Add the sidebar and game section with proper proportions
        sideBar(); // Add sidebar
        gameSection(); // Add game section

        // Add the tables to the rootTable
        rootTable.add(tables.get("sidebar"))
            .width(viewport.getWorldWidth() * 0.25f)
            .fillY()
            .align(Align.left);

        rootTable.add(tables.get("gameSection"))
            .width(viewport.getWorldWidth() * 0.75f)
            .fillY()
            .align(Align.right);
    }


    public void sideBar() {
        Table sidebar;

        // Check if the sidebar table already exists in the HashMap
        if (tables.get("sidebar") == null) {
            sidebar = new Table();
            tables.put("sidebar", sidebar); // Store the table for reuse
        } else {
            sidebar = tables.get("sidebar");
            sidebar.clear(); // Clear contents to avoid duplicates
        }

        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();


        // Name and Score Section
        Table nameScoreTable;
        if (tables.get("nameScoreTable") == null) {
            nameScoreTable = new Table();
//            nameScoreTable.add(new Label("Wiktor", mainGame.getSkin())).row();
//            nameScoreTable.add(new Label("Score: 1,896,312", mainGame.getSkin())).row();
            tables.put("nameScoreTable", nameScoreTable); // Store for reuse
        } else {
            nameScoreTable = tables.get("nameScoreTable");
            nameScoreTable.clear();
        }

        Drawable purpleBoxDrawable = new TextureRegionDrawable(new TextureRegion(textures.get("purpleBox")));

        Texture purpleBoxTexture = textures.get("purpleBox");

// Adjust width and height
        float adjustedWidth = purpleBoxTexture.getWidth() * 0.8f; // Reduce width by 10%
        float adjustedHeight = purpleBoxTexture.getHeight() * 0.65f; // Reduce height by 15%

// Set the size of the table
        nameScoreTable.setSize(adjustedWidth, adjustedHeight);

// Set the Drawable as the table background
        nameScoreTable.setBackground(purpleBoxDrawable);



        // Stat Bars Section
        Table statBarsTable;
        if (tables.get("statBarsTable") == null) {
            statBarsTable = new Table();
//            statBarsTable.add(createStatBar("Health", 0.8f)).row();
//            statBarsTable.add(createStatBar("Sleep", 0.6f)).row();
//            statBarsTable.add(createStatBar("Happiness", 0.9f)).row();
//            statBarsTable.add(createStatBar("Fullness", 0.5f)).row();
//            statBarsTable.add(createStatBar("Stress", 0.3f)).row();
            tables.put("statBarsTable", statBarsTable); // Store for reuse
        } else {
            statBarsTable = tables.get("statBarsTable");
            statBarsTable.clear();
        }

        // Buttons Section
        Table buttonsTable;
        if (tables.get("buttonsTable") == null) {
            buttonsTable = new Table();
            tables.put("buttonsTable", buttonsTable); // Store for reuse
        } else {
            buttonsTable = tables.get("buttonsTable");
            buttonsTable.clear();
        }

//        float buttonWidth = viewport.getWorldWidth() * 0.3f;
//        float buttonHeight = viewport.getWorldHeight() * 0.3f;
        float buttonPad = viewport.getWorldHeight() * 0.02f;


        float buttonWidth = 375f;
        float ButtonHeight = 220f;


        buttonsTable.add(images.get("feed")).size(buttonWidth,ButtonHeight).padBottom(buttonPad).fill();

        buttonsTable.add(images.get("sleep")).size(buttonWidth,ButtonHeight).padBottom(buttonPad).fill().row();

        buttonsTable.add(images.get("exercise")).size(buttonWidth,ButtonHeight).padBottom(buttonPad).fill();
        buttonsTable.add(images.get("play")).size(buttonWidth,ButtonHeight).padBottom(buttonPad).fill().row();

        buttonsTable.add(images.get("gift")).size(buttonWidth,ButtonHeight).padBottom(buttonPad).fill();
        buttonsTable.add(images.get("doctor")).size(buttonWidth,ButtonHeight).padBottom(buttonPad).fill().row();
        // Add more buttons...

        // Add the nested tables to the sidebar
        sidebar.add(nameScoreTable).padBottom(50).padLeft(60).row();
//        sidebar.add(statBarsTable).row();
        sidebar.add(buttonsTable).padLeft(60).row();
    }


    public void gameSection() {
        Table gameSection;

        // Check if the table already exists in the HashMap
        if (tables.get("gameSection") == null) {
            gameSection = new Table();
            tables.put("gameSection", gameSection);
        } else {
            gameSection = tables.get("gameSection");
            gameSection.clear(); // Clear existing elements for re-adding
        }

        // Set the background for the game section with padding around it
        Image background = new Image(new TextureRegionDrawable(new TextureRegion(textures.get("gameBackground"))));

        // Add the background image to the table
        gameSection.add(background)
            .width(viewport.getWorldWidth() * 0.7f) // Smaller than the allocated 2/3 space
            .height(viewport.getWorldHeight() * 0.95f) // Leave room for padding at top/bottom
            .pad(viewport.getWorldWidth() * 0.01f   ); // Add padding for the illusion of a screen within a screen

        // Center the gameSection content within itself
        gameSection.center();
    }


    public void loadTextures(){
        textures = new HashMap<>();

        textures.put("background", new Texture(Gdx.files.internal("game/game-bg.png")));

        String sideBar = "sideBar";

        textures.put("feed", new Texture(Gdx.files.internal("game/" + sideBar + "/feed-btn.png")));
        textures.put("sleep", new Texture(Gdx.files.internal("game/" + sideBar + "/sleep-btn.png")));
        textures.put("exercise", new Texture(Gdx.files.internal("game/" + sideBar + "/exercise-btn.png")));
        textures.put("play", new Texture(Gdx.files.internal("game/" + sideBar + "/play-btn.png")));
        textures.put("gift", new Texture(Gdx.files.internal("game/" + sideBar + "/gift-btn.png")));
        textures.put("doctor", new Texture(Gdx.files.internal("game/" + sideBar + "/doctor-btn.png")));

        textures.put("openInventory", new Texture(Gdx.files.internal("game/" + sideBar + "/open-inventory-btn.png")));
        textures.put("purpleBox", new Texture(Gdx.files.internal("game/" + sideBar + "/purple-box.png")));
        textures.put("purpleLabel", new Texture(Gdx.files.internal("game/" + sideBar + "/purple-label.png")));

        textures.put("gameBackground", new Texture(Gdx.files.internal("game/game-backdrop.png")));

    }


    public void loadImageButtons(){
        images.put("feed", mainGame.createImageButton(textures.get("feed")));
        images.put("sleep", mainGame.createImageButton(textures.get("sleep")));
        images.put("exercise", mainGame.createImageButton(textures.get("exercise")));
        images.put("play", mainGame.createImageButton(textures.get("play")));
        images.put("gift", mainGame.createImageButton(textures.get("gift")));
        images.put("doctor", mainGame.createImageButton(textures.get("doctor")));

//        images.put("purpleBox", mainGame.createImage(textures.get("purpleBox")));

        images.put("Name", new Label(session.character.getName(), labelStyle));
    }

    @Override
    public void render(float delta) {
        // Clear the screen
        ScreenUtils.clear(0, 0, 0, 1);

        spriteBatch.begin();
        // Draw the current background
        spriteBatch.draw(textures.get("background"), 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());;

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

    }


    public void setStage(){
        Gdx.input.setInputProcessor(stage);
    }

}
