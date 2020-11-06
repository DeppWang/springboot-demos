package wang.depp.exception.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import wang.depp.exception.exception.ResponseEnum;
import wang.depp.substruction.common.ApiResponse;
import wang.depp.substruction.common.exceptions.BaseException;
import wang.depp.substruction.common.exceptions.BusinessException;
import wang.depp.substruction.common.exceptions.SystemException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 生产环境
     */
    private final static String ENV_PROD = "production";

    /**
     * 当前环境
     */
    @Value("${env}")
    private String profile;

    /**
     * 业务异常
     *
     * @param e 异常
     * @return 异常结果
     */
    @ExceptionHandler(value = BusinessException.class)
    @ResponseBody
    public ApiResponse<String> handleBusinessException(BaseException e) {
        log.error(e.getMessage(), e);
        log.error("BusinessException");
        return ApiResponse.fail(e.getCode(), e.getMessage());
    }

    /**
     * 非错误编码类系统异常
     *
     * @param e 异常
     * @return 异常结果
     */
    @ExceptionHandler(value = SystemException.class)
    @ResponseBody
    public ApiResponse<String> handleBaseException(SystemException e) {
        return getServerErrorApiResponse(e);
    }

    /**
     * Controller 上一层相关异常
     *
     * @param e 异常
     * @return 异常结果
     */
    @ExceptionHandler({NoHandlerFoundException.class,
            HttpRequestMethodNotSupportedException.class,
            HttpMediaTypeNotSupportedException.class,
            MissingPathVariableException.class,
            MissingServletRequestParameterException.class,
            TypeMismatchException.class,
            HttpMessageNotReadableException.class,
            HttpMessageNotWritableException.class,
            // BindException.class,
            // MethodArgumentNotValidException.class
            HttpMediaTypeNotAcceptableException.class,
            ServletRequestBindingException.class,
            ConversionNotSupportedException.class,
            MissingServletRequestPartException.class,
            AsyncRequestTimeoutException.class
    })
    @ResponseBody
    public ApiResponse<String> handleServletException(Exception e) {
        return getServerErrorApiResponse(e);
    }

    /**
     * 未定义异常。相当于全局异常捕获处理器。
     *
     * @param e 异常
     * @return 异常结果
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ApiResponse<String> handleException(Exception e) {
//        throw e;
//        log.error("未定义异常");
        return getServerErrorApiResponse(e);
    }

    private ApiResponse<String> getServerErrorApiResponse(Exception e) {
        log.error(e.getMessage() + "log", e);
//        log.error(e.getMessage());
//        log.error(e.printStackTrace());
        log.info("--------------------");
        System.out.println(e.getMessage() + "e.");
        e.printStackTrace();
        int code = ResponseEnum.SERVER_ERROR.getCode();
        String productShowMessage = ResponseEnum.SERVER_ERROR.getMessage();
        if (ENV_PROD.equals(profile)) {
            return ApiResponse.fail(code, productShowMessage);
        }
        return ApiResponse.fail(code, e.getMessage());
    }
}
