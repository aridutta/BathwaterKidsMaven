/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bathwater;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Base64;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;

/**
 *
 * @author rajeshk
 */
public class TestBathwaterRest {
    
    public static final String BASE_URI = "https://staging.bathwaterkids.com/rest";
    
    @Test
    public void testLogin() throws ParseException {
        String emailAddress = "rajesh.k.nitk@gmail.com";
        String password = Base64.getEncoder().encodeToString("s@mpl3".getBytes());
        String userType = "normal";
        
        String body = "{"
                + "\"emailAddress\":\"" + emailAddress + "\","
                + "\"password\":\"" + password + "\","
                + "\"userType\":\"" + userType + "\""
                + "}";
        
        Client client = ClientBuilder.newClient();
        Response response = client.target(BASE_URI).path("/login").request().post(Entity.entity(body, MediaType.APPLICATION_JSON), Response.class);
        
        String res = response.readEntity(String.class);
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(res);
        
        
        assertEquals("success", json.get("message"));
        assertNotNull((String) json.get("userid"));
        assertNotNull((String) json.get("key"));
    }
    
    @Test
    public void testLoginIncorrectpassword() throws ParseException {
        String emailAddress = "rajesh.k.nitk@gmail.com";
        String password = Base64.getEncoder().encodeToString("wrong password".getBytes());
        String userType = "normal";
        
        String body = "{"
                + "\"emailAddress\":\"" + emailAddress + "\","
                + "\"password\":\"" + password + "\","
                + "\"userType\":\"" + userType + "\""
                + "}";
        
        Client client = ClientBuilder.newClient();
        Response response = client.target(BASE_URI).path("/login").request().post(Entity.entity(body, MediaType.APPLICATION_JSON), Response.class);
        
        String res = response.readEntity(String.class);
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(res);
        
        
        assertEquals("email and password did not match", json.get("message"));
        assertNull((String) json.get("userid"));
        assertNull((String) json.get("key"));
    }
    
    @Test
    public void testLoginInvalidEmail() throws ParseException {
        String emailAddress = "rajesh@test.com";
        String password = Base64.getEncoder().encodeToString("s@mpl3".getBytes());
        String userType = "normal";
        
        String body = "{"
                + "\"emailAddress\":\"" + emailAddress + "\","
                + "\"password\":\"" + password + "\","
                + "\"userType\":\"" + userType + "\""
                + "}";
        
        Client client = ClientBuilder.newClient();
        Response response = client.target(BASE_URI).path("/login").request().post(Entity.entity(body, MediaType.APPLICATION_JSON), Response.class);
        
        String res = response.readEntity(String.class);
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(res);
        
        
        assertEquals("email not registered", json.get("message"));
        assertNull((String) json.get("userid"));
        assertNull((String) json.get("key"));
    }
    
    @Test
    public void testLoginFBUserWithoutMembership() throws ParseException {
        String emailAddress = "rajkumar@bathwaterkids.com";
        String userType = "fbUser";
        
        String body = "{"
                + "\"emailAddress\":\"" + emailAddress + "\","
                + "\"userType\":\"" + userType + "\""
                + "}";
        
        Client client = ClientBuilder.newClient();
        Response response = client.target(BASE_URI).path("/login").request().post(Entity.entity(body, MediaType.APPLICATION_JSON), Response.class);
        
        String res = response.readEntity(String.class);
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(res);
        
        
        assertEquals("success without membership", json.get("message"));
    }
    
    @Test
    public void testGetUserProfile() throws ParseException {
        String emailAddress = "rajesh.k.nitk@gmail.com";
        String password = Base64.getEncoder().encodeToString("s@mpl3".getBytes());
        String userType = "normal";
        
        String body = "{"
                + "\"emailAddress\":\"" + emailAddress + "\","
                + "\"password\":\"" + password + "\","
                + "\"userType\":\"" + userType + "\""
                + "}";
        
        Client client = ClientBuilder.newClient();
        Response response = client.target(BASE_URI).path("/login").request().post(Entity.entity(body, MediaType.APPLICATION_JSON), Response.class);
        
        String res = response.readEntity(String.class);
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(res);
        
        String key = (String) json.get("key");
        String userID = (String) json.get("userid");
        
        assertNotNull(userID);
        assertNotNull(key);
        
        Response profileResponse = client.target(BASE_URI).path("/getUserProfile").queryParam("userid", userID).request().header("Authorization", key).get();
        
        assertEquals(200, profileResponse.getStatus());
        String profile = profileResponse.readEntity(String.class);
        assertNotNull(profile);
        System.out.println("Profile: " + profile);
    }
    
    @Test
    public void testGetUserProfileInvalidUserId() throws ParseException {
        String emailAddress = "rajesh.k.nitk@gmail.com";
        String password = Base64.getEncoder().encodeToString("s@mpl3".getBytes());
        String userType = "normal";
        
        String body = "{"
                + "\"emailAddress\":\"" + emailAddress + "\","
                + "\"password\":\"" + password + "\","
                + "\"userType\":\"" + userType + "\""
                + "}";
        
        Client client = ClientBuilder.newClient();
        Response response = client.target(BASE_URI).path("/login").request().post(Entity.entity(body, MediaType.APPLICATION_JSON), Response.class);
        
        String res = response.readEntity(String.class);
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(res);
        
        String key = (String) json.get("key");
        String userID = (String) json.get("userid");
        
        assertNotNull(userID);
        assertNotNull(key);
        
        Response profileResponse = client.target(BASE_URI).path("/getUserProfile").queryParam("userid", userID + "xyz").request().header("Authorization", key).get();
        
        String profile = profileResponse.readEntity(String.class);
        json = (JSONObject) parser.parse(profile);
        assertEquals("invalid userid", (String)json.get("message"));
    }
    
    @Test
    public void testGetUserProfileUnauthorized() {
        Client client = ClientBuilder.newClient();
        
        Response profileResponse = client.target(BASE_URI).path("/getUserProfile").queryParam("userid", "55ed1677-6582-45ec-89ee-737045f6981f").request().get();
        
        assertEquals(401, profileResponse.getStatus());
    }
    
    @Test
    public void testGetContactDetails() throws ParseException {
        String emailAddress = "rajesh.k.nitk@gmail.com";
        String password = Base64.getEncoder().encodeToString("s@mpl3".getBytes());
        String userType = "normal";
        
        String body = "{"
                + "\"emailAddress\":\"" + emailAddress + "\","
                + "\"password\":\"" + password + "\","
                + "\"userType\":\"" + userType + "\""
                + "}";
        
        Client client = ClientBuilder.newClient();
        Response response = client.target(BASE_URI).path("/login").request().post(Entity.entity(body, MediaType.APPLICATION_JSON), Response.class);
        
        String res = response.readEntity(String.class);
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(res);
        
        String key = (String) json.get("key");
        String userID = (String) json.get("userid");
        
        assertNotNull(userID);
        assertNotNull(key);
        
        Response contactResponse = client.target(BASE_URI).path("/getContactDetails").queryParam("userId", userID).request().header("Authorization", key).get();
        
        assertEquals(200, contactResponse.getStatus());
        String contactDetails = contactResponse.readEntity(String.class);
        assertNotNull(contactDetails);
        System.out.println("Contact Details: " + contactDetails);
    }
    
    @Test
    public void testGetContactDetailsUnauthorized() {
        Client client = ClientBuilder.newClient();
        
        Response profileResponse = client.target(BASE_URI).path("/getContactDetails").queryParam("userId", "55ed1677-6582-45ec-89ee-737045f6981f").request().get();
        
        assertEquals(401, profileResponse.getStatus());
    }
    
    @Test
    public void testCheckZipCode() throws ParseException {
        String emailAddress = "rajesh.k.nitk@gmail.com";
        String password = Base64.getEncoder().encodeToString("s@mpl3".getBytes());
        String userType = "normal";
        
        String body = "{"
                + "\"emailAddress\":\"" + emailAddress + "\","
                + "\"password\":\"" + password + "\","
                + "\"userType\":\"" + userType + "\""
                + "}";
        
        Client client = ClientBuilder.newClient();
        Response response = client.target(BASE_URI).path("/login").request().post(Entity.entity(body, MediaType.APPLICATION_JSON), Response.class);
        
        String res = response.readEntity(String.class);
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(res);
        
        String key = (String) json.get("key");
        String userID = (String) json.get("userid");
        
        assertNotNull(userID);
        assertNotNull(key);
        
        Response zipCodeResponse = client.target(BASE_URI).path("/checkZipCode").queryParam("zipcode", "46202").queryParam("uid", userID).request().header("Authorization", key).get();
        
        assertEquals(200, zipCodeResponse.getStatus());
        String zipCode = zipCodeResponse.readEntity(String.class);
        assertNotNull(zipCode);
        System.out.println("Zipcode: " + zipCode);
    }
    
    @Test
    public void testCheckZipCodeNegative() throws ParseException {
        String emailAddress = "rajesh.k.nitk@gmail.com";
        String password = Base64.getEncoder().encodeToString("s@mpl3".getBytes());
        String userType = "normal";
        
        String body = "{"
                + "\"emailAddress\":\"" + emailAddress + "\","
                + "\"password\":\"" + password + "\","
                + "\"userType\":\"" + userType + "\""
                + "}";
        
        Client client = ClientBuilder.newClient();
        Response response = client.target(BASE_URI).path("/login").request().post(Entity.entity(body, MediaType.APPLICATION_JSON), Response.class);
        
        String res = response.readEntity(String.class);
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(res);
        
        String key = (String) json.get("key");
        String userID = (String) json.get("userid");
        
        assertNotNull(userID);
        assertNotNull(key);
        
        Response zipCodeResponse = client.target(BASE_URI).path("/checkZipCode").queryParam("zipcode", "abcde").queryParam("uid", userID).request().header("Authorization", key).get();
        
        assertEquals(200, zipCodeResponse.getStatus());
        String zipCode = zipCodeResponse.readEntity(String.class);
        JSONObject zip = (JSONObject) parser.parse(zipCode);
        assertEquals("zipCode currently not under service", zip.get("message"));
    }
    
    @Test
    public void testCheckZipCodeUnauthorized() {
        Client client = ClientBuilder.newClient();
        Response profileResponse = client.target(BASE_URI).path("/checkZipCode").queryParam("zipcode", "46202").queryParam("uid", "55ed1677-6582-45ec-89ee-737045f6981f").request().get();
        assertEquals(401, profileResponse.getStatus());
    }
    
    @Test
    public void testGetPlans() throws ParseException {
        String emailAddress = "rajesh.k.nitk@gmail.com";
        String password = Base64.getEncoder().encodeToString("s@mpl3".getBytes());
        String userType = "normal";
        
        String body = "{"
                + "\"emailAddress\":\"" + emailAddress + "\","
                + "\"password\":\"" + password + "\","
                + "\"userType\":\"" + userType + "\""
                + "}";
        
        Client client = ClientBuilder.newClient();
        Response response = client.target(BASE_URI).path("/login").request().post(Entity.entity(body, MediaType.APPLICATION_JSON), Response.class);
        
        String res = response.readEntity(String.class);
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(res);
        
        String key = (String) json.get("key");
        String userID = (String) json.get("userid");
        
        assertNotNull(userID);
        assertNotNull(key);
        
        Response plansResponse = client.target(BASE_URI).path("/getPlans").request().header("Authorization", key).get();
        
        assertEquals(200, plansResponse.getStatus());
        String plans = plansResponse.readEntity(String.class);
        assertNotNull(plans);
        System.out.println("Plans: " + plans);
    }
    
    @Test
    public void testGetPlansUnauthorized() {
        Client client = ClientBuilder.newClient();
        Response profileResponse = client.target(BASE_URI).path("/getPlans").request().get();
        assertEquals(401, profileResponse.getStatus());
    }
    
    @Test
    public void testGetMembership() throws ParseException {
        String emailAddress = "rajesh.k.nitk@gmail.com";
        String password = Base64.getEncoder().encodeToString("s@mpl3".getBytes());
        String userType = "normal";
        
        String body = "{"
                + "\"emailAddress\":\"" + emailAddress + "\","
                + "\"password\":\"" + password + "\","
                + "\"userType\":\"" + userType + "\""
                + "}";
        
        Client client = ClientBuilder.newClient();
        Response response = client.target(BASE_URI).path("/login").request().post(Entity.entity(body, MediaType.APPLICATION_JSON), Response.class);
        
        String res = response.readEntity(String.class);
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(res);
        
        String key = (String) json.get("key");
        String userID = (String) json.get("userid");
        
        assertNotNull(userID);
        assertNotNull(key);
        
        Response membershipResponse = client.target(BASE_URI).path("/getMembership").queryParam("userID", userID).request().header("Authorization", key).get();
        
        assertEquals(200, membershipResponse.getStatus());
        String membership = membershipResponse.readEntity(String.class);
        assertNotNull(membership);
        System.out.println("Membership: " + membership);
    }
    
    @Test
    public void testGetMembershipInvalidUserID() throws ParseException {
        String emailAddress = "rajesh.k.nitk@gmail.com";
        String password = Base64.getEncoder().encodeToString("s@mpl3".getBytes());
        String userType = "normal";
        
        String body = "{"
                + "\"emailAddress\":\"" + emailAddress + "\","
                + "\"password\":\"" + password + "\","
                + "\"userType\":\"" + userType + "\""
                + "}";
        
        Client client = ClientBuilder.newClient();
        Response response = client.target(BASE_URI).path("/login").request().post(Entity.entity(body, MediaType.APPLICATION_JSON), Response.class);
        
        String res = response.readEntity(String.class);
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(res);
        
        String key = (String) json.get("key");
        String userID = (String) json.get("userid");
        
        assertNotNull(userID);
        assertNotNull(key);
        
        Response membershipResponse = client.target(BASE_URI).path("/getMembership").queryParam("userID", userID + "xyz").request().header("Authorization", key).get();
        
        assertEquals(200, membershipResponse.getStatus());
        String membership = membershipResponse.readEntity(String.class);
        assertNotNull(membership);
        JSONObject jsonMembership = (JSONObject) parser.parse(membership);
        assertEquals("invalid userid", jsonMembership.get("message"));
    }
    
    @Test
    public void testGetMembershipUnauthorized() {
        Client client = ClientBuilder.newClient();
        Response profileResponse = client.target(BASE_URI).path("/getMembership").queryParam("userId", "55ed1677-6582-45ec-89ee-737045f6981f").request().get();
        assertEquals(401, profileResponse.getStatus());
    }
    
    @Test
    public void testGetCards() throws ParseException {
        String emailAddress = "rajesh.k.nitk@gmail.com";
        String password = Base64.getEncoder().encodeToString("s@mpl3".getBytes());
        String userType = "normal";
        
        String body = "{"
                + "\"emailAddress\":\"" + emailAddress + "\","
                + "\"password\":\"" + password + "\","
                + "\"userType\":\"" + userType + "\""
                + "}";
        
        Client client = ClientBuilder.newClient();
        Response response = client.target(BASE_URI).path("/login").request().post(Entity.entity(body, MediaType.APPLICATION_JSON), Response.class);
        
        String res = response.readEntity(String.class);
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(res);
        
        String key = (String) json.get("key");
        String userID = (String) json.get("userid");
        
        assertNotNull(userID);
        assertNotNull(key);
        
        Response cardsResponse = client.target(BASE_URI).path("/getCards").queryParam("userID", userID).request().header("Authorization", key).get();
        
        assertEquals(200, cardsResponse.getStatus());
        String cards = cardsResponse.readEntity(String.class);
        assertNotNull(cards);
        System.out.println("Cards: " + cards);
    }
    
    @Test
    public void testGetCardsUnauthorized() {
        Client client = ClientBuilder.newClient();
        Response profileResponse = client.target(BASE_URI).path("/getCards").queryParam("userID", "55ed1677-6582-45ec-89ee-737045f6981f").request().get();
        assertEquals(401, profileResponse.getStatus());
    }
    
    @Test
    public void testGetTimeslots() throws ParseException {
        String emailAddress = "rajesh.k.nitk@gmail.com";
        String password = Base64.getEncoder().encodeToString("s@mpl3".getBytes());
        String userType = "normal";
        
        String body = "{"
                + "\"emailAddress\":\"" + emailAddress + "\","
                + "\"password\":\"" + password + "\","
                + "\"userType\":\"" + userType + "\""
                + "}";
        
        Client client = ClientBuilder.newClient();
        Response response = client.target(BASE_URI).path("/login").request().post(Entity.entity(body, MediaType.APPLICATION_JSON), Response.class);
        
        String res = response.readEntity(String.class);
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(res);
        
        String key = (String) json.get("key");
        String userID = (String) json.get("userid");
        
        assertNotNull(userID);
        assertNotNull(key);
        
        Response timeslotsResponse = client.target(BASE_URI).path("/getTimeslots").request().header("Authorization", key).get();
        
        assertEquals(200, timeslotsResponse.getStatus());
        String timeslots = timeslotsResponse.readEntity(String.class);
        assertNotNull(timeslots);
        System.out.println("Timeslots: " + timeslots);
    }
    
    @Test
    public void testGetTimeslotsUnauthorized() {
        Client client = ClientBuilder.newClient();
        Response profileResponse = client.target(BASE_URI).path("/getTimeslots").request().get();
        assertEquals(401, profileResponse.getStatus());
    }
    
    @Test
    public void testShippingCharge() throws Exception {
        String emailAddress = "rajesh.k.nitk@gmail.com";
        String password = Base64.getEncoder().encodeToString("s@mpl3".getBytes());
        String userType = "normal";

        String body = "{"
                + "\"emailAddress\":\"" + emailAddress + "\","
                + "\"password\":\"" + password + "\","
                + "\"userType\":\"" + userType + "\""
                + "}";

        Client client = ClientBuilder.newClient();
        Response response = client.target(BASE_URI).path("/login").request().post(Entity.entity(body, MediaType.APPLICATION_JSON), Response.class);

        String res = response.readEntity(String.class);
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(res);

        String key = (String) json.get("key");
        String userID = (String) json.get("userid");

        assertNotNull(userID);
        assertNotNull(key);
        
        body = "{"
                + "\"userID\": \"" + userID + "\""
                + "}";
        Response shippingChargeResponse = client.target(BASE_URI).path("/shippingCharge").request().header("Authorization", key).post(Entity.entity(body, MediaType.APPLICATION_JSON), Response.class);
        assertEquals(200, shippingChargeResponse.getStatus());
        String shippinCharge = shippingChargeResponse.readEntity(String.class);
        JSONObject obj = (JSONObject) parser.parse(shippinCharge);
        Long charge = (Long) obj.get("shippingCharge");
        assertEquals(15, charge.intValue());
    }
    
    @Test
    public void testShippingChargeUnauthorized() {
        Client client = ClientBuilder.newClient();
        String body = "{"
                + "\"userID\": \"55ed1677-6582-45ec-89ee-737045f6981f\""
                + "}";
        Response profileResponse = client.target(BASE_URI).path("/shippingCharge").request().post(Entity.entity(body, MediaType.APPLICATION_JSON), Response.class);
        assertEquals(401, profileResponse.getStatus());
    }
    
    @Test
    public void testRequestPickup() throws ParseException {
        String emailAddress = "rajesh.k.nitk@gmail.com";
        String password = Base64.getEncoder().encodeToString("s@mpl3".getBytes());
        String userType = "normal";

        String body = "{"
                + "\"emailAddress\":\"" + emailAddress + "\","
                + "\"password\":\"" + password + "\","
                + "\"userType\":\"" + userType + "\""
                + "}";

        Client client = ClientBuilder.newClient();
        Response response = client.target(BASE_URI).path("/login").request().post(Entity.entity(body, MediaType.APPLICATION_JSON), Response.class);

        String res = response.readEntity(String.class);
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(res);

        String key = (String) json.get("key");
        String userID = (String) json.get("userid");

        assertNotNull(userID);
        assertNotNull(key);
        
        Response timeslotsResponse = client.target(BASE_URI).path("/createTimeslotForToday").request().header("Authorization", key).get();
        
        assertEquals(200, timeslotsResponse.getStatus());
        String timeslotsStr = timeslotsResponse.readEntity(String.class);
        assertNotNull(timeslotsStr);
        
        json = (JSONObject) parser.parse(timeslotsStr);
        String tsID = (String) json.get("tsID");
        
        Response profileResponse = client.target(BASE_URI).path("/getUserProfile").queryParam("userid", userID).request().header("Authorization", key).get();
        
        assertEquals(200, profileResponse.getStatus());
        String profileStr = profileResponse.readEntity(String.class);
        assertNotNull(profileStr);
        
        JSONObject profile = (JSONObject) parser.parse(profileStr);
        JSONArray addresses = (JSONArray) profile.get("address");
        JSONObject address = (JSONObject) addresses.get(0);
        String addressID = (String) address.get("addressID");
        
        body = "{ "
                + "\"tsID\" : \"" + tsID + "\","
                + "\"addressID\" : \"" + addressID + "\","
                + "\"usrid\" : \"" + userID + "\""
                + "}";
        
        Response pickupRequestResponse = client.target(BASE_URI).path("/requestPickup").request().header("Authorization", key).post(Entity.entity(body, MediaType.APPLICATION_JSON), Response.class);
        assertEquals(200, pickupRequestResponse.getStatus());
        String pickupRequestStr = pickupRequestResponse.readEntity(String.class);
        
        assertNotNull(pickupRequestStr);
        JSONObject pickupRequest = (JSONObject) parser.parse(pickupRequestStr);
        assertNotNull(pickupRequest.get("userRequestID"));
    }
    
    @Test
    public void testRequestPickupUnauthorized() {
        Client client = ClientBuilder.newClient();
        String body = "{"
                + "	\"tsID\" : \"896051e0-9687-4dc7-8552-b0d0a2450b89\","
                + "	\"addressID\" : \"1472669939094\","
                + "	\"usrid\" : \"55ed1677-6582-45ec-89ee-737045f6981f\""
                + "}";
        Response pickupRequestResponse = client.target(BASE_URI).path("/requestPickup").request().post(Entity.entity(body, MediaType.APPLICATION_JSON), Response.class);
        assertEquals(401, pickupRequestResponse.getStatus());
    }
    
    @Test
    public void testRequestDropOff() throws ParseException {
        String emailAddress = "rajesh.k.nitk@gmail.com";
        String password = Base64.getEncoder().encodeToString("s@mpl3".getBytes());
        String userType = "normal";

        String body = "{"
                + "\"emailAddress\":\"" + emailAddress + "\","
                + "\"password\":\"" + password + "\","
                + "\"userType\":\"" + userType + "\""
                + "}";

        Client client = ClientBuilder.newClient();
        Response response = client.target(BASE_URI).path("/login").request().post(Entity.entity(body, MediaType.APPLICATION_JSON), Response.class);

        String res = response.readEntity(String.class);
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(res);

        String key = (String) json.get("key");
        String userID = (String) json.get("userid");

        assertNotNull(userID);
        assertNotNull(key);
        
        Response timeslotsResponse = client.target(BASE_URI).path("/createTimeslotForToday").request().header("Authorization", key).get();
        
        assertEquals(200, timeslotsResponse.getStatus());
        String timeslotsStr = timeslotsResponse.readEntity(String.class);
        assertNotNull(timeslotsStr);
        
        json = (JSONObject) parser.parse(timeslotsStr);
        String tsID = (String) json.get("tsID");
        
        Response profileResponse = client.target(BASE_URI).path("/getUserProfile").queryParam("userid", userID).request().header("Authorization", key).get();
        
        assertEquals(200, profileResponse.getStatus());
        String profileStr = profileResponse.readEntity(String.class);
        assertNotNull(profileStr);
        
        JSONObject profile = (JSONObject) parser.parse(profileStr);
        JSONArray addresses = (JSONArray) profile.get("address");
        JSONObject address = (JSONObject) addresses.get(0);
        String addressID = (String) address.get("addressID");
        
        body = "{ "
                + "\"tsID\" : \"" + tsID + "\","
                + "\"addressID\" : \"" + addressID + "\","
                + "\"usrid\" : \"" + userID + "\""
                + "}";
        
        Response pickupRequestResponse = client.target(BASE_URI).path("/requestDropOff").request().header("Authorization", key).post(Entity.entity(body, MediaType.APPLICATION_JSON), Response.class);
        assertEquals(200, pickupRequestResponse.getStatus());
        String pickupRequestStr = pickupRequestResponse.readEntity(String.class);
        
        assertNotNull(pickupRequestStr);
        JSONObject pickupRequest = (JSONObject) parser.parse(pickupRequestStr);
        assertNotNull(pickupRequest.get("userRequestID"));
    }
    
    @Test
    public void testRequestDropOffUnauthorized() {
        Client client = ClientBuilder.newClient();
        String body = "{"
                + "	\"tsID\" : \"896051e0-9687-4dc7-8552-b0d0a2450b89\","
                + "	\"addressID\" : \"1472669939094\","
                + "	\"usrid\" : \"55ed1677-6582-45ec-89ee-737045f6981f\""
                + "}";
        Response pickupRequestResponse = client.target(BASE_URI).path("/requestDropOff").request().post(Entity.entity(body, MediaType.APPLICATION_JSON), Response.class);
        assertEquals(401, pickupRequestResponse.getStatus());
    }
    
    @Test
    public void testCheckout() throws ParseException {
        String emailAddress = "rajesh.k.nitk@gmail.com";
        String password = Base64.getEncoder().encodeToString("s@mpl3".getBytes());
        String userType = "normal";

        String body = "{"
                + "\"emailAddress\":\"" + emailAddress + "\","
                + "\"password\":\"" + password + "\","
                + "\"userType\":\"" + userType + "\""
                + "}";

        Client client = ClientBuilder.newClient();
        Response response = client.target(BASE_URI).path("/login").request().post(Entity.entity(body, MediaType.APPLICATION_JSON), Response.class);

        String res = response.readEntity(String.class);
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(res);

        String key = (String) json.get("key");
        String userID = (String) json.get("userid");

        assertNotNull(userID);
        assertNotNull(key);
        
        Response timeslotsResponse = client.target(BASE_URI).path("/createTimeslotForToday").request().header("Authorization", key).get();
        
        assertEquals(200, timeslotsResponse.getStatus());
        String timeslotsStr = timeslotsResponse.readEntity(String.class);
        assertNotNull(timeslotsStr);
        
        json = (JSONObject) parser.parse(timeslotsStr);
        String tsID = (String) json.get("tsID");
        
        Response cardsResponse = client.target(BASE_URI).path("/getCards").queryParam("userID", userID).request().header("Authorization", key).get();
        
        assertEquals(200, cardsResponse.getStatus());
        String cardsStr = cardsResponse.readEntity(String.class);
        assertNotNull(cardsStr);
        JSONArray cards = (JSONArray) parser.parse(cardsStr);
        JSONObject card = (JSONObject) cards.get(0);
        String cardID = (String) card.get("cardID");
        assertNotNull(cardID);
        
        body = "{"
                + "\"userID\" : \""+userID+"\","
                + "\"addressID\" : \"1472669939094\","
                + "\"swapItems\" : ["
                + "],"
                + "\"retrieveItems\" : ["
                + "],"
                + "\"shippingCharges\" : 15,"
                + "\"isPickUpRequired\" : true,"
                + "\"timeslotID\" : \"" + tsID +"\","
                + "\"cardID\" : \"" + cardID +"\""
                + "}";
        
        Response checkoutResponse = client.target(BASE_URI).path("/checkOut").request().header("Authorization", key).post(Entity.entity(body, MediaType.APPLICATION_JSON), Response.class);
        
        assertEquals(200, checkoutResponse.getStatus());
        
        String checkOutStr = checkoutResponse.readEntity(String.class);
        assertNotNull(checkOutStr);
        JSONObject checkOut = (JSONObject) parser.parse(checkOutStr);
        assertEquals("success", checkOut.get("message"));
    }
    
    @Test
    public void testGetZipCodes() throws ParseException {
        Client client = ClientBuilder.newClient();
        Response zipCodesResponse = client.target(BASE_URI).path("/getZipCodes").request().header("Authorization", "Basic YWRtaW46YWRtaW4=").get();
        
        assertEquals(200, zipCodesResponse.getStatus());
        
        String zipCodesStr = zipCodesResponse.readEntity(String.class);
        assertNotNull(zipCodesStr);
        System.out.println(zipCodesStr);
    }
    
    @Test
    public void testGetZipCodesUnauthorized() {
        Client client = ClientBuilder.newClient();
        Response zipCodesResponse = client.target(BASE_URI).path("/getZipCodes").request().get();
        assertEquals(401, zipCodesResponse.getStatus());
    }
    
}
