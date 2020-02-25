package kz.kaznu.nmm.aglomer.repository.search;

import kz.kaznu.nmm.aglomer.domain.RecordField;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link RecordField} entity.
 */
public interface RecordFieldSearchRepository extends ElasticsearchRepository<RecordField, Long> {
}
