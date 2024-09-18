package cloud.macca.microservices.grades.dto.response;

public class SuccessResponse<T> {
    private T result;
    private boolean success;

    public SuccessResponse(T item){
        this.result = item; this.success = true;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean getSuccess() {
        return success;
    }
    public void setResult(T result) {
        this.result = result;
    }
    public T getResult() {
        return result;
    }
}
