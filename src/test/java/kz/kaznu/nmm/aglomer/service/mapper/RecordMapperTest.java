package kz.kaznu.nmm.aglomer.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class RecordMapperTest {

    private RecordMapper recordMapper;

    @BeforeEach
    public void setUp() {
        recordMapper = new RecordMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 1L;
        assertThat(recordMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(recordMapper.fromId(null)).isNull();
    }
}
