package kz.kaznu.nmm.aglomer.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import kz.kaznu.nmm.aglomer.web.rest.TestUtil;

public class RecordGroupTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RecordGroup.class);
        RecordGroup recordGroup1 = new RecordGroup();
        recordGroup1.setId(1L);
        RecordGroup recordGroup2 = new RecordGroup();
        recordGroup2.setId(recordGroup1.getId());
        assertThat(recordGroup1).isEqualTo(recordGroup2);
        recordGroup2.setId(2L);
        assertThat(recordGroup1).isNotEqualTo(recordGroup2);
        recordGroup1.setId(null);
        assertThat(recordGroup1).isNotEqualTo(recordGroup2);
    }
}
