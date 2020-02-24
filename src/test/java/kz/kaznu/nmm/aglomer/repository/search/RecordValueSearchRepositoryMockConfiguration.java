package kz.kaznu.nmm.aglomer.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link RecordValueSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class RecordValueSearchRepositoryMockConfiguration {

    @MockBean
    private RecordValueSearchRepository mockRecordValueSearchRepository;

}
