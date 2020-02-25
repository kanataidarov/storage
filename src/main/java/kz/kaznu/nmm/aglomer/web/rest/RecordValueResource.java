package kz.kaznu.nmm.aglomer.web.rest;

import kz.kaznu.nmm.aglomer.service.RecordValueService;
import kz.kaznu.nmm.aglomer.web.rest.errors.BadRequestAlertException;
import kz.kaznu.nmm.aglomer.service.dto.RecordValueDTO;

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
 * REST controller for managing {@link kz.kaznu.nmm.aglomer.domain.RecordValue}.
 */
@RestController
@RequestMapping("/api")
public class RecordValueResource {

    private final Logger log = LoggerFactory.getLogger(RecordValueResource.class);

    private static final String ENTITY_NAME = "storageRecordValue";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RecordValueService recordValueService;

    public RecordValueResource(RecordValueService recordValueService) {
        this.recordValueService = recordValueService;
    }

    /**
     * {@code POST  /record-values} : Create a new recordValue.
     *
     * @param recordValueDTO the recordValueDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new recordValueDTO, or with status {@code 400 (Bad Request)} if the recordValue has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/record-values")
    public ResponseEntity<RecordValueDTO> createRecordValue(@Valid @RequestBody RecordValueDTO recordValueDTO) throws URISyntaxException {
        log.debug("REST request to save RecordValue : {}", recordValueDTO);
        if (recordValueDTO.getId() != null) {
            throw new BadRequestAlertException("A new recordValue cannot already have an ID", ENTITY_NAME, "idexists");
        }
        RecordValueDTO result = recordValueService.save(recordValueDTO);
        return ResponseEntity.created(new URI("/api/record-values/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /record-values} : Updates an existing recordValue.
     *
     * @param recordValueDTO the recordValueDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated recordValueDTO,
     * or with status {@code 400 (Bad Request)} if the recordValueDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the recordValueDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/record-values")
    public ResponseEntity<RecordValueDTO> updateRecordValue(@Valid @RequestBody RecordValueDTO recordValueDTO) throws URISyntaxException {
        log.debug("REST request to update RecordValue : {}", recordValueDTO);
        if (recordValueDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        RecordValueDTO result = recordValueService.save(recordValueDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, recordValueDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /record-values} : get all the recordValues.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of recordValues in body.
     */
    @GetMapping("/record-values")
    public List<RecordValueDTO> getAllRecordValues() {
        log.debug("REST request to get all RecordValues");
        return recordValueService.findAll();
    }

    /**
     * {@code GET  /record-values/:id} : get the "id" recordValue.
     *
     * @param id the id of the recordValueDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the recordValueDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/record-values/{id}")
    public ResponseEntity<RecordValueDTO> getRecordValue(@PathVariable Long id) {
        log.debug("REST request to get RecordValue : {}", id);
        Optional<RecordValueDTO> recordValueDTO = recordValueService.findOne(id);
        return ResponseUtil.wrapOrNotFound(recordValueDTO);
    }

    /**
     * {@code DELETE  /record-values/:id} : delete the "id" recordValue.
     *
     * @param id the id of the recordValueDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/record-values/{id}")
    public ResponseEntity<Void> deleteRecordValue(@PathVariable Long id) {
        log.debug("REST request to delete RecordValue : {}", id);
        recordValueService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/record-values?query=:query} : search for the recordValue corresponding
     * to the query.
     *
     * @param query the query of the recordValue search.
     * @return the result of the search.
     */
    @GetMapping("/_search/record-values")
    public List<RecordValueDTO> searchRecordValues(@RequestParam String query) {
        log.debug("REST request to search RecordValues for query {}", query);
        return recordValueService.search(query);
    }
}
