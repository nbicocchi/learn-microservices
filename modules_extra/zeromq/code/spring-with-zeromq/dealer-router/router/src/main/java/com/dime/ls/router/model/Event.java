package com.dime.ls.router.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Event<K, T> {

    public enum Type {CREATE, DELETE, UPDATE}

    @JsonProperty("eventType") private Type eventType;

    @JsonProperty("key") private K key;

    @JsonProperty("data") private T data;

    @Builder.Default
    @JsonProperty("eventCreatedAt")
    private ZonedDateTime eventCreatedAt = ZonedDateTime.now();
}
