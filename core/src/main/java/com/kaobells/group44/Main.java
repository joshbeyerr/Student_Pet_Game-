package com.kaobells.group44;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.files.FileHandle;

import java.util.Stack;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {

    private Viewport viewport;


    private ImageButton backButton;

    private SpriteBatch sharedBatch;

    // global resource manager
    public ResourceManager resourceManager;

    private Stack<Screen> screenStack;

    @Override
    public void create() {

        screenStack = new Stack<>();

//        int baseWidth = Gdx.graphics.getDisplayMode().width;  // Fullscreen width
//        int baseHeight = Gdx.graphics.getDisplayMode().height; // Fullscreen height

        int baseWidth = 1920;
        int baseHeight = 1080;

        sharedBatch = new SpriteBatch();
        resourceManager = new ResourceManager();

        loadTextures();
        resourceManager.setFont(new BitmapFont(Gdx.files.internal("fonts/dick.fnt"))); // Adjust path as necessary);

        // Create a ScalingViewport to maintain aspect ratio

        viewport = new FitViewport(baseWidth, baseHeight);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        initializeLocalFile();

        this.pushScreen(new StartScreen(this));

    }

    // Push a screen onto the stack and set it as the active screen
    public void pushScreen(Screen newScreen) {
        if (!screenStack.isEmpty()) {
            screenStack.peek().pause(); // Pause the current screen
        }
        screenStack.push(newScreen);
        setScreen(newScreen);
    }

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

    public Screen getPreviousScreen() {
        if (screenStack.size() > 1) {
            // Get the second-to-last screen in the stack
            return screenStack.get(screenStack.size() - 1);
        }
        return null; // No previous screen exists
    }

    // for when a game is loading, clear all menu screens in memory except for very main menu
    public void clearStackExceptMain() {
        while (screenStack.size() > 1) {
            Screen screen = screenStack.pop();
            screen.dispose(); // Dispose of each screen being popped
        }
        // Leave the main menu (first screen) in the stack
    }



    private void loadTextures(){

        resourceManager.add("mainBackground", new Texture(Gdx.files.internal("globalAssets/menu-bg.png")));
        resourceManager.add("storyBackground", new Texture(Gdx.files.internal("globalAssets/story-bg.png")));
    }

    @Override
    public void render() {
        super.render(); // important!
    }

    @Override
    public void dispose() {
        sharedBatch.dispose();
        super.dispose();
    }

    public Viewport getViewport(){
        return viewport;
    }

    // for creating any custom Button !
    public ImageButton createImageButton(Texture upTexture) {
        TextureRegionDrawable upDrawable = new TextureRegionDrawable(new TextureRegion(upTexture));

        ImageButton.ImageButtonStyle buttonStyle = new ImageButton.ImageButtonStyle();
        buttonStyle.imageUp = upDrawable;

        ImageButton button = new ImageButton(buttonStyle);
        button.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                ((ImageButton) event.getListenerActor()).getImage().setScale(0.95f);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                ((ImageButton) event.getListenerActor()).getImage().setScale(1f);
            }
        });

        return button;
    }

    public Image createImage(Texture texture) {
        return new Image(new TextureRegionDrawable(new TextureRegion(texture)));
    }

    public ImageButton getBackButton(){
        // Create the back button using the loaded texture

        if (backButton == null) {

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
                popScreen();
            }
        });

        return backButton;

    }



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

        float textY; // Declare textY outside the conditional

        // title should be a bit higher on the new game screens for formatting
        if (title.equals("New Game")) {
            textY = (float) ((viewport.getWorldHeight() + layout.height) / 2f + (viewport.getWorldHeight() * 0.32)); // Center vertically
        } else {
            textY = (float) ((viewport.getWorldHeight() + layout.height) / 2f + (viewport.getWorldHeight() * 0.25)); // Center vertically
        }

        // Draw the text
        font.draw(batch, title, textX, textY);
    }

    public SpriteBatch getSharedBatch() {
        return sharedBatch;
    }

    @Override
    public void resize(int width, int height) {
        // Ensure the viewport updates its size while maintaining aspect ratio
        viewport.update(width, height, true);
    }

    //Internal JSON cannot be written too, only local ones
    //So we must create a local version of database on first launch
    private void initializeLocalFile(){
        // Get file handles for the assets and local directories
        FileHandle internalFile = Gdx.files.internal("database.json");
        FileHandle localFile = Gdx.files.local("database.json");

        // Check if the local file already exists
        if (!localFile.exists()) {
            // Copy the file from assets to local storage
            internalFile.copyTo(localFile);
            System.out.println("Database JSON created in local storage");
        }
        else {
            System.out.println("Local Database JSON Previously Created");
        }
    }
}
