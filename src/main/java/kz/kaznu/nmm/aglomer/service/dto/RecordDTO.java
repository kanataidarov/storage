package kz.kaznu.nmm.aglomer.service.dto;

import java.time.Instant;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link kz.kaznu.nmm.aglomer.domain.Record} entity.
 */
public class RecordDTO implements Serializable {

    private Long id;

    @NotNull
    private BigDecimal code;

    @NotNull
    private Instant created;

    @NotNull
    private Instant updated;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getCode() {
        return code;
    }

    public void setCode(BigDecimal code) {
        this.code = code;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RecordDTO recordDTO = (RecordDTO) o;
        if (recordDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), recordDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "RecordDTO{" +
            "id=" + getId() +
            ", code=" + getCode() +
            ", created='" + getCreated() + "'" +
            ", updated='" + getUpdated() + "'" +
            "}";
    }
}
