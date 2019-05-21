package com.dyonishw.seekmeout.service.impl;

import com.dyonishw.seekmeout.service.PlaceService;
import com.dyonishw.seekmeout.domain.Place;
import com.dyonishw.seekmeout.repository.PlaceRepository;
import com.dyonishw.seekmeout.repository.UserRepository;
import com.dyonishw.seekmeout.repository.search.PlaceSearchRepository;
import com.dyonishw.seekmeout.service.dto.PlaceDTO;
import com.dyonishw.seekmeout.service.mapper.PlaceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Place.
 */
@Service
@Transactional
public class PlaceServiceImpl implements PlaceService {

    private final Logger log = LoggerFactory.getLogger(PlaceServiceImpl.class);

    private final PlaceRepository placeRepository;

    private final PlaceMapper placeMapper;

    private final PlaceSearchRepository placeSearchRepository;

    private final UserRepository userRepository;

    public PlaceServiceImpl(PlaceRepository placeRepository, PlaceMapper placeMapper, PlaceSearchRepository placeSearchRepository, UserRepository userRepository) {
        this.placeRepository = placeRepository;
        this.placeMapper = placeMapper;
        this.placeSearchRepository = placeSearchRepository;
        this.userRepository = userRepository;
    }

    /**
     * Save a place.
     *
     * @param placeDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public PlaceDTO save(PlaceDTO placeDTO) {
        log.debug("Request to save Place : {}", placeDTO);
        Place place = placeMapper.toEntity(placeDTO);
        long userId = placeDTO.getRolePlaceUserId();
        // TODO: Uncomment this line
//        userRepository.findById(userId).ifPresent(place::user);
        userRepository.findById(userId).ifPresent(place::setRolePlaceUser);
        place = placeRepository.save(place);
        PlaceDTO result = placeMapper.toDto(place);
        placeSearchRepository.save(place);
        return result;
    }

    /**
     * Get all the places.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<PlaceDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Places");
        return placeRepository.findAll(pageable)
            .map(placeMapper::toDto);
    }

    /**
     * Get all the Place with eager load of many-to-many relationships.
     *
     * @return the list of entities
     */
    public Page<PlaceDTO> findAllWithEagerRelationships(Pageable pageable) {
        return placeRepository.findAllWithEagerRelationships(pageable).map(placeMapper::toDto);
    }
    

    /**
     * Get one place by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<PlaceDTO> findOne(Long id) {
        log.debug("Request to get Place : {}", id);
        return placeRepository.findOneWithEagerRelationships(id)
            .map(placeMapper::toDto);
    }

    /**
     * Delete the place by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Place : {}", id);
        placeRepository.deleteById(id);
        placeSearchRepository.deleteById(id);
    }

    /**
     * Search for the place corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<PlaceDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Places for query {}", query);
        return placeSearchRepository.search(queryStringQuery(query), pageable)
            .map(placeMapper::toDto);
    }
}
