package com.corenlp.training;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import com.corenlp.training.pojo.response.ErrorResponse;

@ControllerAdvice
public class GlobalControllerExceptionHandler {

    @ExceptionHandler({FileNotFoundException.class, IOException.class})
	@ResponseBody
    public ResponseEntity<ErrorResponse> handleFileNotFound() {
        ErrorResponse er = new ErrorResponse();
        er.setErrorMessage("Training File not found or could not be processed");
        return new ResponseEntity(er,HttpStatus.BAD_REQUEST);
    }
}
