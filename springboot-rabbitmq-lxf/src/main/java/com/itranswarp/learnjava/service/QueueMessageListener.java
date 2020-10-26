package com.itranswarp.learnjava.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.itranswarp.learnjava.messaging.LoginMessage;
import com.itranswarp.learnjava.messaging.RegistrationMessage;

@Component
public class QueueMessageListener {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	static final String QUEUE_MAIL = "q_mail";
	static final String QUEUE_SMS = "q_sms";
	static final String QUEUE_APP = "q_app";

	@RabbitListener(queues = QUEUE_MAIL)
	public void onRegistrationMessageFromMailQueue(RegistrationMessage message) throws Exception {
		logger.info("queue {} received registration message: {}", QUEUE_MAIL, message);
	}

	@RabbitListener(queues = QUEUE_SMS)
	public void onRegistrationMessageFromSmsQueue(RegistrationMessage message) throws Exception {
		logger.info("queue {} received registration message: {}", QUEUE_SMS, message);
	}

	@RabbitListener(queues = QUEUE_MAIL)
	public void onLoginMessageFromMailQueue(LoginMessage message) throws Exception {
		logger.info("queue {} received message: {}", QUEUE_MAIL, message);
	}

	@RabbitListener(queues = QUEUE_SMS)
	public void onLoginMessageFromSmsQueue(LoginMessage message) throws Exception {
		logger.info("queue {} received message: {}", QUEUE_SMS, message);
	}

	@RabbitListener(queues = QUEUE_APP)
	public void onLoginMessageFromAppQueue(LoginMessage message) throws Exception {
		logger.info("queue {} received message: {}", QUEUE_APP, message);
	}
}
