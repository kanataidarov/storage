package kz.kaznu.nmm.aglomer.repository;

import kz.kaznu.nmm.aglomer.domain.PropertyGroup;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the PropertyGroup entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PropertyGroupRepository extends JpaRepository<PropertyGroup, Long>, JpaSpecificationExecutor<PropertyGroup> {

}
