package kz.kaznu.nmm.aglomer.repository.search;

import kz.kaznu.nmm.aglomer.domain.Properties;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Properties} entity.
 */
public interface PropertiesSearchRepository extends ElasticsearchRepository<Properties, Long> {
}
