package kz.kaznu.nmm.aglomer.web.rest;

import kz.kaznu.nmm.aglomer.service.RecordService;
import kz.kaznu.nmm.aglomer.web.rest.errors.BadRequestAlertException;
import kz.kaznu.nmm.aglomer.service.dto.RecordDTO;
import kz.kaznu.nmm.aglomer.service.dto.RecordCriteria;
import kz.kaznu.nmm.aglomer.service.RecordQueryService;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
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
 * REST controller for managing {@link kz.kaznu.nmm.aglomer.domain.Record}.
 */
@RestController
@RequestMapping("/api")
public class RecordResource {

    private final Logger log = LoggerFactory.getLogger(RecordResource.class);

    private static final String ENTITY_NAME = "storageRecord";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RecordService recordService;

    private final RecordQueryService recordQueryService;

    public RecordResource(RecordService recordService, RecordQueryService recordQueryService) {
        this.recordService = recordService;
        this.recordQueryService = recordQueryService;
    }

    /**
     * {@code POST  /records} : Create a new record.
     *
     * @param recordDTO the recordDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new recordDTO, or with status {@code 400 (Bad Request)} if the record has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/records")
    public ResponseEntity<RecordDTO> createRecord(@Valid @RequestBody RecordDTO recordDTO) throws URISyntaxException {
        log.debug("REST request to save Record : {}", recordDTO);
        if (recordDTO.getId() != null) {
            throw new BadRequestAlertException("A new record cannot already have an ID", ENTITY_NAME, "idexists");
        }
        RecordDTO result = recordService.save(recordDTO);
        return ResponseEntity.created(new URI("/api/records/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /records} : Updates an existing record.
     *
     * @param recordDTO the recordDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated recordDTO,
     * or with status {@code 400 (Bad Request)} if the recordDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the recordDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/records")
    public ResponseEntity<RecordDTO> updateRecord(@Valid @RequestBody RecordDTO recordDTO) throws URISyntaxException {
        log.debug("REST request to update Record : {}", recordDTO);
        if (recordDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        RecordDTO result = recordService.save(recordDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, recordDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /records} : get all the records.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of records in body.
     */
    @GetMapping("/records")
    public ResponseEntity<List<RecordDTO>> getAllRecords(RecordCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Records by criteria: {}", criteria);
        Page<RecordDTO> page = recordQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /records/count} : count all the records.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/records/count")
    public ResponseEntity<Long> countRecords(RecordCriteria criteria) {
        log.debug("REST request to count Records by criteria: {}", criteria);
        return ResponseEntity.ok().body(recordQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /records/:id} : get the "id" record.
     *
     * @param id the id of the recordDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the recordDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/records/{id}")
    public ResponseEntity<RecordDTO> getRecord(@PathVariable Long id) {
        log.debug("REST request to get Record : {}", id);
        Optional<RecordDTO> recordDTO = recordService.findOne(id);
        return ResponseUtil.wrapOrNotFound(recordDTO);
    }

    /**
     * {@code DELETE  /records/:id} : delete the "id" record.
     *
     * @param id the id of the recordDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/records/{id}")
    public ResponseEntity<Void> deleteRecord(@PathVariable Long id) {
        log.debug("REST request to delete Record : {}", id);
        recordService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/records?query=:query} : search for the record corresponding
     * to the query.
     *
     * @param query the query of the record search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/records")
    public ResponseEntity<List<RecordDTO>> searchRecords(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Records for query {}", query);
        Page<RecordDTO> page = recordService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
