package com.kaobells.group44;

/**
 * The {@code State} enum represents the various states that a character in the game can be in.
 * These states are used to control character behavior and interactions within the game.
 *
 * <p>The possible states are:
 * <ul>
 *     <li>{@code DEAD} - Represents a state where the character has run out of resources or health.</li>
 *     <li>{@code SLEEPING} - Represents a state where the character is resting to regain energy.</li>
 *     <li>{@code ANGRY} - Represents a state where the character is upset due to unmet needs or conditions.</li>
 *     <li>{@code HUNGRY} - Represents a state where the character needs food.</li>
 *     <li>{@code NEUTRAL} - Represents a default state where the character is stable and has no pressing needs.</li>
 * </ul>
 *
 * <p>This enumeration can be used in various parts of the game logic, such as determining actions
 * the character can perform or displaying appropriate feedback to the player.</p>
 *
 * @author group 44
 * @version 1.0
 */
public enum State {
    DEAD, SLEEPING, ANGRY, HUNGRY, NEUTRAL
}
