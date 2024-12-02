package com.kaobells.group44;

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class Item implements Json.Serializable {
    public int itemID;  // Item identifier
    public int itemCount;
    public transient float itemStatValue; //Float for calculating how much item will change stat
    private ImageButton image;


    public Item(){
        itemID = 0; // default
        itemCount = 0; // default
    }

    public Item(int ItemValID, int itemTotal){ //Constructor
        this.itemID = ItemValID;
        this.itemCount = itemTotal;

    }

    public void setItemValues(){ //based on item ID fills in stats
        switch (this.itemID){
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

    public boolean isFood(){
        return itemID == 0 || itemID == 1 || itemID == 2;
    }
    public boolean isGift(){
        return itemID == 3 || itemID == 4 || itemID == 5;
    }

    public ImageButton getImage() {
        return image;
    }

    public void setImage(ImageButton image) {
        this.image = image;
    }

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
