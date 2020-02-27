package kz.kaznu.nmm.aglomer.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A PropertyGroup.
 */
@Entity
@Table(name = "property_group")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "propertygroup")
public class PropertyGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Size(max = 99)
    @Column(name = "name", length = 99, nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "group")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Property> propertyGroupProperties = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public PropertyGroup name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Property> getPropertyGroupProperties() {
        return propertyGroupProperties;
    }

    public PropertyGroup propertyGroupProperties(Set<Property> properties) {
        this.propertyGroupProperties = properties;
        return this;
    }

    public PropertyGroup addPropertyGroupProperty(Property property) {
        this.propertyGroupProperties.add(property);
        property.setGroup(this);
        return this;
    }

    public PropertyGroup removePropertyGroupProperty(Property property) {
        this.propertyGroupProperties.remove(property);
        property.setGroup(null);
        return this;
    }

    public void setPropertyGroupProperties(Set<Property> properties) {
        this.propertyGroupProperties = properties;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PropertyGroup)) {
            return false;
        }
        return id != null && id.equals(((PropertyGroup) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "PropertyGroup{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
