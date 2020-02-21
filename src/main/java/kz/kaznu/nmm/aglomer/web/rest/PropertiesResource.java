package kz.kaznu.nmm.aglomer.web.rest;

import kz.kaznu.nmm.aglomer.domain.Properties;
import kz.kaznu.nmm.aglomer.service.PropertiesService;
import kz.kaznu.nmm.aglomer.web.rest.errors.BadRequestAlertException;

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
 * REST controller for managing {@link kz.kaznu.nmm.aglomer.domain.Properties}.
 */
@RestController
@RequestMapping("/api")
public class PropertiesResource {

    private final Logger log = LoggerFactory.getLogger(PropertiesResource.class);

    private static final String ENTITY_NAME = "storageProperties";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PropertiesService propertiesService;

    public PropertiesResource(PropertiesService propertiesService) {
        this.propertiesService = propertiesService;
    }

    /**
     * {@code POST  /properties} : Create a new properties.
     *
     * @param properties the properties to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new properties, or with status {@code 400 (Bad Request)} if the properties has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/properties")
    public ResponseEntity<Properties> createProperties(@Valid @RequestBody Properties properties) throws URISyntaxException {
        log.debug("REST request to save Properties : {}", properties);
        if (properties.getId() != null) {
            throw new BadRequestAlertException("A new properties cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Properties result = propertiesService.save(properties);
        return ResponseEntity.created(new URI("/api/properties/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /properties} : Updates an existing properties.
     *
     * @param properties the properties to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated properties,
     * or with status {@code 400 (Bad Request)} if the properties is not valid,
     * or with status {@code 500 (Internal Server Error)} if the properties couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/properties")
    public ResponseEntity<Properties> updateProperties(@Valid @RequestBody Properties properties) throws URISyntaxException {
        log.debug("REST request to update Properties : {}", properties);
        if (properties.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Properties result = propertiesService.save(properties);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, properties.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /properties} : get all the properties.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of properties in body.
     */
    @GetMapping("/properties")
    public List<Properties> getAllProperties() {
        log.debug("REST request to get all Properties");
        return propertiesService.findAll();
    }

    /**
     * {@code GET  /properties/:id} : get the "id" properties.
     *
     * @param id the id of the properties to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the properties, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/properties/{id}")
    public ResponseEntity<Properties> getProperties(@PathVariable Long id) {
        log.debug("REST request to get Properties : {}", id);
        Optional<Properties> properties = propertiesService.findOne(id);
        return ResponseUtil.wrapOrNotFound(properties);
    }

    /**
     * {@code DELETE  /properties/:id} : delete the "id" properties.
     *
     * @param id the id of the properties to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/properties/{id}")
    public ResponseEntity<Void> deleteProperties(@PathVariable Long id) {
        log.debug("REST request to delete Properties : {}", id);
        propertiesService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/properties?query=:query} : search for the properties corresponding
     * to the query.
     *
     * @param query the query of the properties search.
     * @return the result of the search.
     */
    @GetMapping("/_search/properties")
    public List<Properties> searchProperties(@RequestParam String query) {
        log.debug("REST request to search Properties for query {}", query);
        return propertiesService.search(query);
    }
}
