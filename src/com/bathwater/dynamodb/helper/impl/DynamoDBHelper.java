/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bathwater.dynamodb.helper.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.bathwater.dynamodb.helper.IDynamoDBHelper;
import com.bathwater.dynamodb.tables.BathwaterItem;

/**
 *
 * @author rajeshk
 */
public class DynamoDBHelper implements IDynamoDBHelper {

	private static DynamoDBHelper instance = null;

	private final AmazonDynamoDB dynamoDBClient;

	private final DynamoDBMapper mapper;

	protected DynamoDBHelper() {
		AWSCredentials credentials = new AWSCredentials() {
			@Override
			public String getAWSAccessKeyId() {
				return System.getProperty("AWSACCESS_KEY");
			}

			@Override
			public String getAWSSecretKey() {

				return System.getProperty("AWSSECRET_KEY");
			}
		};
		dynamoDBClient = new AmazonDynamoDBClient(credentials).withEndpoint("http://dynamodb.us-east-1.amazonaws.com");
		mapper = new DynamoDBMapper(dynamoDBClient, new DynamoDBMapperConfig(new TableNameResolver()));
	}

	public static DynamoDBHelper getInstance() {
		if (instance == null) {
			instance = new DynamoDBHelper();
		}
		return instance;
	}

	@Override
	public boolean putItem(BathwaterItem item) {
		try {
			item.saveItem(mapper);
			return true;
		} catch (Exception ex) {
			Logger.getLogger(DynamoDBHelper.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
		}
		return false;
	}

	@Override
	public boolean deleteItem(BathwaterItem item) {
		try {
			item.deleteItem(mapper);
			return true;
		} catch (Exception ex) {
			Logger.getLogger(DynamoDBHelper.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
		}
		return false;
	}

	@Override
	public DynamoDBMapper getMapper() {
		return mapper;
	}

	@Override
	public DynamoDB getDynamoDB() {
		return new DynamoDB(dynamoDBClient);
	}

	public static class TableNameResolver extends DynamoDBMapperConfig.DefaultTableNameResolver {

		@Override
		public String getTableName(Class<?> clazz, DynamoDBMapperConfig config) {
			String base = super.getTableName(clazz, config);

			if (!"1".equals(System.getProperty("PRODUCTION_MODE"))) {
				base = base + "_dev";
			}

			return base;
		}

	}
}
