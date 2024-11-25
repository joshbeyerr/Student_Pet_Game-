package com.kaobells.group44;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;


public class StoryScreen extends ScreenAdapter {

    private final Main mainGame;

    // only need to make this a class variable because of the back button logic custom to this class
    private final Screen previousScreenVar;

    private final SpriteBatch spriteBatch;
    private final Stage stage;
    private final Viewport viewport;

    // two fonts because this page has two different font colors/sizes needed
    private final BitmapFont font;
    private final BitmapFont textFont;

    private final String[] storyTexts = new String[4];
    private int currentTextIndex = 0;

    private ImageButton backButton;

    InputMultiplexer multiplexer;


    public StoryScreen(Main game, Screen previousScreen) {
        mainGame = game;
        previousScreenVar = previousScreen;

        // getting fonts, title font is set in resource manager
        font = mainGame.resourceManager.getTitleFont();

        // text font details (e.g color and size) will be determined in this class, thus this is loading a fresh font object
        textFont = mainGame.resourceManager.getFont(true);

        spriteBatch = mainGame.getSharedBatch();
        viewport = mainGame.getViewport();

        stage = new Stage(viewport, spriteBatch);

        // no "show" method again just due to the nature of this class, calling stuff from the constructor
        setStage();
        createUI();
    }

    public void createUI(){

        Gdx.app.log("heloo", "hello");

        Gdx.app.log("CreateUI", Integer.toString(currentTextIndex));

        // due to this screen displaying multiple different screens, must make sure stage is clear before starting
        stage.clear();

        createBackButton();
        stage.addActor(backButton);

        LoadText();
    }

    private void createBackButton() {
        if (currentTextIndex > 0){
            backButton.clearListeners(); // Clear any previous listeners to avoid stacking
            backButton.addListener(new ClickListener() {
                @Override
                public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                    if (currentTextIndex > 0) {
                        // Go back to the previous story text
                        currentTextIndex--;
                        createUI();
                    }
                }
            });
        }
        else{
            backButton = mainGame.getBackButton(mainGame, previousScreenVar);
        }
    }


    // method to adjust story text to fix perfectly on the screen, put new lines in the right place
    public String stringFormatter(String text) {
        StringBuilder newString = new StringBuilder();
        int charCount = 0;

        for (int i = 0; i < text.length(); i++) {
            // Add characters to the new string
            newString.append(text.charAt(i));
            charCount++;

            // Check if we need to break the line
            if (charCount >= 35) {
                // Find the last space within the last 42 characters to break nicely
                int lastSpaceIndex = newString.lastIndexOf(" ", newString.length());
                if (lastSpaceIndex != -1) {
                    newString.setCharAt(lastSpaceIndex, '\n'); // Replace the space with a line break
                    newString.insert(lastSpaceIndex + 1, '\n'); // Add the second '\n' after the line break
                    charCount = newString.length() - lastSpaceIndex - 2; // Reset char count after the last line break
                } else {
                    newString.append("\n\n"); // No space found; force a break
                    charCount = 0; // Reset char count
                }
            }
        }

        return newString.toString();
    }

    public void LoadText(){

        if (storyTexts[0] == null){
            storyTexts[0] = stringFormatter("Welcome. You have been tasked with taking care of a Computer Science student at Western University. They can only get through their tough semester with your love and care.");
            storyTexts[1] = stringFormatter("You can take care of your Student by feeding them, playing with them, having them exercise, taking them to the doctor, making sure they sleep, and giving them gifts.");
            storyTexts[2] = stringFormatter("But be careful! You have to keep the health, happiness, energy and stress level of your Student in check, or there may be consequences!");
            storyTexts[3] = stringFormatter("They are waiting for you, so we won’t keep you any longer.  Good luck! And take good care.");
        }

        textFont.setColor(Color.WHITE);
        float scaleFactor = viewport.getWorldHeight() * 0.0011f; // Scale as a percentage
        textFont.getData().setScale(scaleFactor);

    }

    public void calculateTextRender(){
        // Draw the story text

        GlyphLayout layout = new GlyphLayout();
        layout.setText(textFont, storyTexts[currentTextIndex]);

        float textWidth = layout.width;
        float textHeight = layout.height;

        // Calculate centered positions
        float textX = (viewport.getWorldWidth() - textWidth) / 2; // Center horizontally
        float textY = (viewport.getWorldHeight() + textHeight) / 2; // Center vertically

        // Draw the story text
        textFont.draw(spriteBatch, storyTexts[currentTextIndex], textX, textY);
    }

    @Override
    public void render(float delta) {
        // Clear the screen
        ScreenUtils.clear(0, 0, 0, 1);

        spriteBatch.begin();
        // Draw the current background

        mainGame.drawBackground(spriteBatch, mainGame.resourceManager.get("storyBackground"), font, "New Game");

        calculateTextRender();

        // No extra drawing logic for SPRITE_CHOOSE here; handled by stage
        spriteBatch.end();

        // Render the stage (actors like buttons, character sprites, etc.)
        stage.act(delta);
        stage.draw();
    }

    private void handleKeyPress() {
        if (currentTextIndex == 3) {
            mainGame.setScreenNoDispose(new characterSelection(mainGame, StoryScreen.this));
        } else if (0 <= currentTextIndex && currentTextIndex < 3) {
            currentTextIndex++;
            createUI();
        }

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {

        stage.dispose();
    }

    public void setStage(){
        if (multiplexer == null){
            // Create an InputAdapter for key press handling
            InputAdapter inputAdapter = new InputAdapter() {
                @Override
                public boolean keyDown(int keycode) {
                    handleKeyPress();
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
