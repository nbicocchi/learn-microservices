package cloud.macca.aggregator.dto;

import cloud.macca.aggregator.model.Grade;

public class GradesResponse {
    public final Grade[] result;
    public final boolean success;
    public GradesResponse(
            Grade[] result
    ){
        this.result = result;
        this.success = true;
    }
}
