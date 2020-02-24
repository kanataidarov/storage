package kz.kaznu.nmm.aglomer.service.dto;

import java.time.Instant;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link kz.kaznu.nmm.aglomer.domain.Property} entity.
 */
public class PropertyDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 99)
    private String name;

    @NotNull
    private Instant created;

    @NotNull
    private Instant updated;

    private Long group;


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

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getUpdated() {
        return updated;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    public Long getGroup() {
        return group;
    }

    public void setGroup(Long group) {
        this.group = group;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PropertyDTO propertyDTO = (PropertyDTO) o;
        if (propertyDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), propertyDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "PropertyDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", created='" + getCreated() + "'" +
            ", updated='" + getUpdated() + "'" +
            ", group=" + getGroup() +
            "}";
    }
}
