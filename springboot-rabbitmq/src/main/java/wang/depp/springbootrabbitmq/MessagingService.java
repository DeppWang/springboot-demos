package wang.depp.springbootrabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessagingService {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    RabbitTemplate rabbitTemplate;

    public void sendMessage(Message msg) {
        logger.info("msg: {}", msg);
        rabbitTemplate.convertAndSend("login", "", msg);
    }

    public void sendMessage2(Message msg) {
        logger.info("msg: {}", msg);
        rabbitTemplate.convertAndSend("waitfile", "", msg);
    }
}
