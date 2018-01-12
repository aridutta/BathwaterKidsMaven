/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bathwater.testdata.inserter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.bathwater.dynamodb.helper.IDynamoDBHelper;
import com.bathwater.dynamodb.helper.impl.DynamoDBHelper;
import com.bathwater.dynamodb.helper.queries.DynamoDBScans;
import com.bathwater.dynamodb.tables.InventoryTableItem;
import com.bathwater.dynamodb.tables.UserTableItem;

/**
 *
 * @author rajeshk
 */
public class ItemsInserter {
    
    public static void main(String[] args) {
        IDynamoDBHelper helper = DynamoDBHelper.getInstance();
        DynamoDBScans scanHelper = new DynamoDBScans();
        
        InventoryTableItem item = new InventoryTableItem();
        item.setItemName("Safety 1st Custom Care Modular Bath Center");
        
        List<String> itemCodes = new ArrayList<>();
        itemCodes.add("30444176abcf");
        item.setItemCode(itemCodes);
        
        item.setDescription("The Custom Care Modular Bath Center by Safety 1st is a sleekly designed tub that offers the flexibility to adapt and grow with a young family through 3 stages, from newborn to baby to toddler. This first truly modular bathing system provides custom support and comfort at every stage of growth. Its unique flexibility makes bath time a nurturing and relaxing time for you and baby.");
        
        List<String> imageURLs = new ArrayList<>();
        imageURLs.add("https://s3.amazonaws.com/bathwater.images.dev/30444176a.jpg");
//        imageURLs.add("https://s3.amazonaws.com/bathwater.images.dev/80497616b.jpg");
        
        item.setImageURLs(imageURLs);
        item.setCredits(15);
        item.setCategoryID("31");
        item.setParentID("6");
        item.setStorageID("1");
        item.setOwnerEmail("admin@bathwaterkids.com");
        item.setOwnerFirstName("BathwaterKids");
        item.setOwnerID("1");
        item.setStorageTimestamp(new Date().toString());
        
        helper.putItem(item);
        
        UserTableItem bathwater = scanHelper.getUsersBasedById("1").get(0);
        UserTableItem.StoredItems storedItem = new UserTableItem.StoredItems();
        storedItem.setCategoryID(item.getCategoryID());
        storedItem.setCredits(item.getCredits());
        storedItem.setDescription(item.getDescription());
        storedItem.setItemID(item.getStoredItemId());
        storedItem.setName(item.getItemName());
        if (bathwater.getStoredItems() == null) {
            bathwater.setStoredItems(new ArrayList<UserTableItem.StoredItems>());
        }
        bathwater.getStoredItems().add(storedItem);
        helper.putItem(bathwater);
        
//        item = new InventoryTableItem();
//        item.setItemName("Baby Cradle Big Polka Dots - Green & White");
//        
//        itemCodes = new ArrayList<>();
//        itemCodes.add("931098");
//        item.setItemCode(itemCodes);
//        
//        item.setDescription("This Cradle is made of high quality fabric and features a mosquito net. Lightweight and versatile, this cradle can easily be moved from room to room. This Cradle requires light assembly.");
//        
//        imageURLs = new ArrayList<>();
//        imageURLs.add("https://s3.amazonaws.com/bathwater.images.dev/931098a.jpg");
//        imageURLs.add("https://s3.amazonaws.com/bathwater.images.dev/931098b.jpg");
//        
//        item.setImageURLs(imageURLs);
//        item.setCredits(50);
//        item.setCategoryID("1");
//        item.setStorageID("1");
//        item.setOwnerEmail("admin@bathwaterkids.com");
//        item.setOwnerFirstName("BathwaterKids");
//        item.setOwnerID("1");
//        item.setStorageTimestamp(new Date().toString());
//        
//        helper.putItem(item);
    }
    
}
