/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bathwater.dynamodb.tables;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

/**
 *
 * @author rajeshk
 */
@DynamoDBTable(tableName = "memberships")
public class MembershipTableItem implements BathwaterItem {
    
    private String membershipID;
    
    private Float price;
    
    private String interval;
    
    private String name;
    
    private String description;
    
    private Integer kickOffPoints;
    
    private String stripePlanID;

    @DynamoDBHashKey(attributeName = "membershipID")
    @DynamoDBAutoGeneratedKey
    public String getMembershipID() {
        return membershipID;
    }

    public void setMembershipID(String membershipID) {
        this.membershipID = membershipID;
    }

    @DynamoDBAttribute(attributeName = "price")
    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    @DynamoDBAttribute(attributeName = "interval")
    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    @DynamoDBAttribute(attributeName = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @DynamoDBAttribute(attributeName = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }   

    @DynamoDBAttribute(attributeName = "kickOffPoints")
    public Integer getKickOffPoints() {
        return kickOffPoints;
    }

    public void setKickOffPoints(Integer kickOffPoints) {
        this.kickOffPoints = kickOffPoints;
    }

    public String getStripePlanID() {
        return stripePlanID;
    }

    public void setStripePlanID(String stripePlanID) {
        this.stripePlanID = stripePlanID;
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
