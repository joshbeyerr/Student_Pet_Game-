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

/**
 * The {@code StoryScreen} class represents the story introduction screen of the game.
 * This screen displays narrative text about the game's objectives and mechanics.
 * It supports navigation through different pages of the story.
 *
 * <p>Key Features:</p>
 * <ul>
 *     <li>Displays multiple pages of story text with navigation buttons.</li>
 *     <li>Allows the user to progress through the story or return to previous pages.</li>
 *     <li>Transitions to the character selection screen upon completion of the story.</li>
 * </ul>
 *
 * @author group 44
 * @version 1.0
 */
public class StoryScreen extends ScreenAdapter {

    /** Reference to the main game instance. */
    private final Main mainGame;

    /** Slot identifier associated with this instance. */
    private final String slot;

    /** Sprite batch used for rendering. */
    private final SpriteBatch spriteBatch;

    /** Stage for managing and rendering UI components. */
    private final Stage stage;

    /** Viewport for handling screen size and scaling. */
    private final Viewport viewport;

    /** Primary font used for rendering text. */
    private final BitmapFont font;

    /** Secondary font used for rendering text with different color or size. */
    private final BitmapFont textFont;

    /** Array of story texts displayed on this page. */
    private final String[] storyTexts = new String[4];

    /** Index of the current story text being displayed. */
    private int currentTextIndex = 0;

    /** Button for navigating back in the UI. */
    private ImageButton backButton;

    /** Multiplexer for handling multiple input processors. */
    InputMultiplexer multiplexer;


    /**
     * Constructs a new {@code StoryScreen}.
     *
     * @param game       The main game instance for managing transitions and shared resources.
     * @param slotNumber The save slot number associated with this game instance.
     */
    public StoryScreen(Main game, String slotNumber) {
        mainGame = game;
        slot = slotNumber;

        // getting fonts, title font is set in resource manager
        font = mainGame.resourceManager.getTitleFont();

        // text font details (e.g color and size) will be determined in this class, thus this is loading a fresh font object
        textFont = mainGame.resourceManager.getFont(true);

        spriteBatch = mainGame.getSharedBatch();
        viewport = mainGame.getViewport();

        stage = new Stage(viewport, spriteBatch);

    }

    /**
     * Called when this screen becomes the current screen for the game.
     * Sets up the stage and initializes the UI.
     */
    public void show() {
        super.show();
        setStage(); // Reset input processor
        createUI();
    }

    /**
     * Creates the user interface for the story screen, including the back button and story text.
     */
    public void createUI(){

        Gdx.app.log("heloo", "hello");

        Gdx.app.log("CreateUI", Integer.toString(currentTextIndex));

        // due to this screen displaying multiple different screens, must make sure stage is clear before starting
        stage.clear();

        createBackButton();
        stage.addActor(backButton);

        LoadText();
    }

    /**
     * Creates a back button for navigating to the previous page of the story.
     * If on the first page, it uses the default back button from the main game.
     */
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
            backButton = mainGame.getBackButton();
        }
    }


    /**
     * Formats the given string to fit within the screen by adding line breaks at appropriate positions.
     *
     * @param text The input string to format.
     * @return A formatted string with line breaks for better readability.
     */
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

    /**
     * Loads the story text to be displayed on the screen and initializes the font properties.
     */
    public void LoadText(){

        if (storyTexts[0] == null){
            storyTexts[0] = stringFormatter("Welcome. You have been tasked with taking care of a Computer Science student at Western University. They can only get through their tough semester with your love and care.");
            storyTexts[1] = stringFormatter("You can take care of your Student by feeding them, playing with them, having them exercise, taking them to the doctor, making sure they sleep, and giving them gifts.");
            storyTexts[2] = stringFormatter("But be careful! You have to keep the health, happiness, energy and stress level of your Student in check, or there may be consequences!");
            storyTexts[3] = stringFormatter("Now's time to choose what kind of student you want to take care of and name him!");
        }

        textFont.setColor(Color.WHITE);
        float scaleFactor = viewport.getWorldHeight() * 0.0011f; // Scale as a percentage
        textFont.getData().setScale(scaleFactor);

    }

    /**
     * Calculates the position and renders the story text on the screen.
     */
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

    /**
     * Renders the story screen, including the background and current story text.
     *
     * @param delta The time elapsed since the last frame.
     */
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

    /**
     * Handles user input for navigating through the story text pages.
     */
    private void handleKeyPress() {
        if (currentTextIndex == 3) {
            mainGame.pushScreen(new CharacterSelection(mainGame, slot));
        } else if (0 <= currentTextIndex && currentTextIndex < 3) {
            currentTextIndex++;
            createUI();
        }

    }

    /**
     * Resizes the viewport when the window dimensions change.
     *
     * @param width  The new width of the window.
     * @param height The new height of the window.
     */
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    /**
     * Releases resources used by this screen, such as the stage.
     */
    @Override
    public void dispose() {
        stage.dispose();
    }

    /**
     * Sets the stage as the input processor to handle user interactions.
     */
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
