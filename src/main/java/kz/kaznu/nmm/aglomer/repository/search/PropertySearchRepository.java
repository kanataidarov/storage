package kz.kaznu.nmm.aglomer.repository.search;

import kz.kaznu.nmm.aglomer.domain.Property;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Property} entity.
 */
public interface PropertySearchRepository extends ElasticsearchRepository<Property, Long> {
}
