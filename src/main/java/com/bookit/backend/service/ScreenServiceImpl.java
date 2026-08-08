package com.bookit.backend.service;

import com.bookit.backend.exception.ResourceNotFoundException;
import com.bookit.backend.model.Screen;
import com.bookit.backend.model.Theatre;
import com.bookit.backend.payload.ScreenRequest;
import com.bookit.backend.payload.ScreenResponse;
import com.bookit.backend.repository.ScreenRepository;
import com.bookit.backend.repository.TheatreRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
@Service
public class ScreenServiceImpl implements ScreenService{

    @Autowired
    private ScreenRepository screenRepository;

    @Autowired
    private TheatreRepository theatreRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional
    @Override
    public ScreenResponse addScreen(UUID theatreId, ScreenRequest screenRequest) {
        Theatre theatre = theatreRepository.findById(theatreId)
                .orElseThrow(()-> new ResourceNotFoundException("Theatre", "TheatreId", theatreId));
        Screen screen = modelMapper.map(screenRequest, Screen.class);
        screen.setTheatre(theatre);
        theatre.getScreens().add(screen);
        Screen savedScreen = screenRepository.save(screen);
        return modelMapper.map(savedScreen, ScreenResponse.class);
    }

    @Transactional
    @Override
    public ScreenResponse updateScreen(UUID screenId, ScreenRequest screenRequest) {
        Screen screen = screenRepository.findById(screenId)
                .orElseThrow(()-> new ResourceNotFoundException("Screen", "ScreenId", screenId));
        screen.setName(screenRequest.getName());
        screen.setTotalSeats(screenRequest.getTotalSeats());
        screen.setScreenType(screenRequest.getScreenType());
        return modelMapper.map(screen, ScreenResponse.class);
    }

    @Transactional
    @Override
    public ScreenResponse deleteScreen(UUID screenId) {
        Screen screen = screenRepository.findById(screenId)
                .orElseThrow(()-> new ResourceNotFoundException("Screen", "ScreenId", screenId));
        screen.getTheatre().getScreens().remove(screen);
        screen.setTheatre(null);
        screenRepository.delete(screen);
        return modelMapper.map(screen, ScreenResponse.class);
    }

    @Override
    public List<ScreenResponse> getAllScreens(UUID theatreId) {
        Theatre theatre = theatreRepository.findById(theatreId)
                .orElseThrow(()-> new ResourceNotFoundException("Theatre", "TheatreId", theatreId));
        List<Screen> screens = theatre.getScreens();
        return screens.stream().map((screen) ->{
            return modelMapper.map(screen, ScreenResponse.class);
        }).toList();
    }

    @Override
    public ScreenResponse getScreen(UUID screenId) {
        Screen screen = screenRepository.findById(screenId)
                .orElseThrow(()-> new ResourceNotFoundException("Screen", "ScreenId", screenId));
        return modelMapper.map(screen, ScreenResponse.class);
    }
}
