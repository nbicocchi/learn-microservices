package com.valentini.compositeservice.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class CustomErrorController implements ErrorController {

    /**
     * Handles errors and returns appropriate view based on the error status code.
     *
     * @param httpServletRequest the HttpServletRequest object
     * @return the view name for the error page
     */
    @GetMapping("/error")
    public String handleError(HttpServletRequest httpServletRequest) {
        Object statusCode = httpServletRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (statusCode != null && Integer.parseInt(statusCode.toString()) == HttpStatus.FORBIDDEN.value()) {
            return "error/403";
        } else if (statusCode != null && Integer.parseInt(statusCode.toString()) == HttpStatus.NOT_FOUND.value()) {
            return "error/404";
        }
        return "error/error";
    }
}
