package kz.kaznu.nmm.aglomer.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import kz.kaznu.nmm.aglomer.web.rest.TestUtil;

public class RecordValueTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RecordValue.class);
        RecordValue recordValue1 = new RecordValue();
        recordValue1.setId(1L);
        RecordValue recordValue2 = new RecordValue();
        recordValue2.setId(recordValue1.getId());
        assertThat(recordValue1).isEqualTo(recordValue2);
        recordValue2.setId(2L);
        assertThat(recordValue1).isNotEqualTo(recordValue2);
        recordValue1.setId(null);
        assertThat(recordValue1).isNotEqualTo(recordValue2);
    }
}
