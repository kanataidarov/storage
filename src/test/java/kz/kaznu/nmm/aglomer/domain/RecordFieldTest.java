package kz.kaznu.nmm.aglomer.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import kz.kaznu.nmm.aglomer.web.rest.TestUtil;

public class RecordFieldTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RecordField.class);
        RecordField recordField1 = new RecordField();
        recordField1.setId(1L);
        RecordField recordField2 = new RecordField();
        recordField2.setId(recordField1.getId());
        assertThat(recordField1).isEqualTo(recordField2);
        recordField2.setId(2L);
        assertThat(recordField1).isNotEqualTo(recordField2);
        recordField1.setId(null);
        assertThat(recordField1).isNotEqualTo(recordField2);
    }
}
