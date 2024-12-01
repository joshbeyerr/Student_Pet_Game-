package com.kaobells.group44;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Stack;

public class InstructionsScreens extends ScreenAdapter {
    private final Main mainGame;

    private final SpriteBatch spriteBatch;
    private final Viewport viewport;

    private final Stack<Texture> instructionTextures; // Stack for instruction images
    private Texture currentTexture; // Currently displayed texture

    private InputMultiplexer multiplexer;

    public InstructionsScreens(Main game) {
        this.mainGame = game;

        this.spriteBatch = mainGame.getSharedBatch();
        this.viewport = mainGame.getViewport();

        // Initialize the stack and load instruction textures
        this.instructionTextures = new Stack<>();
        loadInstructionTextures();

        // Set the first texture to display
        if (!instructionTextures.isEmpty()) {
            currentTexture = instructionTextures.pop();
        }
    }

    private void loadInstructionTextures() {
        // Push instruction textures in reverse order (last screen first)
        instructionTextures.push(new Texture(Gdx.files.internal("instructionsScreens/instructions-screen4.png")));
        instructionTextures.push(new Texture(Gdx.files.internal("instructionsScreens/instructions-screen3.png")));
        instructionTextures.push(new Texture(Gdx.files.internal("instructionsScreens/instructions-screen2.png")));
        instructionTextures.push(new Texture(Gdx.files.internal("instructionsScreens/instructions-screen1.png")));
        instructionTextures.push(new Texture(Gdx.files.internal("instructionsScreens/instructions-screen0.png")));
    }

    private void handleKeyPress() {
        if (currentTexture != null) {
            currentTexture.dispose();
        }

        if (!instructionTextures.isEmpty()) {
            currentTexture = instructionTextures.pop(); // Load the next texture
        } else {
            Gdx.app.log("InstructionsScreens", "Finished instructions. Returning to previous screen.");
            mainGame.popScreen(); // Exit when all instructions are viewed
        }
    }

    @Override
    public void show() {
        super.show();

        if (multiplexer == null) {
            // Create an InputAdapter for key press handling
            InputAdapter inputAdapter = new InputAdapter() {
                @Override
                public boolean keyDown(int keycode) {
                    handleKeyPress(); // Advance to the next instruction on any key press
                    return true;
                }
            };

            // Set up the multiplexer
            multiplexer = new InputMultiplexer();
            multiplexer.addProcessor(inputAdapter); // Handle key presses
        }

        Gdx.input.setInputProcessor(multiplexer); // Set the multiplexer as the input processor
    }

    @Override
    public void render(float delta) {
        // Clear the screen
        ScreenUtils.clear(0, 0, 0, 1);

        spriteBatch.begin();

        if (currentTexture != null) {
            // Draw the current instruction texture to fill the screen
            spriteBatch.draw(currentTexture, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
        }

        spriteBatch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        // Dispose all remaining textures
        while (!instructionTextures.isEmpty()) {
            instructionTextures.pop().dispose();
        }

        // Dispose of the currently displayed texture
        if (currentTexture != null) {
            currentTexture.dispose();
        }
    }
}
