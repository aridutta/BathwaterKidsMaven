/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bathwater.amazons3;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;


/**
 *
 * @author rajeshk
 */
public class PromoCodeDatabaseUtil {
    private static final String BUCKET_NAME  = "1".equals(System.getProperty("PRODUCTION_MODE")) ? "bathwater.promos" : "bathwater.promos.dev";
    
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
    
    public static boolean deleteFile(String fileName) {
        boolean isDeleted = true;
        AmazonS3 s3 = new AmazonS3Client(CREDENTIALS);
        try {
            s3.deleteObject(new DeleteObjectRequest(BUCKET_NAME,fileName));
        } catch (AmazonS3Exception ase) {
            isDeleted = false;
        }
        
        return isDeleted;
    }
    
    public static List<String> getAllFileNames()
    {
        AmazonS3 s3client = new AmazonS3Client(CREDENTIALS);

        ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
                .withBucketName(BUCKET_NAME);
        ObjectListing objectListing;
        List<String> keyNames = new ArrayList<String>();
        do {
            objectListing = s3client.listObjects(listObjectsRequest);
            for (S3ObjectSummary objectSummary
                    : objectListing.getObjectSummaries()) {
                System.out.println(" - " + objectSummary.getKey() + "  "
                        + "(size = " + objectSummary.getSize()
                        + ")");
                keyNames.add(objectSummary.getKey());
            }
            listObjectsRequest.setMarker(objectListing.getNextMarker());
        } while (objectListing.isTruncated());
        
        return keyNames;
    }
}
