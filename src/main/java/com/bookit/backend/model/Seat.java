package com.bookit.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "seats")
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "seat_id")
    private UUID seatId;

    @NotNull
    @Column(name = "row_label")
    private String rowLabel;

    @NotNull
    @Column(name = "seat_number")
    private Integer seatNumber;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "seat_type")
    private SeatType seatType;

    @NotNull
    private Boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_id")
    private Screen screen;


    @Column(name = "created_at")
    @CreationTimestamp
    private Timestamp createdAt;


    @Column(name = "updated_at")
    @UpdateTimestamp
    private Timestamp updatedAt;
}
