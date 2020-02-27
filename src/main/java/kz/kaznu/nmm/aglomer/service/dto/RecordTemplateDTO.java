package kz.kaznu.nmm.aglomer.service.dto;

import java.time.Instant;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;
import kz.kaznu.nmm.aglomer.domain.enumeration.Language;

/**
 * A DTO for the {@link kz.kaznu.nmm.aglomer.domain.RecordTemplate} entity.
 */
public class RecordTemplateDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 99)
    private String name;

    @NotNull
    private Instant created;

    @NotNull
    private Instant updated;

    @NotNull
    private Language language;


    private Long groupId;

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

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long recordGroupId) {
        this.groupId = recordGroupId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RecordTemplateDTO recordTemplateDTO = (RecordTemplateDTO) o;
        if (recordTemplateDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), recordTemplateDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "RecordTemplateDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", created='" + getCreated() + "'" +
            ", updated='" + getUpdated() + "'" +
            ", language='" + getLanguage() + "'" +
            ", groupId=" + getGroupId() +
            "}";
    }
}
