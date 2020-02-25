package kz.kaznu.nmm.aglomer.web.rest;

import kz.kaznu.nmm.aglomer.RedisTestContainerExtension;
import kz.kaznu.nmm.aglomer.StorageApp;
import kz.kaznu.nmm.aglomer.config.SecurityBeanOverrideConfiguration;
import kz.kaznu.nmm.aglomer.domain.PropertyGroup;
import kz.kaznu.nmm.aglomer.domain.Property;
import kz.kaznu.nmm.aglomer.repository.PropertyGroupRepository;
import kz.kaznu.nmm.aglomer.repository.search.PropertyGroupSearchRepository;
import kz.kaznu.nmm.aglomer.service.PropertyGroupService;
import kz.kaznu.nmm.aglomer.service.dto.PropertyGroupDTO;
import kz.kaznu.nmm.aglomer.service.mapper.PropertyGroupMapper;
import kz.kaznu.nmm.aglomer.web.rest.errors.ExceptionTranslator;
import kz.kaznu.nmm.aglomer.service.dto.PropertyGroupCriteria;
import kz.kaznu.nmm.aglomer.service.PropertyGroupQueryService;

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
 * Integration tests for the {@link PropertyGroupResource} REST controller.
 */
@SpringBootTest(classes = {SecurityBeanOverrideConfiguration.class, StorageApp.class})
@ExtendWith(RedisTestContainerExtension.class)
public class PropertyGroupResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    @Autowired
    private PropertyGroupRepository propertyGroupRepository;

    @Autowired
    private PropertyGroupMapper propertyGroupMapper;

    @Autowired
    private PropertyGroupService propertyGroupService;

    /**
     * This repository is mocked in the kz.kaznu.nmm.aglomer.repository.search test package.
     *
     * @see kz.kaznu.nmm.aglomer.repository.search.PropertyGroupSearchRepositoryMockConfiguration
     */
    @Autowired
    private PropertyGroupSearchRepository mockPropertyGroupSearchRepository;

    @Autowired
    private PropertyGroupQueryService propertyGroupQueryService;

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

    private MockMvc restPropertyGroupMockMvc;

    private PropertyGroup propertyGroup;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final PropertyGroupResource propertyGroupResource = new PropertyGroupResource(propertyGroupService, propertyGroupQueryService);
        this.restPropertyGroupMockMvc = MockMvcBuilders.standaloneSetup(propertyGroupResource)
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
    public static PropertyGroup createEntity(EntityManager em) {
        PropertyGroup propertyGroup = new PropertyGroup()
            .name(DEFAULT_NAME);
        return propertyGroup;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PropertyGroup createUpdatedEntity(EntityManager em) {
        PropertyGroup propertyGroup = new PropertyGroup()
            .name(UPDATED_NAME);
        return propertyGroup;
    }

    @BeforeEach
    public void initTest() {
        propertyGroup = createEntity(em);
    }

    @Test
    @Transactional
    public void createPropertyGroup() throws Exception {
        int databaseSizeBeforeCreate = propertyGroupRepository.findAll().size();

        // Create the PropertyGroup
        PropertyGroupDTO propertyGroupDTO = propertyGroupMapper.toDto(propertyGroup);
        restPropertyGroupMockMvc.perform(post("/api/property-groups")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(propertyGroupDTO)))
            .andExpect(status().isCreated());

        // Validate the PropertyGroup in the database
        List<PropertyGroup> propertyGroupList = propertyGroupRepository.findAll();
        assertThat(propertyGroupList).hasSize(databaseSizeBeforeCreate + 1);
        PropertyGroup testPropertyGroup = propertyGroupList.get(propertyGroupList.size() - 1);
        assertThat(testPropertyGroup.getName()).isEqualTo(DEFAULT_NAME);

        // Validate the PropertyGroup in Elasticsearch
        verify(mockPropertyGroupSearchRepository, times(1)).save(testPropertyGroup);
    }

    @Test
    @Transactional
    public void createPropertyGroupWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = propertyGroupRepository.findAll().size();

        // Create the PropertyGroup with an existing ID
        propertyGroup.setId(1L);
        PropertyGroupDTO propertyGroupDTO = propertyGroupMapper.toDto(propertyGroup);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPropertyGroupMockMvc.perform(post("/api/property-groups")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(propertyGroupDTO)))
            .andExpect(status().isBadRequest());

        // Validate the PropertyGroup in the database
        List<PropertyGroup> propertyGroupList = propertyGroupRepository.findAll();
        assertThat(propertyGroupList).hasSize(databaseSizeBeforeCreate);

        // Validate the PropertyGroup in Elasticsearch
        verify(mockPropertyGroupSearchRepository, times(0)).save(propertyGroup);
    }


    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = propertyGroupRepository.findAll().size();
        // set the field null
        propertyGroup.setName(null);

        // Create the PropertyGroup, which fails.
        PropertyGroupDTO propertyGroupDTO = propertyGroupMapper.toDto(propertyGroup);

        restPropertyGroupMockMvc.perform(post("/api/property-groups")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(propertyGroupDTO)))
            .andExpect(status().isBadRequest());

        List<PropertyGroup> propertyGroupList = propertyGroupRepository.findAll();
        assertThat(propertyGroupList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPropertyGroups() throws Exception {
        // Initialize the database
        propertyGroupRepository.saveAndFlush(propertyGroup);

        // Get all the propertyGroupList
        restPropertyGroupMockMvc.perform(get("/api/property-groups?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(propertyGroup.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }
    
    @Test
    @Transactional
    public void getPropertyGroup() throws Exception {
        // Initialize the database
        propertyGroupRepository.saveAndFlush(propertyGroup);

        // Get the propertyGroup
        restPropertyGroupMockMvc.perform(get("/api/property-groups/{id}", propertyGroup.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(propertyGroup.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }


    @Test
    @Transactional
    public void getPropertyGroupsByIdFiltering() throws Exception {
        // Initialize the database
        propertyGroupRepository.saveAndFlush(propertyGroup);

        Long id = propertyGroup.getId();

        defaultPropertyGroupShouldBeFound("id.equals=" + id);
        defaultPropertyGroupShouldNotBeFound("id.notEquals=" + id);

        defaultPropertyGroupShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultPropertyGroupShouldNotBeFound("id.greaterThan=" + id);

        defaultPropertyGroupShouldBeFound("id.lessThanOrEqual=" + id);
        defaultPropertyGroupShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllPropertyGroupsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        propertyGroupRepository.saveAndFlush(propertyGroup);

        // Get all the propertyGroupList where name equals to DEFAULT_NAME
        defaultPropertyGroupShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the propertyGroupList where name equals to UPDATED_NAME
        defaultPropertyGroupShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllPropertyGroupsByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        propertyGroupRepository.saveAndFlush(propertyGroup);

        // Get all the propertyGroupList where name not equals to DEFAULT_NAME
        defaultPropertyGroupShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the propertyGroupList where name not equals to UPDATED_NAME
        defaultPropertyGroupShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllPropertyGroupsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        propertyGroupRepository.saveAndFlush(propertyGroup);

        // Get all the propertyGroupList where name in DEFAULT_NAME or UPDATED_NAME
        defaultPropertyGroupShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the propertyGroupList where name equals to UPDATED_NAME
        defaultPropertyGroupShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllPropertyGroupsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        propertyGroupRepository.saveAndFlush(propertyGroup);

        // Get all the propertyGroupList where name is not null
        defaultPropertyGroupShouldBeFound("name.specified=true");

        // Get all the propertyGroupList where name is null
        defaultPropertyGroupShouldNotBeFound("name.specified=false");
    }
                @Test
    @Transactional
    public void getAllPropertyGroupsByNameContainsSomething() throws Exception {
        // Initialize the database
        propertyGroupRepository.saveAndFlush(propertyGroup);

        // Get all the propertyGroupList where name contains DEFAULT_NAME
        defaultPropertyGroupShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the propertyGroupList where name contains UPDATED_NAME
        defaultPropertyGroupShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllPropertyGroupsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        propertyGroupRepository.saveAndFlush(propertyGroup);

        // Get all the propertyGroupList where name does not contain DEFAULT_NAME
        defaultPropertyGroupShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the propertyGroupList where name does not contain UPDATED_NAME
        defaultPropertyGroupShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }


    @Test
    @Transactional
    public void getAllPropertyGroupsByPropertyGroupPropertyIsEqualToSomething() throws Exception {
        // Initialize the database
        propertyGroupRepository.saveAndFlush(propertyGroup);
        Property propertyGroupProperty = PropertyResourceIT.createEntity(em);
        em.persist(propertyGroupProperty);
        em.flush();
        propertyGroup.addPropertyGroupProperty(propertyGroupProperty);
        propertyGroupRepository.saveAndFlush(propertyGroup);
        Long propertyGroupPropertyId = propertyGroupProperty.getId();

        // Get all the propertyGroupList where propertyGroupProperty equals to propertyGroupPropertyId
        defaultPropertyGroupShouldBeFound("propertyGroupPropertyId.equals=" + propertyGroupPropertyId);

        // Get all the propertyGroupList where propertyGroupProperty equals to propertyGroupPropertyId + 1
        defaultPropertyGroupShouldNotBeFound("propertyGroupPropertyId.equals=" + (propertyGroupPropertyId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultPropertyGroupShouldBeFound(String filter) throws Exception {
        restPropertyGroupMockMvc.perform(get("/api/property-groups?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(propertyGroup.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));

        // Check, that the count call also returns 1
        restPropertyGroupMockMvc.perform(get("/api/property-groups/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultPropertyGroupShouldNotBeFound(String filter) throws Exception {
        restPropertyGroupMockMvc.perform(get("/api/property-groups?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restPropertyGroupMockMvc.perform(get("/api/property-groups/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingPropertyGroup() throws Exception {
        // Get the propertyGroup
        restPropertyGroupMockMvc.perform(get("/api/property-groups/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePropertyGroup() throws Exception {
        // Initialize the database
        propertyGroupRepository.saveAndFlush(propertyGroup);

        int databaseSizeBeforeUpdate = propertyGroupRepository.findAll().size();

        // Update the propertyGroup
        PropertyGroup updatedPropertyGroup = propertyGroupRepository.findById(propertyGroup.getId()).get();
        // Disconnect from session so that the updates on updatedPropertyGroup are not directly saved in db
        em.detach(updatedPropertyGroup);
        updatedPropertyGroup
            .name(UPDATED_NAME);
        PropertyGroupDTO propertyGroupDTO = propertyGroupMapper.toDto(updatedPropertyGroup);

        restPropertyGroupMockMvc.perform(put("/api/property-groups")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(propertyGroupDTO)))
            .andExpect(status().isOk());

        // Validate the PropertyGroup in the database
        List<PropertyGroup> propertyGroupList = propertyGroupRepository.findAll();
        assertThat(propertyGroupList).hasSize(databaseSizeBeforeUpdate);
        PropertyGroup testPropertyGroup = propertyGroupList.get(propertyGroupList.size() - 1);
        assertThat(testPropertyGroup.getName()).isEqualTo(UPDATED_NAME);

        // Validate the PropertyGroup in Elasticsearch
        verify(mockPropertyGroupSearchRepository, times(1)).save(testPropertyGroup);
    }

    @Test
    @Transactional
    public void updateNonExistingPropertyGroup() throws Exception {
        int databaseSizeBeforeUpdate = propertyGroupRepository.findAll().size();

        // Create the PropertyGroup
        PropertyGroupDTO propertyGroupDTO = propertyGroupMapper.toDto(propertyGroup);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPropertyGroupMockMvc.perform(put("/api/property-groups")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(propertyGroupDTO)))
            .andExpect(status().isBadRequest());

        // Validate the PropertyGroup in the database
        List<PropertyGroup> propertyGroupList = propertyGroupRepository.findAll();
        assertThat(propertyGroupList).hasSize(databaseSizeBeforeUpdate);

        // Validate the PropertyGroup in Elasticsearch
        verify(mockPropertyGroupSearchRepository, times(0)).save(propertyGroup);
    }

    @Test
    @Transactional
    public void deletePropertyGroup() throws Exception {
        // Initialize the database
        propertyGroupRepository.saveAndFlush(propertyGroup);

        int databaseSizeBeforeDelete = propertyGroupRepository.findAll().size();

        // Delete the propertyGroup
        restPropertyGroupMockMvc.perform(delete("/api/property-groups/{id}", propertyGroup.getId())
            .accept(TestUtil.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<PropertyGroup> propertyGroupList = propertyGroupRepository.findAll();
        assertThat(propertyGroupList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the PropertyGroup in Elasticsearch
        verify(mockPropertyGroupSearchRepository, times(1)).deleteById(propertyGroup.getId());
    }

    @Test
    @Transactional
    public void searchPropertyGroup() throws Exception {
        // Initialize the database
        propertyGroupRepository.saveAndFlush(propertyGroup);
        when(mockPropertyGroupSearchRepository.search(queryStringQuery("id:" + propertyGroup.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(propertyGroup), PageRequest.of(0, 1), 1));
        // Search the propertyGroup
        restPropertyGroupMockMvc.perform(get("/api/_search/property-groups?query=id:" + propertyGroup.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(propertyGroup.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }
}
