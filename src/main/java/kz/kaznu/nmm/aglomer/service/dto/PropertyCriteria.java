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
import io.github.jhipster.service.filter.InstantFilter;

/**
 * Criteria class for the {@link kz.kaznu.nmm.aglomer.domain.Property} entity. This class is used
 * in {@link kz.kaznu.nmm.aglomer.web.rest.PropertyResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /properties?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class PropertyCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private InstantFilter created;

    private InstantFilter updated;

    private LongFilter groupId;

    public PropertyCriteria() {
    }

    public PropertyCriteria(PropertyCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.created = other.created == null ? null : other.created.copy();
        this.updated = other.updated == null ? null : other.updated.copy();
        this.groupId = other.groupId == null ? null : other.groupId.copy();
    }

    @Override
    public PropertyCriteria copy() {
        return new PropertyCriteria(this);
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

    public LongFilter getGroupId() {
        return groupId;
    }

    public void setGroupId(LongFilter groupId) {
        this.groupId = groupId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final PropertyCriteria that = (PropertyCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(created, that.created) &&
            Objects.equals(updated, that.updated) &&
            Objects.equals(groupId, that.groupId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        name,
        created,
        updated,
        groupId
        );
    }

    @Override
    public String toString() {
        return "PropertyCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (name != null ? "name=" + name + ", " : "") +
                (created != null ? "created=" + created + ", " : "") +
                (updated != null ? "updated=" + updated + ", " : "") +
                (groupId != null ? "groupId=" + groupId + ", " : "") +
            "}";
    }

}
