package kz.kaznu.nmm.aglomer.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class RecordGroupMapperTest {

    private RecordGroupMapper recordGroupMapper;

    @BeforeEach
    public void setUp() {
        recordGroupMapper = new RecordGroupMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 1L;
        assertThat(recordGroupMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(recordGroupMapper.fromId(null)).isNull();
    }
}
