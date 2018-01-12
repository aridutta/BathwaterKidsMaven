/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bathwater;

import org.glassfish.jersey.server.ResourceConfig;

/**
 *
 * @author rajeshk
 */
public class ApplicationConfig extends ResourceConfig {

    public ApplicationConfig() {
        packages(BathwaterRest.class.getPackage().getName());
        register(new AuthenticationBinder());
    }    
    
    
}
