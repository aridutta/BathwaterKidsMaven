/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bathwater;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

/**
 *
 * @author rajeshk
 */
public class AuthenticationBinder extends AbstractBinder {

    @Override
    protected void configure() {
        bind(new AuthenticationService()).to(AuthenticationService.class);
    }
    
}
