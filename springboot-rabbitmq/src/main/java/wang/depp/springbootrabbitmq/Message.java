package wang.depp.springbootrabbitmq;

public class Message {
    // message 1、设置为公开、2 使用 setter 和 getter
    public String message;
    public String email;

    public static Message of(String message) {
        Message msg = new Message();
        msg.message = message;
        msg.email = message;
        return msg;
    }
}
