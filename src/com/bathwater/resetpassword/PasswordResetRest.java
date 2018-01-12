/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bathwater.resetpassword;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.mvc.Viewable;

import com.bathwater.amazons3.URLTokenHandler;

/**
 *
 * @author rajeshk
 */
@Path("/")
public class PasswordResetRest {

    @GET
    @Consumes(MediaType.TEXT_HTML)
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
            
            return Response.ok(new Viewable("/passwordReset", map)).build();
        }
        
        
        return Response.ok(new Viewable("/error")).build();
    }

}
