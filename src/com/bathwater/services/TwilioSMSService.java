/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bathwater.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.MessageFactory;
import com.twilio.sdk.resource.instance.Message;

/**
 *
 * @author rajeshk
 */
public class TwilioSMSService {

	private static final String ACCOUNT_SID = System.getProperty("TWILIO_SID");
	private static final String AUTH_TOKEN = System.getProperty("TWILIO_TOKEN");

	public static boolean sendSMS(String body, String to) {
		TwilioRestClient client = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN);

		List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("From", "+19177460268"));
		if (!to.startsWith("+")) {
			params.add(new BasicNameValuePair("To", "+" + to));
		} else {
			params.add(new BasicNameValuePair("To", to));
		}
		params.add(new BasicNameValuePair("Body", body));

		MessageFactory messageFactory = client.getAccount().getMessageFactory();
		try {
			Message message = messageFactory.create(params);
		} catch (TwilioRestException ex) {
			return false;
		}

		return true;
	}

}
