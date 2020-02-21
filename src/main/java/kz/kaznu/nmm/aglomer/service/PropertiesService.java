package kz.kaznu.nmm.aglomer.service;

import kz.kaznu.nmm.aglomer.domain.Properties;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link Properties}.
 */
public interface PropertiesService {

    /**
     * Save a properties.
     *
     * @param properties the entity to save.
     * @return the persisted entity.
     */
    Properties save(Properties properties);

    /**
     * Get all the properties.
     *
     * @return the list of entities.
     */
    List<Properties> findAll();

    /**
     * Get the "id" properties.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Properties> findOne(Long id);

    /**
     * Delete the "id" properties.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the properties corresponding to the query.
     *
     * @param query the query of the search.
     * 
     * @return the list of entities.
     */
    List<Properties> search(String query);
}
