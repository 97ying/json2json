package com.automotive.common.json2json.Exception;

/**
 * Created by ejaiwng on 8/30/2017.
 */
public class ServiceException extends RuntimeException {

    private int status;
    private String errorMessage;

    /**
     * Construct a ServiceException.
     *
     * @param status       the HTTP status code
     * @param errorMessage the detail error message
     */
    public ServiceException(int status, String errorMessage) {
        this.status = status;
        this.errorMessage = errorMessage;
    }

    /**
     * Construct a ServiceException.
     *
     * @param status       the HTTP status code
     * @param code         the code defined in CPI
     * @param errorMessage the detail error message
     * @param cause        the cause
     */
    public ServiceException(int status, String errorMessage, Throwable cause) {
        super(cause);
        this.status = status;
        this.errorMessage = errorMessage;
    }

    /**
     * Returns the http status code.
     *
     * @return the http status code
     */
    public int getStatus() {
        return status;
    }

    /**
     * Returns the response message.
     *
     * @return the detail error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

}