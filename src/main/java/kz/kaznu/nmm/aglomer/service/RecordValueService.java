package kz.kaznu.nmm.aglomer.service;

import kz.kaznu.nmm.aglomer.service.dto.RecordValueDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link kz.kaznu.nmm.aglomer.domain.RecordValue}.
 */
public interface RecordValueService {

    /**
     * Save a recordValue.
     *
     * @param recordValueDTO the entity to save.
     * @return the persisted entity.
     */
    RecordValueDTO save(RecordValueDTO recordValueDTO);

    /**
     * Get all the recordValues.
     *
     * @return the list of entities.
     */
    List<RecordValueDTO> findAll();

    /**
     * Get the "id" recordValue.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<RecordValueDTO> findOne(Long id);

    /**
     * Delete the "id" recordValue.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the recordValue corresponding to the query.
     *
     * @param query the query of the search.
     * 
     * @return the list of entities.
     */
    List<RecordValueDTO> search(String query);
}
