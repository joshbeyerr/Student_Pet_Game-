package com.kaobells.group44;

public class Item {
    public int itemID;  // Item identifier
    public String itemStat; // Stat that Item will change
    public float itemStatValue; //Float for calculating how much item will change stat
    public int itemCount;

    public Item(int ItemValID, int itemTotal){ //Constructor
    this.itemID = ItemValID;
    this.itemCount = itemTotal;
    setItemValues(itemID);
    }

    public void setItemValues(int ItemID){ //based on item ID fills in stats
        switch (ItemID){
            //case 0 cup noodle food item
            case 0:
                this.itemStat = "fullness";
                this.itemStatValue = 30.0f;
                break;

            //case 1 spoke bagel food item
            case 1:
                this.itemStat = "fullness";
                this.itemStatValue = 60.0f;
                break;

            //case 2 shawarma food item
            case 2:
                this.itemStat = "fullness";
                this.itemStatValue = 90.0f;
                break;

            //case 3 bronze duck gift item
            case 3:
                this.itemStat = "happiness";
                this.itemStatValue = 30.0f;
                break;

            //case 4 silver duck gift item
            case 4:
                this.itemStat = "happiness";
                this.itemStatValue = 60.0f;
                break;

            //case 5 golden duck gift item
            case 5:
                this.itemStat = "happiness";
                this.itemStatValue = 90.0f;
                break;
        }
    }

    public void setItemStat(String itemStat){
        this.itemStat = itemStat;
    }

    public String getItemStat(){
        return this.itemStat;
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
}
