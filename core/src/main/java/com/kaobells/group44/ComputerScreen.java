package com.kaobells.group44;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@code ComputerScreen} class represents a screen in the game
 * where the player can interact with the computer and select mini-games to play.
 * It handles the UI layout, button interactions, and screen transitions.
 *
 * <p>The class utilizes LibGDX's {@link Stage} and {@link Table} for UI management,
 * and it dynamically loads textures and resources required for the screen.</p>
 *
 * @author group 44
 * @version 1.0
 */
public class ComputerScreen extends ScreenAdapter {
    private final Main mainGame;
    private final GameSession session;
    private final Viewport viewport;

    private final SpriteBatch spriteBatch;
    private final Stage stage;
    private final ImageButton backButton;

    private Map<String, Texture> textures;
    private final Map<String, ImageButton> buttons;

    /**
     * Constructs a new {@code ComputerScreen} instance.
     *
     * @param mainGame The main game instance managing shared resources and screen transitions.
     * @param session  The current game session containing player and game state data.
     */
    public ComputerScreen(Main mainGame, GameSession session) {
        this.mainGame = mainGame;
        this.session = session;

        viewport = mainGame.getViewport();
        spriteBatch = mainGame.getSharedBatch();
        stage = new Stage(mainGame.getViewport(), spriteBatch);
        backButton = mainGame.getBackButton();


        buttons = new HashMap<>();
        loadTextures();
        createUI();
    }

    /**
     * Loads the textures required for the {@code ComputerScreen}.
     * The textures are stored in a {@code Map} for efficient access.
     */
    private void loadTextures() {
        textures = new HashMap<>();

        textures.put("computerScreenBg", new Texture(Gdx.files.internal("computerScreen/computerScreen-bg.png")));
        textures.put("minigamePrompt", new Texture(Gdx.files.internal("computerScreen/minigame-prompt-txt.png")));
        textures.put("bugdodgeBtn", new Texture(Gdx.files.internal("computerScreen/bugdodge-btn.png")));
        textures.put("jbordleBtn", new Texture(Gdx.files.internal("computerScreen/jbordle-btn.png")));
        textures.put("backBtn", new Texture(Gdx.files.internal("computerScreen/minigame-back-btn.png")));
    }

    /**
     * Creates the UI elements for the {@code ComputerScreen} and sets up
     * their layout using a {@link Table}.
     */
    private void createUI() {
        // Set the background
        Image background = new Image(new TextureRegionDrawable(textures.get("computerScreenBg")));
        background.setSize(stage.getWidth(), stage.getHeight());
        background.setPosition(0, 0);
        stage.addActor(background);

        Table table = new Table();
        table.setFillParent(true); // Make the table fill the screen
//        table.top().padTop(viewport.getWorldWidth() * 0.06f);   // Align the table to the top of the screen with some padding
        stage.addActor(table);


        Table gamePromtTable = new Table();
        Image prompt = new Image(new TextureRegionDrawable(textures.get("minigamePrompt")));
        gamePromtTable.add(prompt);

        float padBottom = viewport.getWorldWidth() * 0.01f;
        table.add(gamePromtTable).padBottom(padBottom).center().row();


        Table gameButtonsTable = new Table();

        ImageButton bugdodgeBtn = mainGame.createImageButton(textures.get("bugdodgeBtn"));
        bugdodgeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mainGame.popScreen();
                mainGame.pushScreen(new BugDodger(mainGame, session.character));
            }
        });

        ImageButton jbordle = mainGame.createImageButton(textures.get("jbordleBtn"));
        jbordle.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mainGame.popScreen();
                mainGame.pushScreen(new JBordle(mainGame,session.character));

            }
        });

        float pad = viewport.getWorldWidth() * 0.05f;
        gameButtonsTable.add(bugdodgeBtn).padLeft(pad).padRight(pad).center();
        gameButtonsTable.add(jbordle).padLeft(pad).padRight(pad).center();
;
        table.add(gameButtonsTable).center();


    }

    /**
     * Prepares the screen when it becomes visible, including setting the back button's position.
     */
    @Override
    public void show() {
        float buttonWidth = viewport.getWorldWidth() * 0.1f; // 10% of the viewport width
        float buttonHeight = viewport.getWorldHeight() * 0.1f; // 10% of the viewport height
        float offsetX = viewport.getWorldWidth() * 0.08f; // Add 2% of the viewport width as offset

        backButton.setPosition(buttonWidth + offsetX, viewport.getWorldHeight() - buttonHeight - buttonHeight);
        stage.addActor(backButton);
        Gdx.input.setInputProcessor(stage);
    }

    /**
     * Renders the {@code ComputerScreen} and processes user interactions.
     *
     * @param delta The time elapsed since the last frame, in seconds.
     */
    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    /**
     * Handles the resizing of the viewport to fit new window dimensions.
     *
     * @param width  The new width of the window.
     * @param height The new height of the window.
     */
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }


    /**
     * Releases resources used by the {@code ComputerScreen}.
     * This includes disposing of all textures and the {@link Stage}.
     */
    @Override
    public void dispose() {
        for (Texture texture : textures.values()) {
            texture.dispose();
        }
        stage.dispose();
    }
}
