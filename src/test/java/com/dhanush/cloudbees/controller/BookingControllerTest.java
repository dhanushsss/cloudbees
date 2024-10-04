package com.dhanush.cloudbees.controller;

import com.dhanush.cloudbees.model.Ticket;
import com.dhanush.cloudbees.model.User;
import com.dhanush.cloudbees.model.dto.TicketDTO;
import com.dhanush.cloudbees.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

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
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setName("Dhanush");
        testUser.setEmail("dhanu@gmail.com");
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

        mockMvc.perform(post("/api/v1/tickets/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"name\": \"Dhanush\", \"email\": \"dhanu@gmail.com\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("dhanu@gmail.com"))
                .andExpect(jsonPath("$.name").value("Dhanush"));
    }

    @Test
    void testAddUser_Conflict() throws Exception {
        when(bookingService.getUserByEmail(anyString())).thenReturn(Optional.of(testUser));

        mockMvc.perform(post("/api/v1/tickets/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"name\": \"Dhanush\", \"email\": \"dhanu@gmail.com\" }"))
                .andExpect(status().isConflict());
    }

    @Test
    void testPurchaseTicket_Success() throws Exception {
        when(bookingService.purchaseTicket(any(Ticket.class))).thenReturn(testTicketDTO);

        mockMvc.perform(post("/api/v1/tickets/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"fromStation\": \"Boston\", \"toStation\": \"New York\", \"pricePaid\": 150.0, \"section\": \"A\", \"user\": { \"email\": \"dhanu@gmail.com\" }}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fromStation").value("Boston"))
                .andExpect(jsonPath("$.toStation").value("New York"))
                .andExpect(jsonPath("$.userEmail").value("dhanu@gmail.com"));
    }

    @Test
    void testGetTicketDetails_Success() throws Exception {
        when(bookingService.getTicketDetails(anyString())).thenReturn(testTicket);

        mockMvc.perform(get("/api/v1/tickets/dhanu@gmail.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fromStation").value("Boston"))
                .andExpect(jsonPath("$.toStation").value("New York"))
                .andExpect(jsonPath("$.pricePaid").value(150.0));
    }

    @Test
    void testGetUsersBySection_Success() throws Exception {
        when(bookingService.getAllUsersBySection(anyString())).thenReturn(Collections.singletonList(testTicket));

        mockMvc.perform(get("/api/v1/tickets/section/A")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].section").value("A"));
    }

    @Test
    void testGetAllTicketsByUser_Success() throws Exception {
        when(bookingService.getAllTicketsByUser(anyString())).thenReturn(Collections.singletonList(testTicket));

        mockMvc.perform(get("/api/v1/tickets/user/john.doe@example.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].user.email").value("dhanu@gmail.com"));
    }

    @Test
    void testRemoveUserFromTrain_Success() throws Exception {
        when(bookingService.removeUserFromTrain(anyString())).thenReturn(true);
        mockMvc.perform(delete("/api/v1/tickets/remove/dhanu@gmail.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("User removed successfully."));
    }

    @Test
    void testRemoveUserFromTrain_NotFound() throws Exception {
        when(bookingService.removeUserFromTrain(anyString())).thenReturn(false);
        mockMvc.perform(delete("/api/v1/tickets/remove/dhanu@gmail.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("User not found."));
    }
}
