package kz.kaznu.nmm.aglomer.web.rest;

import kz.kaznu.nmm.aglomer.RedisTestContainerExtension;
import kz.kaznu.nmm.aglomer.StorageApp;
import kz.kaznu.nmm.aglomer.config.SecurityBeanOverrideConfiguration;
import kz.kaznu.nmm.aglomer.domain.PropertyGroup;
import kz.kaznu.nmm.aglomer.repository.PropertyGroupRepository;
import kz.kaznu.nmm.aglomer.repository.search.PropertyGroupSearchRepository;
import kz.kaznu.nmm.aglomer.service.PropertyGroupService;
import kz.kaznu.nmm.aglomer.service.dto.PropertyGroupDTO;
import kz.kaznu.nmm.aglomer.service.mapper.PropertyGroupMapper;
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
        final PropertyGroupResource propertyGroupResource = new PropertyGroupResource(propertyGroupService);
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
        when(mockPropertyGroupSearchRepository.search(queryStringQuery("id:" + propertyGroup.getId())))
            .thenReturn(Collections.singletonList(propertyGroup));
        // Search the propertyGroup
        restPropertyGroupMockMvc.perform(get("/api/_search/property-groups?query=id:" + propertyGroup.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(propertyGroup.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }
}
