package pl.agh.order.management.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.stream.Collectors;

import static java.lang.String.format;

@ControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CustomExceptionResponse handleBadRequestException(MethodArgumentNotValidException e) {
        String errorString = e.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> format("%s=[%s] -> %s", error.getField(), error.getRejectedValue(), error.getDefaultMessage()))
                .collect(Collectors.joining(", "));
        return new CustomExceptionResponse(errorString);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class CustomExceptionResponse {
        private String error;
    }
}
