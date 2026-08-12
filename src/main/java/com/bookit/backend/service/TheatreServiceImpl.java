package com.bookit.backend.service;

import com.bookit.backend.exception.APIException;
import com.bookit.backend.exception.ResourceNotFoundException;
import com.bookit.backend.model.Theatre;
import com.bookit.backend.payload.PageResponse;
import com.bookit.backend.payload.TheatreRequest;
import com.bookit.backend.payload.TheatreResponse;
import com.bookit.backend.repository.TheatreRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class TheatreServiceImpl implements TheatreService{

    @Autowired
    private TheatreRepository theatreRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public TheatreResponse addTheatre(TheatreRequest theatreRequest) {

        log.debug("Creating theatre name={}", theatreRequest.getName());

        if(theatreRepository.existsByNameAndAddressAndCityAndActiveTrue(
                theatreRequest.getName(),
                theatreRequest.getAddress(),
                theatreRequest.getCity())) {
            log.warn("Theatre already exists name={}", theatreRequest.getName());
            throw new APIException("Theatre already exists");
        }

        Theatre theatre = modelMapper.map(theatreRequest, Theatre.class);
        Theatre savedTheatre = theatreRepository.save(theatre);

        log.info("Theatre created successfully theatreId={}", savedTheatre.getTheatreId());
        return modelMapper.map(savedTheatre, TheatreResponse.class);
    }

    @Transactional
    @Override
    public TheatreResponse updateTheatre(UUID theatreId, TheatreRequest theatreRequest) {
        log.debug("Updating theatre theatreId={}", theatreId);

        Theatre savedTheatre = theatreRepository.findByTheatreIdAndActiveTrue(theatreId)
                .orElseThrow(() -> {
                    log.warn("Theatre not found or deactivated theatreId={}", theatreId);
                    return new ResourceNotFoundException("Theatre", "TheatreId", theatreId);
                });

        if(theatreRepository.existsByNameAndAddressAndCityAndActiveTrueAndTheatreIdNot(
                theatreRequest.getName(),
                theatreRequest.getAddress(),
                theatreRequest.getCity(),
                theatreId
        )) {
            log.warn("Theatre already exists name={}", theatreRequest.getName());
            throw new APIException("Theatre already exists");
        }

        savedTheatre.setCity(theatreRequest.getCity());
        savedTheatre.setName(theatreRequest.getName());
        savedTheatre.setAddress(theatreRequest.getAddress());
        savedTheatre.setLatitude(theatreRequest.getLatitude());
        savedTheatre.setLongitude(theatreRequest.getLongitude());

        log.info("Theatre updated successfully theatreId={}", theatreId);
        return modelMapper.map(savedTheatre, TheatreResponse.class);
    }

    @Transactional
    @Override
    public TheatreResponse deleteTheatre(UUID theatreId) {

        log.debug("Deactivating theatre theatreId={}", theatreId);

        Theatre savedTheatre = theatreRepository.findByTheatreIdAndActiveTrue(theatreId)
                .orElseThrow(() -> {
                    log.warn("Theatre not found or deactivated theatreId={}", theatreId);
                    return new ResourceNotFoundException("Theatre", "TheatreId", theatreId);
                });

        savedTheatre.setActive(false); // soft deletion

        log.info("Theatre deactivated successfully theatreId={}", theatreId);
        return modelMapper.map(savedTheatre, TheatreResponse.class);
    }

    @Override
    public PageResponse<TheatreResponse> getAllTheatres(
            Integer pageNumber,
            Integer pageSize,
            String sortBy,
            String sortOrder) {

        log.debug(
                "Fetching theatres page={}, size={}, sortBy={}, sortOrder={}",
                pageNumber,
                pageSize,
                sortBy,
                sortOrder
        );

        Sort sortAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortAndOrder);

        Page<Theatre> theatrePage = theatreRepository.findAllByActiveTrue(pageDetails);

        if(theatrePage.isEmpty()) {
            log.warn("No theatre found!");
            throw new APIException("No theatres are there!");
        }

        List<TheatreResponse> theatres = theatrePage.getContent()
                .stream()
                .map((theatre) -> modelMapper.map(theatre, TheatreResponse.class))
                .toList();
        PageResponse<TheatreResponse> theatrePageResponse =
                new PageResponse<>();

        theatrePageResponse.setContent(theatres);
        theatrePageResponse.setPageSize(theatrePage.getSize());
        theatrePageResponse.setPageNumber(theatrePage.getNumber());
        theatrePageResponse.setTotalPages(theatrePage.getTotalPages());
        theatrePageResponse.setTotalElements(theatrePage.getTotalElements());

        log.info(
                "Theatres fetched successfully: page={}, size={}, totalElements={}",
                theatrePage.getNumber(),
                theatrePage.getSize(),
                theatrePage.getTotalElements()
        );
        return theatrePageResponse;
    }

    @Override
    public TheatreResponse getTheatreDetails(UUID theatreId) {

        log.debug("Fetching theatre details theatreId={}", theatreId);

        Theatre savedTheatre = theatreRepository.findByTheatreIdAndActiveTrue(theatreId)
                .orElseThrow(() -> {
                    log.warn("Theatre not found or deactivated theatreId={}", theatreId);
                    return new ResourceNotFoundException("Theatre", "TheatreId", theatreId);
                });

        log.debug("Theatre details fetched successfully theatreId={}", theatreId);
        return modelMapper.map(savedTheatre, TheatreResponse.class);
    }
}
