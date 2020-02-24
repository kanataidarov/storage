package kz.kaznu.nmm.aglomer.service.impl;

import kz.kaznu.nmm.aglomer.service.RecordGroupService;
import kz.kaznu.nmm.aglomer.domain.RecordGroup;
import kz.kaznu.nmm.aglomer.repository.RecordGroupRepository;
import kz.kaznu.nmm.aglomer.repository.search.RecordGroupSearchRepository;
import kz.kaznu.nmm.aglomer.service.dto.RecordGroupDTO;
import kz.kaznu.nmm.aglomer.service.mapper.RecordGroupMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link RecordGroup}.
 */
@Service
@Transactional
public class RecordGroupServiceImpl implements RecordGroupService {

    private final Logger log = LoggerFactory.getLogger(RecordGroupServiceImpl.class);

    private final RecordGroupRepository recordGroupRepository;

    private final RecordGroupMapper recordGroupMapper;

    private final RecordGroupSearchRepository recordGroupSearchRepository;

    public RecordGroupServiceImpl(RecordGroupRepository recordGroupRepository, RecordGroupMapper recordGroupMapper, RecordGroupSearchRepository recordGroupSearchRepository) {
        this.recordGroupRepository = recordGroupRepository;
        this.recordGroupMapper = recordGroupMapper;
        this.recordGroupSearchRepository = recordGroupSearchRepository;
    }

    /**
     * Save a recordGroup.
     *
     * @param recordGroupDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public RecordGroupDTO save(RecordGroupDTO recordGroupDTO) {
        log.debug("Request to save RecordGroup : {}", recordGroupDTO);
        RecordGroup recordGroup = recordGroupMapper.toEntity(recordGroupDTO);
        recordGroup = recordGroupRepository.save(recordGroup);
        RecordGroupDTO result = recordGroupMapper.toDto(recordGroup);
        recordGroupSearchRepository.save(recordGroup);
        return result;
    }

    /**
     * Get all the recordGroups.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<RecordGroupDTO> findAll(Pageable pageable) {
        log.debug("Request to get all RecordGroups");
        return recordGroupRepository.findAll(pageable)
            .map(recordGroupMapper::toDto);
    }

    /**
     * Get one recordGroup by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<RecordGroupDTO> findOne(Long id) {
        log.debug("Request to get RecordGroup : {}", id);
        return recordGroupRepository.findById(id)
            .map(recordGroupMapper::toDto);
    }

    /**
     * Delete the recordGroup by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete RecordGroup : {}", id);
        recordGroupRepository.deleteById(id);
        recordGroupSearchRepository.deleteById(id);
    }

    /**
     * Search for the recordGroup corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<RecordGroupDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of RecordGroups for query {}", query);
        return recordGroupSearchRepository.search(queryStringQuery(query), pageable)
            .map(recordGroupMapper::toDto);
    }
}
