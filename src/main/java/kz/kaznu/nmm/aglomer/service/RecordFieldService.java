package kz.kaznu.nmm.aglomer.service;

import kz.kaznu.nmm.aglomer.service.dto.RecordFieldDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link kz.kaznu.nmm.aglomer.domain.RecordField}.
 */
public interface RecordFieldService {

    /**
     * Save a recordField.
     *
     * @param recordFieldDTO the entity to save.
     * @return the persisted entity.
     */
    RecordFieldDTO save(RecordFieldDTO recordFieldDTO);

    /**
     * Get all the recordFields.
     *
     * @return the list of entities.
     */
    List<RecordFieldDTO> findAll();

    /**
     * Get the "id" recordField.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<RecordFieldDTO> findOne(Long id);

    /**
     * Delete the "id" recordField.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the recordField corresponding to the query.
     *
     * @param query the query of the search.
     * 
     * @return the list of entities.
     */
    List<RecordFieldDTO> search(String query);
}
