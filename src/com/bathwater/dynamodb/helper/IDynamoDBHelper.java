/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bathwater.dynamodb.helper;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.bathwater.dynamodb.tables.BathwaterItem;

/**
 *
 * @author rajeshk
 */
public interface IDynamoDBHelper {
    
    /**
     * Method to save item in the table
     * @param item
     * @return true if successful, false otherwise
     */
    public boolean putItem(BathwaterItem item);
    
    /**
     * Method to delete item from a table
     * @param item
     * @return true if successful, false otherwise
     */
    public boolean deleteItem(BathwaterItem item);
    
    /**
     * Method to return the mapper object for the current environment
     * @return mapper object
     */
    public DynamoDBMapper getMapper();
    
    /**
     * Method to get dynamoDB object for the current environment
     * @return dynamoDB object
     */
    public DynamoDB getDynamoDB();
    
}
