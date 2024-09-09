/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package io.project.gate.responses;

/**
 *
 * @author Armen Arzumanyan
 */
public class BaseResult {

    private BaseResult() {
    }

    public static BaseResponse<String> success(String data) {
        return new BaseResponse<>(ErrorCode.SUCCESS.getCode(), data, ErrorCode.SUCCESS.getMessage());
    }

    /**
     * Error response
     *
     * @param errorCode custom errorCode
     * @return error response
     */
    public static BaseResponse error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode);
    }

    /**
     * Error response
     *
     * @param code custom status code
     * @param message custom message
     * @return error response
     */
    public static BaseResponse error(int code, String message) {
        return new BaseResponse<>(code, "No data for this request", message);
    }

    /**
     * Error response
     *
     * @param errorCode custom errorCode
     * @param message custom message
     * @return error response
     */
    public static BaseResponse error(ErrorCode errorCode, String message) {
        return new BaseResponse<>(errorCode, message);
    }
}