package com.nbicocchi.sync_service.controller;

import com.nbicocchi.sync_service.model.Event;
import com.nbicocchi.sync_service.source.EventSenderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.UUID;
import java.util.random.RandomGenerator;

@RestController
public class ApiControllerImpl implements ApiController {
    String providerServiceUrl;
    private static final Logger LOG = LoggerFactory.getLogger(ApiControllerImpl.class);
    private final EventSenderImpl eventSender;
    private static final RandomGenerator RANDOM = RandomGenerator.getDefault();

    public ApiControllerImpl(
            @Value("${app.ext-api-service.host}") String providerServiceHost,
            @Value("${app.ext-api-service.port}") int providerServicePort, EventSenderImpl eventSender) {
        providerServiceUrl = "http://" + providerServiceHost + ":" + providerServicePort + "/number";
        this.eventSender = eventSender;
    }

    @GetMapping("/process")
    public String process() {
        RestClient restClient = RestClient.builder().build();
        try {
            Integer response = restClient.get()
                    .uri(providerServiceUrl)
                    .retrieve()
                    .body(Integer.class);

            int index = RANDOM.nextInt(Event.Type.class.getEnumConstants().length);
            Event<String, Integer> event = new Event(
                    Event.Type.class.getEnumConstants()[index],
                    UUID.randomUUID().toString(),
                    response*2
            );

            LOG.info("[SENDING] -> {} to {}", event, "message-out-0");
            eventSender.sendMessage("message-out-0", event);
            return ("Data retrieved successfully from API and sent to message queue");

        } catch (RestClientException e) {
            LOG.error("Error calling REST API");
            return ("Error calling REST API");
        }
    }
}