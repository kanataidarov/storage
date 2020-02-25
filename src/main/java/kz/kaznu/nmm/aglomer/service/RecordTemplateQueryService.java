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

import kz.kaznu.nmm.aglomer.domain.RecordTemplate;
import kz.kaznu.nmm.aglomer.domain.*; // for static metamodels
import kz.kaznu.nmm.aglomer.repository.RecordTemplateRepository;
import kz.kaznu.nmm.aglomer.repository.search.RecordTemplateSearchRepository;
import kz.kaznu.nmm.aglomer.service.dto.RecordTemplateCriteria;
import kz.kaznu.nmm.aglomer.service.dto.RecordTemplateDTO;
import kz.kaznu.nmm.aglomer.service.mapper.RecordTemplateMapper;

/**
 * Service for executing complex queries for {@link RecordTemplate} entities in the database.
 * The main input is a {@link RecordTemplateCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link RecordTemplateDTO} or a {@link Page} of {@link RecordTemplateDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class RecordTemplateQueryService extends QueryService<RecordTemplate> {

    private final Logger log = LoggerFactory.getLogger(RecordTemplateQueryService.class);

    private final RecordTemplateRepository recordTemplateRepository;

    private final RecordTemplateMapper recordTemplateMapper;

    private final RecordTemplateSearchRepository recordTemplateSearchRepository;

    public RecordTemplateQueryService(RecordTemplateRepository recordTemplateRepository, RecordTemplateMapper recordTemplateMapper, RecordTemplateSearchRepository recordTemplateSearchRepository) {
        this.recordTemplateRepository = recordTemplateRepository;
        this.recordTemplateMapper = recordTemplateMapper;
        this.recordTemplateSearchRepository = recordTemplateSearchRepository;
    }

    /**
     * Return a {@link List} of {@link RecordTemplateDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<RecordTemplateDTO> findByCriteria(RecordTemplateCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<RecordTemplate> specification = createSpecification(criteria);
        return recordTemplateMapper.toDto(recordTemplateRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link RecordTemplateDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<RecordTemplateDTO> findByCriteria(RecordTemplateCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<RecordTemplate> specification = createSpecification(criteria);
        return recordTemplateRepository.findAll(specification, page)
            .map(recordTemplateMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(RecordTemplateCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<RecordTemplate> specification = createSpecification(criteria);
        return recordTemplateRepository.count(specification);
    }

    /**
     * Function to convert {@link RecordTemplateCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<RecordTemplate> createSpecification(RecordTemplateCriteria criteria) {
        Specification<RecordTemplate> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), RecordTemplate_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), RecordTemplate_.name));
            }
            if (criteria.getCreated() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreated(), RecordTemplate_.created));
            }
            if (criteria.getUpdated() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getUpdated(), RecordTemplate_.updated));
            }
            if (criteria.getLanguage() != null) {
                specification = specification.and(buildSpecification(criteria.getLanguage(), RecordTemplate_.language));
            }
            if (criteria.getRecordTemplateRecordFieldId() != null) {
                specification = specification.and(buildSpecification(criteria.getRecordTemplateRecordFieldId(),
                    root -> root.join(RecordTemplate_.recordTemplateRecordFields, JoinType.LEFT).get(RecordField_.id)));
            }
            if (criteria.getGroupId() != null) {
                specification = specification.and(buildSpecification(criteria.getGroupId(),
                    root -> root.join(RecordTemplate_.group, JoinType.LEFT).get(RecordGroup_.id)));
            }
        }
        return specification;
    }
}
