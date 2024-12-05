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
        Item item = new Item(2, 1);
        item.setItemValues();
        assertEquals(90.0f, item.getItemStatValue(), "Stat value for itemID 2 should be 90.0f");
    }

    @Test
    void getItemID() {
        Item item = new Item(3, 1);
        assertEquals(3, item.getItemID(), "Item ID should be 3");
    }

    @Test
    void reduceCount() {
        Item item = new Item(1, 2);
        assertTrue(item.reduceCount(), "Reduce count should return true when itemCount > 0");
        assertEquals(1, item.getItemCount(), "Item count should decrease by 1");
        item.reduceCount();
        assertFalse(item.reduceCount(), "Reduce count should return false when itemCount <= 0");
    }

    @Test
    void increaseCount() {
        Item item = new Item(1, 2);
        item.increaseCount();
        assertEquals(3, item.getItemCount(), "Item count should increase by 1");
    }

    @Test
    void setItemCount() {
        Item item = new Item(1, 0);
        item.setItemCount(5);
        assertEquals(5, item.getItemCount(), "Item count should be set to 5");
    }

    @Test
    void getItemCount() {
        Item item = new Item(1, 3);
        assertEquals(3, item.getItemCount(), "Item count should be 3");
    }

    @Test
    void isFood() {
        Item item = new Item(0, 1);
        assertTrue(item.isFood(), "Item with ID 0 should be food");
        item = new Item(3, 1);
        assertFalse(item.isFood(), "Item with ID 3 should not be food");
    }

    @Test
    void isGift() {
        Item item = new Item(3, 1);
        assertTrue(item.isGift(), "Item with ID 3 should be a gift");
        item = new Item(0, 1);
        assertFalse(item.isGift(), "Item with ID 0 should not be a gift");
    }
}
