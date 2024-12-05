package com.kaobells.group44;

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

/**
 * The {@code Item} class represents an item within the game, capable of affecting
 * the character's stats and being serialized/deserialized using JSON. Items can
 * either be food or gifts, each affecting stats differently.
 *
 * <p>Items are identified by their unique {@code itemID} and can have a count
 * indicating their quantity.</p>
 *
 * @author group 44
 * @version 1.0
 */
public class Item implements Json.Serializable {

    /** Unique identifier for the item. */
    public int itemID;

    /** Count of the item in inventory. */
    public int itemCount;

    /** Float value used to calculate how much the item will change a stat. */
    public transient float itemStatValue;

    /** Image button representing the item in the UI. */
    private ImageButton image;



    public Item(){ //default constructor for JSON
        itemID = 0; // default
        itemCount = 0; // default
    }

    /**
     * Constructs an item with a specific ID and count.
     *
     * @param ItemValID The unique identifier for the item.
     * @param itemTotal The initial count of this item.
     */
    public Item(int ItemValID, int itemTotal){ //Constructor
        this.itemID = ItemValID; //
        this.itemCount = itemTotal;

    }

    /**
     * Sets the {@code itemStatValue} for the item based on its {@code itemID}.
     */
    public void setItemValues(){ //based on item ID fills in stats
        switch (this.itemID){
            //case 0 = apple food item
            case 0:
                this.itemStatValue = 30.0f;
                break;
            //case 1 = lemon food item
            case 1:
                this.itemStatValue = 60.0f;
                break;
            //case 2 = orange food item
            case 2:
                this.itemStatValue = 90.0f;
                break;
            //case 3 = duck gift item
            case 3:
                this.itemStatValue = 30.0f;
                break;
            //case 4 = orange duck gift item
            case 4:
                this.itemStatValue = 60.0f;
                break;
            //case 5 = blue duck gift item
            case 5:
                this.itemStatValue = 90.0f;
                break;
        }
    }

    /**
     * Retrieves the stat value effect of the item.
     *
     * @return The stat value effect as a float.
     */
    public float getItemStatValue(){
        return this.itemStatValue;
    }

    /**
     * Retrieves the unique identifier for this item.
     *
     * @return The {@code itemID} of the item.
     */
    public int getItemID(){
        return this.itemID;
    }

    /**
     * Reduces the count of the item by 1. If the count is already 0, it does not
     * reduce further.
     *
     * @return {@code true} if the count was reduced, {@code false} otherwise.
     */
    public boolean reduceCount(){
        if (this.itemCount <= 0){
            return false;
        }
        else{
            this.itemCount--;
            return true;
        }
    }

    /**
     * Increases the count of the item by 1.
     */
    public void increaseCount(){
        this.itemCount++;
    }

    /**
     * Sets the count of this item to a specific value.
     *
     * @param setCount The new count for the item.
     */
    public void setItemCount(int setCount){ this.itemCount = setCount; }

    /**
     * Retrieves the current count of this item.
     *
     * @return The count of this item.
     */
    public int getItemCount(){ return this.itemCount; }

    /**
     * Determines if the item is a food item.
     *
     * @return {@code true} if the item is food, {@code false} otherwise.
     */
    public boolean isFood(){
        return itemID == 0 || itemID == 1 || itemID == 2;
    }

    /**
     * Determines if the item is a gift item.
     *
     * @return {@code true} if the item is a gift, {@code false} otherwise.
     */
    public boolean isGift(){
        return itemID == 3 || itemID == 4 || itemID == 5;
    }

    /**
     * Retrieves the visual representation of the item.
     *
     * @return The {@link ImageButton} associated with this item.
     */
    public ImageButton getImage() {
        return image;
    }

    /**
     * Sets the visual representation of the item.
     *
     * @param image The {@link ImageButton} to associate with this item.
     */
    public void setImage(ImageButton image) {
        this.image = image;
    }

    /**
     * Serializes the item's {@code itemID} and {@code itemCount} into JSON format.
     *
     * @param json The {@link Json} instance to handle serialization.
     */
    @Override
    public void write(Json json) {
        json.writeValue("itemID", itemID);
        json.writeValue("itemCount", itemCount);
    }

    /**
     * Deserializes the item's {@code itemID} and {@code itemCount} from JSON data.
     *
     * @param json     The {@link Json} instance to handle deserialization.
     * @param jsonData The {@link JsonValue} containing the serialized data.
     */
    @Override
    public void read(Json json, JsonValue jsonData) {
        itemID = jsonData.getInt("itemID", 0);
        itemCount = jsonData.getInt("itemCount", 0);
    }
}
