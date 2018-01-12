/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bathwater.dto;

/**
 *
 * @author rajeshk
 */
public class CancelRequestParameters {
    
    String userID;
    
    String userRequestID;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserRequestID() {
        return userRequestID;
    }

    public void setUserRequestID(String userRequestID) {
        this.userRequestID = userRequestID;
    }
    
}
