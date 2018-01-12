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
public class CheckoutList {
    
    String userID;
    
    String timeslotID;
    
    String promoCode;
    
    Boolean isPickUpRequired;
    
    Integer nItems;
    
    String pickUpTimeslotID;
    
    List<RetrieveItems> retrieveItems;
    
    List<SwapItems> swapItems;
    
    Integer creditsRequired;
    
    Integer amount;
        
    String cardID;
    
    Integer shippingCharges;
    
    String addressID;

    public String getAddressID() {
        return addressID;
    }

    public void setAddressID(String addressID) {
        this.addressID = addressID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getTimeslotID() {
        return timeslotID;
    }

    public void setTimeslotID(String timeslotID) {
        this.timeslotID = timeslotID;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public Boolean getIsPickUpRequired() {
        return isPickUpRequired;
    }

    public void setIsPickUpRequired(Boolean isPickUpRequired) {
        this.isPickUpRequired = isPickUpRequired;
    }

    public Integer getnItems() {
        return nItems;
    }

    public void setnItems(Integer nItems) {
        this.nItems = nItems;
    }

    public String getPickUpTimeslotID() {
        return pickUpTimeslotID;
    }

    public void setPickUpTimeslotID(String pickUpTimeslotID) {
        this.pickUpTimeslotID = pickUpTimeslotID;
    }

    public List<RetrieveItems> getRetrieveItems() {
        return retrieveItems;
    }

    public void setRetrieveItems(List<RetrieveItems> retrieveItems) {
        this.retrieveItems = retrieveItems;
    }

    public List<SwapItems> getSwapItems() {
        return swapItems;
    }

    public void setSwapItems(List<SwapItems> swapItems) {
        this.swapItems = swapItems;
    }

    public Integer getCreditsRequired() {
        return creditsRequired;
    }

    public void setCreditsRequired(Integer creditsRequired) {
        this.creditsRequired = creditsRequired;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getCardID() {
        return cardID;
    }

    public void setCardID(String cardID) {
        this.cardID = cardID;
    }

    public Integer getShippingCharges() {
        return shippingCharges;
    }

    public void setShippingCharges(Integer shippingCharges) {
        this.shippingCharges = shippingCharges;
    }
    
    public static class SwapItems {
        
        String itemID;
        
        Integer credits;

        public String getItemID() {
            return itemID;
        }

        public void setItemID(String itemID) {
            this.itemID = itemID;
        }

        public Integer getCredits() {
            return credits;
        }

        public void setCredits(Integer credits) {
            this.credits = credits;
        }
    }
    
    public static class RetrieveItems {
        
        String itemID;

        public String getItemID() {
            return itemID;
        }

        public void setItemID(String itemID) {
            this.itemID = itemID;
        }
        
    }
    
}
