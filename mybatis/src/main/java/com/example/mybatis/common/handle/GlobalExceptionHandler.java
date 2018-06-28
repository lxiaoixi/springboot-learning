package com.example.mybatis.common.handle;

import com.example.mybatis.common.exception.ParamsInvalidException;
import com.example.mybatis.common.response.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.UnsupportedEncodingException;

@RestControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity handleNotFound() {
        ErrorResponse errorResponse = new ErrorResponse(1001, "路由不存在");
        return new ResponseEntity<Object>(errorResponse, HttpStatus.NOT_FOUND);
    }

//    @ExceptionHandler(AuthenticationException.class)
//    @ResponseStatus(HttpStatus.UNAUTHORIZED)
//    public ResponseEntity handleShiroAuthentication() {
//        ErrorResponse errorResponse = new ErrorResponse(1002, "未授权");
//        return new ResponseEntity<Object>(errorResponse, HttpStatus.BAD_REQUEST);
//    }
//    @ExceptionHandler(AuthorizationException.class)
//    @ResponseStatus(HttpStatus.UNAUTHORIZED)
//    public ResponseEntity handleShiroUnAuthorized() {
//        ErrorResponse errorResponse = new ErrorResponse(1002, "未授权");
//        return new ResponseEntity<Object>(errorResponse, HttpStatus.BAD_REQUEST);
//    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity handleBindException(BindException e) {
        BindingResult result = e.getBindingResult();
        return handleException(result);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity handleRequestBodyInvalid(MethodArgumentNotValidException e) {
        BindingResult result = e.getBindingResult();
        return handleException(result);
    }

    private ResponseEntity handleException(BindingResult result){
        FieldError fieldError = result.getFieldError();
        String field = fieldError.getField();
        String error = fieldError.getDefaultMessage();
        int code = 1000;
        try {
            error = new String(error.getBytes("ISO-8859-1"),"utf-8");
            String[] arr = error.split("::");
            if(arr.length<2){
                code =1007;
                error="错误信息或错误码不存在";
            }else{
                code = Integer.valueOf(arr[0]);
                error = arr[1];
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ErrorResponse errorResponse = new ErrorResponse(code, field + error);
        return new ResponseEntity<Object>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        logger.warn("请求body格式错误: ", e);
        ErrorResponse errorResponse = new ErrorResponse(1003,"请求body格式错误");
        return new ResponseEntity<Object>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ParamsInvalidException.class)
    public ResponseEntity handleCommonException(ParamsInvalidException e) {
        logger.warn("参数校验错误: ", e);
        ErrorResponse errorResponse = new ErrorResponse(e.getCode(), e.getMessage());
        return new ResponseEntity<Object>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        logger.warn("不支持的请求方法: ", e);
        ErrorResponse errorResponse = new ErrorResponse(1004,"不支持的请求方法");
        return new ResponseEntity<Object>(errorResponse, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ResponseEntity handleException(Exception e) {
        logger.error("系统错误: ", e);
        ErrorResponse errorResponse = new ErrorResponse(1005, "系统繁忙");
        return new ResponseEntity<Object>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
