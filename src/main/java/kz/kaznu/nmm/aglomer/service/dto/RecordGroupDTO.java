package kz.kaznu.nmm.aglomer.service.dto;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;
import kz.kaznu.nmm.aglomer.domain.enumeration.RecordType;

/**
 * A DTO for the {@link kz.kaznu.nmm.aglomer.domain.RecordGroup} entity.
 */
public class RecordGroupDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 99)
    private String name;

    @NotNull
    private RecordType type;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RecordType getType() {
        return type;
    }

    public void setType(RecordType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RecordGroupDTO recordGroupDTO = (RecordGroupDTO) o;
        if (recordGroupDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), recordGroupDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "RecordGroupDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", type='" + getType() + "'" +
            "}";
    }
}
