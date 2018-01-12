/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bathwater.testdata;

import java.util.HashMap;
import java.util.Map;

import com.stripe.Stripe;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.model.Token;

/**
 *
 * @author rajeshk
 */
public class StripeTokenCreator {
    
    public static void main(String[] args) throws AuthenticationException, InvalidRequestException, APIConnectionException, CardException, APIException {
        Stripe.apiKey = "sk_test_Pj6izShu4KwPk1EHOXcCQSH8";

        Map<String, Object> tokenParams = new HashMap<>();
        Map<String, Object> cardParams = new HashMap<>();
        cardParams.put("number", "4242424242424242");
        cardParams.put("exp_month", 9);
        cardParams.put("exp_year", 2017);
        cardParams.put("cvc", "314");
        tokenParams.put("card", cardParams);

        Token token = (Token) Token.create(tokenParams);
        System.out.println(token.getId());
    }
    
}
