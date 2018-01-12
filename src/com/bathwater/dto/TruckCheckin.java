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
public class TruckCheckin {
    
    String truckID;
    
    Integer toCheckIn;
    
    Integer checkedIn;

    public String getTruckID() {
        return truckID;
    }

    public void setTruckID(String truckID) {
        this.truckID = truckID;
    }

    public Integer getToCheckIn() {
        return toCheckIn;
    }

    public void setToCheckIn(Integer toCheckIn) {
        this.toCheckIn = toCheckIn;
    }

    public Integer getCheckedIn() {
        return checkedIn;
    }

    public void setCheckedIn(Integer checkedIn) {
        this.checkedIn = checkedIn;
    }
    
}
