package com.nbicocchi.order.pojos;

public record TaskResult(Result result, String reason) {

    public enum Result {
        PASS, FAIL
    }

}
