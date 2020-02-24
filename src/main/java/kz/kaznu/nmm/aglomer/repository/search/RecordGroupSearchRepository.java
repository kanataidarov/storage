package kz.kaznu.nmm.aglomer.repository.search;

import kz.kaznu.nmm.aglomer.domain.RecordGroup;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link RecordGroup} entity.
 */
public interface RecordGroupSearchRepository extends ElasticsearchRepository<RecordGroup, Long> {
}
