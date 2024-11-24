package com.kaobells.group44;

import java.util.Date;

public class GameSession {

    CharacterClass character;
    Date date;

    Item[] inventory;



    public GameSession(CharacterClass charc){
        character = charc;
        date = new Date();

        // maximum 6 items in inventory?
        inventory = new Item[6];

    }




}
