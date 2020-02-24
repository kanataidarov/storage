package kz.kaznu.nmm.aglomer.service;

import kz.kaznu.nmm.aglomer.service.dto.PropertyDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link kz.kaznu.nmm.aglomer.domain.Property}.
 */
public interface PropertyService {

    /**
     * Save a property.
     *
     * @param propertyDTO the entity to save.
     * @return the persisted entity.
     */
    PropertyDTO save(PropertyDTO propertyDTO);

    /**
     * Get all the properties.
     *
     * @return the list of entities.
     */
    List<PropertyDTO> findAll();

    /**
     * Get the "id" property.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<PropertyDTO> findOne(Long id);

    /**
     * Delete the "id" property.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the property corresponding to the query.
     *
     * @param query the query of the search.
     * 
     * @return the list of entities.
     */
    List<PropertyDTO> search(String query);
}
