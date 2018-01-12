/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bathwater.dynamodb.tables;

import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

/**
 *
 * @author rajeshk
 */
@DynamoDBTable(tableName = "referralCodeMapper")
public class ReferralCodeMapper implements BathwaterItem {
    
    private String referralCode;
    
    private List<String> usedUserIDs;

    @DynamoDBHashKey(attributeName = "referralCode")
    public String getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }

    @DynamoDBAttribute(attributeName = "usedUserIDs")
    public List<String> getUsedUserIDs() {
        return usedUserIDs;
    }

    public void setUsedUserIDs(List<String> usedUserIDs) {
        this.usedUserIDs = usedUserIDs;
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
