package kz.kaznu.nmm.aglomer.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;
import java.time.Instant;

/**
 * A RecordValue.
 */
@Entity
@Table(name = "record_value")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "recordvalue")
public class RecordValue implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Size(max = 299)
    @Column(name = "value", length = 299)
    private String value;

    @NotNull
    @Column(name = "created", nullable = false)
    private Instant created;

    @NotNull
    @Column(name = "updated", nullable = false)
    private Instant updated;

    @ManyToOne
    @JsonIgnoreProperties("recordRecordValues")
    private Record record;

    @ManyToOne
    @JsonIgnoreProperties("recordFieldRecordValues")
    private RecordField field;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public RecordValue value(String value) {
        this.value = value;
        return this;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Instant getCreated() {
        return created;
    }

    public RecordValue created(Instant created) {
        this.created = created;
        return this;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getUpdated() {
        return updated;
    }

    public RecordValue updated(Instant updated) {
        this.updated = updated;
        return this;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    public Record getRecord() {
        return record;
    }

    public RecordValue record(Record record) {
        this.record = record;
        return this;
    }

    public void setRecord(Record record) {
        this.record = record;
    }

    public RecordField getField() {
        return field;
    }

    public RecordValue field(RecordField recordField) {
        this.field = recordField;
        return this;
    }

    public void setField(RecordField recordField) {
        this.field = recordField;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RecordValue)) {
            return false;
        }
        return id != null && id.equals(((RecordValue) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "RecordValue{" +
            "id=" + getId() +
            ", value='" + getValue() + "'" +
            ", created='" + getCreated() + "'" +
            ", updated='" + getUpdated() + "'" +
            "}";
    }
}
