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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "theatres")
public class Theatre {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "theatre_id")
    private UUID theatreId;

    @NotNull
    private String name;

    @NotNull
    private String address;

    @NotNull
    private String city;

    private double latitude;
    private double longitude;

    @NotNull
    private Boolean active = true;


    @Column(name = "created_at")
    @CreationTimestamp
    private Timestamp createdAt;


    @Column(name = "updated_at")
    @UpdateTimestamp
    private Timestamp updatedAt;

    @OneToMany(mappedBy = "theatre", cascade = CascadeType.ALL)
    private List<Screen> screens = new ArrayList<>();

}
