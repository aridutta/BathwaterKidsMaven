/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bathwater.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import com.bathwater.amazons3.JSONDatabaseUtil;

/**
 *
 * @author rajeshk
 */
public class JUnitTestListener extends RunListener {
    
    private final String keyName;
    
    private int runCount;
    
    private int failCount;
    
    private Map<String, String> failureMap;

    public JUnitTestListener(String keyName) {
        this.keyName = keyName;
        this.runCount = 0;
        this.failCount = 0;
        this.failureMap = new HashMap<>();
    }

    @Override
    public void testFinished(Description description) throws Exception {
        this.runCount++;
        JSONObject json = new JSONObject();
        json.put("runCount", this.runCount);
        json.put("failureCount", this.failCount);
        json.put("failureMap", this.failureMap);
        
        InputStream in = new ByteArrayInputStream(json.toJSONString().getBytes());
        JSONDatabaseUtil.uploadFile(this.keyName, in);
    }

    @Override
    public void testFailure(Failure failure) throws Exception {
        this.failCount++;
        this.failureMap.put(failure.getDescription().getMethodName(), failure.getMessage());
    }

    @Override
    public void testRunFinished(Result result) throws Exception {
        JSONObject json = new JSONObject();
        json.put("runCount", result.getRunCount());
        json.put("failureCount", result.getFailureCount());
        json.put("failureMap", this.failureMap);
        json.put("runTime", (result.getRunTime() / 1000L));
        
        InputStream in = new ByteArrayInputStream(json.toJSONString().getBytes());
        JSONDatabaseUtil.uploadFile(this.keyName, in);
    }
    
}
