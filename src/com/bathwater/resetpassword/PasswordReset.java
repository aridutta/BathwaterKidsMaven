/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bathwater.resetpassword;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.TracingConfig;
import org.glassfish.jersey.server.mvc.jsp.JspMvcFeature;

/**
 *
 * @author rajeshk
 */
public class PasswordReset extends ResourceConfig {

    public PasswordReset() {
        
        packages(PasswordResetRest.class.getPackage().getName());
        
        register(JspMvcFeature.class);
        
        property(ServerProperties.TRACING, TracingConfig.ON_DEMAND.name());
        
    }
    
}
