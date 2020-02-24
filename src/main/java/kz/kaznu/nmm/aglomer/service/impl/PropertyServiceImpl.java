package kz.kaznu.nmm.aglomer.service.impl;

import kz.kaznu.nmm.aglomer.service.PropertyService;
import kz.kaznu.nmm.aglomer.domain.Property;
import kz.kaznu.nmm.aglomer.repository.PropertyRepository;
import kz.kaznu.nmm.aglomer.repository.search.PropertySearchRepository;
import kz.kaznu.nmm.aglomer.service.dto.PropertyDTO;
import kz.kaznu.nmm.aglomer.service.mapper.PropertyMapper;
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
 * Service Implementation for managing {@link Property}.
 */
@Service
@Transactional
public class PropertyServiceImpl implements PropertyService {

    private final Logger log = LoggerFactory.getLogger(PropertyServiceImpl.class);

    private final PropertyRepository propertyRepository;

    private final PropertyMapper propertyMapper;

    private final PropertySearchRepository propertySearchRepository;

    public PropertyServiceImpl(PropertyRepository propertyRepository, PropertyMapper propertyMapper, PropertySearchRepository propertySearchRepository) {
        this.propertyRepository = propertyRepository;
        this.propertyMapper = propertyMapper;
        this.propertySearchRepository = propertySearchRepository;
    }

    /**
     * Save a property.
     *
     * @param propertyDTO the entity to save.
     * @return the persisted entity.
     */
    @Override
    public PropertyDTO save(PropertyDTO propertyDTO) {
        log.debug("Request to save Property : {}", propertyDTO);
        Property property = propertyMapper.toEntity(propertyDTO);
        property = propertyRepository.save(property);
        PropertyDTO result = propertyMapper.toDto(property);
        propertySearchRepository.save(property);
        return result;
    }

    /**
     * Get all the properties.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<PropertyDTO> findAll() {
        log.debug("Request to get all Properties");
        return propertyRepository.findAll().stream()
            .map(propertyMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one property by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<PropertyDTO> findOne(Long id) {
        log.debug("Request to get Property : {}", id);
        return propertyRepository.findById(id)
            .map(propertyMapper::toDto);
    }

    /**
     * Delete the property by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Property : {}", id);
        propertyRepository.deleteById(id);
        propertySearchRepository.deleteById(id);
    }

    /**
     * Search for the property corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<PropertyDTO> search(String query) {
        log.debug("Request to search Properties for query {}", query);
        return StreamSupport
            .stream(propertySearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(propertyMapper::toDto)
            .collect(Collectors.toList());
    }
}
