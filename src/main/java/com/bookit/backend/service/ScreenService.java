package com.bookit.backend.service;

import com.bookit.backend.payload.PageResponse;
import com.bookit.backend.payload.ScreenRequest;
import com.bookit.backend.payload.ScreenResponse;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface ScreenService {
    ScreenResponse addScreen(UUID theatreId, ScreenRequest screenRequest);

    ScreenResponse updateScreen(UUID screenId, ScreenRequest screenRequest);

    ScreenResponse deleteScreen(UUID screenId);


    ScreenResponse getScreenDetails(UUID screenId);

    PageResponse<ScreenResponse> getAllScreens(UUID theatreId,
                                               @Min(value = 0, message = "Page number cannot be negative") Integer pageNumber,
                                               @Min(value = 1, message = "Page size must be at least 1")
                                               @Max(value = 50, message = "Page size cannot exceed 50") Integer pageSize,
                                               String sortBy,
                                               String sortOrder);
}
