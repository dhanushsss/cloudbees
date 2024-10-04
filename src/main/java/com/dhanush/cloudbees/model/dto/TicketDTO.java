package com.dhanush.cloudbees.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class TicketDTO {
    private UUID id;
    private String fromStation;
    private String toStation;
    private String userEmail;
    private double pricePaid;
    private String section;
    private String seatNumber;
}
