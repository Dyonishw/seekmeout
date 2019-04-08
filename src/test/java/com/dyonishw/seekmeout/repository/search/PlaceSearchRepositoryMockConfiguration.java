package com.dyonishw.seekmeout.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of PlaceSearchRepository to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class PlaceSearchRepositoryMockConfiguration {

    @MockBean
    private PlaceSearchRepository mockPlaceSearchRepository;

}
