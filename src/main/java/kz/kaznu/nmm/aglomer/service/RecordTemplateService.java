package kz.kaznu.nmm.aglomer.service;

import kz.kaznu.nmm.aglomer.service.dto.RecordTemplateDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing {@link kz.kaznu.nmm.aglomer.domain.RecordTemplate}.
 */
public interface RecordTemplateService {

    /**
     * Save a recordTemplate.
     *
     * @param recordTemplateDTO the entity to save.
     * @return the persisted entity.
     */
    RecordTemplateDTO save(RecordTemplateDTO recordTemplateDTO);

    /**
     * Get all the recordTemplates.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<RecordTemplateDTO> findAll(Pageable pageable);

    /**
     * Get the "id" recordTemplate.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<RecordTemplateDTO> findOne(Long id);

    /**
     * Delete the "id" recordTemplate.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the recordTemplate corresponding to the query.
     *
     * @param query the query of the search.
     * 
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<RecordTemplateDTO> search(String query, Pageable pageable);
}
