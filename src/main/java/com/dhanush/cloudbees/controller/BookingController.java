package com.dhanush.cloudbees.controller;

import com.dhanush.cloudbees.model.Ticket;
import com.dhanush.cloudbees.model.User;
import com.dhanush.cloudbees.model.dto.TicketDTO;
import com.dhanush.cloudbees.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tickets")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping("/add")
    public ResponseEntity<User> addUser(@RequestBody User user) {
        Optional<User> existingUser = bookingService.getUserByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
        User createdUser = bookingService.addUser(user);
        return ResponseEntity.ok(createdUser);
    }

    @PostMapping("/purchase")
    public ResponseEntity<TicketDTO> purchaseTicket(@RequestBody Ticket ticket) {
        return ResponseEntity.ok(bookingService.purchaseTicket(ticket));
    }

    @GetMapping("/{email}")
    public ResponseEntity<Ticket> getTicketDetails(@PathVariable String email) {
        return ResponseEntity.ok(bookingService.getTicketDetails(email));
    }

    @GetMapping("/section/{section}")
    public ResponseEntity<List<Ticket>> getUsersBySection(@PathVariable String section) {
        return ResponseEntity.ok(bookingService.getAllUsersBySection(section));
    }

    @GetMapping("/user/{email}")
    public ResponseEntity<List<Ticket>> getAllTicketsByUser(@PathVariable String email) {
        return ResponseEntity.ok(bookingService.getAllTicketsByUser(email));
    }

    @PutMapping("/modify-seat/{email}")
    public ResponseEntity<Ticket> modifySeat(@PathVariable String email, @RequestParam String newSeat) {
        return ResponseEntity.ok(bookingService.modifySeat(email, newSeat));
    }

    @DeleteMapping("/remove/{email}")
    public ResponseEntity<String> removeUserFromTrain(@PathVariable String email) {
        boolean removed = bookingService.removeUserFromTrain(email);
        return ResponseEntity.ok(removed ? "User removed successfully." : "User not found.");
    }
}
