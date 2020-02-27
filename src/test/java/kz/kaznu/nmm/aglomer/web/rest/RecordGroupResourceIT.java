package kz.kaznu.nmm.aglomer.web.rest;

import kz.kaznu.nmm.aglomer.RedisTestContainerExtension;
import kz.kaznu.nmm.aglomer.StorageApp;
import kz.kaznu.nmm.aglomer.config.SecurityBeanOverrideConfiguration;
import kz.kaznu.nmm.aglomer.domain.RecordGroup;
import kz.kaznu.nmm.aglomer.domain.RecordTemplate;
import kz.kaznu.nmm.aglomer.repository.RecordGroupRepository;
import kz.kaznu.nmm.aglomer.repository.search.RecordGroupSearchRepository;
import kz.kaznu.nmm.aglomer.service.RecordGroupService;
import kz.kaznu.nmm.aglomer.service.dto.RecordGroupDTO;
import kz.kaznu.nmm.aglomer.service.mapper.RecordGroupMapper;
import kz.kaznu.nmm.aglomer.web.rest.errors.ExceptionTranslator;
import kz.kaznu.nmm.aglomer.service.dto.RecordGroupCriteria;
import kz.kaznu.nmm.aglomer.service.RecordGroupQueryService;

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

import kz.kaznu.nmm.aglomer.domain.enumeration.RecordType;
/**
 * Integration tests for the {@link RecordGroupResource} REST controller.
 */
@SpringBootTest(classes = {SecurityBeanOverrideConfiguration.class, StorageApp.class})
@ExtendWith(RedisTestContainerExtension.class)
public class RecordGroupResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final RecordType DEFAULT_TYPE = RecordType.SURVEY;
    private static final RecordType UPDATED_TYPE = RecordType.SURVEY;

    @Autowired
    private RecordGroupRepository recordGroupRepository;

    @Autowired
    private RecordGroupMapper recordGroupMapper;

    @Autowired
    private RecordGroupService recordGroupService;

    /**
     * This repository is mocked in the kz.kaznu.nmm.aglomer.repository.search test package.
     *
     * @see kz.kaznu.nmm.aglomer.repository.search.RecordGroupSearchRepositoryMockConfiguration
     */
    @Autowired
    private RecordGroupSearchRepository mockRecordGroupSearchRepository;

    @Autowired
    private RecordGroupQueryService recordGroupQueryService;

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

    private MockMvc restRecordGroupMockMvc;

    private RecordGroup recordGroup;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final RecordGroupResource recordGroupResource = new RecordGroupResource(recordGroupService, recordGroupQueryService);
        this.restRecordGroupMockMvc = MockMvcBuilders.standaloneSetup(recordGroupResource)
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
    public static RecordGroup createEntity(EntityManager em) {
        RecordGroup recordGroup = new RecordGroup()
            .name(DEFAULT_NAME)
            .type(DEFAULT_TYPE);
        return recordGroup;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RecordGroup createUpdatedEntity(EntityManager em) {
        RecordGroup recordGroup = new RecordGroup()
            .name(UPDATED_NAME)
            .type(UPDATED_TYPE);
        return recordGroup;
    }

    @BeforeEach
    public void initTest() {
        recordGroup = createEntity(em);
    }

    @Test
    @Transactional
    public void createRecordGroup() throws Exception {
        int databaseSizeBeforeCreate = recordGroupRepository.findAll().size();

        // Create the RecordGroup
        RecordGroupDTO recordGroupDTO = recordGroupMapper.toDto(recordGroup);
        restRecordGroupMockMvc.perform(post("/api/record-groups")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(recordGroupDTO)))
            .andExpect(status().isCreated());

        // Validate the RecordGroup in the database
        List<RecordGroup> recordGroupList = recordGroupRepository.findAll();
        assertThat(recordGroupList).hasSize(databaseSizeBeforeCreate + 1);
        RecordGroup testRecordGroup = recordGroupList.get(recordGroupList.size() - 1);
        assertThat(testRecordGroup.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testRecordGroup.getType()).isEqualTo(DEFAULT_TYPE);

        // Validate the RecordGroup in Elasticsearch
        verify(mockRecordGroupSearchRepository, times(1)).save(testRecordGroup);
    }

    @Test
    @Transactional
    public void createRecordGroupWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = recordGroupRepository.findAll().size();

        // Create the RecordGroup with an existing ID
        recordGroup.setId(1L);
        RecordGroupDTO recordGroupDTO = recordGroupMapper.toDto(recordGroup);

        // An entity with an existing ID cannot be created, so this API call must fail
        restRecordGroupMockMvc.perform(post("/api/record-groups")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(recordGroupDTO)))
            .andExpect(status().isBadRequest());

        // Validate the RecordGroup in the database
        List<RecordGroup> recordGroupList = recordGroupRepository.findAll();
        assertThat(recordGroupList).hasSize(databaseSizeBeforeCreate);

        // Validate the RecordGroup in Elasticsearch
        verify(mockRecordGroupSearchRepository, times(0)).save(recordGroup);
    }


    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = recordGroupRepository.findAll().size();
        // set the field null
        recordGroup.setName(null);

        // Create the RecordGroup, which fails.
        RecordGroupDTO recordGroupDTO = recordGroupMapper.toDto(recordGroup);

        restRecordGroupMockMvc.perform(post("/api/record-groups")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(recordGroupDTO)))
            .andExpect(status().isBadRequest());

        List<RecordGroup> recordGroupList = recordGroupRepository.findAll();
        assertThat(recordGroupList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = recordGroupRepository.findAll().size();
        // set the field null
        recordGroup.setType(null);

        // Create the RecordGroup, which fails.
        RecordGroupDTO recordGroupDTO = recordGroupMapper.toDto(recordGroup);

        restRecordGroupMockMvc.perform(post("/api/record-groups")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(recordGroupDTO)))
            .andExpect(status().isBadRequest());

        List<RecordGroup> recordGroupList = recordGroupRepository.findAll();
        assertThat(recordGroupList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllRecordGroups() throws Exception {
        // Initialize the database
        recordGroupRepository.saveAndFlush(recordGroup);

        // Get all the recordGroupList
        restRecordGroupMockMvc.perform(get("/api/record-groups?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(recordGroup.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));
    }
    
    @Test
    @Transactional
    public void getRecordGroup() throws Exception {
        // Initialize the database
        recordGroupRepository.saveAndFlush(recordGroup);

        // Get the recordGroup
        restRecordGroupMockMvc.perform(get("/api/record-groups/{id}", recordGroup.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(recordGroup.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()));
    }


    @Test
    @Transactional
    public void getRecordGroupsByIdFiltering() throws Exception {
        // Initialize the database
        recordGroupRepository.saveAndFlush(recordGroup);

        Long id = recordGroup.getId();

        defaultRecordGroupShouldBeFound("id.equals=" + id);
        defaultRecordGroupShouldNotBeFound("id.notEquals=" + id);

        defaultRecordGroupShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultRecordGroupShouldNotBeFound("id.greaterThan=" + id);

        defaultRecordGroupShouldBeFound("id.lessThanOrEqual=" + id);
        defaultRecordGroupShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllRecordGroupsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        recordGroupRepository.saveAndFlush(recordGroup);

        // Get all the recordGroupList where name equals to DEFAULT_NAME
        defaultRecordGroupShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the recordGroupList where name equals to UPDATED_NAME
        defaultRecordGroupShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllRecordGroupsByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        recordGroupRepository.saveAndFlush(recordGroup);

        // Get all the recordGroupList where name not equals to DEFAULT_NAME
        defaultRecordGroupShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the recordGroupList where name not equals to UPDATED_NAME
        defaultRecordGroupShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllRecordGroupsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        recordGroupRepository.saveAndFlush(recordGroup);

        // Get all the recordGroupList where name in DEFAULT_NAME or UPDATED_NAME
        defaultRecordGroupShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the recordGroupList where name equals to UPDATED_NAME
        defaultRecordGroupShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllRecordGroupsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        recordGroupRepository.saveAndFlush(recordGroup);

        // Get all the recordGroupList where name is not null
        defaultRecordGroupShouldBeFound("name.specified=true");

        // Get all the recordGroupList where name is null
        defaultRecordGroupShouldNotBeFound("name.specified=false");
    }
                @Test
    @Transactional
    public void getAllRecordGroupsByNameContainsSomething() throws Exception {
        // Initialize the database
        recordGroupRepository.saveAndFlush(recordGroup);

        // Get all the recordGroupList where name contains DEFAULT_NAME
        defaultRecordGroupShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the recordGroupList where name contains UPDATED_NAME
        defaultRecordGroupShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllRecordGroupsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        recordGroupRepository.saveAndFlush(recordGroup);

        // Get all the recordGroupList where name does not contain DEFAULT_NAME
        defaultRecordGroupShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the recordGroupList where name does not contain UPDATED_NAME
        defaultRecordGroupShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }


    @Test
    @Transactional
    public void getAllRecordGroupsByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        recordGroupRepository.saveAndFlush(recordGroup);

        // Get all the recordGroupList where type equals to DEFAULT_TYPE
        defaultRecordGroupShouldBeFound("type.equals=" + DEFAULT_TYPE);

        // Get all the recordGroupList where type equals to UPDATED_TYPE
        defaultRecordGroupShouldNotBeFound("type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    public void getAllRecordGroupsByTypeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        recordGroupRepository.saveAndFlush(recordGroup);

        // Get all the recordGroupList where type not equals to DEFAULT_TYPE
        defaultRecordGroupShouldNotBeFound("type.notEquals=" + DEFAULT_TYPE);

        // Get all the recordGroupList where type not equals to UPDATED_TYPE
        defaultRecordGroupShouldBeFound("type.notEquals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    public void getAllRecordGroupsByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        recordGroupRepository.saveAndFlush(recordGroup);

        // Get all the recordGroupList where type in DEFAULT_TYPE or UPDATED_TYPE
        defaultRecordGroupShouldBeFound("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE);

        // Get all the recordGroupList where type equals to UPDATED_TYPE
        defaultRecordGroupShouldNotBeFound("type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    public void getAllRecordGroupsByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        recordGroupRepository.saveAndFlush(recordGroup);

        // Get all the recordGroupList where type is not null
        defaultRecordGroupShouldBeFound("type.specified=true");

        // Get all the recordGroupList where type is null
        defaultRecordGroupShouldNotBeFound("type.specified=false");
    }

    @Test
    @Transactional
    public void getAllRecordGroupsByRecordGroupRecordTemplateIsEqualToSomething() throws Exception {
        // Initialize the database
        recordGroupRepository.saveAndFlush(recordGroup);
        RecordTemplate recordGroupRecordTemplate = RecordTemplateResourceIT.createEntity(em);
        em.persist(recordGroupRecordTemplate);
        em.flush();
        recordGroup.addRecordGroupRecordTemplate(recordGroupRecordTemplate);
        recordGroupRepository.saveAndFlush(recordGroup);
        Long recordGroupRecordTemplateId = recordGroupRecordTemplate.getId();

        // Get all the recordGroupList where recordGroupRecordTemplate equals to recordGroupRecordTemplateId
        defaultRecordGroupShouldBeFound("recordGroupRecordTemplateId.equals=" + recordGroupRecordTemplateId);

        // Get all the recordGroupList where recordGroupRecordTemplate equals to recordGroupRecordTemplateId + 1
        defaultRecordGroupShouldNotBeFound("recordGroupRecordTemplateId.equals=" + (recordGroupRecordTemplateId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultRecordGroupShouldBeFound(String filter) throws Exception {
        restRecordGroupMockMvc.perform(get("/api/record-groups?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(recordGroup.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));

        // Check, that the count call also returns 1
        restRecordGroupMockMvc.perform(get("/api/record-groups/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultRecordGroupShouldNotBeFound(String filter) throws Exception {
        restRecordGroupMockMvc.perform(get("/api/record-groups?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restRecordGroupMockMvc.perform(get("/api/record-groups/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingRecordGroup() throws Exception {
        // Get the recordGroup
        restRecordGroupMockMvc.perform(get("/api/record-groups/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRecordGroup() throws Exception {
        // Initialize the database
        recordGroupRepository.saveAndFlush(recordGroup);

        int databaseSizeBeforeUpdate = recordGroupRepository.findAll().size();

        // Update the recordGroup
        RecordGroup updatedRecordGroup = recordGroupRepository.findById(recordGroup.getId()).get();
        // Disconnect from session so that the updates on updatedRecordGroup are not directly saved in db
        em.detach(updatedRecordGroup);
        updatedRecordGroup
            .name(UPDATED_NAME)
            .type(UPDATED_TYPE);
        RecordGroupDTO recordGroupDTO = recordGroupMapper.toDto(updatedRecordGroup);

        restRecordGroupMockMvc.perform(put("/api/record-groups")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(recordGroupDTO)))
            .andExpect(status().isOk());

        // Validate the RecordGroup in the database
        List<RecordGroup> recordGroupList = recordGroupRepository.findAll();
        assertThat(recordGroupList).hasSize(databaseSizeBeforeUpdate);
        RecordGroup testRecordGroup = recordGroupList.get(recordGroupList.size() - 1);
        assertThat(testRecordGroup.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testRecordGroup.getType()).isEqualTo(UPDATED_TYPE);

        // Validate the RecordGroup in Elasticsearch
        verify(mockRecordGroupSearchRepository, times(1)).save(testRecordGroup);
    }

    @Test
    @Transactional
    public void updateNonExistingRecordGroup() throws Exception {
        int databaseSizeBeforeUpdate = recordGroupRepository.findAll().size();

        // Create the RecordGroup
        RecordGroupDTO recordGroupDTO = recordGroupMapper.toDto(recordGroup);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRecordGroupMockMvc.perform(put("/api/record-groups")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(recordGroupDTO)))
            .andExpect(status().isBadRequest());

        // Validate the RecordGroup in the database
        List<RecordGroup> recordGroupList = recordGroupRepository.findAll();
        assertThat(recordGroupList).hasSize(databaseSizeBeforeUpdate);

        // Validate the RecordGroup in Elasticsearch
        verify(mockRecordGroupSearchRepository, times(0)).save(recordGroup);
    }

    @Test
    @Transactional
    public void deleteRecordGroup() throws Exception {
        // Initialize the database
        recordGroupRepository.saveAndFlush(recordGroup);

        int databaseSizeBeforeDelete = recordGroupRepository.findAll().size();

        // Delete the recordGroup
        restRecordGroupMockMvc.perform(delete("/api/record-groups/{id}", recordGroup.getId())
            .accept(TestUtil.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<RecordGroup> recordGroupList = recordGroupRepository.findAll();
        assertThat(recordGroupList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the RecordGroup in Elasticsearch
        verify(mockRecordGroupSearchRepository, times(1)).deleteById(recordGroup.getId());
    }

    @Test
    @Transactional
    public void searchRecordGroup() throws Exception {
        // Initialize the database
        recordGroupRepository.saveAndFlush(recordGroup);
        when(mockRecordGroupSearchRepository.search(queryStringQuery("id:" + recordGroup.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(recordGroup), PageRequest.of(0, 1), 1));
        // Search the recordGroup
        restRecordGroupMockMvc.perform(get("/api/_search/record-groups?query=id:" + recordGroup.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(recordGroup.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));
    }
}
