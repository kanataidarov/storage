package kz.kaznu.nmm.aglomer.web.rest;

import kz.kaznu.nmm.aglomer.RedisTestContainerExtension;
import kz.kaznu.nmm.aglomer.StorageApp;
import kz.kaznu.nmm.aglomer.config.SecurityBeanOverrideConfiguration;
import kz.kaznu.nmm.aglomer.domain.RecordTemplate;
import kz.kaznu.nmm.aglomer.domain.RecordField;
import kz.kaznu.nmm.aglomer.domain.RecordGroup;
import kz.kaznu.nmm.aglomer.repository.RecordTemplateRepository;
import kz.kaznu.nmm.aglomer.repository.search.RecordTemplateSearchRepository;
import kz.kaznu.nmm.aglomer.service.RecordTemplateService;
import kz.kaznu.nmm.aglomer.service.dto.RecordTemplateDTO;
import kz.kaznu.nmm.aglomer.service.mapper.RecordTemplateMapper;
import kz.kaznu.nmm.aglomer.web.rest.errors.ExceptionTranslator;
import kz.kaznu.nmm.aglomer.service.dto.RecordTemplateCriteria;
import kz.kaznu.nmm.aglomer.service.RecordTemplateQueryService;

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

import kz.kaznu.nmm.aglomer.domain.enumeration.Language;
/**
 * Integration tests for the {@link RecordTemplateResource} REST controller.
 */
@SpringBootTest(classes = {SecurityBeanOverrideConfiguration.class, StorageApp.class})
@ExtendWith(RedisTestContainerExtension.class)
public class RecordTemplateResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Language DEFAULT_LANGUAGE = Language.RUSSIAN;
    private static final Language UPDATED_LANGUAGE = Language.ENGLISH;

    @Autowired
    private RecordTemplateRepository recordTemplateRepository;

    @Autowired
    private RecordTemplateMapper recordTemplateMapper;

    @Autowired
    private RecordTemplateService recordTemplateService;

    /**
     * This repository is mocked in the kz.kaznu.nmm.aglomer.repository.search test package.
     *
     * @see kz.kaznu.nmm.aglomer.repository.search.RecordTemplateSearchRepositoryMockConfiguration
     */
    @Autowired
    private RecordTemplateSearchRepository mockRecordTemplateSearchRepository;

    @Autowired
    private RecordTemplateQueryService recordTemplateQueryService;

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

    private MockMvc restRecordTemplateMockMvc;

    private RecordTemplate recordTemplate;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final RecordTemplateResource recordTemplateResource = new RecordTemplateResource(recordTemplateService, recordTemplateQueryService);
        this.restRecordTemplateMockMvc = MockMvcBuilders.standaloneSetup(recordTemplateResource)
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
    public static RecordTemplate createEntity(EntityManager em) {
        RecordTemplate recordTemplate = new RecordTemplate()
            .name(DEFAULT_NAME)
            .created(DEFAULT_CREATED)
            .updated(DEFAULT_UPDATED)
            .language(DEFAULT_LANGUAGE);
        return recordTemplate;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RecordTemplate createUpdatedEntity(EntityManager em) {
        RecordTemplate recordTemplate = new RecordTemplate()
            .name(UPDATED_NAME)
            .created(UPDATED_CREATED)
            .updated(UPDATED_UPDATED)
            .language(UPDATED_LANGUAGE);
        return recordTemplate;
    }

    @BeforeEach
    public void initTest() {
        recordTemplate = createEntity(em);
    }

    @Test
    @Transactional
    public void createRecordTemplate() throws Exception {
        int databaseSizeBeforeCreate = recordTemplateRepository.findAll().size();

        // Create the RecordTemplate
        RecordTemplateDTO recordTemplateDTO = recordTemplateMapper.toDto(recordTemplate);
        restRecordTemplateMockMvc.perform(post("/api/record-templates")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(recordTemplateDTO)))
            .andExpect(status().isCreated());

        // Validate the RecordTemplate in the database
        List<RecordTemplate> recordTemplateList = recordTemplateRepository.findAll();
        assertThat(recordTemplateList).hasSize(databaseSizeBeforeCreate + 1);
        RecordTemplate testRecordTemplate = recordTemplateList.get(recordTemplateList.size() - 1);
        assertThat(testRecordTemplate.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testRecordTemplate.getCreated()).isEqualTo(DEFAULT_CREATED);
        assertThat(testRecordTemplate.getUpdated()).isEqualTo(DEFAULT_UPDATED);
        assertThat(testRecordTemplate.getLanguage()).isEqualTo(DEFAULT_LANGUAGE);

        // Validate the RecordTemplate in Elasticsearch
        verify(mockRecordTemplateSearchRepository, times(1)).save(testRecordTemplate);
    }

    @Test
    @Transactional
    public void createRecordTemplateWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = recordTemplateRepository.findAll().size();

        // Create the RecordTemplate with an existing ID
        recordTemplate.setId(1L);
        RecordTemplateDTO recordTemplateDTO = recordTemplateMapper.toDto(recordTemplate);

        // An entity with an existing ID cannot be created, so this API call must fail
        restRecordTemplateMockMvc.perform(post("/api/record-templates")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(recordTemplateDTO)))
            .andExpect(status().isBadRequest());

        // Validate the RecordTemplate in the database
        List<RecordTemplate> recordTemplateList = recordTemplateRepository.findAll();
        assertThat(recordTemplateList).hasSize(databaseSizeBeforeCreate);

        // Validate the RecordTemplate in Elasticsearch
        verify(mockRecordTemplateSearchRepository, times(0)).save(recordTemplate);
    }


    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = recordTemplateRepository.findAll().size();
        // set the field null
        recordTemplate.setName(null);

        // Create the RecordTemplate, which fails.
        RecordTemplateDTO recordTemplateDTO = recordTemplateMapper.toDto(recordTemplate);

        restRecordTemplateMockMvc.perform(post("/api/record-templates")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(recordTemplateDTO)))
            .andExpect(status().isBadRequest());

        List<RecordTemplate> recordTemplateList = recordTemplateRepository.findAll();
        assertThat(recordTemplateList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkCreatedIsRequired() throws Exception {
        int databaseSizeBeforeTest = recordTemplateRepository.findAll().size();
        // set the field null
        recordTemplate.setCreated(null);

        // Create the RecordTemplate, which fails.
        RecordTemplateDTO recordTemplateDTO = recordTemplateMapper.toDto(recordTemplate);

        restRecordTemplateMockMvc.perform(post("/api/record-templates")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(recordTemplateDTO)))
            .andExpect(status().isBadRequest());

        List<RecordTemplate> recordTemplateList = recordTemplateRepository.findAll();
        assertThat(recordTemplateList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkUpdatedIsRequired() throws Exception {
        int databaseSizeBeforeTest = recordTemplateRepository.findAll().size();
        // set the field null
        recordTemplate.setUpdated(null);

        // Create the RecordTemplate, which fails.
        RecordTemplateDTO recordTemplateDTO = recordTemplateMapper.toDto(recordTemplate);

        restRecordTemplateMockMvc.perform(post("/api/record-templates")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(recordTemplateDTO)))
            .andExpect(status().isBadRequest());

        List<RecordTemplate> recordTemplateList = recordTemplateRepository.findAll();
        assertThat(recordTemplateList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLanguageIsRequired() throws Exception {
        int databaseSizeBeforeTest = recordTemplateRepository.findAll().size();
        // set the field null
        recordTemplate.setLanguage(null);

        // Create the RecordTemplate, which fails.
        RecordTemplateDTO recordTemplateDTO = recordTemplateMapper.toDto(recordTemplate);

        restRecordTemplateMockMvc.perform(post("/api/record-templates")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(recordTemplateDTO)))
            .andExpect(status().isBadRequest());

        List<RecordTemplate> recordTemplateList = recordTemplateRepository.findAll();
        assertThat(recordTemplateList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllRecordTemplates() throws Exception {
        // Initialize the database
        recordTemplateRepository.saveAndFlush(recordTemplate);

        // Get all the recordTemplateList
        restRecordTemplateMockMvc.perform(get("/api/record-templates?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(recordTemplate.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].created").value(hasItem(DEFAULT_CREATED.toString())))
            .andExpect(jsonPath("$.[*].updated").value(hasItem(DEFAULT_UPDATED.toString())))
            .andExpect(jsonPath("$.[*].language").value(hasItem(DEFAULT_LANGUAGE.toString())));
    }
    
    @Test
    @Transactional
    public void getRecordTemplate() throws Exception {
        // Initialize the database
        recordTemplateRepository.saveAndFlush(recordTemplate);

        // Get the recordTemplate
        restRecordTemplateMockMvc.perform(get("/api/record-templates/{id}", recordTemplate.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(recordTemplate.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.created").value(DEFAULT_CREATED.toString()))
            .andExpect(jsonPath("$.updated").value(DEFAULT_UPDATED.toString()))
            .andExpect(jsonPath("$.language").value(DEFAULT_LANGUAGE.toString()));
    }


    @Test
    @Transactional
    public void getRecordTemplatesByIdFiltering() throws Exception {
        // Initialize the database
        recordTemplateRepository.saveAndFlush(recordTemplate);

        Long id = recordTemplate.getId();

        defaultRecordTemplateShouldBeFound("id.equals=" + id);
        defaultRecordTemplateShouldNotBeFound("id.notEquals=" + id);

        defaultRecordTemplateShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultRecordTemplateShouldNotBeFound("id.greaterThan=" + id);

        defaultRecordTemplateShouldBeFound("id.lessThanOrEqual=" + id);
        defaultRecordTemplateShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllRecordTemplatesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        recordTemplateRepository.saveAndFlush(recordTemplate);

        // Get all the recordTemplateList where name equals to DEFAULT_NAME
        defaultRecordTemplateShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the recordTemplateList where name equals to UPDATED_NAME
        defaultRecordTemplateShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllRecordTemplatesByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        recordTemplateRepository.saveAndFlush(recordTemplate);

        // Get all the recordTemplateList where name not equals to DEFAULT_NAME
        defaultRecordTemplateShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the recordTemplateList where name not equals to UPDATED_NAME
        defaultRecordTemplateShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllRecordTemplatesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        recordTemplateRepository.saveAndFlush(recordTemplate);

        // Get all the recordTemplateList where name in DEFAULT_NAME or UPDATED_NAME
        defaultRecordTemplateShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the recordTemplateList where name equals to UPDATED_NAME
        defaultRecordTemplateShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllRecordTemplatesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        recordTemplateRepository.saveAndFlush(recordTemplate);

        // Get all the recordTemplateList where name is not null
        defaultRecordTemplateShouldBeFound("name.specified=true");

        // Get all the recordTemplateList where name is null
        defaultRecordTemplateShouldNotBeFound("name.specified=false");
    }
                @Test
    @Transactional
    public void getAllRecordTemplatesByNameContainsSomething() throws Exception {
        // Initialize the database
        recordTemplateRepository.saveAndFlush(recordTemplate);

        // Get all the recordTemplateList where name contains DEFAULT_NAME
        defaultRecordTemplateShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the recordTemplateList where name contains UPDATED_NAME
        defaultRecordTemplateShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllRecordTemplatesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        recordTemplateRepository.saveAndFlush(recordTemplate);

        // Get all the recordTemplateList where name does not contain DEFAULT_NAME
        defaultRecordTemplateShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the recordTemplateList where name does not contain UPDATED_NAME
        defaultRecordTemplateShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }


    @Test
    @Transactional
    public void getAllRecordTemplatesByCreatedIsEqualToSomething() throws Exception {
        // Initialize the database
        recordTemplateRepository.saveAndFlush(recordTemplate);

        // Get all the recordTemplateList where created equals to DEFAULT_CREATED
        defaultRecordTemplateShouldBeFound("created.equals=" + DEFAULT_CREATED);

        // Get all the recordTemplateList where created equals to UPDATED_CREATED
        defaultRecordTemplateShouldNotBeFound("created.equals=" + UPDATED_CREATED);
    }

    @Test
    @Transactional
    public void getAllRecordTemplatesByCreatedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        recordTemplateRepository.saveAndFlush(recordTemplate);

        // Get all the recordTemplateList where created not equals to DEFAULT_CREATED
        defaultRecordTemplateShouldNotBeFound("created.notEquals=" + DEFAULT_CREATED);

        // Get all the recordTemplateList where created not equals to UPDATED_CREATED
        defaultRecordTemplateShouldBeFound("created.notEquals=" + UPDATED_CREATED);
    }

    @Test
    @Transactional
    public void getAllRecordTemplatesByCreatedIsInShouldWork() throws Exception {
        // Initialize the database
        recordTemplateRepository.saveAndFlush(recordTemplate);

        // Get all the recordTemplateList where created in DEFAULT_CREATED or UPDATED_CREATED
        defaultRecordTemplateShouldBeFound("created.in=" + DEFAULT_CREATED + "," + UPDATED_CREATED);

        // Get all the recordTemplateList where created equals to UPDATED_CREATED
        defaultRecordTemplateShouldNotBeFound("created.in=" + UPDATED_CREATED);
    }

    @Test
    @Transactional
    public void getAllRecordTemplatesByCreatedIsNullOrNotNull() throws Exception {
        // Initialize the database
        recordTemplateRepository.saveAndFlush(recordTemplate);

        // Get all the recordTemplateList where created is not null
        defaultRecordTemplateShouldBeFound("created.specified=true");

        // Get all the recordTemplateList where created is null
        defaultRecordTemplateShouldNotBeFound("created.specified=false");
    }

    @Test
    @Transactional
    public void getAllRecordTemplatesByUpdatedIsEqualToSomething() throws Exception {
        // Initialize the database
        recordTemplateRepository.saveAndFlush(recordTemplate);

        // Get all the recordTemplateList where updated equals to DEFAULT_UPDATED
        defaultRecordTemplateShouldBeFound("updated.equals=" + DEFAULT_UPDATED);

        // Get all the recordTemplateList where updated equals to UPDATED_UPDATED
        defaultRecordTemplateShouldNotBeFound("updated.equals=" + UPDATED_UPDATED);
    }

    @Test
    @Transactional
    public void getAllRecordTemplatesByUpdatedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        recordTemplateRepository.saveAndFlush(recordTemplate);

        // Get all the recordTemplateList where updated not equals to DEFAULT_UPDATED
        defaultRecordTemplateShouldNotBeFound("updated.notEquals=" + DEFAULT_UPDATED);

        // Get all the recordTemplateList where updated not equals to UPDATED_UPDATED
        defaultRecordTemplateShouldBeFound("updated.notEquals=" + UPDATED_UPDATED);
    }

    @Test
    @Transactional
    public void getAllRecordTemplatesByUpdatedIsInShouldWork() throws Exception {
        // Initialize the database
        recordTemplateRepository.saveAndFlush(recordTemplate);

        // Get all the recordTemplateList where updated in DEFAULT_UPDATED or UPDATED_UPDATED
        defaultRecordTemplateShouldBeFound("updated.in=" + DEFAULT_UPDATED + "," + UPDATED_UPDATED);

        // Get all the recordTemplateList where updated equals to UPDATED_UPDATED
        defaultRecordTemplateShouldNotBeFound("updated.in=" + UPDATED_UPDATED);
    }

    @Test
    @Transactional
    public void getAllRecordTemplatesByUpdatedIsNullOrNotNull() throws Exception {
        // Initialize the database
        recordTemplateRepository.saveAndFlush(recordTemplate);

        // Get all the recordTemplateList where updated is not null
        defaultRecordTemplateShouldBeFound("updated.specified=true");

        // Get all the recordTemplateList where updated is null
        defaultRecordTemplateShouldNotBeFound("updated.specified=false");
    }

    @Test
    @Transactional
    public void getAllRecordTemplatesByLanguageIsEqualToSomething() throws Exception {
        // Initialize the database
        recordTemplateRepository.saveAndFlush(recordTemplate);

        // Get all the recordTemplateList where language equals to DEFAULT_LANGUAGE
        defaultRecordTemplateShouldBeFound("language.equals=" + DEFAULT_LANGUAGE);

        // Get all the recordTemplateList where language equals to UPDATED_LANGUAGE
        defaultRecordTemplateShouldNotBeFound("language.equals=" + UPDATED_LANGUAGE);
    }

    @Test
    @Transactional
    public void getAllRecordTemplatesByLanguageIsNotEqualToSomething() throws Exception {
        // Initialize the database
        recordTemplateRepository.saveAndFlush(recordTemplate);

        // Get all the recordTemplateList where language not equals to DEFAULT_LANGUAGE
        defaultRecordTemplateShouldNotBeFound("language.notEquals=" + DEFAULT_LANGUAGE);

        // Get all the recordTemplateList where language not equals to UPDATED_LANGUAGE
        defaultRecordTemplateShouldBeFound("language.notEquals=" + UPDATED_LANGUAGE);
    }

    @Test
    @Transactional
    public void getAllRecordTemplatesByLanguageIsInShouldWork() throws Exception {
        // Initialize the database
        recordTemplateRepository.saveAndFlush(recordTemplate);

        // Get all the recordTemplateList where language in DEFAULT_LANGUAGE or UPDATED_LANGUAGE
        defaultRecordTemplateShouldBeFound("language.in=" + DEFAULT_LANGUAGE + "," + UPDATED_LANGUAGE);

        // Get all the recordTemplateList where language equals to UPDATED_LANGUAGE
        defaultRecordTemplateShouldNotBeFound("language.in=" + UPDATED_LANGUAGE);
    }

    @Test
    @Transactional
    public void getAllRecordTemplatesByLanguageIsNullOrNotNull() throws Exception {
        // Initialize the database
        recordTemplateRepository.saveAndFlush(recordTemplate);

        // Get all the recordTemplateList where language is not null
        defaultRecordTemplateShouldBeFound("language.specified=true");

        // Get all the recordTemplateList where language is null
        defaultRecordTemplateShouldNotBeFound("language.specified=false");
    }

    @Test
    @Transactional
    public void getAllRecordTemplatesByRecordTemplateRecordFieldIsEqualToSomething() throws Exception {
        // Initialize the database
        recordTemplateRepository.saveAndFlush(recordTemplate);
        RecordField recordTemplateRecordField = RecordFieldResourceIT.createEntity(em);
        em.persist(recordTemplateRecordField);
        em.flush();
        recordTemplate.addRecordTemplateRecordField(recordTemplateRecordField);
        recordTemplateRepository.saveAndFlush(recordTemplate);
        Long recordTemplateRecordFieldId = recordTemplateRecordField.getId();

        // Get all the recordTemplateList where recordTemplateRecordField equals to recordTemplateRecordFieldId
        defaultRecordTemplateShouldBeFound("recordTemplateRecordFieldId.equals=" + recordTemplateRecordFieldId);

        // Get all the recordTemplateList where recordTemplateRecordField equals to recordTemplateRecordFieldId + 1
        defaultRecordTemplateShouldNotBeFound("recordTemplateRecordFieldId.equals=" + (recordTemplateRecordFieldId + 1));
    }


    @Test
    @Transactional
    public void getAllRecordTemplatesByGroupIsEqualToSomething() throws Exception {
        // Initialize the database
        recordTemplateRepository.saveAndFlush(recordTemplate);
        RecordGroup group = RecordGroupResourceIT.createEntity(em);
        em.persist(group);
        em.flush();
        recordTemplate.setGroup(group);
        recordTemplateRepository.saveAndFlush(recordTemplate);
        Long groupId = group.getId();

        // Get all the recordTemplateList where group equals to groupId
        defaultRecordTemplateShouldBeFound("groupId.equals=" + groupId);

        // Get all the recordTemplateList where group equals to groupId + 1
        defaultRecordTemplateShouldNotBeFound("groupId.equals=" + (groupId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultRecordTemplateShouldBeFound(String filter) throws Exception {
        restRecordTemplateMockMvc.perform(get("/api/record-templates?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(recordTemplate.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].created").value(hasItem(DEFAULT_CREATED.toString())))
            .andExpect(jsonPath("$.[*].updated").value(hasItem(DEFAULT_UPDATED.toString())))
            .andExpect(jsonPath("$.[*].language").value(hasItem(DEFAULT_LANGUAGE.toString())));

        // Check, that the count call also returns 1
        restRecordTemplateMockMvc.perform(get("/api/record-templates/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultRecordTemplateShouldNotBeFound(String filter) throws Exception {
        restRecordTemplateMockMvc.perform(get("/api/record-templates?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restRecordTemplateMockMvc.perform(get("/api/record-templates/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingRecordTemplate() throws Exception {
        // Get the recordTemplate
        restRecordTemplateMockMvc.perform(get("/api/record-templates/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRecordTemplate() throws Exception {
        // Initialize the database
        recordTemplateRepository.saveAndFlush(recordTemplate);

        int databaseSizeBeforeUpdate = recordTemplateRepository.findAll().size();

        // Update the recordTemplate
        RecordTemplate updatedRecordTemplate = recordTemplateRepository.findById(recordTemplate.getId()).get();
        // Disconnect from session so that the updates on updatedRecordTemplate are not directly saved in db
        em.detach(updatedRecordTemplate);
        updatedRecordTemplate
            .name(UPDATED_NAME)
            .created(UPDATED_CREATED)
            .updated(UPDATED_UPDATED)
            .language(UPDATED_LANGUAGE);
        RecordTemplateDTO recordTemplateDTO = recordTemplateMapper.toDto(updatedRecordTemplate);

        restRecordTemplateMockMvc.perform(put("/api/record-templates")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(recordTemplateDTO)))
            .andExpect(status().isOk());

        // Validate the RecordTemplate in the database
        List<RecordTemplate> recordTemplateList = recordTemplateRepository.findAll();
        assertThat(recordTemplateList).hasSize(databaseSizeBeforeUpdate);
        RecordTemplate testRecordTemplate = recordTemplateList.get(recordTemplateList.size() - 1);
        assertThat(testRecordTemplate.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testRecordTemplate.getCreated()).isEqualTo(UPDATED_CREATED);
        assertThat(testRecordTemplate.getUpdated()).isEqualTo(UPDATED_UPDATED);
        assertThat(testRecordTemplate.getLanguage()).isEqualTo(UPDATED_LANGUAGE);

        // Validate the RecordTemplate in Elasticsearch
        verify(mockRecordTemplateSearchRepository, times(1)).save(testRecordTemplate);
    }

    @Test
    @Transactional
    public void updateNonExistingRecordTemplate() throws Exception {
        int databaseSizeBeforeUpdate = recordTemplateRepository.findAll().size();

        // Create the RecordTemplate
        RecordTemplateDTO recordTemplateDTO = recordTemplateMapper.toDto(recordTemplate);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRecordTemplateMockMvc.perform(put("/api/record-templates")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(recordTemplateDTO)))
            .andExpect(status().isBadRequest());

        // Validate the RecordTemplate in the database
        List<RecordTemplate> recordTemplateList = recordTemplateRepository.findAll();
        assertThat(recordTemplateList).hasSize(databaseSizeBeforeUpdate);

        // Validate the RecordTemplate in Elasticsearch
        verify(mockRecordTemplateSearchRepository, times(0)).save(recordTemplate);
    }

    @Test
    @Transactional
    public void deleteRecordTemplate() throws Exception {
        // Initialize the database
        recordTemplateRepository.saveAndFlush(recordTemplate);

        int databaseSizeBeforeDelete = recordTemplateRepository.findAll().size();

        // Delete the recordTemplate
        restRecordTemplateMockMvc.perform(delete("/api/record-templates/{id}", recordTemplate.getId())
            .accept(TestUtil.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<RecordTemplate> recordTemplateList = recordTemplateRepository.findAll();
        assertThat(recordTemplateList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the RecordTemplate in Elasticsearch
        verify(mockRecordTemplateSearchRepository, times(1)).deleteById(recordTemplate.getId());
    }

    @Test
    @Transactional
    public void searchRecordTemplate() throws Exception {
        // Initialize the database
        recordTemplateRepository.saveAndFlush(recordTemplate);
        when(mockRecordTemplateSearchRepository.search(queryStringQuery("id:" + recordTemplate.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(recordTemplate), PageRequest.of(0, 1), 1));
        // Search the recordTemplate
        restRecordTemplateMockMvc.perform(get("/api/_search/record-templates?query=id:" + recordTemplate.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(recordTemplate.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].created").value(hasItem(DEFAULT_CREATED.toString())))
            .andExpect(jsonPath("$.[*].updated").value(hasItem(DEFAULT_UPDATED.toString())))
            .andExpect(jsonPath("$.[*].language").value(hasItem(DEFAULT_LANGUAGE.toString())));
    }
}
