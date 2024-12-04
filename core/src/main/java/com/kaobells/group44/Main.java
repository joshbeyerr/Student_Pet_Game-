package com.kaobells.group44;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Stack;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
/**
 * The {@code Main} class is the entry point of the application and serves
 * as the primary controller for managing screens, resources, and global
 * functionality across the game.
 *
 * <p>This class handles the game's lifecycle, manages screen transitions,
 * and provides utility methods for shared components such as the back button,
 * error messages, and custom UI elements.</p>
 *
 * @author Group 44
 * @version 1.0
 */
public class Main extends Game {

    private Viewport viewport;
    public JsonHandler jsonHandler;


    private ImageButton backButton;
    private Table errorTable;
    private Label errorLabel;
    private Sound clickSound;
    private Sound backButtonSound;
    private SpriteBatch sharedBatch;

    // global resource manager GOING TO ERASE THIS

    private AssetManager assetManager;

    public ResourceManager resourceManager;

    private Stack<Screen> screenStack;

    /**
     * Initializes the game, setting up resources, the viewport, and the initial screen.
     */
    @Override
    public void create() {


//        assetManager = new AssetManager();
        screenStack = new Stack<>();

        jsonHandler = new JsonHandler();

        int baseWidth = 1920;
        int baseHeight = 1080;

        sharedBatch = new SpriteBatch();
        resourceManager = new ResourceManager();

        loadTextures();
        resourceManager.setFont(new BitmapFont(Gdx.files.internal("fonts/dick.fnt"))); // Adjust path as necessary);

        // Create a ScalingViewport to maintain aspect ratio

        viewport = new FitViewport(baseWidth, baseHeight);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);


        this.pushScreen(new StartScreen(this));

    }

    /**
     * Pushes a new screen onto the screen stack and sets it as the active screen.
     *
     * @param newScreen The screen to be displayed.
     */
    // Push a screen onto the stack and set it as the active screen
    public void pushScreen(Screen newScreen) {
        if (!screenStack.isEmpty()) {
            screenStack.peek().pause(); // Pause the current screen
        }
        screenStack.push(newScreen);
        setScreen(newScreen);
    }

    /**
     * Pops the current screen from the stack, disposes of it, and resumes the previous screen.
     */
    // Pop the current screen, dispose of it, and set the previous one
    public void popScreen() {
        if (!screenStack.isEmpty()) {
            Screen currentScreen = screenStack.pop();
            currentScreen.dispose(); // Dispose of the current screen
        }
        if (!screenStack.isEmpty()) {
            Gdx.app.log("herrre", "gg");
            Screen previousScreen = screenStack.peek();
            previousScreen.resume(); // Resume the previous screen
            setScreen(previousScreen);
        }
    }

    /**
     * Retrieves the previous screen from the stack without popping it.
     *
     * @return The previous screen, or {@code null} if no previous screen exists.
     */
    public Screen getPreviousScreen() {
        if (screenStack.size() > 1) {
            // Get the second-to-last screen in the stack
            return screenStack.get(screenStack.size() - 1);
        }
        return null; // No previous screen exists
    }

    /**
     * Clears all screens from the stack except the main menu.
     */
    // for when a game is loading, clear all menu screens in memory except for very main menu
    public void clearStackExceptMain() {
        while (screenStack.size() > 1) {
            Screen screen = screenStack.pop();
            screen.dispose(); // Dispose of each screen being popped
        }
        // Leave the main menu (first screen) in the stack
    }


    /**
     * Loads textures and sounds required for the game.
     */
    private void loadTextures(){

        resourceManager.add("mainBackground", new Texture(Gdx.files.internal("globalAssets/menu-bg.png")));
        resourceManager.add("storyBackground", new Texture(Gdx.files.internal("globalAssets/story-bg.png")));
        clickSound = Gdx.audio.newSound(Gdx.files.internal("music/btn-click.mp3"));
        backButtonSound = Gdx.audio.newSound(Gdx.files.internal("music/back-click.mp3"));
    }

    /**
     * Renders the game, delegating rendering tasks to the active screen.
     */
    @Override
    public void render() {
        super.render(); // important!
    }


    /**
     * Disposes of all shared resources, including the sprite batch and super resources.
     */
    @Override
    public void dispose() {
        sharedBatch.dispose();
        super.dispose();
    }

    /**
     * Retrieves the viewport used for managing screen layouts.
     *
     * @return The game's {@link Viewport}.
     */
    public Viewport getViewport(){
        return viewport;
    }

    /**
     * Retrieves the click sound used for button interactions.
     *
     * @return A {@link Sound} object for the click sound effect.
     */
    public Sound getClickSound(){
        return clickSound;
    }



    /**
     * Creates a custom {@link ImageButton} with a click effect.
     *
     * @param upTexture The texture to display for the button's normal state.
     * @return An {@link ImageButton} instance with the specified texture and effects.
     */
    // for creating any custom Button !
    public ImageButton createImageButton(Texture upTexture) {
        TextureRegionDrawable upDrawable = new TextureRegionDrawable(new TextureRegion(upTexture));

        ImageButton.ImageButtonStyle buttonStyle = new ImageButton.ImageButtonStyle();
        buttonStyle.imageUp = upDrawable;

        ImageButton button = new ImageButton(buttonStyle);
        button.addListener(new ClickListener() {

            /**
             * Plays the click sound and scales the image down for a pressed button effect.
             *
             * @param event   The {@link InputEvent} triggered by the button press.
             * @param x       The x-coordinate of the touch.
             * @param y       The y-coordinate of the touch.
             * @param pointer The pointer for the touch event.
             * @param button  The button involved in the touch event.
             * @return {@code true} if the event was handled successfully.
             */
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                clickSound.play();

                // Cast the event actor to ImageButton
                Image image = ((ImageButton) event.getListenerActor()).getImage();

                // Set the origin to the center of the image
                image.setOrigin(image.getWidth() / 2, image.getHeight() / 2);

                // Scale down the image relative to its center
                image.setScale(0.95f);

                return true;
            }

            /**
             * Resets the image to its original size after the button is released.
             *
             * @param event   The {@link InputEvent} triggered by the button release.
             * @param x       The x-coordinate of the touch.
             * @param y       The y-coordinate of the touch.
             * @param pointer The pointer for the touch event.
             * @param button  The button involved in the touch event.
             */
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {

                // Cast the event actor to ImageButton
                Image image = ((ImageButton) event.getListenerActor()).getImage();

                // Reset the origin to the center of the image
                image.setOrigin(image.getWidth() / 2, image.getHeight() / 2);

                // Reset the scale to normal
                image.setScale(1f);
            }

        });

        return button;
    }

    /**
     * Creates an {@link Image} using the specified texture.
     *
     * @param texture The texture for the image.
     * @return A new {@link Image} instance.
     */
    public Image createImage(Texture texture) {
        return new Image(new TextureRegionDrawable(new TextureRegion(texture)));
    }

    /**
     * Retrieves the global back button, creating it if necessary.
     *
     * @return The {@link ImageButton} instance for the back button.
     */
    public ImageButton getBackButton(){
        // Create the back button using the loaded texture

        if (backButton == null) {
            System.out.println("\n back button created (was null) ");
            Texture backText = new Texture(Gdx.files.internal("globalAssets/backButton.png"));
            backButton = createImageButton(backText);
        }

        // Calculate size and position relative to the viewport
        float buttonWidth = viewport.getWorldWidth() * 0.1f; // 10% of the viewport width
        float buttonHeight = viewport.getWorldHeight() * 0.1f; // 10% of the viewport height
        backButton.setSize(buttonWidth, buttonHeight);
        backButton.setPosition(buttonWidth, viewport.getWorldHeight() - buttonHeight - buttonHeight);
        backButton.clearListeners(); // Clear any previous listeners to avoid stacking
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                backButtonSound.play();
                popScreen();
            }
        });

        return backButton;

    }

    /**
     * Retrieves the error message table, creating it if necessary.
     *
     * @return A {@link Table} containing the error message UI.
     */
    public Table getErrorMessage() {
        if (errorTable == null) {
            // Create the error table
            errorTable = new Table();
            errorTable.setVisible(false);

            // Load the background texture
            Texture errorBackground = new Texture(Gdx.files.internal("globalAssets/error-sm-box.png"));

            // Set table background
            errorTable.setBackground(new TextureRegionDrawable(new TextureRegion(errorBackground)));

            // Create the error label
            BitmapFont font = resourceManager.getFont(true);
            font.getData().setScale(0.8f); // Adjust font size as needed
            Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.BLACK);
            errorLabel = new Label("", labelStyle);
            errorLabel.setAlignment(Align.center);

            // Add label to the table
            errorTable.add(errorLabel).expand().fill().pad(10); // Add some padding for aesthetics

            // Set size and position of the table
            float errorWidth = viewport.getWorldWidth() * 0.3f; // 30% of the viewport width
            float errorHeight = viewport.getWorldHeight() * 0.1f; // 10% of the viewport height
            errorTable.setSize(errorWidth, errorHeight);
            errorTable.setPosition(viewport.getWorldWidth() - errorWidth - 10, errorHeight/3); // Bottom-right corner with padding

        }

        return errorTable;
    }


    /**
     * Displays an error message on the screen.
     *
     * @param message The error message to display.
     */
    public void sendError(String message) {
        if (errorTable == null) {
            getErrorMessage();
        }
        if (errorLabel.isVisible()){
            // Split the message into multiple lines if it's longer than 20 characters
            StringBuilder formattedMessage = new StringBuilder();
            int maxLineLength = 30;
            int currentIndex = 0;

            while (currentIndex < message.length()) {
                int endIndex = Math.min(currentIndex + maxLineLength, message.length());
                formattedMessage.append(message, currentIndex, endIndex);

                // If not the last line, add a newline character
                if (endIndex < message.length()) {
                    formattedMessage.append("\n");
                }

                currentIndex = endIndex;
            }

            // Set the formatted message text
            errorLabel.setText(formattedMessage.toString());

            // Make the table visible
            errorTable.clearActions(); // Clear any ongoing actions
            errorTable.setVisible(true);

            // Add fade-out animation
            errorTable.addAction(Actions.sequence(
                Actions.alpha(1), // Ensure full opacity
                Actions.delay(5), // Stay visible for 5 seconds
                Actions.fadeOut(1), // Fade out over 1 second
                Actions.run(() -> errorTable.setVisible(false)) // Hide after fading out
            ));
        }
    }

    /**
     * Draws the background and title text.
     *
     * @param batch              The {@link SpriteBatch} for rendering.
     * @param backgroundTexture  The background texture.
     * @param font               The font for the title text.
     * @param title              The title text to display.
     */
    public void drawBackground(SpriteBatch batch, Texture backgroundTexture, BitmapFont font, String title) {
        // Draw the background texture
        batch.draw(backgroundTexture, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());

        float scaleFactor = viewport.getWorldHeight() * 0.0025f; // Scale as a percentage
        font.getData().setScale(scaleFactor);

        // Use GlyphLayout to calculate the width and height of the text
        GlyphLayout layout = new GlyphLayout();
        layout.setText(font, title);

        // Calculate text position to center it in the viewport
        float textX = (viewport.getWorldWidth() - layout.width) / 2f;
        float textY = (float) ((viewport.getWorldHeight() + layout.height) / 2f + (viewport.getWorldHeight() * 0.32)); // Center vertically


        // Draw the text
        font.draw(batch, title, textX, textY);
    }

    /**
     * Retrieves the shared {@link SpriteBatch} for rendering.
     *
     * @return The shared {@link SpriteBatch}.
     */
    public SpriteBatch getSharedBatch() {
        return sharedBatch;
    }

    /**
     * Updates the viewport size when the window is resized.
     *
     * @param width  The new width of the window.
     * @param height The new height of the window.
     */
    @Override
    public void resize(int width, int height) {
        // Ensure the viewport updates its size while maintaining aspect ratio
        viewport.update(width, height, true);
    }


}
