package cloud.macca.microservices.frontend.dto;

import org.springframework.http.HttpStatus;

public class ErrorResponse<T> {
    private T error;
    private HttpStatus status;
    private boolean success;

    public ErrorResponse(HttpStatus status, T item){
        this.error = item;
        this.status = status;
        this.success = false;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean getSuccess() {
        return success;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public T getError() {
        return error;
    }

    public void setError(T error) {
        this.error = error;
    }

    public static ErrorResponse<String> internalServerError(){
        return new ErrorResponse<String>(HttpStatus.INTERNAL_SERVER_ERROR, "fatal_error");
    }

}

