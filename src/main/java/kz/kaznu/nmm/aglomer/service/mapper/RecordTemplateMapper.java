package kz.kaznu.nmm.aglomer.service.mapper;


import kz.kaznu.nmm.aglomer.domain.*;
import kz.kaznu.nmm.aglomer.service.dto.RecordTemplateDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link RecordTemplate} and its DTO {@link RecordTemplateDTO}.
 */
@Mapper(componentModel = "spring", uses = {RecordGroupMapper.class})
public interface RecordTemplateMapper extends EntityMapper<RecordTemplateDTO, RecordTemplate> {

    @Mapping(source = "group.id", target = "groupId")
    RecordTemplateDTO toDto(RecordTemplate recordTemplate);

    @Mapping(target = "recordTemplateRecordFields", ignore = true)
    @Mapping(target = "removeRecordTemplateRecordField", ignore = true)
    @Mapping(source = "groupId", target = "group")
    RecordTemplate toEntity(RecordTemplateDTO recordTemplateDTO);

    default RecordTemplate fromId(Long id) {
        if (id == null) {
            return null;
        }
        RecordTemplate recordTemplate = new RecordTemplate();
        recordTemplate.setId(id);
        return recordTemplate;
    }
}
