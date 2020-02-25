package kz.kaznu.nmm.aglomer.service.mapper;


import kz.kaznu.nmm.aglomer.domain.*;
import kz.kaznu.nmm.aglomer.service.dto.RecordDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Record} and its DTO {@link RecordDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface RecordMapper extends EntityMapper<RecordDTO, Record> {


    @Mapping(target = "recordRecordValues", ignore = true)
    @Mapping(target = "removeRecordRecordValue", ignore = true)
    Record toEntity(RecordDTO recordDTO);

    default Record fromId(Long id) {
        if (id == null) {
            return null;
        }
        Record record = new Record();
        record.setId(id);
        return record;
    }
}
