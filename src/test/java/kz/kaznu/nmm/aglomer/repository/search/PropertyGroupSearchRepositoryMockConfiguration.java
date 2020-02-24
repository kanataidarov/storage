package kz.kaznu.nmm.aglomer.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link PropertyGroupSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class PropertyGroupSearchRepositoryMockConfiguration {

    @MockBean
    private PropertyGroupSearchRepository mockPropertyGroupSearchRepository;

}
