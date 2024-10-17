package com.nbicocchi.async_service.handler;

import com.nbicocchi.async_service.model.Event;

import java.util.function.Consumer;

public interface EventReceiver {
    Consumer<Event<String, String>> messageProcessor();
}
