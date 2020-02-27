package kz.kaznu.nmm.aglomer.service.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import kz.kaznu.nmm.aglomer.web.rest.TestUtil;

public class RecordGroupDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(RecordGroupDTO.class);
        RecordGroupDTO recordGroupDTO1 = new RecordGroupDTO();
        recordGroupDTO1.setId(1L);
        RecordGroupDTO recordGroupDTO2 = new RecordGroupDTO();
        assertThat(recordGroupDTO1).isNotEqualTo(recordGroupDTO2);
        recordGroupDTO2.setId(recordGroupDTO1.getId());
        assertThat(recordGroupDTO1).isEqualTo(recordGroupDTO2);
        recordGroupDTO2.setId(2L);
        assertThat(recordGroupDTO1).isNotEqualTo(recordGroupDTO2);
        recordGroupDTO1.setId(null);
        assertThat(recordGroupDTO1).isNotEqualTo(recordGroupDTO2);
    }
}
