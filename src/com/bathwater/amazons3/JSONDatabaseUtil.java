/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bathwater.amazons3;

import java.io.IOException;
import java.io.InputStream;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

/**
 *
 * @author rajeshk
 */
public class JSONDatabaseUtil {
    private static final String BUCKET_NAME  = "1".equals(System.getProperty("PRODUCTION_MODE")) ? "bathwater.jsons" : "bathwater.jsons.dev";
    
    private final static AWSCredentials CREDENTIALS = new AWSCredentials() {
        @Override
        public String getAWSAccessKeyId() {
            return System.getProperty("AWSACCESS_KEY");
        }

        @Override
        public String getAWSSecretKey() {
            return System.getProperty("AWSSECRET_KEY");
        }
    };
    
    public static void uploadFile(String fileName, InputStream inStream) throws IOException {
        AmazonS3Client client = new AmazonS3Client(CREDENTIALS);
        
        ObjectMetadata metadata = new ObjectMetadata();
        PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET_NAME, fileName, inStream, metadata);
        client.putObject(putObjectRequest);
    }
    
    public static InputStream downloadFile(String fileName) {
        AmazonS3Client client = new AmazonS3Client(CREDENTIALS);
        GetObjectRequest getObjectRequest = new GetObjectRequest(BUCKET_NAME, fileName);
        S3Object s3Object = client.getObject(getObjectRequest);
        return s3Object.getObjectContent();
    }

    public static String getBUCKET_NAME() {
        return BUCKET_NAME;
    }
}
