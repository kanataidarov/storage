package kz.kaznu.nmm.aglomer.service;

import kz.kaznu.nmm.aglomer.domain.RecordField;
import kz.kaznu.nmm.aglomer.repository.RecordFieldRepository;
import kz.kaznu.nmm.aglomer.repository.search.RecordFieldSearchRepository;
import kz.kaznu.nmm.aglomer.service.dto.RecordFieldDTO;
import kz.kaznu.nmm.aglomer.service.mapper.RecordFieldMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link RecordField}.
 */
@Service
@Transactional
public class RecordFieldService {

    private final Logger log = LoggerFactory.getLogger(RecordFieldService.class);

    private final RecordFieldRepository recordFieldRepository;

    private final RecordFieldMapper recordFieldMapper;

    private final RecordFieldSearchRepository recordFieldSearchRepository;

    public RecordFieldService(RecordFieldRepository recordFieldRepository, RecordFieldMapper recordFieldMapper, RecordFieldSearchRepository recordFieldSearchRepository) {
        this.recordFieldRepository = recordFieldRepository;
        this.recordFieldMapper = recordFieldMapper;
        this.recordFieldSearchRepository = recordFieldSearchRepository;
    }

    /**
     * Save a recordField.
     *
     * @param recordFieldDTO the entity to save.
     * @return the persisted entity.
     */
    public RecordFieldDTO save(RecordFieldDTO recordFieldDTO) {
        log.debug("Request to save RecordField : {}", recordFieldDTO);
        RecordField recordField = recordFieldMapper.toEntity(recordFieldDTO);
        recordField = recordFieldRepository.save(recordField);
        RecordFieldDTO result = recordFieldMapper.toDto(recordField);
        recordFieldSearchRepository.save(recordField);
        return result;
    }

    /**
     * Get all the recordFields.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<RecordFieldDTO> findAll() {
        log.debug("Request to get all RecordFields");
        return recordFieldRepository.findAll().stream()
            .map(recordFieldMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one recordField by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<RecordFieldDTO> findOne(Long id) {
        log.debug("Request to get RecordField : {}", id);
        return recordFieldRepository.findById(id)
            .map(recordFieldMapper::toDto);
    }

    /**
     * Delete the recordField by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete RecordField : {}", id);
        recordFieldRepository.deleteById(id);
        recordFieldSearchRepository.deleteById(id);
    }

    /**
     * Search for the recordField corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<RecordFieldDTO> search(String query) {
        log.debug("Request to search RecordFields for query {}", query);
        return StreamSupport
            .stream(recordFieldSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(recordFieldMapper::toDto)
            .collect(Collectors.toList());
    }
}
