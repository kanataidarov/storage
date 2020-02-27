package kz.kaznu.nmm.aglomer.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import kz.kaznu.nmm.aglomer.web.rest.TestUtil;

public class RecordTemplateTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RecordTemplate.class);
        RecordTemplate recordTemplate1 = new RecordTemplate();
        recordTemplate1.setId(1L);
        RecordTemplate recordTemplate2 = new RecordTemplate();
        recordTemplate2.setId(recordTemplate1.getId());
        assertThat(recordTemplate1).isEqualTo(recordTemplate2);
        recordTemplate2.setId(2L);
        assertThat(recordTemplate1).isNotEqualTo(recordTemplate2);
        recordTemplate1.setId(null);
        assertThat(recordTemplate1).isNotEqualTo(recordTemplate2);
    }
}
