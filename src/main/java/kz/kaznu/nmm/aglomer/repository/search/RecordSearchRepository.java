package kz.kaznu.nmm.aglomer.repository.search;

import kz.kaznu.nmm.aglomer.domain.Record;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Record} entity.
 */
public interface RecordSearchRepository extends ElasticsearchRepository<Record, Long> {
}
