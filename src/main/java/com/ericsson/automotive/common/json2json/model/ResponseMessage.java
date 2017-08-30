package com.ericsson.automotive.common.json2json.model;

/**
 * Created by ejaiwng on 8/30/2017.
 */
public class ResponseMessage {
    private Integer code = null;

    private String message = null;

    public ResponseMessage(Integer code, String message) {
        this.code = code;
        this.message = message;
    }


    /**
     * Get code
     *
     * @return code
     **/
    public Integer getCode() {
        return code;
    }


    /**
     * Get message
     *
     * @return message
     **/
    public String getMessage() {
        return message;
    }
}
