package kz.kaznu.nmm.aglomer.service.impl;

import kz.kaznu.nmm.aglomer.service.PropertyGroupService;
import kz.kaznu.nmm.aglomer.domain.PropertyGroup;
import kz.kaznu.nmm.aglomer.repository.PropertyGroupRepository;
import kz.kaznu.nmm.aglomer.repository.search.PropertyGroupSearchRepository;
import kz.kaznu.nmm.aglomer.service.dto.PropertyGroupDTO;
import kz.kaznu.nmm.aglomer.service.mapper.PropertyGroupMapper;
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
 * Service Implementation for managing {@link PropertyGroup}.
 */
@Service
@Transactional
public class PropertyGroupServiceImpl implements PropertyGroupService {

    private final Logger log = LoggerFactory.getLogger(PropertyGroupServiceImpl.class);

    private final PropertyGroupRepository propertyGroupRepository;

    private final PropertyGroupMapper propertyGroupMapper;

    private final PropertyGroupSearchRepository propertyGroupSearchRepository;

    public PropertyGroupServiceImpl(PropertyGroupRepository propertyGroupRepository, PropertyGroupMapper propertyGroupMapper, PropertyGroupSearchRepository propertyGroupSearchRepository) {
        this.propertyGroupRepository = propertyGroupRepository;
        this.propertyGroupMapper = propertyGroupMapper;
        this.propertyGroupSearchRepository = propertyGroupSearchRepository;
    }

    /**
     * Save a propertyGroup.
     *
     * @param propertyGroupDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public PropertyGroupDTO save(PropertyGroupDTO propertyGroupDTO) {
        log.debug("Request to save PropertyGroup : {}", propertyGroupDTO);
        PropertyGroup propertyGroup = propertyGroupMapper.toEntity(propertyGroupDTO);
        propertyGroup = propertyGroupRepository.save(propertyGroup);
        PropertyGroupDTO result = propertyGroupMapper.toDto(propertyGroup);
        propertyGroupSearchRepository.save(propertyGroup);
        return result;
    }

    /**
     * Get all the propertyGroups.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<PropertyGroupDTO> findAll() {
        log.debug("Request to get all PropertyGroups");
        return propertyGroupRepository.findAll().stream()
            .map(propertyGroupMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one propertyGroup by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<PropertyGroupDTO> findOne(Long id) {
        log.debug("Request to get PropertyGroup : {}", id);
        return propertyGroupRepository.findById(id)
            .map(propertyGroupMapper::toDto);
    }

    /**
     * Delete the propertyGroup by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete PropertyGroup : {}", id);
        propertyGroupRepository.deleteById(id);
        propertyGroupSearchRepository.deleteById(id);
    }

    /**
     * Search for the propertyGroup corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<PropertyGroupDTO> search(String query) {
        log.debug("Request to search PropertyGroups for query {}", query);
        return StreamSupport
            .stream(propertyGroupSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(propertyGroupMapper::toDto)
            .collect(Collectors.toList());
    }
}
