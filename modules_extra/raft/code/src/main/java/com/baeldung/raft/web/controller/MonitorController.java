package com.baeldung.raft.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller responsible for serving the monitoring page of the Raft cluster.
 */
@Controller
public class MonitorController {

    /**
     * Handles GET requests to the "/monitor" endpoint.
     *
     * @return the name of the view to render for the monitoring page
     */
    @GetMapping("/monitor")
    public String monitorPage() {
        return "monitor";
    }
}
