package com.dhanush.cloudbees.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Ticket {
    private UUID id;
    private String fromStation;
    private String toStation;
    private double pricePaid;
    private String section;
    private String seatNumber;
    private User user;
}
