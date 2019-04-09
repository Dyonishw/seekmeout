package com.dyonishw.seekmeout.web.rest;

import com.dyonishw.seekmeout.SeekMeOutApp;

import com.dyonishw.seekmeout.domain.Activity;
import com.dyonishw.seekmeout.domain.Place;
import com.dyonishw.seekmeout.domain.Event;
import com.dyonishw.seekmeout.repository.ActivityRepository;
import com.dyonishw.seekmeout.repository.search.ActivitySearchRepository;
import com.dyonishw.seekmeout.service.ActivityService;
import com.dyonishw.seekmeout.service.dto.ActivityDTO;
import com.dyonishw.seekmeout.service.mapper.ActivityMapper;
import com.dyonishw.seekmeout.web.rest.errors.ExceptionTranslator;
import com.dyonishw.seekmeout.service.dto.ActivityCriteria;
import com.dyonishw.seekmeout.service.ActivityQueryService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;


import static com.dyonishw.seekmeout.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ActivityResource REST controller.
 *
 * @see ActivityResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SeekMeOutApp.class)
public class ActivityResourceIntTest {

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final Integer DEFAULT_NUMBER_OF_PLAYERS = 2;
    private static final Integer UPDATED_NUMBER_OF_PLAYERS = 3;

    private static final String DEFAULT_OFFICIAL_DURATION = "AAAAAAAAAA";
    private static final String UPDATED_OFFICIAL_DURATION = "BBBBBBBBBB";

    private static final String DEFAULT_OFFICIAL_RULES = "AAAAAAAAAA";
    private static final String UPDATED_OFFICIAL_RULES = "BBBBBBBBBB";

    private static final String DEFAULT_SHORT_DESCRIPTION = "AAAAAAAAAAAAAAA";
    private static final String UPDATED_SHORT_DESCRIPTION = "BBBBBBBBBBBBBBB";

    private static final String DEFAULT_RECOMMENDED_GEAR = "AAAAAAAAAA";
    private static final String UPDATED_RECOMMENDED_GEAR = "BBBBBBBBBB";

    private static final String DEFAULT_LONG_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_LONG_DESCRIPTION = "BBBBBBBBBB";

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private ActivityService activityService;

    /**
     * This repository is mocked in the com.dyonishw.seekmeout.repository.search test package.
     *
     * @see com.dyonishw.seekmeout.repository.search.ActivitySearchRepositoryMockConfiguration
     */
    @Autowired
    private ActivitySearchRepository mockActivitySearchRepository;

    @Autowired
    private ActivityQueryService activityQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restActivityMockMvc;

    private Activity activity;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ActivityResource activityResource = new ActivityResource(activityService, activityQueryService);
        this.restActivityMockMvc = MockMvcBuilders.standaloneSetup(activityResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Activity createEntity(EntityManager em) {
        Activity activity = new Activity()
            .type(DEFAULT_TYPE)
            .numberOfPlayers(DEFAULT_NUMBER_OF_PLAYERS)
            .officialDuration(DEFAULT_OFFICIAL_DURATION)
            .officialRules(DEFAULT_OFFICIAL_RULES)
            .shortDescription(DEFAULT_SHORT_DESCRIPTION)
            .recommendedGear(DEFAULT_RECOMMENDED_GEAR)
            .longDescription(DEFAULT_LONG_DESCRIPTION);
        return activity;
    }

    @Before
    public void initTest() {
        activity = createEntity(em);
    }

    @Test
    @Transactional
    public void createActivity() throws Exception {
        int databaseSizeBeforeCreate = activityRepository.findAll().size();

        // Create the Activity
        ActivityDTO activityDTO = activityMapper.toDto(activity);
        restActivityMockMvc.perform(post("/api/activities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(activityDTO)))
            .andExpect(status().isCreated());

        // Validate the Activity in the database
        List<Activity> activityList = activityRepository.findAll();
        assertThat(activityList).hasSize(databaseSizeBeforeCreate + 1);
        Activity testActivity = activityList.get(activityList.size() - 1);
        assertThat(testActivity.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testActivity.getNumberOfPlayers()).isEqualTo(DEFAULT_NUMBER_OF_PLAYERS);
        assertThat(testActivity.getOfficialDuration()).isEqualTo(DEFAULT_OFFICIAL_DURATION);
        assertThat(testActivity.getOfficialRules()).isEqualTo(DEFAULT_OFFICIAL_RULES);
        assertThat(testActivity.getShortDescription()).isEqualTo(DEFAULT_SHORT_DESCRIPTION);
        assertThat(testActivity.getRecommendedGear()).isEqualTo(DEFAULT_RECOMMENDED_GEAR);
        assertThat(testActivity.getLongDescription()).isEqualTo(DEFAULT_LONG_DESCRIPTION);

        // Validate the Activity in Elasticsearch
        verify(mockActivitySearchRepository, times(1)).save(testActivity);
    }

    @Test
    @Transactional
    public void createActivityWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = activityRepository.findAll().size();

        // Create the Activity with an existing ID
        activity.setId(1L);
        ActivityDTO activityDTO = activityMapper.toDto(activity);

        // An entity with an existing ID cannot be created, so this API call must fail
        restActivityMockMvc.perform(post("/api/activities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(activityDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Activity in the database
        List<Activity> activityList = activityRepository.findAll();
        assertThat(activityList).hasSize(databaseSizeBeforeCreate);

        // Validate the Activity in Elasticsearch
        verify(mockActivitySearchRepository, times(0)).save(activity);
    }

    @Test
    @Transactional
    public void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = activityRepository.findAll().size();
        // set the field null
        activity.setType(null);

        // Create the Activity, which fails.
        ActivityDTO activityDTO = activityMapper.toDto(activity);

        restActivityMockMvc.perform(post("/api/activities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(activityDTO)))
            .andExpect(status().isBadRequest());

        List<Activity> activityList = activityRepository.findAll();
        assertThat(activityList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNumberOfPlayersIsRequired() throws Exception {
        int databaseSizeBeforeTest = activityRepository.findAll().size();
        // set the field null
        activity.setNumberOfPlayers(null);

        // Create the Activity, which fails.
        ActivityDTO activityDTO = activityMapper.toDto(activity);

        restActivityMockMvc.perform(post("/api/activities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(activityDTO)))
            .andExpect(status().isBadRequest());

        List<Activity> activityList = activityRepository.findAll();
        assertThat(activityList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllActivities() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList
        restActivityMockMvc.perform(get("/api/activities?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(activity.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].numberOfPlayers").value(hasItem(DEFAULT_NUMBER_OF_PLAYERS)))
            .andExpect(jsonPath("$.[*].officialDuration").value(hasItem(DEFAULT_OFFICIAL_DURATION.toString())))
            .andExpect(jsonPath("$.[*].officialRules").value(hasItem(DEFAULT_OFFICIAL_RULES.toString())))
            .andExpect(jsonPath("$.[*].shortDescription").value(hasItem(DEFAULT_SHORT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].recommendedGear").value(hasItem(DEFAULT_RECOMMENDED_GEAR.toString())))
            .andExpect(jsonPath("$.[*].longDescription").value(hasItem(DEFAULT_LONG_DESCRIPTION.toString())));
    }
    
    @Test
    @Transactional
    public void getActivity() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get the activity
        restActivityMockMvc.perform(get("/api/activities/{id}", activity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(activity.getId().intValue()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.numberOfPlayers").value(DEFAULT_NUMBER_OF_PLAYERS))
            .andExpect(jsonPath("$.officialDuration").value(DEFAULT_OFFICIAL_DURATION.toString()))
            .andExpect(jsonPath("$.officialRules").value(DEFAULT_OFFICIAL_RULES.toString()))
            .andExpect(jsonPath("$.shortDescription").value(DEFAULT_SHORT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.recommendedGear").value(DEFAULT_RECOMMENDED_GEAR.toString()))
            .andExpect(jsonPath("$.longDescription").value(DEFAULT_LONG_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    public void getAllActivitiesByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where type equals to DEFAULT_TYPE
        defaultActivityShouldBeFound("type.equals=" + DEFAULT_TYPE);

        // Get all the activityList where type equals to UPDATED_TYPE
        defaultActivityShouldNotBeFound("type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    public void getAllActivitiesByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where type in DEFAULT_TYPE or UPDATED_TYPE
        defaultActivityShouldBeFound("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE);

        // Get all the activityList where type equals to UPDATED_TYPE
        defaultActivityShouldNotBeFound("type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    public void getAllActivitiesByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where type is not null
        defaultActivityShouldBeFound("type.specified=true");

        // Get all the activityList where type is null
        defaultActivityShouldNotBeFound("type.specified=false");
    }

    @Test
    @Transactional
    public void getAllActivitiesByNumberOfPlayersIsEqualToSomething() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where numberOfPlayers equals to DEFAULT_NUMBER_OF_PLAYERS
        defaultActivityShouldBeFound("numberOfPlayers.equals=" + DEFAULT_NUMBER_OF_PLAYERS);

        // Get all the activityList where numberOfPlayers equals to UPDATED_NUMBER_OF_PLAYERS
        defaultActivityShouldNotBeFound("numberOfPlayers.equals=" + UPDATED_NUMBER_OF_PLAYERS);
    }

    @Test
    @Transactional
    public void getAllActivitiesByNumberOfPlayersIsInShouldWork() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where numberOfPlayers in DEFAULT_NUMBER_OF_PLAYERS or UPDATED_NUMBER_OF_PLAYERS
        defaultActivityShouldBeFound("numberOfPlayers.in=" + DEFAULT_NUMBER_OF_PLAYERS + "," + UPDATED_NUMBER_OF_PLAYERS);

        // Get all the activityList where numberOfPlayers equals to UPDATED_NUMBER_OF_PLAYERS
        defaultActivityShouldNotBeFound("numberOfPlayers.in=" + UPDATED_NUMBER_OF_PLAYERS);
    }

    @Test
    @Transactional
    public void getAllActivitiesByNumberOfPlayersIsNullOrNotNull() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where numberOfPlayers is not null
        defaultActivityShouldBeFound("numberOfPlayers.specified=true");

        // Get all the activityList where numberOfPlayers is null
        defaultActivityShouldNotBeFound("numberOfPlayers.specified=false");
    }

    @Test
    @Transactional
    public void getAllActivitiesByNumberOfPlayersIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where numberOfPlayers greater than or equals to DEFAULT_NUMBER_OF_PLAYERS
        defaultActivityShouldBeFound("numberOfPlayers.greaterOrEqualThan=" + DEFAULT_NUMBER_OF_PLAYERS);

        // Get all the activityList where numberOfPlayers greater than or equals to UPDATED_NUMBER_OF_PLAYERS
        defaultActivityShouldNotBeFound("numberOfPlayers.greaterOrEqualThan=" + UPDATED_NUMBER_OF_PLAYERS);
    }

    @Test
    @Transactional
    public void getAllActivitiesByNumberOfPlayersIsLessThanSomething() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where numberOfPlayers less than or equals to DEFAULT_NUMBER_OF_PLAYERS
        defaultActivityShouldNotBeFound("numberOfPlayers.lessThan=" + DEFAULT_NUMBER_OF_PLAYERS);

        // Get all the activityList where numberOfPlayers less than or equals to UPDATED_NUMBER_OF_PLAYERS
        defaultActivityShouldBeFound("numberOfPlayers.lessThan=" + UPDATED_NUMBER_OF_PLAYERS);
    }


    @Test
    @Transactional
    public void getAllActivitiesByOfficialDurationIsEqualToSomething() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where officialDuration equals to DEFAULT_OFFICIAL_DURATION
        defaultActivityShouldBeFound("officialDuration.equals=" + DEFAULT_OFFICIAL_DURATION);

        // Get all the activityList where officialDuration equals to UPDATED_OFFICIAL_DURATION
        defaultActivityShouldNotBeFound("officialDuration.equals=" + UPDATED_OFFICIAL_DURATION);
    }

    @Test
    @Transactional
    public void getAllActivitiesByOfficialDurationIsInShouldWork() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where officialDuration in DEFAULT_OFFICIAL_DURATION or UPDATED_OFFICIAL_DURATION
        defaultActivityShouldBeFound("officialDuration.in=" + DEFAULT_OFFICIAL_DURATION + "," + UPDATED_OFFICIAL_DURATION);

        // Get all the activityList where officialDuration equals to UPDATED_OFFICIAL_DURATION
        defaultActivityShouldNotBeFound("officialDuration.in=" + UPDATED_OFFICIAL_DURATION);
    }

    @Test
    @Transactional
    public void getAllActivitiesByOfficialDurationIsNullOrNotNull() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where officialDuration is not null
        defaultActivityShouldBeFound("officialDuration.specified=true");

        // Get all the activityList where officialDuration is null
        defaultActivityShouldNotBeFound("officialDuration.specified=false");
    }

    @Test
    @Transactional
    public void getAllActivitiesByOfficialRulesIsEqualToSomething() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where officialRules equals to DEFAULT_OFFICIAL_RULES
        defaultActivityShouldBeFound("officialRules.equals=" + DEFAULT_OFFICIAL_RULES);

        // Get all the activityList where officialRules equals to UPDATED_OFFICIAL_RULES
        defaultActivityShouldNotBeFound("officialRules.equals=" + UPDATED_OFFICIAL_RULES);
    }

    @Test
    @Transactional
    public void getAllActivitiesByOfficialRulesIsInShouldWork() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where officialRules in DEFAULT_OFFICIAL_RULES or UPDATED_OFFICIAL_RULES
        defaultActivityShouldBeFound("officialRules.in=" + DEFAULT_OFFICIAL_RULES + "," + UPDATED_OFFICIAL_RULES);

        // Get all the activityList where officialRules equals to UPDATED_OFFICIAL_RULES
        defaultActivityShouldNotBeFound("officialRules.in=" + UPDATED_OFFICIAL_RULES);
    }

    @Test
    @Transactional
    public void getAllActivitiesByOfficialRulesIsNullOrNotNull() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where officialRules is not null
        defaultActivityShouldBeFound("officialRules.specified=true");

        // Get all the activityList where officialRules is null
        defaultActivityShouldNotBeFound("officialRules.specified=false");
    }

    @Test
    @Transactional
    public void getAllActivitiesByShortDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where shortDescription equals to DEFAULT_SHORT_DESCRIPTION
        defaultActivityShouldBeFound("shortDescription.equals=" + DEFAULT_SHORT_DESCRIPTION);

        // Get all the activityList where shortDescription equals to UPDATED_SHORT_DESCRIPTION
        defaultActivityShouldNotBeFound("shortDescription.equals=" + UPDATED_SHORT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllActivitiesByShortDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where shortDescription in DEFAULT_SHORT_DESCRIPTION or UPDATED_SHORT_DESCRIPTION
        defaultActivityShouldBeFound("shortDescription.in=" + DEFAULT_SHORT_DESCRIPTION + "," + UPDATED_SHORT_DESCRIPTION);

        // Get all the activityList where shortDescription equals to UPDATED_SHORT_DESCRIPTION
        defaultActivityShouldNotBeFound("shortDescription.in=" + UPDATED_SHORT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllActivitiesByShortDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where shortDescription is not null
        defaultActivityShouldBeFound("shortDescription.specified=true");

        // Get all the activityList where shortDescription is null
        defaultActivityShouldNotBeFound("shortDescription.specified=false");
    }

    @Test
    @Transactional
    public void getAllActivitiesByRecommendedGearIsEqualToSomething() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where recommendedGear equals to DEFAULT_RECOMMENDED_GEAR
        defaultActivityShouldBeFound("recommendedGear.equals=" + DEFAULT_RECOMMENDED_GEAR);

        // Get all the activityList where recommendedGear equals to UPDATED_RECOMMENDED_GEAR
        defaultActivityShouldNotBeFound("recommendedGear.equals=" + UPDATED_RECOMMENDED_GEAR);
    }

    @Test
    @Transactional
    public void getAllActivitiesByRecommendedGearIsInShouldWork() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where recommendedGear in DEFAULT_RECOMMENDED_GEAR or UPDATED_RECOMMENDED_GEAR
        defaultActivityShouldBeFound("recommendedGear.in=" + DEFAULT_RECOMMENDED_GEAR + "," + UPDATED_RECOMMENDED_GEAR);

        // Get all the activityList where recommendedGear equals to UPDATED_RECOMMENDED_GEAR
        defaultActivityShouldNotBeFound("recommendedGear.in=" + UPDATED_RECOMMENDED_GEAR);
    }

    @Test
    @Transactional
    public void getAllActivitiesByRecommendedGearIsNullOrNotNull() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where recommendedGear is not null
        defaultActivityShouldBeFound("recommendedGear.specified=true");

        // Get all the activityList where recommendedGear is null
        defaultActivityShouldNotBeFound("recommendedGear.specified=false");
    }

    @Test
    @Transactional
    public void getAllActivitiesByLongDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where longDescription equals to DEFAULT_LONG_DESCRIPTION
        defaultActivityShouldBeFound("longDescription.equals=" + DEFAULT_LONG_DESCRIPTION);

        // Get all the activityList where longDescription equals to UPDATED_LONG_DESCRIPTION
        defaultActivityShouldNotBeFound("longDescription.equals=" + UPDATED_LONG_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllActivitiesByLongDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where longDescription in DEFAULT_LONG_DESCRIPTION or UPDATED_LONG_DESCRIPTION
        defaultActivityShouldBeFound("longDescription.in=" + DEFAULT_LONG_DESCRIPTION + "," + UPDATED_LONG_DESCRIPTION);

        // Get all the activityList where longDescription equals to UPDATED_LONG_DESCRIPTION
        defaultActivityShouldNotBeFound("longDescription.in=" + UPDATED_LONG_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllActivitiesByLongDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where longDescription is not null
        defaultActivityShouldBeFound("longDescription.specified=true");

        // Get all the activityList where longDescription is null
        defaultActivityShouldNotBeFound("longDescription.specified=false");
    }

    @Test
    @Transactional
    public void getAllActivitiesByActivityPlaceIsEqualToSomething() throws Exception {
        // Initialize the database
        Place activityPlace = PlaceResourceIntTest.createEntity(em);
        em.persist(activityPlace);
        em.flush();
        activity.addActivityPlace(activityPlace);
        activityRepository.saveAndFlush(activity);
        Long activityPlaceId = activityPlace.getId();

        // Get all the activityList where activityPlace equals to activityPlaceId
        defaultActivityShouldBeFound("activityPlaceId.equals=" + activityPlaceId);

        // Get all the activityList where activityPlace equals to activityPlaceId + 1
        defaultActivityShouldNotBeFound("activityPlaceId.equals=" + (activityPlaceId + 1));
    }


    @Test
    @Transactional
    public void getAllActivitiesByActivityEventIsEqualToSomething() throws Exception {
        // Initialize the database
        Event activityEvent = EventResourceIntTest.createEntity(em);
        em.persist(activityEvent);
        em.flush();
        activity.addActivityEvent(activityEvent);
        activityRepository.saveAndFlush(activity);
        Long activityEventId = activityEvent.getId();

        // Get all the activityList where activityEvent equals to activityEventId
        defaultActivityShouldBeFound("activityEventId.equals=" + activityEventId);

        // Get all the activityList where activityEvent equals to activityEventId + 1
        defaultActivityShouldNotBeFound("activityEventId.equals=" + (activityEventId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultActivityShouldBeFound(String filter) throws Exception {
        restActivityMockMvc.perform(get("/api/activities?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(activity.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].numberOfPlayers").value(hasItem(DEFAULT_NUMBER_OF_PLAYERS)))
            .andExpect(jsonPath("$.[*].officialDuration").value(hasItem(DEFAULT_OFFICIAL_DURATION)))
            .andExpect(jsonPath("$.[*].officialRules").value(hasItem(DEFAULT_OFFICIAL_RULES)))
            .andExpect(jsonPath("$.[*].shortDescription").value(hasItem(DEFAULT_SHORT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].recommendedGear").value(hasItem(DEFAULT_RECOMMENDED_GEAR)))
            .andExpect(jsonPath("$.[*].longDescription").value(hasItem(DEFAULT_LONG_DESCRIPTION)));

        // Check, that the count call also returns 1
        restActivityMockMvc.perform(get("/api/activities/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultActivityShouldNotBeFound(String filter) throws Exception {
        restActivityMockMvc.perform(get("/api/activities?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restActivityMockMvc.perform(get("/api/activities/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingActivity() throws Exception {
        // Get the activity
        restActivityMockMvc.perform(get("/api/activities/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateActivity() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        int databaseSizeBeforeUpdate = activityRepository.findAll().size();

        // Update the activity
        Activity updatedActivity = activityRepository.findById(activity.getId()).get();
        // Disconnect from session so that the updates on updatedActivity are not directly saved in db
        em.detach(updatedActivity);
        updatedActivity
            .type(UPDATED_TYPE)
            .numberOfPlayers(UPDATED_NUMBER_OF_PLAYERS)
            .officialDuration(UPDATED_OFFICIAL_DURATION)
            .officialRules(UPDATED_OFFICIAL_RULES)
            .shortDescription(UPDATED_SHORT_DESCRIPTION)
            .recommendedGear(UPDATED_RECOMMENDED_GEAR)
            .longDescription(UPDATED_LONG_DESCRIPTION);
        ActivityDTO activityDTO = activityMapper.toDto(updatedActivity);

        restActivityMockMvc.perform(put("/api/activities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(activityDTO)))
            .andExpect(status().isOk());

        // Validate the Activity in the database
        List<Activity> activityList = activityRepository.findAll();
        assertThat(activityList).hasSize(databaseSizeBeforeUpdate);
        Activity testActivity = activityList.get(activityList.size() - 1);
        assertThat(testActivity.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testActivity.getNumberOfPlayers()).isEqualTo(UPDATED_NUMBER_OF_PLAYERS);
        assertThat(testActivity.getOfficialDuration()).isEqualTo(UPDATED_OFFICIAL_DURATION);
        assertThat(testActivity.getOfficialRules()).isEqualTo(UPDATED_OFFICIAL_RULES);
        assertThat(testActivity.getShortDescription()).isEqualTo(UPDATED_SHORT_DESCRIPTION);
        assertThat(testActivity.getRecommendedGear()).isEqualTo(UPDATED_RECOMMENDED_GEAR);
        assertThat(testActivity.getLongDescription()).isEqualTo(UPDATED_LONG_DESCRIPTION);

        // Validate the Activity in Elasticsearch
        verify(mockActivitySearchRepository, times(1)).save(testActivity);
    }

    @Test
    @Transactional
    public void updateNonExistingActivity() throws Exception {
        int databaseSizeBeforeUpdate = activityRepository.findAll().size();

        // Create the Activity
        ActivityDTO activityDTO = activityMapper.toDto(activity);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restActivityMockMvc.perform(put("/api/activities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(activityDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Activity in the database
        List<Activity> activityList = activityRepository.findAll();
        assertThat(activityList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Activity in Elasticsearch
        verify(mockActivitySearchRepository, times(0)).save(activity);
    }

    @Test
    @Transactional
    public void deleteActivity() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        int databaseSizeBeforeDelete = activityRepository.findAll().size();

        // Delete the activity
        restActivityMockMvc.perform(delete("/api/activities/{id}", activity.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Activity> activityList = activityRepository.findAll();
        assertThat(activityList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Activity in Elasticsearch
        verify(mockActivitySearchRepository, times(1)).deleteById(activity.getId());
    }

    @Test
    @Transactional
    public void searchActivity() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);
        when(mockActivitySearchRepository.search(queryStringQuery("id:" + activity.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(activity), PageRequest.of(0, 1), 1));
        // Search the activity
        restActivityMockMvc.perform(get("/api/_search/activities?query=id:" + activity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(activity.getId().intValue())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].numberOfPlayers").value(hasItem(DEFAULT_NUMBER_OF_PLAYERS)))
            .andExpect(jsonPath("$.[*].officialDuration").value(hasItem(DEFAULT_OFFICIAL_DURATION)))
            .andExpect(jsonPath("$.[*].officialRules").value(hasItem(DEFAULT_OFFICIAL_RULES)))
            .andExpect(jsonPath("$.[*].shortDescription").value(hasItem(DEFAULT_SHORT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].recommendedGear").value(hasItem(DEFAULT_RECOMMENDED_GEAR)))
            .andExpect(jsonPath("$.[*].longDescription").value(hasItem(DEFAULT_LONG_DESCRIPTION)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Activity.class);
        Activity activity1 = new Activity();
        activity1.setId(1L);
        Activity activity2 = new Activity();
        activity2.setId(activity1.getId());
        assertThat(activity1).isEqualTo(activity2);
        activity2.setId(2L);
        assertThat(activity1).isNotEqualTo(activity2);
        activity1.setId(null);
        assertThat(activity1).isNotEqualTo(activity2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ActivityDTO.class);
        ActivityDTO activityDTO1 = new ActivityDTO();
        activityDTO1.setId(1L);
        ActivityDTO activityDTO2 = new ActivityDTO();
        assertThat(activityDTO1).isNotEqualTo(activityDTO2);
        activityDTO2.setId(activityDTO1.getId());
        assertThat(activityDTO1).isEqualTo(activityDTO2);
        activityDTO2.setId(2L);
        assertThat(activityDTO1).isNotEqualTo(activityDTO2);
        activityDTO1.setId(null);
        assertThat(activityDTO1).isNotEqualTo(activityDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(activityMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(activityMapper.fromId(null)).isNull();
    }
}
