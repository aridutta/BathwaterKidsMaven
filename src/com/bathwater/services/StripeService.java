/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bathwater.services;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bathwater.dto.ReferralCodeValidStatus;
import com.bathwater.util.StringUtil;
import com.stripe.Stripe;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.exception.StripeException;
import com.stripe.model.Card;
import com.stripe.model.Charge;
import com.stripe.model.Coupon;
import com.stripe.model.Customer;
import com.stripe.model.DeletedCard;
import com.stripe.model.DeletedPlan;
import com.stripe.model.Plan;
import com.stripe.model.Subscription;

/**
 *
 * @author rajeshk
 */
public class StripeService {

    static {
        Stripe.apiKey = "sk_test_Pj6izShu4KwPk1EHOXcCQSH8";//"1".equals(System.getProperty("PRODUCTION_MODE")) ? System.getProperty("STRIPE") : System.getProperty("STRIPE_TEST");
    }
    
    public static String createUser(String token, String emailAddress) throws StripeException {
        Map<String, Object> customerParams = new HashMap<>();
        customerParams.put("description", "Customer object for " + emailAddress);
        customerParams.put("source", token);
        
        Customer customer = (Customer) Customer.create(customerParams);
        
        return customer.getId();
    }

    public static String createUser(String userid, String email, String cardNumber, String expMonth, String expYear, String cvc, String token) throws AuthenticationException, InvalidRequestException, APIConnectionException, CardException, APIException {
        Map<String, Object> customerParams = new HashMap<>();

        customerParams.put("description", "Customer object for " + userid);
        customerParams.put("email", email);

        if (token != null && !token.equals("")) {
            customerParams.put("source", token);
        } else {
            Map<String, String> cardDetails = new HashMap<>();

            cardDetails.put("object", "card");
            cardDetails.put("exp_month", expMonth);
            cardDetails.put("exp_year", expYear);
            cardDetails.put("number", cardNumber);
            if (cvc != null && !cvc.equals("")) {
                cardDetails.put("cvc", cvc);
            }
            customerParams.put("source", cardDetails);
        }

        Customer customer = Customer.create(customerParams);

        return customer.getId();
    }

    public static String getDefaultCardId(String customerId) throws AuthenticationException, InvalidRequestException, APIConnectionException, CardException, APIException {
        Customer customer = Customer.retrieve(customerId);

        return customer.getDefaultSource();
    }

    public static String updateCustomerCreditCard(String customerId, String cardNumber, String expMonth, String expYear, String cvc) throws AuthenticationException, InvalidRequestException, APIConnectionException, CardException, APIException {

        Customer customer = Customer.retrieve(customerId);

        Map<String, Object> updateParams = new HashMap<>();
        Map<String, String> cardDetails = new HashMap<>();

        cardDetails.put("object", "card");
        cardDetails.put("exp_month", expMonth);
        cardDetails.put("exp_year", expYear);
        cardDetails.put("number", cardNumber);
        if (cvc != null && !cvc.equals("")) {
            cardDetails.put("cvc", cvc);
        }
        updateParams.put("source", cardDetails);
        customer.update(updateParams);

        return customer.getId();
    }

    public static String addNewCard(String customerId, String cardNumber, String expMonth, String expYear, String cvc) throws AuthenticationException, InvalidRequestException, APIConnectionException, CardException, APIException {
        Customer customer = Customer.retrieve(customerId);
        Map<String, String> cardDetails = new HashMap<>();

        cardDetails.put("object", "card");
        cardDetails.put("exp_month", expMonth);
        cardDetails.put("exp_year", expYear);
        cardDetails.put("number", cardNumber);
        if (!StringUtil.isBlank(cvc)) {
            cardDetails.put("cvc", cvc);
        }

        Map<String, Object> createParams = new HashMap<>();
        createParams.put("source", cardDetails);
        Card card = (Card) customer.getSources().create(createParams);

        return card.getId();
    }
    
    public static String addNewCard(String customerId, String token) throws AuthenticationException, InvalidRequestException, APIConnectionException, CardException, APIException {
        Customer customer = Customer.retrieve(customerId);
        Map<String, Object> params = new HashMap<>();
        params.put("source", token);
        
        Card card = (Card) customer.getSources().create(params);
        
        return card.getId();
    }
    
    public static boolean deleteCard(String customerId, String cardId) throws AuthenticationException, InvalidRequestException, APIConnectionException, CardException, APIException {
        Customer customer = Customer.retrieve(customerId);
        Card card = (Card) customer.getSources().retrieve(cardId);
        DeletedCard deletedCard = card.delete();
        return deletedCard.getDeleted();
    }

    public static String createSubscription(String customerId, String planId, String couponCode) throws AuthenticationException, InvalidRequestException, APIConnectionException, CardException, APIException {
        Customer customer = Customer.retrieve(customerId);
        Map<String, Object> params = new HashMap<>();
        params.put("plan", planId);
        if (couponCode != null && !couponCode.equals("")) {
            params.put("coupon", couponCode);
        }
        Subscription subscription = customer.createSubscription(params);
        return subscription.getId();
    }

    public static boolean deleteSubscription(String customerId, String subscriptionId) throws AuthenticationException, InvalidRequestException, APIConnectionException, CardException, APIException {

        Customer customer = Customer.retrieve(customerId);

        for (Subscription subscription : customer.getSubscriptions().getData()) {
            if (subscription.getId().equals(subscriptionId)) {
                subscription.cancel(null);
                return true;
            }
        }

        return false;
    }

    public static String createMembershipPlan(String planId, String planName, String amount, String interval) throws AuthenticationException, InvalidRequestException, APIConnectionException, CardException, APIException, InvalidRequestException {

        HashMap<String, Object> planParams = new HashMap<>();

        planParams.put("amount", amount);
        planParams.put("interval", interval);
        planParams.put("name", planName);
        planParams.put("currency", "usd");
        planParams.put("id", planId);

        Plan createdPlan = Plan.create(planParams);

        return createdPlan.getId();
    }

    public static boolean deleteMembershipPlan(String planId) throws AuthenticationException, InvalidRequestException, APIConnectionException, CardException, APIException {
        Plan plan = Plan.retrieve(planId);

        DeletedPlan deletedPlan = plan.delete();

        return deletedPlan.getDeleted();
    }

    public static String listAllPlans() throws AuthenticationException, InvalidRequestException, APIConnectionException, CardException, APIException {
        return Plan.list(new HashMap<String, Object>()).toString();
    }

    public static boolean validateCoupon(String couponID) {
        try {
            Coupon coupon = Coupon.retrieve(couponID);
            return coupon != null;
        } catch (CardException | APIException | AuthenticationException | InvalidRequestException | APIConnectionException ex) {
            return false;
        }
    }
    
    public static ReferralCodeValidStatus populateCouponData(ReferralCodeValidStatus status, String couponID) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("MM.dd.yyyy kk:mm:ss");
            Coupon coupon = Coupon.retrieve(couponID);
            status.setAmountOff((int) (long)coupon.getAmountOff());
            status.setPercentOff(coupon.getPercentOff());
            status.setRedeemBy(formatter.format(new Date(coupon.getRedeemBy() * 1000)));
        } catch (AuthenticationException | InvalidRequestException | APIConnectionException | CardException | APIException ex) {
            Logger.getLogger(StripeService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return status;
    }

    public static String charge(Integer amount, String cardNumber, String expMonth, String expYear, String customerID) throws AuthenticationException, InvalidRequestException, APIConnectionException, CardException, APIException {

        Map<String, Object> params = new HashMap<>();
        params.put("amount", amount);

        if (customerID != null) {
            params.put("customer", customerID);
        } else {
            Map<String, String> card = new HashMap<>();
            card.put("object", "card");
            card.put("number", cardNumber);
            card.put("exp_year", expYear);
            card.put("exp_month", expMonth);
            params.put("source", card);
        }

        params.put("currency", "usd");

        Charge charge = Charge.create(params);
        return charge.getStatus();
    }

    public static String charge(Integer amount, String customerId, String cardId) throws AuthenticationException, InvalidRequestException, APIConnectionException, CardException, APIException {
        Map<String, Object> params = new HashMap<>();
        params.put("amount", amount);

        params.put("customer", customerId);
        
        params.put("card", cardId);

        params.put("currency", "usd");

        Charge charge = Charge.create(params);
        return charge.getStatus();
    }
}
