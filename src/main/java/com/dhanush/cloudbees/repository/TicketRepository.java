package com.dhanush.cloudbees.repository;

import com.dhanush.cloudbees.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    List<Ticket> findAllByUserEmail(String email);
    List<Ticket> findBySection(String section);
}
