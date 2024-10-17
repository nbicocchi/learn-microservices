package com.nbicocchi.sync_service.source;

import com.nbicocchi.sync_service.model.Event;

public interface EventSender {
    void sendMessage(String bindingName, Event<String, Integer> event);
}
