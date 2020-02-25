package kz.kaznu.nmm.aglomer.service;

import java.util.List;

import javax.persistence.criteria.JoinType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import kz.kaznu.nmm.aglomer.domain.Record;
import kz.kaznu.nmm.aglomer.domain.*; // for static metamodels
import kz.kaznu.nmm.aglomer.repository.RecordRepository;
import kz.kaznu.nmm.aglomer.repository.search.RecordSearchRepository;
import kz.kaznu.nmm.aglomer.service.dto.RecordCriteria;
import kz.kaznu.nmm.aglomer.service.dto.RecordDTO;
import kz.kaznu.nmm.aglomer.service.mapper.RecordMapper;

/**
 * Service for executing complex queries for {@link Record} entities in the database.
 * The main input is a {@link RecordCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link RecordDTO} or a {@link Page} of {@link RecordDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class RecordQueryService extends QueryService<Record> {

    private final Logger log = LoggerFactory.getLogger(RecordQueryService.class);

    private final RecordRepository recordRepository;

    private final RecordMapper recordMapper;

    private final RecordSearchRepository recordSearchRepository;

    public RecordQueryService(RecordRepository recordRepository, RecordMapper recordMapper, RecordSearchRepository recordSearchRepository) {
        this.recordRepository = recordRepository;
        this.recordMapper = recordMapper;
        this.recordSearchRepository = recordSearchRepository;
    }

    /**
     * Return a {@link List} of {@link RecordDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<RecordDTO> findByCriteria(RecordCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Record> specification = createSpecification(criteria);
        return recordMapper.toDto(recordRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link RecordDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<RecordDTO> findByCriteria(RecordCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Record> specification = createSpecification(criteria);
        return recordRepository.findAll(specification, page)
            .map(recordMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(RecordCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Record> specification = createSpecification(criteria);
        return recordRepository.count(specification);
    }

    /**
     * Function to convert {@link RecordCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Record> createSpecification(RecordCriteria criteria) {
        Specification<Record> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Record_.id));
            }
            if (criteria.getCode() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCode(), Record_.code));
            }
            if (criteria.getCreated() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreated(), Record_.created));
            }
            if (criteria.getUpdated() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getUpdated(), Record_.updated));
            }
            if (criteria.getRecordRecordValueId() != null) {
                specification = specification.and(buildSpecification(criteria.getRecordRecordValueId(),
                    root -> root.join(Record_.recordRecordValues, JoinType.LEFT).get(RecordValue_.id)));
            }
        }
        return specification;
    }
}
