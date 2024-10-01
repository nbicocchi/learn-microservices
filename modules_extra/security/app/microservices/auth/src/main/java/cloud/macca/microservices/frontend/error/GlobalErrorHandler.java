package cloud.macca.microservices.frontend.error;

import cloud.macca.microservices.frontend.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalErrorHandler {
    @ExceptionHandler(value = ExpiredAccessTokenError.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String invalidToken(
            ExpiredAccessTokenError e, WebRequest w
    ){
        return "profile/invalid_token";
    }
    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse<String> handle500(
            Exception e, WebRequest w
    ){
        e.printStackTrace();
        return ErrorResponse.internalServerError();
    }
}
