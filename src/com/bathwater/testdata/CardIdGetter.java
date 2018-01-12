/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bathwater.testdata;

import java.util.ArrayList;
import java.util.List;

import com.bathwater.dao.DAO;
import com.bathwater.dynamodb.helper.impl.DynamoDBHelper;
import com.bathwater.dynamodb.helper.queries.DynamoDBScans;
import com.bathwater.dynamodb.tables.UserTableItem;
import com.bathwater.services.StripeService;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;

/**
 *
 * @author rajeshk
 */
public class CardIdGetter {

    public static void main(String[] args) throws AuthenticationException, InvalidRequestException, APIConnectionException, CardException, APIException {
        DynamoDBScans scanHelper = new DynamoDBScans();
        DynamoDBHelper helper = DynamoDBHelper.getInstance();
        
        
        UserTableItem user = DAO.getInstance().getUserProfile("72947a08-4430-4ca5-a8ff-5112a3284855");
        
        if (user != null) {
            String stripeID = user.getStripeID();
            
            String cardID = StripeService.getDefaultCardId(stripeID);
            
            System.out.println(cardID);
            
            List<UserTableItem.Payments> payments = user.getPayments();
            
            if (payments == null) {
                payments = new ArrayList<>();
            }
            
            UserTableItem.Payments payment = new UserTableItem.Payments();
            payment.setCardID(cardID);
            payment.setCardNumber("************4242");
            payment.setCardType("VISA");
            payment.setExpMonth("12");
            payment.setExpYear("2017");
            payments.add(payment);
            
            user.setPayments(payments);
            helper.putItem(user);
        }
        
    }

}
