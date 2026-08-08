package com.bookit.backend.service;

import com.bookit.backend.payload.ScreenRequest;
import com.bookit.backend.payload.ScreenResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface ScreenService {
    ScreenResponse addScreen(UUID theatreId, ScreenRequest screenRequest);

    ScreenResponse updateScreen(UUID screenId, ScreenRequest screenRequest);

    ScreenResponse deleteScreen(UUID screenId);

    List<ScreenResponse> getAllScreens(UUID theatreId);

    ScreenResponse getScreen(UUID screenId);
}
