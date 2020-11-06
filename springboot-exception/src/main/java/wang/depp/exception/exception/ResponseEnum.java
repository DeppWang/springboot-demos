package wang.depp.exception.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wang.depp.substruction.common.exceptions.BusinessExceptionAssert;


/**
 * 例如 101001  ，前2位表示系统例如10-支付系统，最后4位服务内部定义错误码（可以自己规划模块的概念，外部其实不太关注）。
10-支付，11-申请，12-核心 ，14-passport 这种规则等等等
 **/
@Getter
@AllArgsConstructor
public enum ResponseEnum implements BusinessExceptionAssert {


    SUCCESS(111000,"success"),
    PARAM_VALID_ERROR(111001,"param check error."),
    SERVER_ERROR(111002,"server error."),
    LOGIN_ERROR(111003,"login error"),
    UNAUTHORIZED(111004, "unauthorized"),
    SERVICE_ERROR(111005,"service error."),
    FORBIDDEN(114003, "forbidden"),
    TIMEOUT(114000, "timeout"),
    REJECT(114001, "reject"),
    EMAIL_CONFLICT(114002, "email conflict"),
    EMAIL_VERIFY_FAIL(114004, "email verify fail"),
    UUID_DUPLICATE(114006, "UUID duplicate"),
    USERID_INCONSIST(114007, "Userid inconsistent"),
    DB_OPTIMISTIC_LOCK(114008, "update fail"),// 数据库乐观锁
    LANDING_TOKEN_TIME_OUT(114009, "apply link expired"),
    NO_CREDIT_RESULT(114010, "no credit result"),
    EMAIL_SEND_FAIL(114011, "email send fail"),
    DATA_NOT_FOUND(114012, "data not found"),
    LANDING_TOKEN_DOB(114013, "Please verify your birthday"),
    LOGIN_TOKEN_VERIFY_FAIL(114014, "login token verify fail"),

    ;

    /**
     * 返回码
     */
    private int code;
    /**
     * 返回消息
     */
    private String message;

}
