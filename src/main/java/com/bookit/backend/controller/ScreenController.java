package com.bookit.backend.controller;

import com.bookit.backend.payload.ScreenRequest;
import com.bookit.backend.payload.ScreenResponse;
import com.bookit.backend.service.ScreenService;
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
    public ResponseEntity<List<ScreenResponse>> getAllScreens(@PathVariable UUID theatreId) {
        List<ScreenResponse> screenResponses = screenService.getAllScreens(theatreId);
        return new ResponseEntity<>(screenResponses, HttpStatus.OK);
    }

    @GetMapping("/screens/{screenId}")
    public ResponseEntity<ScreenResponse> getScreen(@PathVariable UUID screenId) {
        ScreenResponse screenResponse = screenService.getScreen(screenId);
        return new ResponseEntity<>(screenResponse, HttpStatus.OK);
    }

}
