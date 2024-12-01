package com.kaobells.group44;

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
    private final Main mainGame;
    private int sessionsPlayed;



    public GameSession(CharacterClass charc, Main game){
        this.mainGame = game;
        this.character = charc;
        this.startTime = LocalTime.now();
        this.currentDay = LocalDate.now().getDayOfWeek();
        this.morningParentBlock = mainGame.jsonHandler.getParentalControlBoolean("morningParentBlock");
        this.afternoonParentBlock = mainGame.jsonHandler.getParentalControlBoolean("afternoonParentBlock");
        this.eveningParentBlock = mainGame.jsonHandler.getParentalControlBoolean("eveningParentBlock");
        this.weekdayParentBlock = mainGame.jsonHandler.getParentalControlBoolean("weekdayParentBlock");
        this.weekendParentBlock = mainGame.jsonHandler.getParentalControlBoolean("weekendParentBlock");
        this.sessionsPlayed = mainGame.jsonHandler.getParentalControlInt("totalSessionsPlayed") + 1;
        mainGame.jsonHandler.setParentalControlInt("totalSessionsPlayed", sessionsPlayed); //increment sessions played on new creation of a GameSession



        //code to load number of game sessions created from JSON and increment by one (then write that updated count back to JSON)
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


    public void updateParentalStats(){ //to be called on save

        //create values
        int oldTotalSecondsPlayed = mainGame.jsonHandler.getParentalControlInt("totalSecondsPlayed"); //to be replaced by call to JSON
        int newTotalSecondsPlayed = oldTotalSecondsPlayed + getSecondsPlayedThisSession();
        //write new stats to JSON
        mainGame.jsonHandler.setParentalControlInt("totalSecondsPlayed",newTotalSecondsPlayed);
        mainGame.jsonHandler.setParentalControlInt("averagePlaytimePerSession",(newTotalSecondsPlayed/sessionsPlayed));
    }

    public int getSecondsPlayedThisSession(){
        this.secondsPlayed = Duration.between(this.startTime, Instant.now()).toSeconds();
        return (int) secondsPlayed;
    }



}
