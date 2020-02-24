package kz.kaznu.nmm.aglomer.repository.search;

import kz.kaznu.nmm.aglomer.domain.PropertyGroup;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link PropertyGroup} entity.
 */
public interface PropertyGroupSearchRepository extends ElasticsearchRepository<PropertyGroup, Long> {
}
