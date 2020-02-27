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

import kz.kaznu.nmm.aglomer.domain.PropertyGroup;
import kz.kaznu.nmm.aglomer.domain.*; // for static metamodels
import kz.kaznu.nmm.aglomer.repository.PropertyGroupRepository;
import kz.kaznu.nmm.aglomer.repository.search.PropertyGroupSearchRepository;
import kz.kaznu.nmm.aglomer.service.dto.PropertyGroupCriteria;
import kz.kaznu.nmm.aglomer.service.dto.PropertyGroupDTO;
import kz.kaznu.nmm.aglomer.service.mapper.PropertyGroupMapper;

/**
 * Service for executing complex queries for {@link PropertyGroup} entities in the database.
 * The main input is a {@link PropertyGroupCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link PropertyGroupDTO} or a {@link Page} of {@link PropertyGroupDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class PropertyGroupQueryService extends QueryService<PropertyGroup> {

    private final Logger log = LoggerFactory.getLogger(PropertyGroupQueryService.class);

    private final PropertyGroupRepository propertyGroupRepository;

    private final PropertyGroupMapper propertyGroupMapper;

    private final PropertyGroupSearchRepository propertyGroupSearchRepository;

    public PropertyGroupQueryService(PropertyGroupRepository propertyGroupRepository, PropertyGroupMapper propertyGroupMapper, PropertyGroupSearchRepository propertyGroupSearchRepository) {
        this.propertyGroupRepository = propertyGroupRepository;
        this.propertyGroupMapper = propertyGroupMapper;
        this.propertyGroupSearchRepository = propertyGroupSearchRepository;
    }

    /**
     * Return a {@link List} of {@link PropertyGroupDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<PropertyGroupDTO> findByCriteria(PropertyGroupCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<PropertyGroup> specification = createSpecification(criteria);
        return propertyGroupMapper.toDto(propertyGroupRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link PropertyGroupDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<PropertyGroupDTO> findByCriteria(PropertyGroupCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<PropertyGroup> specification = createSpecification(criteria);
        return propertyGroupRepository.findAll(specification, page)
            .map(propertyGroupMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(PropertyGroupCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<PropertyGroup> specification = createSpecification(criteria);
        return propertyGroupRepository.count(specification);
    }

    /**
     * Function to convert {@link PropertyGroupCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<PropertyGroup> createSpecification(PropertyGroupCriteria criteria) {
        Specification<PropertyGroup> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), PropertyGroup_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), PropertyGroup_.name));
            }
            if (criteria.getPropertyGroupPropertyId() != null) {
                specification = specification.and(buildSpecification(criteria.getPropertyGroupPropertyId(),
                    root -> root.join(PropertyGroup_.propertyGroupProperties, JoinType.LEFT).get(Property_.id)));
            }
        }
        return specification;
    }
}
