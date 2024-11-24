package com.badlogic.drop;
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
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {

    private Viewport viewport;


    private ImageButton backButton;

    private SpriteBatch sharedBatch;

    // global resource manager
    public ResourceManager resourceManager;


    private int baseWidth;
    private int baseHeight;


    @Override
    public void create() {

        baseWidth = Gdx.graphics.getDisplayMode().width;  // Fullscreen width
        baseHeight = Gdx.graphics.getDisplayMode().height; // Fullscreen height

        sharedBatch = new SpriteBatch();
        resourceManager = new ResourceManager();

        loadTextures();
        resourceManager.setFont(new BitmapFont(Gdx.files.internal("fonts/dick.fnt"))); // Adjust path as necessary);

        // Create a ScalingViewport to maintain aspect ratio

        viewport = new FitViewport(baseWidth, baseHeight);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        this.setScreen(new StartScreen(this));

    }

    @Override
    public void setScreen(Screen screen) {


        // Dispose the previous screen if it exists
        if (getScreen() != null) {
            getScreen().dispose();
        }
        super.setScreen(screen);
    }

    public void setScreenNoDispose(Screen screen) {
        super.setScreen(screen);
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

    public ImageButton getBackButton(Main mainGame,Screen previousScreen){
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
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                mainGame.setScreen(previousScreen);
                ((StartScreen) previousScreen).setStage(); // Ensure screen resets its input processor
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

}
