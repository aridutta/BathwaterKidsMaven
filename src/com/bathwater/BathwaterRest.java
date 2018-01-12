/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bathwater;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.runner.JUnitCore;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.bathwater.amazons3.ImageDatabaseUtil;
import com.bathwater.amazons3.JSONDatabaseUtil;
import com.bathwater.amazons3.PromoCodeDatabaseUtil;
import com.bathwater.amazons3.URLTokenHandler;
import com.bathwater.dao.DAO;
import com.bathwater.dto.AddNewCard;
import com.bathwater.dto.CancelPickupAndDropOffParameters;
import com.bathwater.dto.CancelRequestParameters;
import com.bathwater.dto.CheckInParameters;
import com.bathwater.dto.CheckoutItem;
import com.bathwater.dto.CheckoutList;
import com.bathwater.dto.ChildrenUpdateParamaeters;
import com.bathwater.dto.ContactDetails;
import com.bathwater.dto.DeleteCardParameters;
import com.bathwater.dto.DisplayPicture;
import com.bathwater.dto.DriverLoginParameters;
import com.bathwater.dto.DropOffList;
import com.bathwater.dto.DropOffRequestParameters;
import com.bathwater.dto.ErrorResult;
import com.bathwater.dto.ImageString;
import com.bathwater.dto.LockItemParameters;
import com.bathwater.dto.LoginParameters;
import com.bathwater.dto.MembershipSubscriptionParameters;
import com.bathwater.dto.OAuthLoginParameters;
import com.bathwater.dto.PickupList;
import com.bathwater.dto.PickupRequestParameters;
import com.bathwater.dto.ProductImages;
import com.bathwater.dto.ReferralCodeValidStatus;
import com.bathwater.dto.ResetPasswordParameters;
import com.bathwater.dto.SetCreditsParameters;
import com.bathwater.dto.ShareItemParameters;
import com.bathwater.dto.ShippingChargeParmaeters;
import com.bathwater.dto.StartEventParameters;
import com.bathwater.dto.StoreItemParameters;
import com.bathwater.dto.TodaysCheckins;
import com.bathwater.dto.TruckCheckin;
import com.bathwater.dto.UpdatePasswordParameters;
import com.bathwater.dto.UserRegistrationParameters;
import com.bathwater.dto.VerifyOTPParameters;
import com.bathwater.dto.WeeksTimeslots;
import com.bathwater.dto.WishlistParameters;
import com.bathwater.dynamodb.tables.CategoryTableItem;
import com.bathwater.dynamodb.tables.DriverTableItem;
import com.bathwater.dynamodb.tables.DriverTruckHistoryTableItem;
import com.bathwater.dynamodb.tables.InventoryTableItem;
import com.bathwater.dynamodb.tables.MembershipTableItem;
import com.bathwater.dynamodb.tables.Notification;
import com.bathwater.dynamodb.tables.OAuthTableItem;
import com.bathwater.dynamodb.tables.ReferralCodeMapper;
import com.bathwater.dynamodb.tables.ServiceZipCodeTableItem;
import com.bathwater.dynamodb.tables.StorageTableItem;
import com.bathwater.dynamodb.tables.TimeslotTableItem;
import com.bathwater.dynamodb.tables.TruckTableItem;
import com.bathwater.dynamodb.tables.UserRequestTableItem;
import com.bathwater.dynamodb.tables.UserTableItem;
import com.bathwater.services.MandrillEmailService;
import com.bathwater.services.StripeService;
import com.bathwater.services.TwilioSMSService;
import com.bathwater.util.AdvancedEncryptionStandard;
import com.bathwater.util.CryptoUtil;
import com.bathwater.util.JUnitTestListener;
import com.bathwater.util.SMSBodyFactory;
import com.bathwater.util.StringUtil;
import com.bathwater.util.UserRequestComparator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;

/**
 *
 * @author rajeshk
 */
@Resource
@Path("/")
public class BathwaterRest {

    @Context
    UriInfo uriInfo;

    @Path("test")
    @GET
    public Response test() {
        return Response.ok()
                .entity("hi-1.0")
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE")
                .allow("OPTIONS")
                .build();
    }

    @Path("oauth/login")
    @OPTIONS
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response oAuthLogin() {
        return Response.ok()
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "POST")
                .header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization")
                .allow("OPTIONS")
                .build();
    }

    @Path("oauth/login")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response oAuthLogin(OAuthLoginParameters params) {
        ErrorResult res = DAO.getInstance().oAuthLogin(params);

        JSONObject response = new JSONObject();
        response.put("statusCode", res.getErrorCode());
        response.put("message", res.getMessage());

        return Response.ok()
                .entity(response.toJSONString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "POST")
                .header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization")
                .allow("OPTIONS")
                .build();
    }

    @Path("register")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(UserRegistrationParameters user) {

        ErrorResult res = DAO.getInstance().register(user);

        JSONObject response = new JSONObject();
        response.put("statusCode", res.getErrorCode());
        response.put("message", res.getMessage());

        if (res.getErrorCode() == 200 || res.getErrorCode() == 201) {
            response.put("userid", res.getData());
            StringBuilder keyBuilder = new StringBuilder();
            keyBuilder.append(user.getEmailAddress()).append(":").append(user.getPassword()).append(":").append(res.getToken());
            String key = Base64.encode(keyBuilder.toString().getBytes());
            key = key.replaceAll("\n", "");
            response.put("key", key);
        }

        return Response.ok()
                .entity(response.toJSONString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE")
                .allow("OPTIONS")
                .build();
    }

    @Path("requestOTP")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendOTP(@QueryParam("usrid") String userID, @QueryParam("phone") String mobileNumber) {
        UserTableItem user = DAO.getInstance().getUserProfile(userID);
        JSONObject response = new JSONObject();

        if (user == null) {
            response.put("statusCode", 101);
            response.put("message", "invalid userid");
        } else {
            String randomPin = StringUtil.generateRandomNumbers(6);
            user.setOtp(randomPin);

            String smsBody = SMSBodyFactory.createVerificationBody(randomPin);
            boolean send = TwilioSMSService.sendSMS(smsBody, mobileNumber);

            if (send) {
                response.put("statusCode", 200);
                response.put("message", "success");
                DAO.getInstance().saveItem(user);
            } else {
                response.put("statusCode", 500);
                response.put("message", "Sending SMS failed.");
            }
        }

        return Response.ok()
                .entity(response.toJSONString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "POST")
                .header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization")
                .allow("OPTIONS")
                .build();
    }

    @Path("verifyOTP")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response verifyOTP(VerifyOTPParameters params) {

        UserTableItem user = DAO.getInstance().getUserProfile(params.getUsrid());
        JSONObject response = new JSONObject();
        if (user == null) {
            response.put("statusCode", 101);
            response.put("message", "invalid usrid");
        } else if (user.getOtp().equals(params.getOtp())) {
            user.setPhoneNumber(params.getPhoneNumber());
            DAO.getInstance().saveItem(user);
            response.put("statusCode", 200);
            response.put("message", "success");
        } else {
            response.put("statusCode", 106);
            response.put("message", "invalid otp");
        }

        return Response.ok()
                .entity(response.toJSONString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "POST")
                .header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization")
                .allow("OPTIONS")
                .build();
    }

    @Path("updateChildren")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateChildren(ChildrenUpdateParamaeters children) {
        ErrorResult res = DAO.getInstance().updateChildren(children);

        JSONObject response = new JSONObject();

        response.put("statusCode", res.getErrorCode());
        response.put("message", res.getMessage());

        if (res.getErrorCode() == 200) {
            response.put("userid", res.getData());
        }

        return Response.ok()
                .entity(response.toJSONString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE")
                .allow("OPTIONS")
                .build();
    }

    @Path("updateDisplayPicture")
    @POST
    public Response updateDisplayPicture(DisplayPicture dp) {
        ErrorResult res = DAO.getInstance().updateDisplayPicture(dp);

        JSONObject response = new JSONObject();
        response.put("statusCode", res.getErrorCode());
        response.put("message", res.getMessage());

        return Response.ok()
                .entity(response.toJSONString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE")
                .allow("OPTIONS")
                .build();
    }

    @Path("updateProfile")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateProfile(String jsonStr) {
        JSONObject response = new JSONObject();

        Logger.getLogger(BathwaterRest.class.getName()).log(Level.INFO, jsonStr);

        try {
            JSONParser parser = new JSONParser();
            String userid;
            String firstName, lastName, email, phoneNum;
            String streetAddress, apartment, city, state, zipCode, imageBase64;
            List<ChildrenUpdateParamaeters.Children> childArray = new ArrayList<>();
            JSONObject json = (JSONObject) parser.parse(jsonStr);
            userid = (String) json.get("uid");
            firstName = (String) json.get("firstname");
            lastName = (String) json.get("lastname");
            email = (String) json.get("email");
            phoneNum = (String) json.get("contactno");
            streetAddress = (String) json.get("streetAddress");
            apartment = (String) json.get("apartment");
            city = (String) json.get("city");
            state = (String) json.get("state");
            zipCode = (String) json.get("zipCode");
            imageBase64 = (String) json.get("imageBase64");
            JSONArray jsonArray = (JSONArray) json.get("child");

            ErrorResult res = DAO.getInstance().updateProfile(userid, firstName, lastName, email, phoneNum, streetAddress, apartment, city, state, zipCode, imageBase64);

            if (res.getErrorCode() == 200 && jsonArray != null) {
                for (Object obj : jsonArray) {
                    JSONObject jsonObj = (JSONObject) obj;
                    ChildrenUpdateParamaeters.Children child = new ChildrenUpdateParamaeters.Children();
                    child.setName((String) jsonObj.get("name"));
                    Long ageStr = (Long) jsonObj.get("age");
                    int age = ageStr.intValue();
                    child.setAge(age);
                    String gender = (String) jsonObj.get("gender");
                    child.setGender(gender);
                    childArray.add(child);
                }
                ChildrenUpdateParamaeters childUpdateParams = new ChildrenUpdateParamaeters();
                childUpdateParams.setUserID(userid);
                childUpdateParams.setChildren(childArray);
                res = DAO.getInstance().updateChildren(childUpdateParams);
            }

            response.put("statusCode", res.getErrorCode());
            response.put("message", res.getMessage());
            if (res.getErrorCode() == 200) {

                UserTableItem user = DAO.getInstance().getUserProfile(userid);
                user.setPassword(null);
                user.setToken(null);
                user.setWishlist(null);
                user.setNotifications(null);

                ObjectMapper mapper = new ObjectMapper();
                String userProfile = mapper.writeValueAsString(user);
                JSONObject profile = (JSONObject) parser.parse(userProfile);

                response.put("profile", profile);
            }
        } catch (org.json.simple.parser.ParseException | JsonProcessingException ex) {
            response.put("statusCode", 500);
            response.put("message", ex.getMessage());
        }

        return Response
                .ok()
                .entity(response.toJSONString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @Path("resetPassword")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response resetPassword(ResetPasswordParameters params) throws IOException {
        try {
            String newPassword = StringUtil.generateRandomPassword();

            UserTableItem user = DAO.getInstance().getUserByEmail(params.getEmailAddress());

            if (user == null) {
                JSONObject response = new JSONObject();
                response.put("statusCode", 102);
                response.put("message", "email not registered");

                return Response.ok()
                        .entity(response.toJSONString())
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                        .allow("OPTIONS")
                        .build();
            }

            Date now = new Date();
            String hexTime = Long.toHexString(now.getTime());

            String token = MD5(hexTime + user.getEmailAddress());

            InputStream in = new ByteArrayInputStream(user.getEmailAddress().getBytes());

            URLTokenHandler.uploadTokenFile(token, in);

            String basePath = uriInfo.getBaseUri().toString();

            String resetURL = basePath + "reset?token=" + token;

            String response = MandrillEmailService.sendForgotPasswordMail(user.getFirstName(), newPassword, user.getEmailAddress(), resetURL);

            JSONParser parser = new JSONParser();

            JSONArray responseArray = (JSONArray) parser.parse(response);
            JSONObject responseObject = (JSONObject) responseArray.get(0);

            JSONObject res = new JSONObject();

            if (responseObject.get("status").equals("sent")) {
                user.setPassword(newPassword);
                DAO.getInstance().saveItem(user);
                res.put("statusCode", 200);
                res.put("message", "success");
            } else {
                res.put("statusCode", 500);
                res.put("message", "failed");
                res.put("reason", responseObject.get("reject_reason"));
            }

            return Response.ok()
                    .entity(res.toJSONString())
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                    .allow("OPTIONS")
                    .build();
        } catch (org.json.simple.parser.ParseException ex) {
            JSONObject response = new JSONObject();
            response.put("statusCode", 500);
            response.put("message", "internal server exception");

            return Response.ok()
                    .entity(response.toJSONString())
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                    .allow("OPTIONS")
                    .build();
        }

    }

    @GET
    @Path("/reset")
    @Produces(MediaType.TEXT_HTML)
    public Response resetPassword(@QueryParam("token") String token) throws IOException {
        Map<String, Object> map = new HashMap<>();
        if (token != null && !token.equals("") && URLTokenHandler.isValidFile(token)) {
            InputStream in = URLTokenHandler.downloadTokenFile(token);

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            String email = "";

            while ((line = reader.readLine()) != null) {
                email += line;
            }

            map.put("email", email.trim());
            map.put("token", token);

            String resetPageBody = StringUtil.buildResetPasswordHtmlPage(email.trim(), token);

            return Response.ok()
                    .entity(resetPageBody)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE")
                    .build();
        }

        return Response.ok()
                .entity(StringUtil.buildInvalidTokenErrorPage())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE")
                .build();
    }

    private String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }

    @Path("updatePassword")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updatePassword(UpdatePasswordParameters params) {

        boolean justUpdate = false;

        if (!StringUtil.isBlank(params.getOldPassword()) && !StringUtil.isBlank(params.getUserID())) {
            UserTableItem user = DAO.getInstance().getUserProfile(params.getUserID());

            try {
                String decodedOldPassword = new String(Base64.decode(params.getOldPassword()));
                String encryptedOldPassword = CryptoUtil.calculateHMACSignature(decodedOldPassword);

                if (!user.getPassword().equals(encryptedOldPassword)) {
                    JSONObject response = new JSONObject();
                    response.put("statusCode", 101);
                    response.put("message", "passwords mismatch");

                    return Response.ok()
                            .entity(response.toJSONString())
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Access-Control-Allow-Methods", "POST")
                            .allow("OPTIONS")
                            .build();
                }
                justUpdate = true;
                params.setEmailAddress(user.getEmailAddress());

            } catch (Base64DecodingException | NoSuchAlgorithmException | InvalidKeyException ex) {
                JSONObject response = new JSONObject();
                response.put("statusCode", 500);
                response.put("message", ex.getMessage());

                return Response.ok()
                        .entity(response.toJSONString())
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Access-Control-Allow-Methods", "POST")
                        .allow("OPTIONS")
                        .build();
            }

        }

        if (!StringUtil.isBlank(params.getConfirmPassword()) && !params.getNewPassword().equals(params.getConfirmPassword())) {
            JSONObject response = new JSONObject();
            response.put("statusCode", 101);
            response.put("message", "passwords mismatch");

            return Response.ok()
                    .entity(response.toJSONString())
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "POST")
                    .allow("OPTIONS")
                    .build();
        }

        ErrorResult res = DAO.getInstance().updatePassword(params.getEmailAddress(), params.getNewPassword(), params.getToken(), justUpdate);

        JSONObject response = new JSONObject();
        response.put("statusCode", res.getErrorCode());
        response.put("message", res.getMessage());
        if (res.getErrorCode() == 200) {
            response.put("userid", res.getData());
        }

        return Response.ok()
                .entity(response.toJSONString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "POST")
                .allow("OPTIONS")
                .build();
    }

    @Path("getUserAddresses")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserAddresses(@QueryParam("userID") String userID) {
        ErrorResult res = DAO.getInstance().getUserAddresses(userID);

        if (res.getErrorCode() == 200) {
            return Response.ok()
                    .entity(res.getData())
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "POST")
                    .allow("OPTIONS")
                    .build();
        } else {
            JSONObject response = new JSONObject();
            response.put("statusCode", res.getErrorCode());
            response.put("message", res.getMessage());

            return Response.ok()
                    .entity(response.toJSONString())
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "POST")
                    .allow("OPTIONS")
                    .build();
        }
    }

    @Path("removeAddress/{userID}/{addressID}")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeAddress(@PathParam("userID") String userID, @PathParam("addressID") String addressID) {
        ErrorResult res = DAO.getInstance().deleteAddress(userID, addressID);
        JSONObject response = new JSONObject();
        response.put("statusCode", res.getErrorCode());
        response.put("message", res.getMessage());

        return Response.ok()
                .entity(response.toJSONString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "POST")
                .allow("OPTIONS")
                .build();
    }

    @Path("addContactDetails/{userID}")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addContactDetails(@PathParam("userID") String userID, @FormParam("streetAddress") String streetAddress, @FormParam("apartment") String apartment, @FormParam("city") String city, @FormParam("state") String state, @FormParam("zipCode") String zipCode, @FormParam("phone") String phone, @FormParam("specialInstructions") String specialInstructions) {

        ErrorResult res = DAO.getInstance().addContactDetails(userID, streetAddress, apartment, city, state, zipCode, phone, specialInstructions);

        JSONObject response = new JSONObject();
        response.put("statusCode", res.getErrorCode());
        response.put("message", res.getMessage());
        if (res.getErrorCode() == 200) {
            response.put("userid", res.getData());
            response.put("addressid", res.getMiscellaneous());
        }

        return Response.ok()
                .entity(response.toJSONString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE")
                .allow("OPTIONS")
                .build();
    }

    @Path("updateContactDetails/{userID}")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateContactDetails(@PathParam("userID") String userID, @PathParam("userRequestID") String userRequestID, @FormParam("streetAddress") String streetAddress, @FormParam("apartment") String apartment, @FormParam("city") String city, @FormParam("state") String state, @FormParam("zipCode") String zipCode, @FormParam("phone") String phone, @FormParam("specialInstructions") String specialInstructions, @FormParam("addressID") String addressID) {

        ErrorResult res = DAO.getInstance().updateContactDetails(userID, streetAddress, apartment, city, state, zipCode, phone, specialInstructions, addressID);

        String response = "{\"response\" : \"" + res.getMessage() + "\"}";

        return Response.ok()
                .entity(response)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE")
                .allow("OPTIONS")
                .build();
    }

    @Path("login")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(LoginParameters user) {
        ErrorResult res = DAO.getInstance().login(user);

        JSONObject response = new JSONObject();
        response.put("statusCode", res.getErrorCode());
        response.put("message", res.getMessage());
        if (res.getErrorCode() == 200 || res.getErrorCode() == 201 || res.getErrorCode() == 203) {
            response.put("userid", res.getData());
            StringBuilder keyBuilder = new StringBuilder();
            keyBuilder.append(user.getEmailAddress()).append(":").append(user.getPassword()).append(":").append(res.getToken());
            String key = Base64.encode(keyBuilder.toString().getBytes());
            key = key.replaceAll("\n", "");
            response.put("key", key);
        }

        return Response.ok()
                .entity(response.toJSONString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE")
                .header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept")
                .allow("OPTIONS")
                .build();
    }

    @Path("getUserProfile")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserProfile(@QueryParam("userid") String userID) {
        UserTableItem user = DAO.getInstance().getUserProfile(userID);

        if (user == null) {
            JSONObject response = new JSONObject();
            response.put("statusCode", 101);
            response.put("message", "invalid userid");

            return Response.ok()
                    .entity(response.toJSONString())
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE")
                    .allow("OPTIONS")
                    .build();
        }

        user.setPassword(null);
        user.setToken(null);
        user.setWishlist(null);
        user.setNotifications(null);

        return Response.ok()
                .entity(user)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE")
                .allow("OPTIONS")
                .build();
    }

    @Path("getContactDetails")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getContactDetails(@QueryParam("userId") String userID) {
        UserTableItem user = DAO.getInstance().getUserProfile(userID);
        ContactDetails contactDetails = new ContactDetails();
        contactDetails.setPhoneNumber(user.getPhoneNumber() == null ? "" : user.getPhoneNumber());
        List<UserTableItem.Address> userAddresses = user.getAddress();

        if (userAddresses != null) {
            List<ContactDetails.Address> addresses = new ArrayList<>();
            for (UserTableItem.Address address : userAddresses) {
                ContactDetails.Address contactAddress = new ContactDetails.Address();
                contactAddress.setAddressID(address.getAddressID());
                contactAddress.setStreetAddress(address.getStreetAddress());
                contactAddress.setApartment(address.getApartment());
                contactAddress.setCity(address.getCity());
                contactAddress.setState(address.getState());
                contactAddress.setZipCode(address.getZipCode());
                contactAddress.setSpecialInstructions(address.getSpecialInstructions());

                addresses.add(contactAddress);
            }

            contactDetails.setAddresses(addresses);
        }

        return Response.ok()
                .entity(contactDetails)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE")
                .allow("OPTIONS")
                .build();
    }

    @Path("checkZipCode")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkZipCode(@QueryParam("zipcode") String zipCode, @QueryParam("uid") String userId) {

        ErrorResult res = DAO.getInstance().checkZipCode(zipCode, userId);

        JSONObject response = new JSONObject();
        response.put("statusCode", res.getErrorCode());
        response.put("message", res.getMessage());

        response.put("zipCode", res.getData());

        return Response.ok().entity(response.toJSONString()).header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    
    @Path("getStorages")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStorages() {
        List<StorageTableItem> planList = DAO.getInstance().getAllStorages();
        GenericEntity<List<StorageTableItem>> list = new GenericEntity<List<StorageTableItem>>(planList) {
        };

        return Response.ok()
                .entity(list)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }
    
    @Path("getPlans")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPlans() {
        List<MembershipTableItem> planList = DAO.getInstance().getAllPlans();
        GenericEntity<List<MembershipTableItem>> list = new GenericEntity<List<MembershipTableItem>>(planList) {
        };

        return Response.ok()
                .entity(list)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @Path("getMembership")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getMembership(@QueryParam("userID") String userID) {
        UserTableItem user = DAO.getInstance().getUserProfile(userID);

        if (user == null) {
            JSONObject response = new JSONObject();
            response.put("statusCode", 101);
            response.put("message", "invalid userid");
            return Response.ok()
                    .entity(response.toJSONString())
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                    .allow("OPTIONS")
                    .build();
        }

        UserTableItem.Membership membership = user.getMembership();

        if (membership == null) {
            JSONObject response = new JSONObject();
            response.put("statusCode", 120);
            response.put("message", "no subscriptions");
            return Response.ok()
                    .entity(response.toJSONString())
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                    .allow("OPTIONS")
                    .build();
        }

        return Response.ok()
                .entity(membership)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @Path("addNewCard")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addNewCard(AddNewCard card) {

        ErrorResult res = DAO.getInstance().addNewCard(card);

        JSONObject response = new JSONObject();
        response.put("statusCode", res.getErrorCode());
        response.put("message", res.getMessage());

        return Response.ok()
                .entity(response.toJSONString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @Path("deleteCard")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCard(DeleteCardParameters params) {
        ErrorResult res = DAO.getInstance().deleteCard(params);

        JSONObject response = new JSONObject();
        response.put("statusCode", res.getErrorCode());
        response.put("message", res.getMessage());

        return Response.ok()
                .entity(response.toJSONString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @Path("getCards")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCards(@QueryParam("userID") String userID) {

        List<UserTableItem.Payments> cards = DAO.getInstance().getUserPayments(userID);

        if (cards == null) {
            cards = new ArrayList<>();
        }

        GenericEntity<List<UserTableItem.Payments>> list = new GenericEntity<List<UserTableItem.Payments>>(cards) {
        };

        return Response.ok()
                .entity(list)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @Path("addSubscription")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addCustomerToSubscription(MembershipSubscriptionParameters subscriptionParameters) {
        UserTableItem user = DAO.getInstance().getUserProfile(subscriptionParameters.getUserid());
        MembershipTableItem membershipPlan = DAO.getInstance().getMemberShipPlan(subscriptionParameters.getPlanid());
        if (user == null) {
            return Response.ok().entity("{\"response\":\"invalid user\"}")
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                    .allow("OPTIONS")
                    .build();
        }

        if (membershipPlan == null) {
            return Response.ok().entity("{\"response\":\"invalid plan\"}")
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                    .allow("OPTIONS")
                    .build();
        }

        if (subscriptionParameters.getReferralCode() != null) {
            // JIRA issue CON-6
            if (subscriptionParameters.getReferralCode().equalsIgnoreCase("bwalpha")) {
                subscriptionParameters.setReferralCode("admin100");
            }
            if (!subscriptionParameters.getReferralCode().equalsIgnoreCase("admin100")) {
                if (!DAO.getInstance().isReferralCodeValid(subscriptionParameters.getReferralCode())) {
                    return Response.ok().entity("{\"response\":\"invalid referral code\"}")
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                            .allow("OPTIONS")
                            .build();
                }
            }
        }

        String token = subscriptionParameters.getToken();
        AdvancedEncryptionStandard aes = new AdvancedEncryptionStandard(user.getUserID().replaceAll("-", ""));

        try {
            String paramsCardNumber = null;
            if (user.getStripeID() == null || user.getStripeID().equals("")) {

                if (token != null) {
                    try {
                        if (!StringUtil.isBlank(subscriptionParameters.getCardNumber())) {
                            subscriptionParameters.setCardNumber(aes.decrypt(subscriptionParameters.getCardNumber()));
                        }
                        subscriptionParameters.setStripeCustomerID(StripeService.createUser(token, user.getEmailAddress()));
                    } catch (Exception ex) {
                        JSONObject response = new JSONObject();
                        response.put("statusCode", 500);
                        response.put("message", ex.getMessage());

                        return Response.ok().entity(response.toJSONString())
                                .header("Access-Control-Allow-Origin", "*")
                                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                                .allow("OPTIONS")
                                .build();
                    }
                } else if (StringUtil.isBlank(subscriptionParameters.getCardNumber())) {
                    JSONObject response = new JSONObject();
                    response.put("response", "missing card parameters");

                    return Response.ok().entity(response.toJSONString())
                            .header("Access-Control-Allow-Origin", "*")
                            .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                            .allow("OPTIONS")
                            .build();
                } else {
                    try {
                        paramsCardNumber = aes.decrypt(subscriptionParameters.getCardNumber());
                    } catch (Exception ex) {
                        JSONObject response = new JSONObject();
                        response.put("statusCode", 500);
                        response.put("message", ex.getMessage());

                        return Response.ok().entity(response.toJSONString())
                                .header("Access-Control-Allow-Origin", "*")
                                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                                .allow("OPTIONS")
                                .build();
                    }
                }

                String stripeID = subscriptionParameters.getStripeCustomerID();
                user.setStripeID(stripeID);
            }

            String subscriptionID;
            if (subscriptionParameters.getReferralCode() != null) {
                if (subscriptionParameters.getReferralCode().equalsIgnoreCase("admin100")) {
                    subscriptionID = StripeService.createSubscription(user.getStripeID(), membershipPlan.getStripePlanID(), "ADMIN100");
                } else {
                    subscriptionID = StripeService.createSubscription(user.getStripeID(), membershipPlan.getStripePlanID(), "REFBATH20");
                }
                //pending
                ReferralCodeMapper refMapper = DAO.getInstance().getReferralCodeMapperById(subscriptionParameters.getReferralCode());

                if (refMapper == null) {
                    refMapper = new ReferralCodeMapper();
                    refMapper.setReferralCode(subscriptionParameters.getReferralCode());
                }

                if (refMapper.getUsedUserIDs() == null) {
                    refMapper.setUsedUserIDs(new ArrayList<String>());
                }

                if (!refMapper.getUsedUserIDs().contains(subscriptionParameters.getUserid())) {
                    refMapper.getUsedUserIDs().add(subscriptionParameters.getUserid());
                }

                DAO.getInstance().saveItem(refMapper);
            } else {
                subscriptionID = StripeService.createSubscription(user.getStripeID(), membershipPlan.getStripePlanID(), null);
            }
            UserTableItem.Membership userMembership = new UserTableItem.Membership();
            userMembership.setName(membershipPlan.getName());
            userMembership.setDescription(membershipPlan.getDescription());
            userMembership.setPrice(membershipPlan.getPrice());
            userMembership.setInterval(membershipPlan.getInterval());
            userMembership.setStripeSubscriptionID(subscriptionID);
            userMembership.setPoints(membershipPlan.getKickOffPoints());

            String cardNumber;
            if (!StringUtil.isBlank(subscriptionParameters.getCardNumber())) {
                cardNumber = subscriptionParameters.getCardNumber();
            } else {
                cardNumber = "APPLE PAY";
            }
            List<UserTableItem.Payments> payments = user.getPayments();

            if (payments == null) {
                payments = new ArrayList<>();
            }

            UserTableItem.Payments payment = new UserTableItem.Payments();
            payment.setCardID(StripeService.getDefaultCardId(user.getStripeID()));
            payment.setCardNumber(cardNumber);
            payment.setToken(token);

            if (!StringUtil.isBlank(subscriptionParameters.getCardType())) {
                payment.setCardType(subscriptionParameters.getCardType());
            }

            payments.add(payment);
            user.setPayments(payments);
            user.setMembership(userMembership);

            user.setCredits(membershipPlan.getKickOffPoints());
            DAO.getInstance().saveItem(user);

        } catch (AuthenticationException | InvalidRequestException | APIConnectionException | CardException | APIException ex) {
            JSONObject response = new JSONObject();
            response.put("response", ex.getMessage());

            return Response.ok().entity(response.toJSONString())
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                    .allow("OPTIONS")
                    .build();
        }

        return Response.ok()
                .entity("{\"response\":\"success\"}")
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @Path("unsubscribeUser")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response unSubscribeUser(String jsonStr) {

        JSONParser parser = new JSONParser();

        try {
            JSONObject params = (JSONObject) parser.parse(jsonStr);
            String userID = (String) params.get("userid");
            String subscriptionID = (String) params.get("subscriptionid");

            UserTableItem user = DAO.getInstance().getUserProfile(userID);

            if (user == null) {
                return Response.ok().entity("{\"response\":\"invalid user\"}")
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                        .allow("OPTIONS")
                        .build();
            }

            UserTableItem.Membership userMembership = user.getMembership();

            if (userMembership == null || !userMembership.getStripeSubscriptionID().equals(subscriptionID)) {
                return Response.ok().entity("{\"response\":\"invalid subscription\"}")
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                        .allow("OPTIONS")
                        .build();
            }

            if (StripeService.deleteSubscription(user.getStripeID(), subscriptionID)) {
                user.setMembership(null);

                // Should the points be deducted from the user on unsubscription -- ???
                DAO.getInstance().saveItem(user);
            } else {
                return Response.serverError().entity("{\"response\":\"internal error\"}")
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                        .allow("OPTIONS")
                        .build();
            }

        } catch (org.json.simple.parser.ParseException | AuthenticationException | InvalidRequestException | APIConnectionException | CardException | APIException ex) {
            Logger.getLogger(BathwaterRest.class.getName()).log(Level.SEVERE, null, ex);
        }

        return Response.ok()
                .entity("{\"response\":\"success\"}")
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @Path("getTimeslots")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTimeslots() {

        try {
            List<TimeslotTableItem> timeslots = DAO.getInstance().getAvailableTimeSlots();
            GenericEntity<List<TimeslotTableItem>> list = new GenericEntity<List<TimeslotTableItem>>(timeslots) {
            };

            return Response.ok()
                    .entity(list)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                    .allow("OPTIONS")
                    .build();
        } catch (ParseException ex) {
            Logger.getLogger(BathwaterRest.class.getName()).log(Level.SEVERE, null, ex);
        }

        return Response.serverError()
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @Path("getTimeslotsForTheWeek")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTimeslotsForTheWeek() {

        WeeksTimeslots weeksTimeslots = DAO.getInstance().getTimeslotsForTheWeek();

        return Response.ok()
                .entity(weeksTimeslots)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @Path("schedulePickup")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response schedulePickup(PickupRequestParameters pickupRequest) {
        ErrorResult res = DAO.getInstance().schedulePickup(pickupRequest);

        String response = "{\"response\" : \"" + res.getMessage() + "\"}";

        return Response.ok()
                .entity(response)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization")
                .allow("OPTIONS")
                .build();
    }

    @Path("getCategories")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCategories(@QueryParam("parentID") String parentID) {
        parentID = parentID == null || parentID.equals("") ? "0" : parentID;
        List<CategoryTableItem> categories = DAO.getInstance().getCategories(parentID);
        GenericEntity<List<CategoryTableItem>> list = new GenericEntity<List<CategoryTableItem>>(categories) {
        };

        return Response.ok()
                .entity(list)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @Path("suggest")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSuggestions(@QueryParam("text") String text) {
        List<String> options = DAO.getInstance().getAutoCompleteSuggestions(text);

        JSONObject response = new JSONObject();

        response.put("suggestions", options);
        return Response.ok()
                .entity(response.toJSONString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @Path("search")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchItems(@QueryParam("keywords") List<String> keywords) {
        List<InventoryTableItem> items = DAO.getInstance().searchItems(keywords);

        GenericEntity<List<InventoryTableItem>> list = new GenericEntity<List<InventoryTableItem>>(items) {
        };

        return Response.ok()
                .entity(list)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @Path("lockSwapItem")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response lockItem(LockItemParameters params) throws ParseException {

        ErrorResult res = DAO.getInstance().lockItem(params);

        JSONObject response = new JSONObject();
        response.put("statusCode", res.getErrorCode());
        response.put("message", res.getMessage());

        return Response.ok()
                .entity(response.toJSONString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @Path("getItems")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getItems(@QueryParam("filterID") String filterID, @QueryParam("userID") String userID) {

        List<InventoryTableItem> items = DAO.getInstance().getBathwaterItems(filterID, userID);
        GenericEntity<List<InventoryTableItem>> list = new GenericEntity<List<InventoryTableItem>>(items) {
        };

        return Response.ok()
                .entity(list)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @Path("getItem")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getItemById(@QueryParam("itemID") String itemID) {

        try {
            ErrorResult res = DAO.getInstance().getItemByID(itemID);

            if (res.getErrorCode() == 200) {
                return Response.ok()
                        .entity(res.getData())
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                        .allow("OPTIONS")
                        .build();
            }

            JSONObject response = new JSONObject();
            response.put("statusCode", res.getErrorCode());
            response.put("message", res.getMessage());

            return Response.ok()
                    .entity(response.toJSONString())
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                    .allow("OPTIONS")
                    .build();
        } catch (JsonProcessingException ex) {
            JSONObject response = new JSONObject();
            response.put("statusCode", 500);
            response.put("message", ex.getMessage());

            return Response.ok()
                    .entity(response.toJSONString())
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                    .allow("OPTIONS")
                    .build();
        }

    }

    @Path("getUserItems")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserItems(@QueryParam("userid") String userID) {

        if (StringUtil.isBlank(userID)) {
            JSONObject response = new JSONObject();
            response.put("statusCode", 101);
            response.put("message", "invalid userid");

            return Response.ok()
                    .entity(response.toJSONString())
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                    .allow("OPTIONS")
                    .build();
        }

        UserTableItem user = DAO.getInstance().getUserProfile(userID);

        if (user == null) {
            JSONObject response = new JSONObject();
            response.put("statusCode", 101);
            response.put("message", "invalid userid");

            return Response.ok()
                    .entity(response.toJSONString())
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                    .allow("OPTIONS")
                    .build();
        }

        List<InventoryTableItem> items = DAO.getInstance().getItemsByUserID(userID);
        GenericEntity<List<InventoryTableItem>> list = new GenericEntity<List<InventoryTableItem>>(items) {
        };

        return Response.ok()
                .entity(list)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @Path("getUserTS")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUserTimeslots(@QueryParam("statusType") String status) {
        List<UserRequestTableItem> requests = DAO.getInstance().getAllUserRequests(status);

        List<UserRequestTableItem> requestList = new LinkedList<>(requests);

        Collections.sort(requestList, new UserRequestComparator());

        GenericEntity<List<UserRequestTableItem>> list = new GenericEntity<List<UserRequestTableItem>>(requestList) {
        };

        return Response.ok()
                .entity(list)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @Path("getPromos")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllPromos() {

        List<String> list = PromoCodeDatabaseUtil.getAllFileNames();

        JSONArray array = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            JSONObject obj = new JSONObject();
            obj.put("name", list.get(i));
            array.add(obj);
        }

        return Response.ok()
                .entity(array.toJSONString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @Path("getUsers")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers() {
        List<UserTableItem> users = DAO.getInstance().getAllUsers();
        GenericEntity<List<UserTableItem>> list = new GenericEntity<List<UserTableItem>>(users) {
        };

        return Response.ok()
                .entity(list)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @Path("swapItem/{userID}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response swapItem(@PathParam("userID") String userID, @QueryParam("itemID") String itemID) {
        JSONObject response = new JSONObject();

        UserTableItem user = DAO.getInstance().getUserProfile(userID);

        ErrorResult res = DAO.getInstance().swapItem(user, itemID);

        user = (UserTableItem) res.getMiscellaneous();
        DAO.getInstance().saveItem(user);

        response.put("statusCode", res.getErrorCode());
        response.put("message", res.getMessage());

        return Response.ok()
                .entity(response.toJSONString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "POST")
                .allow("OPTIONS")
                .build();

    }

    @Path("addToWishList")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response addToWishList(WishlistParameters params) {

        ErrorResult res = DAO.getInstance().addToWishlist(params.getUserID(), params.getItemID());

        JSONObject response = new JSONObject();
        response.put("statusCode", res.getErrorCode());
        response.put("message", res.getMessage());

        return Response.ok()
                .entity(response.toJSONString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "POST")
                .allow("OPTIONS")
                .build();
    }

    @Path("removeFromWishList")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeFromWishList(WishlistParameters params) {

        ErrorResult res = DAO.getInstance().removeFromWishlist(params.getUserID(), params.getItemID());

        JSONObject response = new JSONObject();
        response.put("statusCode", res.getErrorCode());
        response.put("message", res.getMessage());

        return Response.ok()
                .entity(response.toJSONString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "POST")
                .allow("OPTIONS")
                .build();

    }

    @Path("getWishList")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getWishList(@QueryParam("userID") String userID) {
        List<InventoryTableItem> items = DAO.getInstance().getUsersWishList(userID);
        GenericEntity<List<InventoryTableItem>> list = new GenericEntity<List<InventoryTableItem>>(items) {
        };

        return Response.ok()
                .entity(list)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @Path("verifyReferralCode")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response verifyReferralCode(@QueryParam("referralCode") String referralCode) {

        if (referralCode.equalsIgnoreCase("bwalpha")) {
            referralCode = "admin100";
        }
        boolean valid = DAO.getInstance().isReferralCodeValid(referralCode);

        ReferralCodeValidStatus status = new ReferralCodeValidStatus();

        if (valid) {
            status.setStatusCode(200);
            status.setMessage("valid");
            if (referralCode.equalsIgnoreCase("admin100")) {
                status = StripeService.populateCouponData(status, "ADMIN100");
            } else {
                status = StripeService.populateCouponData(status, "REFBATH02");
            }
        } else {
            status.setStatusCode(101);
            status.setMessage("invalid referral code");
        }

        return Response.ok()
                .entity(status)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "POST")
                .allow("OPTIONS")
                .build();
    }

    @Path("verifyPromo")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response verifyPromo(@QueryParam("promocode") String promoCode) {

        JSONObject response = new JSONObject();

        boolean valid = PromoCodeDatabaseUtil.isValidFile(promoCode);

        if (valid) {
            response.put("statusCode", 200);
            response.put("message", "valid");
            try {
                int discount = DAO.getInstance().getPromoCodeDiscout(promoCode);
                response.put("discount", discount);
            } catch (IOException ex) {
                response.put("statusCode", 203);
                response.put("message", "valid but error retrieving discount");
            }
        } else {
            response.put("statusCode", 120);
            response.put("message", "invalid");
        }

        return Response.ok()
                .entity(response.toJSONString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "POST")
                .allow("OPTIONS")
                .build();
    }

    @Path("shippingCharge")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response shippingCharge(ShippingChargeParmaeters params) {
        JSONObject response = new JSONObject();

        ErrorResult res = DAO.getInstance().getShippingCharge(params);

        response.put("statusCode", res.getErrorCode());
        response.put("message", res.getMessage());
        response.put("shippingCharge", Integer.parseInt(res.getData()));

        return Response.ok()
                .entity(response.toJSONString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "POST")
                .allow("OPTIONS")
                .build();
    }

    @Path("checkOut")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkOut(CheckoutList checkoutList) throws ParseException {
        JSONObject response = new JSONObject();

        ErrorResult res;
        try {
            res = DAO.getInstance().checkOut(checkoutList);
        } catch (IOException ex) {
            res = new ErrorResult();
            res.setErrorCode(125);
            res.setMessage("invalid promocode");
        }

        response.put("statusCode", res.getErrorCode());
        response.put("message", res.getMessage());

        return Response.ok()
                .entity(response.toJSONString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "POST")
                .allow("OPTIONS")
                .build();
    }

    @Path("getNotifications")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNotifications(@QueryParam("userID") String userID) {
        UserTableItem user = DAO.getInstance().getUserProfile(userID);

        if (user == null) {
            JSONObject response = new JSONObject();
            response.put("statusCode", "101");
            response.put("message", "invalid userid");
            return Response.ok()
                    .entity(response.toJSONString())
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "POST")
                    .allow("OPTIONS")
                    .build();
        }

        List<Notification> notifications = user.getNotifications();

        if (notifications == null) {
            notifications = new ArrayList<>();
        }

        JSONArray jsonArray = new JSONArray();
        for (Notification notification : notifications) {
            JSONObject jsonNotification = new JSONObject();
            jsonNotification.put("date", notification.getDate());
            JSONObject jsonItems = new JSONObject();
            JSONArray jsonEntry = new JSONArray();

            for (Map<String, String> map : notification.getParams()) {
                JSONArray jsonParams = new JSONArray();
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    JSONObject jsonObj = new JSONObject();
                    String key = entry.getKey();
                    String value = entry.getValue();
                    jsonObj.put("key", key);
                    jsonObj.put("value", value);
                    jsonParams.add(jsonObj);
                }
                JSONObject params = new JSONObject();
                params.put("params", jsonParams);
                jsonEntry.add(params);
            }

            jsonItems.put("entry", jsonEntry);
            jsonNotification.put("items", jsonItems);
            jsonArray.add(jsonNotification);
        }

        return Response.ok()
                .entity(jsonArray.toJSONString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "POST")
                .allow("OPTIONS")
                .build();
    }

    @Path("storeItem")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response storeItem(StoreItemParameters params) {
        ErrorResult res = DAO.getInstance().storeItem(params);
        String response = "{\"response\" : \"" + res.getMessage() + "\"}";

        return Response.ok()
                .entity(response)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @Path("shareItem")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response shareItem(ShareItemParameters params) {
        ErrorResult res = DAO.getInstance().shareItem(params);
        String response = "{\"response\" : \"" + res.getMessage() + "\"}";

        return Response.ok()
                .entity(response)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @Path("getSharingItems")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSharingItems() {
        List<InventoryTableItem> items = DAO.getInstance().getSharingItems();
        GenericEntity<List<InventoryTableItem>> list = new GenericEntity<List<InventoryTableItem>>(items) {
        };

        return Response.ok()
                .entity(list)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @Path("requestDropOff")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response requestDropOff(DropOffRequestParameters params) throws ParseException {

        ErrorResult res = DAO.getInstance().scheduleDropOff(params);

        JSONObject response = new JSONObject();
        response.put("statusCode", res.getErrorCode());
        response.put("message", res.getMessage());
        if (res.getErrorCode() == 200) {
            response.put("userRequestID", res.getData());
        } else if (res.getErrorCode() == 105) {
            response.put("invalidStoredItemID", res.getData());
        }

        return Response.ok()
                .entity(response.toJSONString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "POST")
                .allow("OPTIONS")
                .build();
    }

    @Path("cancelDropOff")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response cancelDropOff(CancelRequestParameters params) {

        ErrorResult res = DAO.getInstance().cancelRequest(params.getUserID(), params.getUserRequestID());

        JSONObject response = new JSONObject();

        response.put("statusCode", res.getErrorCode());
        response.put("message", res.getMessage());

        return Response.ok()
                .entity(response.toJSONString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "POST")
                .allow("OPTIONS")
                .build();
    }

    @Path("cancelPickup")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response cancelPickup(CancelRequestParameters params) {
        ErrorResult res = DAO.getInstance().cancelRequest(params.getUserID(), params.getUserRequestID());

        JSONObject response = new JSONObject();

        response.put("statusCode", res.getErrorCode());
        response.put("message", res.getMessage());

        return Response.ok()
                .entity(response.toJSONString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "POST")
                .allow("OPTIONS")
                .build();
    }
    //swap item case
    @Path("cancelPickupAndDropOff")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response cancelPickupAndDropOff(CancelPickupAndDropOffParameters params) {
        ErrorResult res = DAO.getInstance().cancelRequest(params.getUserID(), params.getPickupRequestID());

        if (res.getErrorCode() == 200) {
            res = DAO.getInstance().cancelRequest(params.getUserID(), params.getDropOffRequestID());
        }

        JSONObject response = new JSONObject();

        response.put("statusCode", res.getErrorCode());
        response.put("message", res.getMessage());

        return Response.ok()
                .entity(response.toJSONString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "POST")
                .allow("OPTIONS")
                .build();
    }

    @Path("activateUser")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response activateUser(@QueryParam("uid") String userID) {
        ErrorResult res = DAO.getInstance().activateUser(userID);

        String response = "{\"response\":\"" + res.getMessage() + "\"}";

        return Response.ok()
                .entity(response)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE")
                .allow("OPTIONS")
                .build();
    }

    @Path("deactivateUser")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response deactivateUser(@QueryParam("uid") String userID) {
        ErrorResult res = DAO.getInstance().deactivateUser(userID);

        String response = "{\"response\":\"" + res.getMessage() + "\"}";

        return Response.ok()
                .entity(response)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE")
                .allow("OPTIONS")
                .build();
    }

    @Path("getCategory")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCategory(@QueryParam("id") String id) {
        CategoryTableItem cateogry = DAO.getInstance().getCategoryByID(id);

        return Response.ok()
                .entity(cateogry)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE")
                .allow("OPTIONS")
                .build();
    }

    @Path("updateCategory")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCategory(@QueryParam("id") String categoryId, @QueryParam("title") String title, @QueryParam("desc") String desc, @QueryParam("parentid") String parentid, @QueryParam("image") String imageName) {
        ErrorResult res = DAO.getInstance().updateCategory(categoryId, title, desc, parentid, imageName);
        String response = "{\"response\" : \"" + res.getMessage() + "\"}";

        return Response.ok()
                .entity(response)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE")
                .allow("OPTIONS")
                .build();
    }

    @Path("createCategory")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCategory(@FormParam("title") String title, @FormParam("desc") String description, @FormParam("parentid") String parentId) {
        ErrorResult res = DAO.getInstance().createCategory(title, description, parentId, null);
        String response = "{\"response\" : \"" + res.getMessage() + "\"}";

        return Response.ok()
                .entity(response)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE")
                .allow("OPTIONS")
                .build();
    }

    @Path("deleteCategory")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCategory(@QueryParam("id") String id) {
        ErrorResult res = DAO.getInstance().deleteCategory(id);
        String response = "{\"response\" : \"" + res.getMessage() + "\"}";

        return Response.ok()
                .entity(response)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE")
                .allow("OPTIONS")
                .build();
    }

    @Path("getProductImages")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProductImages(@QueryParam("pid") String productId) {
        List<ProductImages> productImages = DAO.getInstance().getProductImages(productId);

        if (productImages == null) {
            JSONObject response = new JSONObject();
            response.put("statusCode", 105);
            response.put("message", "invalid productid");

            return Response.ok()
                    .entity(response.toJSONString())
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE")
                    .allow("OPTIONS")
                    .build();
        }

        GenericEntity<List<ProductImages>> list = new GenericEntity<List<ProductImages>>(productImages) {
        };
        return Response.ok()
                .entity(list)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE")
                .allow("OPTIONS")
                .build();
    }

    @Path("createTimeslotForToday")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTimeSlot() throws ParseException {
        DateFormat formatter = new SimpleDateFormat("MM.dd.yyyy");
        String today = formatter.format(new Date());
        ErrorResult res = DAO.getInstance().createTimeSlot(today, "10am-12pm", 10);

        JSONObject response = new JSONObject();
        response.put("statusCode", 200);
        response.put("message", res.getMessage());
        response.put("tsID", res.getData());

        return Response.ok()
                .entity(response.toJSONString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE")
                .allow("OPTIONS")
                .build();
    }

    @Path("createTimeslotForDate/{date}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTimeSlot(@PathParam("date") String date) {
        ErrorResult res = DAO.getInstance().createTimeSlot(date, "10am-12pm", 10);

        JSONObject response = new JSONObject();
        response.put("statusCode", 200);
        response.put("message", res.getMessage());
        response.put("tsID", res.getData());

        return Response.ok()
                .entity(response.toJSONString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE")
                .allow("OPTIONS")
                .build();
    }

    @Path("createTimeSlotsRange")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTimeSlot(@FormParam("days") List<String> days,
            @FormParam("until") String dateUntil,
            @FormParam("start") String startDate,
            @FormParam("timeslots") List<String> timeslots,
            @FormParam("available") List<Integer> availabilityCount) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("MM.dd.yyyy");
        SimpleDateFormat dayFormatter = new SimpleDateFormat("EE");

        List<String> timesList = new ArrayList<>();
        timesList.add("8am-10am");
        timesList.add("10am-12pm");
        timesList.add("12pm-2pm");
        timesList.add("2pm-4pm");
        timesList.add("4pm-6pm");
        timesList.add("6pm-8pm");

        // swagger gives a csv list of days in a single element of the array
        if (days.size() == 1) {
            String[] daysArray = days.get(0).split(",");
            days = Arrays.asList(daysArray);
        }

        try {

            Date startdate = dateFormatter.parse(startDate);
            Calendar start = Calendar.getInstance();
            start.setTime(startdate);

            Date endDate = dateFormatter.parse(dateUntil);
            Calendar end = Calendar.getInstance();
            end.setTime(endDate);

            for (Date date = start.getTime(); start.before(end); start.add(Calendar.DATE, 1), date = start.getTime()) {
                String dayOfWeek = dayFormatter.format(date);
                if (days.contains(dayOfWeek)) {
                    for (int i = 0; i < timeslots.size(); i++) {
                        int availability = availabilityCount.get(Integer.parseInt(timeslots.get(i))) == null ? 0 : availabilityCount.get(Integer.parseInt(timeslots.get(i)));
                        DAO.getInstance().createTimeSlot(dateFormatter.format(date), timesList.get(Integer.parseInt(timeslots.get(i))), availability);
                    }
                }
            }

        } catch (ParseException ex) {
//            Logger.getLogger(BathwaterRest.class.getName()).log(Level.SEVERE, null, ex);
            return Response.ok()
                    .entity("{\"response\" : \"invalid date format\"}")
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE")
                    .allow("OPTIONS")
                    .build();
        }

        return Response.ok()
                .entity("{\"response\" : \"success\"}")
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE")
                .allow("OPTIONS")
                .build();
    }

    @Path("createTimeSlot")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTimeSlot(@FormParam("days") List<String> days,
            @FormParam("until") String dateUntil,
            @FormParam("timeslots") List<String> timeslots,
            @FormParam("available") List<Integer> availabilityCount) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("MM.dd.yyyy");
        SimpleDateFormat dayFormatter = new SimpleDateFormat("EE");

        List<String> timesList = new ArrayList<>();
        timesList.add("8am-10am");
        timesList.add("10am-12pm");
        timesList.add("12pm-2pm");
        timesList.add("2pm-4pm");
        timesList.add("4pm-6pm");
        timesList.add("6pm-8pm");

        // swagger gives a csv list of days in a single element of the array
        if (days.size() == 1) {
            String[] daysArray = days.get(0).split(",");
            days = Arrays.asList(daysArray);
        }

        try {
            Date today = new Date();
            Calendar start = Calendar.getInstance();
            start.setTime(today);

            Date endDate = dateFormatter.parse(dateUntil);
            Calendar end = Calendar.getInstance();
            end.setTime(endDate);

            for (Date date = start.getTime(); start.before(end); start.add(Calendar.DATE, 1), date = start.getTime()) {
                String dayOfWeek = dayFormatter.format(date);
                if (days.contains(dayOfWeek)) {
                    for (int i = 0; i < timeslots.size(); i++) {
                        int availability = availabilityCount.get(Integer.parseInt(timeslots.get(i))) == null ? 0 : availabilityCount.get(Integer.parseInt(timeslots.get(i)));
                        DAO.getInstance().createTimeSlot(dateFormatter.format(date), timesList.get(Integer.parseInt(timeslots.get(i))), availability);
                    }
                }
            }

        } catch (ParseException ex) {
//            Logger.getLogger(BathwaterRest.class.getName()).log(Level.SEVERE, null, ex);
            return Response.ok()
                    .entity("{\"response\" : \"invalid date format\"}")
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE")
                    .allow("OPTIONS")
                    .build();
        }

        return Response.ok()
                .entity("{\"response\" : \"success\"}")
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE")
                .allow("OPTIONS")
                .build();
    }

    @Path("uploadImage")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response image(@QueryParam("pid") String pId, byte[] imageData) {
        String res = "fail";
        String fName = System.currentTimeMillis() + ".jpg";

        try {
            InputStream inStream = new ByteArrayInputStream(imageData);
            ImageDatabaseUtil.uploadImage(fName, inStream);
            DAO dao = DAO.getInstance();
            res = dao.addProductImage(pId, fName);
        } catch (Exception ex) {
            Logger.getLogger(BathwaterRest.class.getName()).log(Level.SEVERE, null, ex);
        }

        String response = "{\"result\":\"" + res + "\"}";
        return Response.ok().entity(response).header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @Path("downloadImage")
    @GET
    public Response downloadImage(@QueryParam("imgname") String imageName) {
        InputStream inStream = ImageDatabaseUtil.downloadImage(imageName);
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        int nRead;
        byte[] buffer = new byte[1024];

        try {
            while ((nRead = inStream.read(buffer, 0, buffer.length)) > 0) {
                outStream.write(buffer);
            }

            outStream.flush();
        } catch (Exception ex) {
            Logger.getLogger(BathwaterRest.class.getName()).log(Level.SEVERE, null, ex);
        }

        return Response.ok(outStream.toByteArray(), MediaType.APPLICATION_OCTET_STREAM).header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept")
                .allow("OPTIONS")
                .build();
    }

    @Path("uploadCategoryImage/{categoryID}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadCategoryImage(@PathParam("categoryID") String categoryID, String base64) {

        String imageName = System.currentTimeMillis() + ".jpg";

        String response = "{\"result\":\"fail\"}";
        try {
            byte[] uploadImage = Base64.decode(base64);
            InputStream inStream = new ByteArrayInputStream(uploadImage);
            ImageDatabaseUtil.uploadImage(imageName, inStream);
            String res = DAO.getInstance().addCategoryImage(categoryID, imageName);
            response = "{\"result\":\"" + res + "\"}";
        } catch (IOException | Base64DecodingException | InterruptedException ex) {
            Logger.getLogger(BathwaterRest.class.getName()).log(Level.SEVERE, null, ex);
        }

        return Response.ok()
                .entity(response)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @Path("uploadImage")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadProductImage(ImageString image) {
        JSONObject response = new JSONObject();

        String fileName = System.currentTimeMillis() + ".jpg";

        try {
            byte[] uploadImage = Base64.decode(image.getImageBase64());
            InputStream inStream = new ByteArrayInputStream(uploadImage);
            ImageDatabaseUtil.uploadImage(fileName, inStream);
            String url = "https://s3.amazonaws.com/" + ImageDatabaseUtil.getBUCKET_NAME() + "/" + fileName;
            response.put("statusCode", 200);
            response.put("message", "success");
            response.put("url", url);
        } catch (IOException | Base64DecodingException | InterruptedException ex) {
            Logger.getLogger(BathwaterRest.class.getName()).log(Level.SEVERE, null, ex);
            response.put("statusCode", 500);
            response.put("message", "failure");
        }

        return Response.ok()
                .entity(response.toJSONString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @Path("addZipCode")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addZipCode(@FormParam("zipCode") String zipCode) {

        String res = DAO.getInstance().addZipCode(zipCode);

        String response = "{\"response\":\"" + res + "\"}";

        return Response.ok()
                .entity(response)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @Path("getZipCodes")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllZipCodes() {
        List<ServiceZipCodeTableItem> zipCodes = DAO.getInstance().getAllZipCodeTableItems();
        GenericEntity<List<ServiceZipCodeTableItem>> list = new GenericEntity<List<ServiceZipCodeTableItem>>(zipCodes) {
        };

        return Response.ok()
                .entity(list)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @Path("deleteZipCode/{zipCode}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteZipCode(@PathParam("zipCode") String zipCode) {
        DAO.getInstance().deleteZipCode(zipCode);

        return Response.ok()
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @Path("deletePromoCode/{promoCode}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response deletePromoCode(@PathParam("promoCode") String promoCode) {
        PromoCodeDatabaseUtil.deleteFile(promoCode);

        return Response.ok()
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @Path("setCreditsForItem")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response setCreditsForItem(SetCreditsParameters params) {
        ErrorResult res = DAO.getInstance().setCreditsToItem(params);
        JSONObject response = new JSONObject();

        response.put("statusCode", res.getErrorCode());
        response.put("message", res.getMessage());

        return Response.ok()
                .entity(response.toJSONString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @Path("requestPickup")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response requestPickup(PickupRequestParameters params) throws ParseException {
        ErrorResult res = DAO.getInstance().schedulePickup(params.getUsrid(), params.getAddressID(), params.getTsID());

        JSONObject response = new JSONObject();
        response.put("statusCode", res.getErrorCode());
        response.put("message", res.getMessage());

        if (res.getErrorCode() == 200) {
            response.put("userRequestID", res.getData());
        }

        return Response.ok()
                .entity(response.toJSONString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @Path("requestTimeslot")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response requestTimeslot(@QueryParam("usrid") String userId, @QueryParam("tsID") String timeslotID, @QueryParam("request") String requestType) {
        ErrorResult res = DAO.getInstance().scheduleRequest(userId, timeslotID, requestType);

        JSONObject response = new JSONObject();
        response.put("statusCode", res.getErrorCode());
        response.put("message", res.getMessage());

        if (res.getErrorCode() == 200) {
            response.put("userRequestID", res.getData());
        }

        return Response.ok()
                .entity(response.toJSONString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @Path("addDriver")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addDriver(@FormParam("firstName") String firstName,
            @FormParam("lastName") String lastName,
            @FormParam("licenseId") String licenseId,
            @FormParam("streetAddress") String streetAddress,
            @FormParam("apartment") String apartment,
            @FormParam("city") String city,
            @FormParam("state") String state,
            @FormParam("zipCode") String zipCode,
            @FormParam("phoneNumber") String phoneNumber,
            @FormParam("emergencyPhoneNumber") String emergencyPhoneNumber,
            @FormParam("emailAddress") String emailAddress) {

        ErrorResult res = DAO.getInstance().addDriver(firstName, lastName, licenseId, streetAddress, apartment, city, state, zipCode, phoneNumber, emergencyPhoneNumber, emailAddress);
        String response = "{\"response\" : \"" + res.getMessage() + "\" }";
        return Response.ok()
                .entity(response)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @Path("addTruck")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    //@Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addTruck(@FormParam("truckType") String truckType,
            @FormParam("licensePlate") String licensePlate,
            @FormParam("dealerName") String dealerName,
            @FormParam("dealerStreetAddress") String streetAddress,
            @FormParam("dealerCity") String city,
            @FormParam("dealerState") String state,
            @FormParam("dealerZip") String zipCode,
            @FormParam("dealerPhNumber") String phoneNumber,
            @FormParam("leaseExpirationDate") String leaseExpirationDate
    ) {
        ErrorResult res = DAO.getInstance().addTruck(truckType, licensePlate, dealerName, streetAddress, city, state, zipCode, phoneNumber, leaseExpirationDate);

        String response = "{\"response\" : \"" + res.getMessage() + "\" }";

        return Response.ok()
                .entity(response)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @Path("addTruckwithImage")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response addTruckwithImage(String jsonStr) {
        JSONObject response = new JSONObject();

        Logger.getLogger(BathwaterRest.class.getName()).log(Level.INFO, jsonStr);

        try {
            JSONParser parser = new JSONParser();
            String tt;
            String lp, dn, dsa, dc;
            String ds, dz, dpn, led, imageBase64;
            JSONObject json = (JSONObject) parser.parse(jsonStr);
            tt = (String) json.get("truckType");
            lp = (String) json.get("licensePlate");
            dn = (String) json.get("dealerName");
            dsa = (String) json.get("dealerStreetAddress");
            dc = (String) json.get("dealerCity");
            ds = (String) json.get("dealerState");
            dz = (String) json.get("dealerZip");
            dpn = (String) json.get("dealerPhNumber");
            led = (String) json.get("leaseExpirationDate");
            imageBase64 = (String) json.get("truckImage");

            ErrorResult res = DAO.getInstance().addTruck(tt, lp, dn, dsa, dc, ds, dz, dpn, led);

            String imageName = System.currentTimeMillis() + ".jpg";

            String resStr = "{\"result\":\"fail\"}";
            try {
                byte[] uploadImage = Base64.decode(imageBase64);
                InputStream inStream = new ByteArrayInputStream(uploadImage);
                ImageDatabaseUtil.uploadImage(imageName, inStream);
                ErrorResult resp = DAO.getInstance().addTruckImage(res.getData(), imageName);
                resStr = "{\"result\":\"" + res.getMessage() + "\"}";
            } catch (IOException | Base64DecodingException | InterruptedException ex) {
                Logger.getLogger(BathwaterRest.class.getName()).log(Level.SEVERE, null, ex);
            }

            return Response.ok()
                    .entity(resStr)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                    .allow("OPTIONS")
                    .build();

        } catch (Exception ex) {
            response.put("statusCode", 500);
            response.put("message", ex.getMessage());
        }

        return Response
                .ok()
                .entity(response.toJSONString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @Path("uploadTruckImage/{truckID}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadTruckImage(@PathParam("truckID") String truckID, String base64) {
        String imageName = System.currentTimeMillis() + ".jpg";

        String response = "{\"result\":\"fail\"}";
        try {
            byte[] uploadImage = Base64.decode(base64);
            InputStream inStream = new ByteArrayInputStream(uploadImage);
            ImageDatabaseUtil.uploadImage(imageName, inStream);
            ErrorResult res = DAO.getInstance().addTruckImage(truckID, imageName);
            response = "{\"result\":\"" + res.getMessage() + "\"}";
        } catch (IOException | Base64DecodingException | InterruptedException ex) {
            Logger.getLogger(BathwaterRest.class.getName()).log(Level.SEVERE, null, ex);
        }

        return Response.ok()
                .entity(response)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @Path("uploadDriverImage/{driverID}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadDriverImage(@PathParam("driverID") String driverID, String base64) {
        String imageName = System.currentTimeMillis() + ".jpg";

        String response = "{\"result\":\"fail\"}";
        try {
            byte[] uploadImage = Base64.decode(base64);
            InputStream inStream = new ByteArrayInputStream(uploadImage);
            ImageDatabaseUtil.uploadImage(imageName, inStream);
            ErrorResult res = DAO.getInstance().addDriverImage(driverID, imageName);
            response = "{\"result\":\"" + res.getMessage() + "\"}";
        } catch (IOException | Base64DecodingException | InterruptedException ex) {
            Logger.getLogger(BathwaterRest.class.getName()).log(Level.SEVERE, null, ex);
        }

        return Response.ok()
                .entity(response)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @Path("uploadPromoFile/{name}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadPromoFile(@PathParam("name") String fName, String base64) {
        String imageName = fName;

        String response = "{\"result\":\"fail\"}";
        try {
            //byte[] uploadImage = Base64.decode(base64);
            InputStream inStream = new ByteArrayInputStream(base64.getBytes());
            //Promo.uploadImage(imageName, inStream);
            PromoCodeDatabaseUtil.uploadFile(imageName, inStream);
            ErrorResult res = new ErrorResult();
            res.setMessage("success");
            response = "{\"result\":\"" + res.getMessage() + "\"}";
        } catch (Exception ex) {
            Logger.getLogger(BathwaterRest.class.getName()).log(Level.SEVERE, null, ex);
        }

        return Response.ok()
                .entity(response)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @Path("getAllDrivers")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllDrivers() {
        List<DriverTableItem> drivers = DAO.getInstance().getAllDrivers();
        GenericEntity<List<DriverTableItem>> list = new GenericEntity<List<DriverTableItem>>(drivers) {
        };

        return Response.ok()
                .entity(list)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @Path("getAllTrucks")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllTrucks() {
        List<TruckTableItem> trucks = DAO.getInstance().getAllTrucks();
        GenericEntity<List<TruckTableItem>> list = new GenericEntity<List<TruckTableItem>>(trucks) {
        };

        return Response.ok()
                .entity(list)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @Path("assignDriverToTruck/{truckID}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response assignDriverToTruck(@PathParam("truckID") String truckID, @QueryParam("driverID") String driverID) {
        ErrorResult res = DAO.getInstance().assignDriverToTruck(truckID, driverID);

        String response = "{\"response\" : \"" + res.getMessage() + "\"}";

        return Response.ok()
                .entity(response)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @Path("cancelDriverAssignment/{truckID}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response cancelDriverAssignment(@PathParam("truckID") String truckID, 
            @QueryParam("driverID") String driverID,
            @QueryParam("date") String date) {
        ErrorResult res = DAO.getInstance().cancelDriverAssignment(truckID, driverID, date);

        String response = "{\"response\" : \"" + res.getMessage() + "\"}";

        return Response.ok()
                .entity(response)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }
    
    @Path("assignDriverToUserRequest/{driverID}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response assignDriverToUserRequest(@PathParam("driverID") String driverID, @QueryParam("userReqID") String userRequestID) {
        ErrorResult res = DAO.getInstance().assignDriverToUserRequest(driverID, userRequestID);

        String response = "{\"response\" : \"" + res.getMessage() + "\"}";

        return Response.ok()
                .entity(response)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @Path("getAllDriverTruckHistory")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDriverTruckHistory() {
        List<DriverTruckHistoryTableItem> history = DAO.getInstance().getAllDriverTruckHistory();
        GenericEntity<List<DriverTruckHistoryTableItem>> list = new GenericEntity<List<DriverTruckHistoryTableItem>>(history) {
        };
        return Response.ok()
                .entity(list)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @Path("driver/login")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response driverLogin(DriverLoginParameters params) {
        ErrorResult res = DAO.getInstance().driverLogin(params);

        JSONObject response = new JSONObject();
        response.put("statusCode", res.getErrorCode());
        response.put("message", res.getMessage());

        if (res.getErrorCode() == 200) {
            response.put("driverID", res.getData());
            StringBuilder keyBuilder = new StringBuilder();
            keyBuilder.append(params.getEmail()).append(":").append(params.getPassword()).append(":").append(res.getToken());
            response.put("key", Base64.encode(keyBuilder.toString().getBytes()));
        }

        return Response.ok()
                .entity(response.toJSONString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @Path("driver/getTodaysTruck")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTodaysTruckForDriver(@QueryParam("driverID") String driverID) {
        DriverTableItem.Truck truck = DAO.getInstance().getTodaysTruckForDriver(driverID);

        if (truck == null) {
            JSONObject response = new JSONObject();
            response.put("statusCode", 302);
            response.put("message", "no truck assigned");
            return Response.ok()
                    .entity(response.toJSONString())
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                    .allow("OPTIONS")
                    .build();
        }

        return Response.ok()
                .entity(truck)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @Path("driver/startTodaysShift/{driverID}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response startDriversShift(@PathParam("driverID") String driverID) {
        JSONObject response = new JSONObject();

        ErrorResult res = DAO.getInstance().startTodaysDriverShift(driverID);

        response.put("statusCode", res.getErrorCode());
        response.put("message", res.getMessage());

        if (res.getErrorCode() == 200) {
            response.put("driverID", res.getData());
        }

        return Response.ok()
                .entity(response.toJSONString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @Path("driver/endTodaysShift/{driverID}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response endTodaysDriverShift(@PathParam("driverID") String driverID) {
        JSONObject response = new JSONObject();

        ErrorResult res = DAO.getInstance().endTodaysDriverShift(driverID);

        response.put("statusCode", res.getErrorCode());
        response.put("message", res.getMessage());

        if (res.getErrorCode() == 200) {
            response.put("driverID", res.getData());
        }

        return Response.ok()
                .entity(response.toJSONString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @Path("driver/getTodaysEvents")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTodaysEventsForDriver(@QueryParam("driverID") String driverID) {
        List<UserRequestTableItem> events = DAO.getInstance().getTodaysEventsForDriver(driverID);
        GenericEntity<List<UserRequestTableItem>> list = new GenericEntity<List<UserRequestTableItem>>(events) {
        };

        return Response.ok()
                .entity(list)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @Path("driver/startEvent/{driverID}/{userRequestID}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response startEvent(@PathParam("driverID") String driverID, @PathParam("userRequestID") String userRequestID, StartEventParameters params) {

        ErrorResult res = DAO.getInstance().startEvent(driverID, userRequestID, params);

        JSONObject response = new JSONObject();
        response.put("statusCode", res.getErrorCode());
        response.put("message", res.getMessage());

        return Response.ok()
                .entity(response.toJSONString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @Path("driver/noShow/{driverID}/{userRequestID}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response noShow(@PathParam("driverID") String driverID, @PathParam("userRequestID") String userRequestID) {
        ErrorResult res = DAO.getInstance().noShowEvent(driverID, userRequestID);

        JSONObject response = new JSONObject();
        response.put("statusCode", res.getErrorCode());
        response.put("message", res.getMessage());

        return Response.ok()
                .entity(response.toJSONString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "POST")
                .header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept")
                .allow("OPTIONS")
                .build();
    }

    @Path("driver/completeDropOff/{driverID}/{userRequestID}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response completeEvent(@PathParam("driverID") String driverID, @PathParam("userRequestID") String userRequestID, DropOffList dropOffList) {
        ErrorResult res = DAO.getInstance().completeDropOff(driverID, userRequestID, dropOffList);

        JSONObject response = new JSONObject();
        response.put("statusCode", res.getErrorCode());
        response.put("message", res.getMessage());

        return Response.ok()
                .entity(response.toJSONString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @Path("driver/getItemConditionValues")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getItemConditionValues() {
        List<String> conditionValues = DAO.getInstance().getItemConditionValues();
        JSONObject response = new JSONObject();
        response.put("statusCode", 200);
        response.put("message", "success");
        response.put("conditions", conditionValues);

        return Response.ok()
                .entity(response.toJSONString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @Path("driver/completePickup")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response completePickup(PickupList pickupList) {
        ErrorResult res = DAO.getInstance().completePickup(pickupList);
        JSONObject response = new JSONObject();
        response.put("statusCode", res.getErrorCode());
        response.put("message", res.getMessage());

        return Response.ok()
                .entity(response.toJSONString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @Path("driver/checkoutItem")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkOutItem(CheckoutItem item) {

        ErrorResult res = DAO.getInstance().checkOutItem(item);

        String response = "{\"response\" : \"" + res.getMessage() + "\"}";

        return Response.ok()
                .entity(response)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @Path("getTodaysCheckins")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTodaysCheckins() {
        List<TodaysCheckins> todaysCheckins = DAO.getInstance().getTodaysCheckIns();
        GenericEntity<List<TodaysCheckins>> list = new GenericEntity<List<TodaysCheckins>>(todaysCheckins) {
        };
        return Response.ok()
                .entity(list)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @Path("getTrucksCheckins")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTrucksCheckins(@QueryParam("truckID") String truckID) {

        TruckCheckin truckCheckIn = DAO.getInstance().truckCheckIn(truckID);

        return Response.ok()
                .entity(truckCheckIn)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @Path("checkIn")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkInProduct(CheckInParameters params) {

        ErrorResult res = DAO.getInstance().checkInItem(params);

        JSONObject response = new JSONObject();
        response.put("statusCode", res.getErrorCode());
        response.put("message", res.getMessage());

        return Response.ok()
                .entity(response.toJSONString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @Path("startTestCases")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTestCasesResults() throws ClassNotFoundException {
        JSONObject response = new JSONObject();
        final String keyName = "test_" + System.currentTimeMillis();
        final Class testClass = Class.forName("com.bathwater.TestBathwaterRest");

        new Thread(new Runnable() {
            @Override
            public void run() {
                JUnitCore junit = new JUnitCore();
                junit.addListener(new JUnitTestListener(keyName));
                junit.run(testClass);
            }
        }).start();

        response.put("keyName", keyName);
        response.put("statusCode", 200);
        response.put("testCases", testClass.getDeclaredMethods().length);
        response.put("message", "started");

        return Response.ok()
                .entity(response.toJSONString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @Path("getTestResultProgress")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTestResults(@QueryParam("keyName") String keyName) throws org.json.simple.parser.ParseException {
        try {
            InputStream in = JSONDatabaseUtil.downloadFile(keyName);
            Scanner scanner = new Scanner(in);
            StringBuilder responseString = new StringBuilder();

            while (scanner.hasNextLine()) {
                responseString.append(scanner.nextLine());
            }

            return Response.ok()
                    .entity(responseString.toString())
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                    .allow("OPTIONS")
                    .build();
        } catch (AmazonS3Exception ex) {
            JSONObject response = new JSONObject();
            response.put("statusCode", 400);
            response.put("message", "file not found");
            return Response.ok()
                    .entity(response.toJSONString())
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                    .allow("OPTIONS")
                    .build();
        }
    }

    @Path("test/populateUserItems")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response populateTestUserItems(@QueryParam("emailID") String encodedEmail) {
        if (!"1".equals(System.getProperty("PRODUCTION_MODE"))) {
            try {
                String email = URLDecoder.decode(encodedEmail, "UTF-8");
                ErrorResult res = DAO.getInstance().populateUserItems(email);
                JSONObject response = new JSONObject();
                response.put("statusCode", res.getErrorCode());
                response.put("message", res.getMessage());

                return Response.ok()
                        .entity(response.toJSONString())
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                        .allow("OPTIONS")
                        .build();

            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(BathwaterRest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return Response.noContent().build();
    }

    @Path("test/populateUserItem")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response populateUserItem(@QueryParam("emailID") String emailAddress) {
        if (!"1".equals(System.getProperty("PRODUCTION_MODE"))) {
            try {
                String email = URLDecoder.decode(emailAddress, "UTF-8");
                ErrorResult res = DAO.getInstance().populateUserItem(email);

                return Response.ok()
                        .entity(res.getData())
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                        .allow("OPTIONS")
                        .build();

            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(BathwaterRest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return Response.noContent().build();
    }

    /// ADMIN ROLE MANGEMENT APIS //////////
    @GET
    @Path("admin/glogin")
    @Produces(MediaType.TEXT_HTML)
    public Response adminLogin(@QueryParam("email") String token) throws IOException {
        Map<String, Object> map = new HashMap<>();
        // Check if email exists - then login in 

        String resetPageBody = StringUtil.buildGoogleSignINHtmlPage();

        return Response.ok()
                .entity(resetPageBody)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE")
                .build();

    }

    @Path("admin/glogintemp")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response gLoginTemp(OAuthLoginParameters params) {
        ErrorResult res = DAO.getInstance().updateAdminID(params);

        JSONObject response = new JSONObject();
        response.put("statusCode", res.getErrorCode());
        response.put("message", res.getMessage());

        return Response.ok()
                .entity(response.toJSONString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "POST")
                .header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization")
                .allow("OPTIONS")
                .build();
    }

    @GET
    @Path("admin/gloginsuccess")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response glogin(@QueryParam("email") String email, @QueryParam("id") String userid) {

        ErrorResult res = DAO.getInstance().verifyAdmin(email, userid);

        JSONObject response = new JSONObject();
        response.put("statusCode", res.getErrorCode());
        response.put("message", res.getMessage());
        if (res.getErrorCode() == 200 || res.getErrorCode() == 201 || res.getErrorCode() == 203) {
            response.put("role", res.getData());
            StringBuilder keyBuilder = new StringBuilder();
            keyBuilder.append(email).append(":").append(userid).append(":").append(res.getData()).append(":").append(res.getToken());
            String key = Base64.encode(keyBuilder.toString().getBytes());
            key = key.replaceAll("\n", "");
            response.put("key", key);
        }

        return Response.ok()
                .entity(response.toJSONString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE")
                .header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept")
                .allow("OPTIONS")
                .build();
    }

    @Path("admin/login")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    public Response adminlogin(@QueryParam("email") String email) {
        OAuthLoginParameters params = new OAuthLoginParameters();
        params.setEmail(email);
        ErrorResult res = DAO.getInstance().isAdminExists(params.getEmail());

        JSONObject response = new JSONObject();
        response.put("statusCode", res.getErrorCode());
        response.put("message", res.getMessage());

        if (res.getErrorCode() == 200) {
            String resetPageBody = StringUtil.buildGoogleSignINHtmlPage();

            return Response.ok()
                    .entity(resetPageBody)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE")
                    .build();
        }
        return Response.ok()
                .entity(response.toJSONString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE")
                .build();

    }

    @Path("admin/addAdmin")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addAdmin(@Context HttpHeaders httpHeaders, OAuthLoginParameters params) {

        List<String> key = httpHeaders.getRequestHeader("Authorization");
        String credentials = null;
        try {
            byte[] bytes = java.util.Base64.getDecoder().decode(key.get(0));
            credentials = new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(AuthenticationService.class.getName()).log(Level.SEVERE, null, ex);
        }

        final StringTokenizer tokenizer = new StringTokenizer(credentials, ":");
        final String email = tokenizer.nextToken();

        ErrorResult res = DAO.getInstance().addAdmin(params, email);

        JSONObject response = new JSONObject();
        response.put("statusCode", res.getErrorCode());
        response.put("message", res.getMessage());

        return Response.ok()
                .entity(response.toJSONString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "POST")
                .header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization")
                .allow("OPTIONS")
                .build();
    }

    @Path("admin/editAdmin")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editAdmin(@Context HttpHeaders httpHeaders, OAuthLoginParameters params) {
        List<String> key = httpHeaders.getRequestHeader("Authorization");
        String credentials = null;
        try {
            byte[] bytes = java.util.Base64.getDecoder().decode(key.get(0));
            credentials = new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(AuthenticationService.class.getName()).log(Level.SEVERE, null, ex);
        }

        final StringTokenizer tokenizer = new StringTokenizer(credentials, ":");
        final String email = tokenizer.nextToken();

        ErrorResult res = DAO.getInstance().addAdmin(params, email);

        JSONObject response = new JSONObject();
        response.put("statusCode", res.getErrorCode());
        response.put("message", res.getMessage());

        return Response.ok()
                .entity(response.toJSONString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "POST")
                .header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization")
                .allow("OPTIONS")
                .build();
    }

    @Path("admin/deleteAdmin")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteAdmin(@Context HttpHeaders httpHeaders, OAuthLoginParameters params) {
        List<String> key = httpHeaders.getRequestHeader("Authorization");
        String credentials = null;
        try {
            byte[] bytes = java.util.Base64.getDecoder().decode(key.get(0));
            credentials = new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(AuthenticationService.class.getName()).log(Level.SEVERE, null, ex);
        }

        final StringTokenizer tokenizer = new StringTokenizer(credentials, ":");
        final String email = tokenizer.nextToken();

        ErrorResult res = DAO.getInstance().deleteAdmin(params, email);
        JSONObject response = new JSONObject();
        response.put("statusCode", res.getErrorCode());
        response.put("message", res.getMessage());

        return Response.ok()
                .entity(response.toJSONString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "POST")
                .header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization")
                .allow("OPTIONS")
                .build();
    }

    @Path("admin/listAdmins")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response listAdmin(@Context HttpHeaders httpHeaders, OAuthLoginParameters params) {
        List<String> key = httpHeaders.getRequestHeader("Authorization");
        String credentials = null;
        JSONObject response = new JSONObject();
        response.put("statusCode", "404");
        response.put("message", "Error");
        try {
            byte[] bytes = java.util.Base64.getDecoder().decode(key.get(0));
            credentials = new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(AuthenticationService.class.getName()).log(Level.SEVERE, null, ex);
        }

        final StringTokenizer tokenizer = new StringTokenizer(credentials, ":");
        final String email = tokenizer.nextToken();
        List<OAuthTableItem> res = DAO.getInstance().getAdmins(params, email);
        if (res == null) {
            return Response.ok()
                    .entity(response.toJSONString())
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "POST")
                    .header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization")
                    .allow("OPTIONS")
                    .build();
        } else {
            GenericEntity<List<OAuthTableItem>> list = new GenericEntity<List<OAuthTableItem>>(res) {
            };
            return Response.ok()
                    .entity(list)
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "POST")
                    .header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization")
                    .allow("OPTIONS")
                    .build();
        }
    }

    @Path("getUserInventory")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserInventory(@QueryParam("userid") String userID) {
        UserTableItem user = DAO.getInstance().getUserInventory(userID);

        if (user == null) {
            JSONObject response = new JSONObject();
            response.put("statusCode", 101);
            response.put("message", "invalid userid");

            return Response.ok()
                    .entity(response.toJSONString())
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE")
                    .allow("OPTIONS")
                    .build();
        }

        user.setPassword(null);
        user.setToken(null);
        user.setWishlist(null);
        user.setNotifications(null);

        return Response.ok()
                .entity(user)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE")
                .allow("OPTIONS")
                .build();
    }

    @Path("getUserRequests")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserRequests(@QueryParam("userid") String userID) {
        List<UserRequestTableItem> userRequests = DAO.getInstance().getUserRequests(userID);
        GenericEntity<List<UserRequestTableItem>> list = new GenericEntity<List<UserRequestTableItem>>(userRequests) {
        };
        if (userRequests == null) {
            JSONObject response = new JSONObject();
            response.put("statusCode", 101);
            response.put("message", "invalid userid");

            return Response.ok()
                    .entity(response.toJSONString())
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE")
                    .allow("OPTIONS")
                    .build();
        }

        return Response.ok()
                .entity(list)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE")
                .allow("OPTIONS")
                .build();
    }

    @Path("admin/getBWItems")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getItems() {
        List<InventoryTableItem> items = DAO.getInstance().getBathwaterItems();
        GenericEntity<List<InventoryTableItem>> list = new GenericEntity<List<InventoryTableItem>>(items) {
        };

        return Response.ok()
                .entity(list)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @Path("admin/updateCredits")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCredits(@QueryParam("storedID") String storedID, @QueryParam("credits") int credits) {
        ErrorResult res = DAO.getInstance().updateCredits(storedID, credits);
        JSONObject response = new JSONObject();
        response.put("statusCode", res.getErrorCode());
        response.put("message", res.getMessage());
        return Response.ok()
                .entity(response.toJSONString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }
    
    // USED FOR UPDATING LOCATION AND CREDIT
    @Path("admin/checkInStoredItem")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateStoredItem(@QueryParam("storedID") String storedID, 
            @QueryParam("credits") int credits,
            @QueryParam("status") String status,
            @QueryParam("categoryID") String category,
            @QueryParam("location") String location) {
        ErrorResult res = DAO.getInstance().updateStoredItem(storedID, credits,status,category,location);
        JSONObject response = new JSONObject();
        response.put("statusCode", res.getErrorCode());
        response.put("message", res.getMessage());
        return Response.ok()
                .entity(response.toJSONString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    // WAREHOUSE - INCOMIN AND OUTGOING APIS
    @Path("admin/getItemsByStatus")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStoredItemsByStatus(@QueryParam("status") String status) {
        try {
            ErrorResult res = DAO.getInstance().getItemsByStatus(status);

            if (res.getErrorCode() == 200) {
                return Response.ok()
                        .entity(res.getData())
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                        .allow("OPTIONS")
                        .build();
            }

            JSONObject response = new JSONObject();
            response.put("statusCode", res.getErrorCode());
            response.put("message", res.getMessage());

            return Response.ok()
                    .entity(response.toJSONString())
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                    .allow("OPTIONS")
                    .build();
        } catch (JsonProcessingException ex) {
            JSONObject response = new JSONObject();
            response.put("statusCode", 500);
            response.put("message", ex.getMessage());

            return Response.ok()
                    .entity(response.toJSONString())
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                    .allow("OPTIONS")
                    .build();
        }
    }

    @Path("admin/updateItemInWarehouse")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateItemStatus(@QueryParam("storedItemID") String storedID, 
            @QueryParam("status") String status,
            @QueryParam("location") String storageId) {
        ErrorResult res = DAO.getInstance().updateStatus(storedID, status,storageId);
        JSONObject response = new JSONObject();
        response.put("statusCode", res.getErrorCode());
        response.put("message", res.getMessage());
        return Response.ok()
                .entity(response.toJSONString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }
    
    @Path("admin/updateDropItemStatus")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateItemStatus(@QueryParam("storedItemID") String storedID, 
            @QueryParam("status") String status,
            @QueryParam("subitemCode") String itemCode,
            @QueryParam("location") String storageId) {
        ErrorResult res = DAO.getInstance().updateDropItemStatus(storedID, status,itemCode,storageId);
        JSONObject response = new JSONObject();
        response.put("statusCode", res.getErrorCode());
        response.put("message", res.getMessage());
        return Response.ok()
                .entity(response.toJSONString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                .allow("OPTIONS")
                .build();
    }

    @Path("admin/incomingItems")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTrucksByUserrequests(@QueryParam("date") String date) {
        try {
        ErrorResult res = DAO.getInstance().getTrucksByUserrequests(date);
         if (res.getErrorCode() == 200) {
                return Response.ok()
                        .entity(res.getData())
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                        .allow("OPTIONS")
                        .build();
            }

            JSONObject response = new JSONObject();
            response.put("statusCode", res.getErrorCode());
            response.put("message", res.getMessage());

            return Response.ok()
                    .entity(response.toJSONString())
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                    .allow("OPTIONS")
                    .build();
        } catch (JsonProcessingException ex) {
            JSONObject response = new JSONObject();
            response.put("statusCode", 500);
            response.put("message", ex.getMessage());

            return Response.ok()
                    .entity(response.toJSONString())
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                    .allow("OPTIONS")
                    .build();
        }
    }

    @Path("admin/outgoingItems")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTrucksByUserrequestsDropoff(@QueryParam("date") String date) {
        try {
        ErrorResult res = DAO.getInstance().getTrucksByUserrequestsDropoff(date);
         if (res.getErrorCode() == 200) {
                return Response.ok()
                        .entity(res.getData())
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                        .allow("OPTIONS")
                        .build();
            }

            JSONObject response = new JSONObject();
            response.put("statusCode", res.getErrorCode());
            response.put("message", res.getMessage());

            return Response.ok()
                    .entity(response.toJSONString())
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                    .allow("OPTIONS")
                    .build();
        } catch (JsonProcessingException ex) {
            JSONObject response = new JSONObject();
            response.put("statusCode", 500);
            response.put("message", ex.getMessage());

            return Response.ok()
                    .entity(response.toJSONString())
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT")
                    .allow("OPTIONS")
                    .build();
        }
    }    
    
}
