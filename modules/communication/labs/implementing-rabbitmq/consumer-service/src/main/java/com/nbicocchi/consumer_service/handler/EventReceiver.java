package com.nbicocchi.consumer_service.handler;

import com.nbicocchi.consumer_service.model.Event;

import java.util.function.Consumer;

public interface EventReceiver {
    Consumer<Event<String, String>> messageProcessor();
}
