package kz.kaznu.nmm.aglomer.repository;

import kz.kaznu.nmm.aglomer.domain.RecordGroup;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the RecordGroup entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RecordGroupRepository extends JpaRepository<RecordGroup, Long>, JpaSpecificationExecutor<RecordGroup> {

}
