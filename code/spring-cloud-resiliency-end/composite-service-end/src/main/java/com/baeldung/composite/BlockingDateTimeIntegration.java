package com.baeldung.composite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class BlockingDateTimeIntegration {
    private static final Logger LOG = LoggerFactory.getLogger(DateTimeIntegration.class);
    private static final String TIME_SERVICE_URL = "http://TIME-SERVICE/time";

    RestTemplate restTemplate;

    public BlockingDateTimeIntegration(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public LocalTime getTime(int delay, int faultPercent) {
        URI url = UriComponentsBuilder.fromUriString(TIME_SERVICE_URL + "?delay={delay}&faultPercent={faultPercent}").build(delay, faultPercent);

        LOG.info("Getting time on URL: {}", url);
        return restTemplate.getForObject(url, LocalTime.class);
    }

    public LocalDate getDate(int delay, int faultPercent) {
        URI url = UriComponentsBuilder.fromUriString("http://DATE-SERVICE/date?delay={delay}&faultPercent={faultPercent}")
                .build(delay, faultPercent);

        LOG.info("Getting date on URL: {}", url);
        return restTemplate.getForObject(url, LocalDate.class);
    }

}
