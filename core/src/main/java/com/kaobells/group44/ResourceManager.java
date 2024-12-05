package com.kaobells.group44;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@code ResourceManager} class is a class responsible for managing
 * shared resources, such as textures and fonts, used across the application.
 *
 * <p>This class allows the centralized management of assets, ensuring that
 * resources are loaded, accessed, and disposed of efficiently.</p>
 *
 * @author group 44
 * @version 1.0
 */
public class ResourceManager {

    /** Instance of the resource manager. */
    private static ResourceManager instance;

    /** Map containing textures for managing game assets. */
    private Map<String, Texture> textures;

    /** Font used for rendering text in the game. */
    private BitmapFont font;


    /**
     * Constructs a new {@code ResourceManager}. Initializes an empty map for textures.
     */
    public ResourceManager() {
        textures = new HashMap<>();
    }

    /**
     * Returns the singleton instance of the {@code ResourceManager}.
     * If the instance does not exist, it creates one.
     *
     * @return The singleton instance of {@code ResourceManager}.
     */
    public static ResourceManager getInstance() {
        if (instance == null) {
            instance = new ResourceManager();
        }
        return instance;
    }

    /**
     * Adds a texture to the resource manager.
     *
     * @param key     The unique key to associate with the texture.
     * @param texture The {@link Texture} to be added.
     */
    public void add(String key, Texture texture) {
        textures.put(key, texture);
    }

    /**
     * Retrieves a texture from the resource manager based on its key.
     *
     * @param key The unique key associated with the texture.
     * @return The {@link Texture} associated with the given key, or {@code null} if not found.
     */
    public Texture get(String key) {
        return textures.get(key);
    }


    /**
     * Sets the default font to be used by the resource manager.
     *
     * @param font The {@link BitmapFont} to be set as the default font.
     */
    public void setFont(BitmapFont font) {
        this.font = font;
    }

    /**
     * Retrieves the default font, optionally creating a new instance.
     *
     * @param neww If {@code true}, returns a new instance of the {@link BitmapFont};
     *             if {@code false}, returns the existing instance.
     * @return The {@link BitmapFont} based on the specified behavior.
     */
    public BitmapFont getFont(boolean neww) {
        if (neww){
            return new BitmapFont(font.getData().fontFile, font.getRegion(), false);
        }
        else{
            return font;
        }
    }

    /**
     * Retrieves the title font with a specific color setting.
     *
     * <p>The font color is set to a custom purple shade (R: 0.3, G: 0.1, B: 0.45, A: 1.0).</p>
     *
     * @return The {@link BitmapFont} styled as the title font.
     */
    public BitmapFont getTitleFont(){
        font.setColor(new Color(0.3f, 0.1f, 0.45f, 1.0f));
        return font;
    }

}
