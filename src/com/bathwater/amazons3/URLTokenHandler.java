/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bathwater.amazons3;

import java.io.IOException;
import java.io.InputStream;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

/**
 *
 * @author rajeshk
 */
public class URLTokenHandler {
    private static final String BUCKET_NAME  = "1".equals(System.getProperty("PRODUCTION_MODE")) ? "bathwater.tokens" : "bathwater.tokens.dev";
    
    
    
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
    
    public static void uploadTokenFile(String fileName, InputStream inStream) throws IOException {
        AmazonS3Client client = new AmazonS3Client(CREDENTIALS);
        
        ObjectMetadata metadata = new ObjectMetadata();
        PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET_NAME, fileName, inStream, metadata);
        client.putObject(putObjectRequest);
    }
    
    public static InputStream downloadTokenFile(String fileName) {
        AmazonS3Client client = new AmazonS3Client(CREDENTIALS);
        GetObjectRequest getObjectRequest = new GetObjectRequest(BUCKET_NAME, fileName);
        S3Object s3Object = client.getObject(getObjectRequest);
        return s3Object.getObjectContent();
    }
    
    public static boolean deleteTokenFile(String fileName) {
        AmazonS3Client client = new AmazonS3Client(CREDENTIALS);
        DeleteObjectRequest deleteRequest = new DeleteObjectRequest(BUCKET_NAME, fileName);
        try {
            client.deleteObject(deleteRequest);
        } catch (AmazonServiceException ase) {
            return false;
        }
        
        return true;
    }
    
    public static boolean isValidFile(String fileName) {
        boolean isValidFile = true;
        AmazonS3 s3 = new AmazonS3Client(CREDENTIALS);
        try {
            ObjectMetadata metaData = s3.getObjectMetadata(BUCKET_NAME, fileName);
        } catch (AmazonS3Exception ase) {
            isValidFile = false;
        }
        
        return isValidFile;
    }
}
