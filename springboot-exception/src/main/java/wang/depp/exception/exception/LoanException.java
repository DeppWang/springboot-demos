package wang.depp.exception.exception;


import wang.depp.substruction.common.exceptions.BusinessException;
import wang.depp.substruction.common.exceptions.IResponseEnum;

/**
 * Created by sunbo on 2020-06-16
 */
public class LoanException extends BusinessException {

    public static LoanException INTERNAL_ERROR = new LoanException(ResponseEnum.SERVER_ERROR);
    public static LoanException REJECT = new LoanException(ResponseEnum.REJECT);
    public static LoanException FORBIDDEN = new LoanException(ResponseEnum.FORBIDDEN);
//    public static LoanException PARAM_VALID_ERROR = new LoanException(ResponseEnum.PARAM_VALID_ERROR);
    public static LoanException DB_OPTIMISTIC_LOCK = new LoanException(ResponseEnum.DB_OPTIMISTIC_LOCK);

    public LoanException(IResponseEnum responseEnum) {
        super(responseEnum, null, responseEnum.getMessage());
    }

    public LoanException(IResponseEnum responseEnum, String message) {
        super(responseEnum, null, message);
    }

}
