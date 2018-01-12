/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bathwater.testdata.inserter;

import java.util.ArrayList;
import java.util.List;

import com.bathwater.dynamodb.helper.impl.DynamoDBHelper;
import com.bathwater.dynamodb.helper.queries.DynamoDBScans;
import com.bathwater.dynamodb.tables.DriverTableItem;
import com.bathwater.dynamodb.tables.UserRequestTableItem;
import com.bathwater.dynamodb.tables.UserTableItem;

/**
 *
 * @author rajeshk
 */
public class UserAddressInserter {
    
    public static void main(String[] args) {
        
        DynamoDBScans scanHelper = new DynamoDBScans();
        DynamoDBHelper helper = DynamoDBHelper.getInstance();
        
        List<DriverTableItem> drivers = scanHelper.getDriverByEmail("sherlock.bond@bakerstreet.com");
        DriverTableItem sherlock = drivers.get(0);
        
        List<UserRequestTableItem> todaysEvents = scanHelper.getTodaysUserRequestsByDriverID(sherlock.getDriverID());
        
        List<UserRequestTableItem> events = new ArrayList<>(todaysEvents);
        
        for (UserRequestTableItem event : events) {
            
            UserTableItem user = scanHelper.getUsersBasedById(event.getUser().getUserID()).get(0);
            UserRequestTableItem.User.Address address = new UserRequestTableItem.User.Address();
            address.setStreetAddress(user.getAddress().get(0).getStreetAddress());
            address.setApartment(user.getAddress().get(0).getApartment());
            address.setCity(user.getAddress().get(0).getCity());
            address.setState(user.getAddress().get(0).getState());
            address.setZipCode(user.getAddress().get(0).getZipCode());
            address.setSpecialInstructions(user.getAddress().get(0).getSpecialInstructions());
            event.getUser().setAddress(address);
            
            helper.putItem(event);
        }
        
//        String[] userIDs = {"91f4f884-b54c-4a4a-9f3b-1c0ed51736ef", "59dbb2b5-2dd2-49a3-9a5f-9005a00279ee",
//        "b46a7a9e-3658-4612-b573-0715ffa7a153", "9994ed06-9f6f-4e1a-807c-41fa9ed55adb", "48b2e51e-efdc-40e2-b1dd-9af999c7884e",
//        "5c06ffa4-e426-472a-a0f0-10fbffb6bedb", "2d805275-65e0-4ef5-bdf9-6c91802d7c99"};
        
//        for (String userID : userIDs) {
//            List<UserTableItem> users = scanHelper.getUsersBasedById(userID);
//            UserTableItem user = users.get(0);
//            
//            if (user.getAddress() == null || user.getAddress().isEmpty()) {
//                UserTableItem.Address address = new UserTableItem.Address();
//                address.setAddressID(System.currentTimeMillis() + "");
//                address.setStreetAddress("200 W Washington St");
//                address.setApartment(StringUtil.generateRandomNumbers(2));
//                address.setCity("Indianapolis");
//                address.setState("IN");
//                address.setSpecialInstructions("Call on arrival");
//                address.setZipCode("46202");
//                List<UserTableItem.Address> addresses = new ArrayList<>();
//                addresses.add(address);
//                user.setAddress(addresses);
//            }
//            
//            helper.putItem(user);
//        }
        
        
        
    }
    
}
