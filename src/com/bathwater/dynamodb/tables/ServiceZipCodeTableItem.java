/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bathwater.dynamodb.tables;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

/**
 *
 * @author rajeshk
 */
@DynamoDBTable(tableName = "service_zipcodes")
public class ServiceZipCodeTableItem implements BathwaterItem {
    
    private String zipCode;

    @DynamoDBHashKey(attributeName = "zipcode")
    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    @Override
    public void saveItem(DynamoDBMapper mapper) {
        mapper.save(this);
    }

    @Override
    public void deleteItem(DynamoDBMapper mapper) {
        mapper.delete(this);
    }
    
}
