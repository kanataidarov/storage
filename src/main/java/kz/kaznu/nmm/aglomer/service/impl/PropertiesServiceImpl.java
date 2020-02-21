package kz.kaznu.nmm.aglomer.service.impl;

import kz.kaznu.nmm.aglomer.service.PropertiesService;
import kz.kaznu.nmm.aglomer.domain.Properties;
import kz.kaznu.nmm.aglomer.repository.PropertiesRepository;
import kz.kaznu.nmm.aglomer.repository.search.PropertiesSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link Properties}.
 */
@Service
@Transactional
public class PropertiesServiceImpl implements PropertiesService {

    private final Logger log = LoggerFactory.getLogger(PropertiesServiceImpl.class);

    private final PropertiesRepository propertiesRepository;

    private final PropertiesSearchRepository propertiesSearchRepository;

    public PropertiesServiceImpl(PropertiesRepository propertiesRepository, PropertiesSearchRepository propertiesSearchRepository) {
        this.propertiesRepository = propertiesRepository;
        this.propertiesSearchRepository = propertiesSearchRepository;
    }

    /**
     * Save a properties.
     *
     * @param properties the entity to save.
     * @return the persisted entity.
     */
    @Override
    public Properties save(Properties properties) {
        log.debug("Request to save Properties : {}", properties);
        Properties result = propertiesRepository.save(properties);
        propertiesSearchRepository.save(result);
        return result;
    }

    /**
     * Get all the properties.
     *
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Properties> findAll() {
        log.debug("Request to get all Properties");
        return propertiesRepository.findAll();
    }

    /**
     * Get one properties by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Properties> findOne(Long id) {
        log.debug("Request to get Properties : {}", id);
        return propertiesRepository.findById(id);
    }

    /**
     * Delete the properties by id.
     *
     * @param id the id of the entity.
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Properties : {}", id);
        propertiesRepository.deleteById(id);
        propertiesSearchRepository.deleteById(id);
    }

    /**
     * Search for the properties corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Properties> search(String query) {
        log.debug("Request to search Properties for query {}", query);
        return StreamSupport
            .stream(propertiesSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
