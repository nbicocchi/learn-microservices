package com.nbicocchi.inventory.dto;

public record TaskResult(Result result, String reason) {

    public enum Result {
        PASS, FAIL
    }

}
