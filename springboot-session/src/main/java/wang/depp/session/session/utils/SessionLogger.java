package wang.depp.session.session.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionLogger {

    private static Logger log = LoggerFactory.getLogger(SessionLogger.class);

    public static void logError(String message) {
        log.error(message);
    }

    public static void logError(String message, Exception e) {
        log.error(message, e);
    }


}
