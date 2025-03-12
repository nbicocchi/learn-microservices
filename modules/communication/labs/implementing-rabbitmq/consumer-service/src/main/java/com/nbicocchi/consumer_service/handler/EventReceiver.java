package com.nbicocchi.consumer.handler;

import com.nbicocchi.consumer.model.Event;

import java.util.function.Consumer;

public interface EventReceiver {
    Consumer<Event<String, String>> messageProcessor();
}
