package ru.practicum.ewm.user.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.event.model.Status;

import java.time.ZonedDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "participation_requests")
@Data
public class ParticipationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private ZonedDateTime created;
    @Column(nullable = false)
    private Long event;
    @Column(nullable = false)
    private Long requester;
    @Column(nullable = false)
    private Status status;
}
