package kz.kaznu.nmm.aglomer.service.dto;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link kz.kaznu.nmm.aglomer.domain.RecordField} entity.
 */
public class RecordFieldDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 99)
    private String name;

    @Size(max = 299)
    private String description;


    private Long templateId;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long recordTemplateId) {
        this.templateId = recordTemplateId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RecordFieldDTO recordFieldDTO = (RecordFieldDTO) o;
        if (recordFieldDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), recordFieldDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "RecordFieldDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", templateId=" + getTemplateId() +
            "}";
    }
}
