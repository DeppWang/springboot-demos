package wang.depp.springbootrabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class QueueMessageListener {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	static final String QUEUE_FILE = "waitfile";

	@RabbitListener(queues = QUEUE_FILE)
	public void onFileMessageFromFileQueue(Message message) {
		logger.info("queue {} received waitfile message: {}", QUEUE_FILE, message.message);
	}
}
