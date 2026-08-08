package com.bookit.backend.service;

import com.bookit.backend.model.ShowStatus;
import com.bookit.backend.payload.ShowRequest;
import com.bookit.backend.payload.ShowResponse;
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

    List<ShowResponse> getAllShows();

    ShowResponse getShowDetails(UUID showId);

    List<ShowResponse> getShowsForMovie(UUID movieId);

    List<ShowResponse> getShowsInTheatre(UUID theatreId);
}
