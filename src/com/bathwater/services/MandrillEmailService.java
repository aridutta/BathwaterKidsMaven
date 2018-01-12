/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bathwater.services;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.filter.LoggingFilter;

import com.bathwater.dto.MailAPIBody;

/**
 *
 * @author rajeshk
 */
public class MandrillEmailService {

	public static String sendForgotPasswordMail(String userName, String newPassword, String email, String urlString)
			throws MalformedURLException, IOException {
		Client client = ClientBuilder.newClient();
		client.register(LoggingFilter.class);
		WebTarget target = client.target("https://mandrillapp.com/api/1.0/messages/send-template.json");

		MailAPIBody mail = new MailAPIBody();
		mail.setKey(System.getProperty("MANDRILL"));
		MailAPIBody.Message message = new MailAPIBody.Message();
		message.setFrom_email("noreply@bathwaterkids.com");
		message.setFrom_name("Bathwater Kids");
		message.setSubject("Password Reset");
		List<String> tags = new LinkedList<>();
		tags.add("password-reset");
		message.setTags(tags);
		mail.setTemplate_name("Forgot Password");
		List<MailAPIBody.TemplateContent> template_contents = new ArrayList<>();
		MailAPIBody.TemplateContent fname = new MailAPIBody.TemplateContent();
		fname.setName("fname");
		fname.setContent(userName);
		template_contents.add(fname);
		MailAPIBody.TemplateContent resetURL = new MailAPIBody.TemplateContent();
		resetURL.setName("reseturl");
		resetURL.setContent(urlString);
		template_contents.add(resetURL);
		mail.setTemplate_content(template_contents);
		MailAPIBody.Message.To to = new MailAPIBody.Message.To();
		to.setEmail(email);
		to.setName(userName);
		to.setType("to");
		List<MailAPIBody.Message.To> tos = new ArrayList<>();
		tos.add(to);
		message.setTo(tos);
		mail.setMessage(message);

		Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
		Response response = invocationBuilder.post(Entity.entity(mail, MediaType.APPLICATION_JSON));

		return response.readEntity(String.class);
	}

	public static String sendMail(String templateName, Map<String, String> templateContent, String emailAddress,
			String subject) {
		Client client = ClientBuilder.newClient();
		client.register(LoggingFilter.class);
		WebTarget target = client.target("https://mandrillapp.com/api/1.0/messages/send-template.json");

		MailAPIBody mail = new MailAPIBody();
		mail.setKey(System.getProperty("MANDRILL"));
		MailAPIBody.Message message = new MailAPIBody.Message();
		message.setFrom_email("noreply@bathwaterkids.com");
		message.setFrom_name("Bathwater Kids");
		message.setSubject(subject);

		List<String> tags = new LinkedList<>();
		tags.add(templateName);
		message.setTags(tags);

		mail.setTemplate_name(templateName);

		if (templateContent != null) {
			List<MailAPIBody.TemplateContent> template_contents = new ArrayList<>();

			Iterator<Map.Entry<String, String>> iterator = templateContent.entrySet().iterator();

			while (iterator.hasNext()) {
				Map.Entry<String, String> entry = iterator.next();
				MailAPIBody.TemplateContent content = new MailAPIBody.TemplateContent();
				content.setName(entry.getKey());
				content.setContent(entry.getValue());
				template_contents.add(content);
			}

			mail.setTemplate_content(template_contents);
		}

		MailAPIBody.Message.To to = new MailAPIBody.Message.To();
		to.setEmail(emailAddress);
		to.setName(emailAddress.substring(0, emailAddress.indexOf("@")));
		to.setType("to");
		List<MailAPIBody.Message.To> tos = new ArrayList<>();
		tos.add(to);
		message.setTo(tos);
		mail.setMessage(message);

		Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
		Response response = invocationBuilder.post(Entity.entity(mail, MediaType.APPLICATION_JSON));

		return response.readEntity(String.class);
	}
}
