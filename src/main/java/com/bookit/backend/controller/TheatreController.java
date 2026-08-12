package com.bookit.backend.controller;

import com.bookit.backend.config.AppConstants;
import com.bookit.backend.payload.PageResponse;
import com.bookit.backend.payload.TheatreRequest;
import com.bookit.backend.payload.TheatreResponse;
import com.bookit.backend.service.TheatreService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
    public ResponseEntity<PageResponse<TheatreResponse>> getAllTheatres(
            @RequestParam(
                    name = "pageNumber",
                    defaultValue = AppConstants.PAGE_NUMBER
            )
            @Min(value = 0, message = "Page number cannot be negative")
            Integer pageNumber,

            @RequestParam(
                    name = "pageSize",
                    defaultValue = AppConstants.PAGE_SIZE
            )
            @Min(value = 1, message = "Page size must be at least 1")
            @Max(value = 50, message = "Page size cannot exceed 50")
            Integer pageSize,

            @RequestParam(
                    name = "sortBy",
                    defaultValue = "name"
            )
            String sortBy,

            @RequestParam(
                    name = "sortOrder",
                    defaultValue = AppConstants.SORT_DIR
            )
            String sortOrder
    ) {
        PageResponse<TheatreResponse> theatreResponses = theatreService.getAllTheatres(
                pageNumber,
                pageSize,
                sortBy,
                sortOrder
        );
        return new ResponseEntity<>(theatreResponses, HttpStatus.OK);
    }

    @GetMapping("/theatres/{theatreId}")
    public ResponseEntity<TheatreResponse> getTheatreDetails(@PathVariable UUID theatreId) {
        TheatreResponse theatreResponse = theatreService.getTheatreDetails(theatreId);
        return new ResponseEntity<>(theatreResponse, HttpStatus.OK);
    }
}
