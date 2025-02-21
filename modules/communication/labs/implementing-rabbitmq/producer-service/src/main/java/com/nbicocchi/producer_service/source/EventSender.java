package com.nbicocchi.producer_service.source;

import com.nbicocchi.producer_service.model.Event;

public interface EventSender {
    void randomMessage();
    void sendMessage(String bindingName, Event<String, String> event);
}
