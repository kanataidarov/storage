package kz.kaznu.nmm.aglomer.service.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import kz.kaznu.nmm.aglomer.web.rest.TestUtil;

public class RecordDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(RecordDTO.class);
        RecordDTO recordDTO1 = new RecordDTO();
        recordDTO1.setId(1L);
        RecordDTO recordDTO2 = new RecordDTO();
        assertThat(recordDTO1).isNotEqualTo(recordDTO2);
        recordDTO2.setId(recordDTO1.getId());
        assertThat(recordDTO1).isEqualTo(recordDTO2);
        recordDTO2.setId(2L);
        assertThat(recordDTO1).isNotEqualTo(recordDTO2);
        recordDTO1.setId(null);
        assertThat(recordDTO1).isNotEqualTo(recordDTO2);
    }
}
