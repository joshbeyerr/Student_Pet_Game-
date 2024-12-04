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


public class BugDodge extends ScreenAdapter {
    private final Main mainGame;
    private final CharacterClass player;
    private final SpriteBatch spriteBatch;
    private final Stage stage;
    private final Viewport viewport;
    Container<Actor> container;

    private final ImageButton backButton;
    private HashMap<String, Texture> textures;
    private HashMap<String, Sound> sounds;
    private Music music;

    private Image computerImage; // Replacing Sprite with Scene2D Image
    private Array<Image> bugImages; // Scene2D Images for bugs
    private Array<Boolean> bugBoxStatus;
    private int maxBugs = 5;
    private float bugTimer = 0;
    private boolean gameOver = false;
    private Rectangle computerRectangle = new Rectangle();
    private Rectangle bugRectangle = new Rectangle();


    public BugDodge(Main game,CharacterClass playa) {
        mainGame = game;
        player = playa;

        spriteBatch = mainGame.getSharedBatch();
        viewport = mainGame.getViewport();


        loadAssets();

        backButton = mainGame.getBackButton();
        stage = new Stage(viewport, spriteBatch);
    }

    @Override
    public void show() {
        createUI();
        super.show();
        Gdx.input.setInputProcessor(stage);
    }


    public void createUI() {
        // Background
        Image background = new Image(new TextureRegionDrawable(textures.get("background")));
        background.setSize(viewport.getWorldWidth(), viewport.getWorldHeight());
        stage.addActor(background);

        // Computer
        computerImage = new Image(new TextureRegionDrawable(textures.get("computer")));
        computerImage.setSize(viewport.getWorldWidth() * 0.1f, viewport.getWorldHeight() * 0.15f);
        computerImage.setPosition(
            (viewport.getWorldWidth() - computerImage.getWidth()) / 2,
            viewport.getWorldHeight() * 0.1f
        );
        stage.addActor(computerImage);

        // Back button
        float buttonWidth = viewport.getWorldWidth() * 0.1f;
        float buttonHeight = viewport.getWorldHeight() * 0.1f;
        float offsetX = viewport.getWorldWidth() * 0.08f;
        backButton.setPosition(buttonWidth + offsetX, viewport.getWorldHeight() - buttonHeight - buttonHeight);
        stage.addActor(backButton);

        // Bug boxes
        bugBoxStatus = new Array<>(maxBugs);
        for (int i = 0; i < maxBugs; i++) {
            bugBoxStatus.add(false);
        }
        bugImages = new Array<>();
    }


    private void handleInput(float delta) {
        if (gameOver) return;

        // Find the computer actor
        Image computerSprite = (Image) stage.getActors().get(1); // Assuming computer is the second actor added

        float speed = viewport.getWorldWidth() * 0.5f; // Speed proportional to world width

        // Get the current position of the computerSprite
        float currentX = computerSprite.getX();
        float currentY = computerSprite.getY();

        // Update position based on input
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            computerSprite.setPosition(currentX + speed * delta, currentY); // Move right
        } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            computerSprite.setPosition(currentX - speed * delta, currentY); // Move left
        }

        // Clamp position to keep it within the screen bounds
        float minX = 0; // Minimum X position
        float maxX = viewport.getWorldWidth() - computerSprite.getWidth(); // Maximum X position
        computerSprite.setPosition(MathUtils.clamp(computerSprite.getX(), minX, maxX), currentY);
    }


    public void loadAssets() {
        // Load textures into a HashMap
        textures = new HashMap<>();
        textures.put("background", new Texture(Gdx.files.internal("bugDodge/background.png")));
        textures.put("computer", new Texture(Gdx.files.internal("bugDodge/computer-happy.png")));
        textures.put("bug", new Texture(Gdx.files.internal("bugDodge/blue-bug.png")));
        textures.put("title", new Texture(Gdx.files.internal("bugDodge/title.png")));
        textures.put("bugBoxEmpty", new Texture(Gdx.files.internal("bugDodge/bug-box-empty.png")));
        textures.put("bugBoxFilled", new Texture(Gdx.files.internal("bugDodge/bug-box-filled.png")));
        textures.put("win", new Texture(Gdx.files.internal("bugDodge/bug-win-txtbox.png")));

        // Load sounds into a HashMap
        sounds = new HashMap<>();
        sounds.put("hit", Gdx.audio.newSound(Gdx.files.internal("bugDodge/error.mp3")));
        sounds.put("dodge", Gdx.audio.newSound(Gdx.files.internal("bugDodge/switch-click.mp3")));

        // Load music
        music = Gdx.audio.newMusic(Gdx.files.internal("bugDodge/music.mp3"));
        music.setLooping(true); // Ensure the music loops
        music.setVolume(0.5f);  // Adjust the volume
        // music.play();           // Uncomment to start playing the music immediately

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);

        if (!gameOver) {
            handleInput(delta);
            updateGameLogic(delta);
        }

        stage.act(delta);
        stage.draw();
    }

    private void updateGameLogic(float delta) {
        // Update the computer's bounding rectangle
        computerRectangle.set(
            computerImage.getX(),
            computerImage.getY(),
            computerImage.getWidth(),
            computerImage.getHeight()
        );

        bugTimer += delta;
        if (bugTimer > 1f) {
            bugTimer = 0;
            createBug();
        }

        for (int i = bugImages.size - 1; i >= 0; i--) {
            Image bug = bugImages.get(i);
            bug.moveBy(0, -viewport.getWorldHeight() * 0.3f * delta);

            // Update the bug's bounding rectangle
            bugRectangle.set(
                bug.getX(),
                bug.getY(),
                bug.getWidth(),
                bug.getHeight()
            );

            // Check if the bug goes off-screen
            if (bug.getY() < 0) {
                bugImages.removeIndex(i);
                stage.getActors().removeValue(bug, true);
                fillBugBox();
                sounds.get("dodge").play();
            } else if (bugRectangle.overlaps(computerRectangle)) {
                // Check for collision with the computer
                bugImages.removeIndex(i);
                stage.getActors().removeValue(bug, true);
                removeBugBox();
                sounds.get("hit").play();
            }
        }

        if (!bugBoxStatus.contains(false, false)) {
            gameOver = true;
            loadEndGame();
        }
    }


    private void createBug() {
        Image bug = new Image(new TextureRegionDrawable(textures.get("bug")));
        bug.setSize(viewport.getWorldWidth() * 0.05f, viewport.getWorldWidth() * 0.05f);

        float bugX = MathUtils.random(300f, viewport.getWorldWidth() - bug.getWidth() - 300f);
        float bugY = viewport.getWorldHeight() - bug.getHeight() - 250f;
        bug.setPosition(bugX, bugY);

        bugImages.add(bug);
        stage.addActor(bug);
    }

    private void fillBugBox() {
        for (int i = 0; i < maxBugs; i++) {
            if (!bugBoxStatus.get(i)) {
                bugBoxStatus.set(i, true);
                return;
            }
        }
    }

    private void removeBugBox() {
        for (int i = maxBugs - 1; i >= 0; i--) {
            if (bugBoxStatus.get(i)) {
                bugBoxStatus.set(i, false);
                return;
            }
        }
    }

    public ImageButton loadEndGame() {

        ImageButton endGameButton = null;
        endGameButton = mainGame.createImageButton(textures.get("win"));
        stage.addActor(endGameButton);

        if (endGameButton != null) {
            endGameButton.setTouchable(Touchable.enabled); // Ensure the button is clickable

            endGameButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    System.out.println("End game button clicked!");

                    // Example: Return to main menu
                    mainGame.popScreen();
                }
            });
        }


        return endGameButton;
    }


    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        for (Texture texture : textures.values()) {
            texture.dispose();
        }
        stage.dispose();
        music.dispose();
    }
}

