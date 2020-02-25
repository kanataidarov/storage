package kz.kaznu.nmm.aglomer.web.rest;

import kz.kaznu.nmm.aglomer.RedisTestContainerExtension;
import kz.kaznu.nmm.aglomer.StorageApp;
import kz.kaznu.nmm.aglomer.config.SecurityBeanOverrideConfiguration;
import kz.kaznu.nmm.aglomer.domain.Property;
import kz.kaznu.nmm.aglomer.domain.PropertyGroup;
import kz.kaznu.nmm.aglomer.repository.PropertyRepository;
import kz.kaznu.nmm.aglomer.repository.search.PropertySearchRepository;
import kz.kaznu.nmm.aglomer.service.PropertyService;
import kz.kaznu.nmm.aglomer.service.dto.PropertyDTO;
import kz.kaznu.nmm.aglomer.service.mapper.PropertyMapper;
import kz.kaznu.nmm.aglomer.web.rest.errors.ExceptionTranslator;
import kz.kaznu.nmm.aglomer.service.dto.PropertyCriteria;
import kz.kaznu.nmm.aglomer.service.PropertyQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import static kz.kaznu.nmm.aglomer.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link PropertyResource} REST controller.
 */
@SpringBootTest(classes = {SecurityBeanOverrideConfiguration.class, StorageApp.class})
@ExtendWith(RedisTestContainerExtension.class)
public class PropertyResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private PropertyMapper propertyMapper;

    @Autowired
    private PropertyService propertyService;

    /**
     * This repository is mocked in the kz.kaznu.nmm.aglomer.repository.search test package.
     *
     * @see kz.kaznu.nmm.aglomer.repository.search.PropertySearchRepositoryMockConfiguration
     */
    @Autowired
    private PropertySearchRepository mockPropertySearchRepository;

    @Autowired
    private PropertyQueryService propertyQueryService;

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

    private MockMvc restPropertyMockMvc;

    private Property property;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final PropertyResource propertyResource = new PropertyResource(propertyService, propertyQueryService);
        this.restPropertyMockMvc = MockMvcBuilders.standaloneSetup(propertyResource)
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
    public static Property createEntity(EntityManager em) {
        Property property = new Property()
            .name(DEFAULT_NAME)
            .created(DEFAULT_CREATED)
            .updated(DEFAULT_UPDATED);
        return property;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Property createUpdatedEntity(EntityManager em) {
        Property property = new Property()
            .name(UPDATED_NAME)
            .created(UPDATED_CREATED)
            .updated(UPDATED_UPDATED);
        return property;
    }

    @BeforeEach
    public void initTest() {
        property = createEntity(em);
    }

    @Test
    @Transactional
    public void createProperty() throws Exception {
        int databaseSizeBeforeCreate = propertyRepository.findAll().size();

        // Create the Property
        PropertyDTO propertyDTO = propertyMapper.toDto(property);
        restPropertyMockMvc.perform(post("/api/properties")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(propertyDTO)))
            .andExpect(status().isCreated());

        // Validate the Property in the database
        List<Property> propertyList = propertyRepository.findAll();
        assertThat(propertyList).hasSize(databaseSizeBeforeCreate + 1);
        Property testProperty = propertyList.get(propertyList.size() - 1);
        assertThat(testProperty.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testProperty.getCreated()).isEqualTo(DEFAULT_CREATED);
        assertThat(testProperty.getUpdated()).isEqualTo(DEFAULT_UPDATED);

        // Validate the Property in Elasticsearch
        verify(mockPropertySearchRepository, times(1)).save(testProperty);
    }

    @Test
    @Transactional
    public void createPropertyWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = propertyRepository.findAll().size();

        // Create the Property with an existing ID
        property.setId(1L);
        PropertyDTO propertyDTO = propertyMapper.toDto(property);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPropertyMockMvc.perform(post("/api/properties")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(propertyDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Property in the database
        List<Property> propertyList = propertyRepository.findAll();
        assertThat(propertyList).hasSize(databaseSizeBeforeCreate);

        // Validate the Property in Elasticsearch
        verify(mockPropertySearchRepository, times(0)).save(property);
    }


    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = propertyRepository.findAll().size();
        // set the field null
        property.setName(null);

        // Create the Property, which fails.
        PropertyDTO propertyDTO = propertyMapper.toDto(property);

        restPropertyMockMvc.perform(post("/api/properties")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(propertyDTO)))
            .andExpect(status().isBadRequest());

        List<Property> propertyList = propertyRepository.findAll();
        assertThat(propertyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkCreatedIsRequired() throws Exception {
        int databaseSizeBeforeTest = propertyRepository.findAll().size();
        // set the field null
        property.setCreated(null);

        // Create the Property, which fails.
        PropertyDTO propertyDTO = propertyMapper.toDto(property);

        restPropertyMockMvc.perform(post("/api/properties")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(propertyDTO)))
            .andExpect(status().isBadRequest());

        List<Property> propertyList = propertyRepository.findAll();
        assertThat(propertyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkUpdatedIsRequired() throws Exception {
        int databaseSizeBeforeTest = propertyRepository.findAll().size();
        // set the field null
        property.setUpdated(null);

        // Create the Property, which fails.
        PropertyDTO propertyDTO = propertyMapper.toDto(property);

        restPropertyMockMvc.perform(post("/api/properties")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(propertyDTO)))
            .andExpect(status().isBadRequest());

        List<Property> propertyList = propertyRepository.findAll();
        assertThat(propertyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllProperties() throws Exception {
        // Initialize the database
        propertyRepository.saveAndFlush(property);

        // Get all the propertyList
        restPropertyMockMvc.perform(get("/api/properties?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(property.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].created").value(hasItem(DEFAULT_CREATED.toString())))
            .andExpect(jsonPath("$.[*].updated").value(hasItem(DEFAULT_UPDATED.toString())));
    }
    
    @Test
    @Transactional
    public void getProperty() throws Exception {
        // Initialize the database
        propertyRepository.saveAndFlush(property);

        // Get the property
        restPropertyMockMvc.perform(get("/api/properties/{id}", property.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(property.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.created").value(DEFAULT_CREATED.toString()))
            .andExpect(jsonPath("$.updated").value(DEFAULT_UPDATED.toString()));
    }


    @Test
    @Transactional
    public void getPropertiesByIdFiltering() throws Exception {
        // Initialize the database
        propertyRepository.saveAndFlush(property);

        Long id = property.getId();

        defaultPropertyShouldBeFound("id.equals=" + id);
        defaultPropertyShouldNotBeFound("id.notEquals=" + id);

        defaultPropertyShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultPropertyShouldNotBeFound("id.greaterThan=" + id);

        defaultPropertyShouldBeFound("id.lessThanOrEqual=" + id);
        defaultPropertyShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllPropertiesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        propertyRepository.saveAndFlush(property);

        // Get all the propertyList where name equals to DEFAULT_NAME
        defaultPropertyShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the propertyList where name equals to UPDATED_NAME
        defaultPropertyShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllPropertiesByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        propertyRepository.saveAndFlush(property);

        // Get all the propertyList where name not equals to DEFAULT_NAME
        defaultPropertyShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the propertyList where name not equals to UPDATED_NAME
        defaultPropertyShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllPropertiesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        propertyRepository.saveAndFlush(property);

        // Get all the propertyList where name in DEFAULT_NAME or UPDATED_NAME
        defaultPropertyShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the propertyList where name equals to UPDATED_NAME
        defaultPropertyShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllPropertiesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        propertyRepository.saveAndFlush(property);

        // Get all the propertyList where name is not null
        defaultPropertyShouldBeFound("name.specified=true");

        // Get all the propertyList where name is null
        defaultPropertyShouldNotBeFound("name.specified=false");
    }
                @Test
    @Transactional
    public void getAllPropertiesByNameContainsSomething() throws Exception {
        // Initialize the database
        propertyRepository.saveAndFlush(property);

        // Get all the propertyList where name contains DEFAULT_NAME
        defaultPropertyShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the propertyList where name contains UPDATED_NAME
        defaultPropertyShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllPropertiesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        propertyRepository.saveAndFlush(property);

        // Get all the propertyList where name does not contain DEFAULT_NAME
        defaultPropertyShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the propertyList where name does not contain UPDATED_NAME
        defaultPropertyShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }


    @Test
    @Transactional
    public void getAllPropertiesByCreatedIsEqualToSomething() throws Exception {
        // Initialize the database
        propertyRepository.saveAndFlush(property);

        // Get all the propertyList where created equals to DEFAULT_CREATED
        defaultPropertyShouldBeFound("created.equals=" + DEFAULT_CREATED);

        // Get all the propertyList where created equals to UPDATED_CREATED
        defaultPropertyShouldNotBeFound("created.equals=" + UPDATED_CREATED);
    }

    @Test
    @Transactional
    public void getAllPropertiesByCreatedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        propertyRepository.saveAndFlush(property);

        // Get all the propertyList where created not equals to DEFAULT_CREATED
        defaultPropertyShouldNotBeFound("created.notEquals=" + DEFAULT_CREATED);

        // Get all the propertyList where created not equals to UPDATED_CREATED
        defaultPropertyShouldBeFound("created.notEquals=" + UPDATED_CREATED);
    }

    @Test
    @Transactional
    public void getAllPropertiesByCreatedIsInShouldWork() throws Exception {
        // Initialize the database
        propertyRepository.saveAndFlush(property);

        // Get all the propertyList where created in DEFAULT_CREATED or UPDATED_CREATED
        defaultPropertyShouldBeFound("created.in=" + DEFAULT_CREATED + "," + UPDATED_CREATED);

        // Get all the propertyList where created equals to UPDATED_CREATED
        defaultPropertyShouldNotBeFound("created.in=" + UPDATED_CREATED);
    }

    @Test
    @Transactional
    public void getAllPropertiesByCreatedIsNullOrNotNull() throws Exception {
        // Initialize the database
        propertyRepository.saveAndFlush(property);

        // Get all the propertyList where created is not null
        defaultPropertyShouldBeFound("created.specified=true");

        // Get all the propertyList where created is null
        defaultPropertyShouldNotBeFound("created.specified=false");
    }

    @Test
    @Transactional
    public void getAllPropertiesByUpdatedIsEqualToSomething() throws Exception {
        // Initialize the database
        propertyRepository.saveAndFlush(property);

        // Get all the propertyList where updated equals to DEFAULT_UPDATED
        defaultPropertyShouldBeFound("updated.equals=" + DEFAULT_UPDATED);

        // Get all the propertyList where updated equals to UPDATED_UPDATED
        defaultPropertyShouldNotBeFound("updated.equals=" + UPDATED_UPDATED);
    }

    @Test
    @Transactional
    public void getAllPropertiesByUpdatedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        propertyRepository.saveAndFlush(property);

        // Get all the propertyList where updated not equals to DEFAULT_UPDATED
        defaultPropertyShouldNotBeFound("updated.notEquals=" + DEFAULT_UPDATED);

        // Get all the propertyList where updated not equals to UPDATED_UPDATED
        defaultPropertyShouldBeFound("updated.notEquals=" + UPDATED_UPDATED);
    }

    @Test
    @Transactional
    public void getAllPropertiesByUpdatedIsInShouldWork() throws Exception {
        // Initialize the database
        propertyRepository.saveAndFlush(property);

        // Get all the propertyList where updated in DEFAULT_UPDATED or UPDATED_UPDATED
        defaultPropertyShouldBeFound("updated.in=" + DEFAULT_UPDATED + "," + UPDATED_UPDATED);

        // Get all the propertyList where updated equals to UPDATED_UPDATED
        defaultPropertyShouldNotBeFound("updated.in=" + UPDATED_UPDATED);
    }

    @Test
    @Transactional
    public void getAllPropertiesByUpdatedIsNullOrNotNull() throws Exception {
        // Initialize the database
        propertyRepository.saveAndFlush(property);

        // Get all the propertyList where updated is not null
        defaultPropertyShouldBeFound("updated.specified=true");

        // Get all the propertyList where updated is null
        defaultPropertyShouldNotBeFound("updated.specified=false");
    }

    @Test
    @Transactional
    public void getAllPropertiesByGroupIsEqualToSomething() throws Exception {
        // Initialize the database
        propertyRepository.saveAndFlush(property);
        PropertyGroup group = PropertyGroupResourceIT.createEntity(em);
        em.persist(group);
        em.flush();
        property.setGroup(group);
        propertyRepository.saveAndFlush(property);
        Long groupId = group.getId();

        // Get all the propertyList where group equals to groupId
        defaultPropertyShouldBeFound("groupId.equals=" + groupId);

        // Get all the propertyList where group equals to groupId + 1
        defaultPropertyShouldNotBeFound("groupId.equals=" + (groupId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPropertyShouldBeFound(String filter) throws Exception {
        restPropertyMockMvc.perform(get("/api/properties?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(property.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].created").value(hasItem(DEFAULT_CREATED.toString())))
            .andExpect(jsonPath("$.[*].updated").value(hasItem(DEFAULT_UPDATED.toString())));

        // Check, that the count call also returns 1
        restPropertyMockMvc.perform(get("/api/properties/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPropertyShouldNotBeFound(String filter) throws Exception {
        restPropertyMockMvc.perform(get("/api/properties?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPropertyMockMvc.perform(get("/api/properties/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingProperty() throws Exception {
        // Get the property
        restPropertyMockMvc.perform(get("/api/properties/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateProperty() throws Exception {
        // Initialize the database
        propertyRepository.saveAndFlush(property);

        int databaseSizeBeforeUpdate = propertyRepository.findAll().size();

        // Update the property
        Property updatedProperty = propertyRepository.findById(property.getId()).get();
        // Disconnect from session so that the updates on updatedProperty are not directly saved in db
        em.detach(updatedProperty);
        updatedProperty
            .name(UPDATED_NAME)
            .created(UPDATED_CREATED)
            .updated(UPDATED_UPDATED);
        PropertyDTO propertyDTO = propertyMapper.toDto(updatedProperty);

        restPropertyMockMvc.perform(put("/api/properties")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(propertyDTO)))
            .andExpect(status().isOk());

        // Validate the Property in the database
        List<Property> propertyList = propertyRepository.findAll();
        assertThat(propertyList).hasSize(databaseSizeBeforeUpdate);
        Property testProperty = propertyList.get(propertyList.size() - 1);
        assertThat(testProperty.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProperty.getCreated()).isEqualTo(UPDATED_CREATED);
        assertThat(testProperty.getUpdated()).isEqualTo(UPDATED_UPDATED);

        // Validate the Property in Elasticsearch
        verify(mockPropertySearchRepository, times(1)).save(testProperty);
    }

    @Test
    @Transactional
    public void updateNonExistingProperty() throws Exception {
        int databaseSizeBeforeUpdate = propertyRepository.findAll().size();

        // Create the Property
        PropertyDTO propertyDTO = propertyMapper.toDto(property);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPropertyMockMvc.perform(put("/api/properties")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(propertyDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Property in the database
        List<Property> propertyList = propertyRepository.findAll();
        assertThat(propertyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Property in Elasticsearch
        verify(mockPropertySearchRepository, times(0)).save(property);
    }

    @Test
    @Transactional
    public void deleteProperty() throws Exception {
        // Initialize the database
        propertyRepository.saveAndFlush(property);

        int databaseSizeBeforeDelete = propertyRepository.findAll().size();

        // Delete the property
        restPropertyMockMvc.perform(delete("/api/properties/{id}", property.getId())
            .accept(TestUtil.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Property> propertyList = propertyRepository.findAll();
        assertThat(propertyList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Property in Elasticsearch
        verify(mockPropertySearchRepository, times(1)).deleteById(property.getId());
    }

    @Test
    @Transactional
    public void searchProperty() throws Exception {
        // Initialize the database
        propertyRepository.saveAndFlush(property);
        when(mockPropertySearchRepository.search(queryStringQuery("id:" + property.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(property), PageRequest.of(0, 1), 1));
        // Search the property
        restPropertyMockMvc.perform(get("/api/_search/properties?query=id:" + property.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(property.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].created").value(hasItem(DEFAULT_CREATED.toString())))
            .andExpect(jsonPath("$.[*].updated").value(hasItem(DEFAULT_UPDATED.toString())));
    }
}
