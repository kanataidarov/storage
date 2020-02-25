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

/**
 * Criteria class for the {@link kz.kaznu.nmm.aglomer.domain.PropertyGroup} entity. This class is used
 * in {@link kz.kaznu.nmm.aglomer.web.rest.PropertyGroupResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /property-groups?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class PropertyGroupCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private LongFilter propertyGroupPropertyId;

    public PropertyGroupCriteria() {
    }

    public PropertyGroupCriteria(PropertyGroupCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.propertyGroupPropertyId = other.propertyGroupPropertyId == null ? null : other.propertyGroupPropertyId.copy();
    }

    @Override
    public PropertyGroupCriteria copy() {
        return new PropertyGroupCriteria(this);
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

    public LongFilter getPropertyGroupPropertyId() {
        return propertyGroupPropertyId;
    }

    public void setPropertyGroupPropertyId(LongFilter propertyGroupPropertyId) {
        this.propertyGroupPropertyId = propertyGroupPropertyId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final PropertyGroupCriteria that = (PropertyGroupCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(propertyGroupPropertyId, that.propertyGroupPropertyId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        name,
        propertyGroupPropertyId
        );
    }

    @Override
    public String toString() {
        return "PropertyGroupCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (name != null ? "name=" + name + ", " : "") +
                (propertyGroupPropertyId != null ? "propertyGroupPropertyId=" + propertyGroupPropertyId + ", " : "") +
            "}";
    }

}
