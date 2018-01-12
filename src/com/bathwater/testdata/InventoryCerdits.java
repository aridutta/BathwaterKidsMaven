/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bathwater.testdata;

import java.util.List;
import java.util.Random;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.bathwater.dynamodb.helper.impl.DynamoDBHelper;
import com.bathwater.dynamodb.helper.queries.DynamoDBScans;
import com.bathwater.dynamodb.tables.InventoryTableItem;

/**
 *
 * @author rajeshk
 */
public class InventoryCerdits {
    
    public static void main(String[] args) {
        DynamoDBMapper mapper = DynamoDBHelper.getInstance().getMapper();
        DynamoDBScans scanHelper = new DynamoDBScans();
        List<InventoryTableItem> items = scanHelper.getAllStoredItems();
        
        Random random = new Random();
        for (int i = 0; i < items.size(); i++) {
            InventoryTableItem item = items.get(i);
            item.setCredits(random.nextInt(10) + 10);
            mapper.save(item);
        }
    }
    
}
