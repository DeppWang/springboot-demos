package wang.depp.exception;

import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Created by DEPP WANG on 3/11/2020
 */
@Service
public class TestService {
    public void testThrowRuntimeException() throws ArrayIndexOutOfBoundsException {
        System.out.println("testThrowRuntimeException");
    }

    public void testThrowIOException() throws IOException {
        System.out.println("testThrowIOException");
    }
}
