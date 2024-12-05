package com.kaobells.group44;

import java.time.*;

/**
 * The {@code GameSession} class manages the active gameplay session,
 * including parental controls, session statistics, and playtime tracking.
 * It ensures that gameplay adheres to the rules set by parental controls
 * and tracks playtime for each session.
 *
 * <p>This class is tightly integrated with the {@link Main} and {@link CharacterClass}
 * to facilitate gameplay and parental statistics functionality.</p>
 *
 * @author group 44
 * @version 1.0
 */
public class GameSession {

    CharacterClass character;
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



    /**
     * Constructs a new {@code GameSession}.
     *
     * <p>Initializes the session's start time, checks parental controls,
     * and increments the session count in the database.</p>
     *
     * @param charc The character associated with this session.
     * @param game The main game instance.
     */
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
    }

    /**
     * Checks if the current playtime falls within a blocked period defined by parental controls.
     *
     * <p>If blocked, decrements the session count to account for an invalid session.</p>
     *
     * @return {@code true} if the current playtime is blocked; {@code false} otherwise.
     */
    public boolean blockedPlayTimeCheck(){
        /*
        Returns true if player is playing in a blocked time called post-construction but pre-GameScreen Creation.
        Since constructor increments sessions played in JSON if the block is true it decrements it in the JSON
        since this does not count as a session.
        */
        if(morningParentBlock){
            if(LocalTime.now().isAfter(LocalTime.of(5,59)) && LocalTime.now().isBefore(LocalTime.of(12,0))){
                this.sessionsPlayed = mainGame.jsonHandler.getParentalControlInt("totalSessionsPlayed") - 1;
                mainGame.jsonHandler.setParentalControlInt("totalSessionsPlayed", sessionsPlayed);
                return true;
            }
        }

        if(afternoonParentBlock){
            if(LocalTime.now().isAfter(LocalTime.of(11,59)) && LocalTime.now().isBefore(LocalTime.of(20,1))){
                this.sessionsPlayed = mainGame.jsonHandler.getParentalControlInt("totalSessionsPlayed") - 1;
                mainGame.jsonHandler.setParentalControlInt("totalSessionsPlayed", sessionsPlayed);
                return true;
            }
        }

        if(eveningParentBlock){
            if(LocalTime.now().isAfter(LocalTime.of(20,0))|| LocalTime.now().isBefore(LocalTime.of(6,0))){
                this.sessionsPlayed = mainGame.jsonHandler.getParentalControlInt("totalSessionsPlayed") - 1;
                mainGame.jsonHandler.setParentalControlInt("totalSessionsPlayed", sessionsPlayed);
                return true;
            }
        }

        if(weekendParentBlock){
            if(currentDay == DayOfWeek.FRIDAY || currentDay == DayOfWeek.SATURDAY || currentDay == DayOfWeek.SUNDAY){
                this.sessionsPlayed = mainGame.jsonHandler.getParentalControlInt("totalSessionsPlayed") - 1;
                mainGame.jsonHandler.setParentalControlInt("totalSessionsPlayed", sessionsPlayed);
                return true;
            }
        }

        if(weekdayParentBlock){
            if(currentDay != DayOfWeek.FRIDAY && currentDay != DayOfWeek.SATURDAY && currentDay != DayOfWeek.SUNDAY){
                this.sessionsPlayed = mainGame.jsonHandler.getParentalControlInt("totalSessionsPlayed") - 1;
                mainGame.jsonHandler.setParentalControlInt("totalSessionsPlayed", sessionsPlayed);
                return true;
            }
        }

        return false;
    }

    /**
     * Updates parental statistics in the database, such as total playtime
     * and average playtime per session.
     *
     * <p>Typically called when saving the game.</p>
     */
    public void updateParentalStats(){ //Called on save
        //create values
        int oldTotalSecondsPlayed = mainGame.jsonHandler.getParentalControlInt("totalSecondsPlayed"); //to be replaced by call to JSON
        int newTotalSecondsPlayed = oldTotalSecondsPlayed + getSecondsPlayedThisSession();
        //write new stats to JSON
        mainGame.jsonHandler.setParentalControlInt("totalSecondsPlayed",newTotalSecondsPlayed);
        mainGame.jsonHandler.setParentalControlInt("averagePlaytimePerSession",(newTotalSecondsPlayed/sessionsPlayed));
    }

    /**
     * Calculates the total seconds played since the session started.
     *
     * @return The total seconds played as an {@code int}.
     */
    public int getSecondsPlayedThisSession(){
        this.secondsPlayed = Duration.between(this.startTime, LocalTime.now()).toSeconds();
        return (int) secondsPlayed;
    }

}
