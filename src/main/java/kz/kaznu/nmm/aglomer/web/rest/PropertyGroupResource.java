package kz.kaznu.nmm.aglomer.web.rest;

import kz.kaznu.nmm.aglomer.service.PropertyGroupService;
import kz.kaznu.nmm.aglomer.web.rest.errors.BadRequestAlertException;
import kz.kaznu.nmm.aglomer.service.dto.PropertyGroupDTO;

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
 * REST controller for managing {@link kz.kaznu.nmm.aglomer.domain.PropertyGroup}.
 */
@RestController
@RequestMapping("/api")
public class PropertyGroupResource {

    private final Logger log = LoggerFactory.getLogger(PropertyGroupResource.class);

    private static final String ENTITY_NAME = "storagePropertyGroup";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PropertyGroupService propertyGroupService;

    public PropertyGroupResource(PropertyGroupService propertyGroupService) {
        this.propertyGroupService = propertyGroupService;
    }

    /**
     * {@code POST  /property-groups} : Create a new propertyGroup.
     *
     * @param propertyGroupDTO the propertyGroupDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new propertyGroupDTO, or with status {@code 400 (Bad Request)} if the propertyGroup has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/property-groups")
    public ResponseEntity<PropertyGroupDTO> createPropertyGroup(@Valid @RequestBody PropertyGroupDTO propertyGroupDTO) throws URISyntaxException {
        log.debug("REST request to save PropertyGroup : {}", propertyGroupDTO);
        if (propertyGroupDTO.getId() != null) {
            throw new BadRequestAlertException("A new propertyGroup cannot already have an ID", ENTITY_NAME, "idexists");
        }
        PropertyGroupDTO result = propertyGroupService.save(propertyGroupDTO);
        return ResponseEntity.created(new URI("/api/property-groups/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /property-groups} : Updates an existing propertyGroup.
     *
     * @param propertyGroupDTO the propertyGroupDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated propertyGroupDTO,
     * or with status {@code 400 (Bad Request)} if the propertyGroupDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the propertyGroupDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/property-groups")
    public ResponseEntity<PropertyGroupDTO> updatePropertyGroup(@Valid @RequestBody PropertyGroupDTO propertyGroupDTO) throws URISyntaxException {
        log.debug("REST request to update PropertyGroup : {}", propertyGroupDTO);
        if (propertyGroupDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        PropertyGroupDTO result = propertyGroupService.save(propertyGroupDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, propertyGroupDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /property-groups} : get all the propertyGroups.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of propertyGroups in body.
     */
    @GetMapping("/property-groups")
    public List<PropertyGroupDTO> getAllPropertyGroups() {
        log.debug("REST request to get all PropertyGroups");
        return propertyGroupService.findAll();
    }

    /**
     * {@code GET  /property-groups/:id} : get the "id" propertyGroup.
     *
     * @param id the id of the propertyGroupDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the propertyGroupDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/property-groups/{id}")
    public ResponseEntity<PropertyGroupDTO> getPropertyGroup(@PathVariable Long id) {
        log.debug("REST request to get PropertyGroup : {}", id);
        Optional<PropertyGroupDTO> propertyGroupDTO = propertyGroupService.findOne(id);
        return ResponseUtil.wrapOrNotFound(propertyGroupDTO);
    }

    /**
     * {@code DELETE  /property-groups/:id} : delete the "id" propertyGroup.
     *
     * @param id the id of the propertyGroupDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/property-groups/{id}")
    public ResponseEntity<Void> deletePropertyGroup(@PathVariable Long id) {
        log.debug("REST request to delete PropertyGroup : {}", id);
        propertyGroupService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/property-groups?query=:query} : search for the propertyGroup corresponding
     * to the query.
     *
     * @param query the query of the propertyGroup search.
     * @return the result of the search.
     */
    @GetMapping("/_search/property-groups")
    public List<PropertyGroupDTO> searchPropertyGroups(@RequestParam String query) {
        log.debug("REST request to search PropertyGroups for query {}", query);
        return propertyGroupService.search(query);
    }
}
