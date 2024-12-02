package com.kaobells.group44;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;

public class BugDodger extends ScreenAdapter {
    private final Main mainGame;

    private final Stage stage;
    private final Viewport viewport;

    private Texture backgroundTexture, computerTexture, bugTexture, bugBoxEmptyTexture, bugBoxFilledTexture, winTexture;
    private Sound hitSound, dodgeSound;

    private Image computerImage;
    private final Array<Image> bugImages = new Array<>();
    private final Array<Boolean> bugBoxStatus = new Array<>();
    private final int maxBugs = 5;
    private float bugTimer = 0;
    private boolean gameOver = false;

    private final Rectangle computerRectangle = new Rectangle();
    private final Rectangle bugRectangle = new Rectangle();

    public BugDodger(Main mainGame, CharacterClass playa) {
        this.mainGame = mainGame;
        this.viewport = mainGame.getViewport();
        this.stage = new Stage(viewport, mainGame.getSharedBatch());
    }

    @Override
    public void show() {
        loadAssets();
        setupUI();
        initializeGameLogic();

        Gdx.input.setInputProcessor(stage);
    }

    private void loadAssets() {
        backgroundTexture = new Texture(Gdx.files.internal("bugDodge/background.png"));
        computerTexture = new Texture(Gdx.files.internal("bugDodge/computer-happy.png"));
        bugTexture = new Texture(Gdx.files.internal("bugDodge/blue-bug.png"));
        bugBoxEmptyTexture = new Texture(Gdx.files.internal("bugDodge/bug-box-empty.png"));
        bugBoxFilledTexture = new Texture(Gdx.files.internal("bugDodge/bug-box-filled.png"));
        winTexture = new Texture(Gdx.files.internal("bugDodge/bug-win-txtbox.png"));

        hitSound = Gdx.audio.newSound(Gdx.files.internal("bugDodge/error.mp3"));
        dodgeSound = Gdx.audio.newSound(Gdx.files.internal("bugDodge/switch-click.mp3"));
    }

    private void setupUI() {
        Image background = new Image(backgroundTexture);
        background.setSize(viewport.getWorldWidth(), viewport.getWorldHeight());
        stage.addActor(background);

        Table bugBoxTable = new Table();
        bugBoxTable.top().right().pad(10);
        bugBoxTable.setFillParent(true);

        for (int i = 0; i < maxBugs; i++) {
            bugBoxStatus.add(false);
            Image bugBox = new Image(bugBoxEmptyTexture);
            bugBoxTable.add(bugBox).size(0.2f, 0.2f).pad(5);
        }

        stage.addActor(bugBoxTable);
    }

    private void initializeGameLogic() {
        computerImage = new Image(computerTexture);
        computerImage.setSize(1, 1);
        computerImage.setPosition(
            (viewport.getWorldWidth() - computerImage.getWidth()) / 2,
            0.6f
        );
        stage.addActor(computerImage);
    }

    @Override
    public void render(float delta) {
        if (!gameOver) {
            handleInput(delta);
            updateLogic(delta);
        }

        drawGame();
    }

    private void handleInput(float delta) {
        float speed = 4f;

        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.D)) {
            computerImage.moveBy(speed * delta, 0);
        } else if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.A)) {
            computerImage.moveBy(-speed * delta, 0);
        }

        float minX = 1.5f;
        float maxX = viewport.getWorldWidth() - computerImage.getWidth() - 1.5f;
        float clampedX = MathUtils.clamp(computerImage.getX(), minX, maxX);
        computerImage.setX(clampedX);
    }

    private void updateLogic(float delta) {
        computerRectangle.set(computerImage.getX(), computerImage.getY(), computerImage.getWidth(), computerImage.getHeight());

        for (int i = bugImages.size - 1; i >= 0; i--) {
            Image bug = bugImages.get(i);
            bug.moveBy(0, -4f * delta);
            bugRectangle.set(bug.getX(), bug.getY(), bug.getWidth(), bug.getHeight());

            if (bug.getY() < 0.5f) {
                bugImages.removeIndex(i);
                bug.remove();
                fillBugBox();
                dodgeSound.play();
            } else if (computerRectangle.overlaps(bugRectangle)) {
                bugImages.removeIndex(i);
                bug.remove();
                removeBugBox();
                hitSound.play();
            }
        }

        bugTimer += delta;
        if (bugTimer > 1f) {
            bugTimer = 0;
            spawnBug();
        }
    }

    private void drawGame() {
        ScreenUtils.clear(0, 0, 0, 1);
        stage.act();
        stage.draw();
    }

    private void spawnBug() {
        Image bug = new Image(bugTexture);
        bug.setSize(0.4f, 0.4f);
        bug.setPosition(
            MathUtils.random(1.5f, viewport.getWorldWidth() - 1.5f),
            viewport.getWorldHeight() - 1.5f
        );
        bugImages.add(bug);
        stage.addActor(bug);
    }

    private void fillBugBox() {
        for (int i = maxBugs - 1; i >= 0; i--) {
            if (!bugBoxStatus.get(i)) {
                bugBoxStatus.set(i, true);
                break;
            }
        }

        if (!bugBoxStatus.contains(false, false)) {
            gameOver = true;
        }
    }

    private void removeBugBox() {
        for (int i = 0; i < maxBugs; i++) {
            if (bugBoxStatus.get(i)) {
                bugBoxStatus.set(i, false);
                break;
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        backgroundTexture.dispose();
        computerTexture.dispose();
        bugTexture.dispose();
        bugBoxEmptyTexture.dispose();
        bugBoxFilledTexture.dispose();
        winTexture.dispose();
        hitSound.dispose();
        dodgeSound.dispose();
        stage.dispose();
    }
}
