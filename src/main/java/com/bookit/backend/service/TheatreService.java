package com.bookit.backend.service;

import com.bookit.backend.payload.TheatreRequest;
import com.bookit.backend.payload.TheatreResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface TheatreService {
    TheatreResponse addTheatre(TheatreRequest theatreRequest);

    TheatreResponse updateTheatre(UUID theatreId, TheatreRequest theatreRequest);

    TheatreResponse deleteTheatre(UUID theatreId);

    List<TheatreResponse> getAllTheatres();

    TheatreResponse getTheatre(UUID theatreId);
}
