package kz.kaznu.nmm.aglomer.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class PropertyMapperTest {

    private PropertyMapper propertyMapper;

    @BeforeEach
    public void setUp() {
        propertyMapper = new PropertyMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 1L;
        assertThat(propertyMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(propertyMapper.fromId(null)).isNull();
    }
}
