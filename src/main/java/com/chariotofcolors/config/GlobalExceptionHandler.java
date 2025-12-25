package com.chariotofcolors.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Object handleException(Exception e, HttpServletRequest request) {
        // e.printStackTrace(); // Removed as per instruction

        String acceptHeader = request.getHeader("Accept");
        boolean isApiRequest = (acceptHeader != null && acceptHeader.contains("application/json")) ||
                "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));

        if (isApiRequest) {
            // Return JSON for API
            // For Chat/Violet, the service catches most errors, but if something else fails
            // here:
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal Server Error", "message",
                            "I am not feeling well, please try again later."));
        }

        // Return HTML Error Page for browser
        ModelAndView mav = new ModelAndView("error");
        return mav;
    }
}
