package kz.kaznu.nmm.aglomer.service.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import kz.kaznu.nmm.aglomer.web.rest.TestUtil;

public class PropertyGroupDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PropertyGroupDTO.class);
        PropertyGroupDTO propertyGroupDTO1 = new PropertyGroupDTO();
        propertyGroupDTO1.setId(1L);
        PropertyGroupDTO propertyGroupDTO2 = new PropertyGroupDTO();
        assertThat(propertyGroupDTO1).isNotEqualTo(propertyGroupDTO2);
        propertyGroupDTO2.setId(propertyGroupDTO1.getId());
        assertThat(propertyGroupDTO1).isEqualTo(propertyGroupDTO2);
        propertyGroupDTO2.setId(2L);
        assertThat(propertyGroupDTO1).isNotEqualTo(propertyGroupDTO2);
        propertyGroupDTO1.setId(null);
        assertThat(propertyGroupDTO1).isNotEqualTo(propertyGroupDTO2);
    }
}
