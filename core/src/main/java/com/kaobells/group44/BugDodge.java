package com.kaobells.group44;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.audio.Sound;

import java.util.*;

/**
 * BugDodge is the main game screen for the bug-dodging game.
 * It handles rendering, input, and game logic.
 */
public class BugDodge extends ScreenAdapter {
    private final Main mainGame;
    private final CharacterClass player;
    private final SpriteBatch spriteBatch;
    private final Stage stage;
    private final Viewport viewport;
    private final ImageButton backButton;

    private HashMap<String, Texture> textures; // stores game textures
    private HashMap<String, Sound> sounds;    // stores game sounds
    private Music music;                      // background music

    private Image computerImage;              // the computer actor
    private Array<Image> bugImages;           // actors for falling bugs
    private Array<Boolean> bugBoxStatus;      // tracks filled bug boxes

    private int maxBugs = 5;                  // total number of bug boxes
    private float bugTimer = 0;               // timer to spawn bugs
    private boolean gameOver = false;         // whether the game is over

    private Rectangle computerRectangle = new Rectangle(); // bounds for computer
    private Rectangle bugRectangle = new Rectangle();      // bounds for bugs

    /**
     * Constructor for the BugDodge screen.
     *
     * @param game   The main game instance.
     * @param playa  The player character data.
     */
    public BugDodge(Main game, CharacterClass playa) {
        mainGame = game;
        player = playa;

        spriteBatch = mainGame.getSharedBatch();
        viewport = mainGame.getViewport();

        loadAssets(); // load all textures, sounds, and music

        backButton = mainGame.getBackButton(); // retrieve the shared back button
        stage = new Stage(viewport, spriteBatch); // create a new stage
    }

    /**
     * Called when this screen is displayed. Sets up the UI and input.
     */
    @Override
    public void show() {
        createUI(); // set up the user interface
        super.show();
        Gdx.input.setInputProcessor(stage); // make the stage handle inputs
    }

    /**
     * Sets up the user interface, including background, title, and game elements.
     */
    public void createUI() {
        // add the background
        Image background = new Image(new TextureRegionDrawable(textures.get("background")));
        background.setSize(viewport.getWorldWidth(), viewport.getWorldHeight());
        stage.addActor(background);

        // add the title at the top of the screen
        Image title = new Image(new TextureRegionDrawable(textures.get("title")));
        float titleWidth = viewport.getWorldWidth() * 0.5f;
        float titleHeight = titleWidth * (textures.get("title").getHeight() / (float) textures.get("title").getWidth());
        title.setSize(titleWidth, titleHeight);
        title.setPosition((viewport.getWorldWidth() - titleWidth) / 2 + 70, viewport.getWorldHeight() - titleHeight - 120);
        title.setTouchable(Touchable.disabled); // disable touch for the title
        stage.addActor(title);

        // add the computer at the bottom center
        computerImage = new Image(new TextureRegionDrawable(textures.get("computer")));
        computerImage.setSize(viewport.getWorldWidth() * 0.1f, viewport.getWorldHeight() * 0.15f);
        computerImage.setPosition((viewport.getWorldWidth() - computerImage.getWidth()) / 2, viewport.getWorldHeight() * 0.13f);
        stage.addActor(computerImage);

        // position and add the back button
        float buttonWidth = viewport.getWorldWidth() * 0.1f;
        float buttonHeight = viewport.getWorldHeight() * 0.1f;
        backButton.setPosition(buttonWidth + viewport.getWorldWidth() * 0.08f, viewport.getWorldHeight() - buttonHeight * 2);
        stage.addActor(backButton);

        // initialize bug boxes and add to the UI
        bugBoxStatus = new Array<>(maxBugs);
        for (int i = 0; i < maxBugs; i++) {
            bugBoxStatus.add(false);
        }
        bugImages = new Array<>();
    }

    /**
     * Handles input for moving the computer left and right.
     *
     * @param delta The time elapsed since the last frame.
     */
    private void handleInput(float delta) {
        if (gameOver) return; // no input allowed if the game is over

        float speed = viewport.getWorldWidth() * 0.5f; // speed of movement

        // get the current position of the computer
        float currentX = computerImage.getX();
        float currentY = computerImage.getY();

        // move left or right based on input keys
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            computerImage.setPosition(currentX + speed * delta, currentY);
        } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            computerImage.setPosition(currentX - speed * delta, currentY);
        }

        // clamp the position so it stays within bounds
        float minX = 200f;
        float maxX = viewport.getWorldWidth() - computerImage.getWidth();
        computerImage.setPosition(MathUtils.clamp(computerImage.getX(), minX, maxX), currentY);
    }

    /**
     * Loads all assets like textures, sounds, and music into HashMaps.
     */
    public void loadAssets() {
        textures = new HashMap<>();
        textures.put("background", new Texture(Gdx.files.internal("bugDodge/background.png")));
        textures.put("computer", new Texture(Gdx.files.internal("bugDodge/computer-happy.png")));
        textures.put("bug", new Texture(Gdx.files.internal("bugDodge/blue-bug.png")));
        textures.put("title", new Texture(Gdx.files.internal("bugDodge/title.png")));
        textures.put("bugBoxEmpty", new Texture(Gdx.files.internal("bugDodge/bug-box-empty.png")));
        textures.put("bugBoxFilled", new Texture(Gdx.files.internal("bugDodge/bug-box-filled.png")));
        textures.put("win", new Texture(Gdx.files.internal("bugDodge/bug-win-txtbox.png")));

        sounds = new HashMap<>();
        sounds.put("hit", Gdx.audio.newSound(Gdx.files.internal("bugDodge/error.mp3")));
        sounds.put("dodge", Gdx.audio.newSound(Gdx.files.internal("bugDodge/switch-click.mp3")));

        music = Gdx.audio.newMusic(Gdx.files.internal("bugDodge/music.mp3"));
        music.setLooping(true);
        music.setVolume(0.5f);
    }

    /**
     * Main render loop for the screen. Handles input, updates game logic, and draws actors.
     *
     * @param delta Time elapsed since the last frame.
     */
    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);

        if (!gameOver) {
            handleInput(delta); // check for user input
            updateGameLogic(delta); // update game logic
        }

        stage.act(delta); // update stage actors
        stage.draw();     // draw all actors
    }

    /**
     * Updates the game state, such as moving bugs and detecting collisions.
     *
     * @param delta Time elapsed since the last frame.
     */
    private void updateGameLogic(float delta) {
        // update the computer's collision bounds
        computerRectangle.set(computerImage.getX(), computerImage.getY(), computerImage.getWidth(), computerImage.getHeight());

        bugTimer += delta;
        if (bugTimer > 1f) { // spawn bugs every 1 second
            bugTimer = 0;
            createBug();
        }

        for (int i = bugImages.size - 1; i >= 0; i--) {
            Image bug = bugImages.get(i);
            bug.moveBy(0, -viewport.getWorldHeight() * 0.3f * delta);

            bugRectangle.set(bug.getX(), bug.getY(), bug.getWidth(), bug.getHeight());

            if (bug.getY() < 150f) { // bug falls off-screen
                bugImages.removeIndex(i);
                stage.getActors().removeValue(bug, true);
                fillBugBox();
                sounds.get("dodge").play();
            } else if (bugRectangle.overlaps(computerRectangle)) { // collision with computer
                bugImages.removeIndex(i);
                stage.getActors().removeValue(bug, true);
                removeBugBox();
                sounds.get("hit").play();
            }
        }

        if (!bugBoxStatus.contains(false, false)) { // all bug boxes are filled
            gameOver = true;
            loadEndGame(); // display end game screen
        }
    }

    /**
     * Creates a new bug and adds it to the stage.
     */
    private void createBug() {
        Image bug = new Image(new TextureRegionDrawable(textures.get("bug")));
        bug.setSize(viewport.getWorldWidth() * 0.05f, viewport.getWorldWidth() * 0.05f);

        float bugX = MathUtils.random(300f, viewport.getWorldWidth() - bug.getWidth() - 300f);
        float bugY = viewport.getWorldHeight() - bug.getHeight() - 250f;
        bug.setPosition(bugX, bugY);

        bugImages.add(bug);
        stage.addActor(bug);
    }

    /**
     * Handles bug box filling logic when a bug is successfully dodged.
     */
    private void fillBugBox() {
        for (int i = 0; i < maxBugs; i++) {
            if (!bugBoxStatus.get(i)) {
                bugBoxStatus.set(i, true);
                return;
            }
        }
    }

    /**
     * Handles bug box removal logic when a bug hits the computer.
     */
    private void removeBugBox() {
        for (int i = maxBugs - 1; i >= 0; i--) {
            if (bugBoxStatus.get(i)) {
                bugBoxStatus.set(i, false);
                return;
            }
        }
    }

    /**
     * Displays the end game screen with a win button.
     */
    public void loadEndGame() {
        ImageButton endGameButton = mainGame.createImageButton(textures.get("win"));

        float buttonWidth = viewport.getWorldWidth() * 0.6f;
        float buttonHeight = buttonWidth * (textures.get("win").getHeight() / (float) textures.get("win").getWidth());
        endGameButton.setSize(buttonWidth, buttonHeight);

        float winX = (viewport.getWorldWidth() - buttonWidth) / 2;
        float winY = (viewport.getWorldHeight() - buttonHeight) / 2;
        endGameButton.setPosition(winX, winY);

        stage.addActor(endGameButton);

        endGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                player.gainItem((int) (Math.random() * 6));
                mainGame.popScreen();
            }
        });
    }

    /**
     * Updates the viewport size when the window is resized.
     *
     * @param width  The new width of the window.
     * @param height The new height of the window.
     */
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    /**
     * Disposes of assets to free memory.
     */
    @Override
    public void dispose() {
        for (Texture texture : textures.values()) {
            texture.dispose();
        }
        stage.dispose();
        music.dispose();
    }
}
