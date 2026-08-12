package com.bookit.backend.controller;

import com.bookit.backend.config.AppConstants;
import com.bookit.backend.payload.PageResponse;
import com.bookit.backend.payload.ShowRequest;
import com.bookit.backend.payload.ShowResponse;
import com.bookit.backend.payload.ShowStatusRequest;
import com.bookit.backend.service.ShowService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class ShowController {
    @Autowired
    private ShowService showService;

    @PostMapping("/shows")
    public ResponseEntity<ShowResponse> addShow(@RequestBody ShowRequest showRequest) {
        ShowResponse showResponse = showService.addShow(showRequest);
        return new ResponseEntity<>(showResponse, HttpStatus.CREATED);
    }

    @PutMapping("/shows/{showId}")
    public ResponseEntity<ShowResponse> updateShow(@PathVariable UUID showId,
                                                   @RequestBody ShowRequest showRequest) {
        ShowResponse showResponse = showService.updateShow(showId, showRequest);
        return new ResponseEntity<>(showResponse, HttpStatus.OK);
    }

    @DeleteMapping("/shows/{showId}")
    public ResponseEntity<ShowResponse> deleteShow(@PathVariable UUID showId) {
        ShowResponse showResponse = showService.deleteShow(showId);
        return new ResponseEntity<>(showResponse, HttpStatus.OK);
    }

    @PatchMapping("/shows/{showId}/status")
    public ResponseEntity<ShowResponse> updateShowStatus(
            @PathVariable UUID showId,
            @Valid @RequestBody ShowStatusRequest request) {

        ShowResponse showResponse =
                showService.updateShowStatus(showId, request.getStatus());

        return ResponseEntity.ok(showResponse);
    }

    // Customer Operations

    @GetMapping("/shows")
    public ResponseEntity<PageResponse<ShowResponse>> getAllShows(
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
                    defaultValue = "basePrice"
            )
            String sortBy,

            @RequestParam(
                    name = "sortOrder",
                    defaultValue = AppConstants.SORT_DIR
            )
            String sortOrder
    ) {
        PageResponse<ShowResponse> showResponses = showService.getAllShows(
                pageNumber,
                pageSize,
                sortBy,
                sortOrder
        );
        return new ResponseEntity<>(showResponses, HttpStatus.OK);
    }

    @GetMapping("/shows/{showId}")
    public ResponseEntity<ShowResponse> getShowDetails(@PathVariable UUID showId) {
        ShowResponse showResponse = showService.getShowDetails(showId);
        return new ResponseEntity<>(showResponse, HttpStatus.OK);
    }

    @GetMapping("movies/{movieId}/shows")
    public ResponseEntity<List<ShowResponse>> getShowsForMovie(@PathVariable UUID movieId) {
        List<ShowResponse> showResponses = showService.getShowsForMovie(movieId);
        return new ResponseEntity<>(showResponses, HttpStatus.OK);
    }

    @GetMapping("/theatres/{theatreId}/shows")
    public ResponseEntity<List<ShowResponse>> getShowsInTheatre(@PathVariable UUID theatreId) {
        List<ShowResponse> showResponses = showService.getShowsInTheatre(theatreId);
        return new ResponseEntity<>(showResponses, HttpStatus.OK);
    }
}
