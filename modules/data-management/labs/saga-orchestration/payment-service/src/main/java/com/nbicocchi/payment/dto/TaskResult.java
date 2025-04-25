package com.nbicocchi.payment.dto;

public record TaskResult(Result result, String reason) {

    public enum Result {
        PASS, FAIL
    }

}
