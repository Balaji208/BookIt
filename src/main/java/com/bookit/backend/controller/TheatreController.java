package com.bookit.backend.controller;

import com.bookit.backend.payload.TheatreRequest;
import com.bookit.backend.payload.TheatreResponse;
import com.bookit.backend.service.TheatreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class TheatreController {

    @Autowired
    private TheatreService theatreService;

    // Admin Operations

    @PostMapping("/theatres")
    public ResponseEntity<TheatreResponse> addTheatre(@RequestBody TheatreRequest theatreRequest) {
        TheatreResponse theatreResponse = theatreService.addTheatre(theatreRequest);
        return new ResponseEntity<>(theatreResponse, HttpStatus.CREATED);
    }

    @PutMapping("/theatres/{theatreId}")
    public ResponseEntity<TheatreResponse> updateTheatre(@PathVariable UUID theatreId,
                                                         @RequestBody TheatreRequest theatreRequest) {
        TheatreResponse theatreResponse = theatreService.updateTheatre(theatreId, theatreRequest);
        return new ResponseEntity<>(theatreResponse, HttpStatus.OK);
    }

    @DeleteMapping("/theatres/{theatreId}")
    public ResponseEntity<TheatreResponse> deleteTheatre(@PathVariable UUID theatreId) {
        TheatreResponse theatreResponse = theatreService.deleteTheatre(theatreId);
        return new ResponseEntity<>(theatreResponse, HttpStatus.OK);
    }

    // Customer Operations

    @GetMapping("/theatres")
    public ResponseEntity<List<TheatreResponse>> getAllTheatres() {
        List<TheatreResponse> theatreResponses = theatreService.getAllTheatres();
        return new ResponseEntity<>(theatreResponses, HttpStatus.OK);
    }

    @GetMapping("/theatres/{theatreId}")
    public ResponseEntity<TheatreResponse> getTheatre(@PathVariable UUID theatreId) {
        TheatreResponse theatreResponse = theatreService.getTheatre(theatreId);
        return new ResponseEntity<>(theatreResponse, HttpStatus.OK);
    }
}
