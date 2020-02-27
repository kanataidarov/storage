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

import kz.kaznu.nmm.aglomer.domain.RecordGroup;
import kz.kaznu.nmm.aglomer.domain.*; // for static metamodels
import kz.kaznu.nmm.aglomer.repository.RecordGroupRepository;
import kz.kaznu.nmm.aglomer.repository.search.RecordGroupSearchRepository;
import kz.kaznu.nmm.aglomer.service.dto.RecordGroupCriteria;
import kz.kaznu.nmm.aglomer.service.dto.RecordGroupDTO;
import kz.kaznu.nmm.aglomer.service.mapper.RecordGroupMapper;

/**
 * Service for executing complex queries for {@link RecordGroup} entities in the database.
 * The main input is a {@link RecordGroupCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link RecordGroupDTO} or a {@link Page} of {@link RecordGroupDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class RecordGroupQueryService extends QueryService<RecordGroup> {

    private final Logger log = LoggerFactory.getLogger(RecordGroupQueryService.class);

    private final RecordGroupRepository recordGroupRepository;

    private final RecordGroupMapper recordGroupMapper;

    private final RecordGroupSearchRepository recordGroupSearchRepository;

    public RecordGroupQueryService(RecordGroupRepository recordGroupRepository, RecordGroupMapper recordGroupMapper, RecordGroupSearchRepository recordGroupSearchRepository) {
        this.recordGroupRepository = recordGroupRepository;
        this.recordGroupMapper = recordGroupMapper;
        this.recordGroupSearchRepository = recordGroupSearchRepository;
    }

    /**
     * Return a {@link List} of {@link RecordGroupDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<RecordGroupDTO> findByCriteria(RecordGroupCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<RecordGroup> specification = createSpecification(criteria);
        return recordGroupMapper.toDto(recordGroupRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link RecordGroupDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<RecordGroupDTO> findByCriteria(RecordGroupCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<RecordGroup> specification = createSpecification(criteria);
        return recordGroupRepository.findAll(specification, page)
            .map(recordGroupMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(RecordGroupCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<RecordGroup> specification = createSpecification(criteria);
        return recordGroupRepository.count(specification);
    }

    /**
     * Function to convert {@link RecordGroupCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<RecordGroup> createSpecification(RecordGroupCriteria criteria) {
        Specification<RecordGroup> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), RecordGroup_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), RecordGroup_.name));
            }
            if (criteria.getType() != null) {
                specification = specification.and(buildSpecification(criteria.getType(), RecordGroup_.type));
            }
            if (criteria.getRecordGroupRecordTemplateId() != null) {
                specification = specification.and(buildSpecification(criteria.getRecordGroupRecordTemplateId(),
                    root -> root.join(RecordGroup_.recordGroupRecordTemplates, JoinType.LEFT).get(RecordTemplate_.id)));
            }
        }
        return specification;
    }
}
