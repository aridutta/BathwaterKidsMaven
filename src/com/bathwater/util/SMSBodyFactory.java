/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bathwater.util;

/**
 *
 * @author rajeshk
 */
public class SMSBodyFactory {
    
    private final static String APP_URL = "http://www.bathwaterkids.com/";
    
    public static String createVerificationBody(String otp) {
        StringBuilder body = new StringBuilder();
        body.append("Bathwater code: ").append(otp).append(". ");
        body.append("Happy decluttering!");
        
        return body.toString();
    }
    
    public static String createPickupConfirmation(String userName) {
        StringBuilder body = new StringBuilder();
        //body.append("Hey ").append(userName).append(", your pickup has been successfully scheduled!");
        body.append("Hi from Bathwater! Your pickup has been successfully scheduled!");
        return body.toString();
    }
    
    public static String createDropOffConfirmation(String userName) {
        StringBuilder body = new StringBuilder();
        //body.append("Hey ").append(userName).append(", your dropoff has been successfully scheduled!");
        body.append("Hi from Bathwater! Your dropoff has been successfully scheduled!");
        return body.toString();
    }
    
    public static String createPickupAndDropOffConfirmation(String userName) {
        StringBuilder body = new StringBuilder();
        //body.append("Hey ").append(userName).append(", your pickup and delivery have been sucessfully scheduled!");
        body.append("Hi from Bathwater! Your pickup and delivery have been sucessfully scheduled!");
        return body.toString();
    }
    
    public static String createEarnedCreditsMessage(String userName, int creditsAdded) {
        StringBuilder body = new StringBuilder();
//        body.append("Hey ").append(userName).append(", your gear was successfully swapped and you earned ")
//                .append(creditsAdded).append("! Go to the catalog & happy browsing!");
        //You just earned 10 credits by swapping gear on the Bathwater marketplace! 
        body.append("You just earned ").append(creditsAdded).append(" credits by swapping gear on the Bathwater marketplace!");
        return body.toString();
    }
    
    public static String createReferralCodeShareMessage(String userName, String referralCode) {
        StringBuilder body = new StringBuilder();
        body.append("Hi! ").append(userName).append(" is exclusively inviting you to join Bathwater Kids!");
        body.append(" Use the invite code - ").append(referralCode).append(" - when signing up and get 20$ off! ");
        body.append("Download the app to redeem it! ").append(APP_URL);
        
        return body.toString();
    }
    
    public static String createDriverIsOnTheWayMessage(String requestType, String eta) {
        StringBuilder body = new StringBuilder();
            body.append("Hi! A Bathwater driver is on the way to declutter you home!");
//            body.append(requestType);
//            body.append(".");
//            if (eta != null && !eta.isEmpty()) {
//                body.append(" The driver should reach in ");
//                body.append(eta);
//            }
            
            return body.toString();
    }
    
}
