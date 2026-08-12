package com.bookit.backend.controller;

import com.bookit.backend.config.AppConstants;
import com.bookit.backend.payload.PageResponse;
import com.bookit.backend.payload.ScreenRequest;
import com.bookit.backend.payload.ScreenResponse;
import com.bookit.backend.service.ScreenService;
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
public class ScreenController {
    @Autowired
    private ScreenService screenService;

    @PostMapping("/theatres/{theatreId}/screens")
    public ResponseEntity<ScreenResponse> addScreen(@PathVariable UUID theatreId,
                                                    @RequestBody ScreenRequest screenRequest) {
        ScreenResponse screenResponse = screenService.addScreen(theatreId, screenRequest);
        return new ResponseEntity<>(screenResponse, HttpStatus.CREATED);
    }

    @PutMapping("/screens/{screenId}")
    public ResponseEntity<ScreenResponse> updateScreen(@PathVariable UUID screenId,
                                                       @RequestBody ScreenRequest screenRequest) {
        ScreenResponse screenResponse = screenService.updateScreen(screenId, screenRequest);
        return new ResponseEntity<>(screenResponse, HttpStatus.OK);
    }

    @DeleteMapping("/screens/{screenId}")
    public ResponseEntity<ScreenResponse> deleteScreen(@PathVariable UUID screenId) {
        ScreenResponse screenResponse = screenService.deleteScreen(screenId);
        return new ResponseEntity<>(screenResponse, HttpStatus.OK);
    }

    // Customer Operations
    @GetMapping("/theatres/{theatreId}/screens")
    public ResponseEntity<PageResponse<ScreenResponse>> getAllScreens(
            @PathVariable UUID theatreId,
            @RequestParam(
                    name = "pageNumber",
                    defaultValue = AppConstants.PAGE_NUMBER,
                    required = false
            )
            @Min(value = 0, message = "Page number cannot be negative")
            Integer pageNumber,

            @RequestParam(
                    name = "pageSize",
                    defaultValue = AppConstants.PAGE_SIZE,
                    required = false
            )
            @Min(value = 1, message = "Page size must be at least 1")
            @Max(value = 50, message = "Page size cannot exceed 50")
            Integer pageSize,

            @RequestParam(
                    name = "sortBy",
                    defaultValue = "name",
                    required = false
            )
            String sortBy,

            @RequestParam(
                    name = "sortOrder",
                    defaultValue = AppConstants.SORT_DIR,
                    required = false
            )
            String sortOrder
    ) {
        PageResponse<ScreenResponse> screenResponses = screenService.getAllScreens(theatreId,
                pageNumber,
                pageSize,
                sortBy,
                sortOrder);
        return new ResponseEntity<>(screenResponses, HttpStatus.OK);
    }

    @GetMapping("/screens/{screenId}")
    public ResponseEntity<ScreenResponse> getScreenDetails(@PathVariable UUID screenId) {
        ScreenResponse screenResponse = screenService.getScreenDetails(screenId);
        return new ResponseEntity<>(screenResponse, HttpStatus.OK);
    }

}
