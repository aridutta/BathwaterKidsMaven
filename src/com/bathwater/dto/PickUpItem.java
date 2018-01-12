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
public class PickUpItem {
    
    List<String> itemCode;
    
    String itemName;
    
    String observations;
    
    List<String> imagesBase64;
    
//    String condition;
    
    Integer sharable;

    public List<String> getItemCode() {
        return itemCode;
    }

    public void setItemCode(List<String> itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public List<String> getImagesBase64() {
        return imagesBase64;
    }

    public void setImagesBase64(List<String> imagesBase64) {
        this.imagesBase64 = imagesBase64;
    }

//    public String getCondition() {
//        return condition;
//    }
//
//    public void setCondition(String condition) {
//        this.condition = condition;
//    }

    public Integer getSharable() {
        return sharable;
    }

    public void setSharable(Integer sharable) {
        this.sharable = sharable;
    }
    
}
