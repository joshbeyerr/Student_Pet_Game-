package com.kaobells.group44;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TransformDrawable;
import com.badlogic.gdx.utils.Align;
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
    private Map<String, TextureRegionDrawable> drawables;




    // stores in images and image buttons
    private final Map<String, Actor> images;

    Container<Image> headContainer;
    Container<Image> bodyContainer;


    // Root table for storing the sidebar section, and the game section
    Table rootTable;

    // test vars
    // delete later
    private Actor test;
    boolean blink = false;

    InputMultiplexer multiplexer;


    public GameScreen(Main game, GameSession gameSession){

        this.mainGame = game;
        this.session = gameSession;

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

                session.character.statBarTick();

                updateStatBar("fullnessBar", session.character.getHunger());
                updateStatBar("happinessBar", session.character.getHappiness());
                updateStatBar("healthBar", session.character.getHealth());
                updateStatBar("sleepBar", session.character.getSleep());
                updateStatBar("stressBar", session.character.getSleep());



            }
        }, 1, 1f); // Update every 5 seconds


        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                updateHead(gameSession.character.getCurrentHead());
                updateBody(gameSession.character.getCurrentBody());
            }
        }, 1, 0.1f);
    }

    @Override
    public void show() {
        setStage();

    }

    public void setLabelStyles(){
        BitmapFont nameFont = mainGame.resourceManager.getFont(true);
        nameFont.getData().setScale((viewport.getWorldHeight() / 500f)); // Scale by 1.5x
        nameLabelStyle = new Label.LabelStyle();
        nameLabelStyle.font = nameFont; // Set the font for the label

        nameLabelStyle.fontColor = new Color(0x66 / 255f, 0x2d / 255f, 0x91 / 255f, 1f);

        BitmapFont scoreFont = mainGame.resourceManager.getFont(true);
        scoreFont.getData().setScale((viewport.getWorldHeight() / 1000f)); // Scale by 1.5x
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
            .width(viewport.getWorldHeight() * 0.4f)
            .align(Align.left);


        rootTable.add(tables.get("gameSection"))
                .width(viewport.getWorldHeight() * 1.13f) // This will control the table size and hence the background size
                .height(viewport.getWorldHeight() * 0.95f)
                .padLeft(viewport.getWorldHeight() * 0.04f)
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
        // screen height for dynamics
        float height = viewport.getWorldHeight();

        Table sidebar = getOrCreateTable("sidebar");

        Table nameScoreTable = createNameScoreTable();

        nameScoreTable.add(createStatBarTable()).size(height * 0.34f, height * 0.23f);

        float leftPad = (height * 0.025f);

        // Add the nested tables to the sidebar
        sidebar.add(nameScoreTable).size(height * 0.42f, height * 0.45f).padTop((height * 0.018f)).padLeft(leftPad).row();

        sidebar.add(createButtonsTable()).size(height * 0.42f, height * 0.48f).padTop((height * 0.025f)).padLeft(leftPad);
    }


    // clean method generates the table for name, score and table button
    public Table createNameScoreTable(){

        // screen height for dynamics
        float height = viewport.getWorldHeight();

        Table nameScoreTable = getOrCreateTable("nameScoreTable");

        Drawable purpleBoxDrawable = new TextureRegionDrawable(new TextureRegion(textures.get("purpleBox")));

        nameScoreTable.setBackground(purpleBoxDrawable);

        Label nameLabel = (Label)images.get("Name");
        Label scoreLabel = (Label)images.get("Score");

        // Optionally set alignment or wrapping if needed
        nameLabel.setAlignment(Align.center);
        scoreLabel.setAlignment(Align.center);

        // Add the Labels to the Table

        nameScoreTable.add(nameLabel).size((height* 0.11f), (height * 0.045f)).pad((height * 0.012f)).row(); // Add nameLabel and move to next row
        nameScoreTable.add(scoreLabel).size((height * 0.15f), (height * 0.022f)).pad((height * 0.012f)).row();

        nameScoreTable.add(images.get("openInventory")).size((height * 0.35f), (height * 0.045f)).padTop((height * 0.006f)).row();

        return nameScoreTable;
    }

    public Table createStatBar(float currentValue) {
        Table statBarTable = new Table();

        // Background of the stat bar
        TextureRegionDrawable backgroundDrawable = new TextureRegionDrawable(new Texture("game/sideBar/stat-bar-background.png"));

        statBarTable.setBackground(backgroundDrawable); // Set as actual table background

        // Foreground (dynamic portion) of the stat bar
        Image barForeground = (getStatBarColor(currentValue));

        // Calculate the width of the bar foreground based on the current stat value
        float maxBarWidth = (viewport.getWorldHeight() * 0.166f); // Maximum width of the bar

        // max bar value is always 100
        float currentBarWidth = (currentValue / 100) * maxBarWidth;

        // Wrap the foreground bar in a container to enforce its width
        Container<Image> barForegroundContainer = new Container<>(barForeground);
        barForegroundContainer.width(currentBarWidth);
        barForegroundContainer.align(Align.left); // Ensure the foreground is aligned to the left

        // Add only the foreground to the table (background is already set)
        statBarTable.add(barForegroundContainer).width(maxBarWidth).height((viewport.getWorldHeight() * 0.012f)).align(Align.left); // Align the bar container to the left

        return statBarTable;
    }

    public void updateStatBar(String statName, float currentValue) {
        // Retrieve the stat bar table from the images map
        Table statBarTable = (Table) images.get(statName);
        if (statBarTable != null) {
            Actor child = statBarTable.getChildren().get(0);
            if (child instanceof Container) {
                Container<?> container = (Container<?>) child;
                if (container.getActor() instanceof Image) {
                    // Use the already-checked container
                    @SuppressWarnings("unchecked")
                    Container<Image> barForegroundContainer = (Container<Image>) container;

                    barForegroundContainer.setActor(getStatBarColor(currentValue));

                    float maxBarWidth = (viewport.getWorldHeight() * 0.166f); // Maximum width of the bar

                    // 100 being the max value of the stat bar
                    float newWidth = (currentValue / 100) * maxBarWidth;
                    // Update the width of the container
                    barForegroundContainer.width(newWidth);

                    // Force the container to relayout with the updated width
                    barForegroundContainer.invalidate();
                } else {
                    throw new IllegalStateException("The container does not contain an Image!");
                }
            } else {
                throw new IllegalStateException("The first child is not a Container!");
            }
        }
    }

    public Image getStatBarColor(float currentValue) {
        if (currentValue >= 0.0f && currentValue <= 100.0f) {
            if (currentValue >= 80.0f) {
                return new Image(drawables.get("greenBar"));
            } else if (currentValue >= 60.0f) {
                return new Image(drawables.get("grellowBar"));
            } else if (currentValue >= 40.0f) {
                return new Image(drawables.get("yellowBar"));
            } else if (currentValue >= 20.0f) {
                return new Image(drawables.get("orangeBar"));
            } else {
                return new Image(drawables.get("redBar"));
            }
        }
        return null;
    }


    public Table createStatBarTable(){
        // Stat Bars Section
        Table statBarsTable = getOrCreateTable("statBarsTable");

        statBarsTable.padTop(viewport.getWorldHeight() * 0.012f);

        float padValue = viewport.getWorldHeight() * 0.006f;
        float imageWidth = viewport.getWorldHeight() * 0.15f;
        float imageHeight = viewport.getWorldHeight() * 0.03f;

        float barWidth = viewport.getWorldHeight() * 0.166f;
        float barHeight= viewport.getWorldHeight() * 0.013f;

        statBarsTable.add(images.get("fullnessBox")).size(imageWidth, imageHeight).pad(padValue);

        statBarsTable.add(images.get("fullnessBar")).size(barWidth, barHeight).pad(padValue).row();

        statBarsTable.add(images.get("sleepBox")).size(imageWidth, imageHeight).pad(padValue);
        statBarsTable.add(images.get("sleepBar")).size(barWidth, barHeight).pad(padValue).row();

        statBarsTable.add(images.get("happinessBox")).size(imageWidth, imageHeight).pad(padValue);
        statBarsTable.add(images.get("happinessBar")).size(barWidth, barHeight).pad(padValue).row();

        statBarsTable.add(images.get("healthBox")).size(imageWidth, imageHeight).pad(padValue);
        statBarsTable.add(images.get("healthBar")).size(barWidth, barHeight).pad(padValue).row();

        statBarsTable.add(images.get("stressBox")).size(imageWidth, imageHeight).pad(padValue);
        statBarsTable.add(images.get("stressBar")).size(barWidth, barHeight).pad(padValue).row();

        return statBarsTable;

    }

    public Table createButtonsTable(){
        // Buttons Section
        Table buttonsTable = getOrCreateTable("buttonsTable");

        float buttonPad = viewport.getWorldHeight() * 0.03f;

        float buttonWidth =  viewport.getWorldHeight()  * 0.21f; // 13% of viewport width
        float buttonHeight = viewport.getWorldHeight() * 0.13f; // 13% of viewport height


        buttonsTable.add(images.get("feed")).size(buttonWidth,buttonHeight).padBottom(buttonPad);

        buttonsTable.add(images.get("sleep")).size(buttonWidth,buttonHeight).padBottom(buttonPad).row();

        buttonsTable.add(images.get("exercise")).size(buttonWidth,buttonHeight).padBottom(buttonPad);
        buttonsTable.add(images.get("play")).size(buttonWidth,buttonHeight).padBottom(buttonPad).row();

        buttonsTable.add(images.get("gift")).size(buttonWidth,buttonHeight).padBottom(buttonPad);
        buttonsTable.add(images.get("doctor")).size(buttonWidth,buttonHeight).padBottom(buttonPad).row();
        // Add more buttons...

        return buttonsTable;
    }

    public void gameSection() {
        Table gameSection = getOrCreateTable("gameSection");

        // Set the background for the game section
        TextureRegionDrawable backgroundDrawable = new TextureRegionDrawable(new TextureRegion(textures.get("gameBackground")));
        gameSection.setBackground(backgroundDrawable);

        // Create a container for the head
        headContainer = new Container<>();
        headContainer.size(viewport.getWorldHeight() * 0.25f, viewport.getWorldHeight() * 0.22f);
        headContainer.setActor(session.character.getCurrentHead());

        // Add the head container to the table
        gameSection.add(headContainer)
                .padTop(viewport.getWorldHeight() * 0.35f)
                .row(); // Move to the next row

        // Create a container for the body
        bodyContainer = new Container<>();
        bodyContainer.size(viewport.getWorldHeight() * 0.36f, viewport.getWorldHeight() * 0.20f);
        bodyContainer.setActor(session.character.getCurrentBody());

        // Add the body container to the table
        gameSection.add(bodyContainer);

        // Center the gameSection content within itself
        gameSection.center();
    }



    // ? idk, i think all head and body textures should be stored in character class,
    // and a call of getHead or getBody will return the image

    public void updateHead(Image newHead) {
        // Find the head container
        if (headContainer != null) {
            headContainer.setActor(newHead); // Update the actor inside the container
        }

    }

    public void updateBody(Image newBody) {
        // Find the head container
        if (bodyContainer != null) {
            bodyContainer.setActor(newBody); // Update the actor inside the container
        }

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
        ImageButton feed = mainGame.createImageButton(textures.get("feed"));
        feed.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                session.character.feed();


            }
        });
        images.put("feed", feed);

        images.put("sleep", mainGame.createImageButton(textures.get("sleep")));

        ImageButton exercise = mainGame.createImageButton(textures.get("exercise"));
        exercise.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("exercise", "here");
                session.character.exercise();

            }
        });

        images.put("exercise", exercise);
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

        images.put("Name", new Label(session.character.getName(), nameLabelStyle));
        images.put("Score", new Label("Score: " + (session.score), scoreLabelStyle));

        // star bar colors
        drawables = new HashMap<>();

        drawables.put("greenBar", new TextureRegionDrawable(new Texture("game/sideBar/stat/stat-green-bg.png")));
        drawables.put("grellowBar", new TextureRegionDrawable(new Texture("game/sideBar/stat/stat-grellow-bg.png")));
        drawables.put("yellowBar", new TextureRegionDrawable(new Texture("game/sideBar/stat/stat-yellow-bg.png")));
        drawables.put("orangeBar", new TextureRegionDrawable(new Texture("game/sideBar/stat/stat-orange-bg.png")));
        drawables.put("redBar", new TextureRegionDrawable(new Texture("game/sideBar/stat/stat-red-bg.png")));

        // creating actual stat bar images (tables)
        Table sleepBar = createStatBar(session.character.getSleep());
        images.put("sleepBar", sleepBar);

        Table happinessBar = createStatBar(session.character.getHappiness());
        images.put("happinessBar", happinessBar);

        Table stressBar = createStatBar(session.character.getStress());
        images.put("stressBar", stressBar);

        Table healthBar = createStatBar(session.character.getHealth());
        images.put("healthBar", healthBar);

        // stat bar creation
        Table fullnessBar = createStatBar(session.character.getHunger());
        images.put("fullnessBar", fullnessBar);

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

    }

    @Override
    public void dispose() {

    }

    public void setStage(){
        if (multiplexer == null){
            // Create an InputAdapter for key press handling
            InputAdapter inputAdapter = new InputAdapter() {
                @Override
                public boolean keyDown(int keycode) {
                    if (keycode == Input.Keys.F) {
                        session.character.feed();
                        return true;
                    }
                    return true;
                }
            };
            multiplexer = new InputMultiplexer();
            multiplexer.addProcessor(stage);       // Stage for UI interactions
            multiplexer.addProcessor(inputAdapter); // InputAdapter for key presses
        }
        Gdx.input.setInputProcessor(multiplexer);
    }

}
