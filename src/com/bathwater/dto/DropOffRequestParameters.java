/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bathwater.dto;

import java.util.List;

/**
 *
 * @author rajeshk
 */
public class DropOffRequestParameters {
    
    String usrid;
    
    String addressID;
    
    List<String> storedItemIDs;
    
    String tsID;

    public String getUsrid() {
        return usrid;
    }

    public void setUsrid(String usrid) {
        this.usrid = usrid;
    }

    public String getAddressID() {
        return addressID;
    }

    public void setAddressID(String addressID) {
        this.addressID = addressID;
    }

    public List<String> getStoredItemIDs() {
        return storedItemIDs;
    }

    public void setStoredItemIDs(List<String> storedItemIDs) {
        this.storedItemIDs = storedItemIDs;
    }

    public String getTsID() {
        return tsID;
    }

    public void setTsID(String tsID) {
        this.tsID = tsID;
    }
}
