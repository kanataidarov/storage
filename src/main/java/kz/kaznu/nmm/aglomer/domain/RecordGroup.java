package kz.kaznu.nmm.aglomer.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import kz.kaznu.nmm.aglomer.domain.enumeration.RecordType;

/**
 * A RecordGroup.
 */
@Entity
@Table(name = "record_group")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "recordgroup")
public class RecordGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Size(max = 99)
    @Column(name = "name", length = 99, nullable = false, unique = true)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private RecordType type;

    @OneToMany(mappedBy = "group")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<RecordTemplate> recordGroupRecordTemplates = new HashSet<>();

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

    public RecordGroup name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RecordType getType() {
        return type;
    }

    public RecordGroup type(RecordType type) {
        this.type = type;
        return this;
    }

    public void setType(RecordType type) {
        this.type = type;
    }

    public Set<RecordTemplate> getRecordGroupRecordTemplates() {
        return recordGroupRecordTemplates;
    }

    public RecordGroup recordGroupRecordTemplates(Set<RecordTemplate> recordTemplates) {
        this.recordGroupRecordTemplates = recordTemplates;
        return this;
    }

    public RecordGroup addRecordGroupRecordTemplate(RecordTemplate recordTemplate) {
        this.recordGroupRecordTemplates.add(recordTemplate);
        recordTemplate.setGroup(this);
        return this;
    }

    public RecordGroup removeRecordGroupRecordTemplate(RecordTemplate recordTemplate) {
        this.recordGroupRecordTemplates.remove(recordTemplate);
        recordTemplate.setGroup(null);
        return this;
    }

    public void setRecordGroupRecordTemplates(Set<RecordTemplate> recordTemplates) {
        this.recordGroupRecordTemplates = recordTemplates;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RecordGroup)) {
            return false;
        }
        return id != null && id.equals(((RecordGroup) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "RecordGroup{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", type='" + getType() + "'" +
            "}";
    }
}
