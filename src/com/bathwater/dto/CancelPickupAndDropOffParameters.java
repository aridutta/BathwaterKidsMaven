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
public class CancelPickupAndDropOffParameters {
    
    String userID;
    
    String pickupRequestID;
    
    String dropOffRequestID;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPickupRequestID() {
        return pickupRequestID;
    }

    public void setPickupRequestID(String pickupRequestID) {
        this.pickupRequestID = pickupRequestID;
    }

    public String getDropOffRequestID() {
        return dropOffRequestID;
    }

    public void setDropOffRequestID(String dropOffRequestID) {
        this.dropOffRequestID = dropOffRequestID;
    }
    
}
