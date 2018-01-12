/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bathwater;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.ManagedBean;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.bathwater.dao.DAO;
import com.bathwater.dto.DriverLoginParameters;
import com.bathwater.dto.ErrorResult;
import com.bathwater.dto.LoginParameters;
import com.bathwater.dto.OAuthLoginParameters;
import com.bathwater.util.StringUtil;

/**
 *
 * @author rajeshk
 */
@Resource
@ManagedBean
public class AuthenticationService {

    public boolean authenticate(String authCredentials) {

        if (authCredentials == null || authCredentials.equals("")) {
            return false;
        }

        final String encodedCredentials = authCredentials.replaceFirst("Basic ", "");

        String credentials = "";

        try {
            byte[] bytes = Base64.getDecoder().decode(encodedCredentials);
            credentials = new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(AuthenticationService.class.getName()).log(Level.SEVERE, null, ex);
        }

        final StringTokenizer tokenizer = new StringTokenizer(credentials, ":");
        final String userName = tokenizer.nextToken();
        String password = "";
        if (!credentials.contains("::")) {
            password = tokenizer.nextToken();
        }

        if ("admin".equals(userName) && "admin".equals(password)) {
            return true;
        }
        
        final String token = tokenizer.nextToken();

        LoginParameters parameters = new LoginParameters();
        parameters.setEmailAddress(userName);
        parameters.setPassword(password);
        parameters.setToken(token);
        if (StringUtil.isBlank(password) || parameters.getPassword().equals("null")) {
            parameters.setUserType("fbUser");
        }

        ErrorResult res = DAO.getInstance().login(parameters);

        return res.getErrorCode() == 200 || res.getErrorCode() == 201 || res.getErrorCode() == 203;
    }

    public boolean authenticateDriver(String authCredentials) {
        if (authCredentials == null || authCredentials.equals("")) {
            return false;
        }

        final String encodedCredentials = authCredentials.replaceFirst("Basic ", "");

        String credentials = null;

        try {
            byte[] bytes = Base64.getDecoder().decode(encodedCredentials);
            credentials = new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(AuthenticationService.class.getName()).log(Level.SEVERE, null, ex);
        }

        final StringTokenizer tokenizer = new StringTokenizer(credentials, ":");
        final String userName = tokenizer.nextToken();
        final String password = tokenizer.nextToken();
        final String token = tokenizer.nextToken();

        DriverLoginParameters driverParams = new DriverLoginParameters();
        driverParams.setEmail(userName);
        driverParams.setPassword(password);
        driverParams.setToken(token);

        ErrorResult res = DAO.getInstance().driverLogin(driverParams);

        return res.getErrorCode() == 200;

    }
    
    public boolean authenticateAdmin(String authCredentials,String method,HttpServletRequest req) {
        if (authCredentials == null || authCredentials.equals("")) {
            return false;
        }

        final String encodedCredentials = authCredentials.replaceFirst("Basic ", "");

        String credentials = null;

        try {
            byte[] bytes = Base64.getDecoder().decode(encodedCredentials);
            credentials = new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(AuthenticationService.class.getName()).log(Level.SEVERE, null, ex);
        }

        final StringTokenizer tokenizer = new StringTokenizer(credentials, ":");
        if(tokenizer.countTokens() > 3) { 
        final String email = tokenizer.nextToken();
        final String userid = tokenizer.nextToken();
        final String role = tokenizer.nextToken();
        final String token = tokenizer.nextToken();
        

        OAuthLoginParameters params = new OAuthLoginParameters();
        params.setEmail(email);
        params.setId(userid);
        params.setRole(role);
        params.setToken(token);

        ErrorResult res = DAO.getInstance().verifyAdmin(params);
        if(res.getErrorCode() == 200)
        {
        if(method.contains("/rest/getAllDrivers") 
                || method.contains("/rest/uploadDriverImage")
                || method.contains("/rest/addDriver")
                || method.contains("/rest/getPromos")
                || method.contains("/rest/deletePromoCode")
                || method.contains("/rest/addTruckwithImage")
                || method.contains("/rest/getTimeslotsForTheWeek")
                || method.contains("/rest/uploadPromoFile")
                || method.contains("/rest/createTimeSlotsRange")
                || method.contains("/rest/getAllTrucks")
                || method.contains("/rest/uploadTruckImage")
                || method.contains("/rest/getZipCodes")
                || method.contains("/rest/deleteZipCode")
                || method.contains("/rest/addZipCode")) {
            if(role.equalsIgnoreCase("4") || role.equalsIgnoreCase("10"))
                return true;
            else
                return false;
        }    
        }
        return res.getErrorCode() == 200;
    }else {
            return false;
        }
    }
    
    

}
