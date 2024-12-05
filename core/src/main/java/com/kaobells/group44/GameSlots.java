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

/**
 * The {@code GameSlots} class manages the screen for selecting,
 * loading, reviving, or starting a new game based on save slots.
 *
 * <p>It displays the available slots and their respective states
 * (empty, alive, or dead) and provides appropriate actions based
 * on the selected slot and the current screen mode.</p>
 *
 * @author group 44
 * @version 1.0
 */
public class GameSlots  extends ScreenAdapter {

    /** Reference to the main game instance. */
    private final Main mainGame;

    /** The current screen being displayed. */
    private final Screen screen;

    /** Sprite batch used for rendering. */
    private final SpriteBatch spriteBatch;

    /** Stage for managing and rendering UI components. */
    private final Stage stage;

    /** Viewport for handling screen size and scaling. */
    private final Viewport viewport;

    /** Button for navigating back in the UI. */
    private final ImageButton backButton;

    /** Map containing textures used in the scene. */
    private final Map<String, Texture> textures;

    /** Label style used for displaying names. */
    Label.LabelStyle nameLabelStyle;


    /**
     * Constructs a new {@code GameSlots} screen.
     *
     * @param game       The main game instance for managing resources and transitions.
     * @param screenType The type of screen ("load", "revive", or "new").
     */
    public GameSlots(Main game, String screenType) {
        mainGame = game;
        textures = new HashMap<>();

        if (screenType.equals("load")){
            screen = Screen.LOAD;
        }
        else if (screenType.equals("revive")){
            screen = Screen.REVIVE;
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

    /**
     * Displays the screen and initializes the stage input processor.
     */
    public void show() {

        super.show();
        setStage(); // Reset input processor
        stage.addActor(backButton);
    }

    /**
     * Sets the label style for slot names.
     */
    public void setLabels(){
        BitmapFont nameFont = mainGame.resourceManager.getFont(true);
        nameFont.getData().setScale((viewport.getWorldHeight() / 750f)); // Scale by 1.5x
        nameLabelStyle = new Label.LabelStyle();
        nameLabelStyle.font = nameFont; // Set the font for the label
        nameLabelStyle.fontColor = new Color(0x66 / 255f, 0x2d / 255f, 0x91 / 255f, 1f);
    }

    /**
     * Sets the input processor to the stage.
     */
    public void setStage() {
        Gdx.input.setInputProcessor(stage);
    }

    /**
     * Creates the user interface for the game slots screen.
     */
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


    /**
     * Creates a new table for managing the screen layout.
     *
     * @return A new {@link Table} instance.
     */
    private Table createTable() {
        Table newTable = new Table();
        newTable.setFillParent(true);
        newTable.center();
        newTable.padTop(viewport.getWorldHeight() * 0.1f);
        return newTable;
    }

    /**
     * Creates a table displaying a text box based on the screen type.
     *
     * @return A table with a text box image.
     */
    public Table textTable(){
        Table newTable = new Table();
        Image loadGameTextbox;

        if (screen == Screen.LOAD){
            loadGameTextbox = mainGame.createImage(textures.get("loadGameText"));
        }
        else if (screen == Screen.REVIVE){
            loadGameTextbox = mainGame.createImage(textures.get("reviveText"));
        }
        else{
            loadGameTextbox = mainGame.createImage(textures.get("newGameText"));
        }

        newTable.add(loadGameTextbox);
        return newTable;
    }

    /**
     * Creates a table for displaying a warning message.
     *
     * @return A table with a warning text image.
     */
    public Table warningTable(){
        Table newTable = new Table();
        Image warningText = mainGame.createImage(textures.get("warningText"));
        newTable.add(warningText);
        return newTable;

    }

    /**
     * Retrieves the file path for a character type.
     *
     * @param characterType The type of character.
     * @return The file path for the character image.
     */
    public String getCharacterType(String characterType) {
        return "characters/" + characterType + "-head.png";

    }

    /**
     * Retrieves the texture for a slot based on the character's state.
     *
     * @param character The character in the slot.
     * @return A drawable for the slot background.
     */
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


    /**
     * Creates a slot table with a character and its respective actions.
     *
     * @param character  The character in the slot.
     * @param slotNumber The slot number.
     * @return A table representing the slot.
     */
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

        slot.addListener(new ClickListener() {
            /**
             * Handles the touch down event, scaling the slot table down for a visual effect.
             *
             * @param event   The input event triggering the action.
             * @param x       The x-coordinate of the touch.
             * @param y       The y-coordinate of the touch.
             * @param pointer The pointer for the touch.
             * @param button  The button pressed during the touch.
             * @return True if the event is consumed.
             */
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                mainGame.getClickSound().play();

                // Scale down the table when touched
                slot.setTransform(true);
                slot.setOrigin(slot.getWidth() / 2, slot.getHeight() / 2);
                slot.setScale(0.95f);

                return true; // Consume the event
            }

            /**
             * Handles the touch up event, resetting the scale of the slot table.
             *
             * @param event   The input event triggering the action.
             * @param x       The x-coordinate of the release.
             * @param y       The y-coordinate of the release.
             * @param pointer The pointer for the touch.
             * @param button  The button released.
             */
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                // Reset the table scale when the touch is released
                slot.setTransform(true);
                slot.setOrigin(slot.getWidth() / 2, slot.getHeight() / 2);
                slot.setScale(1f);
            }
        });

        mainGame.jsonHandler.printStuff();

        if (screen == Screen.LOAD){
            if (character != null){
                slot.addListener(new ClickListener(){
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        mainGame.jsonHandler.printStuff();


                        character.startLoadCharacter(mainGame);
                        // Clear all screens except the main menu, memory saver
                        mainGame.clearStackExceptMain();
                        // just for this state right now, passing through to story screen


                        GameSession newGame = new GameSession(character, mainGame);
                        if(!(newGame.blockedPlayTimeCheck())){  //checks for playing during active parental block
                            mainGame.pushScreen(new GameScreen(mainGame, newGame));
                        } else {
                            //blocked playtime error
                            mainGame.jsonHandler.showBlockedTimeMessage(stage, viewport, mainGame);
                        }
                    }
                });
            }
        }
        else if (screen == Screen.REVIVE){
            if (character != null){
                slot.addListener(new ClickListener(){
                    @Override
                    public void clicked(InputEvent event, float x, float y) {

                        character.setHappiness(100f);
                        character.setHealth(100f);
                        character.setHunger(100f);
                        character.setSleep(100f);
                        character.setStress(100f);
                        character.setState(State.NEUTRAL);

                        character.startLoadCharacter(mainGame);
                        // Clear all screens except the main menu, memory saver
                        mainGame.clearStackExceptMain();

                        GameSession newGame = new GameSession(character, mainGame);
                        if(!(newGame.blockedPlayTimeCheck())) {
                            mainGame.pushScreen(new GameScreen(mainGame, newGame));
                        } else {
                            //playtime block error
                            mainGame.jsonHandler.showBlockedTimeMessage(stage, viewport, mainGame);
                        }
                    }
                });
            }
        }
        else{
            slot.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    // just for this state right now, passing through to story screen
                    mainGame.pushScreen(new StoryScreen(mainGame, slotNumber));
                }
            });
        }
        return slot;
    }

    /**
     * Creates a table with all the available slots.
     *
     * @return A table containing all slots.
     */
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

    /**
     * Renders the game slots screen.
     *
     * @param delta Time elapsed since the last frame.
     */
    @Override
    public void render(float delta) {
        // Clear the screen
        ScreenUtils.clear(0, 0, 0, 1);

        spriteBatch.begin();

        BitmapFont font = mainGame.resourceManager.getTitleFont();

        if (screen == Screen.NEW){
            mainGame.drawBackground(spriteBatch, mainGame.resourceManager.get("mainBackground"), font, "New Game");         // Draw the current background

        }
        else{
            mainGame.drawBackground(spriteBatch, mainGame.resourceManager.get("mainBackground"), font, "Load Game");         // Draw the current background

        }
        spriteBatch.end();

        // Render the stage (actors like buttons, character sprites, etc.)
        stage.act(delta);
        stage.draw();
    }

    /**
     * Loads textures for the game slots screen.
     */
    public void loadTextures(){
        textures.put("aliveSlot", new Texture(Gdx.files.internal("gameSlot/alive-slot-btn.png")));
        textures.put("deadSlot", new Texture(Gdx.files.internal("gameSlot/dead-slot-btn.png")));
        textures.put("emptySlot", new Texture(Gdx.files.internal("gameSlot/empty-slot-btn.png")));
        textures.put("loadGameText", new Texture(Gdx.files.internal("gameSlot/load-game-textbox.png")));
        textures.put("newGameText", new Texture(Gdx.files.internal("gameSlot/new-game-textbox.png")));
        textures.put("mysteryHead", new Texture(Gdx.files.internal("gameSlot/mysterious-head.png")));
        textures.put("warningText", new Texture(Gdx.files.internal("gameSlot/load-warning-txt.png")));
        textures.put("reviveText", new Texture(Gdx.files.internal("gameSlot/revive-box.png")));


    }

    /**
     * Adjusts the viewport on window resize.
     *
     * @param width  New window width.
     * @param height New window height.
     */
    public void resize(int width, int height) {
        viewport.update(width, height, true);

    }

    /**
     * Disposes of resources used by the screen.
     */
    @Override
    public void dispose() {
        for (Texture texture : textures.values()) {
            texture.dispose();
        }
        stage.dispose();
    }

    /**
     * Enum representing the different screen modes.
     */
    public enum Screen{
        NEW, LOAD, REVIVE
    }
}

