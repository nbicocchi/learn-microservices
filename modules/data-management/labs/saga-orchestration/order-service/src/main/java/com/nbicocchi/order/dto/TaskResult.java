package com.nbicocchi.order.dto;

public record TaskResult(Result result, String reason) {

    public enum Result {
        PASS, FAIL
    }

}
