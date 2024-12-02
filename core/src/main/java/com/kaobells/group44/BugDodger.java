package com.kaobells.group44;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;

public class BugDodger extends ScreenAdapter {
    private final Main mainGame;

    private final SpriteBatch spriteBatch;
    private final Viewport viewport;

    private Texture backgroundTexture, computerTexture, bugTexture, titleTexture, bugBoxEmptyTexture, bugBoxFilledTexture, winTexture;

    private Rectangle computerRectangle;
    private Array<Sprite> bugSprites;
    private Array<Boolean> bugBoxStatus;

    private boolean gameOver = false;
    private float bugTimer;
    private int maxBugs = 5;

    public BugDodger(Main game) {
        this.mainGame = game;

        this.spriteBatch = mainGame.getSharedBatch();
        this.viewport = mainGame.getViewport();

        initialize();
    }

    private void initialize() {
        backgroundTexture = new Texture("background.png");
        computerTexture = new Texture("computer-happy.png");
        bugTexture = new Texture("blue-bug.png");
        titleTexture = new Texture("title.png");
        bugBoxEmptyTexture = new Texture("bug-box-empty.png");
        bugBoxFilledTexture = new Texture("bug-box-filled.png");
        winTexture = new Texture("bug-win-txtbox.png");

        bugSprites = new Array<>();
        bugBoxStatus = new Array<>(maxBugs);
        for (int i = 0; i < maxBugs; i++) {
            bugBoxStatus.add(false); // Initialize all bug boxes as empty
        }

        computerRectangle = new Rectangle(4, 0.6f, 1, 1); // Initial computer size and position
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1); // Clear with black color

        // Update game logic
        if (!gameOver) {
            updateLogic(delta);
        }

        // Draw game elements
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();

        spriteBatch.draw(backgroundTexture, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());

        if (gameOver) {
            // Draw win box in the center of the screen
            float winWidth = 5f;
            float winHeight = 1.5f;
            float winX = (viewport.getWorldWidth() - winWidth) / 2;
            float winY = (viewport.getWorldHeight() - winHeight) / 2;
            spriteBatch.draw(winTexture, winX, winY, winWidth, winHeight);
        } else {
            // Draw title
            spriteBatch.draw(titleTexture, 1.5f, viewport.getWorldHeight() - 2f, 3f, 1f);

            // Draw bugs
            for (Sprite bug : bugSprites) {
                bug.draw(spriteBatch);
            }

            // Draw computer
            spriteBatch.draw(computerTexture, computerRectangle.x, computerRectangle.y, computerRectangle.width, computerRectangle.height);
        }

        spriteBatch.end();
    }

    private void updateLogic(float delta) {
        bugTimer += delta;
        if (bugTimer > 1f) {
            bugTimer = 0;
            spawnBug();
        }

        // Move and check bugs
        for (int i = bugSprites.size - 1; i >= 0; i--) {
            Sprite bug = bugSprites.get(i);
            bug.translateY(-4f * delta);

            if (bug.getY() < 0.5f) {
                bugSprites.removeIndex(i); // Remove bug if it falls below screen
            } else if (computerRectangle.overlaps(bug.getBoundingRectangle())) {
                bugSprites.removeIndex(i); // Remove bug if it hits the computer
            }
        }
    }

    private void spawnBug() {
        Sprite bug = new Sprite(bugTexture);
        bug.setSize(0.4f, 0.4f);
        bug.setX(MathUtils.random(1.5f, viewport.getWorldWidth() - 1.5f));
        bug.setY(viewport.getWorldHeight() - 1.5f);
        bugSprites.add(bug);
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
        titleTexture.dispose();
        bugBoxEmptyTexture.dispose();
        bugBoxFilledTexture.dispose();
        winTexture.dispose();
    }
}
