package com.kaobells.group44;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class ItemTest {

    @Test
    void setItemValues() {
        Item item = new Item(0, 1);
        item.setItemValues();
        assertEquals(30.0f, item.getItemStatValue(), "Stat value for itemID 0 should be 30.0f");
    }

    @Test
    void getItemStatValue() {
    }

    @Test
    void getItemID() {
    }

    @Test
    void reduceCount() {
    }

    @Test
    void increaseCount() {
    }

    @Test
    void setItemCount() {
    }

    @Test
    void getItemCount() {
    }

    @Test
    void isFood() {
    }

    @Test
    void isGift() {
    }

    @Test
    void getImage() {
    }

    @Test
    void setImage() {
    }
}
