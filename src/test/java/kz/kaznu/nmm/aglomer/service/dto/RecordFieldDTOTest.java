package kz.kaznu.nmm.aglomer.service.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import kz.kaznu.nmm.aglomer.web.rest.TestUtil;

public class RecordFieldDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(RecordFieldDTO.class);
        RecordFieldDTO recordFieldDTO1 = new RecordFieldDTO();
        recordFieldDTO1.setId(1L);
        RecordFieldDTO recordFieldDTO2 = new RecordFieldDTO();
        assertThat(recordFieldDTO1).isNotEqualTo(recordFieldDTO2);
        recordFieldDTO2.setId(recordFieldDTO1.getId());
        assertThat(recordFieldDTO1).isEqualTo(recordFieldDTO2);
        recordFieldDTO2.setId(2L);
        assertThat(recordFieldDTO1).isNotEqualTo(recordFieldDTO2);
        recordFieldDTO1.setId(null);
        assertThat(recordFieldDTO1).isNotEqualTo(recordFieldDTO2);
    }
}
