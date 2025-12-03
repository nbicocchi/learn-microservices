package com.nbicocchi.notification.event;

import com.nbicocchi.math.event.Event;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Log4j2
@Component
public class EventSender {

    private final StreamBridge streamBridge;

    /**
     * Sends an event with optional headers.
     *
     * @param bindingName  the channel/binding name
     * @param event        the event payload
     * @param headers      optional headers as key-value pairs
     * @param <K>          key type
     * @param <V>          payload type
     */
    @SafeVarargs
    public final <K, V> void send(String bindingName, Event<K, V> event, Header<K,V>... headers) {
        MessageBuilder<Event<K, V>> builder = MessageBuilder.withPayload(event);

        // Apply all headers
        if (headers != null) {
            for (Header<K,V> h : headers) {
                builder.setHeader(h.name, h.value);
            }
        }

        Message<Event<K, V>> message = builder.build();
        streamBridge.send(bindingName, message);
    }

    /**
     * Simple holder for headers.
     */
    public record Header<K,V>(String name, Object value) {}
}
