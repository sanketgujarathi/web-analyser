package boot.controller;

import boot.excpetion.PageAnalyserException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolationException;

/**
 * Created by sanketg on 4/16/2017.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Common handler method to elegantly handle exceptions occurred during processing and return appropriate message as response
     * @param e Exception to handle
     * @return Error message to be displayed
     */
    @ExceptionHandler(PageAnalyserException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handle(PageAnalyserException e){
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unable to process request: " + e.getMessage());
    }

    /**
     * Common handler method to elegantly handle validation errors and return appropriate message as response
     * @param e
     * @return
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handle(ConstraintViolationException e){
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unable to process request: Invalid URL!");
    }

}
