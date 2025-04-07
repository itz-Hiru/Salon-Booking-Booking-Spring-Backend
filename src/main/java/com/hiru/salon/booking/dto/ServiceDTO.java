package com.hiru.salon.booking.dto;

import lombok.Data;

@Data
public class ServiceDTO {
    private Long id;
    private String name;
    private String description;
    private double price;
    private int duration;
    private Long salonId;
    private Long categoryId;
    private String image;
}
