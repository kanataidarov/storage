package kz.kaznu.nmm.aglomer.web.rest;

import kz.kaznu.nmm.aglomer.service.RecordTemplateService;
import kz.kaznu.nmm.aglomer.web.rest.errors.BadRequestAlertException;
import kz.kaznu.nmm.aglomer.service.dto.RecordTemplateDTO;
import kz.kaznu.nmm.aglomer.service.dto.RecordTemplateCriteria;
import kz.kaznu.nmm.aglomer.service.RecordTemplateQueryService;

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
 * REST controller for managing {@link kz.kaznu.nmm.aglomer.domain.RecordTemplate}.
 */
@RestController
@RequestMapping("/api")
public class RecordTemplateResource {

    private final Logger log = LoggerFactory.getLogger(RecordTemplateResource.class);

    private static final String ENTITY_NAME = "storageRecordTemplate";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RecordTemplateService recordTemplateService;

    private final RecordTemplateQueryService recordTemplateQueryService;

    public RecordTemplateResource(RecordTemplateService recordTemplateService, RecordTemplateQueryService recordTemplateQueryService) {
        this.recordTemplateService = recordTemplateService;
        this.recordTemplateQueryService = recordTemplateQueryService;
    }

    /**
     * {@code POST  /record-templates} : Create a new recordTemplate.
     *
     * @param recordTemplateDTO the recordTemplateDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new recordTemplateDTO, or with status {@code 400 (Bad Request)} if the recordTemplate has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/record-templates")
    public ResponseEntity<RecordTemplateDTO> createRecordTemplate(@Valid @RequestBody RecordTemplateDTO recordTemplateDTO) throws URISyntaxException {
        log.debug("REST request to save RecordTemplate : {}", recordTemplateDTO);
        if (recordTemplateDTO.getId() != null) {
            throw new BadRequestAlertException("A new recordTemplate cannot already have an ID", ENTITY_NAME, "idexists");
        }
        RecordTemplateDTO result = recordTemplateService.save(recordTemplateDTO);
        return ResponseEntity.created(new URI("/api/record-templates/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /record-templates} : Updates an existing recordTemplate.
     *
     * @param recordTemplateDTO the recordTemplateDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated recordTemplateDTO,
     * or with status {@code 400 (Bad Request)} if the recordTemplateDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the recordTemplateDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/record-templates")
    public ResponseEntity<RecordTemplateDTO> updateRecordTemplate(@Valid @RequestBody RecordTemplateDTO recordTemplateDTO) throws URISyntaxException {
        log.debug("REST request to update RecordTemplate : {}", recordTemplateDTO);
        if (recordTemplateDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        RecordTemplateDTO result = recordTemplateService.save(recordTemplateDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, recordTemplateDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /record-templates} : get all the recordTemplates.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of recordTemplates in body.
     */
    @GetMapping("/record-templates")
    public ResponseEntity<List<RecordTemplateDTO>> getAllRecordTemplates(RecordTemplateCriteria criteria, Pageable pageable) {
        log.debug("REST request to get RecordTemplates by criteria: {}", criteria);
        Page<RecordTemplateDTO> page = recordTemplateQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /record-templates/count} : count all the recordTemplates.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/record-templates/count")
    public ResponseEntity<Long> countRecordTemplates(RecordTemplateCriteria criteria) {
        log.debug("REST request to count RecordTemplates by criteria: {}", criteria);
        return ResponseEntity.ok().body(recordTemplateQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /record-templates/:id} : get the "id" recordTemplate.
     *
     * @param id the id of the recordTemplateDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the recordTemplateDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/record-templates/{id}")
    public ResponseEntity<RecordTemplateDTO> getRecordTemplate(@PathVariable Long id) {
        log.debug("REST request to get RecordTemplate : {}", id);
        Optional<RecordTemplateDTO> recordTemplateDTO = recordTemplateService.findOne(id);
        return ResponseUtil.wrapOrNotFound(recordTemplateDTO);
    }

    /**
     * {@code DELETE  /record-templates/:id} : delete the "id" recordTemplate.
     *
     * @param id the id of the recordTemplateDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/record-templates/{id}")
    public ResponseEntity<Void> deleteRecordTemplate(@PathVariable Long id) {
        log.debug("REST request to delete RecordTemplate : {}", id);
        recordTemplateService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/record-templates?query=:query} : search for the recordTemplate corresponding
     * to the query.
     *
     * @param query the query of the recordTemplate search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/record-templates")
    public ResponseEntity<List<RecordTemplateDTO>> searchRecordTemplates(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of RecordTemplates for query {}", query);
        Page<RecordTemplateDTO> page = recordTemplateService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
