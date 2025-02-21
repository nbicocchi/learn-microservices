package cloud.macca.microservices.frontend.error;

import cloud.macca.microservices.frontend.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalRestErrorHandler {

    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse<String> handle500(
            Exception e, WebRequest w
    ){
        return ErrorResponse.internalServerError();
    }
    @ExceptionHandler(value = AuthorizationBadRequestError.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse<String> cannotAuth(
            AuthorizationBadRequestError e, WebRequest w
    ){
        return new ErrorResponse<String>(HttpStatus.BAD_REQUEST, "cannot_authenticate: " + e.getMessage());
    }

}
