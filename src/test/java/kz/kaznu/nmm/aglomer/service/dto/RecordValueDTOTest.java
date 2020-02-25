package kz.kaznu.nmm.aglomer.service.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import kz.kaznu.nmm.aglomer.web.rest.TestUtil;

public class RecordValueDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(RecordValueDTO.class);
        RecordValueDTO recordValueDTO1 = new RecordValueDTO();
        recordValueDTO1.setId(1L);
        RecordValueDTO recordValueDTO2 = new RecordValueDTO();
        assertThat(recordValueDTO1).isNotEqualTo(recordValueDTO2);
        recordValueDTO2.setId(recordValueDTO1.getId());
        assertThat(recordValueDTO1).isEqualTo(recordValueDTO2);
        recordValueDTO2.setId(2L);
        assertThat(recordValueDTO1).isNotEqualTo(recordValueDTO2);
        recordValueDTO1.setId(null);
        assertThat(recordValueDTO1).isNotEqualTo(recordValueDTO2);
    }
}
