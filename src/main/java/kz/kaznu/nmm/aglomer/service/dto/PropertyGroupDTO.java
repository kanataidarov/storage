package kz.kaznu.nmm.aglomer.service.dto;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link kz.kaznu.nmm.aglomer.domain.PropertyGroup} entity.
 */
public class PropertyGroupDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 99)
    private String name;


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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PropertyGroupDTO propertyGroupDTO = (PropertyGroupDTO) o;
        if (propertyGroupDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), propertyGroupDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "PropertyGroupDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
