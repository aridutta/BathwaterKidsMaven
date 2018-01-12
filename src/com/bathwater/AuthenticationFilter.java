/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bathwater;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author rajeshk
 */
public class AuthenticationFilter implements Filter {

    public static final String AUTHENTICATION_HEADER = "Authorization";

    AuthenticationService service = new AuthenticationService();

    @Override
    public void init(FilterConfig fc) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filter) throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            
            String requestURI = httpServletRequest.getRequestURI();
                        
            if (httpServletRequest.getMethod().equals("OPTIONS") || requestURI.contains("login") || requestURI.contains("register") || requestURI.contains("reset") || requestURI.contains("updatePassword")) {
                ((HttpServletResponse)response).addHeader("Access-Control-Allow-Origin", "*");
                ((HttpServletResponse)response).addHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
                ((HttpServletResponse)response).addHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Authorization");
                filter.doFilter(request, response);
            } else {

                String authCredentials = httpServletRequest.getHeader(AUTHENTICATION_HEADER);
                

                boolean authStatus = false;
//                String method = requestURI;
//                if(method.contains("/rest/getAllDrivers") 
//                || method.contains("/rest/uploadDriverImage")
//                || method.contains("/rest/addDriver")
//                || method.contains("/rest/getPromos")
//                || method.contains("/rest/deletePromoCode")
//                || method.contains("/rest/addTruckwithImage")
//                || method.contains("/rest/getTimeslotsForTheWeek")
//                || method.contains("/rest/uploadPromoFile")
//                || method.contains("/rest/createTimeSlotsRange")
//                || method.contains("/rest/getAllTrucks")
//                || method.contains("/rest/uploadTruckImage")
//                || method.contains("/rest/getZipCodes")
//                || method.contains("/rest/deleteZipCode")
//                || method.contains("/rest/addZipCode")) {
//                authStatus = service.authenticateAdmin(authCredentials,requestURI, httpServletRequest);
//                }else 
                {
                authStatus = service.authenticate(authCredentials)
                        || service.authenticateAdmin(authCredentials,requestURI, httpServletRequest)
                        || service.authenticateDriver(authCredentials) ;
                }
                         

                if (authStatus) {
                    filter.doFilter(request, response);
                } else if (response instanceof HttpServletResponse) {
                    HttpServletResponse httpServletResponse = (HttpServletResponse) response;
                    httpServletResponse
                            .setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                }
            }
        }
    }

    @Override
    public void destroy() {

    }

}
