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
public class DropOffList {
    
    List<Item> items;
    
    String customerSignatureBase64;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public String getCustomerSignatureBase64() {
        return customerSignatureBase64;
    }

    public void setCustomerSignatureBase64(String customerSignatureBase64) {
        this.customerSignatureBase64 = customerSignatureBase64;
    }
    
    public static class Item {
        List<String> itemCodes;
        
        //List<String> images;
        
        List<SubItem> subItems;
        
        List<String> imagesBase64;
        
        String itemName;
        
        int returned;

        public List<String> getItemCodes() {
            return itemCodes;
        }

        public void setItemCodes(List<String> itemCodes) {
            this.itemCodes = itemCodes;
        }

        public String getItemName() {
            return itemName;
        }

        public void setItemName(String itemName) {
            this.itemName = itemName;
        }

        public int getReturned() {
            return returned;
        }

        public void setReturned(int returned) {
            this.returned = returned;
        }

//        public List<String> getImages() {
//            return images;
//        }
//
//        public void setImages(List<String> images) {
//            this.images = images;
//        }

        public List<SubItem> getSubItems() {
            return subItems;
        }

        public void setSubItems(List<SubItem> subItems) {
            this.subItems = subItems;
        }

        public List<String> getImagesBase64() {
            return imagesBase64;
        }

        public void setImagesBase64(List<String> imagesBase64) {
            this.imagesBase64 = imagesBase64;
        }
        
        

        
    }
    
    public static class SubItem {
        
        String description;
        String itemCode;
        String itemName;
        String status;
        List<String> imageURLs;

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

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

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public List<String> getImageURLs() {
            return imageURLs;
        }

        public void setImageURLs(List<String> imageURLs) {
            this.imageURLs = imageURLs;
        }
    }
    
}
