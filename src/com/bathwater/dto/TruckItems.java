/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bathwater.dto;

import java.util.List;

import com.bathwater.dynamodb.tables.UserRequestTableItem;

/**
 *
 * @author giridhar
 */
public class TruckItems {
    
    private UserRequestTableItem.Driver driver;
    
    private List<UserRequestTableItem> items;
    
    private Integer totalItemsCount;
    
    private String truckId;
    
    private String licensePlate;

    public UserRequestTableItem.Driver getDriver() {
        return driver;
    }

    public void setDriver(UserRequestTableItem.Driver driver) {
        this.driver = driver;
    }

    public List<UserRequestTableItem> getItems() {
        return items;
    }

    public void setItems(List<UserRequestTableItem> items) {
        this.items = items;
    }

    public Integer getTotalItemsCount() {
        return totalItemsCount;
    }

    public void setTotalItemsCount(Integer totalItemsCount) {
        this.totalItemsCount = totalItemsCount;
    }

    public String getTruckId() {
        return truckId;
    }

    public void setTruckId(String truckId) {
        this.truckId = truckId;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }
    
    
    
}
