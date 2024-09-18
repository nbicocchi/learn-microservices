package cloud.macca.microservices.students.error;

import cloud.macca.microservices.students.dto.response.ErrorResponse;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalErrorHandler {

    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse<String> handle500(
            Exception e, WebRequest w
    ){
        return ErrorResponse.internalServerError();
    }

}
