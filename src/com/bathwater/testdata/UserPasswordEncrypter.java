/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bathwater.testdata;

import java.text.ParseException;
import java.util.Map;

import com.bathwater.dynamodb.tables.UserRequestTableItem;
import com.bathwater.util.StringUtil;

/**
 *
 * @author rajeshk
 */
public class UserPasswordEncrypter {
    
    private static UserRequestTableItem filter(Map<String, UserRequestTableItem> requests, String id) {
        UserRequestTableItem req = requests.get(id);
        System.out.println(id);
        if (req != null && req.getUserRequestID().equals(id) && !"completed".equals(req.getStatus())) {
            if (req.getUser() != null) {
                if (StringUtil.isBlank(req.getUser().getPhoneNumber())) {
                    return req;
                }
            }
        }
        return null;
    }
    
    public static void main(String[] args) throws ParseException {
//        DynamoDBScans scanHelper = new DynamoDBScans();
//        DynamoDBHelper helper = DynamoDBHelper.getInstance();        
//        List<UserRequestTableItem> usersRequests = new ArrayList<>(scanHelper.getAllUserRequests());
////        List<TimeslotTableItem> timeslots = new ArrayList<>(scanHelper.getAllTimeslots());
//        System.out.println(usersRequests.size());
//        SimpleDateFormat formatter = new SimpleDateFormat("MM.dd.yyyy");
//        int i=0;
//        
//        for (UserRequestTableItem timeslot : usersRequests) {
//            String date = timeslot.getDate();
//            Date timeslotDate = formatter.parse(date);
//            Long timestamp = timeslotDate.getTime();
//            timeslot.setTimestamp(timestamp);
//            helper.putItem(timeslot);
//            System.out.println((++i) + " / " + usersRequests.size());
//        }
        
//        Date today = new Date();
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(today);
//        cal.add(Calendar.DATE, -2);
//        
//        for (UserRequestTableItem req : usersRequests) {
//            Date date = formatter.parse(req.getDate());
//            
//            if (date.before(cal.getTime()) || req.getStatus().equals("completed")) {
//                helper.deleteItem(req);
//            }            
//        }
        
        
    }

}
