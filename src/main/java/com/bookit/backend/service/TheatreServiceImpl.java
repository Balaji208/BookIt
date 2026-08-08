package com.bookit.backend.service;

import com.bookit.backend.exception.APIException;
import com.bookit.backend.exception.ResourceNotFoundException;
import com.bookit.backend.model.Theatre;
import com.bookit.backend.payload.TheatreRequest;
import com.bookit.backend.payload.TheatreResponse;
import com.bookit.backend.repository.TheatreRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TheatreServiceImpl implements TheatreService{

    @Autowired
    private TheatreRepository theatreRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public TheatreResponse addTheatre(TheatreRequest theatreRequest) {
        if(theatreRepository.existsByNameAndAddressAndCity(
                theatreRequest.getName(),
                theatreRequest.getAddress(),
                theatreRequest.getCity())) {
            throw new APIException("Theatre already exists");
        }
        Theatre theatre = modelMapper.map(theatreRequest, Theatre.class);
        Theatre savedTheatre = theatreRepository.save(theatre);
        return modelMapper.map(savedTheatre, TheatreResponse.class);
    }

    @Transactional
    @Override
    public TheatreResponse updateTheatre(UUID theatreId, TheatreRequest theatreRequest) {
        Theatre theatre = theatreRepository.findById(theatreId)
                .orElseThrow(() -> new ResourceNotFoundException("Theatre", "TheatreId", theatreId));
        theatre.setCity(theatreRequest.getCity());
        theatre.setName(theatreRequest.getName());
        theatre.setAddress(theatreRequest.getAddress());
        theatre.setLatitude(theatreRequest.getLatitude());
        theatre.setLongitude(theatre.getLongitude());

        return modelMapper.map(theatre, TheatreResponse.class);
    }

    @Override
    public TheatreResponse deleteTheatre(UUID theatreId) {
        Theatre theatre = theatreRepository.findById(theatreId)
                .orElseThrow(()->new ResourceNotFoundException("Theatre", "TheatreId", theatreId));
        theatreRepository.deleteById(theatreId);
        return modelMapper.map(theatre, TheatreResponse.class);
    }

    @Override
    public List<TheatreResponse> getAllTheatres() {
        List<Theatre> theatres = theatreRepository.findAll();
        return theatres.stream().map(
                (theatre) -> {
                    return modelMapper.map(theatre, TheatreResponse.class);
                }
        ).toList();
    }

    @Override
    public TheatreResponse getTheatre(UUID theatreId) {
        Theatre theatre = theatreRepository.findById(theatreId)
                .orElseThrow(()->new ResourceNotFoundException("Theatre", "TheatreId", theatreId));
        return modelMapper.map(theatre, TheatreResponse.class);
    }
}
