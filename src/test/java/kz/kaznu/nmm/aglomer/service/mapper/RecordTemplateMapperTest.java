package kz.kaznu.nmm.aglomer.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class RecordTemplateMapperTest {

    private RecordTemplateMapper recordTemplateMapper;

    @BeforeEach
    public void setUp() {
        recordTemplateMapper = new RecordTemplateMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 1L;
        assertThat(recordTemplateMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(recordTemplateMapper.fromId(null)).isNull();
    }
}
