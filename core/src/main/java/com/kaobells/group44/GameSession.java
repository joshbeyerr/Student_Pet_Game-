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
        //load in all the parental blocks from JSON here
        //Temporarily autosetting to false
        morningParentBlock = false;
        afternoonParentBlock = false;
        eveningParentBlock = false;
        weekdayParentBlock = false;
        weekendParentBlock = false;

        //code to load number of game sessions created from JSON and increment by one (then write that updated count back to JSON)
    }


    public void saveState(){

    }

    public boolean blockedPlayTimeCheck(){
        //Returns true if player is playing in a blocked time
        //Call on launch game and maybe every 10 min

        if(morningParentBlock){
            if(LocalTime.now().isAfter(LocalTime.of(6,0)) && LocalTime.now().isBefore(LocalTime.of(12,0))){
                return true;
            }
        }

        if(afternoonParentBlock){
            if(LocalTime.now().isAfter(LocalTime.of(12,0)) && LocalTime.now().isBefore(LocalTime.of(18,0))){
                return true;
            }
        }

        if(eveningParentBlock){
            if(LocalTime.now().isAfter(LocalTime.of(18,0)) && LocalTime.now().isBefore(LocalTime.of(23,59))){
                return true;
            }
        }

        if(weekdayParentBlock){
            if(currentDay == DayOfWeek.FRIDAY || currentDay == DayOfWeek.SATURDAY || currentDay == DayOfWeek.SUNDAY){
                return true;
            }
        }

        if(weekendParentBlock){
            if(currentDay != DayOfWeek.FRIDAY && currentDay != DayOfWeek.SATURDAY && currentDay != DayOfWeek.SUNDAY){
                return true;
            }
        }

        return false;
    }


    public void updateParentalStats(){
        //to be called on save

        //get old stats
        long oldTotalSecondsPlayed = 0; //to be replaced by call to JSON
        long TotalSessionsPlayed = 0; //to be replaced by call to JSON

        //update stats with new session's data
        long newTotalSecondsPlayed = oldTotalSecondsPlayed+getSecondsPlayed();
        long newAverageSecondsPlayed = newTotalSecondsPlayed/TotalSessionsPlayed;

        /*
        Code needs to be put here to write these new values into JSON.
         */

    }

    public long getSecondsPlayed(){
        this.secondsPlayed = Duration.between(this.startTime, Instant.now()).toSeconds();
        return secondsPlayed;
    }



}
