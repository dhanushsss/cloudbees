package com.dhanush.cloudbees.service;

import com.dhanush.cloudbees.exception.TicketNotFoundException;
import com.dhanush.cloudbees.exception.UserNotFoundException;
import com.dhanush.cloudbees.model.Ticket;
import com.dhanush.cloudbees.model.User;
import com.dhanush.cloudbees.model.dto.TicketDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;


class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User testUser;
    private Ticket testTicket;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setName("John Doe");
        testUser.setEmail("john.doe@example.com");
        testTicket = new Ticket();
        testTicket.setId(UUID.randomUUID());
        testTicket.setFromStation("Boston");
        testTicket.setToStation("New York");
        testTicket.setPricePaid(150.0);
        testTicket.setSection("A");
        testTicket.setSeatNumber("A1");
        testTicket.setUser(testUser);
        bookingService.addUser(testUser);
    }

    @Test
    void testAddUser_Success() {
        User newUser = new User();
        newUser.setName("Jane Doe");
        newUser.setEmail("jane.doe@example.com");
        User addedUser = bookingService.addUser(newUser);
        assertNotNull(addedUser.getId());
        assertEquals("Jane Doe", addedUser.getName());
        assertEquals("jane.doe@example.com", addedUser.getEmail());
    }

    @Test
    void testGetUserByEmail_Success() {
        Optional<User> user = bookingService.getUserByEmail("john.doe@example.com");
        assertTrue(user.isPresent());
        assertEquals("john.doe@example.com", user.get().getEmail());
    }

    @Test
    void testGetUserByEmail_NotFound() {
        Optional<User> user = bookingService.getUserByEmail("non.existent@example.com");
        assertFalse(user.isPresent());
    }

    @Test
    void testPurchaseTicket_Success() {
        Ticket ticket = new Ticket();
        ticket.setFromStation("Los Angeles");
        ticket.setToStation("San Francisco");
        ticket.setPricePaid(200.0);
        ticket.setSection("B");
        ticket.setUser(testUser);
        TicketDTO purchasedTicket = bookingService.purchaseTicket(ticket);
        assertNotNull(purchasedTicket.getId());
        assertEquals("Los Angeles", purchasedTicket.getFromStation());
        assertEquals("San Francisco", purchasedTicket.getToStation());
        assertEquals("john.doe@example.com", purchasedTicket.getUserEmail());
    }

    @Test
    void testPurchaseTicket_UserNotFound() {
        Ticket ticket = new Ticket();
        ticket.setFromStation("Chicago");
        ticket.setToStation("Houston");
        ticket.setPricePaid(300.0);
        ticket.setSection("C");
        User nonExistentUser = new User();
        nonExistentUser.setEmail("non.existent@example.com");
        ticket.setUser(nonExistentUser);
        assertThrows(UserNotFoundException.class, () -> bookingService.purchaseTicket(ticket));
    }

    @Test
    void testGetTicketDetails_Success() {

        bookingService.purchaseTicket(testTicket);
        Ticket ticketDetails = bookingService.getTicketDetails("john.doe@example.com");
        assertNotNull(ticketDetails);
        assertEquals("Boston", ticketDetails.getFromStation());
        assertEquals("New York", ticketDetails.getToStation());
    }

    @Test
    void testGetTicketDetails_TicketNotFound() {
        assertThrows(TicketNotFoundException.class, () -> bookingService.getTicketDetails("no.ticket@example.com"));
    }

    @Test
    void testGetAllUsersBySection_Success() {
        bookingService.purchaseTicket(testTicket);
        List<Ticket> tickets = bookingService.getAllUsersBySection("A");
        assertNotNull(tickets);
        assertEquals(1, tickets.size());
        assertEquals("A", tickets.get(0).getSection());
    }

    @Test
    void testGetAllUsersBySection_TicketNotFound() {
        assertThrows(TicketNotFoundException.class, () -> bookingService.getAllUsersBySection("Z"));
    }

    @Test
    void testGetAllTicketsByUser_Success() {
        bookingService.purchaseTicket(testTicket);
        List<Ticket> tickets = bookingService.getAllTicketsByUser("john.doe@example.com");
        assertNotNull(tickets);
        assertEquals(1, tickets.size());
    }

    @Test
    void testGetAllTicketsByUser_TicketNotFound() {
        assertThrows(TicketNotFoundException.class, () -> bookingService.getAllTicketsByUser("non.existent@example.com"));
    }

    @Test
    void testModifySeat_Success() {
        bookingService.purchaseTicket(testTicket);
        Ticket modifiedTicket = bookingService.modifySeat("john.doe@example.com", "B2");
        assertNotNull(modifiedTicket);
        assertEquals("B2", modifiedTicket.getSeatNumber());
    }

    @Test
    void testModifySeat_TicketNotFound() {
        assertThrows(TicketNotFoundException.class, () -> bookingService.modifySeat("non.existent@example.com", "C3"));
    }

    @Test
    void testRemoveUserFromTrain_Success() {
        bookingService.purchaseTicket(testTicket);
        boolean isRemoved = bookingService.removeUserFromTrain("john.doe@example.com");
        assertTrue(isRemoved);
    }

    @Test
    void testRemoveUserFromTrain_UserNotFound() {
        assertThrows(UserNotFoundException.class, () -> bookingService.removeUserFromTrain("non.existent@example.com"));
    }
}
