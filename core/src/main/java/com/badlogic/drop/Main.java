package com.badlogic.drop;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.Stack;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {

    Texture backgroundTexture;
    private ScreenViewport viewport;
    private ImageButton backButton;

    private SpriteBatch sharedBatch;

    // global resource manager
    private ResourceManager resourceManager;
    private Stack stack;

    @Override
    public void create() {

        sharedBatch = new SpriteBatch();

        this.setScreen(new StartScreen(this));

        resourceManager = new ResourceManager();
        stack = new Stack();

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

    @Override
    public void render() {
        super.render(); // important!
    }

    @Override
    public void dispose() {
        sharedBatch.dispose();
        super.dispose();
    }

    public Texture getMainBackground() {
        if (backgroundTexture == null) {
            backgroundTexture = new Texture(Gdx.files.internal("background2.png"));
        }
        return backgroundTexture;
    }

    public ScreenViewport getViewport(){
        if (viewport == null) {
            // Initialize viewport and stage
            viewport = new ScreenViewport();
        }
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
       if (backButton == null){
           Texture backText = new Texture(Gdx.files.internal("backButton.png"));
           backButton = createImageButton(backText);

           // Calculate size and position relative to the viewport
           float buttonWidth = viewport.getWorldWidth() * 0.1f; // 10% of the viewport width
           float buttonHeight = viewport.getWorldHeight() * 0.1f; // 10% of the viewport height
           backButton.setSize(buttonWidth, buttonHeight);
           backButton.setPosition(buttonWidth, viewport.getWorldHeight() - buttonHeight - buttonHeight);
       }
       else{
           // Calculate size and position relative to the viewport
           float buttonWidth = viewport.getWorldWidth() * 0.1f; // 10% of the viewport width
           float buttonHeight = viewport.getWorldHeight() * 0.1f; // 10% of the viewport height
           backButton.setSize(buttonWidth, buttonHeight);
           backButton.setPosition(buttonWidth, viewport.getWorldHeight() - buttonHeight - buttonHeight);
       }



       return backButton;

    }

    public SpriteBatch getSharedBatch() {
        return sharedBatch;
    }


}
