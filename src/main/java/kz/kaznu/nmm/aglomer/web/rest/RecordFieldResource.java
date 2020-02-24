package kz.kaznu.nmm.aglomer.web.rest;

import kz.kaznu.nmm.aglomer.service.RecordFieldService;
import kz.kaznu.nmm.aglomer.web.rest.errors.BadRequestAlertException;
import kz.kaznu.nmm.aglomer.service.dto.RecordFieldDTO;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing {@link kz.kaznu.nmm.aglomer.domain.RecordField}.
 */
@RestController
@RequestMapping("/api")
public class RecordFieldResource {

    private final Logger log = LoggerFactory.getLogger(RecordFieldResource.class);

    private static final String ENTITY_NAME = "storageRecordField";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RecordFieldService recordFieldService;

    public RecordFieldResource(RecordFieldService recordFieldService) {
        this.recordFieldService = recordFieldService;
    }

    /**
     * {@code POST  /record-fields} : Create a new recordField.
     *
     * @param recordFieldDTO the recordFieldDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new recordFieldDTO, or with status {@code 400 (Bad Request)} if the recordField has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/record-fields")
    public ResponseEntity<RecordFieldDTO> createRecordField(@Valid @RequestBody RecordFieldDTO recordFieldDTO) throws URISyntaxException {
        log.debug("REST request to save RecordField : {}", recordFieldDTO);
        if (recordFieldDTO.getId() != null) {
            throw new BadRequestAlertException("A new recordField cannot already have an ID", ENTITY_NAME, "idexists");
        }
        RecordFieldDTO result = recordFieldService.save(recordFieldDTO);
        return ResponseEntity.created(new URI("/api/record-fields/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /record-fields} : Updates an existing recordField.
     *
     * @param recordFieldDTO the recordFieldDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated recordFieldDTO,
     * or with status {@code 400 (Bad Request)} if the recordFieldDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the recordFieldDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/record-fields")
    public ResponseEntity<RecordFieldDTO> updateRecordField(@Valid @RequestBody RecordFieldDTO recordFieldDTO) throws URISyntaxException {
        log.debug("REST request to update RecordField : {}", recordFieldDTO);
        if (recordFieldDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        RecordFieldDTO result = recordFieldService.save(recordFieldDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, recordFieldDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /record-fields} : get all the recordFields.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of recordFields in body.
     */
    @GetMapping("/record-fields")
    public List<RecordFieldDTO> getAllRecordFields() {
        log.debug("REST request to get all RecordFields");
        return recordFieldService.findAll();
    }

    /**
     * {@code GET  /record-fields/:id} : get the "id" recordField.
     *
     * @param id the id of the recordFieldDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the recordFieldDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/record-fields/{id}")
    public ResponseEntity<RecordFieldDTO> getRecordField(@PathVariable Long id) {
        log.debug("REST request to get RecordField : {}", id);
        Optional<RecordFieldDTO> recordFieldDTO = recordFieldService.findOne(id);
        return ResponseUtil.wrapOrNotFound(recordFieldDTO);
    }

    /**
     * {@code DELETE  /record-fields/:id} : delete the "id" recordField.
     *
     * @param id the id of the recordFieldDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/record-fields/{id}")
    public ResponseEntity<Void> deleteRecordField(@PathVariable Long id) {
        log.debug("REST request to delete RecordField : {}", id);
        recordFieldService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/record-fields?query=:query} : search for the recordField corresponding
     * to the query.
     *
     * @param query the query of the recordField search.
     * @return the result of the search.
     */
    @GetMapping("/_search/record-fields")
    public List<RecordFieldDTO> searchRecordFields(@RequestParam String query) {
        log.debug("REST request to search RecordFields for query {}", query);
        return recordFieldService.search(query);
    }
}
