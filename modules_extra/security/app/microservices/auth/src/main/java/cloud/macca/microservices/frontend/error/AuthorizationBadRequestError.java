package cloud.macca.microservices.frontend.error;

public class AuthorizationBadRequestError extends RuntimeException {
    public AuthorizationBadRequestError(String r){
        super(r);
    }
}
