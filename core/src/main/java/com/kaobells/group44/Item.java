package com.kaobells.group44;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class Item implements Json.Serializable {
    public int itemID;  // Item identifier
    public int itemCount;
    public transient float itemStatValue; //Float for calculating how much item will change stat


    public Item(){ //Constructor
        itemID = 0; // default
        itemCount = 0; // default
    }

    public Item(int ItemValID, int itemTotal){ //Constructor
    this.itemID = ItemValID;
    this.itemCount = itemTotal;
    setItemValues(itemID);
    }

    public void setItemValues(int ItemID){ //based on item ID fills in stats
        switch (ItemID){
            //case 0 cup noodle food item
            case 0:
                this.itemStatValue = 30.0f;
                break;

            //case 1 spoke bagel food item
            case 1:
                this.itemStatValue = 60.0f;
                break;

            //case 2 shawarma food item
            case 2:
                this.itemStatValue = 90.0f;
                break;

            //case 3 bronze duck gift item
            case 3:
                this.itemStatValue = 30.0f;
                break;

            //case 4 silver duck gift item
            case 4:
                this.itemStatValue = 60.0f;
                break;

            //case 5 golden duck gift item
            case 5:
                this.itemStatValue = 90.0f;
                break;
        }
    }

    public float getItemStatValue(){
        return this.itemStatValue;
    }

    public int getItemID(){
        return this.itemID;
    }

    public boolean reduceCount(){
        if (this.itemCount <= 0){
            return false;
        }
        else{
            this.itemCount--;
            return true;
        }
    }
    public void increaseCount(){
        this.itemCount++;
    }

    public void setItemCount(int setCount){ this.itemCount = setCount; }

    public int getItemCount(){ return this.itemCount; }

    @Override
    public void write(Json json) {
        json.writeValue("itemID", itemID);
        json.writeValue("itemCount", itemCount);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        itemID = jsonData.getInt("itemID", 0);
        itemCount = jsonData.getInt("itemCount", 0);
    }
}
