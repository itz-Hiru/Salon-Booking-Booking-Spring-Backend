package com.hiru.salon.booking.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    @GetMapping
    public String HomeControllerHandler() {
        return "Booking microservice of salon booking system started successfully!";
    }
}
