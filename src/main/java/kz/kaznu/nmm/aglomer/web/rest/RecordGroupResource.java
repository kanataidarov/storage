package kz.kaznu.nmm.aglomer.web.rest;

import kz.kaznu.nmm.aglomer.service.RecordGroupService;
import kz.kaznu.nmm.aglomer.web.rest.errors.BadRequestAlertException;
import kz.kaznu.nmm.aglomer.service.dto.RecordGroupDTO;
import kz.kaznu.nmm.aglomer.service.dto.RecordGroupCriteria;
import kz.kaznu.nmm.aglomer.service.RecordGroupQueryService;

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
 * REST controller for managing {@link kz.kaznu.nmm.aglomer.domain.RecordGroup}.
 */
@RestController
@RequestMapping("/api")
public class RecordGroupResource {

    private final Logger log = LoggerFactory.getLogger(RecordGroupResource.class);

    private static final String ENTITY_NAME = "storageRecordGroup";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RecordGroupService recordGroupService;

    private final RecordGroupQueryService recordGroupQueryService;

    public RecordGroupResource(RecordGroupService recordGroupService, RecordGroupQueryService recordGroupQueryService) {
        this.recordGroupService = recordGroupService;
        this.recordGroupQueryService = recordGroupQueryService;
    }

    /**
     * {@code POST  /record-groups} : Create a new recordGroup.
     *
     * @param recordGroupDTO the recordGroupDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new recordGroupDTO, or with status {@code 400 (Bad Request)} if the recordGroup has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/record-groups")
    public ResponseEntity<RecordGroupDTO> createRecordGroup(@Valid @RequestBody RecordGroupDTO recordGroupDTO) throws URISyntaxException {
        log.debug("REST request to save RecordGroup : {}", recordGroupDTO);
        if (recordGroupDTO.getId() != null) {
            throw new BadRequestAlertException("A new recordGroup cannot already have an ID", ENTITY_NAME, "idexists");
        }
        RecordGroupDTO result = recordGroupService.save(recordGroupDTO);
        return ResponseEntity.created(new URI("/api/record-groups/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /record-groups} : Updates an existing recordGroup.
     *
     * @param recordGroupDTO the recordGroupDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated recordGroupDTO,
     * or with status {@code 400 (Bad Request)} if the recordGroupDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the recordGroupDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/record-groups")
    public ResponseEntity<RecordGroupDTO> updateRecordGroup(@Valid @RequestBody RecordGroupDTO recordGroupDTO) throws URISyntaxException {
        log.debug("REST request to update RecordGroup : {}", recordGroupDTO);
        if (recordGroupDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        RecordGroupDTO result = recordGroupService.save(recordGroupDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, recordGroupDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /record-groups} : get all the recordGroups.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of recordGroups in body.
     */
    @GetMapping("/record-groups")
    public ResponseEntity<List<RecordGroupDTO>> getAllRecordGroups(RecordGroupCriteria criteria, Pageable pageable) {
        log.debug("REST request to get RecordGroups by criteria: {}", criteria);
        Page<RecordGroupDTO> page = recordGroupQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /record-groups/count} : count all the recordGroups.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/record-groups/count")
    public ResponseEntity<Long> countRecordGroups(RecordGroupCriteria criteria) {
        log.debug("REST request to count RecordGroups by criteria: {}", criteria);
        return ResponseEntity.ok().body(recordGroupQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /record-groups/:id} : get the "id" recordGroup.
     *
     * @param id the id of the recordGroupDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the recordGroupDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/record-groups/{id}")
    public ResponseEntity<RecordGroupDTO> getRecordGroup(@PathVariable Long id) {
        log.debug("REST request to get RecordGroup : {}", id);
        Optional<RecordGroupDTO> recordGroupDTO = recordGroupService.findOne(id);
        return ResponseUtil.wrapOrNotFound(recordGroupDTO);
    }

    /**
     * {@code DELETE  /record-groups/:id} : delete the "id" recordGroup.
     *
     * @param id the id of the recordGroupDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/record-groups/{id}")
    public ResponseEntity<Void> deleteRecordGroup(@PathVariable Long id) {
        log.debug("REST request to delete RecordGroup : {}", id);
        recordGroupService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/record-groups?query=:query} : search for the recordGroup corresponding
     * to the query.
     *
     * @param query the query of the recordGroup search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/record-groups")
    public ResponseEntity<List<RecordGroupDTO>> searchRecordGroups(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of RecordGroups for query {}", query);
        Page<RecordGroupDTO> page = recordGroupService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
