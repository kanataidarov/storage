package kz.kaznu.nmm.aglomer.repository;

import kz.kaznu.nmm.aglomer.domain.RecordValue;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the RecordValue entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RecordValueRepository extends JpaRepository<RecordValue, Long> {

}
