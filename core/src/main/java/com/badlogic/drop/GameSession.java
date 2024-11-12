package com.badlogic.drop;

import java.util.Date;

public class GameSession {

    Character character;
    Date date;

    public GameSession(Character charc){
        character = charc;
        date = new Date();

    }
}
