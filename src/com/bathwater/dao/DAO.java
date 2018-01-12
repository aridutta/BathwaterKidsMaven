/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bathwater.dao;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bathwater.amazons3.ImageDatabaseUtil;
import com.bathwater.amazons3.ProfilePictureDatabaseUtil;
import com.bathwater.amazons3.PromoCodeDatabaseUtil;
import com.bathwater.amazons3.URLTokenHandler;
import com.bathwater.dto.AddNewCard;
import com.bathwater.dto.BorrowItemParameters;
import com.bathwater.dto.CheckInParameters;
import com.bathwater.dto.CheckoutItem;
import com.bathwater.dto.CheckoutList;
import com.bathwater.dto.ChildrenUpdateParamaeters;
import com.bathwater.dto.DeleteCardParameters;
import com.bathwater.dto.DisplayPicture;
import com.bathwater.dto.DriverLoginParameters;
import com.bathwater.dto.DropOffList;
import com.bathwater.dto.DropOffRequestParameters;
import com.bathwater.dto.ErrorResult;
import com.bathwater.dto.LockItemParameters;
import com.bathwater.dto.LoginParameters;
import com.bathwater.dto.OAuthLoginParameters;
import com.bathwater.dto.PickupList;
import com.bathwater.dto.PickupRequestParameters;
import com.bathwater.dto.ProductImages;
import com.bathwater.dto.ResetPasswordParameters;
import com.bathwater.dto.SetCreditsParameters;
import com.bathwater.dto.ShareItemParameters;
import com.bathwater.dto.ShippingChargeParmaeters;
import com.bathwater.dto.StartEventParameters;
import com.bathwater.dto.StoreItemParameters;
import com.bathwater.dto.TimeslotDTO;
import com.bathwater.dto.TodaysCheckins;
import com.bathwater.dto.TruckCheckin;
import com.bathwater.dto.TruckItems;
import com.bathwater.dto.UserRegistrationParameters;
import com.bathwater.dto.WeeksTimeslots;
import com.bathwater.dynamodb.helper.IDynamoDBHelper;
import com.bathwater.dynamodb.helper.impl.DynamoDBHelper;
import com.bathwater.dynamodb.helper.queries.DynamoDBQueries;
import com.bathwater.dynamodb.helper.queries.DynamoDBScans;
import com.bathwater.dynamodb.tables.BathwaterItem;
import com.bathwater.dynamodb.tables.CategoryTableItem;
import com.bathwater.dynamodb.tables.DriverTableItem;
import com.bathwater.dynamodb.tables.DriverTableItem.Truck;
import com.bathwater.dynamodb.tables.DriverTruckHistoryTableItem;
import com.bathwater.dynamodb.tables.InventoryTableItem;
import com.bathwater.dynamodb.tables.InventoryTableItem.SubItem;
import com.bathwater.dynamodb.tables.ItemTableItem;
import com.bathwater.dynamodb.tables.MembershipTableItem;
import com.bathwater.dynamodb.tables.Notification;
import com.bathwater.dynamodb.tables.OAuthTableItem;
import com.bathwater.dynamodb.tables.ReferralCodeMapper;
import com.bathwater.dynamodb.tables.RequestZipCodeTableItem;
import com.bathwater.dynamodb.tables.ServiceZipCodeTableItem;
import com.bathwater.dynamodb.tables.StorageTableItem;
import com.bathwater.dynamodb.tables.TimeslotTableItem;
import com.bathwater.dynamodb.tables.TruckTableItem;
import com.bathwater.dynamodb.tables.UserRequestTableItem;
import com.bathwater.dynamodb.tables.UserRequestTableItem.Bins;
import com.bathwater.dynamodb.tables.UserRequestTableItem.Item;
import com.bathwater.dynamodb.tables.UserTableItem;
import com.bathwater.dynamodb.tables.UserTableItem.Requests;
import com.bathwater.elasticsearch.helper.queries.ESItemQueries;
import com.bathwater.services.MandrillEmailService;
import com.bathwater.services.StripeService;
import com.bathwater.services.TwilioSMSService;
import com.bathwater.util.AdvancedEncryptionStandard;
import com.bathwater.util.CryptoUtil;
import com.bathwater.util.DateUtil;
import com.bathwater.util.NotificationFactory;
import com.bathwater.util.SMSBodyFactory;
import com.bathwater.util.StringUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
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
public class DAO {

    private static final DAO INSTANCE = new DAO();
    private final IDynamoDBHelper helper;
    private final DynamoDBScans scanHelper;
    private final DynamoDBQueries queryHelper;
    private final ESItemQueries esQueryHelper;
    private final Random random;

    private DAO() {
        helper = DynamoDBHelper.getInstance();
        scanHelper = new DynamoDBScans();
        queryHelper = new DynamoDBQueries();
        esQueryHelper = new ESItemQueries();
        random = new Random();
    }

    public static synchronized DAO getInstance() {
        return INSTANCE;
    }

    public ErrorResult oAuthLogin(OAuthLoginParameters params) {
        ErrorResult res = new ErrorResult();
        res.setErrorCode(200);
        res.setMessage("success");

        try {
            OAuthTableItem user = new OAuthTableItem();
            user.setEmail(params.getEmail());
            user.setName(params.getName());

            String role = "0";
            String id = "0";
            List<OAuthTableItem> admins = scanHelper.isAdminExists(params.getEmail());
            if (admins != null && admins.size() > 0) {
                role = admins.get(0).getRole();
                id = admins.get(0).getId();
            }
            user.setRole(role);
            user.setId(params.getId());

            helper.putItem(user);
        } catch (Exception ex) {
            res.setErrorCode(500);
            res.setMessage(ex.getMessage());
        }

        return res;
    }

    public ErrorResult updateAdminID(OAuthLoginParameters params) {
        ErrorResult res = new ErrorResult();
        res.setErrorCode(200);
        res.setMessage("success");

        try {
            String role = "0";
            List<OAuthTableItem> admins = scanHelper.isAdminExists(params.getEmail());
            if (admins != null && admins.size() > 0) {
                role = admins.get(0).getRole();
            }
            OAuthTableItem user = new OAuthTableItem();
            user.setEmail(params.getEmail());
            user.setName(params.getName());
            user.setId(params.getId());
            user.setRole(role);
            //user.setRole("10");

            String token = StringUtil.generateRandomString(5);
            user.setToken(token);

            helper.putItem(user);
        } catch (Exception ex) {
            res.setErrorCode(500);
            res.setMessage(ex.getMessage());
        }

        return res;
    }

    public ErrorResult isAdminExists(String email) {
        ErrorResult res = new ErrorResult();
        res.setErrorCode(404);
        res.setMessage("UnAuthorised Email Address");
        try {

            List<OAuthTableItem> admins = scanHelper.isAdminExists(email);
            if (admins != null && admins.size() > 0) {
                res.setErrorCode(200);
                res.setMessage("Success");
            }

        } catch (Exception e) {
            res.setErrorCode(500);
            res.setMessage(e.getMessage());
        }
        return res;
    }

    public ErrorResult verifyAdmin(String email, String id) {
        ErrorResult res = new ErrorResult();
        res.setErrorCode(404);
        res.setMessage("UnAuthorised Email Address");
        try {

            List<OAuthTableItem> admins = scanHelper.isAdminExists(email);
            if (admins != null && admins.size() > 0) {
                OAuthTableItem user = admins.get(0);
                //if (email.equalsIgnoreCase(user.getEmail()) && id.equalsIgnoreCase(user.getId())) {
                if (email.equalsIgnoreCase(user.getEmail())) {
                    res.setErrorCode(200);
                    res.setMessage("Success");
                    res.setData(user.getRole());
                    res.setToken(user.getToken());

                    // add again data
                    user.setEmail(user.getEmail());
                    user.setName(user.getName());
                    user.setId(id);
                    user.setRole(user.getRole());
                    if (user.getToken() == null) {
                        String token = StringUtil.generateRandomString(5);
                        user.setToken(token);
                    } else {
                        user.setToken(user.getToken());
                    }
                    //user.setToken(user.getToken());

                    res.setToken(user.getToken());

                    helper.putItem(user);

                } else {
                    res.setErrorCode(404);
                    res.setMessage("UnAuthorised Email Address");
                }
            }

        } catch (Exception e) {
            res.setErrorCode(500);
            res.setMessage(e.getMessage());
        }
        return res;
    }

    public ErrorResult verifyAdmin(OAuthLoginParameters params) {
        ErrorResult res = new ErrorResult();
        res.setErrorCode(404);
        res.setMessage("UnAuthorised Email Address");
        try {

            List<OAuthTableItem> admins = scanHelper.isAdminExists(params.getEmail());
            if (admins != null && admins.size() > 0) {
                OAuthTableItem user = admins.get(0);
                if (params.getEmail().equalsIgnoreCase(user.getEmail())
                        && params.getId().equalsIgnoreCase(user.getId())
                        && params.getToken().equalsIgnoreCase(user.getToken())) {
                    res.setErrorCode(200);
                    res.setMessage("Success");
                    res.setData(user.getRole());
                    res.setToken(user.getToken());
                } else {
                    res.setErrorCode(404);
                    res.setMessage("UnAuthorised Email Address");
                }
            }

        } catch (Exception e) {
            res.setErrorCode(500);
            res.setMessage(e.getMessage());
        }
        return res;
    }

    private boolean checkRole(String role, String chkRole) {
        int r1 = Integer.parseInt(role);
        if (chkRole == null) {
            chkRole = "0";
        }
        int r2 = Integer.parseInt(chkRole);

        // Super Admin can add anyone including Auper Admin
        if (r1 == 10 && (r1 >= r2)) {
            return true;
        }

        // Admin can add admins but not super admin 
        if (r1 == 4 && (r1 >= r2)) {
            return true;
        } else {
            return false;
        }
    }

    public ErrorResult addAdmin(OAuthLoginParameters admin, String email) {
        ErrorResult res = new ErrorResult();
        res.setErrorCode(404);
        res.setMessage("UnAuthorised Email Address");
        try {

            List<OAuthTableItem> admins = scanHelper.isAdminExists(email);
            if (admins != null && admins.size() > 0) {
                OAuthTableItem user = admins.get(0);
                String role = user.getRole();
                Boolean isAccess = this.checkRole(role, admin.getRole());

                if (isAccess) {
                    OAuthTableItem newuser = new OAuthTableItem();
                    newuser.setEmail(admin.getEmail());
                    newuser.setName(admin.getName());
                    newuser.setRole(admin.getRole());

                    helper.putItem(newuser);

                    res.setErrorCode(200);
                    res.setMessage("Success");
                    res.setData(user.getRole() + "");
                } else {
                    res.setErrorCode(404);
                    res.setMessage("UnAuthorised Email Address");
                }
            }

        } catch (Exception e) {
            res.setErrorCode(500);
            res.setMessage(e.getMessage());
        }
        return res;
    }

    public List<OAuthTableItem> getAdmins(OAuthLoginParameters admin, String email) {
        ErrorResult res = new ErrorResult();
        res.setErrorCode(404);
        res.setMessage("UnAuthorised Email Address");
        try {

            List<OAuthTableItem> admins = scanHelper.isAdminExists(email);
            if (admins != null && admins.size() > 0) {
                OAuthTableItem user = admins.get(0);
                String role = user.getRole();
                Boolean isAccess = this.checkRole(role, admin.getRole());

                if (isAccess) {
                    if (role.equalsIgnoreCase("10")) {
                        List<OAuthTableItem> newuser = scanHelper.getAdmins();
                        return newuser;
                    } else if (role.equalsIgnoreCase("4")) {
                        List<OAuthTableItem> newuser = scanHelper.getAdmins(role);
                        return newuser;
                    }

                } else {
                    res.setErrorCode(404);
                    res.setMessage("UnAuthorised Email Address");
                }
            }

        } catch (Exception e) {
            res.setErrorCode(500);
            res.setMessage(e.getMessage());
        }
        return null;
    }

    public ErrorResult deleteAdmin(OAuthLoginParameters admin, String email) {
        ErrorResult res = new ErrorResult();
        res.setErrorCode(404);
        res.setMessage("UnAuthorised Email Address");
        try {

            List<OAuthTableItem> admins = scanHelper.getAdmins();
            if (admins != null && admins.size() > 0) {
                OAuthTableItem user = admins.get(0);
                String role = user.getRole();
                Boolean isAccess = this.checkRole(role, admin.getRole());

                if (isAccess) {
                    OAuthTableItem newuser = new OAuthTableItem();
                    newuser.setEmail(admin.getEmail());
                    newuser.setName(admin.getName());

                    helper.deleteItem(newuser);

                    res.setErrorCode(200);
                    res.setMessage("Success");
                    res.setData(user.getRole() + "");
                } else {
                    res.setErrorCode(404);
                    res.setMessage("UnAuthorised Email Address");
                }
            }

        } catch (Exception e) {
            res.setErrorCode(500);
            res.setMessage(e.getMessage());
        }
        return res;
    }

    private String generateReferralCode(String firstName) {

        int length = 3;

        firstName = firstName.replaceAll(" ", "");

        if (firstName.length() < 5) {
            length = 8 - firstName.length();
        }

        if (firstName.length() > 5) {
            firstName = firstName.substring(0, 5);
        }

        String referralCode = firstName.toLowerCase();
        String randomString;

        do {
            randomString = StringUtil.generateRandomNumbers(length);

            List<UserTableItem> existing = scanHelper.getUsersBasedOnReferralCode(referralCode + randomString);

            if (existing == null || existing.isEmpty()) {
                referralCode += randomString;
                break;
            }

        } while (true);

        return referralCode;
    }

    public ErrorResult register(UserRegistrationParameters user) {
        ErrorResult res = new ErrorResult();
        res.setErrorCode(500);
        res.setMessage("fail");

        List<UserTableItem> userList = scanHelper.getUsersBasedOnEmail(user.getEmailAddress());

        if (userList != null && !userList.isEmpty()) {
            res.setErrorCode(202);
            res.setMessage("user exists");

            if (StringUtil.isBlank(userList.get(0).getPhoneNumber())) {
                res.setErrorCode(203);
                res.setMessage("user exists with phone not verified");
                res.setData(userList.get(0).getUserID());
            } else if (userList.get(0).getMembership() == null) {
                res.setErrorCode(201);
                res.setMessage("user exists without membership");
                res.setData(userList.get(0).getUserID());
            }
            return res;
        }

        if (user.getFirstName() == null || user.getEmailAddress() == null) {
            res.setErrorCode(100);
            res.setMessage("missing parameters");
            return res;
        }

        if (StringUtil.isBlank(user.getPassword()) && !user.getUserType().equals("fbUser")) {
            res.setErrorCode(100);
            res.setMessage("missing parameters");
            return res;
        }

        try {
            UserTableItem userItem = new UserTableItem();
            userItem.setFirstName(user.getFirstName());
            userItem.setLastName(user.getLastName());
            userItem.setEmailAddress(user.getEmailAddress());
            String encryptedPassword;
            if (StringUtil.isBlank(user.getUserType()) || user.getUserType().equals("normal")) {
                encryptedPassword = new String(Base64.decode(user.getPassword()), "UTF-8");
            } else {
                encryptedPassword = "FB_USER";
            }
            String password = CryptoUtil.calculateHMACSignature(encryptedPassword);
            userItem.setPassword(password);
            userItem.setReferralCode(generateReferralCode(user.getFirstName()));
            userItem.setActive(1);
            if (!StringUtil.isBlank(user.getUserType())) {
                userItem.setUserType(user.getUserType());
            }
            userItem.setCreationTime(new Date().toString());
            if (user.getImageBase64() != null) {
                try {
                    byte[] uploadImage = Base64.decode(user.getImageBase64());
                    InputStream inputStream = new ByteArrayInputStream(uploadImage);
                    String fileName = System.currentTimeMillis() + ".jpg";
                    ProfilePictureDatabaseUtil.uploadImage(fileName, inputStream);
                    String imageURL = "https://s3.amazonaws.com/" + ProfilePictureDatabaseUtil.getBUCKET_NAME() + "/" + fileName;
                    userItem.setImageURL(imageURL);
                } catch (IOException | Base64DecodingException ex) {
                    res.setErrorCode(130);
                    res.setMessage("error uploading image to S3");
                    return res;
                }
            }

            List<Notification> notifications = new ArrayList<>();

            NotificationFactory.addWelcomeNotification(notifications);

            userItem.setNotifications(notifications);

            String token = StringUtil.generateRandomString(5);
            userItem.setToken(token);
            helper.putItem(userItem);

            Map<String, String> templateContent = new HashMap<>();
            templateContent.put("fname", user.getFirstName());

            MandrillEmailService.sendMail("Welcome to Bathwater", templateContent, user.getEmailAddress(), "Welcome to Bathwater");

            res.setErrorCode(200);
            res.setMessage("success");
            res.setData(userItem.getUserID());
            res.setToken(token);
        } catch (Base64DecodingException | UnsupportedEncodingException | RuntimeException | NoSuchAlgorithmException | InvalidKeyException ex) {
            res.setMessage(ex.getMessage().substring(ex.getMessage().indexOf(":") + 1));
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }

        return res;
    }

    public ErrorResult updateDisplayPicture(DisplayPicture dp) {
        ErrorResult res = new ErrorResult();

        String userID = dp.getUserID();

        UserTableItem user = getUserProfile(userID);

        if (user == null) {
            res.setErrorCode(101);
            res.setMessage("invalid userid");
            return res;
        }

        try {
            byte[] uploadImage = Base64.decode(dp.getDisplayPicture());
            InputStream inputStream = new ByteArrayInputStream(uploadImage);
            String fileName = System.currentTimeMillis() + ".jpg";
            ProfilePictureDatabaseUtil.uploadImage(fileName, inputStream);
            String imageURL = "https://s3.amazonaws.com/" + ProfilePictureDatabaseUtil.getBUCKET_NAME() + "/" + fileName;
            user.setImageURL(imageURL);
            helper.putItem(user);
        } catch (IOException | Base64DecodingException ex) {
            res.setErrorCode(130);
            res.setMessage("error uploading image to S3");
            return res;
        }

        res.setErrorCode(200);
        res.setMessage("success");
        return res;
    }

    public ErrorResult updateProfile(String uid, String firstName, String lastName, String email, String phoneNum, String streetAddress, String apartment, String city, String state, String zipCode, String imageBase64) {
        ErrorResult res = new ErrorResult();
        res.setErrorCode(200);

        if (uid == null) {
            res.setErrorCode(100);
            res.setMessage("missing parameters");
            return res;
        }

        UserTableItem user = getUserProfile(uid);

        if (user == null) {
            res.setErrorCode(101);
            res.setMessage("invalid userid");
            return res;
        }

        try {
            if (firstName != null) {
                user.setFirstName(firstName);
            }
            if (lastName != null) {
                user.setLastName(lastName);
            }

            if (email != null) {
                user.setEmailAddress(email);
            }

            if (phoneNum != null) {
                user.setPhoneNumber(phoneNum);
            }

            List<UserTableItem.Address> addresses = user.getAddress();
            UserTableItem.Address address = new UserTableItem.Address();

            if (addresses == null) {
                addresses = new ArrayList<>();
            }

            if (streetAddress != null) {
                address.setStreetAddress(streetAddress);
            }

            if (apartment != null) {
                address.setApartment(apartment);
            }

            if (city != null) {
                address.setCity(city);
            }

            if (state != null) {
                address.setState(state);
            }

            if (zipCode != null) {
                address.setZipCode(zipCode);
            }

            if (!StringUtil.isBlank(streetAddress) || !StringUtil.isBlank(apartment) || !StringUtil.isBlank(city)
                    || !StringUtil.isBlank(state) || !StringUtil.isBlank(zipCode)) {
                addresses.add(address);
                user.setAddress(addresses);
            }

            if (imageBase64 != null) {
                try {
                    byte[] uploadImage = Base64.decode(imageBase64);
                    InputStream inputStream = new ByteArrayInputStream(uploadImage);
                    String fileName = System.currentTimeMillis() + ".jpg";
                    ProfilePictureDatabaseUtil.uploadImage(fileName, inputStream);
                    String imageURL = "https://s3.amazonaws.com/" + ProfilePictureDatabaseUtil.getBUCKET_NAME() + "/" + fileName;
                    user.setImageURL(imageURL);
                } catch (IOException | Base64DecodingException ex) {
                    res.setErrorCode(130);
                    res.setMessage("error uploading image to S3");
                    return res;
                }
            }

            helper.putItem(user);
            res.setMessage("success");
            res.setData(user.getUserID());
        } catch (Exception ex) {
            res.setErrorCode(500);
            res.setMessage(ex.getMessage().substring(ex.getMessage().indexOf(":") + 1));
            return res;
        }

        return res;
    }

    public ErrorResult updateChildren(ChildrenUpdateParamaeters children) {

        ErrorResult res = new ErrorResult();
        res.setErrorCode(200);
        res.setMessage("success");

        if (children.getUserID() == null || children.getChildren() == null) {
            res.setErrorCode(100);
            res.setMessage("missing parameters");
            return res;
        }

        UserTableItem user = getUserProfile(children.getUserID());

        if (user == null) {
            res.setErrorCode(101);
            res.setMessage("invalid userid");
            return res;
        }

        List<UserTableItem.Children> userChildren = new ArrayList<>();

        for (ChildrenUpdateParamaeters.Children child : children.getChildren()) {
            UserTableItem.Children userChild = new UserTableItem.Children();

            if (child.getName() == null || child.getAge() == null || child.getGender() == null) {
                res.setErrorCode(100);
                res.setMessage("missing parameters");
                return res;
            }

            userChild.setName(child.getName());
            userChild.setAge(child.getAge());
            userChild.setGender(child.getGender());

            try {
                userChild.setChildID(StringUtil.generateMD5Hash(child.getName()));
            } catch (NoSuchAlgorithmException ex) {
                userChild.setChildID(userChildren.size() + 1 + "");
            }
            userChildren.add(userChild);
        }

        user.setChildren(userChildren);
        helper.putItem(user);
        res.setData(user.getUserID());
        return res;
    }

    public ErrorResult updateContactDetails(String userID, String streetAddress, String apartment, String city, String state, String zipCode, String phone, String specialInstructions, String addressID) {
        ErrorResult res = new ErrorResult();
        res.setErrorCode(400);

        if (userID == null || streetAddress == null || apartment == null || city == null || state == null || zipCode == null || phone == null) {
            res.setErrorCode(100);
            res.setMessage("missing parameters");
            return res;
        }

        UserTableItem user = getUserProfile(userID);

        if (user == null) {
            res.setErrorCode(101);
            res.setMessage("invalid userid");
            return res;
        }

        List<UserTableItem.Address> addresses = user.getAddress();

        if (addresses == null) {
            res.setErrorCode(106);
            res.setMessage("invalid addressID");
            return res;
        }

        List<ServiceZipCodeTableItem> zipCodes = queryHelper.getServiceZipCode(zipCode);
        if (zipCodes == null || zipCodes.isEmpty()) {
            res.setErrorCode(404);
            res.setMessage("Service not available");
            return res;
        }

        UserTableItem.Address address = null;

        for (UserTableItem.Address userAddress : addresses) {
            if (userAddress.getAddressID().equals(addressID)) {
                address = userAddress;
                break;
            }
        }

        if (address != null) {
            address.setStreetAddress(streetAddress);
            address.setApartment(apartment);
            address.setCity(city);
            address.setState(state);
            address.setZipCode(zipCode);
            address.setSpecialInstructions(specialInstructions);
        } else {
            res.setErrorCode(106);
            res.setMessage("invalid addressID");
            return res;
        }

        //remove previous address
        //while(addresses.size())
        List<UserTableItem.Address> updatedaddresses = new ArrayList<UserTableItem.Address>();
        for (int i = 0; i < addresses.size(); i++) {
            UserTableItem.Address userAddress = addresses.get(i);
            if (!userAddress.getAddressID().equals(addressID)) {
                updatedaddresses.add(userAddress);
            }
        }

        updatedaddresses.add(address);
        user.setAddress(updatedaddresses);
        helper.putItem(user);

        res.setErrorCode(200);
        res.setMessage("success");
        res.setData(user.getUserID());
        return res;
    }

    public ErrorResult deleteAddress(String userID, String addressID) {
        ErrorResult res = new ErrorResult();

        UserTableItem user = getUserProfile(userID);

        if (user == null) {
            res.setErrorCode(101);
            res.setMessage("invalid userid");
            return res;
        }

        List<UserTableItem.Address> addresses = user.getAddress();

        Iterator<UserTableItem.Address> iterator = addresses.iterator();

        while (iterator.hasNext()) {
            UserTableItem.Address address = iterator.next();
            if (addressID.equals(address.getAddressID())) {
                iterator.remove();
                user.setAddress(addresses);
                helper.putItem(user);

                res.setErrorCode(200);
                res.setMessage("success");
                return res;
            }
        }

        res.setErrorCode(104);
        res.setMessage("invalid addressid");
        return res;
    }

    public ErrorResult addContactDetails(String userID, String streetAddress, String apartment, String city, String state, String zipCode, String phone, String specialInstructions) {
        ErrorResult res = new ErrorResult();
        res.setErrorCode(400);

        if (userID == null || streetAddress == null || apartment == null || city == null || state == null || zipCode == null) {
            res.setErrorCode(100);
            res.setMessage("missing parameters");
            return res;
        }

        UserTableItem user = getUserProfile(userID);

        if (user == null) {
            res.setErrorCode(101);
            res.setMessage("invalid userid");
            return res;
        }

        List<ServiceZipCodeTableItem> zipCodes = queryHelper.getServiceZipCode(zipCode);
        if (zipCodes == null || zipCodes.isEmpty()) {
            res.setErrorCode(404);
            res.setMessage("Service not available");
            return res;
        }

        UserTableItem.Address address = new UserTableItem.Address();
        address.setAddressID(System.currentTimeMillis() + "");
        address.setStreetAddress(streetAddress);
        address.setApartment(apartment);
        address.setCity(city);
        address.setState(state);
        address.setZipCode(zipCode);
        address.setSpecialInstructions(specialInstructions);

        List<UserTableItem.Address> addresses = user.getAddress();

        if (addresses == null) {
            addresses = new ArrayList<>();
        }

        addresses.add(address);
        user.setAddress(addresses);

        if (!StringUtil.isBlank(phone)) {
            user.setPhoneNumber(phone);
        }
        helper.putItem(user);

        res.setErrorCode(200);
        res.setMessage("success");
        res.setData(user.getUserID());
        res.setMiscellaneous(address.getAddressID());
        return res;
    }

    public boolean isReferralCodeValid(String referralCode) {
        if (StringUtil.isBlank(referralCode)) {
            return false;
        }

        List<UserTableItem> users = scanHelper.getUsersBasedOnReferralCode(referralCode);

        return !(users == null || users.isEmpty());
    }

    public ReferralCodeMapper getReferralCodeMapperById(String referralCode) {
        return queryHelper.getReferralCodeMapperByID(referralCode);
    }

    public ErrorResult login(LoginParameters user) {

        ErrorResult res = new ErrorResult();
        res.setErrorCode(200);
        res.setMessage("success");

        try {
            List<UserTableItem> userList = scanHelper.getUsersBasedOnEmail(user.getEmailAddress());
            if (userList != null && !userList.isEmpty()) {
                String password = "FB_USER";
                if (user.getUserType() == null || !user.getUserType().equals("fbUser")) {
                    password = new String(Base64.decode(user.getPassword()), "UTF-8");
                }
                password = CryptoUtil.calculateHMACSignature(password);
                if ((userList.get(0).getPassword().equals(password) && userList.get(0).getActive() == 1)
                        || (user.getUserType() != null && user.getUserType().equals("fbUser"))) {
                    res.setData(userList.get(0).getUserID());

                    if (StringUtil.isBlank(userList.get(0).getPhoneNumber())) {
                        res.setErrorCode(203);
                        res.setMessage("user exists with phone not verified");
                    } else if (userList.get(0).getMembership() == null) {
                        res.setErrorCode(201);
                        res.setMessage("success without membership");
                    }

                    if (user.getToken() == null) {
                        UserTableItem userItem = userList.get(0);
                        String token = StringUtil.generateRandomString(5);
                        userItem.setToken(token);
                        helper.putItem(userItem);
                        res.setToken(token);
                    }

                    if (user.getToken() != null && !userList.get(0).getToken().equals(user.getToken())) {
                        res.setErrorCode(205);
                        res.setMessage("invalid token");
                    }

                } else {
                    res.setErrorCode(103);
                    res.setMessage("email and password did not match");
                    return res;
                }
            } else {
                res.setErrorCode(102);
                res.setMessage("email not registered");
                return res;
            }
        } catch (Base64DecodingException | UnsupportedEncodingException | RuntimeException | NoSuchAlgorithmException | InvalidKeyException ex) {
            res.setErrorCode(500);
            res.setMessage(ex.getMessage());
            return res;
        }
        return res;
    }

    public ErrorResult resetPassword(ResetPasswordParameters params) {
        ErrorResult res = new ErrorResult();

        List<UserTableItem> users = scanHelper.getUsersBasedOnEmail(params.getEmailAddress());

        if (users == null || users.isEmpty()) {
            res.setErrorCode(102);
            res.setMessage("email not registered");
            return res;
        }

        UserTableItem user = users.get(0);
        String randomPassword = StringUtil.generateRandomPassword();
        user.setPassword(randomPassword);

        return res;
    }

    public ErrorResult updatePassword(String emailAddress, String newPassword, String token, boolean justUpdate) {
        ErrorResult res = new ErrorResult();

        if (emailAddress == null || newPassword == null) {
            res.setErrorCode(100);
            res.setMessage("missing parameters");
            return res;
        }

        List<UserTableItem> users = scanHelper.getUsersBasedOnEmail(emailAddress);

        if (users == null || users.isEmpty()) {
            res.setErrorCode(101);
            res.setMessage("invalid email id");
            return res;
        }

        if (!justUpdate && URLTokenHandler.isValidFile(token)) {
            InputStream in = URLTokenHandler.downloadTokenFile(token);

            String email = "";
            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            try {
                while ((line = reader.readLine()) != null) {
                    email += line;
                }
            } catch (IOException ex) {
                res.setErrorCode(500);
                res.setMessage("error parsing token");
                return res;
            }

            if (!email.contains(emailAddress)) {
                res.setErrorCode(101);
                res.setMessage("invalid token");
                return res;
            }

            URLTokenHandler.deleteTokenFile(token);
        }

        UserTableItem user = users.get(0);

        String encodedPassword = newPassword;
        try {
            String decodedPassword = new String(Base64.decode(encodedPassword));
            newPassword = decodedPassword;
        } catch (Base64DecodingException ex) {
            newPassword = encodedPassword;
        }

        try {
            newPassword = CryptoUtil.calculateHMACSignature(newPassword);
        } catch (NoSuchAlgorithmException | InvalidKeyException ex) {
            res.setErrorCode(500);
            res.setMessage("encryption error");
            return res;
        }

        user.setPassword(newPassword);
        helper.putItem(user);

        res.setErrorCode(200);
        res.setMessage("success");
        res.setData(user.getUserID());

        return res;
    }

    public ErrorResult checkZipCode(String zipCode, String userId) {
        ErrorResult res = new ErrorResult();

        if (userId == null || zipCode == null) {
            res.setErrorCode(100);
            res.setMessage("missing parameters");
            return res;
        }

        try {
            List<ServiceZipCodeTableItem> zipCodes = queryHelper.getServiceZipCode(zipCode);
            if (zipCodes != null && !zipCodes.isEmpty()) {
                res.setErrorCode(200);
                res.setMessage("success");
                res.setData(zipCode);
                return res;
            }
            List<RequestZipCodeTableItem> requestZipCodes = queryHelper.getRequestZipCode(zipCode);

            RequestZipCodeTableItem requestZipCode;

            if (requestZipCodes != null && !requestZipCodes.isEmpty()) {
                requestZipCode = requestZipCodes.get(0);
            } else {
                requestZipCode = new RequestZipCodeTableItem();
                requestZipCode.setZipcode(zipCode);
            }

            if (requestZipCode.getRequestors() == null) {
                requestZipCode.setRequestors(new ArrayList<String>());
            }

            requestZipCode.getRequestors().add(userId);
            helper.putItem(requestZipCode);
            res.setErrorCode(115);
            res.setMessage("zipCode currently not under service");
            res.setData(zipCode);
        } catch (Exception ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }

        return res;
    }

    public List<MembershipTableItem> getAllPlans() {
        return scanHelper.getAllPlans();
    }

    public List<StorageTableItem> getAllStorages() {
        return scanHelper.getAllStorages();
    }

    public ErrorResult lockItem(LockItemParameters params) throws ParseException {
        ErrorResult res = new ErrorResult();

        if (params.getItemID() == null || params.getUserID() == null) {
            res.setErrorCode(100);
            res.setMessage("missing parameters");
            return res;
        }

        List<InventoryTableItem> items = queryHelper.getStoredItemsByID(params.getItemID());

        if (items == null || items.isEmpty()) {
            res.setErrorCode(105);
            res.setMessage("invalid itemid");
            return res;
        }

        InventoryTableItem item = items.get(0);

        if (!item.getOwnerID().equals("1")) {
            res.setErrorCode(107);
            res.setMessage("item not available for swap");
            return res;
        }

        UserTableItem user = getUserProfile(params.getUserID());

        if (user == null) {
            res.setErrorCode(100);
            res.setMessage("invalid userid");
            return res;
        }

        InventoryTableItem.Lock lock = item.getLock();

        if (!isItemLocked(lock, params.getUserID())) {
            lock = new InventoryTableItem.Lock();
            lock.setUserID(params.getUserID());
            SimpleDateFormat formatter = new SimpleDateFormat();
            lock.setTimeStamp(formatter.format(new Date()));

            item.setLock(lock);
            helper.putItem(item);
            res.setErrorCode(200);
            res.setMessage("success");
        } else {
            res.setErrorCode(108);
            res.setMessage("item already locked by another user");
        }

        return res;
    }

    public UserTableItem getUserProfile(String userId) {
        List<UserTableItem> userList = scanHelper.getUsersBasedById(userId);

        if (userList == null || userList.isEmpty()) {
            return null;
        }

        UserTableItem user = userList.get(0);

        if (user.getReferralCode() == null) {
            user.setReferralCode(generateReferralCode(user.getFirstName()));
            helper.putItem(user);
        }

        return user;
    }

    public ErrorResult addNewCard(AddNewCard params) {
        ErrorResult res = new ErrorResult();

        UserTableItem user = getUserProfile(params.getUserID());

        if (user == null) {
            res.setErrorCode(101);
            res.setMessage("invalid userid");
            return res;
        }

        List<UserTableItem.Payments> payments = user.getPayments();

        if (payments == null) {
            payments = new ArrayList<>();
        }

        AdvancedEncryptionStandard aes = new AdvancedEncryptionStandard(user.getUserID().replaceAll("-", ""));
        String paramsCardNumber;

        try {
            paramsCardNumber = aes.decrypt(params.getCardNumber());
        } catch (Exception ex) {
            res.setErrorCode(500);
            res.setMessage(ex.getMessage());
            return res;
        }
        if (!StringUtil.isBlank(params.getToken())) {
            try {
                String cardID = StripeService.addNewCard(user.getStripeID(), params.getToken());
                params.setCardID(cardID);
            } catch (AuthenticationException | InvalidRequestException | APIConnectionException | CardException | APIException ex) {
                res.setErrorCode(500);
                res.setMessage(ex.getMessage());
                return res;
            }
        }

        UserTableItem.Payments payment = new UserTableItem.Payments();
        payment.setCardID(params.getCardID());
        payment.setCardNumber(paramsCardNumber);
        payment.setCardType(params.getCardType());
        payment.setToken(params.getToken());

        payments.add(payment);
        user.setPayments(payments);

        helper.putItem(user);

        res.setErrorCode(200);
        res.setMessage("success");
        return res;
    }

    public ErrorResult deleteCard(DeleteCardParameters params) {
        ErrorResult res = new ErrorResult();

        UserTableItem user = getUserProfile(params.getUserID());

        if (user == null) {
            res.setErrorCode(101);
            res.setMessage("invalid userid");
            return res;
        }

        boolean stripeStatus = false;

        try {
            stripeStatus = StripeService.deleteCard(user.getStripeID(), params.getCardID());
        } catch (AuthenticationException | InvalidRequestException | APIConnectionException | CardException | APIException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
            res.setData(ex.getMessage());
        }

        if (!stripeStatus) {
            res.setErrorCode(500);
            if (StringUtil.isBlank(res.getData())) {
                res.setMessage("deleting the card on Stripe failed");
            } else {
                res.setMessage(res.getData());
                res.setData(null);
            }
            return res;
        }

        for (UserTableItem.Payments payment : user.getPayments()) {
            if (payment.getCardID().equals(params.getCardID())) {
                user.getPayments().remove(payment);
                break;
            }
        }

        helper.putItem(user);

        res.setErrorCode(200);
        res.setMessage("success");
        return res;
    }

    public List<UserTableItem.Payments> getUserPayments(String userID) {
        UserTableItem user = getUserProfile(userID);
        return user.getPayments();
    }

    public MembershipTableItem getMemberShipPlan(String membershipID) {
        List<MembershipTableItem> planList = queryHelper.getMembershipPlanById(membershipID);

        if (planList == null || planList.isEmpty()) {
            return null;
        }

        return planList.get(0);
    }

    public List<TimeslotTableItem> getAllTimeslots() {
        List<TimeslotTableItem> timeslots = scanHelper.getAllTimeslots();
        return timeslots;
    }

    public WeeksTimeslots getTimeslotsForTheWeek() {
        List<TimeslotDTO> timeslotsDTO = new ArrayList<>();

        int week = DateUtil.getWeekOfTheYear(new Date());

        Map<String, String> dateMap = DateUtil.getDatesForWeek(week);

        Iterator<Map.Entry<String, String>> iterator = dateMap.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, String> next = iterator.next();
            String day = next.getKey();
            String date = next.getValue();
            List<String> timesList = new ArrayList<>();
            timesList.add("8am-10am");
            timesList.add("10am-12pm");
            timesList.add("12pm-2pm");
            timesList.add("2pm-4pm");
            timesList.add("4pm-6pm");
            timesList.add("6pm-8pm");

            List<TimeslotTableItem> timeslotsForDate = scanHelper.getTimeslotsByDate(date);

            if (timeslotsForDate == null || timeslotsForDate.isEmpty()) {
                for (String time : timesList) {
                    TimeslotDTO timeslot = new TimeslotDTO();
                    timeslot.setTimeslotID("");
                    timeslot.setDate(date);
                    timeslot.setDay(day);
                    timeslot.setTimeslot(time);
                    timeslot.setAvailablityCount(0);
                    timeslot.setBookedCount(0);
                    timeslotsDTO.add(timeslot);
                }
            } else {
                for (TimeslotTableItem timeslotItem : timeslotsForDate) {
                    TimeslotDTO timeslot = new TimeslotDTO();
                    timeslot.setTimeslotID(timeslotItem.getTimeslotID());
                    timeslot.setDate(date);
                    timeslot.setDay(day);
                    timeslot.setTimeslot(timeslotItem.getTimeslot());
                    timeslot.setAvailablityCount(timeslotItem.getAvailabilityCount());
                    timeslot.setBookedCount(scanHelper.getBookedCountForTimeslot(date, timeslot.getTimeslot()));
                    timeslotsDTO.add(timeslot);
                    timesList.remove(timeslotItem.getTimeslot());
                }
                for (String time : timesList) {
                    TimeslotDTO timeslot = new TimeslotDTO();
                    timeslot.setTimeslotID("");
                    timeslot.setDate(date);
                    timeslot.setDay(day);
                    timeslot.setTimeslot(time);
                    timeslot.setAvailablityCount(0);
                    timeslot.setBookedCount(0);
                    timeslotsDTO.add(timeslot);
                }
            }
        }

        WeeksTimeslots weeksTimeslots = new WeeksTimeslots();
        weeksTimeslots.setDateMap(dateMap);

        List<WeeksTimeslots.Timeslot> timeslots = new LinkedList<>();
        List<String> timesList = new ArrayList<>();
        timesList.add("8am-10am");
        timesList.add("10am-12pm");
        timesList.add("12pm-2pm");
        timesList.add("2pm-4pm");
        timesList.add("4pm-6pm");
        timesList.add("6pm-8pm");

        for (String time : timesList) {
            WeeksTimeslots.Timeslot timeslot = new WeeksTimeslots.Timeslot();
            timeslot.setTime(time);

            List<WeeksTimeslots.Timeslot.Count> counts = new LinkedList<>();
            // The timeslotsDTO object is already in the correct order of dates; Now the order of time is be corrected
            for (String date : dateMap.values()) {
                for (TimeslotDTO timeslotDTO : timeslotsDTO) {
                    if (timeslotDTO.getDate().equals(date) && timeslotDTO.getTimeslot().equals(time)) {
                        WeeksTimeslots.Timeslot.Count count = new WeeksTimeslots.Timeslot.Count();
                        count.setAvailabilityCount(timeslotDTO.getAvailablityCount());
                        count.setBookedCount(timeslotDTO.getBookedCount());
                        counts.add(count);
                        break;
                    }
                }

            }
            timeslot.setCounts(counts);
            timeslots.add(timeslot);
        }

        weeksTimeslots.setTimeslots(timeslots);
        return weeksTimeslots;
    }

    public List<TimeslotTableItem> getAvailableTimeSlots() throws ParseException {
        return scanHelper.getAvailableTimeslots();
    }

    public ErrorResult addToWishlist(String userID, String itemID) {
        UserTableItem user = getUserProfile(userID);
        ErrorResult res = new ErrorResult();

        if (user == null) {
            res.setErrorCode(100);
            res.setMessage("invalid userid");
            return res;
        }

        List<InventoryTableItem> items = queryHelper.getStoredItemsByID(itemID);

        if (items == null || items.isEmpty()) {
            res.setErrorCode(105);
            res.setMessage("invalid itemid");
            return res;
        }

        UserTableItem.Wishlist wishList = user.getWishlist();

        if (wishList == null) {
            wishList = new UserTableItem.Wishlist();
            user.setWishlist(wishList);
        }

        Set<String> itemIDs = wishList.getItemIDs();

        if (itemIDs == null) {
            itemIDs = new HashSet<>();
            wishList.setItemIDs(itemIDs);
        }

        itemIDs.add(itemID);
        helper.putItem(user);

        res.setErrorCode(200);
        res.setMessage("success");
        return res;
    }

    public ErrorResult removeFromWishlist(String userID, String itemID) {
        ErrorResult res = new ErrorResult();
        UserTableItem user = getUserProfile(userID);

        if (user == null) {
            res.setErrorCode(100);
            res.setMessage("invalid userid");
            return res;
        }

        List<InventoryTableItem> items = queryHelper.getStoredItemsByID(itemID);

        if (items == null || items.isEmpty()) {
            res.setErrorCode(105);
            res.setMessage("invalid itemid");
            return res;
        }

        UserTableItem.Wishlist wishList = user.getWishlist();

        if (wishList != null) {
            Set<String> itemIDs = wishList.getItemIDs();

            if (itemIDs != null) {
                Iterator<String> iterator = itemIDs.iterator();

                while (iterator.hasNext()) {
                    String next = iterator.next();
                    if (next.equals(itemID)) {
                        iterator.remove();
                        break;
                    }
                }

                if (itemIDs.isEmpty()) {
                    user.setWishlist(null);
                }
            }

        }

        helper.putItem(user);

        res.setErrorCode(200);
        res.setMessage("success");
        return res;
    }

    public List<InventoryTableItem> getUsersWishList(String userID) {
        List<InventoryTableItem> list = new ArrayList<>();

        UserTableItem user = getUserProfile(userID);

        if (user == null) {
            return null;
        }

        UserTableItem.Wishlist wishlist = user.getWishlist();
        if (wishlist != null) {
            Set<String> itemIDs = wishlist.getItemIDs();

            if (itemIDs != null) {
                for (String itemID : itemIDs) {
                    List<InventoryTableItem> items = queryHelper.getStoredItemsByID(itemID);
                    if (items != null && !items.isEmpty()) {
                        InventoryTableItem item = items.get(0);
                        if (item.getOwnerID().equals("1")) {
                            list.add(item);
                        } else {
                            removeFromWishlist(userID, itemID);
                        }
                    }
                }
            }
        }

        return list;
    }

    public ErrorResult getUserAddresses(String userID) {
        UserTableItem user = getUserProfile(userID);
        ErrorResult res = new ErrorResult();

        if (user == null) {
            res.setErrorCode(101);
            res.setMessage("invalid userid");
            return res;
        }

        res.setErrorCode(200);
        res.setMessage("success");

        ObjectMapper mapper = new ObjectMapper();
        try {
            String addresses = mapper.writeValueAsString(user.getAddress());
            res.setData(addresses);
        } catch (JsonProcessingException ex) {
            res.setData("[]");
        }

        return res;
    }

    public ErrorResult schedulePickup(PickupRequestParameters request) {
        ErrorResult res = new ErrorResult();
        res.setErrorCode(500);
        res.setMessage("fail");
        String userID = request.getUsrid();
        UserTableItem user = getUserProfile(userID);
        List<TimeslotTableItem> timeslots = queryHelper.getTimeSlotByID(request.getTsID());

        if (user == null || timeslots == null || timeslots.isEmpty()) {
            res.setMessage("invalid parameters");
            return res;
        }

        if (timeslots.get(0).getAvailabilityCount() <= 0) {
            res.setMessage("time slot not available");
            return res;
        }

        UserRequestTableItem req = new UserRequestTableItem();
        req.setDate(request.getDate());
        req.setTime(request.getTime());
        req.setType("pickup");
        UserRequestTableItem.User reqUser = new UserRequestTableItem.User();
        reqUser.setFirstName(user.getFirstName());
        reqUser.setLastName(user.getLastName());
        reqUser.setUserID(user.getUserID());

        UserRequestTableItem.User.Address reqUserAddress = new UserRequestTableItem.User.Address();

        for (UserTableItem.Address address : user.getAddress()) {
            if (address.getAddressID().equals(request.getAddressID())) {
                reqUserAddress.setStreetAddress(address.getStreetAddress());
                reqUserAddress.setApartment(address.getApartment());
                reqUserAddress.setCity(address.getCity());
                reqUserAddress.setState(address.getState());
                reqUserAddress.setZipCode(address.getZipCode());
                reqUserAddress.setSpecialInstructions(address.getSpecialInstructions());
                reqUser.setZipCode(address.getZipCode());
                break;
            }
        }

        reqUser.setAddress(reqUserAddress);
        reqUser.setPhoneNumber(user.getPhoneNumber());

        req.setUser(reqUser);
        helper.putItem(req);

        List<UserTableItem.Requests> pendingRequest = user.getPendingRequests();

        if (pendingRequest == null) {
            pendingRequest = new ArrayList<>();
        }

        UserTableItem.Requests userRequest = new UserTableItem.Requests();
        userRequest.setDate(timeslots.get(0).getDate());
        userRequest.setTime(timeslots.get(0).getTimeslot());
        userRequest.setType("pickup");
        userRequest.setRequestID(req.getUserRequestID());

        pendingRequest.add(userRequest);

        user.setPendingRequests(pendingRequest);

        List<Notification> notifications = user.getNotifications();

        NotificationFactory.addPickupScheduledNotification(notifications, request.getDate(), request.getTime(), req.getUserRequestID());

        user.setNotifications(notifications);

        helper.putItem(user);

        TimeslotTableItem timeslot = timeslots.get(0);
        timeslot.setAvailabilityCount(timeslot.getAvailabilityCount() - 1);
        helper.putItem(timeslot);

        if (user.getPhoneNumber() != null) {
            String smsBody = SMSBodyFactory.createPickupConfirmation(user.getFirstName());
            TwilioSMSService.sendSMS(smsBody, user.getPhoneNumber());
        }

        res.setErrorCode(200);
        res.setMessage("success");
        return res;
    }

    public ErrorResult scheduleDropOff(DropOffRequestParameters params) throws ParseException {
        ErrorResult res = new ErrorResult();
        res.setErrorCode(200);
        res.setMessage("success");

        if (params.getUsrid() == null || params.getTsID() == null) {
            res.setErrorCode(100);
            res.setMessage("missing parameters");
            return res;
        }

        UserTableItem user = getUserProfile(params.getUsrid());
        List<TimeslotTableItem> timeslots = queryHelper.getTimeSlotByID(params.getTsID());

        if (user == null) {
            res.setErrorCode(101);
            res.setMessage("invalid userid");
            return res;
        }

        if (timeslots == null || timeslots.isEmpty()) {
            res.setErrorCode(109);
            res.setMessage("invalid timeslot id");
            return res;
        }

        if (timeslots.get(0).getAvailabilityCount() <= 0) {
            res.setErrorCode(110);
            res.setMessage("time slot not available");
            return res;
        }

        TimeslotTableItem timeslot = timeslots.get(0);
        UserRequestTableItem req = new UserRequestTableItem();
        req.setDate(timeslot.getDate());
        req.setTime(timeslot.getTimeslot());
        req.setTimestamp(new SimpleDateFormat("MM.dd.yyyy").parse(timeslot.getDate()).getTime());
        req.setType("drop off");
        UserRequestTableItem.User reqUser = new UserRequestTableItem.User();
        reqUser.setFirstName(user.getFirstName());
        reqUser.setLastName(user.getLastName());
        reqUser.setUserID(user.getUserID());
        reqUser.setPhoneNumber(user.getPhoneNumber());
        if (params.getStoredItemIDs() != null && !params.getStoredItemIDs().isEmpty()) {
            List<UserRequestTableItem.Item> reqItems = new ArrayList<>();
            for (String storedItemId : params.getStoredItemIDs()) {
                List<InventoryTableItem> storedItems = queryHelper.getStoredItemsByID(storedItemId);

                if (storedItems == null || storedItems.isEmpty()) {
                    res.setData(storedItemId);
                    res.setErrorCode(105);
                    res.setMessage("invalid productid");
                    return res;
                }

                InventoryTableItem storedItem = storedItems.get(0);
                UserRequestTableItem.Item item = new UserRequestTableItem.Item();
                item.setItemName(storedItem.getItemName());
                item.setItemCodes(storedItem.getItemCode());
                item.setImagesBase64(storedItem.getImageURLs());
                item.setStoredItemID(storedItem.getStoredItemId());
                reqItems.add(item);
            }
            req.setItems(reqItems);
        }

        if (user.getAddress() != null) {
            UserTableItem.Address userAddress = null;

            for (int i = 0; i < user.getAddress().size(); i++) {
                if (user.getAddress().get(i).getAddressID().equals(params.getAddressID())) {
                    userAddress = user.getAddress().get(i);
                    break;
                }
            }

            if (userAddress != null) {
                UserRequestTableItem.User.Address reqUserAddress = new UserRequestTableItem.User.Address();
                reqUserAddress.setStreetAddress(userAddress.getStreetAddress());
                reqUserAddress.setApartment(userAddress.getApartment());
                reqUserAddress.setCity(userAddress.getCity());
                reqUserAddress.setState(userAddress.getState());
                reqUserAddress.setZipCode(userAddress.getZipCode());
                reqUserAddress.setSpecialInstructions(userAddress.getSpecialInstructions());
                reqUser.setAddress(reqUserAddress);
            }
        }
        req.setUser(reqUser);
        req.setStatus("not started");
        helper.putItem(req);

        List<UserTableItem.Requests> pendingRequest = user.getPendingRequests();

        if (pendingRequest == null) {
            pendingRequest = new ArrayList<>();
        }

        UserTableItem.Requests userRequest = new UserTableItem.Requests();
        userRequest.setDate(timeslot.getDate());
        userRequest.setTime(timeslot.getTimeslot());
        userRequest.setType("drop off");
        userRequest.setRequestID(req.getUserRequestID());

        pendingRequest.add(userRequest);

        user.setPendingRequests(pendingRequest);

        List<Notification> notifications = user.getNotifications();

        NotificationFactory.addDeliveryScheduledNotification(notifications, req.getDate(), req.getTime(), req.getUserRequestID());

        user.setNotifications(notifications);

        helper.putItem(user);

        timeslot.setAvailabilityCount(timeslot.getAvailabilityCount() - 1);
        helper.putItem(timeslot);

        if (user.getPhoneNumber() != null) {
            String smsBody = SMSBodyFactory.createDropOffConfirmation(user.getFirstName());
            TwilioSMSService.sendSMS(smsBody, user.getPhoneNumber());
        }

        res.setErrorCode(200);
        res.setMessage("success");
        res.setData(userRequest.getRequestID());

        return res;
    }

    public ErrorResult schedulePickup(String userID, String addressID, String timeSlotID) throws ParseException {
        ErrorResult res = new ErrorResult();
        res.setErrorCode(500);
        res.setMessage("fail");
        if (userID == null || timeSlotID == null) {
            res.setErrorCode(100);
            res.setMessage("missing parameters");
            return res;
        }

        UserTableItem user = getUserProfile(userID);
        List<TimeslotTableItem> timeslots = queryHelper.getTimeSlotByID(timeSlotID);

        if (user == null) {
            res.setErrorCode(101);
            res.setMessage("invalid userid");
            return res;
        }

        if (timeslots == null || timeslots.isEmpty()) {
            res.setErrorCode(109);
            res.setMessage("invalid timeslot id");
            return res;
        }

        if (timeslots.get(0).getAvailabilityCount() <= 0) {
            res.setErrorCode(110);
            res.setMessage("time slot not available");
            return res;
        }

        TimeslotTableItem timeslot = timeslots.get(0);
        UserRequestTableItem req = new UserRequestTableItem();
        req.setDate(timeslot.getDate());
        req.setTime(timeslot.getTimeslot());
        req.setTimestamp(new SimpleDateFormat("MM.dd.yyyy").parse(req.getDate()).getTime());
        req.setType("pickup");
        UserRequestTableItem.User reqUser = new UserRequestTableItem.User();
        reqUser.setFirstName(user.getFirstName());
        reqUser.setLastName(user.getLastName());
        reqUser.setUserID(user.getUserID());
//        reqUser.setZipCode(user.getAddress().getZipCode());

        if (user.getAddress() != null) {
            UserTableItem.Address userAddress = null;

            for (int i = 0; i < user.getAddress().size(); i++) {
                if (user.getAddress().get(i).getAddressID().equals(addressID)) {
                    userAddress = user.getAddress().get(i);
                    break;
                }
            }

            if (userAddress != null) {
                UserRequestTableItem.User.Address reqUserAddress = new UserRequestTableItem.User.Address();
                reqUserAddress.setStreetAddress(userAddress.getStreetAddress());
                reqUserAddress.setApartment(userAddress.getApartment());
                reqUserAddress.setCity(userAddress.getCity());
                reqUserAddress.setState(userAddress.getState());
                reqUserAddress.setZipCode(userAddress.getZipCode());
                reqUserAddress.setSpecialInstructions(userAddress.getSpecialInstructions());
                reqUser.setAddress(reqUserAddress);
            } else {
                res.setErrorCode(106);
                res.setMessage("invalid addressID");
                return res;
            }
        }
        reqUser.setPhoneNumber(user.getPhoneNumber());
        req.setUser(reqUser);
        req.setStatus("not started");
        helper.putItem(req);

        List<UserTableItem.Requests> pendingRequest = user.getPendingRequests();

        if (pendingRequest == null) {
            pendingRequest = new ArrayList<>();
        }

        UserTableItem.Requests userRequest = new UserTableItem.Requests();
        userRequest.setDate(timeslot.getDate());
        userRequest.setTime(timeslot.getTimeslot());
        userRequest.setType("pickup");
        userRequest.setRequestID(req.getUserRequestID());

        pendingRequest.add(userRequest);

        user.setPendingRequests(pendingRequest);

        List<Notification> notifications = user.getNotifications();

        if (notifications == null) {
            notifications = new ArrayList<>();
        }

        NotificationFactory.addPickupScheduledNotification(notifications, req.getDate(), req.getTime(), req.getUserRequestID());

        user.setNotifications(notifications);

        helper.putItem(user);

        timeslot.setAvailabilityCount(timeslot.getAvailabilityCount() - 1);
        helper.putItem(timeslot);

        if (user.getPhoneNumber() != null) {
            String smsBody = SMSBodyFactory.createPickupConfirmation(user.getFirstName());
            TwilioSMSService.sendSMS(smsBody, user.getPhoneNumber());
        }

        res.setErrorCode(200);
        res.setMessage("success");
        res.setData(userRequest.getRequestID());
        return res;
    }

    public ErrorResult cancelRequest(String userID, String userRequestID) {
        ErrorResult res = new ErrorResult();

        UserTableItem user = getUserProfile(userID);

        if (user == null) {
            res.setErrorCode(101);
            res.setMessage("invalid user id");
            return res;
        }

        List<UserRequestTableItem> userRequests = queryHelper.getUserRequestByID(userRequestID);

        if (userRequests == null || userRequests.isEmpty()) {
            res.setErrorCode(402);
            res.setMessage("invalid user request");
            return res;
        }

        UserRequestTableItem userRequest = userRequests.get(0);

        if (!userRequest.getUser().getUserID().equals(userID)) {
            res.setErrorCode(402);
            res.setMessage("invalid user request");
            return res;
        }

        if (userRequest.getStatus().equals("completed")) {
            res.setErrorCode(402);
            res.setMessage("request already completed");
            return res;
        }

        userRequest.setStatus("cancelled");
        helper.putItem(userRequest);

        List<Notification> notifications = user.getNotifications();

        if (notifications == null) {
            notifications = new ArrayList<>();
        }

        if (userRequest.getType().equals("pickup")) {
            NotificationFactory.addPickupCanceledNotification(notifications, userRequest.getDate(), userRequest.getTime());
        } else {
            NotificationFactory.addDeliveryCancelledNotification(notifications, userRequest.getDate(), userRequest.getTime());
        }

        user.setNotifications(notifications);
        helper.putItem(user);

        res.setErrorCode(200);
        res.setMessage("success");
        return res;
    }

    public ErrorResult scheduleRequest(String userID, String timeSlotID, String type) {
        ErrorResult res = new ErrorResult();
        res.setErrorCode(500);
        res.setMessage("fail");
        UserTableItem user = getUserProfile(userID);
        List<TimeslotTableItem> timeslots = queryHelper.getTimeSlotByID(timeSlotID);

        if (user == null || timeslots == null || timeslots.isEmpty()) {
            res.setErrorCode(100);
            res.setMessage("missing parameters");
            return res;
        }

        if (timeslots.get(0).getAvailabilityCount() <= 0) {
            res.setErrorCode(110);
            res.setMessage("time slot not available");
            return res;
        }

        TimeslotTableItem timeslot = timeslots.get(0);
        UserRequestTableItem req = new UserRequestTableItem();
        req.setDate(timeslot.getDate());
        req.setTime(timeslot.getTimeslot());
        req.setType(type);
        UserRequestTableItem.User reqUser = new UserRequestTableItem.User();
        reqUser.setFirstName(user.getFirstName());
        reqUser.setLastName(user.getLastName());
        reqUser.setUserID(user.getUserID());
//        reqUser.setZipCode(user.getAddress().getZipCode());

        if (user.getAddress() != null) {
            UserRequestTableItem.User.Address reqUserAddress = new UserRequestTableItem.User.Address();
            reqUserAddress.setStreetAddress(user.getAddress().get(0).getStreetAddress());
            reqUserAddress.setApartment(user.getAddress().get(0).getApartment());
            reqUserAddress.setCity(user.getAddress().get(0).getCity());
            reqUserAddress.setState(user.getAddress().get(0).getState());
            reqUserAddress.setZipCode(user.getAddress().get(0).getZipCode());
            reqUserAddress.setSpecialInstructions(user.getAddress().get(0).getSpecialInstructions());
            reqUser.setAddress(reqUserAddress);
        }
        req.setUser(reqUser);
        req.setStatus("not started");
        helper.putItem(req);

        List<UserTableItem.Requests> pendingRequest = user.getPendingRequests();

        if (pendingRequest == null) {
            pendingRequest = new ArrayList<>();
        }

        UserTableItem.Requests userRequest = new UserTableItem.Requests();
        userRequest.setDate(timeslot.getDate());
        userRequest.setTime(timeslot.getTimeslot());
        userRequest.setType(type);
        userRequest.setRequestID(req.getUserRequestID());

        pendingRequest.add(userRequest);

        user.setPendingRequests(pendingRequest);

        List<Notification> notifications = user.getNotifications();

        if (notifications == null) {
            notifications = new ArrayList<>();
        }

        if (type.equalsIgnoreCase("pickup")) {
            NotificationFactory.addPickupScheduledNotification(notifications, timeslot.getDate(), timeslot.getTimeslot(), req.getUserRequestID());
        } else {
            NotificationFactory.addDeliveryScheduledNotification(notifications, timeslot.getDate(), timeslot.getTimeslot(), req.getUserRequestID());
        }

        user.setNotifications(notifications);

        helper.putItem(user);

        timeslot.setAvailabilityCount(timeslot.getAvailabilityCount() - 1);
        helper.putItem(timeslot);

        res.setErrorCode(200);
        res.setMessage("success");
        res.setData(userRequest.getRequestID());
        return res;
    }

    public List<CategoryTableItem> getCategories(String parentID) {
        return scanHelper.getCategories(parentID);
    }

    public List<ItemTableItem> getItemsByCategory(String categoryID) {
        if (categoryID == null || "".equals(categoryID)) {
            return scanHelper.getAllItems();
        }
        return scanHelper.getItemsByCategory(categoryID);
    }

    public List<InventoryTableItem> getBathwaterItems(String filterID, String userID) {
        List<InventoryTableItem> list = scanHelper.getBathwaterItems(filterID);
        Date now = new Date();

        SimpleDateFormat formatter = new SimpleDateFormat();

        List<InventoryTableItem> bathwaterItems = new ArrayList<>(list);

        Iterator<InventoryTableItem> iterator = bathwaterItems.iterator();

        while (iterator.hasNext()) {
            InventoryTableItem item = iterator.next();
            InventoryTableItem.Lock lock = item.getLock();

            if (lock != null) {
                try {
                    Date lockTimeStamp = formatter.parse(lock.getTimeStamp());
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(lockTimeStamp);
                    calendar.add(Calendar.MINUTE, 60);

                    if (now.before(calendar.getTime())) {
                        if (!lock.getUserID().equals(userID)) {
                            iterator.remove();
                        }
                    }

                } catch (ParseException ex) {
                    Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        return bathwaterItems;
    }

    public List<InventoryTableItem> getBathwaterItems() {
        List<InventoryTableItem> list = scanHelper.getBathwaterItems();
        return list;
    }

    public List<InventoryTableItem> getItemsByUserID(String userID) {
        return scanHelper.getItemByUserID(userID);
    }

    public ItemTableItem getItemById(String itemID) {
        List<ItemTableItem> items = queryHelper.getItemByID(itemID);

        if (items == null || items.isEmpty()) {
            return null;
        }

        return items.get(0);
    }

    public List<UserRequestTableItem> getAllUserRequests(String status) {
        return scanHelper.getAllUserRequests(status);
    }

    public List<UserTableItem> getAllUsers() {
        return scanHelper.getAllUsers();
    }

    public ErrorResult storeItem(StoreItemParameters params) {
        ErrorResult res = new ErrorResult();
        res.setErrorCode(500);
        res.setMessage("fail");

        List<ItemTableItem> itemList = queryHelper.getItemByID(params.getItemCode());
        UserTableItem user = getUserProfile(params.getUserID());

        if (itemList != null && !itemList.isEmpty()) {
            if (user != null) {
                ItemTableItem item = itemList.get(0);
                InventoryTableItem storeItem = new InventoryTableItem();
                storeItem.setCategoryID(item.getItemID());
                storeItem.setItemName(item.getName());
                storeItem.setCategoryID(item.getCategoryID());
//                storeItem.setSharable(params.getSharable() == null ? 0 : params.getSharable());
                storeItem.setOwnerID(user.getUserID());
                storeItem.setOwnerFirstName(user.getFirstName());
                storeItem.setOwnerLasName(user.getLastName());
                storeItem.setOwnerEmail(user.getEmailAddress());
                storeItem.setStorageID(params.getStorageID());
                storeItem.setBinNumber(params.getBinNumber());
                storeItem.setStorageTimestamp(new Date().toString());
                helper.putItem(storeItem);

                UserTableItem.StoredItems userStoredItem = new UserTableItem.StoredItems();
                userStoredItem.setItemID(storeItem.getStoredItemId());
                userStoredItem.setName(storeItem.getItemName());
//                userStoredItem.setSharable(params.getSharable() == null ? 0 : params.getSharable());

                userStoredItem.setDescription(item.getDescription());
                UserTableItem.StoredItems.Status status = new UserTableItem.StoredItems.Status();
                status.setStatus("stored");
                status.setStorageID(storeItem.getStorageID());
                status.setBinNumber(storeItem.getBinNumber());
                userStoredItem.setStatus(status);
                List<UserTableItem.StoredItems> storedItems = user.getStoredItems();

                if (storedItems == null) {
                    storedItems = new ArrayList<>();
                }

                storedItems.add(userStoredItem);
                user.setStoredItems(storedItems);
                helper.putItem(user);

                res.setErrorCode(200);
                res.setMessage("success");
            } else {
                res.setErrorCode(404);
                res.setMessage("Invalid user");
            }
        } else {
            res.setErrorCode(404);
            res.setMessage("Invalid item ID");
        }

        return res;
    }

    public ErrorResult shareItem(ShareItemParameters params) {
        ErrorResult res = new ErrorResult();
        res.setErrorCode(500);
        res.setMessage("fail");

//        UserTableItem user = getUserProfile(params.getUserID());
//
//        if (user == null) {
//            res.setErrorCode(404);
//            res.setMessage("invalid user");
//            return res;
//        }
//
//        List<UserTableItem.StoredItems> userStoredItemsList = user.getStoredItems();
//        List<InventoryTransitTableItem> storedItemTableItems = queryHelper.getStoredItemsByID(params.getItemID());
//
//        if (userStoredItemsList == null || userStoredItemsList.isEmpty()) {
//            res.setErrorCode(404);
//            res.setMessage("invalid item");
//            return res;
//        }
//
//        for (UserTableItem.StoredItems userStoredItem : userStoredItemsList) {
//            if (userStoredItem.getItemID().equals(params.getItemID())) {
//                if (userStoredItem. == 0) {
//                    userStoredItem.setSharable(1);
//                    helper.putItem(user);
//
//                    storedItemTableItems.get(0).setSharable(1);
//                    helper.putItem(storedItemTableItems.get(0));
//
//                    res.setErrorCode(200);
//                    res.setMessage("success");
//                } else {
//                    res.setErrorCode(400);
//                    res.setMessage("item already shared");
//                }
//
//                return res;
//            }
//        }
//
//        res.setErrorCode(404);
//        res.setMessage("invalid item");
        return res;
    }

    public ErrorResult updateCredits(String sid, int credits) {
        ErrorResult res = new ErrorResult();
        res.setErrorCode(500);
        res.setMessage("fail");
        List<InventoryTableItem> items = queryHelper.getStoredItemsByID(sid);
        if (items != null && items.size() > 0) {
            InventoryTableItem item = items.get(0);
            item.setBinNumber(item.getBinNumber());
            item.setCategoryID(item.getCategoryID());
            item.setDescription(item.getDescription());
            item.setImageName(item.getImageName());
            item.setImageURLs(item.getImageURLs());
            item.setItemCode(item.getItemCode());
            item.setLock(item.getLock());
            item.setObservations(item.getObservations());
            item.setOwnerEmail(item.getOwnerEmail());
            item.setOwnerFirstName(item.getOwnerFirstName());
            item.setOwnerID(item.getOwnerID());
            item.setOwnerLasName(item.getOwnerLasName());
            item.setParentID(item.getParentID());
            item.setSwap(item.getSwap());
            item.setStatus(item.getStatus());
            item.setStorageID(item.getStorageID());
            item.setStorageTimestamp(item.getStorageTimestamp());
            item.setStoredItemId(item.getStoredItemId());
            item.setSubItems(item.getSubItems());
            item.setCredits(new Integer(credits));
            helper.putItem(item);
            res.setErrorCode(200);
            res.setMessage("Success");
        } else {
            res.setErrorCode(404);
            res.setMessage("invalid stored item ID");
        }
        return res;
    }

    public ErrorResult updateStoredItem(String sid, int credits, String statusStr, String category, String location) {
        ErrorResult res = new ErrorResult();
        res.setErrorCode(500);
        res.setMessage("fail");
        boolean status = false;
        // check if the categoryID is valid.
        if (category != null) {
            List<CategoryTableItem> categories = getCategories("0");
            for (CategoryTableItem cat : categories) {
                if (category.equalsIgnoreCase(cat.getCategoryID())) {
                    status = true;
                    break;
                }
            }
            if (!status) {
                res.setMessage("Invalid Category ID");
                return res;
            }
        }

        List<InventoryTableItem> items = queryHelper.getStoredItemsByID(sid);
        if (items != null && items.size() > 0) {
            InventoryTableItem item = items.get(0);
            item.setBinNumber(item.getBinNumber());

            if (category != null) {
                item.setCategoryID(category);
            }

            item.setDescription(item.getDescription());
            item.setImageName(item.getImageName());
            item.setImageURLs(item.getImageURLs());
            item.setItemCode(item.getItemCode());
            item.setLock(item.getLock());
            item.setObservations(item.getObservations());
            item.setOwnerEmail(item.getOwnerEmail());
            item.setOwnerFirstName(item.getOwnerFirstName());
            item.setOwnerID(item.getOwnerID());
            item.setOwnerLasName(item.getOwnerLasName());
            item.setParentID(item.getParentID());
            item.setSwap(item.getSwap());

            if (statusStr != null) {
                item.setStatus(statusStr);
            }

            if (location != null) {
                item.setLocation(location);
            }

            item.setStorageID(item.getStorageID());
            item.setStorageTimestamp(item.getStorageTimestamp());
            item.setStoredItemId(item.getStoredItemId());
            item.setSubItems(item.getSubItems());

            if (credits != 0) {
                item.setCredits(new Integer(credits));
            }

            helper.putItem(item);
            res.setErrorCode(200);
            res.setMessage("Success");
        } else {
            res.setErrorCode(404);
            res.setMessage("invalid stored item ID");
        }
        return res;
    }

    public ErrorResult borrowItem(BorrowItemParameters params) {
        ErrorResult res = new ErrorResult();
        res.setErrorCode(500);
        res.setMessage("fail");

        UserTableItem user = getUserProfile(params.getUserID());
        UserTableItem owner = getUserProfile(params.getOwnerID());
        List<InventoryTableItem> storedItems = queryHelper.getStoredItemsByID(params.getItemID());

        if (user == null || owner == null || storedItems == null || storedItems.isEmpty()) {
            res.setMessage("invalid parameters");
            return res;
        }

        if (storedItems.get(0).getSwap() != 1) {
            res.setMessage("not sharable");
            return res;
        }

        List<UserTableItem.StoredItems> ownerStoredItems = owner.getStoredItems();

        if (ownerStoredItems == null || ownerStoredItems.isEmpty()) {
            res.setMessage("invalid item");
            return res;
        }

        for (UserTableItem.StoredItems storedItem : ownerStoredItems) {
            if (storedItem.getItemID().equals(params.getItemID())) {
                UserTableItem.StoredItems.Status status = storedItem.getStatus();
                status.setStatus("borrowed");
                status.setBinNumber("");
                status.setStorageID("");
                status.setBorrowerID(user.getUserID());
                helper.putItem(owner);
                break;
            }
        }

        List<UserTableItem.BorrowedItems> borrowedItems = user.getBorrowedItems();

        if (borrowedItems == null) {
            borrowedItems = new ArrayList<>();
        }

        UserTableItem.BorrowedItems borrowedItem = new UserTableItem.BorrowedItems();
        borrowedItem.setItemID(storedItems.get(0).getStoredItemId());
        borrowedItem.setName(storedItems.get(0).getItemName());
        borrowedItem.setOwnerID(owner.getUserID());

        borrowedItems.add(borrowedItem);
        user.setBorrowedItems(borrowedItems);
        helper.putItem(user);

        return res;
    }

    public List<InventoryTableItem> getSharingItems() {
        return scanHelper.getSharingItems();
    }

    public ErrorResult activateUser(String userID) {
        ErrorResult res = new ErrorResult();
        res.setErrorCode(200);
        res.setMessage("success");

        UserTableItem user = getUserProfile(userID);

        if (user == null) {
            res.setMessage("invalid user");
            return res;
        }

        user.setActive(1);
        helper.putItem(user);

        return res;
    }

    public ErrorResult deactivateUser(String userID) {
        ErrorResult res = new ErrorResult();
        res.setErrorCode(200);
        res.setMessage("success");

        UserTableItem user = getUserProfile(userID);

        if (user == null) {
            res.setMessage("invalid user");
            return res;
        }

        user.setActive(0);
        helper.putItem(user);

        return res;
    }

    public CategoryTableItem getCategoryByID(String id) {
        List<CategoryTableItem> categories = queryHelper.getCategoryById(id);

        if (categories == null || categories.isEmpty()) {
            return null;
        }

        return categories.get(0);
    }

    public ErrorResult updateCategory(String id, String title, String description, String parentID, String imageName) {
        ErrorResult res = new ErrorResult();
        res.setErrorCode(200);
        res.setMessage("success");

        CategoryTableItem category = getCategoryByID(id);

        if (category == null) {
            res.setMessage("invalid category id");
            return res;
        }

        category.setDescription(description);
        category.setTitle(title);
        category.setParentID(parentID);
        category.setImageName(imageName);
        helper.putItem(category);

        return res;
    }

    public ErrorResult createCategory(String title, String description, String parentID, String imageName) {
        ErrorResult res = new ErrorResult();
        res.setErrorCode(200);
        res.setMessage("fail");

        CategoryTableItem category = new CategoryTableItem();
        category.setDescription(description);
        category.setTitle(title);
        category.setParentID(parentID);
        category.setImageName(imageName);
        helper.putItem(category);
        res.setMessage(category.getCategoryID());

        return res;
    }

    public ErrorResult deleteCategory(String id) {
        ErrorResult res = new ErrorResult();
        res.setErrorCode(200);
        res.setMessage("success");

        CategoryTableItem category = getCategoryByID(id);

        if (category == null) {
            res.setMessage("invalid category");
            return res;
        }

        helper.deleteItem(category);

        return res;
    }

    public ErrorResult createTimeSlot(String date, String time, Integer availabilityCount) {
        ErrorResult res = new ErrorResult();
        res.setErrorCode(200);
        res.setMessage("success");

        List<TimeslotTableItem> existingList = scanHelper.getTimeslotsByTimeAndDate(date, time);

        if (existingList == null || existingList.isEmpty()) {

            TimeslotTableItem timeSlot = new TimeslotTableItem();
            timeSlot.setDate(date);
            timeSlot.setTimeslot(time);
            timeSlot.setAvailabilityCount(availabilityCount);

            helper.putItem(timeSlot);
            res.setData(timeSlot.getTimeslotID());
        } else {
            TimeslotTableItem existing = existingList.get(0);
            existing.setAvailabilityCount(availabilityCount);
            helper.putItem(existing);
            res.setData(existing.getTimeslotID());
        }

        return res;
    }

    public String addProductImage(String itemID, String fileName) {
        List<InventoryTableItem> storedItems = queryHelper.getStoredItemsByID(itemID);

        if (storedItems == null || storedItems.isEmpty()) {
            return "fail";
        }

        for (InventoryTableItem item : storedItems) {
            item.setImageName(fileName);
            helper.putItem(item);
        }

        return "success";
    }

    public String addCategoryImage(String categoryID, String fileName) {
        List<CategoryTableItem> categoryList = queryHelper.getCategoryById(categoryID);
        if (categoryList == null || categoryList.isEmpty()) {
            return "fail";
        }

        for (CategoryTableItem category : categoryList) {
            category.setImageName(fileName);
            helper.putItem(category);
        }

        return "success";
    }

    public String addZipCode(String zipCode) {
        List<ServiceZipCodeTableItem> zipCodes = scanHelper.getZipCodeByZipCode(zipCode);
        if (zipCodes == null || zipCodes.isEmpty()) {
            ServiceZipCodeTableItem zip = new ServiceZipCodeTableItem();
            zip.setZipCode(zipCode);
            helper.putItem(zip);
        }
        return "success";
    }

    public List<ServiceZipCodeTableItem> getAllZipCodeTableItems() {
        return scanHelper.getAllZipCodes();
    }

    public void deleteZipCode(String zipCode) {
        List<ServiceZipCodeTableItem> zipCodes = scanHelper.getZipCodeByZipCode(zipCode);

        if (zipCodes != null) {
            for (ServiceZipCodeTableItem zip : zipCodes) {
                helper.deleteItem(zip);
            }
        }
    }

    public ErrorResult setCreditsToItem(SetCreditsParameters params) {
        ErrorResult res = new ErrorResult();

        List<InventoryTableItem> storedItems = queryHelper.getStoredItemsByID(params.getItemID());

        if (storedItems == null || storedItems.isEmpty()) {
            res.setErrorCode(105);
            res.setMessage("invalid itemid");
            return res;
        }

        InventoryTableItem storedItem = storedItems.get(0);
        storedItem.setCredits(params.getCredits());

        helper.putItem(storedItem);

        UserTableItem owner = getUserProfile(storedItem.getOwnerID());
        List<UserTableItem.StoredItems> userItems = owner.getStoredItems();

        for (UserTableItem.StoredItems item : userItems) {
            if (item.getItemID().equals(params.getItemID())) {
                item.setCredits(params.getCredits());
                break;
            }
        }

        helper.putItem(owner);
        res.setErrorCode(200);
        res.setMessage("success");
        return res;
    }

    public ErrorResult swapItem(UserTableItem user, String itemID) {
        ErrorResult res = new ErrorResult();

        List<InventoryTableItem> storedItems = queryHelper.getStoredItemsByID(itemID);

        if (storedItems == null || storedItems.isEmpty()) {
            res.setErrorCode(105);
            res.setMessage("invalid itemid");
            return res;
        }

        if (user == null) {
            res.setErrorCode(101);
            res.setMessage("invalid userid");
            return res;
        }

        InventoryTableItem storedItem = storedItems.get(0);
        UserTableItem bathwater = getUserProfile("1");

        if (!storedItem.getOwnerID().equals("1")) { // change ownership to bathwater
            //swap use case
            swapFromUserToBathwater(storedItem, user, bathwater);
            helper.putItem(user);

        } else {
            // change ownership to user
            swapFromBathwaterToUser(storedItem, user, bathwater);
            helper.putItem(user);
        }

        res.setErrorCode(200);
        res.setMessage("success");
        res.setMiscellaneous(user);
        return res;
    }

    private void swapFromUserToBathwater(InventoryTableItem storedItem, UserTableItem user, UserTableItem bathwater) {
        storedItem.setOwnerEmail(bathwater.getEmailAddress());
        storedItem.setOwnerFirstName(bathwater.getFirstName());
        storedItem.setOwnerID(bathwater.getUserID());
        storedItem.setOwnerLasName(bathwater.getLastName());

        helper.putItem(storedItem);

        List<UserTableItem.StoredItems> userItems = user.getStoredItems();

        if (userItems == null) {
            userItems = new ArrayList<>();
            UserTableItem.StoredItems item = new UserTableItem.StoredItems();
            item.setCategoryID(storedItem.getCategoryID());
            item.setCredits(storedItem.getCredits());
            item.setDescription(storedItem.getDescription());
            item.setItemID(storedItem.getStoredItemId());
            item.setName(storedItem.getItemName());
            userItems.add(item);
        }

        Iterator<UserTableItem.StoredItems> iterator = userItems.iterator();
        int size = userItems.size();

        while (iterator.hasNext()) {
            UserTableItem.StoredItems next = iterator.next();

            if (!StringUtil.isBlank(next.getItemID()) && next.getItemID().equals(storedItem.getStoredItemId())) {
                List<UserTableItem.StoredItems> bathwaterItems = bathwater.getStoredItems();

                if (bathwaterItems == null) {
                    bathwaterItems = new ArrayList<>();
                }

                bathwaterItems.add(next);
                bathwater.setStoredItems(bathwaterItems);

                helper.putItem(bathwater);
                iterator.remove();
                break;
            }
        }

        if (userItems.size() != size - 1) {
            UserTableItem.StoredItems item = new UserTableItem.StoredItems();
            item.setCategoryID(storedItem.getCategoryID());
            item.setCredits(storedItem.getCredits());
            item.setDescription(storedItem.getDescription());
            item.setItemID(storedItem.getStoredItemId());
            item.setName(storedItem.getItemName());
            List<UserTableItem.StoredItems> bathwaterItems = bathwater.getStoredItems();

            if (bathwaterItems == null) {
                bathwaterItems = new ArrayList<>();
            }

            bathwaterItems.add(item);
            bathwater.setStoredItems(bathwaterItems);

            helper.putItem(bathwater);
        }
        Integer currentCredits = user.getCredits() == null ? 0 : user.getCredits();
        user.setCredits(currentCredits + storedItem.getCredits());
        user.setStoredItems(userItems);

        DateFormat formatter = new SimpleDateFormat("MM.dd.yyyy");
        List<UserTableItem.SwapHistory> swapHistories = user.getSwapHistory();

        if (swapHistories == null) {
            swapHistories = new ArrayList<>();
        }

        UserTableItem.SwapHistory swapHistory = new UserTableItem.SwapHistory();
        swapHistory.setDate(formatter.format(new Date()));

        UserTableItem.Item swapItem = new UserTableItem.Item();
        swapItem.setItemID(storedItem.getStoredItemId());
        swapItem.setItemName(storedItem.getItemName());
        swapItem.setCategoryID(storedItem.getCategoryID());
        swapItem.setCredits(storedItem.getCredits());
        swapItem.setImageURLs(storedItem.getImageURLs());
        swapItem.setParentID(storedItem.getParentID());
        swapHistory.setItem(swapItem);
        swapHistory.setStatus("swapped");
        swapHistories.add(swapHistory);
        user.setSwapHistory(swapHistories);
    }

    private void swapFromBathwaterToUser(InventoryTableItem storedItem, UserTableItem user, UserTableItem bathwater) {
        int itemCredits = storedItem.getCredits() == null ? 0 : storedItem.getCredits();
        int userCredits = user.getCredits() == null ? 0 : user.getCredits();
        storedItem.setOwnerEmail(user.getEmailAddress());
        storedItem.setOwnerID(user.getUserID());
        storedItem.setOwnerFirstName(user.getFirstName());
        storedItem.setOwnerLasName(user.getLastName());

        helper.putItem(storedItem);

        List<UserTableItem.StoredItems> userItems = user.getStoredItems();

        if (userItems == null) {
            userItems = new ArrayList<>();
        }

        UserTableItem.StoredItems item = new UserTableItem.StoredItems();
        item.setCategoryID(storedItem.getCategoryID());
        item.setCredits(storedItem.getCredits());
        item.setDescription(storedItem.getDescription());
        item.setName(storedItem.getItemName());
        item.setItemID(storedItem.getStoredItemId());
        userItems.add(item);
        user.setStoredItems(userItems);
        user.setCredits(userCredits - itemCredits);

        DateFormat formatter = new SimpleDateFormat("MM.dd.yyyy");
        List<UserTableItem.SwapHistory> swapHistories = user.getSwapHistory();

        if (swapHistories == null) {
            swapHistories = new ArrayList<>();
        }

        UserTableItem.SwapHistory swapHistory = new UserTableItem.SwapHistory();
        swapHistory.setDate(formatter.format(new Date()));

        UserTableItem.Item swapItem = new UserTableItem.Item();
        swapItem.setItemID(storedItem.getStoredItemId());
        swapItem.setItemName(storedItem.getItemName());
        swapItem.setCategoryID(storedItem.getCategoryID());
        swapItem.setCredits(storedItem.getCredits());
        swapItem.setImageURLs(storedItem.getImageURLs());
        swapItem.setParentID(storedItem.getParentID());
        swapHistory.setItem(swapItem);
        swapHistory.setStatus("order placed");
        swapHistories.add(swapHistory);
        user.setSwapHistory(swapHistories);
    }

    private UserTableItem.SwapHistory getTodaysSwapHistory(List<UserTableItem.SwapHistory> historyList, DateFormat formatter) {
        UserTableItem.SwapHistory swapHistory = null;

        Date today = new Date();
        String date = formatter.format(today);

        for (UserTableItem.SwapHistory history : historyList) {
            if (date.equals(history.getDate())) {
                swapHistory = history;
                break;
            }
        }

        return swapHistory;
    }

    public ErrorResult checkOut(CheckoutList checkoutList) throws IOException, ParseException {
        ErrorResult res = new ErrorResult();

        String userID = checkoutList.getUserID();

        UserTableItem user = getUserProfile(userID);

        if (user == null) {
            res.setErrorCode(101);
            res.setMessage("invalid userid");
            return res;
        }

        int creditsToBeReduced = 0;

        if (checkoutList.getSwapItems() != null && !checkoutList.getSwapItems().isEmpty()) {
            for (CheckoutList.SwapItems swapItem : checkoutList.getSwapItems()) {
                List<InventoryTableItem> storedItems = queryHelper.getStoredItemsByID(swapItem.getItemID());

                if (storedItems == null || storedItems.isEmpty()) {
                    res.setErrorCode(105);
                    res.setMessage("invalid itemid");
                    return res;
                }

                if (!storedItems.get(0).getOwnerID().equals("1")) {
                    res.setErrorCode(107);
                    res.setMessage("item no longer available for swap");
                    return res;
                }

                creditsToBeReduced += swapItem.getCredits();
            }
        }

        if (checkoutList.getRetrieveItems() != null && !checkoutList.getRetrieveItems().isEmpty()) {
            for (CheckoutList.RetrieveItems retrieveItem : checkoutList.getRetrieveItems()) {

                List<String> subItems = Arrays.asList(retrieveItem.getItemID().split(","));
                String itemID = subItems.get(0);

                List<InventoryTableItem> storedItems = queryHelper.getStoredItemsByID(itemID);

                if (storedItems == null || storedItems.isEmpty()) {
                    res.setErrorCode(105);
                    res.setMessage("invalid itemid");
                    return res;
                }
            }
        }

        // handle promo codes
        if (checkoutList.getPromoCode() != null) {
            int promoCredits = getPromoCodeDiscout(checkoutList.getPromoCode());
            creditsToBeReduced = creditsToBeReduced == 0 ? promoCredits : creditsToBeReduced - promoCredits;
        }

        int balanceCredits = (user.getCredits() == null ? 0 : user.getCredits())
                - creditsToBeReduced;

        int balanceAmount = 0;

        if (balanceCredits < 0) {
            balanceAmount += Math.abs(balanceCredits) * 5;
            balanceCredits = 0;
        }

        // handle shipping charge payment
        if (!StringUtil.isBlank(checkoutList.getCardID())) {
            int totalCharge = (balanceAmount + checkoutList.getShippingCharges()) * 100;
            if (totalCharge > 0) {
                try {
                    String cardID;
                    if (checkoutList.getCardID().contains("tok_")) {
                        cardID = StripeService.addNewCard(user.getStripeID(), checkoutList.getCardID());
                    } else {
                        cardID = checkoutList.getCardID();
                    }

                    StripeService.charge((balanceAmount + checkoutList.getShippingCharges()) * 100, user.getStripeID(), cardID);
                    user.setCredits(user.getCredits() + (balanceAmount / 5));
                } catch (AuthenticationException | InvalidRequestException | APIConnectionException | CardException | APIException ex) {
                    res.setErrorCode(500);
                    res.setMessage("payment failed - " + ex.getMessage());
                    return res;
                }
            }
        }

        List<UserRequestTableItem.Item> reqItems = new ArrayList<>();
        // handle swap items - here swap is only swapping items for credits
        if (checkoutList.getSwapItems() != null && !checkoutList.getSwapItems().isEmpty()) {
            UserTableItem bathwater = getUserProfile("1");
            for (CheckoutList.SwapItems swapItem : checkoutList.getSwapItems()) {
                InventoryTableItem storedItem = queryHelper.getStoredItemsByID(swapItem.getItemID()).get(0);
                swapFromBathwaterToUser(storedItem, user, bathwater);

                UserRequestTableItem.Item item = new UserRequestTableItem.Item();
                item.setItemName(storedItem.getItemName());
                item.setItemCodes(storedItem.getItemCode());
                item.setImagesBase64(storedItem.getImageURLs());
                reqItems.add(item);
            }
        }

        String pickupDay = null;
        String pickupTime = null;
        String deliveryDay = null;
        String deliveryTime = null;

        // handle retrieve items
        if (checkoutList.getRetrieveItems() != null && !checkoutList.getRetrieveItems().isEmpty()) {
            for (CheckoutList.RetrieveItems retrieveItem : checkoutList.getRetrieveItems()) {
                String storedItemId = retrieveItem.getItemID();
                List<String> subItems = Arrays.asList(retrieveItem.getItemID().split(","));
                storedItemId = subItems.get(0);
                List<InventoryTableItem> storedItems = queryHelper.getStoredItemsByID(storedItemId);

                if (storedItems == null || storedItems.isEmpty()) {
                    res.setData(storedItemId);
                    res.setErrorCode(105);
                    res.setMessage("invalid productid");
                    return res;
                }

                // ; seperated items. 
                //String allItems = retrieveItem.getItemID();
                //List<String> subItems = Arrays.asList(allItems.split(","));
                // check 
                if (subItems.size() == 1) {
                    InventoryTableItem storedItem = storedItems.get(0);
                    UserRequestTableItem.Item item = new UserRequestTableItem.Item();
                    item.setItemName(storedItem.getItemName());
                    item.setItemCodes(storedItem.getItemCode());
                    item.setImagesBase64(storedItem.getImageURLs());
                    reqItems.add(item);
                } else {
                    for (int i = 1; i < subItems.size(); i++) {
                        String sItemCode = subItems.get(i);
                        InventoryTableItem storedItem = storedItems.get(0);
                        List<InventoryTableItem.SubItem> sItems = storedItem.getSubItems();
                        UserRequestTableItem.Item item = new UserRequestTableItem.Item();
                        item.setItemName(storedItem.getItemName());
                        List<String> codes = new ArrayList<String>();
                        item.setItemCodes(storedItem.getItemCode());
                        item.setImagesBase64(storedItem.getImageURLs());

                        List<UserRequestTableItem.SubItem> updateSubItems = new ArrayList<UserRequestTableItem.SubItem>();
                        for (int j = 0; j < sItems.size(); j++) {
                            InventoryTableItem.SubItem sub = sItems.get(j);
                            if (sub.getItemCode().equalsIgnoreCase(sItemCode)) {
                                UserRequestTableItem.SubItem s = new UserRequestTableItem.SubItem();
                                s.setDescription(sub.getDescription());
                                s.setStatus("REQUESTED_DROP_OFF");
                                s.setImageURLs(sub.getImageURLs());
                                s.setItemName(sub.getItemName());
                                s.setItemCode(sub.getItemCode());
                                updateSubItems.add(s);
                            }
                        }
                        item.setSubItems(updateSubItems);
                        reqItems.add(item);
                    }
                }
            }
        }

        // handle scheduling drop off request
        if (!reqItems.isEmpty() && !checkoutList.getIsPickUpRequired()) {
            String timeslotID = checkoutList.getTimeslotID();

            if (timeslotID == null) {
                res.setErrorCode(109);
                res.setMessage("invalid timeslotid");
                return res;
            }

            List<TimeslotTableItem> timeslots = queryHelper.getTimeSlotByID(timeslotID);

            if (timeslots == null || timeslots.isEmpty()) {
                res.setErrorCode(109);
                res.setMessage("invalid timeslotid");
                return res;
            }

            TimeslotTableItem timeslot = timeslots.get(0);

            if (timeslot.getAvailabilityCount() <= 0) {
                res.setErrorCode(110);
                res.setMessage("timeslot not available");
                return res;
            }

            UserRequestTableItem req = new UserRequestTableItem();
            req.setDate(timeslot.getDate());
            req.setTime(timeslot.getTimeslot());
            req.setType("drop off");
            UserRequestTableItem.User reqUser = new UserRequestTableItem.User();
            reqUser.setFirstName(user.getFirstName());
            reqUser.setLastName(user.getLastName());
            reqUser.setUserID(user.getUserID());
            reqUser.setPhoneNumber(user.getPhoneNumber());
            req.setItems(reqItems);

            List<UserTableItem.Address> addresses = user.getAddress();

            if (addresses != null) {
                UserRequestTableItem.User.Address reqUserAddress = new UserRequestTableItem.User.Address();

                for (UserTableItem.Address address : addresses) {
                    if (address.getAddressID().equals(checkoutList.getAddressID())) {
                        reqUserAddress.setStreetAddress(address.getStreetAddress());
                        reqUserAddress.setApartment(address.getApartment());
                        reqUserAddress.setCity(address.getCity());
                        reqUserAddress.setState(address.getState());
                        reqUserAddress.setZipCode(address.getZipCode());
                        reqUserAddress.setSpecialInstructions(address.getSpecialInstructions());
                        reqUser.setAddress(reqUserAddress);
                        break;
                    }
                }
            }
            req.setUser(reqUser);
            req.setStatus("not started");
            req.setTimestamp(new SimpleDateFormat("MM.dd.yyyy").parse(timeslot.getDate()).getTime());
            helper.putItem(req);
            res.setDropOffRequestID(req.getUserRequestID());

            List<UserTableItem.Requests> pendingRequest = user.getPendingRequests();

            if (pendingRequest == null) {
                pendingRequest = new ArrayList<>();
            }

            UserTableItem.Requests userRequest = new UserTableItem.Requests();
            userRequest.setDate(timeslot.getDate());
            userRequest.setTime(timeslot.getTimeslot());
            userRequest.setType("drop off");
            userRequest.setRequestID(req.getUserRequestID());

            pendingRequest.add(userRequest);
            user.setPendingRequests(pendingRequest);
            timeslot.setAvailabilityCount(timeslot.getAvailabilityCount() - 1);
            deliveryDay = timeslot.getDate();

            deliveryTime = timeslot.getTimeslot();

            helper.putItem(timeslot);
        } else if (checkoutList.getIsPickUpRequired() != null && checkoutList.getIsPickUpRequired()
                && reqItems.isEmpty()) {
            String timeslotID = checkoutList.getTimeslotID();

            if (StringUtil.isBlank(timeslotID)) {
                res.setErrorCode(109);
                res.setMessage("invalid timeslotid");
                return res;
            }

            List<TimeslotTableItem> timeslots = queryHelper.getTimeSlotByID(timeslotID);

            if (timeslots == null || timeslots.isEmpty()) {
                res.setErrorCode(109);
                res.setMessage("invalid timeslotid");
                return res;
            }

            TimeslotTableItem timeslot = timeslots.get(0);

            if (timeslot.getAvailabilityCount() <= 0) {
                res.setErrorCode(110);
                res.setMessage("timeslot not available");
                return res;
            }

            UserRequestTableItem req = new UserRequestTableItem();
            req.setDate(timeslot.getDate());
            req.setTime(timeslot.getTimeslot());
            req.setTimestamp(new SimpleDateFormat("MM.dd.yyyy").parse(timeslot.getDate()).getTime());
            req.setType("pickup");
            req.setNumberOfItemsToBePicked(checkoutList.getnItems());
            UserRequestTableItem.User reqUser = new UserRequestTableItem.User();
            reqUser.setFirstName(user.getFirstName());
            reqUser.setLastName(user.getLastName());
            reqUser.setUserID(user.getUserID());
            reqUser.setPhoneNumber(user.getPhoneNumber());

            List<UserTableItem.Address> addresses = user.getAddress();

            if (addresses != null) {
                UserRequestTableItem.User.Address reqUserAddress = new UserRequestTableItem.User.Address();

                for (UserTableItem.Address address : addresses) {
                    if (address.getAddressID().equals(checkoutList.getAddressID())) {
                        reqUserAddress.setStreetAddress(address.getStreetAddress());
                        reqUserAddress.setApartment(address.getApartment());
                        reqUserAddress.setCity(address.getCity());
                        reqUserAddress.setState(address.getState());
                        reqUserAddress.setZipCode(address.getZipCode());
                        reqUserAddress.setSpecialInstructions(address.getSpecialInstructions());
                        reqUser.setAddress(reqUserAddress);
                        break;
                    }
                }
            }
            req.setUser(reqUser);
            req.setStatus("not started");
            helper.putItem(req);
            res.setPickupRequestID(req.getUserRequestID());

            List<UserTableItem.Requests> pendingRequest = user.getPendingRequests();

            if (pendingRequest == null) {
                pendingRequest = new ArrayList<>();
            }

            UserTableItem.Requests userRequest = new UserTableItem.Requests();
            userRequest.setDate(timeslot.getDate());
            userRequest.setTime(timeslot.getTimeslot());
            userRequest.setType("pickup");
            userRequest.setRequestID(req.getUserRequestID());

            pendingRequest.add(userRequest);

            user.setPendingRequests(pendingRequest);

            timeslot.setAvailabilityCount(timeslot.getAvailabilityCount() - 1);

            helper.putItem(timeslot);

            pickupDay = timeslot.getDate();
            pickupTime = timeslot.getTimeslot();
        } else {
            String timeslotID = checkoutList.getTimeslotID();

            if (timeslotID == null) {
                res.setErrorCode(109);
                res.setMessage("invalid timeslotid");
                return res;
            }

            List<TimeslotTableItem> timeslots = queryHelper.getTimeSlotByID(timeslotID);

            if (timeslots == null || timeslots.isEmpty()) {
                res.setErrorCode(109);
                res.setMessage("invalid timeslotid");
                return res;
            }

            TimeslotTableItem timeslot = timeslots.get(0);

            if (timeslot.getAvailabilityCount() <= 0) {
                res.setErrorCode(110);
                res.setMessage("timeslot not available");
                return res;
            }

            UserRequestTableItem req = new UserRequestTableItem();
            req.setDate(timeslot.getDate());
            req.setTime(timeslot.getTimeslot());
            req.setTimestamp(new SimpleDateFormat("MM.dd.yyyy").parse(timeslot.getDate()).getTime());
            req.setType("drop off");
            UserRequestTableItem.User reqUser = new UserRequestTableItem.User();
            reqUser.setFirstName(user.getFirstName());
            reqUser.setLastName(user.getLastName());
            reqUser.setUserID(user.getUserID());
            reqUser.setPhoneNumber(user.getPhoneNumber());
            req.setItems(reqItems);

            List<UserTableItem.Address> addresses = user.getAddress();
            UserTableItem.Address userAddress = null;

            if (addresses != null) {
                UserRequestTableItem.User.Address reqUserAddress = new UserRequestTableItem.User.Address();

                for (UserTableItem.Address address : addresses) {
                    if (address.getAddressID().equals(checkoutList.getAddressID())) {
                        userAddress = address;
                        reqUserAddress.setStreetAddress(address.getStreetAddress());
                        reqUserAddress.setApartment(address.getApartment());
                        reqUserAddress.setCity(address.getCity());
                        reqUserAddress.setState(address.getState());
                        reqUserAddress.setZipCode(address.getZipCode());
                        reqUserAddress.setSpecialInstructions(address.getSpecialInstructions());
                        reqUser.setAddress(reqUserAddress);
                        break;
                    }
                }
            }
            req.setUser(reqUser);
            req.setStatus("not started");
            helper.putItem(req);
            res.setDropOffRequestID(req.getUserRequestID());

            List<UserTableItem.Requests> pendingRequest = user.getPendingRequests();

            if (pendingRequest == null) {
                pendingRequest = new ArrayList<>();
            }

            UserTableItem.Requests userRequest = new UserTableItem.Requests();
            userRequest.setDate(timeslot.getDate());
            userRequest.setTime(timeslot.getTimeslot());
            userRequest.setType("dropoff");
            userRequest.setRequestID(req.getUserRequestID());

            pendingRequest.add(userRequest);

            req = new UserRequestTableItem();
            req.setDate(timeslot.getDate());
            req.setTime(timeslot.getTimeslot());
            req.setType("pickup");
            req.setNumberOfItemsToBePicked(checkoutList.getnItems());
            reqUser = new UserRequestTableItem.User();
            reqUser.setFirstName(user.getFirstName());
            reqUser.setLastName(user.getLastName());
            reqUser.setUserID(user.getUserID());
            reqUser.setPhoneNumber(user.getPhoneNumber());

            if (userAddress != null) {
                UserRequestTableItem.User.Address reqUserAddress = new UserRequestTableItem.User.Address();
                reqUserAddress.setStreetAddress(userAddress.getStreetAddress());
                reqUserAddress.setApartment(userAddress.getApartment());
                reqUserAddress.setCity(userAddress.getCity());
                reqUserAddress.setState(userAddress.getState());
                reqUserAddress.setZipCode(userAddress.getZipCode());
                reqUserAddress.setSpecialInstructions(userAddress.getSpecialInstructions());
                reqUser.setAddress(reqUserAddress);
            }
            req.setUser(reqUser);
            req.setStatus("not started");
            helper.putItem(req);

            userRequest = new UserTableItem.Requests();
            userRequest.setDate(timeslot.getDate());
            userRequest.setTime(timeslot.getTimeslot());
            userRequest.setType("pickup");
            userRequest.setRequestID(req.getUserRequestID());

            pendingRequest.add(userRequest);

            user.setPendingRequests(pendingRequest);
            timeslot.setAvailabilityCount(timeslot.getAvailabilityCount() - 1);
            deliveryDay = timeslot.getDate();

            deliveryTime = timeslot.getTimeslot();

            helper.putItem(timeslot);
        }

        if (user.getCredits() != balanceCredits) {
            user.setCredits(balanceCredits);
        }

        List<Notification> notifications = user.getNotifications();

        if (notifications == null) {
            notifications = new ArrayList<>();
        }

        if (checkoutList.getIsPickUpRequired()) {
            if ((checkoutList.getRetrieveItems() != null && checkoutList.getRetrieveItems().size() > 0)
                    || (checkoutList.getSwapItems() != null && checkoutList.getSwapItems().size() > 0)) {
                NotificationFactory.addPickupAndDeliveryNotification(notifications, deliveryDay, deliveryTime, res.getPickupRequestID(), res.getDropOffRequestID());
                if (user.getPhoneNumber() != null) {
                    String smsBody = SMSBodyFactory.createPickupAndDropOffConfirmation(user.getFirstName());
                    TwilioSMSService.sendSMS(smsBody, user.getPhoneNumber());
                }
            } else {
                NotificationFactory.addPickupScheduledNotification(notifications, pickupDay, pickupTime, res.getPickupRequestID());
                if (user.getPhoneNumber() != null) {
                    String smsBody = SMSBodyFactory.createPickupConfirmation(user.getFirstName());
                    TwilioSMSService.sendSMS(smsBody, user.getPhoneNumber());
                }
            }
        } else if ((checkoutList.getRetrieveItems() != null && checkoutList.getRetrieveItems().size() > 0)
                || (checkoutList.getSwapItems() != null && checkoutList.getSwapItems().size() > 0)) {
            NotificationFactory.addDeliveryScheduledNotification(notifications, deliveryDay, deliveryTime, res.getDropOffRequestID());

            if (user.getPhoneNumber() != null) {
                String smsBody = SMSBodyFactory.createDropOffConfirmation(user.getFirstName());
                TwilioSMSService.sendSMS(smsBody, user.getPhoneNumber());
            }
        }

        user.setNotifications(notifications);

        helper.putItem(user);

        res.setErrorCode(200);
        res.setMessage("success");
        return res;
    }

    public ErrorResult addDriver(String firstName,
            String lastName,
            String licenseId,
            String streetAddress,
            String apartment,
            String city,
            String state,
            String zipCode,
            String phoneNumber,
            String emergencyContactNumber,
            String emailAddress) {

        ErrorResult res = new ErrorResult();
        res.setErrorCode(200);
        res.setMessage("success");

        DriverTableItem driver = new DriverTableItem();
        driver.setFirstName(firstName);
        driver.setLastName(lastName);
        driver.setLicenseID(licenseId);
        driver.setEmailAddress(emailAddress);
        driver.setPhoneNumber(phoneNumber);
        driver.setEmergencyContactNumber(emergencyContactNumber);
        DriverTableItem.Address address = new DriverTableItem.Address();
        address.setStreetAddress(streetAddress);
        address.setApartment(apartment);
        address.setCity(city);
        address.setState(state);
        address.setZipCode(zipCode);
        driver.setAddress(address);
        driver.setStatus("Active");

        helper.putItem(driver);

        return res;
    }

    public ErrorResult addDriverImage(String driverID, String imageName) {
        ErrorResult res = new ErrorResult();
        res.setErrorCode(500);
        res.setMessage("failure");

        String url = "https://s3.amazonaws.com/" + ImageDatabaseUtil.getBUCKET_NAME() + "/" + imageName;

        List<DriverTableItem> drivers = scanHelper.getDriverById(driverID);

        if (drivers != null && !drivers.isEmpty()) {
            for (DriverTableItem driver : drivers) {
                List<DriverTableItem.Image> imageURLs = driver.getImages();
                if (imageURLs == null) {
                    imageURLs = new ArrayList<>();
                }
                DriverTableItem.Image img = new DriverTableItem.Image();
                img.setUrl(url);
                imageURLs.add(img);
                driver.setImages(imageURLs);
                helper.putItem(driver);
            }
            res.setErrorCode(200);
            res.setMessage("success");
        }

        return res;
    }

    public ErrorResult addTruck(String truckType, String licensePlate, String dealerName, String streetAddress, String city, String state, String zipCode, String phoneNumber, String expirationDate) {
        ErrorResult res = new ErrorResult();
        res.setErrorCode(200);
        res.setMessage("success");

        TruckTableItem truck = new TruckTableItem();
        truck.setTruckType(truckType);
        truck.setLicensePlate(licensePlate);
        truck.setDealerName(dealerName);
        TruckTableItem.DealerAddress address = new TruckTableItem.DealerAddress();
        address.setStreetAddress(streetAddress);
        address.setCity(city);
        address.setState(state);
        address.setZipCode(zipCode);
        address.setPhoneNumber(phoneNumber);
        truck.setDealerAddress(address);
        truck.setLeaseExpiration(expirationDate);

        helper.putItem(truck);

        res.setData(truck.getTruckID());
        return res;
    }

    public UserTableItem getUserByEmail(String email) {
        List<UserTableItem> users = scanHelper.getUsersBasedOnEmail(email);
        if (users == null || users.isEmpty()) {
            return null;
        }

        return users.get(0);
    }

    public ErrorResult getShippingCharge(ShippingChargeParmaeters params) {
        ErrorResult res = new ErrorResult();

        List<UserTableItem> users = scanHelper.getUsersBasedById(params.getUserID());

        if (users == null || users.isEmpty()) {
            res.setErrorCode(101);
            res.setMessage("invalid userid");
            res.setData(null);
            return res;
        }

        UserTableItem user = users.get(0);

        res.setErrorCode(200);
        res.setMessage("success");
        if (user.getPendingRequests() != null && user.getPendingRequests().size() > 0) {
            res.setData("15");
        } else {
            res.setData("0");
        }

        return res;
    }

    public ErrorResult addTruckImage(String truckID, String imageName) {
        ErrorResult res = new ErrorResult();
        res.setErrorCode(500);
        res.setMessage("failure");

        String url = "https://s3.amazonaws.com/" + ImageDatabaseUtil.getBUCKET_NAME() + "/" + imageName;

        List<TruckTableItem> trucks = scanHelper.getTruckById(truckID);

        if (trucks != null && !trucks.isEmpty()) {
            for (TruckTableItem truck : trucks) {
                List<TruckTableItem.Image> imageURLs = truck.getImages();
                if (imageURLs == null) {
                    imageURLs = new ArrayList<>();
                }
                TruckTableItem.Image img = new TruckTableItem.Image();
                img.setUrl(url);
                imageURLs.add(img);
                truck.setImages(imageURLs);
                helper.putItem(truck);
            }
            res.setErrorCode(200);
            res.setMessage("success");
        }

        return res;
    }

    public List<DriverTableItem> getAllDrivers() {
        return scanHelper.getAllDrivers();
    }

    public List<TruckTableItem> getAllTrucks() {
        return scanHelper.getAllTrucks();
    }

    public ErrorResult cancelDriverAssignment(String truckID, String driverID, String date) {
        ErrorResult res = new ErrorResult();
        res.setErrorCode(500);
        List<TruckTableItem> trucks = scanHelper.getTruckById(truckID);
        List<DriverTableItem> drivers = scanHelper.getDriverById(driverID);

        if (trucks == null || trucks.isEmpty()
                || drivers == null || drivers.isEmpty()
                || date == null || date.isEmpty()) {
            res.setMessage("invalid parameters");
            return res;
        }

        TruckTableItem truck = trucks.get(0);
        DriverTableItem driver = drivers.get(0);
        SimpleDateFormat formatter = new SimpleDateFormat("MM.dd.yyyy");
        List<TruckTableItem.Driver> assignedDrivers = truck.getAssignedDrivers();

        if (assignedDrivers == null) {
            //assignedDrivers = new ArrayList<>();
            res.setMessage("invalid truck id");
            return res;
        }
        boolean isFound = false;
        List<TruckTableItem.Driver> uptDrivers = new ArrayList<TruckTableItem.Driver>();
        for (TruckTableItem.Driver assignedDriver : assignedDrivers) {
            if (assignedDriver.getDate().equalsIgnoreCase(date) && assignedDriver.getDriverID().equalsIgnoreCase(driverID)) {
                isFound = true;
            } else {
                uptDrivers.add(assignedDriver);
            }
        }
        truck.setAssignedDrivers(uptDrivers);
        helper.putItem(truck);

        List<DriverTableItem.Truck> assignedTrucks = driver.getAssignedTrucks();

        if (assignedTrucks == null) {
            assignedTrucks = new ArrayList<>();
            res.setMessage("invalid driver id");
            return res;
        }

        boolean alreadyAssigned = false;
        String existingTruckId = "";
        List<DriverTableItem.Truck> uptTrucks = new ArrayList<DriverTableItem.Truck>();
        for (DriverTableItem.Truck truckInList : assignedTrucks) {
            if (truckInList.getDate().equalsIgnoreCase(date) && truckInList.getTruckID().equalsIgnoreCase(truckID)) {
                isFound = true;
            } else {
                uptTrucks.add(truckInList);
            }
        }
        driver.setAssignedTrucks(uptTrucks);
        helper.putItem(driver);

        List<DriverTruckHistoryTableItem> historyList = scanHelper.getDriverTruckHistory();
        if (historyList == null) {
            res.setMessage("invalid Input Data");
            return res;
        }

        for (DriverTruckHistoryTableItem history : historyList) {
            if (history.getDate().equalsIgnoreCase(date)
                    && history.getTruckID().equalsIgnoreCase(truckID)
                    && history.getDriverID().equalsIgnoreCase(driverID)) {
                helper.deleteItem(history);
                res.setErrorCode(200);
                res.setMessage("success");
                return res;
            }
        }

        res.setMessage("invalid data");
        return res;

    }

    public ErrorResult assignDriverToTruck(String truckID, String driverID) {
        ErrorResult res = new ErrorResult();
        res.setErrorCode(500);
        List<TruckTableItem> trucks = scanHelper.getTruckById(truckID);
        List<DriverTableItem> drivers = scanHelper.getDriverById(driverID);

        if (trucks == null || trucks.isEmpty() || drivers == null || drivers.isEmpty()) {
            res.setMessage("invalid parameters");
            return res;
        }

        TruckTableItem truck = trucks.get(0);
        DriverTableItem driver = drivers.get(0);
        SimpleDateFormat formatter = new SimpleDateFormat("MM.dd.yyyy");
        List<TruckTableItem.Driver> assignedDrivers = truck.getAssignedDrivers();

        if (assignedDrivers == null) {
            assignedDrivers = new ArrayList<>();
        }

        TruckTableItem.Driver assignedDriver = new TruckTableItem.Driver();
        assignedDriver.setDate(formatter.format(new Date()));
        assignedDriver.setDriverID(driver.getDriverID());
        assignedDriver.setFirstName(driver.getFirstName());
        assignedDriver.setLastName(driver.getLastName());
        assignedDrivers.add(assignedDriver);

        truck.setAssignedDrivers(assignedDrivers);
        helper.putItem(truck);

        List<DriverTableItem.Truck> assignedTrucks = driver.getAssignedTrucks();

        if (assignedTrucks == null) {
            assignedTrucks = new ArrayList<>();
        }

        boolean alreadyAssigned = false;
        String existingTruckId = "";

        for (DriverTableItem.Truck truckInList : assignedTrucks) {
            if (truckInList.getDate().equals(formatter.format(new Date()))) {
                existingTruckId = truckInList.getTruckID();
                truckInList.setLicensePlate(truck.getLicensePlate());
                truckInList.setTruckID(truckID);
                truckInList.setStatus("not started");
                alreadyAssigned = true;

                helper.putItem(driver);
            }
        }

        if (!alreadyAssigned) {
            DriverTableItem.Truck assignedTruck = new DriverTableItem.Truck();
            assignedTruck.setDate(formatter.format(new Date()));
            assignedTruck.setLicensePlate(truck.getLicensePlate());
            assignedTruck.setTruckID(truckID);
            assignedTruck.setStatus("not started");
            assignedTrucks.add(assignedTruck);

            driver.setAssignedTrucks(assignedTrucks);
            helper.putItem(driver);

            DriverTruckHistoryTableItem history = new DriverTruckHistoryTableItem();
            history.setDate(formatter.format(new Date()));
            history.setDriverID(driverID);
            history.setDriverName(driver.getFirstName() + " " + driver.getLastName());
            history.setTruckID(truckID);
            history.setTruckLicensePlate(truck.getLicensePlate());
            history.setStatus("not started");
            helper.putItem(history);
        } else {
            List<DriverTruckHistoryTableItem> historyList = scanHelper.getTodaysDriverTruckHistoryByDriverIDAndTruckID(driverID, existingTruckId);

            DriverTruckHistoryTableItem history = historyList.get(0);
            history.setTruckID(truckID);
            history.setStatus("not started");

            helper.putItem(history);
        }

        res.setErrorCode(200);
        res.setMessage("success");
        return res;
    }

    public DriverTableItem.Truck getTodaysTruckForDriver(String driverID) {
        List<DriverTableItem> drivers = scanHelper.getDriverById(driverID);

        if (drivers == null || drivers.isEmpty()) {
            return null;
        }

        DriverTableItem driver = drivers.get(0);
        List<DriverTableItem.Truck> assignedTrucks = driver.getAssignedTrucks();
        SimpleDateFormat formatter = new SimpleDateFormat("MM.dd.yyyy");
        String today = formatter.format(new Date());
        if (assignedTrucks != null) {
            for (DriverTableItem.Truck truck : assignedTrucks) {
                if (truck.getDate().equals(today)) {
                    //return only single truck i.e. single driver > single truck 
                    return truck;
                }
            }
        }

        return null;
    }

    public ErrorResult startTodaysDriverShift(String driverID) {
        ErrorResult res = new ErrorResult();

        List<DriverTableItem> drivers = scanHelper.getDriverById(driverID);

        if (drivers == null || drivers.isEmpty()) {
            res.setErrorCode(301);
            res.setMessage("inavlid driverid");
            return res;
        }

        DriverTableItem driver = drivers.get(0);
        List<DriverTableItem.Truck> assignedTrucks = driver.getAssignedTrucks();

        if (assignedTrucks == null || assignedTrucks.isEmpty()) {
            res.setErrorCode(302);
            res.setMessage("no truck assigned");
            return res;
        }

        SimpleDateFormat formatter = new SimpleDateFormat("MM.dd.yyyy");
        String today = formatter.format(new Date());

        for (DriverTableItem.Truck truck : assignedTrucks) {
            if (truck.getDate().equals(today)) {
                if (truck.getStatus().equalsIgnoreCase("not started")) {
                    truck.setStatus("in progress");
                    helper.putItem(driver);
                    DriverTruckHistoryTableItem history = scanHelper.getTodaysDriverTruckHistoryByDriverIDAndTruckID(driverID, truck.getTruckID()).get(0);
                    history.setStatus("in progress");
                    res.setErrorCode(200);
                    res.setMessage("success");
                    res.setData(driverID);
                    return res;
                } else {
                    res.setErrorCode(305);
                    if (truck.getStatus().equals("in progress")) {
                        res.setMessage("shift already started");
                    } else {
                        res.setMessage("shift already completed");
                    }
                    return res;
                }
            }
        }

        res.setErrorCode(302);
        res.setMessage("no truck assigned");
        return res;
    }

    public ErrorResult endTodaysDriverShift(String driverID) {
        ErrorResult res = new ErrorResult();

        List<DriverTableItem> drivers = scanHelper.getDriverById(driverID);

        if (drivers == null || drivers.isEmpty()) {
            res.setErrorCode(301);
            res.setMessage("inavlid driverid");
            return res;
        }

        DriverTableItem driver = drivers.get(0);
        List<DriverTableItem.Truck> assignedTrucks = driver.getAssignedTrucks();

        if (assignedTrucks == null || assignedTrucks.isEmpty()) {
            res.setErrorCode(302);
            res.setMessage("no truck assigned");
            return res;
        }

        SimpleDateFormat formatter = new SimpleDateFormat("MM.dd.yyyy");
        String today = formatter.format(new Date());

        for (DriverTableItem.Truck truck : assignedTrucks) {
            if (truck.getDate().equals(today)) {
                if (truck.getStatus().equalsIgnoreCase("in progress")) {
                    truck.setStatus("completed");
                    helper.putItem(driver);
                    DriverTruckHistoryTableItem history = scanHelper.getTodaysDriverTruckHistoryByDriverIDAndTruckID(driverID, truck.getTruckID()).get(0);
                    history.setStatus("completed");
                    res.setErrorCode(200);
                    res.setMessage("success");
                    res.setData(driverID);
                    return res;
                } else {
                    res.setErrorCode(305);
                    if (truck.getStatus().equalsIgnoreCase("not started")) {
                        res.setMessage("shift not started");
                    } else {
                        res.setMessage("shift already completed");
                    }
                    return res;
                }
            }
        }

        res.setErrorCode(302);
        res.setMessage("no truck assigned");
        return res;
    }

    public ErrorResult assignDriverToUserRequest(String driverID, String userRequestID) {
        ErrorResult res = new ErrorResult();
        res.setErrorCode(200);
        res.setMessage("success");

        List<DriverTableItem> drivers = scanHelper.getDriverById(driverID);
        List<UserRequestTableItem> userRequests = scanHelper.getUserRequestsById(userRequestID);

        if (drivers == null || drivers.isEmpty() || userRequests == null || userRequests.isEmpty()) {
            res.setErrorCode(402);
            res.setMessage("invalid driver or user request ID");
            return res;
        }

        UserRequestTableItem userReq = userRequests.get(0);

        UserRequestTableItem.Driver userReqDriver = new UserRequestTableItem.Driver();
        userReqDriver.setDriverID(driverID);
        userReqDriver.setFirstName(drivers.get(0).getFirstName());
        userReqDriver.setLastName(drivers.get(0).getLastName());
        userReq.setDriver(userReqDriver);
        helper.putItem(userReq);

        // TODO handle the removal of already assigned userRequest to the driver in case of reassigning a new driver
        DriverTableItem driver = drivers.get(0);
        DriverTableItem.UserRequest driverReq = new DriverTableItem.UserRequest();
        driverReq.setStatus("incomplete");
        driverReq.setUserRequestID(userRequestID);

        List<DriverTableItem.UserRequest> driverRequests = driver.getAssignedRequests();

        if (driverRequests == null) {
            driverRequests = new ArrayList<>();
        }

        driverRequests.add(driverReq);
        driver.setAssignedRequests(driverRequests);
        helper.putItem(driver);

        return res;
    }

    public List<DriverTruckHistoryTableItem> getAllDriverTruckHistory() {
        return scanHelper.getDriverTruckHistory();
    }

    public ErrorResult driverLogin(DriverLoginParameters params) {
        ErrorResult res = new ErrorResult();
        res.setErrorCode(200);
        res.setMessage("success");

        List<DriverTableItem> drivers = scanHelper.getDriverByEmail(params.getEmail());
        DriverTableItem driver;

        if (drivers == null || drivers.isEmpty()) {
            drivers = scanHelper.getDriverByLicenseID(params.getEmail());

            if (drivers == null || drivers.isEmpty()) {
                res.setErrorCode(300);
                res.setMessage("unregistered driver");
                return res;
            }

        }

        driver = drivers.get(0);

        if (driver.getPassword().equals(params.getPassword())) {
            res.setData(driver.getDriverID());

            if (params.getToken() == null) {
                String token = StringUtil.generateRandomString(5);
                driver.setToken(token);
                helper.putItem(driver);
                res.setToken(token);
            }

            if (params.getToken() != null && !driver.getToken().equals(params.getToken())) {
                res.setErrorCode(205);
                res.setMessage("invalid token");
            }
        } else {
            res.setErrorCode(306);
            res.setMessage("invalid credentials");
        }

        return res;
    }

    public List<UserRequestTableItem> getTodaysEventsForDriver(String driverID) {
        return scanHelper.getTodaysUserRequestsByDriverID(driverID);
    }

    public ErrorResult startEvent(String driverID, String userRequestID, StartEventParameters params) {
        ErrorResult res = new ErrorResult();
        res.setErrorCode(200);
        res.setMessage("success");

        List<UserRequestTableItem> userRequests = scanHelper.getUserRequestsById(userRequestID);

        if (userRequests == null || userRequests.isEmpty()) {
            res.setErrorCode(303);
            res.setMessage("invalid request id");
            return res;
        }

        UserRequestTableItem request = userRequests.get(0);

        if (!request.getDriver().getDriverID().equals(driverID)) {
            res.setErrorCode(308);
            res.setMessage("userRequest not assigned to the driver");
            return res;
        }

        if (!request.getStatus().equals("not started")) {
            res.setErrorCode(307);
            res.setMessage("event already started or completed");
            return res;
        }

        request.setStatus("in progress");
        helper.putItem(request);

        // Sending SMS using Twilio
        UserTableItem user = getUserProfile(request.getUser().getUserID());
        String to = user.getPhoneNumber();

        if (to != null && !to.isEmpty()) {
            String eta = params.getEta();

            String body = SMSBodyFactory.createDriverIsOnTheWayMessage(request.getType(), eta);

            TwilioSMSService.sendSMS(body, to);
        }

        List<Notification> notifications = user.getNotifications();

        if (notifications == null) {
            notifications = new ArrayList<>();
        }

        if (request.getType().equalsIgnoreCase("pickup")) {
            NotificationFactory.addPickupNotification(notifications);
        } else {
            NotificationFactory.addDeliveryNotification(notifications);
        }

        user.setNotifications(notifications);

        return res;
    }

    public ErrorResult noShowEvent(String driverID, String userRequestID) {
        ErrorResult res = new ErrorResult();

        res.setErrorCode(200);
        res.setMessage("success");

        List<UserRequestTableItem> userRequests = scanHelper.getUserRequestsById(userRequestID);

        if (userRequests == null || userRequests.isEmpty()) {
            res.setErrorCode(303);
            res.setMessage("invalid request id");
            return res;
        }

        UserRequestTableItem request = userRequests.get(0);

        if (!request.getDriver().getDriverID().equals(driverID)) {
            res.setErrorCode(308);
            res.setMessage("userRequest not assigned to the driver");
            return res;
        }

        if (!request.getStatus().equals("in progress")) {
            res.setErrorCode(309);
            res.setMessage("event not yet started");
            return res;
        }

        request.setStatus("no show");

        helper.putItem(request);

        return res;
    }

    public ErrorResult completeDropOff(String driverID, String userRequestID, DropOffList dropOffList) {
        ErrorResult res = new ErrorResult();
        res.setErrorCode(200);
        res.setMessage("success");

        int isSubitems = 0;

        List<UserRequestTableItem> userRequests = scanHelper.getUserRequestsById(userRequestID);

        if (userRequests == null || userRequests.isEmpty()) {
            res.setErrorCode(303);
            res.setMessage("invalid request id");
            return res;
        }

        List<DriverTableItem> drivers = scanHelper.getDriverById(driverID);

        if (drivers == null || drivers.isEmpty()) {
            res.setErrorCode(300);
            res.setMessage("unregistered driver");
            return res;
        }

        DriverTableItem driver = drivers.get(0);

        UserRequestTableItem request = userRequests.get(0);

        if (!request.getDriver().getDriverID().equals(driverID)) {
            res.setErrorCode(308);
            res.setMessage("userRequest not assigned to the driver");
            return res;
        }

        if (!request.getStatus().equals("in progress")) {
            res.setErrorCode(309);
            res.setMessage("event not yet started");
            return res;
        }

        List<UserRequestTableItem.Item> dropOffItems = request.getItems();

        List<UserRequestTableItem.Item> newdropOffItems = new ArrayList<UserRequestTableItem.Item>();

        if (dropOffItems == null || dropOffItems.isEmpty()) {
            res.setErrorCode(310);
            res.setMessage("no items to be dropped off");
            return res;
        }

        for (UserRequestTableItem.Item item : dropOffItems) {
            List<InventoryTableItem> storedItems = scanHelper.getItemByItemCode(item.getItemCodes().get(0));
            InventoryTableItem storedItem = storedItems.get(0);

            for (DropOffList.Item dropOffItem : dropOffList.getItems()) {
                List<DropOffList.SubItem> subItems = dropOffItem.getSubItems();
                // if Sub items are zero
                if (subItems == null || subItems.size() < 1) {
                    newdropOffItems.add(item);
                    if (dropOffItem.getItemCodes().equals(storedItem.getItemCode())) {
                        storedItem.setStatus("dropped off");
                        helper.putItem(storedItem);
                        break;
                    }
                } else {
                    List<InventoryTableItem.SubItem> sI = new ArrayList<InventoryTableItem.SubItem>();
                    for (DropOffList.SubItem sItems : subItems) {
                        if (sItems.getStatus().equals("dropped off")) {
                            res.setErrorCode(311);
                            res.setMessage("Item already dropped off");
                            return res;
                        }
                        sItems.setStatus("dropped off");
                        InventoryTableItem.SubItem itm = new InventoryTableItem.SubItem();
                        itm.setDescription(sItems.getDescription());
                        itm.setImageURLs(sItems.getImageURLs());
                        itm.setItemCode(sItems.getItemCode());
                        itm.setItemName(sItems.getItemName());
                        itm.setStatus(sItems.getStatus());

                        sI.add(itm);
                    }
                    storedItem.setSubItems(sI);
                    helper.putItem(storedItem);
                }

            }

        }
        String signatureURL = saveCustomersSignature(dropOffList.getCustomerSignatureBase64());
        request.setSignatureURL(signatureURL);
        request.setStatus("completed");
        helper.putItem(request);

        for (DriverTableItem.UserRequest assignedRequest : driver.getAssignedRequests()) {
            if (assignedRequest.getUserRequestID().equals(userRequestID)) {
                assignedRequest.setStatus("completed");
            }
        }

        helper.putItem(driver);

        UserTableItem user = getUserProfile(request.getUser().getUserID());

        if (user != null) {
            List<Notification> notifications = user.getNotifications();
            NotificationFactory.addDeliveryNotification(notifications);
            user.setNotifications(notifications);

            user = removeStoredItemsFromUser(user, newdropOffItems);

            helper.putItem(user);
        }

        return res;
    }

    public String saveCustomersSignature(String signature) {
        if (!StringUtil.isBlank(signature)) {
            try {
                byte[] imageData = Base64.decode(signature);
                InputStream inputStream = new ByteArrayInputStream(imageData);
                String fileName = System.currentTimeMillis() + ".jpg";
                ImageDatabaseUtil.uploadImage(fileName, inputStream);
                String url = "https://s3.amazonaws.com/" + ImageDatabaseUtil.getBUCKET_NAME() + "/" + fileName;
                return url;
            } catch (Base64DecodingException | NullPointerException | IOException | InterruptedException ex) {
                Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return "";
    }

    private boolean compareItems(InventoryTableItem item, PickupList.PickupItem pickedupItem) {
        return item.getItemCode().equals(pickedupItem.getItemCodes());
    }

    private PickupList.PickupItem getPickedUpItem(InventoryTableItem item, PickupList pickupList) {
        for (PickupList.PickupItem pickedupItem : pickupList.getPickedupItems()) {
            if (pickedupItem.getItemCodes().contains(item.getItemCode().get(0))) {
                return pickedupItem;
            }
        }

        return null;
    }

    private PickupList.SubItem getPickedUpSubItem(InventoryTableItem.SubItem subItem, PickupList.PickupItem pickupItem) {
        if (pickupItem.getSubItems() != null) {
            for (PickupList.SubItem pickedupSubItem : pickupItem.getSubItems()) {
                if (subItem.getItemCode().equals(subItem.getItemCode())) {
                    return pickedupSubItem;
                }
            }
        }
        return null;
    }

    private PickupList.Bin getPickedUpBin(InventoryTableItem item, PickupList pickupList) {
        if (pickupList.getBins() != null) {
            for (PickupList.Bin bin : pickupList.getBins()) {
                if (bin.getBinCode().equals(item.getBinNumber())) {
                    return bin;
                }
            }
        }
        return null;
    }

    public ErrorResult completePickup(PickupList pickupList) {
        ErrorResult res = new ErrorResult();

        List<UserRequestTableItem> userRequests = scanHelper.getUserRequestsById(pickupList.getUserRequestID());

        if (userRequests == null || userRequests.isEmpty()) {
            res.setErrorCode(303);
            res.setMessage("invalid userrequestID");
            return res;
        }

        UserRequestTableItem request = userRequests.get(0);

        if (!request.getDriver().getDriverID().equals(pickupList.getDriverID())) {
            res.setErrorCode(308);
            res.setMessage("userRequest not assigned to the driver");
            return res;
        }

//        if (!request.getStatus().equals("in progress") && !request.getStatus().equals("completed")) {
//            res.setErrorCode(309);
//            res.setMessage("event not yet started");
//            return res;
//        }
        UserTableItem user = getUserProfile(request.getUser().getUserID());

        List<UserRequestTableItem.Item> items = request.getItems();
        // pickup item array will b empty
        if (items == null || items.isEmpty()) {
            items = new ArrayList<>();
            List<PickupList.PickupItem> pickedupItems = pickupList.getPickedupItems();
            //item that has been picked up from user
            if (pickedupItems != null) {
                for (PickupList.PickupItem pickedUpItem : pickedupItems) {
                    // set status "inbound "
                    ErrorResult pickupRes = pickupItem(pickedUpItem, user);
                    if (pickupRes.getErrorCode() == 200) {
                        UserRequestTableItem.Item item = new UserRequestTableItem.Item();
                        item.setItemName(pickedUpItem.getProductName());
                        item.setBrandName(pickedUpItem.getBrandName());
                        item.setCondition(pickedUpItem.getCondition());
                        item.setEventualDamages(pickedUpItem.getEventualDamages());
                        item.setImagesBase64(pickedUpItem.getImagesBase64());
                        item.setItemCodes(pickedUpItem.getItemCodes());
                        item.setStoredItemID(pickupRes.getData());

                        if (pickedUpItem.getSubItems() != null) {
                            List<UserRequestTableItem.SubItem> subItems = new ArrayList<>();
                            for (PickupList.SubItem pickedUpSubItem : pickedUpItem.getSubItems()) {
                                UserRequestTableItem.SubItem subItem = new UserRequestTableItem.SubItem();
                                subItem.setItemCode(pickedUpSubItem.getItemCode());
                                subItem.setItemName(pickedUpSubItem.getItemName());
                                subItem.setDescription(pickedUpSubItem.getDescription());
                                subItem.setImageURLs(pickedUpSubItem.getImagesBase64());
                                subItems.add(subItem);
                            }
                            item.setSubItems(subItems);
                        }

                        items.add(item);
                    }

                }
            }
            request.setItems(items);
        } else {
            // if items exist, the update is only w.r.t imageURLs
            for (UserRequestTableItem.Item pickedUpItem : items) {
                if (!StringUtil.isBlank(pickedUpItem.getStoredItemID())) {
                    InventoryTableItem item = queryHelper.getStoredItemsByID(pickedUpItem.getStoredItemID()).get(0);
                    PickupList.PickupItem pickedupItem = getPickedUpItem(item, pickupList);

                    if (pickedupItem != null) {
                        pickedUpItem.setImagesBase64(pickedupItem.getImagesBase64());
                        item.setImageURLs(pickedupItem.getImagesBase64());
                        for (InventoryTableItem.SubItem subItem : item.getSubItems()) {
                            PickupList.SubItem pickedupSubItem = getPickedUpSubItem(subItem, pickedupItem);

                            if (pickedupSubItem != null) {
                                subItem.setImageURLs(pickedupSubItem.getImagesBase64());
                            }
                        }
                    }

                    helper.putItem(item);
                }
            }

            request.setItems(items);
        }

        List<UserRequestTableItem.Bins> bins = request.getBins();
        List<PickupList.Bin> pickedUpBins = pickupList.getBins();

        if (pickedUpBins != null && !pickedUpBins.isEmpty()) {
            if (bins == null) {
                bins = new ArrayList<>();

                for (PickupList.Bin pickedupBin : pickedUpBins) {
                    ErrorResult pickupRes = pickupBin(pickedupBin, user);
                    if (pickupRes.getErrorCode() == 200) {
                        //set the bin data corresponding to userrEQUEST
                        UserRequestTableItem.Bins bin = new UserRequestTableItem.Bins();
                        bin.setBinCode(pickedupBin.getBinCode());
                        bin.setImageURLs(pickedupBin.getImageBase64());
                        bins.add(bin);
                    }
                }
                request.setBins(bins);
            } else {
                for (PickupList.Bin pickedupBin : pickedUpBins) {
                    InventoryTableItem bin = scanHelper.getBinByBinNumber(pickedupBin.getBinCode()).get(0);
                    bin.setImageURLs(pickedupBin.getImageBase64());
                    helper.putItem(bin);
                }
            }
        }
        String signatureURL = saveCustomersSignature(pickupList.getCustomerSignatureBase64());
        request.setSignatureURL(signatureURL);
        request.setStatus("completed");
        helper.putItem(request);

        if (user != null) {
            List<Notification> notifications = user.getNotifications();
            NotificationFactory.addPickupNotification(notifications);
            user.setNotifications(notifications);
            helper.putItem(user);
        }

        res.setErrorCode(200);
        res.setMessage("success");
        return res;
    }

    public ErrorResult pickupBin(PickupList.Bin bin, UserTableItem user) {
        ErrorResult res = new ErrorResult();
        res.setErrorCode(200);
        res.setMessage("success");

        InventoryTableItem binItem = new InventoryTableItem();
        binItem.setOwnerEmail(user.getEmailAddress());
        binItem.setOwnerFirstName(user.getFirstName());
        binItem.setOwnerLasName(user.getLastName());
        binItem.setOwnerID(user.getUserID());
        binItem.setItemName("Bin");
        binItem.setBinNumber(bin.getBinCode());
        int credits = random.nextInt(10) + 10;
        binItem.setCredits(credits);

        if (bin.getImageBase64() != null && !bin.getImageBase64().isEmpty()) {
            binItem.setImageURLs(bin.getImageBase64());
        }

        binItem.setStatus("pickedUp");
        helper.putItem(binItem);

        return res;
    }

    public ErrorResult  pickupItem(PickupList.PickupItem item, UserTableItem user) {
        ErrorResult res = new ErrorResult();
        res.setErrorCode(200);
        res.setMessage("success");

        InventoryTableItem storedItem = new InventoryTableItem();
        storedItem.setOwnerEmail(user.getEmailAddress());
        storedItem.setOwnerFirstName(user.getFirstName());
        storedItem.setOwnerID(user.getUserID());
        storedItem.setOwnerLasName(user.getLastName());
        storedItem.setItemName(item.getProductName());
        storedItem.setObservations(item.getEventualDamages());
        storedItem.setItemCode(item.getItemCodes());
        int credits = random.nextInt(10) + 10;
        storedItem.setCredits(credits);

        if (item.getSubItems() != null) {
            List<InventoryTableItem.SubItem> subItems = new ArrayList<>();

            for (PickupList.SubItem pickedUpSubItem : item.getSubItems()) {
                InventoryTableItem.SubItem subItem = new InventoryTableItem.SubItem();
                subItem.setItemCode(pickedUpSubItem.getItemCode());
                subItem.setItemName(pickedUpSubItem.getItemName());
                subItem.setDescription(pickedUpSubItem.getDescription());
                subItem.setImageURLs(pickedUpSubItem.getImagesBase64());
                subItems.add(subItem);
            }

            storedItem.setSubItems(subItems);
        }

        if (item.getImagesBase64() != null && !item.getImagesBase64().isEmpty()) {
//            List<String> imageURLs = new ArrayList<>();
//            for (String base64 : item.getImagesBase64()) {
//                try {
//                    byte[] imageData = Base64.decode(base64);
//                    InputStream inputStream = new ByteArrayInputStream(imageData);
//                    String fileName = System.currentTimeMillis() + ".jpg";
//                    ImageDatabaseUtil.uploadImage(fileName, inputStream);
//                    String url = "https://s3.amazonaws.com/" + ImageDatabaseUtil.getBUCKET_NAME() + "/" + fileName;
//                    imageURLs.add(url);
//                } catch (Base64DecodingException | IOException ex) {
//                    Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
            storedItem.setImageURLs(item.getImagesBase64());
        }
        storedItem.setStatus("INBOUND");
        helper.putItem(storedItem);
        res.setData(storedItem.getStoredItemId());
        return res;
    }

    public ErrorResult checkOutItem(CheckoutItem item) {
        ErrorResult res = new ErrorResult();

        String userRequestID = item.getUserRequestID();

        List<UserRequestTableItem> userRequests = queryHelper.getUserRequestByID(userRequestID);

        if (userRequests == null || userRequests.isEmpty()) {
            res.setErrorCode(303);
            res.setMessage("invalid userrequestID");
            return res;
        }

        UserRequestTableItem userRequest = userRequests.get(0);

        List<UserRequestTableItem.Item> requestItems = userRequest.getItems();
        String storedItemID = "";
        //doubt?
        for (UserRequestTableItem.Item requestItem : requestItems) {
            if (requestItem.getItemCodes().contains(item.getItemCode())) {
                storedItemID = requestItem.getStoredItemID();
            }
        }

        List<InventoryTableItem> storedItems = queryHelper.getStoredItemsByID(storedItemID);

        if (storedItems == null || storedItems.isEmpty()) {
            res.setErrorCode(105);
            res.setMessage("invalid productid");
            return res;
        }

        InventoryTableItem storedItem = storedItems.get(0);
        storedItem.setStatus("stored");
        helper.putItem(storedItem);

        UserTableItem user = getUserProfile(userRequest.getUser().getUserID());

        List<Notification> notifications = user.getNotifications();

        NotificationFactory.addGearArrivedNotification(notifications);

        user.setNotifications(notifications);

        helper.putItem(user);

        res.setErrorCode(200);
        res.setMessage("success");
        res.setData(storedItem.getStoredItemId());

        return res;
    }

    public List<ProductImages> getProductImages(String productId) {
        List<ProductImages> imagesList = new ArrayList<>();

        List<InventoryTableItem> storedItems = queryHelper.getStoredItemsByID(productId);

        if (storedItems == null || storedItems.isEmpty()) {
            return null;
        }

        for (InventoryTableItem storedItem : storedItems) {
            if (storedItem.getImageURLs() != null && !storedItem.getImageURLs().isEmpty()) {
                for (String imageURL : storedItem.getImageURLs()) {
                    ProductImages productImage = new ProductImages();
                    productImage.setProductID(storedItem.getStoredItemId());
                    productImage.setImageURL(imageURL);
                    imagesList.add(productImage);
                }
            }
        }

        return imagesList;
    }

    public List<String> getItemConditionValues() {
        return queryHelper.getItemConditionsConfiguration();
    }

    public boolean saveItem(BathwaterItem item) {
        return helper.putItem(item);
    }

    public List<InventoryTableItem> searchItems(List<String> keywords) {

        List<String> itemsList;
        List<InventoryTableItem> items = new ArrayList<>();
        try {
//            List<String> items = esQueryHelper.searchItems(keywords);
            itemsList = esQueryHelper.searchItems(keywords);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            for (String item : itemsList) {
                InventoryTableItem invItem = mapper.readValue(item, InventoryTableItem.class);
                items.add(invItem);
            }
        } catch (IOException ex) {
            Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return items;
    }

    public List<TodaysCheckins> getTodaysCheckIns() {
        List<UserRequestTableItem> list = scanHelper.getTodaysCheckIns();

        List<TodaysCheckins> todaysCheckins = new ArrayList<>();

        Map<String, Integer> truckItemMap = new HashMap<>();

        for (UserRequestTableItem userReq : list) {
            DriverTableItem.Truck truck = getTodaysTruckForDriver(userReq.getDriver().getDriverID());

            if (truck != null) {
                if (truckItemMap.containsKey(truck.getTruckID())) {
                    truckItemMap.put(truck.getTruckID(), truckItemMap.get(truck.getTruckID()) + userReq.getItems().size());
                } else {
                    truckItemMap.put(truck.getTruckID(), userReq.getItems().size());
                }
            }
        }

        for (String truckID : truckItemMap.keySet()) {
            TodaysCheckins todaysCheckIn = new TodaysCheckins();
            todaysCheckIn.setTruckID(truckID);
            todaysCheckIn.setNoOfItems(truckItemMap.get(truckID) + "");

            todaysCheckins.add(todaysCheckIn);
        }

        return todaysCheckins;
    }

    public TruckCheckin truckCheckIn(String truckID) {
        TruckCheckin truckCheckin = new TruckCheckin();
        truckCheckin.setCheckedIn(0);
        truckCheckin.setToCheckIn(0);
        truckCheckin.setTruckID(truckID);
        TruckTableItem truck = queryHelper.getTruckByID(truckID);

        List<TruckTableItem.Driver> drivers = truck.getAssignedDrivers();

        SimpleDateFormat formatter = new SimpleDateFormat("MM.dd.yyyy");
        String today = formatter.format(new Date());

        TruckTableItem.Driver assignedDriver = null;

        for (TruckTableItem.Driver driver : drivers) {
            if (driver.getDate().equals(today)) {
                assignedDriver = driver;
                break;
            }
        }

        if (assignedDriver != null) {
            String driverID = assignedDriver.getDriverID();
            List<UserRequestTableItem> todaysAllPickups = scanHelper.getTodaysPickups();
            List<DriverTableItem> driver = scanHelper.getDriverById(driverID);
            List<DriverTableItem.UserRequest> driverRequests = driver.get(0).getAssignedRequests();

            for (UserRequestTableItem pickupRequest : todaysAllPickups) {
                for (DriverTableItem.UserRequest driverRequest : driverRequests) {
                    if (pickupRequest.getUserRequestID().equals(driverRequest.getUserRequestID())) {
                        if (pickupRequest.getStatus().equals("completed")) {
                            for (UserRequestTableItem.Item item : pickupRequest.getItems()) {
                                InventoryTableItem storedItem = queryHelper.getStoredItemsByID(item.getStoredItemID()).get(0);
                                if (storedItem.getStatus().equals("checked-in")) {
                                    truckCheckin.setCheckedIn(truckCheckin.getCheckedIn() + 1);
                                } else if (storedItem.getStatus().equals("pickedUp")) {
                                    truckCheckin.setToCheckIn(truckCheckin.getToCheckIn() + 1);
                                }
                            }
                        }

                        break;
                    }
                }
            }
        }

        return truckCheckin;
    }

    public ErrorResult checkInItem(CheckInParameters params) {
        ErrorResult res = new ErrorResult();

        List<InventoryTableItem> inventoryItems = scanHelper.getItemByItemCode(params.getItemCode());

        if (inventoryItems == null || inventoryItems.isEmpty()) {
            res.setErrorCode(101);
            res.setMessage("invalid itemCode");
            return res;
        }

        InventoryTableItem item = inventoryItems.get(0);

        item.setCategoryID(params.getCategoryID());
        item.setCredits(params.getCredits());
        item.setStorageID(params.getLocation());
        item.setStatus("checked-in");

        helper.putItem(item);

        res.setErrorCode(200);
        res.setMessage("success");
        return res;
    }

    public List<String> getAutoCompleteSuggestions(String text) {
        return esQueryHelper.itemSuggest(text);
    }

    public ErrorResult populateUserItems(String email) {
        ErrorResult res = new ErrorResult();

        List<UserTableItem> users = scanHelper.getUsersBasedOnEmail(email);

        if (users == null || users.isEmpty()) {
            res.setErrorCode(102);
            res.setMessage("email not registered");
            return res;
        }

        UserTableItem user = users.get(0);

        List<String> urls = new ArrayList<>();
        urls.add("https://s3.amazonaws.com/bathwater.images.dev/102701a.jpg");
        urls.add("https://s3.amazonaws.com/bathwater.images.dev/102701b.jpg");

        List<UserTableItem.StoredItems> storedItems = user.getStoredItems();

        if (storedItems == null) {
            storedItems = new ArrayList<>();
        }

        InventoryTableItem itemOne = new InventoryTableItem();
        itemOne.setBinNumber("007");
        itemOne.setCategoryID("51");
        itemOne.setCredits(30);
        itemOne.setDescription("Avent Microwave Steam Sterilizer's lightweight, compact design makes it ideal for use in and out of the home. Contents remain sterile for up to 24 hours if lid is unopened.");
        itemOne.setImageURLs(urls);
        List<String> itemCodes = new ArrayList<>();
        itemCodes.add("12701_" + System.currentTimeMillis());
        itemOne.setItemCode(itemCodes);
        itemOne.setItemName("Avent Microwave Steam Sterilizer");
        itemOne.setOwnerEmail(email);
        itemOne.setOwnerFirstName(user.getFirstName());
        itemOne.setOwnerLasName(user.getLastName());
        itemOne.setOwnerID(user.getUserID());
        itemOne.setParentID("5");
        itemOne.setStorageID("strg001");
        itemOne.setStorageTimestamp(new Date().toString());
        itemOne.setStatus("stored");
        helper.putItem(itemOne);

        UserTableItem.StoredItems storedItem = new UserTableItem.StoredItems();
        storedItem.setCategoryID(itemOne.getCategoryID());
        storedItem.setCredits(itemOne.getCredits());
        storedItem.setDescription(itemOne.getDescription());
        storedItem.setItemID(itemOne.getStoredItemId());
        storedItem.setName(itemOne.getItemName());
        UserTableItem.StoredItems.Status status = new UserTableItem.StoredItems.Status();
        status.setBinNumber("007");
        status.setStorageID("strg001");
        status.setStatus("stored");
        storedItem.setStatus(status);
        storedItems.add(storedItem);

        itemOne = new InventoryTableItem();
        itemOne.setBinNumber("007");
        itemOne.setCategoryID("50");
        itemOne.setCredits(25);
        itemOne.setDescription("3-in-1 Electric Steam sterilizer comes in adjustable size and offers sterilization for soothers when configured in small size; breast pumps, toddler plates, knives, and forks when configured in medium size. Can hold up to six bottles in large size.");
        itemOne.setImageURLs(urls);
        itemCodes = new ArrayList<>();
        itemCodes.add("12701_" + System.currentTimeMillis());
        itemOne.setItemCode(itemCodes);
        itemOne.setItemName("Avent 3-in-1 Electric Steam Sterilizer");
        itemOne.setOwnerEmail(email);
        itemOne.setOwnerFirstName(user.getFirstName());
        itemOne.setOwnerLasName(user.getLastName());
        itemOne.setOwnerID(user.getUserID());
        itemOne.setParentID("5");
        itemOne.setStorageID("strg001");
        itemOne.setStorageTimestamp(new Date().toString());
        itemOne.setStatus("stored");
        helper.putItem(itemOne);

        storedItem = new UserTableItem.StoredItems();
        storedItem.setCategoryID(itemOne.getCategoryID());
        storedItem.setCredits(itemOne.getCredits());
        storedItem.setDescription(itemOne.getDescription());
        storedItem.setItemID(itemOne.getStoredItemId());
        storedItem.setName(itemOne.getItemName());
        status = new UserTableItem.StoredItems.Status();
        status.setBinNumber("007");
        status.setStorageID("strg001");
        status.setStatus("stored");
        storedItem.setStatus(status);
        storedItems.add(storedItem);

        user.setStoredItems(storedItems);
        helper.putItem(user);

        res.setErrorCode(200);
        res.setMessage("success");
        return res;
    }

    public ErrorResult populateUserItem(String email) {
        ErrorResult res = new ErrorResult();

        List<UserTableItem> users = scanHelper.getUsersBasedOnEmail(email);

        if (users == null || users.isEmpty()) {
            res.setErrorCode(102);
            res.setMessage("email not registered");
            return res;
        }

        UserTableItem user = users.get(0);

        List<String> urls = new ArrayList<>();
        urls.add("https://s3.amazonaws.com/bathwater.images.dev/102701a.jpg");
        urls.add("https://s3.amazonaws.com/bathwater.images.dev/102701b.jpg");

        List<UserTableItem.StoredItems> storedItems = user.getStoredItems();

        if (storedItems == null) {
            storedItems = new ArrayList<>();
        }

        InventoryTableItem itemOne = new InventoryTableItem();
        itemOne.setBinNumber("007");
        itemOne.setCategoryID("51");
        itemOne.setCredits(30);
        itemOne.setDescription("Avent Microwave Steam Sterilizer's lightweight, compact design makes it ideal for use in and out of the home. Contents remain sterile for up to 24 hours if lid is unopened.");
        itemOne.setImageURLs(urls);
        List<String> itemCodes = new ArrayList<>();
        itemCodes.add("12701_" + System.currentTimeMillis());
        itemOne.setItemCode(itemCodes);
        itemOne.setItemName("Avent Microwave Steam Sterilizer");
        itemOne.setOwnerEmail(email);
        itemOne.setOwnerFirstName(user.getFirstName());
        itemOne.setOwnerLasName(user.getLastName());
        itemOne.setOwnerID(user.getUserID());
        itemOne.setParentID("5");
        itemOne.setStorageID("strg001");
        itemOne.setStorageTimestamp(new Date().toString());
        itemOne.setStatus("stored");
        helper.putItem(itemOne);

        UserTableItem.StoredItems storedItem = new UserTableItem.StoredItems();
        storedItem.setCategoryID(itemOne.getCategoryID());
        storedItem.setCredits(itemOne.getCredits());
        storedItem.setDescription(itemOne.getDescription());
        storedItem.setItemID(itemOne.getStoredItemId());
        storedItem.setName(itemOne.getItemName());
        UserTableItem.StoredItems.Status status = new UserTableItem.StoredItems.Status();
        status.setBinNumber("007");
        status.setStorageID("strg001");
        status.setStatus("stored");
        storedItem.setStatus(status);
        storedItems.add(storedItem);

        user.setStoredItems(storedItems);
        helper.putItem(user);

        res.setErrorCode(200);
        res.setMessage("success");
        res.setData(itemOne.getStoredItemId());
        return res;
    }

    private Notification getTodaysNotification(List<Notification> notifications) {
        String today = new SimpleDateFormat("MM.dd.yyyy").format(new Date());

        if (notifications != null) {
            for (Notification notification : notifications) {
                if (today.equals(notification.getDate())) {
                    return notification;
                }
            }
        }

        return null;
    }

    public ErrorResult getItemByID(String itemID) throws JsonProcessingException {
        List<InventoryTableItem> items = queryHelper.getStoredItemsByID(itemID);
        ErrorResult res = new ErrorResult();
        if (items == null || items.isEmpty()) {
            res.setErrorCode(105);
            res.setMessage("invalid productid");
        } else {
            ObjectMapper mapper = new ObjectMapper();
            String data = mapper.writeValueAsString(items.get(0));
            res.setErrorCode(200);
            res.setMessage("success");
            res.setData(data);
        }

        return res;
    }

    public int getPromoCodeDiscout(String promoCode) throws IOException {
        InputStream in = PromoCodeDatabaseUtil.downloadFile(promoCode);

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        String line;
        String discount = "";

        while ((line = reader.readLine()) != null) {
            discount += line;
        }

        return Integer.parseInt(discount);
    }

    private UserTableItem removeStoredItemsFromUser(UserTableItem user, List<UserRequestTableItem.Item> dropOffItems) {
        List<UserTableItem.StoredItems> storedItems = user.getStoredItems();

        for (UserRequestTableItem.Item item : dropOffItems) {
            List<InventoryTableItem> inventoryItem = scanHelper.getItemByItemCode(item.getItemCodes().get(0));
            UserTableItem.StoredItems storedItem = getStoredItemByID(storedItems, inventoryItem.get(0).getStoredItemId());

            if (storedItem != null) {
                storedItem.getStatus().setStatus("dropped off");
            }
        }

        return user;
    }

    private UserTableItem.StoredItems getStoredItemByID(List<UserTableItem.StoredItems> storedItems, String storedItemID) {
        for (UserTableItem.StoredItems item : storedItems) {
            if (item.getItemID() != null && item.getItemID().equals(storedItemID)) {
                return item;
            }
        }
        return null;
    }

    private boolean isItemLocked(InventoryTableItem.Lock lock, String userID) throws ParseException {
        if (lock != null) {
            Date now = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat();
            Date lockTimeStamp = formatter.parse(lock.getTimeStamp());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(lockTimeStamp);
            calendar.add(Calendar.MINUTE, 60);
            if (now.before(calendar.getTime())) {
                return !lock.getUserID().equals(userID);
            }
        }

        return false;
    }

    public UserTableItem getUserInventory(String userId) {
        List<UserTableItem> userList = scanHelper.getUsersBasedById(userId);
        if (userList == null || userList.isEmpty()) {
            return null;
        }

        UserTableItem user = userList.get(0);

        if (user.getReferralCode() == null) {
            user.setReferralCode(generateReferralCode(user.getFirstName()));
            helper.putItem(user);
        }
        return user;
    }

    public List<UserRequestTableItem> getUserRequests(String userId) {
        List<UserTableItem> userList = scanHelper.getUsersBasedById(userId);
        if (userList == null || userList.isEmpty()) {
            return null;
        }
        List<UserRequestTableItem> userReq = new ArrayList<UserRequestTableItem>();
        UserTableItem user = userList.get(0);

        if (user.getReferralCode() == null) {
            user.setReferralCode(generateReferralCode(user.getFirstName()));
            helper.putItem(user);
        }

        if (user.getPendingRequests() != null && user.getPendingRequests().size() > 0) {

            for (int i = 0; i < user.getPendingRequests().size(); i++) {
                Requests req = user.getPendingRequests().get(i);
                List<UserRequestTableItem> reqObj = scanHelper.getUserRequestsById(req.getRequestID());
                if (reqObj != null && reqObj.size() > 0) {
                    userReq.add(reqObj.get(0));
                }
            }
        }
        return userReq;
    }

    public ErrorResult getItemsByStatus(String status) throws JsonProcessingException {
        List<InventoryTableItem> items = scanHelper.getStoredItemsByStatus(status);
        ErrorResult res = new ErrorResult();
        if (items == null || items.isEmpty()) {
            res.setErrorCode(105);
            res.setMessage("Items not found");
        } else {
            ObjectMapper mapper = new ObjectMapper();
            String data = mapper.writeValueAsString(items);
            res.setErrorCode(200);
            res.setMessage("success");
            res.setData(data);
        }
        return res;
    }

    public ErrorResult updateStatus(String sid, String status, String storageId) {
        ErrorResult res = new ErrorResult();
        res.setErrorCode(500);
        res.setMessage("fail");
        List<InventoryTableItem> items = queryHelper.getStoredItemsByID(sid);
        if (items != null && items.size() > 0) {
            InventoryTableItem item = items.get(0);
            item.setBinNumber(item.getBinNumber());
            item.setCategoryID(item.getCategoryID());
            item.setDescription(item.getDescription());
            item.setImageName(item.getImageName());
            item.setImageURLs(item.getImageURLs());
            item.setItemCode(item.getItemCode());
            item.setLock(item.getLock());
            item.setObservations(item.getObservations());
            item.setOwnerEmail(item.getOwnerEmail());
            item.setOwnerFirstName(item.getOwnerFirstName());
            item.setOwnerID(item.getOwnerID());
            item.setOwnerLasName(item.getOwnerLasName());
            item.setParentID(item.getParentID());
            item.setSwap(item.getSwap());
            item.setStatus(item.getStatus());
            item.setStorageTimestamp(item.getStorageTimestamp());
            item.setStoredItemId(item.getStoredItemId());
            item.setSubItems(item.getSubItems());
            item.setCredits(item.getCredits());
            item.setStatus(status);
            if (storageId != null && !storageId.isEmpty()) {
                item.setLocation(storageId);
            }
            item.setStorageID(item.getStorageID());

            helper.putItem(item);
            res.setErrorCode(200);
            res.setMessage("Success");
        } else {
            res.setErrorCode(404);
            res.setMessage("invalid stored item ID");
        }
        return res;
    }

    public ErrorResult updateDropItemStatus(String sid, String status, String itemCode, String storageId) {
        ErrorResult res = new ErrorResult();
        res.setErrorCode(500);
        res.setMessage("fail");
        List<InventoryTableItem> items = queryHelper.getStoredItemsByID(sid);
        if (items != null && items.size() > 0) {
            InventoryTableItem item = items.get(0);
            item.setBinNumber(item.getBinNumber());
            item.setCategoryID(item.getCategoryID());
            item.setDescription(item.getDescription());
            item.setImageName(item.getImageName());
            item.setImageURLs(item.getImageURLs());
            item.setItemCode(item.getItemCode());
            item.setLock(item.getLock());
            item.setObservations(item.getObservations());
            item.setOwnerEmail(item.getOwnerEmail());
            item.setOwnerFirstName(item.getOwnerFirstName());
            item.setOwnerID(item.getOwnerID());
            item.setOwnerLasName(item.getOwnerLasName());
            item.setParentID(item.getParentID());
            item.setSwap(item.getSwap());
            item.setStatus(item.getStatus());
            item.setStorageTimestamp(item.getStorageTimestamp());
            item.setStoredItemId(item.getStoredItemId());
            //item.setSubItems(item.getSubItems());
            if (itemCode != null) {
                List<SubItem> updateItems = new ArrayList<SubItem>();
                for (int i = 0; i < item.getSubItems().size(); i++) {
                    SubItem obj = item.getSubItems().get(i);
                    if (obj.getItemCode().equalsIgnoreCase(itemCode)) {
                        obj.setStatus(status);
                    }
                    updateItems.add(obj);
                }
                item.setSubItems(updateItems);
            } else {
                item.setSubItems(item.getSubItems());
            }
            item.setCredits(item.getCredits());
            item.setStatus(status);
            if (storageId != null && !storageId.isEmpty()) {
                item.setLocation(storageId);
            }
            item.setStorageID(item.getStorageID());

            helper.putItem(item);
            res.setErrorCode(200);
            res.setMessage("Success");
        } else {
            res.setErrorCode(404);
            res.setMessage("invalid stored item ID");
        }
        return res;
    }

    public ErrorResult getTrucksByUserrequests(String dateStr) throws JsonProcessingException {
        ErrorResult res = new ErrorResult();
        res.setErrorCode(500);
        res.setMessage("fail");
        List<UserRequestTableItem> userRequests = scanHelper.getUserRequestsByDate("completed", "pickup", dateStr);
        for (int i = 0; i < userRequests.size(); i++) {
            UserRequestTableItem obj1 = userRequests.get(i);

            List<Item> itsUpt = new ArrayList<Item>();
            List<Item> its = obj1.getItems();
            for (Item iObj : its) {
                String sid = iObj.getStoredItemID();
                List<InventoryTableItem> invItems = queryHelper.getStoredItemsByID(sid);
                if (invItems != null && invItems.size() > 0) {
                    InventoryTableItem initem = invItems.get(0);
                    String status = initem.getStatus();
                    if (status.equalsIgnoreCase("pickedUp")) {
                        itsUpt.add(iObj);
                    }
                }
            }
            UserRequestTableItem obj = obj1;
            //Items set
            obj.setItems(itsUpt);
            // data that have status -- inbound & pickup
            List<Item> items = obj.getItems();
            int total = 0;
            if (items != null && items.size() > 0) {
                for (int j = 0; j < items.size(); j++) {
                    Item itemObj = items.get(j);
                    if (itemObj != null && itemObj.getSubItems() != null) {
                        total += itemObj.getSubItems().size() + 1;
                    } else {
                        total += 1;
                    }
                }
            }
            List<Bins> bins = obj.getBins();
            //How many items per user
            obj.setTotalItemsCount(total);
            // Now get the DriverIds and then Assigned Trucks.
            String date = obj.getDate();
            String driverId = obj.getDriver().getDriverID();
            if (driverId != null) {

                List<DriverTableItem> driver = queryHelper.getDriverByID(driverId);
                if (driver != null && driver.size() > 0) {
                    DriverTableItem driverObj = driver.get(0);
                    List<Truck> trucks = driverObj.getAssignedTrucks();
                    if (trucks != null && trucks.size() > 0) {
                        for (int k = 0; k < trucks.size(); k++) {
                            Truck tObj = trucks.get(k);
                            if (tObj.getDate().equalsIgnoreCase(date)) {
                                obj.setTruckId(tObj.getTruckID());
                                obj.setLicensePlate(tObj.getLicensePlate());
                                // this code will pick only driver with r to its 1st truck is will get
                                //which is 
                                break;
                            }
                        }
                    }
                }
            }

        }
        //now userTable.item will have only those data with status : pickedUp, completed, pickup

        // we got the list based on userrequestsids - now will bind all items by trucks.
        if (userRequests != null && userRequests.size() > 0) {
            Map<String, TruckItems> driversList = new HashMap<String, TruckItems>();
            for (UserRequestTableItem uObj : userRequests) {

                String dID = uObj.getDriver().getDriverID();
                TruckItems item = driversList.get(dID);
                if (item != null) {
                    //if already exists - add items 
                    List<UserRequestTableItem> items = item.getItems();

                    items.add(uObj);
                    int total = item.getTotalItemsCount() + uObj.getTotalItemsCount();
                    item.setTotalItemsCount(total);
                    item.setItems(items);

                } else {
                    TruckItems tItem = new TruckItems();

                    List<UserRequestTableItem> items = new ArrayList<UserRequestTableItem>();

                    items.add(uObj);

                    tItem.setDriver(uObj.getDriver());

                    tItem.setItems(items);

                    tItem.setLicensePlate(uObj.getLicensePlate());
                    tItem.setTruckId(uObj.getTruckId());
                    tItem.setTotalItemsCount(uObj.getTotalItemsCount());

                    driversList.put(dID, tItem);
                }
            }

            // Map array of TruckItems.
            List<TruckItems> truckItems = new ArrayList<TruckItems>();
            for (Map.Entry<String, TruckItems> entry : driversList.entrySet()) {

                TruckItems items = entry.getValue();
                List<UserRequestTableItem> uritems = items.getItems();
                // now cleanup unnecessary data
                List<UserRequestTableItem> itemsUptd = new ArrayList<UserRequestTableItem>();
                for (UserRequestTableItem obj : uritems) {

                    obj.setDriver(null);
                    obj.setTotalItemsCount(null);
                    obj.setUser(null);
                    obj.setLicensePlate(null);
                    obj.setTruckId(null);
                    //obj.setItems(itsUpt);

                    itemsUptd.add(obj);
                }
                items.setItems(itemsUptd);
                truckItems.add(items);
            }

            ObjectMapper mapper = new ObjectMapper();
            String data = mapper.writeValueAsString(truckItems);
            res.setData(data);

        }

        res.setErrorCode(200);
        res.setMessage("success");

        return res;
    }

    public ErrorResult getTrucksByUserrequestsDropoff(String dateStr) throws JsonProcessingException {
        ErrorResult res = new ErrorResult();
        res.setErrorCode(500);
        res.setMessage("fail");
        List<UserRequestTableItem> userRequests = scanHelper.getDropoffUserRequestsByDate("in progress", "drop off", dateStr);
        for (int i = 0; i < userRequests.size(); i++) {
            UserRequestTableItem obj = userRequests.get(i);
            List<Item> items = obj.getItems();
            int total = 0;
            if (items != null && items.size() > 0) {
                for (int j = 0; j < items.size(); j++) {
                    Item itemObj = items.get(j);
                    if (itemObj != null && itemObj.getSubItems() != null) {
                        total += itemObj.getSubItems().size() + 1;
                    } else {
                        total += 1;
                    }
                }
            }
            List<Bins> bins = obj.getBins();
            obj.setTotalItemsCount(total);
            // Now get the DriverIds and then Assigned Trucks.
            String date = obj.getDate();
            String driverId = obj.getDriver().getDriverID();
            if (driverId != null) {

                List<DriverTableItem> driver = queryHelper.getDriverByID(driverId);
                if (driver != null && driver.size() > 0) {
                    DriverTableItem driverObj = driver.get(0);
                    List<Truck> trucks = driverObj.getAssignedTrucks();
                    if (trucks != null && trucks.size() > 0) {
                        for (int k = 0; k < trucks.size(); k++) {
                            Truck tObj = trucks.get(k);
                            if (tObj.getDate().equalsIgnoreCase(date)) {
                                obj.setTruckId(tObj.getTruckID());
                                obj.setLicensePlate(tObj.getLicensePlate());
                                break;
                            }
                        }
                    }
                }
            }

        }

        // we got the list based on userrequestsids - now will bind all items by trucks.
        if (userRequests != null && userRequests.size() > 0) {
            Map<String, TruckItems> driversList = new HashMap<String, TruckItems>();
            for (UserRequestTableItem uObj : userRequests) {
                String dID = uObj.getDriver().getDriverID();
                TruckItems item = driversList.get(dID);
                if (item != null) {
                    //if already exists - add items 
                    List<UserRequestTableItem> items = item.getItems();
                    items.add(uObj);
                    int total = item.getTotalItemsCount() + uObj.getTotalItemsCount();
                    item.setTotalItemsCount(total);
                    item.setItems(items);

                } else {
                    TruckItems tItem = new TruckItems();

                    List<UserRequestTableItem> items = new ArrayList<UserRequestTableItem>();
                    items.add(uObj);

                    tItem.setDriver(uObj.getDriver());
                    tItem.setItems(items);
                    tItem.setLicensePlate(uObj.getLicensePlate());
                    tItem.setTruckId(uObj.getTruckId());
                    tItem.setTotalItemsCount(uObj.getTotalItemsCount());

                    driversList.put(dID, tItem);
                }
            }

            // Map array of TruckItems.
            List<TruckItems> truckItems = new ArrayList<TruckItems>();
            for (Map.Entry<String, TruckItems> entry : driversList.entrySet()) {
                truckItems.add(entry.getValue());
            }

            ObjectMapper mapper = new ObjectMapper();
            String data = mapper.writeValueAsString(truckItems);
            res.setData(data);

        }

        res.setErrorCode(200);
        res.setMessage("success");
        return res;
    }

}
