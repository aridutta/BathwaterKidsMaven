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
public class ErrorResult {
    
    int errorCode;
    String message;
    String data;
    String token;
    Object miscellaneous;
    String pickupRequestID;
    String dropOffRequestID;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Object getMiscellaneous() {
        return miscellaneous;
    }

    public void setMiscellaneous(Object miscellaneous) {
        this.miscellaneous = miscellaneous;
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
