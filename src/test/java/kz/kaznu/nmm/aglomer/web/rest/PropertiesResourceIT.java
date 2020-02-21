package kz.kaznu.nmm.aglomer.web.rest;

import kz.kaznu.nmm.aglomer.RedisTestContainerExtension;
import kz.kaznu.nmm.aglomer.StorageApp;
import kz.kaznu.nmm.aglomer.config.SecurityBeanOverrideConfiguration;
import kz.kaznu.nmm.aglomer.domain.Properties;
import kz.kaznu.nmm.aglomer.repository.PropertiesRepository;
import kz.kaznu.nmm.aglomer.repository.search.PropertiesSearchRepository;
import kz.kaznu.nmm.aglomer.service.PropertiesService;
import kz.kaznu.nmm.aglomer.web.rest.errors.ExceptionTranslator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

import static kz.kaznu.nmm.aglomer.web.rest.TestUtil.sameInstant;
import static kz.kaznu.nmm.aglomer.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link PropertiesResource} REST controller.
 */
@SpringBootTest(classes = {SecurityBeanOverrideConfiguration.class, StorageApp.class})
@ExtendWith(RedisTestContainerExtension.class)
public class PropertiesResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_VALUE = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_CREATED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_UPDATED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_UPDATED = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final BigDecimal DEFAULT_GROUP_ID = new BigDecimal(1);
    private static final BigDecimal UPDATED_GROUP_ID = new BigDecimal(2);

    @Autowired
    private PropertiesRepository propertiesRepository;

    @Autowired
    private PropertiesService propertiesService;

    /**
     * This repository is mocked in the kz.kaznu.nmm.aglomer.repository.search test package.
     *
     * @see kz.kaznu.nmm.aglomer.repository.search.PropertiesSearchRepositoryMockConfiguration
     */
    @Autowired
    private PropertiesSearchRepository mockPropertiesSearchRepository;

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

    private MockMvc restPropertiesMockMvc;

    private Properties properties;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final PropertiesResource propertiesResource = new PropertiesResource(propertiesService);
        this.restPropertiesMockMvc = MockMvcBuilders.standaloneSetup(propertiesResource)
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
    public static Properties createEntity(EntityManager em) {
        Properties properties = new Properties()
            .name(DEFAULT_NAME)
            .value(DEFAULT_VALUE)
            .created(DEFAULT_CREATED)
            .updated(DEFAULT_UPDATED)
            .group_id(DEFAULT_GROUP_ID);
        return properties;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Properties createUpdatedEntity(EntityManager em) {
        Properties properties = new Properties()
            .name(UPDATED_NAME)
            .value(UPDATED_VALUE)
            .created(UPDATED_CREATED)
            .updated(UPDATED_UPDATED)
            .group_id(UPDATED_GROUP_ID);
        return properties;
    }

    @BeforeEach
    public void initTest() {
        properties = createEntity(em);
    }

    @Test
    @Transactional
    public void createProperties() throws Exception {
        int databaseSizeBeforeCreate = propertiesRepository.findAll().size();

        // Create the Properties
        restPropertiesMockMvc.perform(post("/api/properties")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(properties)))
            .andExpect(status().isCreated());

        // Validate the Properties in the database
        List<Properties> propertiesList = propertiesRepository.findAll();
        assertThat(propertiesList).hasSize(databaseSizeBeforeCreate + 1);
        Properties testProperties = propertiesList.get(propertiesList.size() - 1);
        assertThat(testProperties.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testProperties.getValue()).isEqualTo(DEFAULT_VALUE);
        assertThat(testProperties.getCreated()).isEqualTo(DEFAULT_CREATED);
        assertThat(testProperties.getUpdated()).isEqualTo(DEFAULT_UPDATED);
        assertThat(testProperties.getGroup_id()).isEqualTo(DEFAULT_GROUP_ID);

        // Validate the Properties in Elasticsearch
        verify(mockPropertiesSearchRepository, times(1)).save(testProperties);
    }

    @Test
    @Transactional
    public void createPropertiesWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = propertiesRepository.findAll().size();

        // Create the Properties with an existing ID
        properties.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPropertiesMockMvc.perform(post("/api/properties")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(properties)))
            .andExpect(status().isBadRequest());

        // Validate the Properties in the database
        List<Properties> propertiesList = propertiesRepository.findAll();
        assertThat(propertiesList).hasSize(databaseSizeBeforeCreate);

        // Validate the Properties in Elasticsearch
        verify(mockPropertiesSearchRepository, times(0)).save(properties);
    }


    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = propertiesRepository.findAll().size();
        // set the field null
        properties.setName(null);

        // Create the Properties, which fails.

        restPropertiesMockMvc.perform(post("/api/properties")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(properties)))
            .andExpect(status().isBadRequest());

        List<Properties> propertiesList = propertiesRepository.findAll();
        assertThat(propertiesList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkValueIsRequired() throws Exception {
        int databaseSizeBeforeTest = propertiesRepository.findAll().size();
        // set the field null
        properties.setValue(null);

        // Create the Properties, which fails.

        restPropertiesMockMvc.perform(post("/api/properties")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(properties)))
            .andExpect(status().isBadRequest());

        List<Properties> propertiesList = propertiesRepository.findAll();
        assertThat(propertiesList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkCreatedIsRequired() throws Exception {
        int databaseSizeBeforeTest = propertiesRepository.findAll().size();
        // set the field null
        properties.setCreated(null);

        // Create the Properties, which fails.

        restPropertiesMockMvc.perform(post("/api/properties")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(properties)))
            .andExpect(status().isBadRequest());

        List<Properties> propertiesList = propertiesRepository.findAll();
        assertThat(propertiesList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkUpdatedIsRequired() throws Exception {
        int databaseSizeBeforeTest = propertiesRepository.findAll().size();
        // set the field null
        properties.setUpdated(null);

        // Create the Properties, which fails.

        restPropertiesMockMvc.perform(post("/api/properties")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(properties)))
            .andExpect(status().isBadRequest());

        List<Properties> propertiesList = propertiesRepository.findAll();
        assertThat(propertiesList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllProperties() throws Exception {
        // Initialize the database
        propertiesRepository.saveAndFlush(properties);

        // Get all the propertiesList
        restPropertiesMockMvc.perform(get("/api/properties?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(properties.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)))
            .andExpect(jsonPath("$.[*].created").value(hasItem(sameInstant(DEFAULT_CREATED))))
            .andExpect(jsonPath("$.[*].updated").value(hasItem(sameInstant(DEFAULT_UPDATED))))
            .andExpect(jsonPath("$.[*].group_id").value(hasItem(DEFAULT_GROUP_ID.intValue())));
    }
    
    @Test
    @Transactional
    public void getProperties() throws Exception {
        // Initialize the database
        propertiesRepository.saveAndFlush(properties);

        // Get the properties
        restPropertiesMockMvc.perform(get("/api/properties/{id}", properties.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(properties.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE))
            .andExpect(jsonPath("$.created").value(sameInstant(DEFAULT_CREATED)))
            .andExpect(jsonPath("$.updated").value(sameInstant(DEFAULT_UPDATED)))
            .andExpect(jsonPath("$.group_id").value(DEFAULT_GROUP_ID.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingProperties() throws Exception {
        // Get the properties
        restPropertiesMockMvc.perform(get("/api/properties/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateProperties() throws Exception {
        // Initialize the database
        propertiesService.save(properties);
        // As the test used the service layer, reset the Elasticsearch mock repository
        reset(mockPropertiesSearchRepository);

        int databaseSizeBeforeUpdate = propertiesRepository.findAll().size();

        // Update the properties
        Properties updatedProperties = propertiesRepository.findById(properties.getId()).get();
        // Disconnect from session so that the updates on updatedProperties are not directly saved in db
        em.detach(updatedProperties);
        updatedProperties
            .name(UPDATED_NAME)
            .value(UPDATED_VALUE)
            .created(UPDATED_CREATED)
            .updated(UPDATED_UPDATED)
            .group_id(UPDATED_GROUP_ID);

        restPropertiesMockMvc.perform(put("/api/properties")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedProperties)))
            .andExpect(status().isOk());

        // Validate the Properties in the database
        List<Properties> propertiesList = propertiesRepository.findAll();
        assertThat(propertiesList).hasSize(databaseSizeBeforeUpdate);
        Properties testProperties = propertiesList.get(propertiesList.size() - 1);
        assertThat(testProperties.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProperties.getValue()).isEqualTo(UPDATED_VALUE);
        assertThat(testProperties.getCreated()).isEqualTo(UPDATED_CREATED);
        assertThat(testProperties.getUpdated()).isEqualTo(UPDATED_UPDATED);
        assertThat(testProperties.getGroup_id()).isEqualTo(UPDATED_GROUP_ID);

        // Validate the Properties in Elasticsearch
        verify(mockPropertiesSearchRepository, times(1)).save(testProperties);
    }

    @Test
    @Transactional
    public void updateNonExistingProperties() throws Exception {
        int databaseSizeBeforeUpdate = propertiesRepository.findAll().size();

        // Create the Properties

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPropertiesMockMvc.perform(put("/api/properties")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(properties)))
            .andExpect(status().isBadRequest());

        // Validate the Properties in the database
        List<Properties> propertiesList = propertiesRepository.findAll();
        assertThat(propertiesList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Properties in Elasticsearch
        verify(mockPropertiesSearchRepository, times(0)).save(properties);
    }

    @Test
    @Transactional
    public void deleteProperties() throws Exception {
        // Initialize the database
        propertiesService.save(properties);

        int databaseSizeBeforeDelete = propertiesRepository.findAll().size();

        // Delete the properties
        restPropertiesMockMvc.perform(delete("/api/properties/{id}", properties.getId())
            .accept(TestUtil.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Properties> propertiesList = propertiesRepository.findAll();
        assertThat(propertiesList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Properties in Elasticsearch
        verify(mockPropertiesSearchRepository, times(1)).deleteById(properties.getId());
    }

    @Test
    @Transactional
    public void searchProperties() throws Exception {
        // Initialize the database
        propertiesService.save(properties);
        when(mockPropertiesSearchRepository.search(queryStringQuery("id:" + properties.getId())))
            .thenReturn(Collections.singletonList(properties));
        // Search the properties
        restPropertiesMockMvc.perform(get("/api/_search/properties?query=id:" + properties.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(properties.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)))
            .andExpect(jsonPath("$.[*].created").value(hasItem(sameInstant(DEFAULT_CREATED))))
            .andExpect(jsonPath("$.[*].updated").value(hasItem(sameInstant(DEFAULT_UPDATED))))
            .andExpect(jsonPath("$.[*].group_id").value(hasItem(DEFAULT_GROUP_ID.intValue())));
    }
}
