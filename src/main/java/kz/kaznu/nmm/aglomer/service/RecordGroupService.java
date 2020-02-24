package kz.kaznu.nmm.aglomer.service;

import kz.kaznu.nmm.aglomer.service.dto.RecordGroupDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link kz.kaznu.nmm.aglomer.domain.RecordGroup}.
 */
public interface RecordGroupService {

    /**
     * Save a recordGroup.
     *
     * @param recordGroupDTO the entity to save.
     * @return the persisted entity.
     */
    RecordGroupDTO save(RecordGroupDTO recordGroupDTO);

    /**
     * Get all the recordGroups.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<RecordGroupDTO> findAll(Pageable pageable);

    /**
     * Get the "id" recordGroup.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<RecordGroupDTO> findOne(Long id);

    /**
     * Delete the "id" recordGroup.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the recordGroup corresponding to the query.
     *
     * @param query the query of the search.
     * 
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<RecordGroupDTO> search(String query, Pageable pageable);
}
