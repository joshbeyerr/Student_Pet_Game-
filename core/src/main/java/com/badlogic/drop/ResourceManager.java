package com.badlogic.drop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class ResourceManager {
    private static ResourceManager instance;

    private Map<String, Texture> textures;
    private Map<String, Texture> disposeTextures;


    public ResourceManager() {
        textures = new HashMap<>();
        disposeTextures = new HashMap<>();
    }

    public static ResourceManager getInstance() {
        if (instance == null) {
            instance = new ResourceManager();
        }
        return instance;
    }
}
