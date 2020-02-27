package kz.kaznu.nmm.aglomer.service.impl;

import kz.kaznu.nmm.aglomer.service.RecordService;
import kz.kaznu.nmm.aglomer.domain.Record;
import kz.kaznu.nmm.aglomer.repository.RecordRepository;
import kz.kaznu.nmm.aglomer.repository.search.RecordSearchRepository;
import kz.kaznu.nmm.aglomer.service.dto.RecordDTO;
import kz.kaznu.nmm.aglomer.service.mapper.RecordMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link Record}.
 */
@Service
@Transactional
public class RecordServiceImpl implements RecordService {

    private final Logger log = LoggerFactory.getLogger(RecordServiceImpl.class);

    private final RecordRepository recordRepository;

    private final RecordMapper recordMapper;

    private final RecordSearchRepository recordSearchRepository;

    public RecordServiceImpl(RecordRepository recordRepository, RecordMapper recordMapper, RecordSearchRepository recordSearchRepository) {
        this.recordRepository = recordRepository;
        this.recordMapper = recordMapper;
        this.recordSearchRepository = recordSearchRepository;
    }

    /**
     * Save a record.
     *
     * @param recordDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public RecordDTO save(RecordDTO recordDTO) {
        log.debug("Request to save Record : {}", recordDTO);
        Record record = recordMapper.toEntity(recordDTO);
        record = recordRepository.save(record);
        RecordDTO result = recordMapper.toDto(record);
        recordSearchRepository.save(record);
        return result;
    }

    /**
     * Get all the records.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<RecordDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Records");
        return recordRepository.findAll(pageable)
            .map(recordMapper::toDto);
    }

    /**
     * Get one record by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<RecordDTO> findOne(Long id) {
        log.debug("Request to get Record : {}", id);
        return recordRepository.findById(id)
            .map(recordMapper::toDto);
    }

    /**
     * Delete the record by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Record : {}", id);
        recordRepository.deleteById(id);
        recordSearchRepository.deleteById(id);
    }

    /**
     * Search for the record corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<RecordDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Records for query {}", query);
        return recordSearchRepository.search(queryStringQuery(query), pageable)
            .map(recordMapper::toDto);
    }
}
