package com.nbicocchi.payment.pojos;

public record TaskResult(Result result, String reason) {

    public enum Result {
        PASS, FAIL
    }

}
