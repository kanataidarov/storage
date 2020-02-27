package kz.kaznu.nmm.aglomer.repository;

import kz.kaznu.nmm.aglomer.domain.RecordTemplate;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the RecordTemplate entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RecordTemplateRepository extends JpaRepository<RecordTemplate, Long>, JpaSpecificationExecutor<RecordTemplate> {

}
