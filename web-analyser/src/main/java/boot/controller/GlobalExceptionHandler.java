package boot.controller;

import boot.excpetion.PageAnalyserException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;

/**
 * Created by sanketg on 4/16/2017.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handle(PageAnalyserException e){
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unable to process request: " + e.getLocalizedMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handle(IOException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Unable to process request: Connectivity Issues");
    }
}
