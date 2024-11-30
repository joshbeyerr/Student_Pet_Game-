package com.kaobells.group44;

import com.badlogic.gdx.Gdx;
import java.util.Date;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import java.time.Instant;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.DayOfWeek;

public class GameSession {

    CharacterClass character;
    int score;
    private LocalTime startTime;
    private long secondsPlayed;
    private DayOfWeek currentDay;
    private boolean morningParentBlock;
    private boolean afternoonParentBlock;
    private boolean eveningParentBlock;
    private boolean weekdayParentBlock;
    private boolean weekendParentBlock;



    public GameSession(CharacterClass charc){
        this.character = charc;
        this.startTime = LocalTime.now();
        this.currentDay = LocalDate.now().getDayOfWeek();
    }


    public void saveState(){

    }

    public void updateParentalStats(){
        //to be called on save

        //get old stats
        long oldTotalSecondsPlayed = 0; //to be replaced by call to JSON
        long oldTotalSessionsPlayed = 0; //to be replaced by call to JSON

        //update stats with new session's data
        long newTotalSessionsPlayed = oldTotalSessionsPlayed+1;
        long newTotalSecondsPlayed = oldTotalSecondsPlayed+getSecondsPlayed();
        long newAverageSecondsPlayed = newTotalSecondsPlayed/newTotalSessionsPlayed;

        /*
        Code needs to be put here to write these new values into JSON.
         */

    }

    public long getSecondsPlayed(){
        this.secondsPlayed = Duration.between(this.startTime, Instant.now()).toSeconds();
        return secondsPlayed;
    }



}
