package com.kaobells.group44;

public class Item {
    public int itemID;  // Item identifier
    public String itemStat; // Stat that Item will change
    public float itemStatValue; //Float for calculating how much item will change stat

    public Item(String ItemID){ //Constructor
    this.itemID = itemID;
    setItemValues(itemID);
    }

    public void setItemValues(int ItemID){ //based on item ID fills in stats
        switch (ItemID){
            //case 0 cup noodle food item
            case 0:
                this.itemStat = "fullness";
                this.itemStatValue = 1.0f;
                break;

            //case 1 spoke bagel food item
            case 1:
                this.itemStat = "fullness";
                this.itemStatValue = 2.0f;
                break;

            //case 2 shawarma food item
            case 2:
                this.itemStat = "fullness";
                this.itemStatValue = 3.0f;
                break;

            //case 3 normal duck gift item
            case 3:
                this.itemStat = "happiness";
                this.itemStatValue = 1.0f;
                break;

            //case 4 cool duck gift item
            case 4:
                this.itemStat = "happiness";
                this.itemStatValue = 2.0f;
                break;

            //case 5 super cool duck gift item
            case 5:
                this.itemStat = "happiness";
                this.itemStatValue = 3.0f;
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
}
