/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bathwater.elasticsearch.helper;

import io.searchbox.client.JestClient;
import io.searchbox.client.config.HttpClientConfig;

/**
 *
 * @author rajeshk
 */
public class JestClientFactory {
    
    private static JestClient client = null;
    
    private static final String ES_SERVER_URL = "http://54.208.116.231:9200";
    
    public static JestClient getJestClient() {
        
        if (client == null) {
            io.searchbox.client.JestClientFactory factory = new io.searchbox.client.JestClientFactory();
            factory.setHttpClientConfig(new HttpClientConfig.Builder(ES_SERVER_URL).build());
            client = factory.getObject();
        }
        
        return client;
    }
    
}
