package com.kaobells.group44;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import java.util.HashMap;
import java.util.Map;

public class ResourceManager {
    private static ResourceManager instance;

    private Map<String, Texture> textures;

    private BitmapFont font;


    public ResourceManager() {
        textures = new HashMap<>();
    }

    public static ResourceManager getInstance() {
        if (instance == null) {
            instance = new ResourceManager();
        }
        return instance;
    }

    public void add(String key, Texture texture) {
        textures.put(key, texture);
    }
    public Texture get(String key) {
        return textures.get(key);
    }


    public void setFont(BitmapFont font) {
        this.font = font;
    }
    public BitmapFont getFont(boolean neww) {
        if (neww){
            return new BitmapFont(font.getData().fontFile, font.getRegion(), false);
        }
        else{
            return font;
        }
    }
    public BitmapFont getTitleFont(){
        font.setColor(new Color(0.3f, 0.1f, 0.45f, 1.0f));
        return font;
    }

}
