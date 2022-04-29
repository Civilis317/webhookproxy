package org.boip.util.webhookproxy.rest;

import lombok.extern.slf4j.Slf4j;
import org.boip.util.webhookproxy.exception.ApplicationException;
import org.boip.util.webhookproxy.exception.NotFoundException;
import org.boip.util.webhookproxy.exception.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public abstract class AbstractController {

    @ExceptionHandler
    protected void handleValidationException(ValidationException e, HttpServletResponse response) throws IOException {
        log.warn("validation failed: ");
        log.warn(e.getMessagesAsString());
        response.sendError(HttpStatus.BAD_REQUEST.value(), ""); // no info
    }

    @ExceptionHandler
    protected void handleNotFoundException(NotFoundException e, HttpServletResponse response) throws IOException {
        log.warn("NotFoundException: {}", e.getMessage());
        response.sendError(HttpStatus.NOT_FOUND.value(), "");  // no info
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<Object> handle(ApplicationException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
    }

}
