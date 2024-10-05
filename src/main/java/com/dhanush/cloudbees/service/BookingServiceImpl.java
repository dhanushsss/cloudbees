package com.dhanush.cloudbees.service;

import com.dhanush.cloudbees.exception.TicketNotFoundException;
import com.dhanush.cloudbees.exception.UserNotFoundException;
import com.dhanush.cloudbees.model.Ticket;
import com.dhanush.cloudbees.model.User;
import com.dhanush.cloudbees.model.dto.TicketDTO;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {
    private final Map<UUID, User> users = new HashMap<>();
    private final Map<UUID, Ticket> tickets = new HashMap<>();
    private final Map<String, UUID> emailToUserIdMap = new HashMap<>();

    @Override
    public TicketDTO purchaseTicket(Ticket ticket) {

        UUID userId = emailToUserIdMap.get(ticket.getUser().getEmail());
        if (userId == null) {
            throw new UserNotFoundException("User not found with email: " + ticket.getUser().getEmail());
        }

        ticket.setUser(users.get(userId));
        ticket.setSeatNumber(assignSeat(ticket.getSection()));
        ticket.setId(UUID.randomUUID());
        tickets.put(ticket.getId(), ticket);
        return convertToDTO(ticket);
    }

    @Override
    public Ticket getTicketDetails(String email) {
        List<Ticket> userTickets = tickets.values().stream()
                .filter(ticket -> ticket.getUser().getEmail().equals(email))
                .collect(Collectors.toList());

        if (userTickets.isEmpty()) {
            throw new TicketNotFoundException("Ticket not found for email: " + email);
        }
        return userTickets.get(0);
    }

    @Override
    public List<Ticket> getAllUsersBySection(String section) {
        List<Ticket> sectionTickets = tickets.values().stream()
                .filter(ticket -> ticket.getSection().equalsIgnoreCase(section))
                .collect(Collectors.toList());

        if (sectionTickets.isEmpty()) {
            throw new TicketNotFoundException("No tickets found for section: " + section);
        }
        return sectionTickets;
    }

    @Override
    public List<Ticket> getAllTicketsByUser(String email) {
        List<Ticket> userTickets = tickets.values().stream()
                .filter(ticket -> ticket.getUser().getEmail().equals(email))
                .collect(Collectors.toList());

        if (userTickets.isEmpty()) {
            throw new TicketNotFoundException("No tickets found for user email: " + email);
        }
        return userTickets;
    }

    @Override
    public Ticket modifySeat(String email, String newSeat) {
        List<Ticket> userTickets = tickets.values().stream()
                .filter(ticket -> ticket.getUser().getEmail().equals(email))
                .collect(Collectors.toList());

        if (userTickets.isEmpty()) {
            throw new TicketNotFoundException("Ticket not found for email: " + email);
        }

        Ticket ticket = userTickets.get(0);
        ticket.setSeatNumber(newSeat);
        tickets.put(ticket.getId(), ticket);
        return ticket;
    }

    @Override
    public boolean removeUserFromTrain(String email) {
        List<UUID> ticketIdsToRemove = tickets.values().stream()
                .filter(ticket -> ticket.getUser().getEmail().equals(email))
                .map(Ticket::getId)
                .collect(Collectors.toList());

        if (ticketIdsToRemove.isEmpty()) {
            throw new UserNotFoundException("User not found with email: " + email);
        }
        for (UUID ticketId : ticketIdsToRemove) {
            tickets.remove(ticketId);
        }
        return true;
    }

    @Override
    public User addUser(User user) {
        if (user.getId() == null) {
            user.setId(UUID.randomUUID());
        }
        users.put(user.getId(), user);
        emailToUserIdMap.put(user.getEmail(), user.getId());
        return user;
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        UUID userId = emailToUserIdMap.get(email);
        return Optional.ofNullable(users.get(userId));
    }

    private String assignSeat(String section) {
        return section.equalsIgnoreCase("A") ? "A1" : "B1";
    }

    private TicketDTO convertToDTO(Ticket ticket) {
        TicketDTO dto = new TicketDTO();
        dto.setId(ticket.getId());
        dto.setFromStation(ticket.getFromStation());
        dto.setToStation(ticket.getToStation());
        dto.setUserEmail(ticket.getUser().getEmail());
        dto.setPricePaid(ticket.getPricePaid());
        dto.setSection(ticket.getSection());
        dto.setSeatNumber(ticket.getSeatNumber());
        return dto;
    }
}
