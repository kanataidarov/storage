package kz.kaznu.nmm.aglomer.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.Criteria;
import kz.kaznu.nmm.aglomer.domain.enumeration.RecordType;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link kz.kaznu.nmm.aglomer.domain.RecordGroup} entity. This class is used
 * in {@link kz.kaznu.nmm.aglomer.web.rest.RecordGroupResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /record-groups?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class RecordGroupCriteria implements Serializable, Criteria {
    /**
     * Class for filtering RecordType
     */
    public static class RecordTypeFilter extends Filter<RecordType> {

        public RecordTypeFilter() {
        }

        public RecordTypeFilter(RecordTypeFilter filter) {
            super(filter);
        }

        @Override
        public RecordTypeFilter copy() {
            return new RecordTypeFilter(this);
        }

    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private RecordTypeFilter type;

    private LongFilter recordGroupRecordTemplateId;

    public RecordGroupCriteria() {
    }

    public RecordGroupCriteria(RecordGroupCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.type = other.type == null ? null : other.type.copy();
        this.recordGroupRecordTemplateId = other.recordGroupRecordTemplateId == null ? null : other.recordGroupRecordTemplateId.copy();
    }

    @Override
    public RecordGroupCriteria copy() {
        return new RecordGroupCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public RecordTypeFilter getType() {
        return type;
    }

    public void setType(RecordTypeFilter type) {
        this.type = type;
    }

    public LongFilter getRecordGroupRecordTemplateId() {
        return recordGroupRecordTemplateId;
    }

    public void setRecordGroupRecordTemplateId(LongFilter recordGroupRecordTemplateId) {
        this.recordGroupRecordTemplateId = recordGroupRecordTemplateId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final RecordGroupCriteria that = (RecordGroupCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(type, that.type) &&
            Objects.equals(recordGroupRecordTemplateId, that.recordGroupRecordTemplateId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        name,
        type,
        recordGroupRecordTemplateId
        );
    }

    @Override
    public String toString() {
        return "RecordGroupCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (name != null ? "name=" + name + ", " : "") +
                (type != null ? "type=" + type + ", " : "") +
                (recordGroupRecordTemplateId != null ? "recordGroupRecordTemplateId=" + recordGroupRecordTemplateId + ", " : "") +
            "}";
    }

}
