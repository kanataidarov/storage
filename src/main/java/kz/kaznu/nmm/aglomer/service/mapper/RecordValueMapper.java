package kz.kaznu.nmm.aglomer.service.mapper;


import kz.kaznu.nmm.aglomer.domain.*;
import kz.kaznu.nmm.aglomer.service.dto.RecordValueDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link RecordValue} and its DTO {@link RecordValueDTO}.
 */
@Mapper(componentModel = "spring", uses = {RecordMapper.class, RecordFieldMapper.class})
public interface RecordValueMapper extends EntityMapper<RecordValueDTO, RecordValue> {

    @Mapping(source = "record.id", target = "recordId")
    @Mapping(source = "field.id", target = "fieldId")
    RecordValueDTO toDto(RecordValue recordValue);

    @Mapping(source = "recordId", target = "record")
    @Mapping(source = "fieldId", target = "field")
    RecordValue toEntity(RecordValueDTO recordValueDTO);

    default RecordValue fromId(Long id) {
        if (id == null) {
            return null;
        }
        RecordValue recordValue = new RecordValue();
        recordValue.setId(id);
        return recordValue;
    }
}
