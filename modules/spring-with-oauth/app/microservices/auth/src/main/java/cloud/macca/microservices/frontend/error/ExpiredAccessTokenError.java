package cloud.macca.microservices.frontend.error;

public class ExpiredAccessTokenError extends RuntimeException {
    public ExpiredAccessTokenError(String r){
        super(r);
    }
}
