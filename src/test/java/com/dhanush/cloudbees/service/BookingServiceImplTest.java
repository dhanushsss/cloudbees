package com.dhanush.cloudbees.service;

import com.dhanush.cloudbees.exception.TicketNotFoundException;
import com.dhanush.cloudbees.exception.UserNotFoundException;
import com.dhanush.cloudbees.model.Ticket;
import com.dhanush.cloudbees.model.User;
import com.dhanush.cloudbees.model.dto.TicketDTO;
import com.dhanush.cloudbees.repository.TicketRepository;
import com.dhanush.cloudbees.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingServiceImplTest {

    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private BookingServiceImpl bookingService;

    private User testUser;
    private Ticket testTicket;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setEmail("dhanu@gmail.com");
        testUser.setName("Dhanush");
        testTicket = new Ticket();
        testTicket.setId(UUID.randomUUID());
        testTicket.setFromStation("Boston");
        testTicket.setToStation("New York");
        testTicket.setPricePaid(150.0);
        testTicket.setSection("A");
        testTicket.setSeatNumber("A1");
        testTicket.setUser(testUser);
    }

    @Test
    void testPurchaseTicket_Success() {
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(testTicket);
        TicketDTO purchasedTicket = bookingService.purchaseTicket(testTicket);
        assertNotNull(purchasedTicket);
        assertEquals(testTicket.getId(), purchasedTicket.getId());
        assertEquals(testTicket.getFromStation(), purchasedTicket.getFromStation());
        assertEquals(testTicket.getUser().getEmail(), purchasedTicket.getUserEmail());
        verify(ticketRepository, times(1)).save(testTicket);
    }

    @Test
    void testPurchaseTicket_UserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> bookingService.purchaseTicket(testTicket));
    }

    @Test
    void testGetTicketDetails_Success() {
        when(ticketRepository.findAllByUserEmail(testUser.getEmail())).thenReturn(Collections.singletonList(testTicket));
        Ticket ticketDetails = bookingService.getTicketDetails(testUser.getEmail());
        assertNotNull(ticketDetails);
        assertEquals(testTicket.getId(), ticketDetails.getId());
        verify(ticketRepository, times(1)).findAllByUserEmail(testUser.getEmail());
    }

    @Test
    void testGetTicketDetails_TicketNotFound() {
        when(ticketRepository.findAllByUserEmail(anyString())).thenReturn(Collections.emptyList());
        assertThrows(TicketNotFoundException.class, () -> bookingService.getTicketDetails(testUser.getEmail()));
    }

    @Test
    void testGetAllUsersBySection_Success() {
        when(ticketRepository.findBySection("A")).thenReturn(Collections.singletonList(testTicket));
        var tickets = bookingService.getAllUsersBySection("A");
        assertEquals(1, tickets.size());
        assertEquals(testTicket.getSection(), tickets.get(0).getSection());
    }

    @Test
    void testGetAllTicketsByUser_Success() {
        when(ticketRepository.findAllByUserEmail(testUser.getEmail())).thenReturn(Collections.singletonList(testTicket));
        var tickets = bookingService.getAllTicketsByUser(testUser.getEmail());
        assertEquals(1, tickets.size());
        assertEquals(testTicket.getUser().getEmail(), tickets.get(0).getUser().getEmail());
    }

    @Test
    void testModifySeat_Success() {
        when(ticketRepository.findAllByUserEmail(testUser.getEmail())).thenReturn(Collections.singletonList(testTicket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(testTicket);
        Ticket modifiedTicket = bookingService.modifySeat(testUser.getEmail(), "B2");
        assertNotNull(modifiedTicket);
        assertEquals("B2", modifiedTicket.getSeatNumber());
    }

    @Test
    void testRemoveUserFromTrain_Success() {
        when(ticketRepository.findAllByUserEmail(testUser.getEmail())).thenReturn(Collections.singletonList(testTicket));
        boolean isRemoved = bookingService.removeUserFromTrain(testUser.getEmail());
        assertTrue(isRemoved);
        verify(ticketRepository, times(1)).deleteAll(anyList());
    }

    @Test
    void testRemoveUserFromTrain_UserNotFound() {
        when(ticketRepository.findAllByUserEmail(anyString())).thenReturn(Collections.emptyList());
        assertThrows(UserNotFoundException.class, () -> bookingService.removeUserFromTrain(testUser.getEmail()));
    }
}
