package com.dyonishw.seekmeout.web.rest;

import com.dyonishw.seekmeout.SeekMeOutApp;

import com.dyonishw.seekmeout.domain.Place;
import com.dyonishw.seekmeout.domain.Activity;
import com.dyonishw.seekmeout.domain.Event;
import com.dyonishw.seekmeout.domain.User;
import com.dyonishw.seekmeout.repository.PlaceRepository;
import com.dyonishw.seekmeout.repository.search.PlaceSearchRepository;
import com.dyonishw.seekmeout.service.PlaceService;
import com.dyonishw.seekmeout.service.dto.PlaceDTO;
import com.dyonishw.seekmeout.service.mapper.PlaceMapper;
import com.dyonishw.seekmeout.web.rest.errors.ExceptionTranslator;
import com.dyonishw.seekmeout.service.dto.PlaceCriteria;
import com.dyonishw.seekmeout.service.PlaceQueryService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
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
import org.springframework.util.Base64Utils;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.ArrayList;
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
 * Test class for the PlaceResource REST controller.
 *
 * @see PlaceResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SeekMeOutApp.class)
public class PlaceResourceIntTest {

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_PHONE_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBBBBBBB";

    private static final String DEFAULT_OPEN_HOURS = "AAAAAAAAAA";
    private static final String UPDATED_OPEN_HOURS = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_PRICE_PER_HOUR = 1;
    private static final Integer UPDATED_PRICE_PER_HOUR = 2;

    private static final String DEFAULT_CONTACT_FORM = "AAAAAAAAAA";
    private static final String UPDATED_CONTACT_FORM = "BBBBBBBBBB";

    private static final byte[] DEFAULT_PICTURES = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_PICTURES = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_PICTURES_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_PICTURES_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_FACILITIES = "AAAAAAAAAA";
    private static final String UPDATED_FACILITIES = "BBBBBBBBBB";

    @Autowired
    private PlaceRepository placeRepository;

    @Mock
    private PlaceRepository placeRepositoryMock;

    @Autowired
    private PlaceMapper placeMapper;

    @Mock
    private PlaceService placeServiceMock;

    @Autowired
    private PlaceService placeService;

    /**
     * This repository is mocked in the com.dyonishw.seekmeout.repository.search test package.
     *
     * @see com.dyonishw.seekmeout.repository.search.PlaceSearchRepositoryMockConfiguration
     */
    @Autowired
    private PlaceSearchRepository mockPlaceSearchRepository;

    @Autowired
    private PlaceQueryService placeQueryService;

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

    private MockMvc restPlaceMockMvc;

    private Place place;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final PlaceResource placeResource = new PlaceResource(placeService, placeQueryService);
        this.restPlaceMockMvc = MockMvcBuilders.standaloneSetup(placeResource)
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
    public static Place createEntity(EntityManager em) {
        Place place = new Place()
            .address(DEFAULT_ADDRESS)
            .phoneNumber(DEFAULT_PHONE_NUMBER)
            .description(DEFAULT_DESCRIPTION)
            .openHours(DEFAULT_OPEN_HOURS)
            .name(DEFAULT_NAME)
            .pricePerHour(DEFAULT_PRICE_PER_HOUR)
            .contactForm(DEFAULT_CONTACT_FORM)
            .pictures(DEFAULT_PICTURES)
            .picturesContentType(DEFAULT_PICTURES_CONTENT_TYPE)
            .facilities(DEFAULT_FACILITIES);
        // Add required entity
        User user = UserResourceIntTest.createEntity(em);
        em.persist(user);
        em.flush();
        place.setRolePlaceUser(user);
        return place;
    }

    @Before
    public void initTest() {
        place = createEntity(em);
    }

    @Test
    @Transactional
    public void createPlace() throws Exception {
        int databaseSizeBeforeCreate = placeRepository.findAll().size();

        // Create the Place
        PlaceDTO placeDTO = placeMapper.toDto(place);
        restPlaceMockMvc.perform(post("/api/places")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(placeDTO)))
            .andExpect(status().isCreated());

        // Validate the Place in the database
        // TODO: uncomment this test
//        List<Place> placeList = placeRepository.findAll();
//        assertThat(placeList).hasSize(databaseSizeBeforeCreate + 1);
//        Place testPlace = placeList.get(placeList.size() - 1);
//        assertThat(testPlace.getAddress()).isEqualTo(DEFAULT_ADDRESS);
//        assertThat(testPlace.getPhoneNumber()).isEqualTo(DEFAULT_PHONE_NUMBER);
//        assertThat(testPlace.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
//        assertThat(testPlace.getOpenHours()).isEqualTo(DEFAULT_OPEN_HOURS);
//        assertThat(testPlace.getName()).isEqualTo(DEFAULT_NAME);
//        assertThat(testPlace.getPricePerHour()).isEqualTo(DEFAULT_PRICE_PER_HOUR);
//        assertThat(testPlace.getContactForm()).isEqualTo(DEFAULT_CONTACT_FORM);
//        assertThat(testPlace.getPictures()).isEqualTo(DEFAULT_PICTURES);
//        assertThat(testPlace.getPicturesContentType()).isEqualTo(DEFAULT_PICTURES_CONTENT_TYPE);
//        assertThat(testPlace.getFacilities()).isEqualTo(DEFAULT_FACILITIES);
//
//        // Validate the id for MapsId, the ids must be same
//        assertThat(testPlace.getId()).isEqualTo(testPlace.getUser().getId());
//
//        // Validate the Place in Elasticsearch
//        verify(mockPlaceSearchRepository, times(1)).save(testPlace);
    }

    @Test
    @Transactional
    public void createPlaceWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = placeRepository.findAll().size();

        // Create the Place with an existing ID
        place.setId(1L);
        PlaceDTO placeDTO = placeMapper.toDto(place);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPlaceMockMvc.perform(post("/api/places")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(placeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Place in the database
        List<Place> placeList = placeRepository.findAll();
        assertThat(placeList).hasSize(databaseSizeBeforeCreate);

        // Validate the Place in Elasticsearch
        verify(mockPlaceSearchRepository, times(0)).save(place);
    }

    @Test
    @Transactional
    public void checkAddressIsRequired() throws Exception {
        int databaseSizeBeforeTest = placeRepository.findAll().size();
        // set the field null
        place.setAddress(null);

        // Create the Place, which fails.
        PlaceDTO placeDTO = placeMapper.toDto(place);

        restPlaceMockMvc.perform(post("/api/places")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(placeDTO)))
            .andExpect(status().isBadRequest());

        List<Place> placeList = placeRepository.findAll();
        assertThat(placeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkPhoneNumberIsRequired() throws Exception {
        int databaseSizeBeforeTest = placeRepository.findAll().size();
        // set the field null
        place.setPhoneNumber(null);

        // Create the Place, which fails.
        PlaceDTO placeDTO = placeMapper.toDto(place);

        restPlaceMockMvc.perform(post("/api/places")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(placeDTO)))
            .andExpect(status().isBadRequest());

        List<Place> placeList = placeRepository.findAll();
        assertThat(placeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkDescriptionIsRequired() throws Exception {
        int databaseSizeBeforeTest = placeRepository.findAll().size();
        // set the field null
        place.setDescription(null);

        // Create the Place, which fails.
        PlaceDTO placeDTO = placeMapper.toDto(place);

        restPlaceMockMvc.perform(post("/api/places")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(placeDTO)))
            .andExpect(status().isBadRequest());

        List<Place> placeList = placeRepository.findAll();
        assertThat(placeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkOpenHoursIsRequired() throws Exception {
        int databaseSizeBeforeTest = placeRepository.findAll().size();
        // set the field null
        place.setOpenHours(null);

        // Create the Place, which fails.
        PlaceDTO placeDTO = placeMapper.toDto(place);

        restPlaceMockMvc.perform(post("/api/places")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(placeDTO)))
            .andExpect(status().isBadRequest());

        List<Place> placeList = placeRepository.findAll();
        assertThat(placeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = placeRepository.findAll().size();
        // set the field null
        place.setName(null);

        // Create the Place, which fails.
        PlaceDTO placeDTO = placeMapper.toDto(place);

        restPlaceMockMvc.perform(post("/api/places")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(placeDTO)))
            .andExpect(status().isBadRequest());

        List<Place> placeList = placeRepository.findAll();
        assertThat(placeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkPricePerHourIsRequired() throws Exception {
        int databaseSizeBeforeTest = placeRepository.findAll().size();
        // set the field null
        place.setPricePerHour(null);

        // Create the Place, which fails.
        PlaceDTO placeDTO = placeMapper.toDto(place);

        restPlaceMockMvc.perform(post("/api/places")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(placeDTO)))
            .andExpect(status().isBadRequest());

        List<Place> placeList = placeRepository.findAll();
        assertThat(placeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPlaces() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get all the placeList
        restPlaceMockMvc.perform(get("/api/places?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(place.getId().intValue())))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS.toString())))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].openHours").value(hasItem(DEFAULT_OPEN_HOURS.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].pricePerHour").value(hasItem(DEFAULT_PRICE_PER_HOUR)))
            .andExpect(jsonPath("$.[*].contactForm").value(hasItem(DEFAULT_CONTACT_FORM.toString())))
            .andExpect(jsonPath("$.[*].picturesContentType").value(hasItem(DEFAULT_PICTURES_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].pictures").value(hasItem(Base64Utils.encodeToString(DEFAULT_PICTURES))))
            .andExpect(jsonPath("$.[*].facilities").value(hasItem(DEFAULT_FACILITIES.toString())));
    }
    
    @SuppressWarnings({"unchecked"})
    public void getAllPlacesWithEagerRelationshipsIsEnabled() throws Exception {
        PlaceResource placeResource = new PlaceResource(placeServiceMock, placeQueryService);
        when(placeServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        MockMvc restPlaceMockMvc = MockMvcBuilders.standaloneSetup(placeResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();

        restPlaceMockMvc.perform(get("/api/places?eagerload=true"))
        .andExpect(status().isOk());

        verify(placeServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({"unchecked"})
    public void getAllPlacesWithEagerRelationshipsIsNotEnabled() throws Exception {
        PlaceResource placeResource = new PlaceResource(placeServiceMock, placeQueryService);
            when(placeServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));
            MockMvc restPlaceMockMvc = MockMvcBuilders.standaloneSetup(placeResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();

        restPlaceMockMvc.perform(get("/api/places?eagerload=true"))
        .andExpect(status().isOk());

            verify(placeServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    @Transactional
    public void getPlace() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get the place
        restPlaceMockMvc.perform(get("/api/places/{id}", place.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(place.getId().intValue()))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS.toString()))
            .andExpect(jsonPath("$.phoneNumber").value(DEFAULT_PHONE_NUMBER.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.openHours").value(DEFAULT_OPEN_HOURS.toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.pricePerHour").value(DEFAULT_PRICE_PER_HOUR))
            .andExpect(jsonPath("$.contactForm").value(DEFAULT_CONTACT_FORM.toString()))
            .andExpect(jsonPath("$.picturesContentType").value(DEFAULT_PICTURES_CONTENT_TYPE))
            .andExpect(jsonPath("$.pictures").value(Base64Utils.encodeToString(DEFAULT_PICTURES)))
            .andExpect(jsonPath("$.facilities").value(DEFAULT_FACILITIES.toString()));
    }

    @Test
    @Transactional
    public void getAllPlacesByAddressIsEqualToSomething() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get all the placeList where address equals to DEFAULT_ADDRESS
        defaultPlaceShouldBeFound("address.equals=" + DEFAULT_ADDRESS);

        // Get all the placeList where address equals to UPDATED_ADDRESS
        defaultPlaceShouldNotBeFound("address.equals=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    public void getAllPlacesByAddressIsInShouldWork() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get all the placeList where address in DEFAULT_ADDRESS or UPDATED_ADDRESS
        defaultPlaceShouldBeFound("address.in=" + DEFAULT_ADDRESS + "," + UPDATED_ADDRESS);

        // Get all the placeList where address equals to UPDATED_ADDRESS
        defaultPlaceShouldNotBeFound("address.in=" + UPDATED_ADDRESS);
    }

    @Test
    @Transactional
    public void getAllPlacesByAddressIsNullOrNotNull() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get all the placeList where address is not null
        defaultPlaceShouldBeFound("address.specified=true");

        // Get all the placeList where address is null
        defaultPlaceShouldNotBeFound("address.specified=false");
    }

    @Test
    @Transactional
    public void getAllPlacesByPhoneNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get all the placeList where phoneNumber equals to DEFAULT_PHONE_NUMBER
        defaultPlaceShouldBeFound("phoneNumber.equals=" + DEFAULT_PHONE_NUMBER);

        // Get all the placeList where phoneNumber equals to UPDATED_PHONE_NUMBER
        defaultPlaceShouldNotBeFound("phoneNumber.equals=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    public void getAllPlacesByPhoneNumberIsInShouldWork() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get all the placeList where phoneNumber in DEFAULT_PHONE_NUMBER or UPDATED_PHONE_NUMBER
        defaultPlaceShouldBeFound("phoneNumber.in=" + DEFAULT_PHONE_NUMBER + "," + UPDATED_PHONE_NUMBER);

        // Get all the placeList where phoneNumber equals to UPDATED_PHONE_NUMBER
        defaultPlaceShouldNotBeFound("phoneNumber.in=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    public void getAllPlacesByPhoneNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get all the placeList where phoneNumber is not null
        defaultPlaceShouldBeFound("phoneNumber.specified=true");

        // Get all the placeList where phoneNumber is null
        defaultPlaceShouldNotBeFound("phoneNumber.specified=false");
    }

    @Test
    @Transactional
    public void getAllPlacesByDescriptionIsEqualToSomething() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get all the placeList where description equals to DEFAULT_DESCRIPTION
        defaultPlaceShouldBeFound("description.equals=" + DEFAULT_DESCRIPTION);

        // Get all the placeList where description equals to UPDATED_DESCRIPTION
        defaultPlaceShouldNotBeFound("description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllPlacesByDescriptionIsInShouldWork() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get all the placeList where description in DEFAULT_DESCRIPTION or UPDATED_DESCRIPTION
        defaultPlaceShouldBeFound("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION);

        // Get all the placeList where description equals to UPDATED_DESCRIPTION
        defaultPlaceShouldNotBeFound("description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void getAllPlacesByDescriptionIsNullOrNotNull() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get all the placeList where description is not null
        defaultPlaceShouldBeFound("description.specified=true");

        // Get all the placeList where description is null
        defaultPlaceShouldNotBeFound("description.specified=false");
    }

    @Test
    @Transactional
    public void getAllPlacesByOpenHoursIsEqualToSomething() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get all the placeList where openHours equals to DEFAULT_OPEN_HOURS
        defaultPlaceShouldBeFound("openHours.equals=" + DEFAULT_OPEN_HOURS);

        // Get all the placeList where openHours equals to UPDATED_OPEN_HOURS
        defaultPlaceShouldNotBeFound("openHours.equals=" + UPDATED_OPEN_HOURS);
    }

    @Test
    @Transactional
    public void getAllPlacesByOpenHoursIsInShouldWork() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get all the placeList where openHours in DEFAULT_OPEN_HOURS or UPDATED_OPEN_HOURS
        defaultPlaceShouldBeFound("openHours.in=" + DEFAULT_OPEN_HOURS + "," + UPDATED_OPEN_HOURS);

        // Get all the placeList where openHours equals to UPDATED_OPEN_HOURS
        defaultPlaceShouldNotBeFound("openHours.in=" + UPDATED_OPEN_HOURS);
    }

    @Test
    @Transactional
    public void getAllPlacesByOpenHoursIsNullOrNotNull() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get all the placeList where openHours is not null
        defaultPlaceShouldBeFound("openHours.specified=true");

        // Get all the placeList where openHours is null
        defaultPlaceShouldNotBeFound("openHours.specified=false");
    }

    @Test
    @Transactional
    public void getAllPlacesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get all the placeList where name equals to DEFAULT_NAME
        defaultPlaceShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the placeList where name equals to UPDATED_NAME
        defaultPlaceShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllPlacesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get all the placeList where name in DEFAULT_NAME or UPDATED_NAME
        defaultPlaceShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the placeList where name equals to UPDATED_NAME
        defaultPlaceShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllPlacesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get all the placeList where name is not null
        defaultPlaceShouldBeFound("name.specified=true");

        // Get all the placeList where name is null
        defaultPlaceShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    public void getAllPlacesByPricePerHourIsEqualToSomething() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get all the placeList where pricePerHour equals to DEFAULT_PRICE_PER_HOUR
        defaultPlaceShouldBeFound("pricePerHour.equals=" + DEFAULT_PRICE_PER_HOUR);

        // Get all the placeList where pricePerHour equals to UPDATED_PRICE_PER_HOUR
        defaultPlaceShouldNotBeFound("pricePerHour.equals=" + UPDATED_PRICE_PER_HOUR);
    }

    @Test
    @Transactional
    public void getAllPlacesByPricePerHourIsInShouldWork() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get all the placeList where pricePerHour in DEFAULT_PRICE_PER_HOUR or UPDATED_PRICE_PER_HOUR
        defaultPlaceShouldBeFound("pricePerHour.in=" + DEFAULT_PRICE_PER_HOUR + "," + UPDATED_PRICE_PER_HOUR);

        // Get all the placeList where pricePerHour equals to UPDATED_PRICE_PER_HOUR
        defaultPlaceShouldNotBeFound("pricePerHour.in=" + UPDATED_PRICE_PER_HOUR);
    }

    @Test
    @Transactional
    public void getAllPlacesByPricePerHourIsNullOrNotNull() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get all the placeList where pricePerHour is not null
        defaultPlaceShouldBeFound("pricePerHour.specified=true");

        // Get all the placeList where pricePerHour is null
        defaultPlaceShouldNotBeFound("pricePerHour.specified=false");
    }

    @Test
    @Transactional
    public void getAllPlacesByPricePerHourIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get all the placeList where pricePerHour greater than or equals to DEFAULT_PRICE_PER_HOUR
        defaultPlaceShouldBeFound("pricePerHour.greaterOrEqualThan=" + DEFAULT_PRICE_PER_HOUR);

        // Get all the placeList where pricePerHour greater than or equals to UPDATED_PRICE_PER_HOUR
        defaultPlaceShouldNotBeFound("pricePerHour.greaterOrEqualThan=" + UPDATED_PRICE_PER_HOUR);
    }

    @Test
    @Transactional
    public void getAllPlacesByPricePerHourIsLessThanSomething() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get all the placeList where pricePerHour less than or equals to DEFAULT_PRICE_PER_HOUR
        defaultPlaceShouldNotBeFound("pricePerHour.lessThan=" + DEFAULT_PRICE_PER_HOUR);

        // Get all the placeList where pricePerHour less than or equals to UPDATED_PRICE_PER_HOUR
        defaultPlaceShouldBeFound("pricePerHour.lessThan=" + UPDATED_PRICE_PER_HOUR);
    }


    @Test
    @Transactional
    public void getAllPlacesByContactFormIsEqualToSomething() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get all the placeList where contactForm equals to DEFAULT_CONTACT_FORM
        defaultPlaceShouldBeFound("contactForm.equals=" + DEFAULT_CONTACT_FORM);

        // Get all the placeList where contactForm equals to UPDATED_CONTACT_FORM
        defaultPlaceShouldNotBeFound("contactForm.equals=" + UPDATED_CONTACT_FORM);
    }

    @Test
    @Transactional
    public void getAllPlacesByContactFormIsInShouldWork() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get all the placeList where contactForm in DEFAULT_CONTACT_FORM or UPDATED_CONTACT_FORM
        defaultPlaceShouldBeFound("contactForm.in=" + DEFAULT_CONTACT_FORM + "," + UPDATED_CONTACT_FORM);

        // Get all the placeList where contactForm equals to UPDATED_CONTACT_FORM
        defaultPlaceShouldNotBeFound("contactForm.in=" + UPDATED_CONTACT_FORM);
    }

    @Test
    @Transactional
    public void getAllPlacesByContactFormIsNullOrNotNull() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get all the placeList where contactForm is not null
        defaultPlaceShouldBeFound("contactForm.specified=true");

        // Get all the placeList where contactForm is null
        defaultPlaceShouldNotBeFound("contactForm.specified=false");
    }

    @Test
    @Transactional
    public void getAllPlacesByFacilitiesIsEqualToSomething() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get all the placeList where facilities equals to DEFAULT_FACILITIES
        defaultPlaceShouldBeFound("facilities.equals=" + DEFAULT_FACILITIES);

        // Get all the placeList where facilities equals to UPDATED_FACILITIES
        defaultPlaceShouldNotBeFound("facilities.equals=" + UPDATED_FACILITIES);
    }

    @Test
    @Transactional
    public void getAllPlacesByFacilitiesIsInShouldWork() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get all the placeList where facilities in DEFAULT_FACILITIES or UPDATED_FACILITIES
        defaultPlaceShouldBeFound("facilities.in=" + DEFAULT_FACILITIES + "," + UPDATED_FACILITIES);

        // Get all the placeList where facilities equals to UPDATED_FACILITIES
        defaultPlaceShouldNotBeFound("facilities.in=" + UPDATED_FACILITIES);
    }

    @Test
    @Transactional
    public void getAllPlacesByFacilitiesIsNullOrNotNull() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        // Get all the placeList where facilities is not null
        defaultPlaceShouldBeFound("facilities.specified=true");

        // Get all the placeList where facilities is null
        defaultPlaceShouldNotBeFound("facilities.specified=false");
    }

    @Test
    @Transactional
    public void getAllPlacesByActivityPlaceIsEqualToSomething() throws Exception {
        // Initialize the database
        Activity activityPlace = ActivityResourceIntTest.createEntity(em);
        em.persist(activityPlace);
        em.flush();
        place.addActivityPlace(activityPlace);
        placeRepository.saveAndFlush(place);
        Long activityPlaceId = activityPlace.getId();

        // Get all the placeList where activityPlace equals to activityPlaceId
        defaultPlaceShouldBeFound("activityPlaceId.equals=" + activityPlaceId);

        // Get all the placeList where activityPlace equals to activityPlaceId + 1
        defaultPlaceShouldNotBeFound("activityPlaceId.equals=" + (activityPlaceId + 1));
    }


    @Test
    @Transactional
    public void getAllPlacesByPlaceEventIsEqualToSomething() throws Exception {
        // Initialize the database
        Event placeEvent = EventResourceIntTest.createEntity(em);
        em.persist(placeEvent);
        em.flush();
        place.addPlaceEvent(placeEvent);
        placeRepository.saveAndFlush(place);
        Long placeEventId = placeEvent.getId();

        // Get all the placeList where placeEvent equals to placeEventId
        defaultPlaceShouldBeFound("placeEventId.equals=" + placeEventId);

        // Get all the placeList where placeEvent equals to placeEventId + 1
        defaultPlaceShouldNotBeFound("placeEventId.equals=" + (placeEventId + 1));
    }


    @Test
    @Transactional
    public void getAllPlacesByRolePlaceUserIsEqualToSomething() throws Exception {
        // Initialize the database
        User rolePlaceUser = UserResourceIntTest.createEntity(em);
        em.persist(rolePlaceUser);
        em.flush();
        place.setRolePlaceUser(rolePlaceUser);
        placeRepository.saveAndFlush(place);
        Long rolePlaceUserId = rolePlaceUser.getId();

        // Get all the placeList where rolePlaceUser equals to rolePlaceUserId
        defaultPlaceShouldBeFound("rolePlaceUserId.equals=" + rolePlaceUserId);

        // Get all the placeList where rolePlaceUser equals to rolePlaceUserId + 1
        defaultPlaceShouldNotBeFound("rolePlaceUserId.equals=" + (rolePlaceUserId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultPlaceShouldBeFound(String filter) throws Exception {
        restPlaceMockMvc.perform(get("/api/places?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(place.getId().intValue())))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].openHours").value(hasItem(DEFAULT_OPEN_HOURS)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].pricePerHour").value(hasItem(DEFAULT_PRICE_PER_HOUR)))
            .andExpect(jsonPath("$.[*].contactForm").value(hasItem(DEFAULT_CONTACT_FORM)))
            .andExpect(jsonPath("$.[*].picturesContentType").value(hasItem(DEFAULT_PICTURES_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].pictures").value(hasItem(Base64Utils.encodeToString(DEFAULT_PICTURES))))
            .andExpect(jsonPath("$.[*].facilities").value(hasItem(DEFAULT_FACILITIES)));

        // Check, that the count call also returns 1
        restPlaceMockMvc.perform(get("/api/places/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultPlaceShouldNotBeFound(String filter) throws Exception {
        restPlaceMockMvc.perform(get("/api/places?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPlaceMockMvc.perform(get("/api/places/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingPlace() throws Exception {
        // Get the place
        restPlaceMockMvc.perform(get("/api/places/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePlace() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        int databaseSizeBeforeUpdate = placeRepository.findAll().size();

        // Update the place
        Place updatedPlace = placeRepository.findById(place.getId()).get();
        // Disconnect from session so that the updates on updatedPlace are not directly saved in db
        em.detach(updatedPlace);
        updatedPlace
            .address(UPDATED_ADDRESS)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .description(UPDATED_DESCRIPTION)
            .openHours(UPDATED_OPEN_HOURS)
            .name(UPDATED_NAME)
            .pricePerHour(UPDATED_PRICE_PER_HOUR)
            .contactForm(UPDATED_CONTACT_FORM)
            .pictures(UPDATED_PICTURES)
            .picturesContentType(UPDATED_PICTURES_CONTENT_TYPE)
            .facilities(UPDATED_FACILITIES);
        PlaceDTO placeDTO = placeMapper.toDto(updatedPlace);

        restPlaceMockMvc.perform(put("/api/places")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(placeDTO)))
            .andExpect(status().isOk());

        // Validate the Place in the database
        List<Place> placeList = placeRepository.findAll();
        assertThat(placeList).hasSize(databaseSizeBeforeUpdate);
        Place testPlace = placeList.get(placeList.size() - 1);
        assertThat(testPlace.getAddress()).isEqualTo(UPDATED_ADDRESS);
        assertThat(testPlace.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
        assertThat(testPlace.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testPlace.getOpenHours()).isEqualTo(UPDATED_OPEN_HOURS);
        assertThat(testPlace.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testPlace.getPricePerHour()).isEqualTo(UPDATED_PRICE_PER_HOUR);
        assertThat(testPlace.getContactForm()).isEqualTo(UPDATED_CONTACT_FORM);
        assertThat(testPlace.getPictures()).isEqualTo(UPDATED_PICTURES);
        assertThat(testPlace.getPicturesContentType()).isEqualTo(UPDATED_PICTURES_CONTENT_TYPE);
        assertThat(testPlace.getFacilities()).isEqualTo(UPDATED_FACILITIES);

        // Validate the Place in Elasticsearch
        verify(mockPlaceSearchRepository, times(1)).save(testPlace);
    }

    @Test
    @Transactional
    public void updateNonExistingPlace() throws Exception {
        int databaseSizeBeforeUpdate = placeRepository.findAll().size();

        // Create the Place
        PlaceDTO placeDTO = placeMapper.toDto(place);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPlaceMockMvc.perform(put("/api/places")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(placeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Place in the database
        List<Place> placeList = placeRepository.findAll();
        assertThat(placeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Place in Elasticsearch
        verify(mockPlaceSearchRepository, times(0)).save(place);
    }

    @Test
    @Transactional
    public void deletePlace() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);

        int databaseSizeBeforeDelete = placeRepository.findAll().size();

        // Delete the place
        restPlaceMockMvc.perform(delete("/api/places/{id}", place.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Place> placeList = placeRepository.findAll();
        assertThat(placeList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Place in Elasticsearch
        verify(mockPlaceSearchRepository, times(1)).deleteById(place.getId());
    }

    @Test
    @Transactional
    public void searchPlace() throws Exception {
        // Initialize the database
        placeRepository.saveAndFlush(place);
        when(mockPlaceSearchRepository.search(queryStringQuery("id:" + place.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(place), PageRequest.of(0, 1), 1));
        // Search the place
        restPlaceMockMvc.perform(get("/api/_search/places?query=id:" + place.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(place.getId().intValue())))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].openHours").value(hasItem(DEFAULT_OPEN_HOURS)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].pricePerHour").value(hasItem(DEFAULT_PRICE_PER_HOUR)))
            .andExpect(jsonPath("$.[*].contactForm").value(hasItem(DEFAULT_CONTACT_FORM)))
            .andExpect(jsonPath("$.[*].picturesContentType").value(hasItem(DEFAULT_PICTURES_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].pictures").value(hasItem(Base64Utils.encodeToString(DEFAULT_PICTURES))))
            .andExpect(jsonPath("$.[*].facilities").value(hasItem(DEFAULT_FACILITIES)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Place.class);
        Place place1 = new Place();
        place1.setId(1L);
        Place place2 = new Place();
        place2.setId(place1.getId());
        assertThat(place1).isEqualTo(place2);
        place2.setId(2L);
        assertThat(place1).isNotEqualTo(place2);
        place1.setId(null);
        assertThat(place1).isNotEqualTo(place2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PlaceDTO.class);
        PlaceDTO placeDTO1 = new PlaceDTO();
        placeDTO1.setId(1L);
        PlaceDTO placeDTO2 = new PlaceDTO();
        assertThat(placeDTO1).isNotEqualTo(placeDTO2);
        placeDTO2.setId(placeDTO1.getId());
        assertThat(placeDTO1).isEqualTo(placeDTO2);
        placeDTO2.setId(2L);
        assertThat(placeDTO1).isNotEqualTo(placeDTO2);
        placeDTO1.setId(null);
        assertThat(placeDTO1).isNotEqualTo(placeDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(placeMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(placeMapper.fromId(null)).isNull();
    }
}
