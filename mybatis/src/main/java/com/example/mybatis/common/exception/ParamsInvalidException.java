package com.example.mybatis.common.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class ParamsInvalidException extends RuntimeException{
    private int code;
    private String message;
}
