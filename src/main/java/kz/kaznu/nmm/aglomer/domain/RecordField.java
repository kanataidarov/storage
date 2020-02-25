package kz.kaznu.nmm.aglomer.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A RecordField.
 */
@Entity
@Table(name = "record_field")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "recordfield")
public class RecordField implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Size(max = 99)
    @Column(name = "name", length = 99, nullable = false)
    private String name;

    @Size(max = 299)
    @Column(name = "description", length = 299)
    private String description;

    @OneToMany(mappedBy = "field")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<RecordValue> recordFieldRecordValues = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties("recordTemplateRecordFields")
    private RecordTemplate template;

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

    public RecordField name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public RecordField description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<RecordValue> getRecordFieldRecordValues() {
        return recordFieldRecordValues;
    }

    public RecordField recordFieldRecordValues(Set<RecordValue> recordValues) {
        this.recordFieldRecordValues = recordValues;
        return this;
    }

    public RecordField addRecordFieldRecordValue(RecordValue recordValue) {
        this.recordFieldRecordValues.add(recordValue);
        recordValue.setField(this);
        return this;
    }

    public RecordField removeRecordFieldRecordValue(RecordValue recordValue) {
        this.recordFieldRecordValues.remove(recordValue);
        recordValue.setField(null);
        return this;
    }

    public void setRecordFieldRecordValues(Set<RecordValue> recordValues) {
        this.recordFieldRecordValues = recordValues;
    }

    public RecordTemplate getTemplate() {
        return template;
    }

    public RecordField template(RecordTemplate recordTemplate) {
        this.template = recordTemplate;
        return this;
    }

    public void setTemplate(RecordTemplate recordTemplate) {
        this.template = recordTemplate;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RecordField)) {
            return false;
        }
        return id != null && id.equals(((RecordField) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "RecordField{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
