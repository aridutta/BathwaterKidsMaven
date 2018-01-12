/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bathwater.dynamodb.helper.queries;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.bathwater.amazons3.JSONDatabaseUtil;
import com.bathwater.dynamodb.helper.IDynamoDBHelper;
import com.bathwater.dynamodb.helper.impl.DynamoDBHelper;
import com.bathwater.dynamodb.tables.CategoryTableItem;
import com.bathwater.dynamodb.tables.ConfigurationTableItem;
import com.bathwater.dynamodb.tables.DriverTableItem;
import com.bathwater.dynamodb.tables.DriverTruckHistoryTableItem;
import com.bathwater.dynamodb.tables.InventoryTableItem;
import com.bathwater.dynamodb.tables.ItemTableItem;
import com.bathwater.dynamodb.tables.MembershipTableItem;
import com.bathwater.dynamodb.tables.OAuthTableItem;
import com.bathwater.dynamodb.tables.ServiceZipCodeTableItem;
import com.bathwater.dynamodb.tables.StorageTableItem;
import com.bathwater.dynamodb.tables.TimeslotTableItem;
import com.bathwater.dynamodb.tables.TruckTableItem;
import com.bathwater.dynamodb.tables.UserRequestTableItem;
import com.bathwater.dynamodb.tables.UserTableItem;
import com.bathwater.util.StringUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @author rajeshk
 */
public class DynamoDBScans {

    public List<UserTableItem> getUsersBasedOnEmail(String name) {
        IDynamoDBHelper helper = DynamoDBHelper.getInstance();

        DynamoDBMapper mapper = helper.getMapper();

        Map<String, String> nameMap = new HashMap<>();
        nameMap.put("#email", "emailAddress");

        Map<String, AttributeValue> valueMap = new HashMap<>();
        valueMap.put(":email", new AttributeValue().withS(name));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("#email = :email")
                .withExpressionAttributeNames(nameMap)
                .withExpressionAttributeValues(valueMap);

        List<UserTableItem> userList = mapper.scan(UserTableItem.class, scanExpression);

        return userList;
    }

    public List<UserTableItem> getUsersBasedOnReferralCode(String referralCode) {
        IDynamoDBHelper helper = DynamoDBHelper.getInstance();

        DynamoDBMapper mapper = helper.getMapper();

        Map<String, String> nameMap = new HashMap<>();
        nameMap.put("#code", "referralCode");

        Map<String, AttributeValue> valueMap = new HashMap<>();
        valueMap.put(":code", new AttributeValue().withS(referralCode));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("#code = :code")
                .withExpressionAttributeNames(nameMap)
                .withExpressionAttributeValues(valueMap);

        List<UserTableItem> userList = mapper.scan(UserTableItem.class, scanExpression);

        return userList;
    }

    public List<OAuthTableItem> isAdminExists(String email) {
        IDynamoDBHelper helper = DynamoDBHelper.getInstance();
        DynamoDBMapper mapper = helper.getMapper();
        Map<String, AttributeValue> valueMap = new HashMap<>();
        valueMap.put(":email", new AttributeValue().withS(email));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("email = :email")
                .withExpressionAttributeValues(valueMap);

        return mapper.scan(OAuthTableItem.class, scanExpression);
    }

    public List<OAuthTableItem> getAdmins() {
        DynamoDBMapper mapper = DynamoDBHelper.getInstance().getMapper();
        return mapper.scan(OAuthTableItem.class, new DynamoDBScanExpression());

    }

    public List<OAuthTableItem> getAdmins(String role) {
        IDynamoDBHelper helper = DynamoDBHelper.getInstance();
        DynamoDBMapper mapper = helper.getMapper();
        Map<String, AttributeValue> valueMap = new HashMap<>();
        valueMap.put(":role", new AttributeValue().withS("10"));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("role != :role")
                .withExpressionAttributeValues(valueMap);

        return mapper.scan(OAuthTableItem.class, scanExpression);

    }

    public List<UserTableItem> getUsersBasedById(String userId) {
        IDynamoDBHelper helper = DynamoDBHelper.getInstance();

        DynamoDBMapper mapper = helper.getMapper();

        Map<String, String> nameMap = new HashMap<>();
        nameMap.put("#userid", "userID");

        Map<String, AttributeValue> valueMap = new HashMap<>();
        valueMap.put(":userid", new AttributeValue().withS(userId));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("#userid = :userid")
                .withExpressionAttributeNames(nameMap)
                .withExpressionAttributeValues(valueMap);

        List<UserTableItem> userList = mapper.scan(UserTableItem.class, scanExpression);

        return userList;
    }

    public List<InventoryTableItem> getBathwaterItems(String filterID) {
        DynamoDBMapper mapper = DynamoDBHelper.getInstance().getMapper();

        Map<String, String> nameMap = new HashMap<>();
        nameMap.put("#ownerID", "ownerID");

        Map<String, AttributeValue> valueMap = new HashMap<>();
        valueMap.put(":id", new AttributeValue().withS("1"));

        String filterExpression = "#ownerID=:id";

        if (filterID != null && !filterID.equals("")) {
            nameMap.put("#filterID", "categoryID");
            valueMap.put(":filterID", new AttributeValue().withS(filterID));
            filterExpression = "#ownerID=:id and #filterID=:filterID";
        }

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression(filterExpression)
                .withExpressionAttributeNames(nameMap)
                .withExpressionAttributeValues(valueMap);

        return mapper.scan(InventoryTableItem.class, scanExpression);
    }

    public List<InventoryTableItem> getBathwaterItems() {
        DynamoDBMapper mapper = DynamoDBHelper.getInstance().getMapper();

        Map<String, String> nameMap = new HashMap<>();
        nameMap.put("#ownerID", "ownerID");

        Map<String, AttributeValue> valueMap = new HashMap<>();
        valueMap.put(":id", new AttributeValue().withS("1"));

        String filterExpression = "#ownerID=:id";

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression(filterExpression)
                .withExpressionAttributeNames(nameMap)
                .withExpressionAttributeValues(valueMap);

        return mapper.scan(InventoryTableItem.class, scanExpression);
    }

    public List<InventoryTableItem> getBinByBinNumber(String binNumber) {
        DynamoDBMapper mapper = DynamoDBHelper.getInstance().getMapper();

        Map<String, String> nameMap = new HashMap<>();
        nameMap.put("#binNumber", "binNumber");

        Map<String, AttributeValue> valueMap = new HashMap<>();
        valueMap.put(":binNumber", new AttributeValue().withS(binNumber));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("#binNumber = :binNumber")
                .withExpressionAttributeNames(nameMap)
                .withExpressionAttributeValues(valueMap);

        return mapper.scan(InventoryTableItem.class, scanExpression);
    }

    public List<TimeslotTableItem> getTimeslotsByDate(String date) {
        DynamoDBMapper mapper = DynamoDBHelper.getInstance().getMapper();

        Map<String, String> nameMap = new HashMap<>();
        nameMap.put("#date", "date");

        Map<String, AttributeValue> valueMap = new HashMap<>();
        valueMap.put(":date", new AttributeValue().withS(date));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("#date = :date")
                .withExpressionAttributeNames(nameMap)
                .withExpressionAttributeValues(valueMap);

        return mapper.scan(TimeslotTableItem.class, scanExpression);
    }

    public List<TimeslotTableItem> getTimeslotsByTimeAndDate(String date, String timeslot) {
        DynamoDBMapper mapper = DynamoDBHelper.getInstance().getMapper();

        Map<String, String> nameMap = new HashMap<>();
        nameMap.put("#date", "date");
        nameMap.put("#timeslot", "timeslot");

        Map<String, AttributeValue> valueMap = new HashMap<>();
        valueMap.put(":date", new AttributeValue().withS(date));
        valueMap.put(":timeslot", new AttributeValue().withS(timeslot));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("#date = :date and #timeslot = :timeslot")
                .withExpressionAttributeNames(nameMap)
                .withExpressionAttributeValues(valueMap);

        return mapper.scan(TimeslotTableItem.class, scanExpression);
    }

    public List<ItemTableItem> getItemsByCategory(String categoryID) {
        DynamoDBMapper mapper = DynamoDBHelper.getInstance().getMapper();
        Map<String, String> nameMap = new HashMap<>();
        nameMap.put("#catID", "categoryID");

        Map<String, AttributeValue> valueMap = new HashMap<>();
        valueMap.put(":catID", new AttributeValue().withS(categoryID));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("#catID = :catID")
                .withExpressionAttributeNames(nameMap)
                .withExpressionAttributeValues(valueMap);

        return mapper.scan(ItemTableItem.class, scanExpression);
    }

    public List<InventoryTableItem> getItemByUserID(String userID) {
        DynamoDBMapper mapper = DynamoDBHelper.getInstance().getMapper();
        Map<String, String> nameMap = new HashMap<>();
        nameMap.put("#uid", "ownerID");
        nameMap.put("#status", "status.status");

        Map<String, AttributeValue> valueMap = new HashMap<>();
        valueMap.put(":uid", new AttributeValue().withS(userID));
        valueMap.put(":status", new AttributeValue().withS("dropped off"));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("#uid = :uid AND #status <> :status")
                .withExpressionAttributeNames(nameMap)
                .withExpressionAttributeValues(valueMap);

        return mapper.scan(InventoryTableItem.class, scanExpression);
    }

    public List<InventoryTableItem> getSharingItems() {
        DynamoDBMapper mapper = DynamoDBHelper.getInstance().getMapper();
        Map<String, String> nameMap = new HashMap<>();
        nameMap.put("#sharable", "sharable");

        Map<String, AttributeValue> valueMap = new HashMap<>();
        valueMap.put(":sharable", new AttributeValue().withN("1"));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("#sharable = :sharable")
                .withExpressionAttributeNames(nameMap)
                .withExpressionAttributeValues(valueMap);

        return mapper.scan(InventoryTableItem.class, scanExpression);
    }

    public List<InventoryTableItem> getAllStoredItems() {
        DynamoDBMapper mapper = DynamoDBHelper.getInstance().getMapper();
        return mapper.scan(InventoryTableItem.class, new DynamoDBScanExpression());
    }

    public List<InventoryTableItem> getStoredItemsByStatus(String status) {
        DynamoDBMapper mapper = DynamoDBHelper.getInstance().getMapper();
        Map<String, AttributeValue> valueMap = new HashMap<>();
        valueMap.put(":id", new AttributeValue().withS(status));

        Map<String, String> nameMap = new HashMap<>();
        nameMap.put("#item_status", "status");

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("#item_status = :id")
                .withExpressionAttributeNames(nameMap)
                .withExpressionAttributeValues(valueMap);
                
                
                
//        DynamoDBScanExpression<InventoryTableItem> scanExpression = new DynamoDBScanExpression<InventoryTableItem>()
//                .withFilterConditionEntry("#item_status = :id")
//                .withExpressionAttributeValues(valueMap).
//                withExpressionAttributeNames(nameMap);

        return mapper.scan(InventoryTableItem.class, scanExpression);
    }

    public List<InventoryTableItem> getItemByItemCode(String itemCode) {
        DynamoDBMapper mapper = DynamoDBHelper.getInstance().getMapper();
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterConditionEntry("itemCode", new Condition()
                        .withAttributeValueList(new AttributeValue().withS(itemCode))
                        .withComparisonOperator(ComparisonOperator.CONTAINS));

        return mapper.scan(InventoryTableItem.class, scanExpression);
    }

    public List<MembershipTableItem> getAllPlans() {
        InputStream in = JSONDatabaseUtil.downloadFile("memberships.json");
        JSONParser parser = new JSONParser();
        try {
            JSONArray jsonArray = (JSONArray) parser.parse(new InputStreamReader(in));
            ObjectMapper mapper = new ObjectMapper();
            List<MembershipTableItem> list = new ArrayList<>();
            for (Iterator it = jsonArray.iterator(); it.hasNext();) {
                JSONObject obj = (JSONObject) it.next();
                MembershipTableItem item = mapper.readValue(obj.toJSONString(), MembershipTableItem.class);
                list.add(item);
            }
            return list;
        } catch (IOException | org.json.simple.parser.ParseException ex) {
            return null;
        }
    }

    public List<StorageTableItem> getAllStorages() {
        InputStream in = JSONDatabaseUtil.downloadFile("storages.json");
        JSONParser parser = new JSONParser();
        try {
            JSONArray jsonArray = (JSONArray) parser.parse(new InputStreamReader(in));
            ObjectMapper mapper = new ObjectMapper();
            List<StorageTableItem> list = new ArrayList<>();
            for (Iterator it = jsonArray.iterator(); it.hasNext();) {
                JSONObject obj = (JSONObject) it.next();
                StorageTableItem item = mapper.readValue(obj.toJSONString(), StorageTableItem.class);
                list.add(item);
            }
            return list;
        } catch (IOException | org.json.simple.parser.ParseException ex) {
            return null;
        }
    }

    public List<ItemTableItem> getAllItems() {
        DynamoDBMapper mapper = DynamoDBHelper.getInstance().getMapper();
        return mapper.scan(ItemTableItem.class, new DynamoDBScanExpression());
    }

    public List<TimeslotTableItem> getAllTimeslots() {
        DynamoDBMapper mapper = DynamoDBHelper.getInstance().getMapper();
        return mapper.scan(TimeslotTableItem.class, new DynamoDBScanExpression());
    }

    public List<TimeslotTableItem> getAvailableTimeslots() throws ParseException {

        List<TimeslotTableItem> allTimeSlots = getAllTimeslots();

        List<TimeslotTableItem> availableTimeslots = new ArrayList<>();

        Date today = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT-05:00"));
        cal.setTime(today);
        cal.add(Calendar.DATE, -1);
        Date tomorrow = cal.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("MM.dd.yyyy");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT-05:00"));

        for (TimeslotTableItem allTimeSlot : allTimeSlots) {
            Date timeslotDate = formatter.parse(allTimeSlot.getDate());
            if (!timeslotDate.before(tomorrow) && allTimeSlot.getAvailabilityCount() >= 1) {
                availableTimeslots.add(allTimeSlot);
            }
        }

        return availableTimeslots;
    }

    public List<CategoryTableItem> getCategories(String parentID) {
        InputStream in = JSONDatabaseUtil.downloadFile("categories.json");
        JSONParser parser = new JSONParser();
        try {
            JSONArray jsonArray = (JSONArray) parser.parse(new InputStreamReader(in));
            ObjectMapper mapper = new ObjectMapper();
            List<CategoryTableItem> list = new ArrayList<>();
            for (Iterator it = jsonArray.iterator(); it.hasNext();) {
                JSONObject obj = (JSONObject) it.next();
                CategoryTableItem item = mapper.readValue(obj.toJSONString(), CategoryTableItem.class);
                if (StringUtil.isBlank(parentID) || item.getParentID().equals(parentID)) {
                    list.add(item);
                }
            }
            return list;
        } catch (IOException | org.json.simple.parser.ParseException ex) {
            return null;
        }
    }

    public List<UserRequestTableItem> getUserRequestsByDate(String status,String type,String date) {
        DynamoDBMapper mapper = DynamoDBHelper.getInstance().getMapper();
        Map<String, String> statusMap = new HashMap<>();
        statusMap.put("#status", "status");
        statusMap.put("#date", "date");
        statusMap.put("#type", "type");

        Map<String, AttributeValue> valueMap = new HashMap<>();
        valueMap.put(":status", new AttributeValue().withS(status));
        valueMap.put(":date", new AttributeValue().withS(date));
        valueMap.put(":type", new AttributeValue().withS(type));

        if (status == null || status.length() < 1) {
            return mapper.scan(UserRequestTableItem.class, new DynamoDBScanExpression());
        }
        return mapper.scan(UserRequestTableItem.class, new DynamoDBScanExpression().
                withFilterExpression("#status=:status AND #date=:date AND #type=:type").withExpressionAttributeNames(statusMap).withExpressionAttributeValues(valueMap));
    }
    
    public List<UserRequestTableItem> getDropoffUserRequestsByDate(String status,String type,String date) {
        DynamoDBMapper mapper = DynamoDBHelper.getInstance().getMapper();
        Map<String, String> statusMap = new HashMap<>();
        statusMap.put("#status", "status");
        statusMap.put("#type","type");
        statusMap.put("#date", "date");

        Map<String, AttributeValue> valueMap = new HashMap<>();
        valueMap.put(":status", new AttributeValue().withS(status));
        valueMap.put(":type", new AttributeValue().withS(type));
        valueMap.put(":date", new AttributeValue().withS(date));

        if (status == null || status.length() < 1) {
            return mapper.scan(UserRequestTableItem.class, new DynamoDBScanExpression());
        }
        return mapper.scan(UserRequestTableItem.class, new DynamoDBScanExpression().
                withFilterExpression("#status=:status AND #date=:date AND #type=:type").withExpressionAttributeNames(statusMap).withExpressionAttributeValues(valueMap));
    }
    
    public List<UserRequestTableItem> getAllUserRequests(String status) {
        DynamoDBMapper mapper = DynamoDBHelper.getInstance().getMapper();
        Map<String, String> statusMap = new HashMap<>();
        statusMap.put("#status", "status");

        Map<String, AttributeValue> valueMap = new HashMap<>();
        valueMap.put(":status", new AttributeValue().withS(status));

        if (status == null || status.length() < 1) {
            return mapper.scan(UserRequestTableItem.class, new DynamoDBScanExpression());
        }
        return mapper.scan(UserRequestTableItem.class, new DynamoDBScanExpression().
                withFilterExpression("#status=:status").withExpressionAttributeNames(statusMap).withExpressionAttributeValues(valueMap));
    }

    public int getBookedCountForTimeslot(String date, String time) {
        DynamoDBMapper mapper = DynamoDBHelper.getInstance().getMapper();

        Map<String, String> nameMap = new HashMap<>();
        nameMap.put("#date", "date");
        nameMap.put("#time", "time");

        Map<String, AttributeValue> valueMap = new HashMap<>();
        valueMap.put(":date", new AttributeValue().withS(date));
        valueMap.put(":time", new AttributeValue().withS(time));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("#date = :date and #time = :time")
                .withExpressionAttributeNames(nameMap)
                .withExpressionAttributeValues(valueMap);

        List<UserRequestTableItem> list = mapper.scan(UserRequestTableItem.class, scanExpression);

        if (list != null) {
            return list.size();
        }

        return 0;
    }

    public List<UserTableItem> getAllUsers() {
        DynamoDBMapper mapper = DynamoDBHelper.getInstance().getMapper();
        return mapper.scan(UserTableItem.class, new DynamoDBScanExpression());
    }

    public List<ServiceZipCodeTableItem> getAllZipCodes() {
        DynamoDBMapper mapper = DynamoDBHelper.getInstance().getMapper();
        return mapper.scan(ServiceZipCodeTableItem.class, new DynamoDBScanExpression());
    }

    public List<DriverTableItem> getAllDrivers() {
        DynamoDBMapper mapper = DynamoDBHelper.getInstance().getMapper();
        return mapper.scan(DriverTableItem.class, new DynamoDBScanExpression());
    }

    public List<DriverTableItem> getDriverById(String driverID) {
        DynamoDBMapper mapper = DynamoDBHelper.getInstance().getMapper();
        Map<String, String> nameMap = new HashMap<>();
        nameMap.put("#id", "driverID");

        Map<String, AttributeValue> valueMap = new HashMap<>();
        valueMap.put(":id", new AttributeValue().withS(driverID));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("#id = :id")
                .withExpressionAttributeNames(nameMap)
                .withExpressionAttributeValues(valueMap);

        return mapper.scan(DriverTableItem.class, scanExpression);
    }

    public List<DriverTableItem> getDriverByEmail(String email) {
        DynamoDBMapper mapper = DynamoDBHelper.getInstance().getMapper();
        Map<String, String> nameMap = new HashMap<>();
        nameMap.put("#id", "emailAddress");

        Map<String, AttributeValue> valueMap = new HashMap<>();
        valueMap.put(":id", new AttributeValue().withS(email));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("#id = :id")
                .withExpressionAttributeNames(nameMap)
                .withExpressionAttributeValues(valueMap);

        return mapper.scan(DriverTableItem.class, scanExpression);
    }

    public List<DriverTableItem> getDriverByLicenseID(String licenseID) {
        DynamoDBMapper mapper = DynamoDBHelper.getInstance().getMapper();
        Map<String, String> nameMap = new HashMap<>();
        nameMap.put("#id", "licenseID");

        Map<String, AttributeValue> valueMap = new HashMap<>();
        valueMap.put(":id", new AttributeValue().withS(licenseID));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("#id = :id")
                .withExpressionAttributeNames(nameMap)
                .withExpressionAttributeValues(valueMap);

        return mapper.scan(DriverTableItem.class, scanExpression);
    }

    public List<TruckTableItem> getAllTrucks() {
        DynamoDBMapper mapper = DynamoDBHelper.getInstance().getMapper();
        return mapper.scan(TruckTableItem.class, new DynamoDBScanExpression());
    }

    public List<TruckTableItem> getTruckById(String truckID) {
        DynamoDBMapper mapper = DynamoDBHelper.getInstance().getMapper();
        Map<String, String> nameMap = new HashMap<>();
        nameMap.put("#id", "truckID");

        Map<String, AttributeValue> valueMap = new HashMap<>();
        valueMap.put(":id", new AttributeValue().withS(truckID));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("#id = :id")
                .withExpressionAttributeNames(nameMap)
                .withExpressionAttributeValues(valueMap);

        return mapper.scan(TruckTableItem.class, scanExpression);
    }

    public List<UserRequestTableItem> getUserRequestsById(String userRequestID) {
        DynamoDBMapper mapper = DynamoDBHelper.getInstance().getMapper();
        Map<String, String> nameMap = new HashMap<>();
        nameMap.put("#id", "userrequestID");

        Map<String, AttributeValue> valueMap = new HashMap<>();
        valueMap.put(":id", new AttributeValue().withS(userRequestID));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("#id = :id")
                .withExpressionAttributeNames(nameMap)
                .withExpressionAttributeValues(valueMap);

        return mapper.scan(UserRequestTableItem.class, scanExpression);
    }

    public List<UserRequestTableItem> getTodaysUserRequestsByDriverID(String driverID) {
        DynamoDBMapper mapper = DynamoDBHelper.getInstance().getMapper();

        SimpleDateFormat formatter = new SimpleDateFormat("MM.dd.yyyy");
        String today = formatter.format(new Date());

        Map<String, AttributeValue> valueMap = new HashMap<>();
        valueMap.put(":id", new AttributeValue().withS(driverID));
        valueMap.put(":today", new AttributeValue().withS(today));

        Map<String, String> nameMap = new HashMap<>();
//        nameMap.put("#id", "driver.driverID");
        nameMap.put("#date", "date");

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("#date = :today and driver.driverID = :id")
                .withExpressionAttributeValues(valueMap)
                .withExpressionAttributeNames(nameMap);

        return mapper.scan(UserRequestTableItem.class, scanExpression);
    }

    public List<UserRequestTableItem> getTodaysCheckIns() {
        DynamoDBMapper mapper = DynamoDBHelper.getInstance().getMapper();

        SimpleDateFormat formatter = new SimpleDateFormat("MM.dd.yyyy");
        String today = formatter.format(new Date());

        Map<String, AttributeValue> valueMap = new HashMap<>();
        valueMap.put(":type", new AttributeValue().withS("pickup"));
        valueMap.put(":today", new AttributeValue().withS(today));
        valueMap.put(":status", new AttributeValue().withS("completed"));

        Map<String, String> nameMap = new HashMap<>();
        nameMap.put("#type", "type");
        nameMap.put("#date", "date");
        nameMap.put("#status", "status");

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("#date=:today and #type=:type and #status=:status")
                .withExpressionAttributeNames(nameMap)
                .withExpressionAttributeValues(valueMap);

        return mapper.scan(UserRequestTableItem.class, scanExpression);
    }

    public List<UserRequestTableItem> getTodaysPickups() {
        DynamoDBMapper mapper = DynamoDBHelper.getInstance().getMapper();

        SimpleDateFormat formatter = new SimpleDateFormat("MM.dd.yyyy");
        String today = formatter.format(new Date());

        Map<String, AttributeValue> valueMap = new HashMap<>();
        valueMap.put(":type", new AttributeValue().withS("pickup"));
        valueMap.put(":today", new AttributeValue().withS(today));

        Map<String, String> nameMap = new HashMap<>();
        nameMap.put("#type", "type");
        nameMap.put("#date", "date");

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("#date=:today and #type=:type")
                .withExpressionAttributeNames(nameMap)
                .withExpressionAttributeValues(valueMap);

        return mapper.scan(UserRequestTableItem.class, scanExpression);
    }

    public List<DriverTruckHistoryTableItem> getDriverTruckHistory() {
        DynamoDBMapper mapper = DynamoDBHelper.getInstance().getMapper();
        return mapper.scan(DriverTruckHistoryTableItem.class, new DynamoDBScanExpression());
    }

    public List<DriverTruckHistoryTableItem> getTodaysDriverTruckHistoryByDriverIDAndTruckID(String driverID, String truckID) {
        DynamoDBMapper mapper = DynamoDBHelper.getInstance().getMapper();
        SimpleDateFormat formatter = new SimpleDateFormat("MM.dd.yyyy");
        String today = formatter.format(new Date());

        Map<String, AttributeValue> valueMap = new HashMap<>();
        valueMap.put(":today", new AttributeValue().withS(today));
        valueMap.put(":driverID", new AttributeValue().withS(driverID));
        valueMap.put(":truckID", new AttributeValue().withS(truckID));

        Map<String, String> nameMap = new HashMap<>();
        nameMap.put("#date", "date");
        nameMap.put("#id", "driverID");
        nameMap.put("#truckID", "truckID");

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("#date = :today and #id = :driverID and #truckID=:truckID")
                .withExpressionAttributeValues(valueMap)
                .withExpressionAttributeNames(nameMap);

        return mapper.scan(DriverTruckHistoryTableItem.class, scanExpression);
    }

    public List<ServiceZipCodeTableItem> getZipCodeByZipCode(String zipCode) {
        IDynamoDBHelper helper = DynamoDBHelper.getInstance();

        DynamoDBMapper mapper = helper.getMapper();

        Map<String, String> nameMap = new HashMap<>();
        nameMap.put("#zipcode", "zipcode");

        Map<String, AttributeValue> valueMap = new HashMap<>();
        valueMap.put(":zipcode", new AttributeValue().withS(zipCode));

        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("#zipcode = :zipcode")
                .withExpressionAttributeNames(nameMap)
                .withExpressionAttributeValues(valueMap);

        List<ServiceZipCodeTableItem> zipCodes = mapper.scan(ServiceZipCodeTableItem.class, scanExpression);

        return zipCodes;
    }

    public List<ConfigurationTableItem> getAllConfigurations() {
        DynamoDBMapper mapper = DynamoDBHelper.getInstance().getMapper();
        return mapper.scan(ConfigurationTableItem.class, new DynamoDBScanExpression());
    }

}
