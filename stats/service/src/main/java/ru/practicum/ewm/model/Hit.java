package ru.practicum.ewm.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "hits")
@Data
public class Hit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private String app;
    @Column(nullable = false, length = 1024)
    private String uri;
    @Column(nullable = false, length = 64)
    private String ip;
    @Column(name = "time_hit", nullable = false)
    private ZonedDateTime timeHit;
}
