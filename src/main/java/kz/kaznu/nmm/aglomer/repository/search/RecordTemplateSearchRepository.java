package kz.kaznu.nmm.aglomer.repository.search;

import kz.kaznu.nmm.aglomer.domain.RecordTemplate;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link RecordTemplate} entity.
 */
public interface RecordTemplateSearchRepository extends ElasticsearchRepository<RecordTemplate, Long> {
}
