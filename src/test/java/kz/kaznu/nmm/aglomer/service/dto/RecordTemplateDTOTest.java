package kz.kaznu.nmm.aglomer.service.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import kz.kaznu.nmm.aglomer.web.rest.TestUtil;

public class RecordTemplateDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(RecordTemplateDTO.class);
        RecordTemplateDTO recordTemplateDTO1 = new RecordTemplateDTO();
        recordTemplateDTO1.setId(1L);
        RecordTemplateDTO recordTemplateDTO2 = new RecordTemplateDTO();
        assertThat(recordTemplateDTO1).isNotEqualTo(recordTemplateDTO2);
        recordTemplateDTO2.setId(recordTemplateDTO1.getId());
        assertThat(recordTemplateDTO1).isEqualTo(recordTemplateDTO2);
        recordTemplateDTO2.setId(2L);
        assertThat(recordTemplateDTO1).isNotEqualTo(recordTemplateDTO2);
        recordTemplateDTO1.setId(null);
        assertThat(recordTemplateDTO1).isNotEqualTo(recordTemplateDTO2);
    }
}
