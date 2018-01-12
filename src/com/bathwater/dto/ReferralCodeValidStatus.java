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
public class ReferralCodeValidStatus {
    
    Integer statusCode;
    
    String message;
    
    Integer amountOff;
    
    Integer percentOff;
    
    String redeemBy;

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getAmountOff() {
        return amountOff;
    }

    public void setAmountOff(Integer amountOff) {
        this.amountOff = amountOff;
    }

    public Integer getPercentOff() {
        return percentOff;
    }

    public void setPercentOff(Integer percentOff) {
        this.percentOff = percentOff;
    }

    public String getRedeemBy() {
        return redeemBy;
    }

    public void setRedeemBy(String redeemBy) {
        this.redeemBy = redeemBy;
    }
    
}
