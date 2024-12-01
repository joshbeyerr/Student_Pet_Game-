package com.kaobells.group44;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kaobells.group44.Main;

import java.util.HashMap;
import java.util.Map;

public class GameSlots  extends ScreenAdapter {
    private final Main mainGame;
    private final Screen screen;

    private final SpriteBatch spriteBatch;
    private final Stage stage;
    private final Viewport viewport;

    private final ImageButton backButton;
    private final Map<String, Texture> textures;
    Label.LabelStyle nameLabelStyle;

    public GameSlots(Main game, String screenType) {
        mainGame = game;
        textures = new HashMap<>();

        if (screenType.equals("load")){
            screen = Screen.LOAD;
        }
        else {
            screen = Screen.NEW;
        }


        spriteBatch = mainGame.getSharedBatch();
        viewport = mainGame.getViewport();

        backButton = mainGame.getBackButton();
        stage = new Stage(viewport, spriteBatch);

        setLabels();
        createUI();

    }

    public void show() {

        super.show();
        setStage(); // Reset input processor
        stage.addActor(backButton);
    }

    public void setLabels(){
        BitmapFont nameFont = mainGame.resourceManager.getFont(true);
        nameFont.getData().setScale((viewport.getWorldHeight() / 750f)); // Scale by 1.5x
        nameLabelStyle = new Label.LabelStyle();
        nameLabelStyle.font = nameFont; // Set the font for the label
        nameLabelStyle.fontColor = new Color(0x66 / 255f, 0x2d / 255f, 0x91 / 255f, 1f);
    }

    public void setStage() {
        Gdx.input.setInputProcessor(stage);
    }

    public void createUI() {

        loadTextures();

        Table table = createTable();

        Table textTable = textTable();
        textTable.center();
        table.add(textTable).row();

        Table slotTable = slotTable();
        slotTable.center();
        table.add(slotTable).padTop(viewport.getWorldHeight() * 0.05f).padBottom(viewport.getWorldHeight() * 0.04f).row();

        if (screen == Screen.NEW){
            Table warningTable = warningTable();
            warningTable.center();
            table.add(warningTable).row();
        }

        stage.addActor(table);
    }


    private Table createTable() {
        Table newTable = new Table();
        newTable.setFillParent(true);
        newTable.center();
        newTable.padTop(viewport.getWorldHeight() * 0.1f);
        return newTable;
    }

    public Table textTable(){
        Table newTable = new Table();
        Image loadGameTextbox;

        if (screen == Screen.LOAD){
            loadGameTextbox = mainGame.createImage(textures.get("loadGameText"));
        }
        else{
            loadGameTextbox = mainGame.createImage(textures.get("newGameText"));
        }

        newTable.add(loadGameTextbox);
        return newTable;
    }

    public Table warningTable(){
        Table newTable = new Table();
        Image warningText = mainGame.createImage(textures.get("warningText"));
        newTable.add(warningText);
        return newTable;

    }

    public String getCharacterType(String characterType) {
        return "characters/" + characterType + "-head.png";

    }

    public TextureRegionDrawable getSlotType(CharacterClass character) {
            if (character == null){
                return new TextureRegionDrawable(new TextureRegion(textures.get("emptySlot")));
            }
            else if (character.getState() == State.DEAD){
                return new TextureRegionDrawable(new TextureRegion(textures.get("deadSlot")));
            }
            else{
                return new TextureRegionDrawable(new TextureRegion(textures.get("aliveSlot")));
            }
    }


    public Table createSlot(CharacterClass character, String slotNumber){
        // slot
        Table slot = new Table();
        slot.setBackground(getSlotType(character));
        slot.setTouchable(Touchable.enabled);

        float headWidth = viewport.getWorldWidth() * 0.09f;
        float headHeight = viewport.getWorldWidth() * 0.08f;
        float headPad = viewport.getWorldWidth() * 0.025f;
        float textPad = viewport.getWorldWidth() * 0.005f;

        if (character != null){
            Label nameLabel = new Label(character.getName(), nameLabelStyle);
            slot.add(nameLabel).center().padBottom(textPad).row();

            Image characterImg = mainGame.createImage(new Texture(Gdx.files.internal(getCharacterType(character.getCharacterType()))));

            slot.add(characterImg).size(headWidth, headHeight).padBottom(headPad);
        }

        else {
            Label nameLabel = new Label("", nameLabelStyle);
            slot.add(nameLabel).center().padBottom(textPad).row();

            Image characterImg = mainGame.createImage(textures.get("mysteryHead"));
            slot.add(characterImg).size(headWidth, headHeight).padBottom(headPad);
        }

        System.out.println("here !");

        slot.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                mainGame.getClickSound().play();

                System.out.println("Table clickewad222d!");

                // Scale down the table when touched
                slot.setTransform(true);
                slot.setOrigin(slot.getWidth() / 2, slot.getHeight() / 2);
                slot.setScale(0.95f);

                return true; // Consume the event
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                // Reset the table scale when the touch is released
                slot.setTransform(true);
                slot.setOrigin(slot.getWidth() / 2, slot.getHeight() / 2);
                slot.setScale(1f);
            }
        });

        if (screen == Screen.LOAD){
            if (character != null){
                slot.addListener(new ClickListener(){
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        System.out.println("Table clicked new!!");

                        // Clear all screens except the main menu, memory saver
                        mainGame.clearStackExceptMain();

                        // just for this state right now, passing through to story screen

                        GameSession newGame = new GameSession(character, mainGame);
                        if(!(newGame.blockedPlayTimeCheck())){  //checks for playing during active parental block
                            mainGame.pushScreen(new GameScreen(mainGame, newGame));
                        } else {
                            //blocked playtime error
                        }
                    }
                });
            }

        }

        else{
            slot.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    System.out.println("Table clickewadd!");
                    // just for this state right now, passing through to story screen
                    mainGame.pushScreen(new StoryScreen(mainGame, slotNumber));
                }
            });
        }

        return slot;
    }

    public Table slotTable(){
        Table newTable = new Table();

        System.out.println("HERE1");

        CharacterClass character1 = (mainGame.jsonHandler.getCharacterFromGameSlot("1"));
        CharacterClass character2 = (mainGame.jsonHandler.getCharacterFromGameSlot("2"));
        CharacterClass character3 = (mainGame.jsonHandler.getCharacterFromGameSlot("3"));


        // slot1
        Table slot1 = createSlot(character1, "1");
        Table slot2 = createSlot(character2, "2");


        Table slot3 = createSlot(character3, "3");

        float padVal = viewport.getWorldWidth() * 0.01f;

        newTable.add(slot1).padLeft(padVal).padRight(padVal);;
        newTable.add(slot2).padLeft(padVal).padRight(padVal);;
        newTable.add(slot3).padLeft(padVal).padRight(padVal);;

        return newTable;

    }


    @Override
    public void render(float delta) {
        // Clear the screen
        ScreenUtils.clear(0, 0, 0, 1);

        spriteBatch.begin();

        BitmapFont font = mainGame.resourceManager.getTitleFont();

        mainGame.drawBackground(spriteBatch, mainGame.resourceManager.get("mainBackground"), font, "New Game");         // Draw the current background
        spriteBatch.end();

        // Render the stage (actors like buttons, character sprites, etc.)
        stage.act(delta);
        stage.draw();
    }

    public void loadTextures(){
        textures.put("aliveSlot", new Texture(Gdx.files.internal("gameSlot/alive-slot-btn.png")));
        textures.put("deadSlot", new Texture(Gdx.files.internal("gameSlot/dead-slot-btn.png")));
        textures.put("emptySlot", new Texture(Gdx.files.internal("gameSlot/empty-slot-btn.png")));
        textures.put("loadGameText", new Texture(Gdx.files.internal("gameSlot/load-game-textbox.png")));
        textures.put("newGameText", new Texture(Gdx.files.internal("gameSlot/new-game-textbox.png")));
        textures.put("mysteryHead", new Texture(Gdx.files.internal("gameSlot/mysterious-head.png")));
        textures.put("warningText", new Texture(Gdx.files.internal("gameSlot/load-warning-txt.png")));


    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);

    }

    @Override
    public void dispose() {
        for (Texture texture : textures.values()) {
            texture.dispose();
        }
        stage.dispose();
    }

    public enum Screen{
        NEW, LOAD
    }
}

