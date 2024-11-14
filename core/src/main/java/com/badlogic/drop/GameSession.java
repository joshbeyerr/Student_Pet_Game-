package com.badlogic.drop;

import java.util.Date;

public class GameSession {

    Character character;
    Date date;

    Item[] inventory;



    public GameSession(Character charc){
        character = charc;
        date = new Date();

        // maximum 6 items in inventory?
        inventory = new Item[6];

    }




}
