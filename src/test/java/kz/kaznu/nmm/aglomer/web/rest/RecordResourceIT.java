package kz.kaznu.nmm.aglomer.web.rest;

import kz.kaznu.nmm.aglomer.RedisTestContainerExtension;
import kz.kaznu.nmm.aglomer.StorageApp;
import kz.kaznu.nmm.aglomer.config.SecurityBeanOverrideConfiguration;
import kz.kaznu.nmm.aglomer.domain.Record;
import kz.kaznu.nmm.aglomer.domain.RecordValue;
import kz.kaznu.nmm.aglomer.repository.RecordRepository;
import kz.kaznu.nmm.aglomer.repository.search.RecordSearchRepository;
import kz.kaznu.nmm.aglomer.service.RecordService;
import kz.kaznu.nmm.aglomer.service.dto.RecordDTO;
import kz.kaznu.nmm.aglomer.service.mapper.RecordMapper;
import kz.kaznu.nmm.aglomer.web.rest.errors.ExceptionTranslator;
import kz.kaznu.nmm.aglomer.service.dto.RecordCriteria;
import kz.kaznu.nmm.aglomer.service.RecordQueryService;

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
import java.math.BigDecimal;
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
 * Integration tests for the {@link RecordResource} REST controller.
 */
@SpringBootTest(classes = {SecurityBeanOverrideConfiguration.class, StorageApp.class})
@ExtendWith(RedisTestContainerExtension.class)
public class RecordResourceIT {

    private static final BigDecimal DEFAULT_CODE = new BigDecimal(1);
    private static final BigDecimal UPDATED_CODE = new BigDecimal(2);
    private static final BigDecimal SMALLER_CODE = new BigDecimal(1 - 1);

    private static final Instant DEFAULT_CREATED = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    @Autowired
    private RecordRepository recordRepository;

    @Autowired
    private RecordMapper recordMapper;

    @Autowired
    private RecordService recordService;

    /**
     * This repository is mocked in the kz.kaznu.nmm.aglomer.repository.search test package.
     *
     * @see kz.kaznu.nmm.aglomer.repository.search.RecordSearchRepositoryMockConfiguration
     */
    @Autowired
    private RecordSearchRepository mockRecordSearchRepository;

    @Autowired
    private RecordQueryService recordQueryService;

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

    private MockMvc restRecordMockMvc;

    private Record record;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final RecordResource recordResource = new RecordResource(recordService, recordQueryService);
        this.restRecordMockMvc = MockMvcBuilders.standaloneSetup(recordResource)
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
    public static Record createEntity(EntityManager em) {
        Record record = new Record()
            .code(DEFAULT_CODE)
            .created(DEFAULT_CREATED)
            .updated(DEFAULT_UPDATED);
        return record;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Record createUpdatedEntity(EntityManager em) {
        Record record = new Record()
            .code(UPDATED_CODE)
            .created(UPDATED_CREATED)
            .updated(UPDATED_UPDATED);
        return record;
    }

    @BeforeEach
    public void initTest() {
        record = createEntity(em);
    }

    @Test
    @Transactional
    public void createRecord() throws Exception {
        int databaseSizeBeforeCreate = recordRepository.findAll().size();

        // Create the Record
        RecordDTO recordDTO = recordMapper.toDto(record);
        restRecordMockMvc.perform(post("/api/records")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(recordDTO)))
            .andExpect(status().isCreated());

        // Validate the Record in the database
        List<Record> recordList = recordRepository.findAll();
        assertThat(recordList).hasSize(databaseSizeBeforeCreate + 1);
        Record testRecord = recordList.get(recordList.size() - 1);
        assertThat(testRecord.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testRecord.getCreated()).isEqualTo(DEFAULT_CREATED);
        assertThat(testRecord.getUpdated()).isEqualTo(DEFAULT_UPDATED);

        // Validate the Record in Elasticsearch
        verify(mockRecordSearchRepository, times(1)).save(testRecord);
    }

    @Test
    @Transactional
    public void createRecordWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = recordRepository.findAll().size();

        // Create the Record with an existing ID
        record.setId(1L);
        RecordDTO recordDTO = recordMapper.toDto(record);

        // An entity with an existing ID cannot be created, so this API call must fail
        restRecordMockMvc.perform(post("/api/records")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(recordDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Record in the database
        List<Record> recordList = recordRepository.findAll();
        assertThat(recordList).hasSize(databaseSizeBeforeCreate);

        // Validate the Record in Elasticsearch
        verify(mockRecordSearchRepository, times(0)).save(record);
    }


    @Test
    @Transactional
    public void checkCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = recordRepository.findAll().size();
        // set the field null
        record.setCode(null);

        // Create the Record, which fails.
        RecordDTO recordDTO = recordMapper.toDto(record);

        restRecordMockMvc.perform(post("/api/records")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(recordDTO)))
            .andExpect(status().isBadRequest());

        List<Record> recordList = recordRepository.findAll();
        assertThat(recordList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkCreatedIsRequired() throws Exception {
        int databaseSizeBeforeTest = recordRepository.findAll().size();
        // set the field null
        record.setCreated(null);

        // Create the Record, which fails.
        RecordDTO recordDTO = recordMapper.toDto(record);

        restRecordMockMvc.perform(post("/api/records")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(recordDTO)))
            .andExpect(status().isBadRequest());

        List<Record> recordList = recordRepository.findAll();
        assertThat(recordList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkUpdatedIsRequired() throws Exception {
        int databaseSizeBeforeTest = recordRepository.findAll().size();
        // set the field null
        record.setUpdated(null);

        // Create the Record, which fails.
        RecordDTO recordDTO = recordMapper.toDto(record);

        restRecordMockMvc.perform(post("/api/records")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(recordDTO)))
            .andExpect(status().isBadRequest());

        List<Record> recordList = recordRepository.findAll();
        assertThat(recordList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllRecords() throws Exception {
        // Initialize the database
        recordRepository.saveAndFlush(record);

        // Get all the recordList
        restRecordMockMvc.perform(get("/api/records?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(record.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE.intValue())))
            .andExpect(jsonPath("$.[*].created").value(hasItem(DEFAULT_CREATED.toString())))
            .andExpect(jsonPath("$.[*].updated").value(hasItem(DEFAULT_UPDATED.toString())));
    }
    
    @Test
    @Transactional
    public void getRecord() throws Exception {
        // Initialize the database
        recordRepository.saveAndFlush(record);

        // Get the record
        restRecordMockMvc.perform(get("/api/records/{id}", record.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(record.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE.intValue()))
            .andExpect(jsonPath("$.created").value(DEFAULT_CREATED.toString()))
            .andExpect(jsonPath("$.updated").value(DEFAULT_UPDATED.toString()));
    }


    @Test
    @Transactional
    public void getRecordsByIdFiltering() throws Exception {
        // Initialize the database
        recordRepository.saveAndFlush(record);

        Long id = record.getId();

        defaultRecordShouldBeFound("id.equals=" + id);
        defaultRecordShouldNotBeFound("id.notEquals=" + id);

        defaultRecordShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultRecordShouldNotBeFound("id.greaterThan=" + id);

        defaultRecordShouldBeFound("id.lessThanOrEqual=" + id);
        defaultRecordShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllRecordsByCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        recordRepository.saveAndFlush(record);

        // Get all the recordList where code equals to DEFAULT_CODE
        defaultRecordShouldBeFound("code.equals=" + DEFAULT_CODE);

        // Get all the recordList where code equals to UPDATED_CODE
        defaultRecordShouldNotBeFound("code.equals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    public void getAllRecordsByCodeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        recordRepository.saveAndFlush(record);

        // Get all the recordList where code not equals to DEFAULT_CODE
        defaultRecordShouldNotBeFound("code.notEquals=" + DEFAULT_CODE);

        // Get all the recordList where code not equals to UPDATED_CODE
        defaultRecordShouldBeFound("code.notEquals=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    public void getAllRecordsByCodeIsInShouldWork() throws Exception {
        // Initialize the database
        recordRepository.saveAndFlush(record);

        // Get all the recordList where code in DEFAULT_CODE or UPDATED_CODE
        defaultRecordShouldBeFound("code.in=" + DEFAULT_CODE + "," + UPDATED_CODE);

        // Get all the recordList where code equals to UPDATED_CODE
        defaultRecordShouldNotBeFound("code.in=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    public void getAllRecordsByCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        recordRepository.saveAndFlush(record);

        // Get all the recordList where code is not null
        defaultRecordShouldBeFound("code.specified=true");

        // Get all the recordList where code is null
        defaultRecordShouldNotBeFound("code.specified=false");
    }

    @Test
    @Transactional
    public void getAllRecordsByCodeIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        recordRepository.saveAndFlush(record);

        // Get all the recordList where code is greater than or equal to DEFAULT_CODE
        defaultRecordShouldBeFound("code.greaterThanOrEqual=" + DEFAULT_CODE);

        // Get all the recordList where code is greater than or equal to UPDATED_CODE
        defaultRecordShouldNotBeFound("code.greaterThanOrEqual=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    public void getAllRecordsByCodeIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        recordRepository.saveAndFlush(record);

        // Get all the recordList where code is less than or equal to DEFAULT_CODE
        defaultRecordShouldBeFound("code.lessThanOrEqual=" + DEFAULT_CODE);

        // Get all the recordList where code is less than or equal to SMALLER_CODE
        defaultRecordShouldNotBeFound("code.lessThanOrEqual=" + SMALLER_CODE);
    }

    @Test
    @Transactional
    public void getAllRecordsByCodeIsLessThanSomething() throws Exception {
        // Initialize the database
        recordRepository.saveAndFlush(record);

        // Get all the recordList where code is less than DEFAULT_CODE
        defaultRecordShouldNotBeFound("code.lessThan=" + DEFAULT_CODE);

        // Get all the recordList where code is less than UPDATED_CODE
        defaultRecordShouldBeFound("code.lessThan=" + UPDATED_CODE);
    }

    @Test
    @Transactional
    public void getAllRecordsByCodeIsGreaterThanSomething() throws Exception {
        // Initialize the database
        recordRepository.saveAndFlush(record);

        // Get all the recordList where code is greater than DEFAULT_CODE
        defaultRecordShouldNotBeFound("code.greaterThan=" + DEFAULT_CODE);

        // Get all the recordList where code is greater than SMALLER_CODE
        defaultRecordShouldBeFound("code.greaterThan=" + SMALLER_CODE);
    }


    @Test
    @Transactional
    public void getAllRecordsByCreatedIsEqualToSomething() throws Exception {
        // Initialize the database
        recordRepository.saveAndFlush(record);

        // Get all the recordList where created equals to DEFAULT_CREATED
        defaultRecordShouldBeFound("created.equals=" + DEFAULT_CREATED);

        // Get all the recordList where created equals to UPDATED_CREATED
        defaultRecordShouldNotBeFound("created.equals=" + UPDATED_CREATED);
    }

    @Test
    @Transactional
    public void getAllRecordsByCreatedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        recordRepository.saveAndFlush(record);

        // Get all the recordList where created not equals to DEFAULT_CREATED
        defaultRecordShouldNotBeFound("created.notEquals=" + DEFAULT_CREATED);

        // Get all the recordList where created not equals to UPDATED_CREATED
        defaultRecordShouldBeFound("created.notEquals=" + UPDATED_CREATED);
    }

    @Test
    @Transactional
    public void getAllRecordsByCreatedIsInShouldWork() throws Exception {
        // Initialize the database
        recordRepository.saveAndFlush(record);

        // Get all the recordList where created in DEFAULT_CREATED or UPDATED_CREATED
        defaultRecordShouldBeFound("created.in=" + DEFAULT_CREATED + "," + UPDATED_CREATED);

        // Get all the recordList where created equals to UPDATED_CREATED
        defaultRecordShouldNotBeFound("created.in=" + UPDATED_CREATED);
    }

    @Test
    @Transactional
    public void getAllRecordsByCreatedIsNullOrNotNull() throws Exception {
        // Initialize the database
        recordRepository.saveAndFlush(record);

        // Get all the recordList where created is not null
        defaultRecordShouldBeFound("created.specified=true");

        // Get all the recordList where created is null
        defaultRecordShouldNotBeFound("created.specified=false");
    }

    @Test
    @Transactional
    public void getAllRecordsByUpdatedIsEqualToSomething() throws Exception {
        // Initialize the database
        recordRepository.saveAndFlush(record);

        // Get all the recordList where updated equals to DEFAULT_UPDATED
        defaultRecordShouldBeFound("updated.equals=" + DEFAULT_UPDATED);

        // Get all the recordList where updated equals to UPDATED_UPDATED
        defaultRecordShouldNotBeFound("updated.equals=" + UPDATED_UPDATED);
    }

    @Test
    @Transactional
    public void getAllRecordsByUpdatedIsNotEqualToSomething() throws Exception {
        // Initialize the database
        recordRepository.saveAndFlush(record);

        // Get all the recordList where updated not equals to DEFAULT_UPDATED
        defaultRecordShouldNotBeFound("updated.notEquals=" + DEFAULT_UPDATED);

        // Get all the recordList where updated not equals to UPDATED_UPDATED
        defaultRecordShouldBeFound("updated.notEquals=" + UPDATED_UPDATED);
    }

    @Test
    @Transactional
    public void getAllRecordsByUpdatedIsInShouldWork() throws Exception {
        // Initialize the database
        recordRepository.saveAndFlush(record);

        // Get all the recordList where updated in DEFAULT_UPDATED or UPDATED_UPDATED
        defaultRecordShouldBeFound("updated.in=" + DEFAULT_UPDATED + "," + UPDATED_UPDATED);

        // Get all the recordList where updated equals to UPDATED_UPDATED
        defaultRecordShouldNotBeFound("updated.in=" + UPDATED_UPDATED);
    }

    @Test
    @Transactional
    public void getAllRecordsByUpdatedIsNullOrNotNull() throws Exception {
        // Initialize the database
        recordRepository.saveAndFlush(record);

        // Get all the recordList where updated is not null
        defaultRecordShouldBeFound("updated.specified=true");

        // Get all the recordList where updated is null
        defaultRecordShouldNotBeFound("updated.specified=false");
    }

    @Test
    @Transactional
    public void getAllRecordsByRecordRecordValueIsEqualToSomething() throws Exception {
        // Initialize the database
        recordRepository.saveAndFlush(record);
        RecordValue recordRecordValue = RecordValueResourceIT.createEntity(em);
        em.persist(recordRecordValue);
        em.flush();
        record.addRecordRecordValue(recordRecordValue);
        recordRepository.saveAndFlush(record);
        Long recordRecordValueId = recordRecordValue.getId();

        // Get all the recordList where recordRecordValue equals to recordRecordValueId
        defaultRecordShouldBeFound("recordRecordValueId.equals=" + recordRecordValueId);

        // Get all the recordList where recordRecordValue equals to recordRecordValueId + 1
        defaultRecordShouldNotBeFound("recordRecordValueId.equals=" + (recordRecordValueId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultRecordShouldBeFound(String filter) throws Exception {
        restRecordMockMvc.perform(get("/api/records?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(record.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE.intValue())))
            .andExpect(jsonPath("$.[*].created").value(hasItem(DEFAULT_CREATED.toString())))
            .andExpect(jsonPath("$.[*].updated").value(hasItem(DEFAULT_UPDATED.toString())));

        // Check, that the count call also returns 1
        restRecordMockMvc.perform(get("/api/records/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultRecordShouldNotBeFound(String filter) throws Exception {
        restRecordMockMvc.perform(get("/api/records?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restRecordMockMvc.perform(get("/api/records/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingRecord() throws Exception {
        // Get the record
        restRecordMockMvc.perform(get("/api/records/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRecord() throws Exception {
        // Initialize the database
        recordRepository.saveAndFlush(record);

        int databaseSizeBeforeUpdate = recordRepository.findAll().size();

        // Update the record
        Record updatedRecord = recordRepository.findById(record.getId()).get();
        // Disconnect from session so that the updates on updatedRecord are not directly saved in db
        em.detach(updatedRecord);
        updatedRecord
            .code(UPDATED_CODE)
            .created(UPDATED_CREATED)
            .updated(UPDATED_UPDATED);
        RecordDTO recordDTO = recordMapper.toDto(updatedRecord);

        restRecordMockMvc.perform(put("/api/records")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(recordDTO)))
            .andExpect(status().isOk());

        // Validate the Record in the database
        List<Record> recordList = recordRepository.findAll();
        assertThat(recordList).hasSize(databaseSizeBeforeUpdate);
        Record testRecord = recordList.get(recordList.size() - 1);
        assertThat(testRecord.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testRecord.getCreated()).isEqualTo(UPDATED_CREATED);
        assertThat(testRecord.getUpdated()).isEqualTo(UPDATED_UPDATED);

        // Validate the Record in Elasticsearch
        verify(mockRecordSearchRepository, times(1)).save(testRecord);
    }

    @Test
    @Transactional
    public void updateNonExistingRecord() throws Exception {
        int databaseSizeBeforeUpdate = recordRepository.findAll().size();

        // Create the Record
        RecordDTO recordDTO = recordMapper.toDto(record);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRecordMockMvc.perform(put("/api/records")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(recordDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Record in the database
        List<Record> recordList = recordRepository.findAll();
        assertThat(recordList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Record in Elasticsearch
        verify(mockRecordSearchRepository, times(0)).save(record);
    }

    @Test
    @Transactional
    public void deleteRecord() throws Exception {
        // Initialize the database
        recordRepository.saveAndFlush(record);

        int databaseSizeBeforeDelete = recordRepository.findAll().size();

        // Delete the record
        restRecordMockMvc.perform(delete("/api/records/{id}", record.getId())
            .accept(TestUtil.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Record> recordList = recordRepository.findAll();
        assertThat(recordList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Record in Elasticsearch
        verify(mockRecordSearchRepository, times(1)).deleteById(record.getId());
    }

    @Test
    @Transactional
    public void searchRecord() throws Exception {
        // Initialize the database
        recordRepository.saveAndFlush(record);
        when(mockRecordSearchRepository.search(queryStringQuery("id:" + record.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(record), PageRequest.of(0, 1), 1));
        // Search the record
        restRecordMockMvc.perform(get("/api/_search/records?query=id:" + record.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(record.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE.intValue())))
            .andExpect(jsonPath("$.[*].created").value(hasItem(DEFAULT_CREATED.toString())))
            .andExpect(jsonPath("$.[*].updated").value(hasItem(DEFAULT_UPDATED.toString())));
    }
}
