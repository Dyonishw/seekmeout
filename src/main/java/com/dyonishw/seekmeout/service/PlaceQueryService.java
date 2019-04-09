package com.dyonishw.seekmeout.service;

import java.util.List;

import javax.persistence.criteria.JoinType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import com.dyonishw.seekmeout.domain.Place;
import com.dyonishw.seekmeout.domain.*; // for static metamodels
import com.dyonishw.seekmeout.repository.PlaceRepository;
import com.dyonishw.seekmeout.repository.search.PlaceSearchRepository;
import com.dyonishw.seekmeout.service.dto.PlaceCriteria;
import com.dyonishw.seekmeout.service.dto.PlaceDTO;
import com.dyonishw.seekmeout.service.mapper.PlaceMapper;

/**
 * Service for executing complex queries for Place entities in the database.
 * The main input is a {@link PlaceCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link PlaceDTO} or a {@link Page} of {@link PlaceDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PlaceQueryService extends QueryService<Place> {

    private final Logger log = LoggerFactory.getLogger(PlaceQueryService.class);

    private final PlaceRepository placeRepository;

    private final PlaceMapper placeMapper;

    private final PlaceSearchRepository placeSearchRepository;

    public PlaceQueryService(PlaceRepository placeRepository, PlaceMapper placeMapper, PlaceSearchRepository placeSearchRepository) {
        this.placeRepository = placeRepository;
        this.placeMapper = placeMapper;
        this.placeSearchRepository = placeSearchRepository;
    }

    /**
     * Return a {@link List} of {@link PlaceDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<PlaceDTO> findByCriteria(PlaceCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Place> specification = createSpecification(criteria);
        return placeMapper.toDto(placeRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link PlaceDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PlaceDTO> findByCriteria(PlaceCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Place> specification = createSpecification(criteria);
        return placeRepository.findAll(specification, page)
            .map(placeMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PlaceCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Place> specification = createSpecification(criteria);
        return placeRepository.count(specification);
    }

    /**
     * Function to convert PlaceCriteria to a {@link Specification}
     */
    private Specification<Place> createSpecification(PlaceCriteria criteria) {
        Specification<Place> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Place_.id));
            }
            if (criteria.getAddress() != null) {
                specification = specification.and(buildStringSpecification(criteria.getAddress(), Place_.address));
            }
            if (criteria.getPossibleActivities() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPossibleActivities(), Place_.possibleActivities));
            }
            if (criteria.getPhoneNumber() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPhoneNumber(), Place_.phoneNumber));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), Place_.description));
            }
            if (criteria.getOpenHours() != null) {
                specification = specification.and(buildStringSpecification(criteria.getOpenHours(), Place_.openHours));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Place_.name));
            }
            if (criteria.getPricePerHour() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPricePerHour(), Place_.pricePerHour));
            }
            if (criteria.getContactForm() != null) {
                specification = specification.and(buildStringSpecification(criteria.getContactForm(), Place_.contactForm));
            }
            if (criteria.getFacilities() != null) {
                specification = specification.and(buildStringSpecification(criteria.getFacilities(), Place_.facilities));
            }
            if (criteria.getActivityPlaceId() != null) {
                specification = specification.and(buildSpecification(criteria.getActivityPlaceId(),
                    root -> root.join(Place_.activityPlaces, JoinType.LEFT).get(Activity_.id)));
            }
            if (criteria.getPlaceEventId() != null) {
                specification = specification.and(buildSpecification(criteria.getPlaceEventId(),
                    root -> root.join(Place_.placeEvents, JoinType.LEFT).get(Event_.id)));
            }
        }
        return specification;
    }
}
