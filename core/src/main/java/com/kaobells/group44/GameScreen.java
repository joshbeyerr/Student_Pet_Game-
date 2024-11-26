package com.kaobells.group44;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.utils.Timer;

import java.util.HashMap;
import java.util.Map;

public class GameScreen extends ScreenAdapter{

    private final Main mainGame;
    private final GameSession session;

    private final SpriteBatch spriteBatch;
    private final Stage stage;
    private final Viewport viewport;

    Label.LabelStyle nameLabelStyle;
    Label.LabelStyle scoreLabelStyle;


    private Map<String, Texture> textures;
    private final Map<String, Table> tables;

    // stores in images and image buttons
    private final Map<String, Actor> images;


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

        setLabelStyles();

        loadTextures();
        loadImageButtons();
        createUI();


        // TESTING A TIMER WITH TEST SHIT
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                Label scoreLabel = (Label) images.get("Score");
                session.score += 1;
                scoreLabel.setText("Score: " + (session.score));

                session.character.setHunger(session.character.getHunger() - 10);
                updateStatBar("fullnessBar", session.character.getHunger(), 100);


            }
        }, 0, 5f); // Update every 5 seconds

    }

    @Override
    public void show() {
        setStage();

    }

    public void setLabelStyles(){
        BitmapFont nameFont = mainGame.resourceManager.getFont(true);
        nameFont.getData().setScale(4f); // Scale by 1.5x
        nameLabelStyle = new Label.LabelStyle();
        nameLabelStyle.font = nameFont; // Set the font for the label

        nameLabelStyle.fontColor = new Color(0x66 / 255f, 0x2d / 255f, 0x91 / 255f, 1f);

        BitmapFont scoreFont = mainGame.resourceManager.getFont(true);
        scoreFont.getData().setScale(2.0f); // Scale by 1.5x
        scoreLabelStyle = new Label.LabelStyle();
        scoreLabelStyle.font = scoreFont; // Set the font for the label
        scoreLabelStyle.fontColor = new Color(0xb5 / 255f, 0x84 / 255f, 0xdb / 255f, 1f);


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

    private Table getOrCreateTable(String key) {
        Table table = tables.get(key);
        if (table == null) {
            table = new Table();
            tables.put(key, table);
        } else {
            table.clear();
        }
        return table;
    }


    public void sideBar() {
        Table sidebar = getOrCreateTable("sidebar");

        Table nameScoreTable = createNameScoreTable();

        nameScoreTable.add(createStatBarTable()).pad(10f);


        // Add the nested tables to the sidebar
        sidebar.add(nameScoreTable).height(viewport.getWorldHeight() * 0.40f).padTop(30f).padLeft(60).row();
//        sidebar.add(statBarsTable).row();
        sidebar.add(createButtonsTable()).padTop(50f).padLeft(60);


    }


    // clean method generates the table for name, score and table button
    public Table createNameScoreTable(){
        Table nameScoreTable = getOrCreateTable("nameScoreTable");

        Drawable purpleBoxDrawable = new TextureRegionDrawable(new TextureRegion(textures.get("purpleBox")));

        nameScoreTable.setBackground(purpleBoxDrawable);

        Label nameLabel = (Label)images.get("Name");
        Label scoreLabel = (Label)images.get("Score");

        // Optionally set alignment or wrapping if needed
        nameLabel.setAlignment(Align.center);
        scoreLabel.setAlignment(Align.center);

        // Add the Labels to the Table
        nameScoreTable.add(nameLabel).pad(20f).row(); // Add nameLabel and move to next row
        nameScoreTable.add(scoreLabel).pad(20f).row();

        nameScoreTable.add(images.get("openInventory")).pad(10f).row();

        return nameScoreTable;
    }

    public Table createStatBar(float currentValue, float maxValue) {
        Table statBarTable = new Table();

        // Background of the stat bar
        TextureRegionDrawable backgroundDrawable = new TextureRegionDrawable(new Texture("game/sideBar/stat-bar-background.png"));
        statBarTable.setBackground(backgroundDrawable); // Set as actual table background

        // Foreground (dynamic portion) of the stat bar
        TextureRegionDrawable foregroundDrawable = new TextureRegionDrawable(new Texture("game/sideBar/stat-bar-foreground.png"));
        Image barForeground = new Image(foregroundDrawable);

        // Calculate the width of the bar foreground based on the current stat value
        float maxBarWidth = 300f; // Maximum width of the bar
        float currentBarWidth = (currentValue / maxValue) * maxBarWidth;

        // Wrap the foreground bar in a container to enforce its width
        Container<Image> barForegroundContainer = new Container<>(barForeground);
        barForegroundContainer.width(currentBarWidth);
        barForegroundContainer.align(Align.left); // Ensure the foreground is aligned to the left

        // Add only the foreground to the table (background is already set)
        statBarTable.add(barForegroundContainer).width(maxBarWidth).height(20).align(Align.left); // Align the bar container to the left

        return statBarTable;
    }

    public void updateStatBar(String statName, float currentValue, float maxValue) {
        // Retrieve the stat bar table from the images map
        Table statBarTable = (Table) images.get(statName);
        if (statBarTable != null) {
            // Get the foreground container (the first child of the table)
            Container<Image> barForegroundContainer = (Container<Image>) statBarTable.getChildren().get(0);

            // Calculate the new width of the foreground bar
            float maxBarWidth = 300f;
            float newWidth = (currentValue / maxValue) * maxBarWidth;

            // Update the width of the container
            barForegroundContainer.width(newWidth);

            // Force the container to relayout with the updated width
            barForegroundContainer.invalidate();
        }
    }



    public Table createStatBarTable(){
        // Stat Bars Section
        Table statBarsTable = getOrCreateTable("statBarsTable");

        statBarsTable.padTop(20f);

        statBarsTable.add(images.get("fullnessBox")).pad(10f);

        statBarsTable.add(images.get("fullnessBar")).pad(10f).row();

        statBarsTable.add(images.get("sleepBox")).pad(10f);
        statBarsTable.add(images.get("sleepBar")).pad(10f).row();

        statBarsTable.add(images.get("happinessBox")).pad(10f);
        statBarsTable.add(images.get("happinessBar")).pad(10f).row();

        statBarsTable.add(images.get("healthBox")).pad(10f);
        statBarsTable.add(images.get("healthBar")).pad(10f).row();

        statBarsTable.add(images.get("stressBox")).pad(10f);
        statBarsTable.add(images.get("stressBar")).pad(10f).row();

        return statBarsTable;

    }

    public Table createButtonsTable(){
        // Buttons Section
        Table buttonsTable = getOrCreateTable("buttonsTable");

        float buttonPad = viewport.getWorldHeight() * 0.03f;

        float buttonWidth = 375f;
        float ButtonHeight = 240f;


        buttonsTable.add(images.get("feed")).size(buttonWidth,ButtonHeight).padBottom(buttonPad).fill();

        buttonsTable.add(images.get("sleep")).size(buttonWidth,ButtonHeight).padBottom(buttonPad).fill().row();

        buttonsTable.add(images.get("exercise")).size(buttonWidth,ButtonHeight).padBottom(buttonPad).fill();
        buttonsTable.add(images.get("play")).size(buttonWidth,ButtonHeight).padBottom(buttonPad).fill().row();

        buttonsTable.add(images.get("gift")).size(buttonWidth,ButtonHeight).padBottom(buttonPad).fill();
        buttonsTable.add(images.get("doctor")).size(buttonWidth,ButtonHeight).padBottom(buttonPad).fill().row();
        // Add more buttons...

        return buttonsTable;
    }


    public void gameSection() {

        Table gameSection = getOrCreateTable("gameSection");

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

        textures.put("fullnessBox", new Texture(Gdx.files.internal("game/" + sideBar + "/fullness-txtbox.png")));
        textures.put("sleepBox", new Texture(Gdx.files.internal("game/" + sideBar + "/sleep-txtbox.png")));
        textures.put("happinessBox", new Texture(Gdx.files.internal("game/" + sideBar + "/happiness-txtbox.png")));
        textures.put("healthBox", new Texture(Gdx.files.internal("game/" + sideBar + "/health-txtbox.png")));
        textures.put("stressBox", new Texture(Gdx.files.internal("game/" + sideBar + "/stress-txtbox.png")));

        textures.put("feed", new Texture(Gdx.files.internal("game/" + sideBar + "/feed-btn.png")));
        textures.put("sleep", new Texture(Gdx.files.internal("game/" + sideBar + "/sleep-btn.png")));
        textures.put("exercise", new Texture(Gdx.files.internal("game/" + sideBar + "/exercise-btn.png")));
        textures.put("play", new Texture(Gdx.files.internal("game/" + sideBar + "/play-btn.png")));
        textures.put("gift", new Texture(Gdx.files.internal("game/" + sideBar + "/gift-btn.png")));
        textures.put("doctor", new Texture(Gdx.files.internal("game/" + sideBar + "/doctor-btn.png")));

        textures.put("openInventory", new Texture(Gdx.files.internal("game/" + sideBar + "/open-inventory-btn.png")));
        textures.put("purpleBox", new Texture(Gdx.files.internal("game/" + sideBar + "/purple-box.png")));
        textures.put("purpleLabel", new Texture(Gdx.files.internal("game/" + sideBar + "/purple-label.png")));

        textures.put("gameBackground", new Texture(Gdx.files.internal("game/actual-game-bg.png")));

    }


    public void loadImageButtons(){
        images.put("feed", mainGame.createImageButton(textures.get("feed")));
        images.put("sleep", mainGame.createImageButton(textures.get("sleep")));
        images.put("exercise", mainGame.createImageButton(textures.get("exercise")));
        images.put("play", mainGame.createImageButton(textures.get("play")));
        images.put("gift", mainGame.createImageButton(textures.get("gift")));
        images.put("doctor", mainGame.createImageButton(textures.get("doctor")));

        images.put("fullnessBox", mainGame.createImage(textures.get("fullnessBox")));
        images.put("sleepBox", mainGame.createImage(textures.get("sleepBox")));
        images.put("happinessBox", mainGame.createImage(textures.get("happinessBox")));
        images.put("healthBox", mainGame.createImage(textures.get("healthBox")));
        images.put("stressBox", mainGame.createImage(textures.get("stressBox")));

        images.put("openInventory", mainGame.createImageButton(textures.get("openInventory")));

        images.put("purpleLabel", mainGame.createImageButton(textures.get("purpleLabel")));
//        images.put("purpleBox", mainGame.createImage(textures.get("purpleBox")));

        images.put("Name", new Label(session.character.getName(), nameLabelStyle));
        images.put("Score", new Label("Score: " + (session.score), scoreLabelStyle));


        // stat bar creation
        Table fullnessBar = createStatBar(session.character.getHunger(), 100);

        images.put("fullnessBar", fullnessBar);

        Table sleepBar = createStatBar(session.character.getSleep(), 100);
        images.put("sleepBar", sleepBar);

        Table happinessBar = createStatBar(session.character.getHappiness(), 100);
        images.put("happinessBar", happinessBar);

        Table stressBar = createStatBar(session.character.getStress(), 100);
        images.put("stressBar", stressBar);

        Table healthBar = createStatBar(session.character.getHealth(), 100);
        images.put("healthBar", healthBar);



    }

    @Override
    public void render(float delta) {
        // Clear the screen
        ScreenUtils.clear(0, 0, 0, 1);

        spriteBatch.begin();
        // Draw the current background
        spriteBatch.draw(textures.get("background"), 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());;

        // Update score label text if it changes

//        Label scoreLabel = (Label) images.get("Score");
//        session.score += 1;

//        scoreLabel.setText("Score : " + (session.score));

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
