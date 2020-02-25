package kz.kaznu.nmm.aglomer.service.mapper;


import kz.kaznu.nmm.aglomer.domain.*;
import kz.kaznu.nmm.aglomer.service.dto.RecordFieldDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link RecordField} and its DTO {@link RecordFieldDTO}.
 */
@Mapper(componentModel = "spring", uses = {RecordTemplateMapper.class})
public interface RecordFieldMapper extends EntityMapper<RecordFieldDTO, RecordField> {

    @Mapping(source = "template.id", target = "templateId")
    RecordFieldDTO toDto(RecordField recordField);

    @Mapping(target = "recordFieldRecordValues", ignore = true)
    @Mapping(target = "removeRecordFieldRecordValue", ignore = true)
    @Mapping(source = "templateId", target = "template")
    RecordField toEntity(RecordFieldDTO recordFieldDTO);

    default RecordField fromId(Long id) {
        if (id == null) {
            return null;
        }
        RecordField recordField = new RecordField();
        recordField.setId(id);
        return recordField;
    }
}
