package kz.kaznu.nmm.aglomer.web.rest;

import kz.kaznu.nmm.aglomer.RedisTestContainerExtension;
import kz.kaznu.nmm.aglomer.StorageApp;
import kz.kaznu.nmm.aglomer.config.SecurityBeanOverrideConfiguration;
import kz.kaznu.nmm.aglomer.domain.RecordField;
import kz.kaznu.nmm.aglomer.repository.RecordFieldRepository;
import kz.kaznu.nmm.aglomer.repository.search.RecordFieldSearchRepository;
import kz.kaznu.nmm.aglomer.service.RecordFieldService;
import kz.kaznu.nmm.aglomer.service.dto.RecordFieldDTO;
import kz.kaznu.nmm.aglomer.service.mapper.RecordFieldMapper;
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
 * Integration tests for the {@link RecordFieldResource} REST controller.
 */
@SpringBootTest(classes = {SecurityBeanOverrideConfiguration.class, StorageApp.class})
@ExtendWith(RedisTestContainerExtension.class)
public class RecordFieldResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    @Autowired
    private RecordFieldRepository recordFieldRepository;

    @Autowired
    private RecordFieldMapper recordFieldMapper;

    @Autowired
    private RecordFieldService recordFieldService;

    /**
     * This repository is mocked in the kz.kaznu.nmm.aglomer.repository.search test package.
     *
     * @see kz.kaznu.nmm.aglomer.repository.search.RecordFieldSearchRepositoryMockConfiguration
     */
    @Autowired
    private RecordFieldSearchRepository mockRecordFieldSearchRepository;

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

    private MockMvc restRecordFieldMockMvc;

    private RecordField recordField;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final RecordFieldResource recordFieldResource = new RecordFieldResource(recordFieldService);
        this.restRecordFieldMockMvc = MockMvcBuilders.standaloneSetup(recordFieldResource)
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
    public static RecordField createEntity(EntityManager em) {
        RecordField recordField = new RecordField()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION);
        return recordField;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RecordField createUpdatedEntity(EntityManager em) {
        RecordField recordField = new RecordField()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION);
        return recordField;
    }

    @BeforeEach
    public void initTest() {
        recordField = createEntity(em);
    }

    @Test
    @Transactional
    public void createRecordField() throws Exception {
        int databaseSizeBeforeCreate = recordFieldRepository.findAll().size();

        // Create the RecordField
        RecordFieldDTO recordFieldDTO = recordFieldMapper.toDto(recordField);
        restRecordFieldMockMvc.perform(post("/api/record-fields")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(recordFieldDTO)))
            .andExpect(status().isCreated());

        // Validate the RecordField in the database
        List<RecordField> recordFieldList = recordFieldRepository.findAll();
        assertThat(recordFieldList).hasSize(databaseSizeBeforeCreate + 1);
        RecordField testRecordField = recordFieldList.get(recordFieldList.size() - 1);
        assertThat(testRecordField.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testRecordField.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);

        // Validate the RecordField in Elasticsearch
        verify(mockRecordFieldSearchRepository, times(1)).save(testRecordField);
    }

    @Test
    @Transactional
    public void createRecordFieldWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = recordFieldRepository.findAll().size();

        // Create the RecordField with an existing ID
        recordField.setId(1L);
        RecordFieldDTO recordFieldDTO = recordFieldMapper.toDto(recordField);

        // An entity with an existing ID cannot be created, so this API call must fail
        restRecordFieldMockMvc.perform(post("/api/record-fields")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(recordFieldDTO)))
            .andExpect(status().isBadRequest());

        // Validate the RecordField in the database
        List<RecordField> recordFieldList = recordFieldRepository.findAll();
        assertThat(recordFieldList).hasSize(databaseSizeBeforeCreate);

        // Validate the RecordField in Elasticsearch
        verify(mockRecordFieldSearchRepository, times(0)).save(recordField);
    }


    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = recordFieldRepository.findAll().size();
        // set the field null
        recordField.setName(null);

        // Create the RecordField, which fails.
        RecordFieldDTO recordFieldDTO = recordFieldMapper.toDto(recordField);

        restRecordFieldMockMvc.perform(post("/api/record-fields")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(recordFieldDTO)))
            .andExpect(status().isBadRequest());

        List<RecordField> recordFieldList = recordFieldRepository.findAll();
        assertThat(recordFieldList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllRecordFields() throws Exception {
        // Initialize the database
        recordFieldRepository.saveAndFlush(recordField);

        // Get all the recordFieldList
        restRecordFieldMockMvc.perform(get("/api/record-fields?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(recordField.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }
    
    @Test
    @Transactional
    public void getRecordField() throws Exception {
        // Initialize the database
        recordFieldRepository.saveAndFlush(recordField);

        // Get the recordField
        restRecordFieldMockMvc.perform(get("/api/record-fields/{id}", recordField.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(recordField.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    public void getNonExistingRecordField() throws Exception {
        // Get the recordField
        restRecordFieldMockMvc.perform(get("/api/record-fields/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRecordField() throws Exception {
        // Initialize the database
        recordFieldRepository.saveAndFlush(recordField);

        int databaseSizeBeforeUpdate = recordFieldRepository.findAll().size();

        // Update the recordField
        RecordField updatedRecordField = recordFieldRepository.findById(recordField.getId()).get();
        // Disconnect from session so that the updates on updatedRecordField are not directly saved in db
        em.detach(updatedRecordField);
        updatedRecordField
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION);
        RecordFieldDTO recordFieldDTO = recordFieldMapper.toDto(updatedRecordField);

        restRecordFieldMockMvc.perform(put("/api/record-fields")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(recordFieldDTO)))
            .andExpect(status().isOk());

        // Validate the RecordField in the database
        List<RecordField> recordFieldList = recordFieldRepository.findAll();
        assertThat(recordFieldList).hasSize(databaseSizeBeforeUpdate);
        RecordField testRecordField = recordFieldList.get(recordFieldList.size() - 1);
        assertThat(testRecordField.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testRecordField.getDescription()).isEqualTo(UPDATED_DESCRIPTION);

        // Validate the RecordField in Elasticsearch
        verify(mockRecordFieldSearchRepository, times(1)).save(testRecordField);
    }

    @Test
    @Transactional
    public void updateNonExistingRecordField() throws Exception {
        int databaseSizeBeforeUpdate = recordFieldRepository.findAll().size();

        // Create the RecordField
        RecordFieldDTO recordFieldDTO = recordFieldMapper.toDto(recordField);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRecordFieldMockMvc.perform(put("/api/record-fields")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(recordFieldDTO)))
            .andExpect(status().isBadRequest());

        // Validate the RecordField in the database
        List<RecordField> recordFieldList = recordFieldRepository.findAll();
        assertThat(recordFieldList).hasSize(databaseSizeBeforeUpdate);

        // Validate the RecordField in Elasticsearch
        verify(mockRecordFieldSearchRepository, times(0)).save(recordField);
    }

    @Test
    @Transactional
    public void deleteRecordField() throws Exception {
        // Initialize the database
        recordFieldRepository.saveAndFlush(recordField);

        int databaseSizeBeforeDelete = recordFieldRepository.findAll().size();

        // Delete the recordField
        restRecordFieldMockMvc.perform(delete("/api/record-fields/{id}", recordField.getId())
            .accept(TestUtil.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<RecordField> recordFieldList = recordFieldRepository.findAll();
        assertThat(recordFieldList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the RecordField in Elasticsearch
        verify(mockRecordFieldSearchRepository, times(1)).deleteById(recordField.getId());
    }

    @Test
    @Transactional
    public void searchRecordField() throws Exception {
        // Initialize the database
        recordFieldRepository.saveAndFlush(recordField);
        when(mockRecordFieldSearchRepository.search(queryStringQuery("id:" + recordField.getId())))
            .thenReturn(Collections.singletonList(recordField));
        // Search the recordField
        restRecordFieldMockMvc.perform(get("/api/_search/record-fields?query=id:" + recordField.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(recordField.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }
}
