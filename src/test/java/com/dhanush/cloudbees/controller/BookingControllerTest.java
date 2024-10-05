package com.dhanush.cloudbees.controller;

import com.dhanush.cloudbees.model.Ticket;
import com.dhanush.cloudbees.model.User;
import com.dhanush.cloudbees.model.dto.TicketDTO;
import com.dhanush.cloudbees.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    private User testUser;
    private Ticket testTicket;
    private TicketDTO testTicketDTO;

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
        testTicketDTO = new TicketDTO();
        testTicketDTO.setId(testTicket.getId());
        testTicketDTO.setFromStation("Boston");
        testTicketDTO.setToStation("New York");
        testTicketDTO.setUserEmail(testUser.getEmail());
        testTicketDTO.setPricePaid(150.0);
        testTicketDTO.setSection("A");
        testTicketDTO.setSeatNumber("A1");
    }

    @Test
    void testAddUser_Success() throws Exception {
        when(bookingService.getUserByEmail(testUser.getEmail())).thenReturn(Optional.empty());
        when(bookingService.addUser(any(User.class))).thenReturn(testUser);

        mockMvc.perform(post("/api/tickets/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"name\": \"John Doe\", \"email\": \"john.doe@example.com\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    void testAddUser_Conflict() throws Exception {
        when(bookingService.getUserByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));

        mockMvc.perform(post("/api/tickets/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"name\": \"John Doe\", \"email\": \"john.doe@example.com\" }"))
                .andExpect(status().isConflict());
    }

    @Test
    void testPurchaseTicket_Success() throws Exception {
        when(bookingService.purchaseTicket(any(Ticket.class))).thenReturn(testTicketDTO);

        mockMvc.perform(post("/api/tickets/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"fromStation\": \"Boston\", \"toStation\": \"New York\", \"pricePaid\": 150.0, \"section\": \"A\", \"user\": { \"email\": \"john.doe@example.com\" }}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fromStation").value("Boston"))
                .andExpect(jsonPath("$.toStation").value("New York"))
                .andExpect(jsonPath("$.userEmail").value("john.doe@example.com"));
    }

    @Test
    void testGetTicketDetails_Success() throws Exception {
        when(bookingService.getTicketDetails(anyString())).thenReturn(testTicket);
        mockMvc.perform(get("/api/tickets/john.doe@example.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fromStation").value("Boston"))
                .andExpect(jsonPath("$.toStation").value("New York"));
    }

    @Test
    void testGetUsersBySection_Success() throws Exception {
        when(bookingService.getAllUsersBySection(anyString())).thenReturn(Collections.singletonList(testTicket));
        mockMvc.perform(get("/api/tickets/section/A")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].section").value("A"));
    }

    @Test
    void testGetAllTicketsByUser_Success() throws Exception {
        when(bookingService.getAllTicketsByUser(anyString())).thenReturn(Collections.singletonList(testTicket));

        mockMvc.perform(get("/api/tickets/user/john.doe@example.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].user.email").value("john.doe@example.com"));
    }


    @Test
    void testRemoveUserFromTrain_Success() throws Exception {
        when(bookingService.removeUserFromTrain(anyString())).thenReturn(true);
        mockMvc.perform(delete("/api/tickets/remove/john.doe@example.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("User removed successfully."));
    }

    @Test
    void testRemoveUserFromTrain_UserNotFound() throws Exception {
        when(bookingService.removeUserFromTrain(anyString())).thenReturn(false);
        mockMvc.perform(delete("/api/tickets/remove/john.doe@example.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("User not found."));
    }
}
