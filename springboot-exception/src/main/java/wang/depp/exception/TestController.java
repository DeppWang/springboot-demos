package wang.depp.exception;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import wang.depp.exception.exception.LoanException;
import wang.depp.exception.exception.ResponseEnum;
import wang.depp.substruction.common.ApiResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

//import org.apache.logging.log4j.spi.LoggerContext;
//import org.slf4j.LoggerFactory;

@Slf4j
@RestController
public class TestController {
    @Autowired
    TestService testService;

    @GetMapping("/test")
    public ApiResponse<String> test(String value) {
        ResponseEnum.SERVICE_ERROR.assertNotNull(value);
        log.info("value: [{}]", value);
        if (!value.equals("test")) {
            return ApiResponse.fail(ResponseEnum.SERVER_ERROR.getCode(), ResponseEnum.SERVER_ERROR.getMessage());
        }
        return ApiResponse.success("true");
    }

    @GetMapping("/testThrowRuntimeException2")
    public ApiResponse<Void> testThrowRuntimeException2() {
        int[] arr = new int[10];
        int x = arr[11];
        return ApiResponse.success();
    }

    @GetMapping("/testThrowRuntimeException3")
    public ApiResponse<Void> testThrowRuntimeException3() throws ArrayIndexOutOfBoundsException {
        throwRuntimeException2();
        return ApiResponse.success();
    }

    private void throwRuntimeException2() throws ArrayIndexOutOfBoundsException {
        System.out.println("testThrowRuntimeException");
    }

    /**
     * GlobalExceptionHandler 可以拦截
     *
     * @return
     */
    @GetMapping("/testThrowRuntimeException")
    public ApiResponse<Void> testThrowRuntimeException() {
        throwRuntimeException();
        return ApiResponse.success();
    }

    private void throwRuntimeException() {
        System.out.println("testThrowRuntimeException");
        throw new ArrayIndexOutOfBoundsException();
    }

    @GetMapping("/testThrowIOException")
    public ApiResponse<Void> testThrowIOException() throws IOException {

        throwIOException();
        return ApiResponse.success();
    }

    private void throwIOException() throws IOException {
        System.out.println("testThrowIOException");
        throw new IOException();
    }

    /**
     * 测试当本身没有异常，却使用 throws 抛出异常时，是否能拦截到异常。结果是不能
     *
     * @return
     * @throws IOException
     */
    @GetMapping("/testThrowIOException2")
    public ApiResponse<Void> testThrowIOException2() throws IOException {

        throwIOException2();
        return ApiResponse.success();
    }

    private void throwIOException2() throws IOException {
        System.out.println("testThrowIOException");
    }

    @GetMapping("/testStackOverflow")
    public ApiResponse<Void> testStackOverflow() {
        log.info("testStackOverflow");
        JavaVMStackSOF oom = new JavaVMStackSOF();
        try {
            oom.stackLeak();
        } catch (Throwable e) {
            System.out.println("stack length:" + oom.stackLength);
            throw e;
        }
        return ApiResponse.success();
    }

    @GetMapping("/testLoanException")
    private ApiResponse<Void> testLoanException() {
        throw LoanException.REJECT;
    }

    @GetMapping("/testSOF")
    private ApiResponse<Void> testSOF() throws Throwable {
        JavaVMStackSOF.main(null);
        return ApiResponse.success();
    }

    @GetMapping("/testOutOfMemory")
    public ApiResponse<Void> testOutOfMemory() {
        HeapOOM.main(null);
        return ApiResponse.success();
    }

    @GetMapping("/testExceptionPrint")
    public ApiResponse<Void> testExceptionPrint() throws IOException {
        throw new IOException("IOException test");
    }

    public static void main(String[] args) {
        // assume SLF4J is bound to logback in the current environment
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        // print logback's internal status
        StatusPrinter.print(lc);
    }

}

class JavaVMStackSOF {
    public int stackLength = 1;

    public void stackLeak() {
        stackLength++;
        stackLeak();
    }

    public static void main(String[] args) throws Throwable {
        JavaVMStackSOF oom = new JavaVMStackSOF();
        try {
            oom.stackLeak();
        } catch (Throwable e) {
            System.out.println("stack length:" + oom.stackLength);
            throw e;
        }
    }
}

class HeapOOM {
    static class OOMObject {
    }

    public static void main(String[] args) {
        List<OOMObject> list = new ArrayList<>();
        while (true) {
            list.add(new OOMObject());
        }
    }
}