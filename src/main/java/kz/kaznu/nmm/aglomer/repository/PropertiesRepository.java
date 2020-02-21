package kz.kaznu.nmm.aglomer.repository;

import kz.kaznu.nmm.aglomer.domain.Properties;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the Properties entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PropertiesRepository extends JpaRepository<Properties, Long> {

}
