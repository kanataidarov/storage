package kz.kaznu.nmm.aglomer.repository;

import kz.kaznu.nmm.aglomer.domain.Property;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the Property entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {

}
