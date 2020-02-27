package kz.kaznu.nmm.aglomer.web.rest;

import kz.kaznu.nmm.aglomer.RedisTestContainerExtension;
import kz.kaznu.nmm.aglomer.StorageApp;
import kz.kaznu.nmm.aglomer.config.SecurityBeanOverrideConfiguration;
import kz.kaznu.nmm.aglomer.domain.RecordValue;
import kz.kaznu.nmm.aglomer.repository.RecordValueRepository;
import kz.kaznu.nmm.aglomer.repository.search.RecordValueSearchRepository;
import kz.kaznu.nmm.aglomer.service.RecordValueService;
import kz.kaznu.nmm.aglomer.service.dto.RecordValueDTO;
import kz.kaznu.nmm.aglomer.service.mapper.RecordValueMapper;
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
 * Integration tests for the {@link RecordValueResource} REST controller.
 */
@SpringBootTest(classes = {SecurityBeanOverrideConfiguration.class, StorageApp.class})
@ExtendWith(RedisTestContainerExtension.class)
public class RecordValueResourceIT {

    private static final String DEFAULT_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_VALUE = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    @Autowired
    private RecordValueRepository recordValueRepository;

    @Autowired
    private RecordValueMapper recordValueMapper;

    @Autowired
    private RecordValueService recordValueService;

    /**
     * This repository is mocked in the kz.kaznu.nmm.aglomer.repository.search test package.
     *
     * @see kz.kaznu.nmm.aglomer.repository.search.RecordValueSearchRepositoryMockConfiguration
     */
    @Autowired
    private RecordValueSearchRepository mockRecordValueSearchRepository;

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

    private MockMvc restRecordValueMockMvc;

    private RecordValue recordValue;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final RecordValueResource recordValueResource = new RecordValueResource(recordValueService);
        this.restRecordValueMockMvc = MockMvcBuilders.standaloneSetup(recordValueResource)
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
    public static RecordValue createEntity(EntityManager em) {
        RecordValue recordValue = new RecordValue()
            .value(DEFAULT_VALUE)
            .created(DEFAULT_CREATED)
            .updated(DEFAULT_UPDATED);
        return recordValue;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RecordValue createUpdatedEntity(EntityManager em) {
        RecordValue recordValue = new RecordValue()
            .value(UPDATED_VALUE)
            .created(UPDATED_CREATED)
            .updated(UPDATED_UPDATED);
        return recordValue;
    }

    @BeforeEach
    public void initTest() {
        recordValue = createEntity(em);
    }

    @Test
    @Transactional
    public void createRecordValue() throws Exception {
        int databaseSizeBeforeCreate = recordValueRepository.findAll().size();

        // Create the RecordValue
        RecordValueDTO recordValueDTO = recordValueMapper.toDto(recordValue);
        restRecordValueMockMvc.perform(post("/api/record-values")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(recordValueDTO)))
            .andExpect(status().isCreated());

        // Validate the RecordValue in the database
        List<RecordValue> recordValueList = recordValueRepository.findAll();
        assertThat(recordValueList).hasSize(databaseSizeBeforeCreate + 1);
        RecordValue testRecordValue = recordValueList.get(recordValueList.size() - 1);
        assertThat(testRecordValue.getValue()).isEqualTo(DEFAULT_VALUE);
        assertThat(testRecordValue.getCreated()).isEqualTo(DEFAULT_CREATED);
        assertThat(testRecordValue.getUpdated()).isEqualTo(DEFAULT_UPDATED);

        // Validate the RecordValue in Elasticsearch
        verify(mockRecordValueSearchRepository, times(1)).save(testRecordValue);
    }

    @Test
    @Transactional
    public void createRecordValueWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = recordValueRepository.findAll().size();

        // Create the RecordValue with an existing ID
        recordValue.setId(1L);
        RecordValueDTO recordValueDTO = recordValueMapper.toDto(recordValue);

        // An entity with an existing ID cannot be created, so this API call must fail
        restRecordValueMockMvc.perform(post("/api/record-values")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(recordValueDTO)))
            .andExpect(status().isBadRequest());

        // Validate the RecordValue in the database
        List<RecordValue> recordValueList = recordValueRepository.findAll();
        assertThat(recordValueList).hasSize(databaseSizeBeforeCreate);

        // Validate the RecordValue in Elasticsearch
        verify(mockRecordValueSearchRepository, times(0)).save(recordValue);
    }


    @Test
    @Transactional
    public void checkCreatedIsRequired() throws Exception {
        int databaseSizeBeforeTest = recordValueRepository.findAll().size();
        // set the field null
        recordValue.setCreated(null);

        // Create the RecordValue, which fails.
        RecordValueDTO recordValueDTO = recordValueMapper.toDto(recordValue);

        restRecordValueMockMvc.perform(post("/api/record-values")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(recordValueDTO)))
            .andExpect(status().isBadRequest());

        List<RecordValue> recordValueList = recordValueRepository.findAll();
        assertThat(recordValueList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkUpdatedIsRequired() throws Exception {
        int databaseSizeBeforeTest = recordValueRepository.findAll().size();
        // set the field null
        recordValue.setUpdated(null);

        // Create the RecordValue, which fails.
        RecordValueDTO recordValueDTO = recordValueMapper.toDto(recordValue);

        restRecordValueMockMvc.perform(post("/api/record-values")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(recordValueDTO)))
            .andExpect(status().isBadRequest());

        List<RecordValue> recordValueList = recordValueRepository.findAll();
        assertThat(recordValueList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllRecordValues() throws Exception {
        // Initialize the database
        recordValueRepository.saveAndFlush(recordValue);

        // Get all the recordValueList
        restRecordValueMockMvc.perform(get("/api/record-values?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(recordValue.getId().intValue())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)))
            .andExpect(jsonPath("$.[*].created").value(hasItem(DEFAULT_CREATED.toString())))
            .andExpect(jsonPath("$.[*].updated").value(hasItem(DEFAULT_UPDATED.toString())));
    }
    
    @Test
    @Transactional
    public void getRecordValue() throws Exception {
        // Initialize the database
        recordValueRepository.saveAndFlush(recordValue);

        // Get the recordValue
        restRecordValueMockMvc.perform(get("/api/record-values/{id}", recordValue.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(recordValue.getId().intValue()))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE))
            .andExpect(jsonPath("$.created").value(DEFAULT_CREATED.toString()))
            .andExpect(jsonPath("$.updated").value(DEFAULT_UPDATED.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingRecordValue() throws Exception {
        // Get the recordValue
        restRecordValueMockMvc.perform(get("/api/record-values/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRecordValue() throws Exception {
        // Initialize the database
        recordValueRepository.saveAndFlush(recordValue);

        int databaseSizeBeforeUpdate = recordValueRepository.findAll().size();

        // Update the recordValue
        RecordValue updatedRecordValue = recordValueRepository.findById(recordValue.getId()).get();
        // Disconnect from session so that the updates on updatedRecordValue are not directly saved in db
        em.detach(updatedRecordValue);
        updatedRecordValue
            .value(UPDATED_VALUE)
            .created(UPDATED_CREATED)
            .updated(UPDATED_UPDATED);
        RecordValueDTO recordValueDTO = recordValueMapper.toDto(updatedRecordValue);

        restRecordValueMockMvc.perform(put("/api/record-values")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(recordValueDTO)))
            .andExpect(status().isOk());

        // Validate the RecordValue in the database
        List<RecordValue> recordValueList = recordValueRepository.findAll();
        assertThat(recordValueList).hasSize(databaseSizeBeforeUpdate);
        RecordValue testRecordValue = recordValueList.get(recordValueList.size() - 1);
        assertThat(testRecordValue.getValue()).isEqualTo(UPDATED_VALUE);
        assertThat(testRecordValue.getCreated()).isEqualTo(UPDATED_CREATED);
        assertThat(testRecordValue.getUpdated()).isEqualTo(UPDATED_UPDATED);

        // Validate the RecordValue in Elasticsearch
        verify(mockRecordValueSearchRepository, times(1)).save(testRecordValue);
    }

    @Test
    @Transactional
    public void updateNonExistingRecordValue() throws Exception {
        int databaseSizeBeforeUpdate = recordValueRepository.findAll().size();

        // Create the RecordValue
        RecordValueDTO recordValueDTO = recordValueMapper.toDto(recordValue);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRecordValueMockMvc.perform(put("/api/record-values")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(recordValueDTO)))
            .andExpect(status().isBadRequest());

        // Validate the RecordValue in the database
        List<RecordValue> recordValueList = recordValueRepository.findAll();
        assertThat(recordValueList).hasSize(databaseSizeBeforeUpdate);

        // Validate the RecordValue in Elasticsearch
        verify(mockRecordValueSearchRepository, times(0)).save(recordValue);
    }

    @Test
    @Transactional
    public void deleteRecordValue() throws Exception {
        // Initialize the database
        recordValueRepository.saveAndFlush(recordValue);

        int databaseSizeBeforeDelete = recordValueRepository.findAll().size();

        // Delete the recordValue
        restRecordValueMockMvc.perform(delete("/api/record-values/{id}", recordValue.getId())
            .accept(TestUtil.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<RecordValue> recordValueList = recordValueRepository.findAll();
        assertThat(recordValueList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the RecordValue in Elasticsearch
        verify(mockRecordValueSearchRepository, times(1)).deleteById(recordValue.getId());
    }

    @Test
    @Transactional
    public void searchRecordValue() throws Exception {
        // Initialize the database
        recordValueRepository.saveAndFlush(recordValue);
        when(mockRecordValueSearchRepository.search(queryStringQuery("id:" + recordValue.getId())))
            .thenReturn(Collections.singletonList(recordValue));
        // Search the recordValue
        restRecordValueMockMvc.perform(get("/api/_search/record-values?query=id:" + recordValue.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(recordValue.getId().intValue())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)))
            .andExpect(jsonPath("$.[*].created").value(hasItem(DEFAULT_CREATED.toString())))
            .andExpect(jsonPath("$.[*].updated").value(hasItem(DEFAULT_UPDATED.toString())));
    }
}
