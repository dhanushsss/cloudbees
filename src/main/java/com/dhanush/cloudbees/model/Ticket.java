package com.dhanush.cloudbees.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(name = "from_station")
    private String fromStation;
    @Column(name = "to_station")
    private String toStation;
    private double pricePaid;
    private String section;
    private String seatNumber;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
