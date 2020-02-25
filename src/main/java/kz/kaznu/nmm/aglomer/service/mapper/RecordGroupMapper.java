package kz.kaznu.nmm.aglomer.service.mapper;


import kz.kaznu.nmm.aglomer.domain.*;
import kz.kaznu.nmm.aglomer.service.dto.RecordGroupDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link RecordGroup} and its DTO {@link RecordGroupDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface RecordGroupMapper extends EntityMapper<RecordGroupDTO, RecordGroup> {


    @Mapping(target = "recordGroupRecordTemplates", ignore = true)
    @Mapping(target = "removeRecordGroupRecordTemplate", ignore = true)
    RecordGroup toEntity(RecordGroupDTO recordGroupDTO);

    default RecordGroup fromId(Long id) {
        if (id == null) {
            return null;
        }
        RecordGroup recordGroup = new RecordGroup();
        recordGroup.setId(id);
        return recordGroup;
    }
}
