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

import com.dyonishw.seekmeout.domain.Activity;
import com.dyonishw.seekmeout.domain.*; // for static metamodels
import com.dyonishw.seekmeout.repository.ActivityRepository;
import com.dyonishw.seekmeout.repository.search.ActivitySearchRepository;
import com.dyonishw.seekmeout.service.dto.ActivityCriteria;
import com.dyonishw.seekmeout.service.dto.ActivityDTO;
import com.dyonishw.seekmeout.service.mapper.ActivityMapper;

/**
 * Service for executing complex queries for Activity entities in the database.
 * The main input is a {@link ActivityCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ActivityDTO} or a {@link Page} of {@link ActivityDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ActivityQueryService extends QueryService<Activity> {

    private final Logger log = LoggerFactory.getLogger(ActivityQueryService.class);

    private final ActivityRepository activityRepository;

    private final ActivityMapper activityMapper;

    private final ActivitySearchRepository activitySearchRepository;

    public ActivityQueryService(ActivityRepository activityRepository, ActivityMapper activityMapper, ActivitySearchRepository activitySearchRepository) {
        this.activityRepository = activityRepository;
        this.activityMapper = activityMapper;
        this.activitySearchRepository = activitySearchRepository;
    }

    /**
     * Return a {@link List} of {@link ActivityDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ActivityDTO> findByCriteria(ActivityCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Activity> specification = createSpecification(criteria);
        return activityMapper.toDto(activityRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ActivityDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ActivityDTO> findByCriteria(ActivityCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Activity> specification = createSpecification(criteria);
        return activityRepository.findAll(specification, page)
            .map(activityMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ActivityCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Activity> specification = createSpecification(criteria);
        return activityRepository.count(specification);
    }

    /**
     * Function to convert ActivityCriteria to a {@link Specification}
     */
    private Specification<Activity> createSpecification(ActivityCriteria criteria) {
        Specification<Activity> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Activity_.id));
            }
            if (criteria.getType() != null) {
                specification = specification.and(buildStringSpecification(criteria.getType(), Activity_.type));
            }
            if (criteria.getNumberOfPlayers() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getNumberOfPlayers(), Activity_.numberOfPlayers));
            }
            if (criteria.getOfficialDuration() != null) {
                specification = specification.and(buildStringSpecification(criteria.getOfficialDuration(), Activity_.officialDuration));
            }
            if (criteria.getOfficialRules() != null) {
                specification = specification.and(buildStringSpecification(criteria.getOfficialRules(), Activity_.officialRules));
            }
            if (criteria.getShortDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getShortDescription(), Activity_.shortDescription));
            }
            if (criteria.getRecommendedGear() != null) {
                specification = specification.and(buildStringSpecification(criteria.getRecommendedGear(), Activity_.recommendedGear));
            }
            if (criteria.getActivityPlaceId() != null) {
                specification = specification.and(buildSpecification(criteria.getActivityPlaceId(),
                    root -> root.join(Activity_.activityPlaces, JoinType.LEFT).get(Place_.id)));
            }
            if (criteria.getActivityEventId() != null) {
                specification = specification.and(buildSpecification(criteria.getActivityEventId(),
                    root -> root.join(Activity_.activityEvents, JoinType.LEFT).get(Event_.id)));
            }
        }
        return specification;
    }
}
