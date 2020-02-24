package kz.kaznu.nmm.aglomer.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import kz.kaznu.nmm.aglomer.web.rest.TestUtil;

public class PropertyGroupTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PropertyGroup.class);
        PropertyGroup propertyGroup1 = new PropertyGroup();
        propertyGroup1.setId(1L);
        PropertyGroup propertyGroup2 = new PropertyGroup();
        propertyGroup2.setId(propertyGroup1.getId());
        assertThat(propertyGroup1).isEqualTo(propertyGroup2);
        propertyGroup2.setId(2L);
        assertThat(propertyGroup1).isNotEqualTo(propertyGroup2);
        propertyGroup1.setId(null);
        assertThat(propertyGroup1).isNotEqualTo(propertyGroup2);
    }
}
