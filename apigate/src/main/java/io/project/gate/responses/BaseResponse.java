/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package io.project.gate.responses;

/**
 *
 * @author Armen Arzumanyan
 */
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
public class BaseResponse<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = -4702764798156570113L;

    private int code;

    private String data;

    private String message;

    /**
     * BaseResponse with code, data, message
     *
     * @param code status code
     * @param data response data
     * @param message response message
     */
    public BaseResponse(int code, String data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    /**
     * BaseResponse with code, data, but no message
     *
     * @param code status code
     * @param data response data
     */
    public BaseResponse(int code, String data) {
        this.code = code;
        this.data = data;
        this.message = "";
    }

    /**
     * BaseResponse with custom errorCode, including code and message but no
     * data
     *
     * @param errorCode custom errorCode
     */
    public BaseResponse(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.data = "No Data";
        this.message = errorCode.getMessage();
    }

    /**
     * BaseResponse with custom errorCode, including code and message but no
     * data
     *
     * @param errorCode custom errorCode
     * @param message custom message
     */
    public BaseResponse(ErrorCode errorCode, String message) {
        this.code = errorCode.getCode();
        this.data = "No Data";
        this.message = message;
    }
    
}
    