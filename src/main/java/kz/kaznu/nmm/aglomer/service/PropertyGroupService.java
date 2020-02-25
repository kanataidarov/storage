package kz.kaznu.nmm.aglomer.service;

import kz.kaznu.nmm.aglomer.service.dto.PropertyGroupDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<PropertyGroupDTO> findAll(Pageable pageable);

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
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<PropertyGroupDTO> search(String query, Pageable pageable);
}
