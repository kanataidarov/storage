package kz.kaznu.nmm.aglomer.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.Criteria;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;
import io.github.jhipster.service.filter.BigDecimalFilter;
import io.github.jhipster.service.filter.InstantFilter;

/**
 * Criteria class for the {@link kz.kaznu.nmm.aglomer.domain.Record} entity. This class is used
 * in {@link kz.kaznu.nmm.aglomer.web.rest.RecordResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /records?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class RecordCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private BigDecimalFilter code;

    private InstantFilter created;

    private InstantFilter updated;

    private LongFilter recordRecordValueId;

    public RecordCriteria() {
    }

    public RecordCriteria(RecordCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.code = other.code == null ? null : other.code.copy();
        this.created = other.created == null ? null : other.created.copy();
        this.updated = other.updated == null ? null : other.updated.copy();
        this.recordRecordValueId = other.recordRecordValueId == null ? null : other.recordRecordValueId.copy();
    }

    @Override
    public RecordCriteria copy() {
        return new RecordCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public BigDecimalFilter getCode() {
        return code;
    }

    public void setCode(BigDecimalFilter code) {
        this.code = code;
    }

    public InstantFilter getCreated() {
        return created;
    }

    public void setCreated(InstantFilter created) {
        this.created = created;
    }

    public InstantFilter getUpdated() {
        return updated;
    }

    public void setUpdated(InstantFilter updated) {
        this.updated = updated;
    }

    public LongFilter getRecordRecordValueId() {
        return recordRecordValueId;
    }

    public void setRecordRecordValueId(LongFilter recordRecordValueId) {
        this.recordRecordValueId = recordRecordValueId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final RecordCriteria that = (RecordCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(code, that.code) &&
            Objects.equals(created, that.created) &&
            Objects.equals(updated, that.updated) &&
            Objects.equals(recordRecordValueId, that.recordRecordValueId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        code,
        created,
        updated,
        recordRecordValueId
        );
    }

    @Override
    public String toString() {
        return "RecordCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (code != null ? "code=" + code + ", " : "") +
                (created != null ? "created=" + created + ", " : "") +
                (updated != null ? "updated=" + updated + ", " : "") +
                (recordRecordValueId != null ? "recordRecordValueId=" + recordRecordValueId + ", " : "") +
            "}";
    }

}
