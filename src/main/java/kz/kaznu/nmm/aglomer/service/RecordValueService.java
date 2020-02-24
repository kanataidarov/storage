package kz.kaznu.nmm.aglomer.service;

import kz.kaznu.nmm.aglomer.domain.RecordValue;
import kz.kaznu.nmm.aglomer.repository.RecordValueRepository;
import kz.kaznu.nmm.aglomer.repository.search.RecordValueSearchRepository;
import kz.kaznu.nmm.aglomer.service.dto.RecordValueDTO;
import kz.kaznu.nmm.aglomer.service.mapper.RecordValueMapper;
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
 * Service Implementation for managing {@link RecordValue}.
 */
@Service
@Transactional
public class RecordValueService {

    private final Logger log = LoggerFactory.getLogger(RecordValueService.class);

    private final RecordValueRepository recordValueRepository;

    private final RecordValueMapper recordValueMapper;

    private final RecordValueSearchRepository recordValueSearchRepository;

    public RecordValueService(RecordValueRepository recordValueRepository, RecordValueMapper recordValueMapper, RecordValueSearchRepository recordValueSearchRepository) {
        this.recordValueRepository = recordValueRepository;
        this.recordValueMapper = recordValueMapper;
        this.recordValueSearchRepository = recordValueSearchRepository;
    }

    /**
     * Save a recordValue.
     *
     * @param recordValueDTO the entity to save.
     * @return the persisted entity.
     */
    public RecordValueDTO save(RecordValueDTO recordValueDTO) {
        log.debug("Request to save RecordValue : {}", recordValueDTO);
        RecordValue recordValue = recordValueMapper.toEntity(recordValueDTO);
        recordValue = recordValueRepository.save(recordValue);
        RecordValueDTO result = recordValueMapper.toDto(recordValue);
        recordValueSearchRepository.save(recordValue);
        return result;
    }

    /**
     * Get all the recordValues.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<RecordValueDTO> findAll() {
        log.debug("Request to get all RecordValues");
        return recordValueRepository.findAll().stream()
            .map(recordValueMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one recordValue by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<RecordValueDTO> findOne(Long id) {
        log.debug("Request to get RecordValue : {}", id);
        return recordValueRepository.findById(id)
            .map(recordValueMapper::toDto);
    }

    /**
     * Delete the recordValue by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete RecordValue : {}", id);
        recordValueRepository.deleteById(id);
        recordValueSearchRepository.deleteById(id);
    }

    /**
     * Search for the recordValue corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<RecordValueDTO> search(String query) {
        log.debug("Request to search RecordValues for query {}", query);
        return StreamSupport
            .stream(recordValueSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(recordValueMapper::toDto)
            .collect(Collectors.toList());
    }
}
