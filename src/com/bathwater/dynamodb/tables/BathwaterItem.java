/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bathwater.dynamodb.tables;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

/**
 *
 * @author rajeshk
 */
public interface BathwaterItem {
    
    /**
     * 
     * @param mapper 
     */
    public void saveItem(DynamoDBMapper mapper);
    
    /**
     * 
     * @param mapper 
     */
    public void deleteItem(DynamoDBMapper mapper);
    
}
