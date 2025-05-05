package com.pearchInventory.ioa.utils;

import com.pearchInventory.ioa.enums.ResponseCode;
import lombok.Data;

@Data
public class Response {
    private String code;
    private String message;
    private GenericData<Object> data;

    public Response(ResponseCode responseCode) {
        this.code = responseCode.getCode();
        this.message = responseCode.getMessage();
    }

    public Response(ResponseCode responseCode, GenericData<Object> data) {
        this.code = responseCode.getCode();
        this.message = responseCode.getMessage();
        this.data = data;
    }

    public Response(String code, String message) {
        this.code = code;
        this.message = message;
    }
}