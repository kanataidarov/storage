package kz.kaznu.nmm.aglomer.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class PropertyGroupMapperTest {

    private PropertyGroupMapper propertyGroupMapper;

    @BeforeEach
    public void setUp() {
        propertyGroupMapper = new PropertyGroupMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 1L;
        assertThat(propertyGroupMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(propertyGroupMapper.fromId(null)).isNull();
    }
}
