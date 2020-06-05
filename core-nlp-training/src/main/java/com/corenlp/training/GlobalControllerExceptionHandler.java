package com.corenlp.training;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.corenlp.training.pojo.response.ErrorResponse;

@ControllerAdvice
public class GlobalControllerExceptionHandler {

	Logger logger = LoggerFactory.getLogger(GlobalControllerExceptionHandler.class);
	
    @ExceptionHandler(IOException.class)
	@ResponseBody
    public ResponseEntity<ErrorResponse> handleFileNotFound(HttpServletRequest request, IOException e) {
    	
    	logger.warn("Training File not found or could not be processed", e.getStackTrace());
    	
        ErrorResponse er = new ErrorResponse();
        er.setErrorMessage("Training File not found or could not be processed");
        return new ResponseEntity(er,HttpStatus.BAD_REQUEST);
    }
}
