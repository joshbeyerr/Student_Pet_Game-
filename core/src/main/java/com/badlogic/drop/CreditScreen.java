package com.badlogic.drop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.HashMap;
import java.util.Map;

public class CreditScreen  extends ScreenAdapter {
    private Main mainGame;
    private Screen previousScreen;

    private SpriteBatch spriteBatch;
    private Stage stage;
    private Viewport viewport;

    // two fonts because this page has two different font colors/sizes needed
    private BitmapFont font;
    private BitmapFont textFont;
    private ImageButton backButton;

    public CreditScreen(Main game, Screen previousScreenn) {
        mainGame = game;
        previousScreen = previousScreenn;

        font = mainGame.resourceManager.getTitleFont();
        textFont = mainGame.resourceManager.getFont(true);

        spriteBatch = mainGame.getSharedBatch();
        viewport = mainGame.getViewport();
        backButton = mainGame.getBackButton(game,previousScreenn);

        stage = new Stage(viewport, spriteBatch);

        setStage();

        createUI();
    }

    public void setStage(){
        // Set the input processor to the stage
        Gdx.input.setInputProcessor(stage);
    }

    public void createUI(){
        stage.clear();
        stage.addActor(backButton);
        }

    public void show() {
        super.show();
        setStage(); // Reset input processor
        createUI(); // Recreate the UI elements
    }
}
/*
    private void createBackButton() {
        backButton.clearListeners(); // Clear any previous listeners to avoid stacking
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                mainGame.setScreen(previousScreen);
                ((StartScreen) previousScreen).setStage(); // Ensure screen resets its input processor
            }
        });
    }
*/
