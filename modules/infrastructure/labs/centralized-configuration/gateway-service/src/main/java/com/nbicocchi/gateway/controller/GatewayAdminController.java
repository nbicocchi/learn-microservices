package com.nbicocchi.gateway.controller;

import com.nbicocchi.gateway.service.ServiceRefresher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class GatewayAdminController {
    private final ServiceRefresher serviceRefresher;

    public GatewayAdminController(ServiceRefresher serviceRefresher) {
        this.serviceRefresher = serviceRefresher;
    }

    @PostMapping("/refresh-all")
    public String refreshAllServices() {
        serviceRefresher.refreshAllServices();
        return "All services refreshed via Gateway!";
    }
}
