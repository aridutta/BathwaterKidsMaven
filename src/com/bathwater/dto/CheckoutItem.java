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
public class CheckoutItem {
    
    
    String userRequestID;
    
    String itemCode;    

    public String getUserRequestID() {
        return userRequestID;
    }

    public void setUserRequestID(String userRequestID) {
        this.userRequestID = userRequestID;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }
}
