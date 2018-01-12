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
public class PickupList {
    
    String userRequestID;
    
    String driverID;
    
    List<PickupItem> pickedupItems;
    
    List<Bin> bins;
    
    String customerSignatureBase64;

    public String getUserRequestID() {
        return userRequestID;
    }

    public void setUserRequestID(String userRequestID) {
        this.userRequestID = userRequestID;
    }

    public String getDriverID() {
        return driverID;
    }

    public void setDriverID(String driverID) {
        this.driverID = driverID;
    }

    public List<PickupItem> getPickedupItems() {
        return pickedupItems;
    }

    public void setPickedupItems(List<PickupItem> pickedupItems) {
        this.pickedupItems = pickedupItems;
    }

    public List<Bin> getBins() {
        return bins;
    }

    public void setBins(List<Bin> bins) {
        this.bins = bins;
    }

    public String getCustomerSignatureBase64() {
        return customerSignatureBase64;
    }

    public void setCustomerSignatureBase64(String customerSignatureBase64) {
        this.customerSignatureBase64 = customerSignatureBase64;
    }
    
    
    public static class PickupItem {
        String productName;
        
        String brandName;
        
        String condition;
        
        String status;
        
        String eventualDamages;
                
        List<String> itemCodes;
        
        List<String> imagesBase64;
        
        List<SubItem> subItems;

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getBrandName() {
            return brandName;
        }

        public void setBrandName(String brandName) {
            this.brandName = brandName;
        }

        public String getCondition() {
            return condition;
        }

        public void setCondition(String condition) {
            this.condition = condition;
        }

        public String getEventualDamages() {
            return eventualDamages;
        }

        public void setEventualDamages(String eventualDamages) {
            this.eventualDamages = eventualDamages;
        }

        public List<String> getImagesBase64() {
            return imagesBase64;
        }

        public void setImagesBase64(List<String> imagesBase64) {
            this.imagesBase64 = imagesBase64;
        }

        public List<String> getItemCodes() {
            return itemCodes;
        }

        public void setItemCodes(List<String> itemCodes) {
            this.itemCodes = itemCodes;
        }

        public List<SubItem> getSubItems() {
            return subItems;
        }

        public void setSubItems(List<SubItem> subItems) {
            this.subItems = subItems;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
        
        
    }
    
    public static class SubItem {
        
        String itemCode;
        
        String itemName;
        
        String description;
        
        List<String> imagesBase64;

        public String getItemCode() {
            return itemCode;
        }

        public void setItemCode(String itemCode) {
            this.itemCode = itemCode;
        }

        public String getItemName() {
            return itemName;
        }

        public void setItemName(String itemName) {
            this.itemName = itemName;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public List<String> getImagesBase64() {
            return imagesBase64;
        }

        public void setImagesBase64(List<String> imagesBase64) {
            this.imagesBase64 = imagesBase64;
        }
        
    }
    
    public static class Bin {
        String binCode;
        
        List<String> imageBase64;

        public String getBinCode() {
            return binCode;
        }

        public void setBinCode(String binCode) {
            this.binCode = binCode;
        }

        public List<String> getImageBase64() {
            return imageBase64;
        }

        public void setImageBase64(List<String> imageBase64) {
            this.imageBase64 = imageBase64;
        }
    }
    
}
