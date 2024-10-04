package com.dhanush.cloudbees.service;

import com.dhanush.cloudbees.model.Ticket;
import com.dhanush.cloudbees.model.User;
import com.dhanush.cloudbees.model.dto.TicketDTO;

import java.util.List;
import java.util.Optional;

public interface BookingService {
    TicketDTO purchaseTicket(Ticket ticket);
    Ticket getTicketDetails(String email);
    List<Ticket> getAllUsersBySection(String section);
    List<Ticket> getAllTicketsByUser(String email);
    Ticket modifySeat(String email, String newSeat);
    boolean removeUserFromTrain(String email);
    User addUser(User user);
    Optional<User> getUserByEmail(String email);
}
