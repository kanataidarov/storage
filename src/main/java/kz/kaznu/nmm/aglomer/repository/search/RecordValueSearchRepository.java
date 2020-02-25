package kz.kaznu.nmm.aglomer.repository.search;

import kz.kaznu.nmm.aglomer.domain.RecordValue;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link RecordValue} entity.
 */
public interface RecordValueSearchRepository extends ElasticsearchRepository<RecordValue, Long> {
}
