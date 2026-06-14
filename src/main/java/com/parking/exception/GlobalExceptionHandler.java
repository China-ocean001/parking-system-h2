package com.parking.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model) {
        e.printStackTrace(); // Print stack trace to console
        model.addAttribute("errorMessage", e.getMessage());
        model.addAttribute("stackTrace", e.toString());
        return "error";
    }
}
