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

    /** Reference to the main game instance. */
    private final Main mainGame;

    /** Player's character data. */
    private final CharacterClass player;

    /** Shared SpriteBatch for rendering. */
    private final SpriteBatch spriteBatch;

    /** Stage for handling UI and actors. */
    private final Stage stage;

    /** Viewport for screen scaling. */
    private final Viewport viewport;

    /** Shared back button for navigation. */
    private final ImageButton backButton;

    /** Stores game textures. */
    private HashMap<String, Texture> textures;

    /** Stores game sounds. */
    private HashMap<String, Sound> sounds;

    /** Background music. */
    private Music music;

    /** Actor for the computer image. */
    private Image computerImage;

    /** Actors for falling bugs. */
    private Array<Image> bugImages;

    /** Tracks filled bug boxes. */
    private Array<Boolean> bugBoxStatus;

    /** Maximum number of bug boxes. */
    private int maxBugs = 5;

    /** Timer for spawning bugs. */
    private float bugTimer = 0;

    /** Whether the game is over. */
    private boolean gameOver = false;

    /** Bounding rectangle for the computer. */
    private final Rectangle computerRectangle = new Rectangle();

    /** Bounding rectangle for bugs. */
    private final Rectangle bugRectangle = new Rectangle();

    /**
     * Constructor for the BugDodge screen.
     *
     * @param game   The main game instance.
     * @param player  The player character data.
     */
    public BugDodge(Main game, CharacterClass player) {
        // initialize game and player references
        this.mainGame = game;
        this.player = player;

        // get shared resources
        this.spriteBatch = mainGame.getSharedBatch();
        this.viewport = mainGame.getViewport();

        // load assets for the game
        loadAssets();

        // initialize stage and back button
        this.backButton = mainGame.getBackButton();
        this.stage = new Stage(viewport, spriteBatch);
    }

    /**
     * Called when this screen is displayed. Sets up the UI and input.
     */
    @Override
    public void show() {
        // create the user interface
        createUI();

        // set the stage as the input processor
        Gdx.input.setInputProcessor(stage);

        super.show();
    }

    /**
     * Sets up the user interface, including background, title, and game elements.
     */
    public void createUI() {
        // add the background image to the stage
        Image background = new Image(new TextureRegionDrawable(textures.get("background")));
        background.setSize(viewport.getWorldWidth(), viewport.getWorldHeight());
        stage.addActor(background);

        // add the title at the top center
        Image title = new Image(new TextureRegionDrawable(textures.get("title")));
        float titleWidth = viewport.getWorldWidth() * 0.5f;
        float titleHeight = titleWidth * (textures.get("title").getHeight() / (float) textures.get("title").getWidth());
        title.setSize(titleWidth, titleHeight);
        title.setPosition(
            (viewport.getWorldWidth() - titleWidth) / 2 + 70,
            viewport.getWorldHeight() - titleHeight - 120
        );
        title.setTouchable(Touchable.disabled); // disable interactions
        stage.addActor(title);

        // add the computer image at the bottom center
        computerImage = new Image(new TextureRegionDrawable(textures.get("computer")));
        computerImage.setSize(viewport.getWorldWidth() * 0.1f, viewport.getWorldHeight() * 0.15f);
        computerImage.setPosition(
            (viewport.getWorldWidth() - computerImage.getWidth()) / 2,
            viewport.getWorldHeight() * 0.13f
        );
        stage.addActor(computerImage);

        // position and add the back button
        float buttonWidth = viewport.getWorldWidth() * 0.1f;
        float buttonHeight = viewport.getWorldHeight() * 0.1f;
        backButton.setPosition(
            buttonWidth + viewport.getWorldWidth() * 0.08f,
            viewport.getWorldHeight() - buttonHeight * 2
        );
        stage.addActor(backButton);

        // initialize bug boxes
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
        // skip input handling if the game is over
        if (gameOver) return;

        // calculate movement speed
        float speed = viewport.getWorldWidth() * 0.5f;

        // get current position of the computer
        float currentX = computerImage.getX();
        float currentY = computerImage.getY();

        // move the computer based on key input
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            computerImage.setPosition(currentX + speed * delta, currentY);
        } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            computerImage.setPosition(currentX - speed * delta, currentY);
        }

        // ensure the computer stays within screen bounds
        float minX = 200f;
        float maxX = viewport.getWorldWidth() - computerImage.getWidth();
        computerImage.setPosition(MathUtils.clamp(computerImage.getX(), minX, maxX), currentY);
    }

    /**
     * Loads all assets like textures, sounds, and music into HashMaps.
     */
    public void loadAssets() {
        // load textures for game visuals
        textures = new HashMap<>();
        textures.put("background", new Texture(Gdx.files.internal("bugDodge/background.png")));
        textures.put("computer", new Texture(Gdx.files.internal("bugDodge/computer-happy.png")));
        textures.put("bug", new Texture(Gdx.files.internal("bugDodge/blue-bug.png")));
        textures.put("title", new Texture(Gdx.files.internal("bugDodge/title.png")));
        textures.put("bugBoxEmpty", new Texture(Gdx.files.internal("bugDodge/bug-box-empty.png")));
        textures.put("bugBoxFilled", new Texture(Gdx.files.internal("bugDodge/bug-box-filled.png")));
        textures.put("win", new Texture(Gdx.files.internal("bugDodge/bug-win-txtbox.png")));

        // load sound effects
        sounds = new HashMap<>();
        sounds.put("hit", Gdx.audio.newSound(Gdx.files.internal("bugDodge/error.mp3")));
        sounds.put("dodge", Gdx.audio.newSound(Gdx.files.internal("bugDodge/switch-click.mp3")));

        // load background music
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
        // clear the screen
        ScreenUtils.clear(Color.BLACK);

        // handle game logic if the game is not over
        if (!gameOver) {
            handleInput(delta);
            updateGameLogic(delta);
        }

        // update and draw all stage actors
        stage.act(delta);
        stage.draw();
    }

    /**
     * Updates the game state, such as moving bugs and detecting collisions.
     *
     * @param delta Time elapsed since the last frame.
     */
    private void updateGameLogic(float delta) {
        // update collision bounds for the computer
        computerRectangle.set(
            computerImage.getX(),
            computerImage.getY(),
            computerImage.getWidth(),
            computerImage.getHeight()
        );

        // increment the bug timer
        bugTimer += delta;

        // spawn a new bug if the timer exceeds 1 second
        if (bugTimer > 1f) {
            bugTimer = 0;
            createBug();
        }

        // iterate through all bugs
        for (int i = bugImages.size - 1; i >= 0; i--) {
            Image bug = bugImages.get(i);
            bug.moveBy(0, -viewport.getWorldHeight() * 0.3f * delta);

            // update bug collision bounds
            bugRectangle.set(
                bug.getX(),
                bug.getY(),
                bug.getWidth(),
                bug.getHeight()
            );

            // check if the bug falls off-screen
            if (bug.getY() < 150f) {
                bugImages.removeIndex(i);
                stage.getActors().removeValue(bug, true);
                fillBugBox();
                sounds.get("dodge").play();
            } else if (bugRectangle.overlaps(computerRectangle)) { // check for collision with computer
                bugImages.removeIndex(i);
                stage.getActors().removeValue(bug, true);
                removeBugBox();
                sounds.get("hit").play();
            }
        }

        // end the game if all bug boxes are filled
        if (!bugBoxStatus.contains(false, false)) {
            gameOver = true;
            loadEndGame();
        }
    }

    /**
     * Creates a new bug and adds it to the stage.
     */
    private void createBug() {
        // create a new bug image
        Image bug = new Image(new TextureRegionDrawable(textures.get("bug")));
        bug.setSize(viewport.getWorldWidth() * 0.05f, viewport.getWorldWidth() * 0.05f);

        // position the bug at a random horizontal location
        float bugX = MathUtils.random(300f, viewport.getWorldWidth() - bug.getWidth() - 300f);
        float bugY = viewport.getWorldHeight() - bug.getHeight() - 250f;
        bug.setPosition(bugX, bugY);

        // add the bug to the list and stage
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
        // create the end game button
        ImageButton endGameButton = mainGame.createImageButton(textures.get("win"));

        // set size and position of the button
        float buttonWidth = viewport.getWorldWidth() * 0.6f;
        float buttonHeight = buttonWidth * (textures.get("win").getHeight() / (float) textures.get("win").getWidth());
        endGameButton.setSize(buttonWidth, buttonHeight);
        endGameButton.setPosition(
            (viewport.getWorldWidth() - buttonWidth) / 2,
            (viewport.getWorldHeight() - buttonHeight) / 2
        );

        // add the button to the stage
        stage.addActor(endGameButton);

        // add a click listener to the button
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
        // dispose of all textures
        for (Texture texture : textures.values()) {
            texture.dispose();
        }

        // dispose of stage and music
        stage.dispose();
        music.dispose();
    }
}
