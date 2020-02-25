package kz.kaznu.nmm.aglomer.service.impl;

import kz.kaznu.nmm.aglomer.service.RecordTemplateService;
import kz.kaznu.nmm.aglomer.domain.RecordTemplate;
import kz.kaznu.nmm.aglomer.repository.RecordTemplateRepository;
import kz.kaznu.nmm.aglomer.repository.search.RecordTemplateSearchRepository;
import kz.kaznu.nmm.aglomer.service.dto.RecordTemplateDTO;
import kz.kaznu.nmm.aglomer.service.mapper.RecordTemplateMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link RecordTemplate}.
 */
@Service
@Transactional
public class RecordTemplateServiceImpl implements RecordTemplateService {

    private final Logger log = LoggerFactory.getLogger(RecordTemplateServiceImpl.class);

    private final RecordTemplateRepository recordTemplateRepository;

    private final RecordTemplateMapper recordTemplateMapper;

    private final RecordTemplateSearchRepository recordTemplateSearchRepository;

    public RecordTemplateServiceImpl(RecordTemplateRepository recordTemplateRepository, RecordTemplateMapper recordTemplateMapper, RecordTemplateSearchRepository recordTemplateSearchRepository) {
        this.recordTemplateRepository = recordTemplateRepository;
        this.recordTemplateMapper = recordTemplateMapper;
        this.recordTemplateSearchRepository = recordTemplateSearchRepository;
    }

    /**
     * Save a recordTemplate.
     *
     * @param recordTemplateDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public RecordTemplateDTO save(RecordTemplateDTO recordTemplateDTO) {
        log.debug("Request to save RecordTemplate : {}", recordTemplateDTO);
        RecordTemplate recordTemplate = recordTemplateMapper.toEntity(recordTemplateDTO);
        recordTemplate = recordTemplateRepository.save(recordTemplate);
        RecordTemplateDTO result = recordTemplateMapper.toDto(recordTemplate);
        recordTemplateSearchRepository.save(recordTemplate);
        return result;
    }

    /**
     * Get all the recordTemplates.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<RecordTemplateDTO> findAll(Pageable pageable) {
        log.debug("Request to get all RecordTemplates");
        return recordTemplateRepository.findAll(pageable)
            .map(recordTemplateMapper::toDto);
    }

    /**
     * Get one recordTemplate by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<RecordTemplateDTO> findOne(Long id) {
        log.debug("Request to get RecordTemplate : {}", id);
        return recordTemplateRepository.findById(id)
            .map(recordTemplateMapper::toDto);
    }

    /**
     * Delete the recordTemplate by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete RecordTemplate : {}", id);
        recordTemplateRepository.deleteById(id);
        recordTemplateSearchRepository.deleteById(id);
    }

    /**
     * Search for the recordTemplate corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<RecordTemplateDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of RecordTemplates for query {}", query);
        return recordTemplateSearchRepository.search(queryStringQuery(query), pageable)
            .map(recordTemplateMapper::toDto);
    }
}
