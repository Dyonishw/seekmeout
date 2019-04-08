package com.dyonishw.seekmeout.service;

import com.dyonishw.seekmeout.service.dto.PlaceDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing Place.
 */
public interface PlaceService {

    /**
     * Save a place.
     *
     * @param placeDTO the entity to save
     * @return the persisted entity
     */
    PlaceDTO save(PlaceDTO placeDTO);

    /**
     * Get all the places.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<PlaceDTO> findAll(Pageable pageable);

    /**
     * Get all the Place with eager load of many-to-many relationships.
     *
     * @return the list of entities
     */
    Page<PlaceDTO> findAllWithEagerRelationships(Pageable pageable);
    
    /**
     * Get the "id" place.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<PlaceDTO> findOne(Long id);

    /**
     * Delete the "id" place.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the place corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<PlaceDTO> search(String query, Pageable pageable);
}
