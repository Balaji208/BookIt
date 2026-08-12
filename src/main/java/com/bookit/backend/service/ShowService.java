package com.bookit.backend.service;

import com.bookit.backend.model.ShowStatus;
import com.bookit.backend.payload.PageResponse;
import com.bookit.backend.payload.ShowRequest;
import com.bookit.backend.payload.ShowResponse;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface ShowService {
    ShowResponse addShow(ShowRequest showRequest);

    ShowResponse updateShow(UUID showId, ShowRequest showRequest);

    ShowResponse deleteShow(UUID showId);

    ShowResponse updateShowStatus(UUID showId, @NotNull ShowStatus status);

    PageResponse<ShowResponse> getAllShows(
            @Min(value = 0, message = "Page number cannot be negative") Integer pageNumber,
            @Min(value = 1, message = "Page size must be at least 1")
            @Max(value = 50, message = "Page size cannot exceed 50")
            Integer pageSize,
            String sortBy,
            String sortOrder
    );

    ShowResponse getShowDetails(UUID showId);

    List<ShowResponse> getShowsForMovie(UUID movieId);

    List<ShowResponse> getShowsInTheatre(UUID theatreId);
}
