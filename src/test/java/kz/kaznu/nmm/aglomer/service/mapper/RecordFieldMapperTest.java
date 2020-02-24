package kz.kaznu.nmm.aglomer.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class RecordFieldMapperTest {

    private RecordFieldMapper recordFieldMapper;

    @BeforeEach
    public void setUp() {
        recordFieldMapper = new RecordFieldMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 1L;
        assertThat(recordFieldMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(recordFieldMapper.fromId(null)).isNull();
    }
}
