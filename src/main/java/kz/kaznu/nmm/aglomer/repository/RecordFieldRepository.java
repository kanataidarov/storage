package kz.kaznu.nmm.aglomer.repository;

import kz.kaznu.nmm.aglomer.domain.RecordField;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the RecordField entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RecordFieldRepository extends JpaRepository<RecordField, Long> {

}
