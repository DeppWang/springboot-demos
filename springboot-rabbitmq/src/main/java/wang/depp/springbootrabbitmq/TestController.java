package wang.depp.springbootrabbitmq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller()
public class TestController {
    @Autowired
    MessagingService messagingService;

    @PostMapping("/sendMessage")
    @ResponseBody
    public String sendMessage(String message) {
        try {
            messagingService.sendMessage(Message.of(message));
        } catch (Exception e) {
            return "False";
        }
        return "True";

    }

    @GetMapping("/sendMessage")
    @ResponseBody
    public String sendMessageGet(String message) {
        try {
            messagingService.sendMessage2(Message.of(message));
        } catch (Exception e) {
            return "False";
        }
        return "True";
    }
}
