package kz.kaznu.nmm.aglomer.service.dto;

import java.time.Instant;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link kz.kaznu.nmm.aglomer.domain.RecordValue} entity.
 */
public class RecordValueDTO implements Serializable {

    private Long id;

    @Size(max = 299)
    private String value;

    @NotNull
    private Instant created;

    @NotNull
    private Instant updated;


    private Long recordId;

    private Long fieldId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public Long getFieldId() {
        return fieldId;
    }

    public void setFieldId(Long recordFieldId) {
        this.fieldId = recordFieldId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RecordValueDTO recordValueDTO = (RecordValueDTO) o;
        if (recordValueDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), recordValueDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "RecordValueDTO{" +
            "id=" + getId() +
            ", value='" + getValue() + "'" +
            ", created='" + getCreated() + "'" +
            ", updated='" + getUpdated() + "'" +
            ", recordId=" + getRecordId() +
            ", fieldId=" + getFieldId() +
            "}";
    }
}
