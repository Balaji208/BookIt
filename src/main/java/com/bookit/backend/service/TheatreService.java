package com.bookit.backend.service;

import com.bookit.backend.payload.PageResponse;
import com.bookit.backend.payload.TheatreRequest;
import com.bookit.backend.payload.TheatreResponse;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface TheatreService {
    TheatreResponse addTheatre(TheatreRequest theatreRequest);

    TheatreResponse updateTheatre(UUID theatreId, TheatreRequest theatreRequest);

    TheatreResponse deleteTheatre(UUID theatreId);

    PageResponse<TheatreResponse> getAllTheatres(
            @Min(value = 0, message = "Page number cannot be negative") Integer pageNumber,
            @Min(value = 1, message = "Page size must be at least 1")
            @Max(value = 50, message = "Page size cannot exceed 50")
            Integer pageSize,
            String sortBy,
            String sortOrder);

    TheatreResponse getTheatreDetails(UUID theatreId);
}
