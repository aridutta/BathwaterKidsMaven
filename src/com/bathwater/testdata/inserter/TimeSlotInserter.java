/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bathwater.testdata.inserter;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.bathwater.dynamodb.helper.IDynamoDBHelper;
import com.bathwater.dynamodb.helper.impl.DynamoDBHelper;
import com.bathwater.dynamodb.tables.TimeslotTableItem;

/**
 *
 * @author rajeshk
 */
public class TimeSlotInserter {
    
    public static void main(String[] args) {
        
        IDynamoDBHelper helper = DynamoDBHelper.getInstance();
        
        TimeslotTableItem item = new TimeslotTableItem();
        item.setDate(new SimpleDateFormat("MM.dd.yyyy").format(new Date()));
        item.setTimeslot("9am - 11am");
        helper.putItem(item);
        
        item = new TimeslotTableItem();
        item.setDate(new SimpleDateFormat("MM.dd.yyyy").format(new Date()));
        item.setTimeslot("12pm - 5pm");
        helper.putItem(item);
        
    }
    
}
