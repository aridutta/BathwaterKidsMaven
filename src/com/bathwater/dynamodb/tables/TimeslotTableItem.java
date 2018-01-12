/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bathwater.dynamodb.tables;

import java.util.Objects;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

/**
 *
 * @author rajeshk
 */
@DynamoDBTable(tableName = "timeslots")
public class TimeslotTableItem implements BathwaterItem {
    
    private String timeslotID;
    
    private String date;
    
    private String timeslot;
    
    private Integer availabilityCount;

    @DynamoDBHashKey(attributeName = "timeslotID")
    @DynamoDBAutoGeneratedKey
    public String getTimeslotID() {
        return timeslotID;
    }

    public void setTimeslotID(String timeslotID) {
        this.timeslotID = timeslotID;
    }

    @DynamoDBAttribute(attributeName = "date")
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @DynamoDBAttribute(attributeName = "timeslot")
    public String getTimeslot() {
        return timeslot;
    }

    public void setTimeslot(String timeslot) {
        this.timeslot = timeslot;
    }

    @DynamoDBAttribute(attributeName = "availabilityCount")
    public Integer getAvailabilityCount() {
        return availabilityCount;
    }

    public void setAvailabilityCount(Integer availabilityCount) {
        this.availabilityCount = availabilityCount;
    }

    @Override
    public void saveItem(DynamoDBMapper mapper) {
        mapper.save(this);
    }

    @Override
    public void deleteItem(DynamoDBMapper mapper) {
        mapper.delete(this);
    }
    
    public boolean isSame(String date, String time) {
        return date.equals(this.date) && time.equals(this.timeslot);
    }

    @Override
    public boolean equals(Object obj) {
        TimeslotTableItem otherDate = (TimeslotTableItem) obj;
        return otherDate.getDate().equals(this.date) && otherDate.getTimeslot().equals(this.timeslot);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 31 * hash + Objects.hashCode(this.date);
        hash = 31 * hash + Objects.hashCode(this.timeslot);
        return hash;
    }
    
    
}