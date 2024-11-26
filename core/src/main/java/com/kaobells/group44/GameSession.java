package com.kaobells.group44;

import com.badlogic.gdx.Gdx;
import java.util.Date;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

public class GameSession {

    CharacterClass character;
    Date date;
    int score;

    Item[] inventory;



    public GameSession(CharacterClass charc){
        character = charc;
        date = new Date();

        // maximum 6 items in inventory?
        inventory = new Item[6];

    }

    public void saveState(){

    }





}
