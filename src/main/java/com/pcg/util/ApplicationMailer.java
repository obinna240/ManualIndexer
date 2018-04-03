package com.pcg.util;

import java.io.File;

import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Service("mailService")
public class ApplicationMailer 
{
	static Log logger = LogFactory.getLog(ApplicationMailer.class);
	
	@Autowired
	private JavaMailSenderImpl mailSender;

	@Autowired
	private SimpleMailMessage preConfiguredMessage;
	
	/**
	 * This method will send compose and send the message
	 * */
	public void sendMail(String to, String subject, String body)
	{
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setSubject(subject);
		message.setText(body);
		mailSender.send(message);
	}
	
	/**
	 * This method will send a pre-configured message
	 * 
	 */
	public void sendPreConfiguredMail(String message, String subjectFlag)
	{
		SimpleMailMessage mailMessage = new SimpleMailMessage(preConfiguredMessage);
		if(subjectFlag!=null)
		{
			mailMessage.setSubject("Success: Hampshire Index Batch Process Report");
		}
		mailMessage.setText(message);
		mailSender.send(mailMessage);
	}
	
	/**
	 * 
	 * @param message
	 * @param fileName
	 * @param processName
	 */
	public void sendEmailWithAttachment(String message,String fileName,String processName){
		try {
			FileSystemResource file = new FileSystemResource(new File(fileName));
			//  mailSender.
			MimeMessage mimeMessage =  mailSender.createMimeMessage();
			// use the true flag to indicate you need a multipart message
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
			helper.setSubject("Batch Failed for " +processName);
			helper.setText(message);
			
			helper.addAttachment(fileName, file);
			helper.setTo(preConfiguredMessage.getTo());
			// Send message
			mailSender.send(mimeMessage);

			logger.info("Sent message successfully....");

		} catch (Exception e) 
		{
			logger.error("Error occurred while sending the Batch email" +  e.getMessage());
		}
	}
}
