/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bathwater.dynamodb.helper.queries;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.bathwater.amazons3.JSONDatabaseUtil;
import com.bathwater.dynamodb.helper.IDynamoDBHelper;
import com.bathwater.dynamodb.helper.impl.DynamoDBHelper;
import com.bathwater.dynamodb.tables.CategoryTableItem;
import com.bathwater.dynamodb.tables.DriverTableItem;
import com.bathwater.dynamodb.tables.InventoryTableItem;
import com.bathwater.dynamodb.tables.ItemTableItem;
import com.bathwater.dynamodb.tables.MembershipTableItem;
import com.bathwater.dynamodb.tables.ReferralCodeMapper;
import com.bathwater.dynamodb.tables.RequestZipCodeTableItem;
import com.bathwater.dynamodb.tables.ServiceZipCodeTableItem;
import com.bathwater.dynamodb.tables.TimeslotTableItem;
import com.bathwater.dynamodb.tables.TruckTableItem;
import com.bathwater.dynamodb.tables.UserRequestTableItem;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @author rajeshk
 */
public class DynamoDBQueries {
    
    public List<ServiceZipCodeTableItem> getServiceZipCode(String zipcode) {
        IDynamoDBHelper helper = DynamoDBHelper.getInstance();
        
        DynamoDBMapper mapper = helper.getMapper();
        
        Map<String, AttributeValue> valueMap = new HashMap<>();
        valueMap.put(":zipcode", new AttributeValue().withS(zipcode));
        
        DynamoDBQueryExpression<ServiceZipCodeTableItem> query = new DynamoDBQueryExpression<ServiceZipCodeTableItem>()
                                                .withKeyConditionExpression("zipcode = :zipcode")
                                                .withExpressionAttributeValues(valueMap);
                                                
        List<ServiceZipCodeTableItem> zipCodeList = mapper.query(ServiceZipCodeTableItem.class, query);
        
        return zipCodeList;
    }
    
    public List<RequestZipCodeTableItem> getRequestZipCode(String zipcode) {
        IDynamoDBHelper helper = DynamoDBHelper.getInstance();
        
        DynamoDBMapper mapper = helper.getMapper();
        
        Map<String, AttributeValue> valueMap = new HashMap<>();
        valueMap.put(":zipcode", new AttributeValue().withS(zipcode));
        
        DynamoDBQueryExpression<RequestZipCodeTableItem> query = new DynamoDBQueryExpression<RequestZipCodeTableItem>()
                                                .withKeyConditionExpression("zipcode = :zipcode")
                                                .withExpressionAttributeValues(valueMap);
                                                
        List<RequestZipCodeTableItem> zipCodeList = mapper.query(RequestZipCodeTableItem.class, query);
        
        return zipCodeList;
    }
    
    public List<MembershipTableItem> getMembershipPlanById(String membershipID) {
        InputStream in = JSONDatabaseUtil.downloadFile("memberships.json");
        JSONParser parser = new JSONParser();
        List<MembershipTableItem> list = new ArrayList<>();
        try {
            JSONArray jsonArray = (JSONArray) parser.parse(new InputStreamReader(in));
            ObjectMapper mapper = new ObjectMapper();
            
            for (Iterator it = jsonArray.iterator(); it.hasNext();) {
                JSONObject obj = (JSONObject) it.next();
                MembershipTableItem item = mapper.readValue(obj.toJSONString(), MembershipTableItem.class);
                if (item.getMembershipID().equals(membershipID))
                list.add(item);
            }
            
        } catch (IOException | ParseException ex) {
            return null;
        }
        
        return list;
    }
    
    public List<ItemTableItem> getItemByID(String itemID) {
        DynamoDBMapper mapper = DynamoDBHelper.getInstance().getMapper();
        Map<String, AttributeValue> valueMap = new HashMap<>();
        valueMap.put(":itemID", new AttributeValue().withS(itemID));
        
        DynamoDBQueryExpression<ItemTableItem> query = new DynamoDBQueryExpression<ItemTableItem>()
                .withKeyConditionExpression("itemID = :itemID")
                .withExpressionAttributeValues(valueMap);
        
        return mapper.query(ItemTableItem.class, query);
    }
    
    public List<TimeslotTableItem> getTimeSlotByID(String timeSlotID) {
        DynamoDBMapper mapper = DynamoDBHelper.getInstance().getMapper();
        Map<String, AttributeValue> valueMap = new HashMap<>();
        valueMap.put(":tid", new AttributeValue().withS(timeSlotID));
        
        DynamoDBQueryExpression<TimeslotTableItem> query = new DynamoDBQueryExpression<TimeslotTableItem>()
                .withKeyConditionExpression("timeslotID = :tid")
                .withExpressionAttributeValues(valueMap);
        
        return mapper.query(TimeslotTableItem.class, query);
    }
    
    public TruckTableItem getTruckByID(String truckID) {
        DynamoDBMapper mapper = DynamoDBHelper.getInstance().getMapper();
        Map<String, AttributeValue> valueMap = new HashMap<>();
        valueMap.put(":truckID", new AttributeValue().withS(truckID));
        
        DynamoDBQueryExpression<TruckTableItem> query = new DynamoDBQueryExpression<TruckTableItem>()
                .withKeyConditionExpression("truckID = :truckID")
                .withExpressionAttributeValues(valueMap);
        
        List<TruckTableItem> truckList = mapper.query(TruckTableItem.class, query);
        
        if (truckList == null || truckList.isEmpty())
            return null;
        
        return truckList.get(0);
    }
    
    public ReferralCodeMapper getReferralCodeMapperByID(String referralCode) {
        DynamoDBMapper mapper = DynamoDBHelper.getInstance().getMapper();
        Map<String, AttributeValue> valueMap = new HashMap<>();
        valueMap.put(":id", new AttributeValue().withS(referralCode));
        
        DynamoDBQueryExpression<ReferralCodeMapper> query = new DynamoDBQueryExpression<ReferralCodeMapper>()
                .withKeyConditionExpression("referralCode = :id")
                .withExpressionAttributeValues(valueMap);
        
        List<ReferralCodeMapper> list = mapper.query(ReferralCodeMapper.class, query);
        
        if (list == null || list.isEmpty())
            return null;
        
        return list.get(0);
    }
    
    public List<InventoryTableItem> getStoredItemsByID(String id) {
        DynamoDBMapper mapper = DynamoDBHelper.getInstance().getMapper();
        Map<String, AttributeValue> valueMap = new HashMap<>();
        valueMap.put(":id", new AttributeValue().withS(id));
        
        DynamoDBQueryExpression<InventoryTableItem> query = new DynamoDBQueryExpression<InventoryTableItem>()
                .withKeyConditionExpression("storedItemId = :id")
                .withExpressionAttributeValues(valueMap);
        
        return mapper.query(InventoryTableItem.class, query);
    }
    
    public List<DriverTableItem> getDriverByID(String id) {
        DynamoDBMapper mapper = DynamoDBHelper.getInstance().getMapper();
        Map<String, AttributeValue> valueMap = new HashMap<>();
        valueMap.put(":id", new AttributeValue().withS(id));
        
        DynamoDBQueryExpression<DriverTableItem> query = new DynamoDBQueryExpression<DriverTableItem>()
                .withKeyConditionExpression("driverID = :id")
                .withExpressionAttributeValues(valueMap);
        
        return mapper.query(DriverTableItem.class, query);
    }

        
    public List<CategoryTableItem> getCategoryById(String id) {
        DynamoDBMapper mapper = DynamoDBHelper.getInstance().getMapper();
        Map<String, AttributeValue> valueMap = new HashMap<>();
        valueMap.put(":id", new AttributeValue().withS(id));
        
        DynamoDBQueryExpression<CategoryTableItem> query = new DynamoDBQueryExpression<CategoryTableItem>()
                .withKeyConditionExpression("categoryID = :id")
                .withExpressionAttributeValues(valueMap);
        
        return mapper.query(CategoryTableItem.class, query);
    }
    
    public List<UserRequestTableItem> getUserRequestByID(String id) {
        DynamoDBMapper mapper = DynamoDBHelper.getInstance().getMapper();
        Map<String, AttributeValue> valueMap = new HashMap<>();
        valueMap.put(":id", new AttributeValue().withS(id));
        
        DynamoDBQueryExpression<UserRequestTableItem> query = new DynamoDBQueryExpression<UserRequestTableItem>()
                .withKeyConditionExpression("userrequestID = :id")
                .withExpressionAttributeValues(valueMap);
        
        return mapper.query(UserRequestTableItem.class, query);
    }
    
    public List<String> getItemConditionsConfiguration() {
        InputStream in = JSONDatabaseUtil.downloadFile("configuration.json");
        JSONParser parser = new JSONParser();
        try {
            JSONArray jsonArray = (JSONArray) parser.parse(new InputStreamReader(in));
            for (Iterator it = jsonArray.iterator(); it.hasNext();) {
                JSONObject obj = (JSONObject) it.next();
                String key = (String) obj.get("key");
                if (key.equals("itemConditions")) {
                    List<String> values = (List<String>) obj.get("values");
                    return values;
                }
            }
        } catch (IOException | org.json.simple.parser.ParseException ex) {
            
        }
        return null;
    }
    
}
