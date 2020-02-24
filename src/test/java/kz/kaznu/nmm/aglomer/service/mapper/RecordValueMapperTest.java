package kz.kaznu.nmm.aglomer.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class RecordValueMapperTest {

    private RecordValueMapper recordValueMapper;

    @BeforeEach
    public void setUp() {
        recordValueMapper = new RecordValueMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 1L;
        assertThat(recordValueMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(recordValueMapper.fromId(null)).isNull();
    }
}
