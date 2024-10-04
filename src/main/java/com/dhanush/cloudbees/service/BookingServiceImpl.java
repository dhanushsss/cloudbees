package com.dhanush.cloudbees.service;

import com.dhanush.cloudbees.exception.TicketNotFoundException;
import com.dhanush.cloudbees.exception.UserNotFoundException;
import com.dhanush.cloudbees.model.Ticket;
import com.dhanush.cloudbees.model.User;
import com.dhanush.cloudbees.model.dto.TicketDTO;
import com.dhanush.cloudbees.repository.TicketRepository;
import com.dhanush.cloudbees.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public TicketDTO purchaseTicket(Ticket ticket) {
        User user = userRepository.findByEmail(ticket.getUser().getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + ticket.getUser().getEmail()));
        ticket.setUser(user);
        ticket.setSeatNumber(assignSeat(ticket.getSection()));
        Ticket savedTicket = ticketRepository.save(ticket);
        return convertToDTO(savedTicket);
    }

    @Override
    public Ticket getTicketDetails(String email) {
        List<Ticket> tickets = ticketRepository.findAllByUserEmail(email);
        if (tickets.isEmpty()) {
            throw new TicketNotFoundException("Ticket not found for email: " + email);
        }
        return tickets.get(0);
    }

    @Override
    public List<Ticket> getAllUsersBySection(String section) {
        List<Ticket> tickets = ticketRepository.findBySection(section);
        if (tickets.isEmpty()) {
            throw new TicketNotFoundException("No tickets found for section: " + section);
        }
        return tickets;
    }

    @Override
    public List<Ticket> getAllTicketsByUser(String email) {
        List<Ticket> tickets = ticketRepository.findAllByUserEmail(email);
        if (tickets.isEmpty()) {
            throw new TicketNotFoundException("No tickets found for user email: " + email);
        }
        return tickets;
    }

    @Override
    public Ticket modifySeat(String email, String newSeat) {
        List<Ticket> tickets = ticketRepository.findAllByUserEmail(email);
        if (tickets.isEmpty()) {
            throw new TicketNotFoundException("Ticket not found for email: " + email);
        }
        Ticket ticket = tickets.get(0);
        ticket.setSeatNumber(newSeat);
        return ticketRepository.save(ticket);
    }

    @Override
    public boolean removeUserFromTrain(String email) {
        List<Ticket> tickets = ticketRepository.findAllByUserEmail(email);
        if (tickets.isEmpty()) {
            throw new UserNotFoundException("User not found with email: " + email);
        }
        ticketRepository.deleteAll(tickets);
        return true;
    }

    @Override
    public User addUser(User user) {
        if (user.getId() == null) {
            user.setId(UUID.randomUUID());
        }
        return userRepository.save(user);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    private String assignSeat(String section) {
        return section.equalsIgnoreCase("A") ? "A1" : "B1";
    }


    public TicketDTO convertToDTO(Ticket ticket) {
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
