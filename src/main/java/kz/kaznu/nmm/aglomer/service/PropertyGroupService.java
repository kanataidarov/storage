package kz.kaznu.nmm.aglomer.service;

import kz.kaznu.nmm.aglomer.service.dto.PropertyGroupDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link kz.kaznu.nmm.aglomer.domain.PropertyGroup}.
 */
public interface PropertyGroupService {

    /**
     * Save a propertyGroup.
     *
     * @param propertyGroupDTO the entity to save.
     * @return the persisted entity.
     */
    PropertyGroupDTO save(PropertyGroupDTO propertyGroupDTO);

    /**
     * Get all the propertyGroups.
     *
     * @return the list of entities.
     */
    List<PropertyGroupDTO> findAll();

    /**
     * Get the "id" propertyGroup.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<PropertyGroupDTO> findOne(Long id);

    /**
     * Delete the "id" propertyGroup.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the propertyGroup corresponding to the query.
     *
     * @param query the query of the search.
     * 
     * @return the list of entities.
     */
    List<PropertyGroupDTO> search(String query);
}
