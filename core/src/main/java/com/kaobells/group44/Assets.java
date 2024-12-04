package com.kaobells.group44;

/**
 * The {@code Assets} class is responsible for managing all game assets such as textures, fonts, sounds, and music.
 * It centralizes the loading and retrieval of these resources to ensure efficient resource management
 * throughout the game.
 *
 * <p>This class uses a singleton pattern to ensure only one instance is created during the application's lifecycle.
 * It is initialized at the start of the game and is used by various game components to fetch required assets.</p>
 *
 * @author group 44
 * @version 1.0
 * @see com.badlogic.gdx.assets.AssetManager
 */
public class Assets {

    // Fonts
    /**
     * Path to the title font file.
     */
    public static final String FONT_TITLE = "fonts/title-font.fnt";

    /**
     * Path to the body font file.
     */
    public static final String FONT_BODY = "fonts/body-font.fnt";

    // Music
    /**
     * Path to the main menu background music file.
     */
    public static final String MUSIC_MAIN_MENU = "music/jb-sample-lowqual.mp3";

    /**
     * Path to the story background music file.
     */
    public static final String MUSIC_STORY = "music/story-theme.mp3";

    // Sound Effects
    /**
     * Path to the button click sound effect file.
     */
    public static final String SOUND_BUTTON_CLICK = "sounds/button-click.wav";

    /**
     * Path to the game over sound effect file.
     */
    public static final String SOUND_GAME_OVER = "sounds/game-over.wav";

    // Textures
    /**
     * Path to the main background texture file.
     */
    public static final String TEXTURE_BACKGROUND_MAIN = "textures/main-background.png";

    /**
     * Path to the story background texture file.
     */
    public static final String TEXTURE_BACKGROUND_STORY = "textures/story-background.png";

    // Buttons
    /**
     * Path to the start game button texture file.
     */
    public static final String BUTTON_START_GAME = "startScreen/start-game-btn.png";

    /**
     * Path to the exit button texture file.
     */
    public static final String BUTTON_EXIT = "startScreen/exit-btn.png";

    /**
     * Path to the parental controls button texture file.
     */
    public static final String BUTTON_PARENTAL_CONTROLS = "startScreen/parental-controls-btn.png";

    // Character Heads
    /**
     * Path to the brave character head texture file.
     */
    public static final String HEAD_BRAVE = "characters/brave-head.png";

    /**
     * Path to the hasty character head texture file.
     */
    public static final String HEAD_HASTY = "characters/hasty-head.png";

    /**
     * Path to the quirky character head texture file.
     */
    public static final String HEAD_QUIRKY = "characters/quirky-head.png";

    /**
     * Path to the relaxed character head texture file.
     */
    public static final String HEAD_RELAXED = "characters/relaxed-head.png";

    /**
     * Path to the serious character head texture file.
     */
    public static final String HEAD_SERIOUS = "characters/serious-head.png";
}
