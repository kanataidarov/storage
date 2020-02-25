package kz.kaznu.nmm.aglomer.service.mapper;


import kz.kaznu.nmm.aglomer.domain.*;
import kz.kaznu.nmm.aglomer.service.dto.PropertyGroupDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link PropertyGroup} and its DTO {@link PropertyGroupDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface PropertyGroupMapper extends EntityMapper<PropertyGroupDTO, PropertyGroup> {


    @Mapping(target = "propertyGroupProperties", ignore = true)
    @Mapping(target = "removePropertyGroupProperty", ignore = true)
    PropertyGroup toEntity(PropertyGroupDTO propertyGroupDTO);

    default PropertyGroup fromId(Long id) {
        if (id == null) {
            return null;
        }
        PropertyGroup propertyGroup = new PropertyGroup();
        propertyGroup.setId(id);
        return propertyGroup;
    }
}
