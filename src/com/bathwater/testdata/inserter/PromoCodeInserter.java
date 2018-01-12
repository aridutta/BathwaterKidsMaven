/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bathwater.testdata.inserter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.bathwater.amazons3.PromoCodeDatabaseUtil;

/**
 *
 * @author rajeshk
 */
public class PromoCodeInserter {
    
    public static void main(String[] args) throws IOException {
        
        String promoCode = "BWGEAR16";
        
        String discount = "10";
        
        InputStream in = new ByteArrayInputStream(discount.getBytes());
        
        PromoCodeDatabaseUtil.uploadFile(promoCode, in);
        
    }
    
}
